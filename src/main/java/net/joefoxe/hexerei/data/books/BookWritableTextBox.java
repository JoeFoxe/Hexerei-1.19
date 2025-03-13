package net.joefoxe.hexerei.data.books;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.joefoxe.hexerei.item.data_components.BookData;
import net.joefoxe.hexerei.tileentity.BookOfShadowsAltarTile;
import net.joefoxe.hexerei.util.ClientProxy;
import net.joefoxe.hexerei.util.HexereiPacketHandler;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.joefoxe.hexerei.util.message.UpdateBookDataToServer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.StringSplitter;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.fml.loading.FMLEnvironment;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.mutable.MutableInt;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class BookWritableTextBox {
    public BookParagraphElements paragraphElement;
    public int index;
    public String align;

    public ResourceLocation parentLocation;

    public Client client;


    BookWritableTextBox(ResourceLocation parentLocation, BookParagraphElements paragraphElement, int index, String align){
        this.paragraphElement = paragraphElement;
        this.parentLocation = parentLocation;
        this.index = index;
        this.align = align;
        this.client = FMLEnvironment.dist.isClient() ? new Client(this) : null;
    }

    public static final Codec<BookWritableTextBox> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    ResourceLocation.CODEC.optionalFieldOf("parentLocation", HexereiUtil.getResource("missing")).forGetter(p -> p.parentLocation),
                    BookParagraphElements.CODEC.fieldOf("paragraph_box").forGetter(p -> p.paragraphElement),
                    Codec.INT.optionalFieldOf("index", 0).forGetter(p -> p.index),
                    Codec.STRING.optionalFieldOf("align", "left").forGetter(p -> p.align)
                ).apply(instance, BookWritableTextBox::new));

    public static class Client {
        private BookWritableTextBox parent;
        public Client(BookWritableTextBox parent) {

            this.parent = parent;
        }

        public static class TextChange {
            public enum ChangeType {
                INSERT, DELETE
            }

            private final ChangeType type;
            private final String text;
            private final int cursorPos;
            private final int selectionPos;

            public TextChange(ChangeType type, String text, int cursorPos, int selectionPos) {
                this.type = type;
                this.text = text;
                this.cursorPos = cursorPos;
                this.selectionPos = selectionPos;
            }

            public ChangeType getType() {
                return type;
            }

            public String getText() {
                return text;
            }

            public int getCursorPos() {
                return cursorPos;
            }

            public int getSelectionPos() {
                return selectionPos;
            }
        }


        public static class UndoManager {
            private final Stack<TextChange> undoStack = new Stack<>();
            private final Stack<TextChange> redoStack = new Stack<>();

            public void recordChange(TextChange change) {
                undoStack.push(change);
                redoStack.clear(); // Clear redo stack on new change
            }

            public TextChange undo() {
                if (!undoStack.isEmpty()) {
                    TextChange change = undoStack.pop();
                    redoStack.push(change);
                    return change;
                }
                return null;
            }

            public TextChange redo() {
                if (!redoStack.isEmpty()) {
                    TextChange change = redoStack.pop();
                    undoStack.push(change);
                    return change;
                }
                return null;
            }
        }

        private final UndoManager undoManager = new UndoManager();

        public void undo () {
            TextChange change = undoManager.undo();
            if (change != null) {
                try {
                    if (change.getType() == TextChange.ChangeType.INSERT) {
                        setText(getText().substring(0, Math.min(change.getCursorPos(), change.getSelectionPos())) + getText().substring(Math.min(change.getCursorPos(), change.getSelectionPos()) + change.getText().length()));
                        pageEdit.setCursorPos(change.getCursorPos());
                        pageEdit.setSelectionPos(change.getSelectionPos());
                    } else if (change.getType() == TextChange.ChangeType.DELETE) {
                        setText(getText().substring(0, Math.min(change.getCursorPos(), change.getSelectionPos())) + change.getText() + getText().substring(Math.min(change.getCursorPos(), change.getSelectionPos())));
                        pageEdit.setCursorPos(change.getCursorPos());
                        pageEdit.setSelectionPos(change.getSelectionPos());
                    }
                } catch (Exception ignored) {
                }
                clearDisplayCache(PageDrawing.focusedWritableTextBox.getLeft().currentBook.getUUID());
            }
        }

        public void redo () {
            TextChange change = undoManager.redo();
            if (change != null) {
                try {
                    if (change.getType() == TextChange.ChangeType.INSERT) {
                        setText(getText().substring(0, Math.min(change.getCursorPos(), change.getSelectionPos())) + change.getText() + getText().substring(Math.min(change.getCursorPos(), change.getSelectionPos())));
                        pageEdit.setCursorPos(change.getCursorPos());
                        pageEdit.setSelectionPos(change.getSelectionPos());
                    } else if (change.getType() == TextChange.ChangeType.DELETE) {
                        setText(getText().substring(0, Math.min(change.getCursorPos(), change.getSelectionPos())) + getText().substring(Math.min(change.getCursorPos(), change.getSelectionPos()) + change.getText().length()));
                        pageEdit.setCursorPos(change.getCursorPos());
                        pageEdit.setSelectionPos(change.getSelectionPos());
                    }
                } catch (Exception ignored) {
                }
                clearDisplayCache(PageDrawing.focusedWritableTextBox.getLeft().currentBook.getUUID());
            }
        }


        public boolean clicked;
        public Pos2i clickedPos; // for clicking and dragging

    //    private String text = "";
        public final net.minecraft.client.gui.font.TextFieldHelper pageEdit = new net.minecraft.client.gui.font.TextFieldHelper(
                this::getText,
                this::setText,
                this::getClipboard,
                this::setClipboard,
                p_280853_ -> p_280853_.length() < 1224 && ClientProxy.font().wordWrapHeight(p_280853_, 114) <= 178
        ) {

            @Override
            public void insertText(@NotNull String text, @NotNull String clipboardText) {
                if (this.getSelectionPos() != this.getCursorPos()) {
                    text = this.deleteSelection(text);
                }

                this.setCursorPos(Mth.clamp(this.getCursorPos(), 0, text.length()));
                String s = (new StringBuilder(text)).insert(this.getCursorPos(), clipboardText).toString();
                if (this.stringValidator.test(s)) {
                    undoManager.recordChange(new TextChange(TextChange.ChangeType.INSERT, clipboardText, this.getCursorPos(), this.getSelectionPos()));
                    this.setMessageFn.accept(s);
                    this.setCursorPos(Math.min(s.length(), this.getCursorPos() + clipboardText.length()));
                    this.setSelectionPos(this.getCursorPos());
                }
    //            super.insertText(text, clipboardText);
            }

            @Override
            public @NotNull String deleteSelection(@NotNull String text) {

                try {
                    if (this.getSelectionPos() != this.getCursorPos()) {
                        int i = Math.min(this.getCursorPos(), this.getSelectionPos());
                        int j = Math.max(this.getCursorPos(), this.getSelectionPos());
                        String deleted = text.substring(i, j);
                        undoManager.recordChange(new TextChange(TextChange.ChangeType.DELETE, deleted, this.getCursorPos(), this.getSelectionPos()));
                    }
                } catch (Exception ignored) {
                }
                return super.deleteSelection(text);
            }


        };
        /**
         * In milliseconds
         */
        public long lastClickTime;
        public int lastIndex = -1;
        private Map<UUID, DisplayCache> displayCache = new HashMap<>();

        private void setClipboard (String clipboardValue){
            net.minecraft.client.gui.font.TextFieldHelper.setClipboardContents(Minecraft.getInstance(), clipboardValue);
        }

        private String getClipboard () {
            return net.minecraft.client.gui.font.TextFieldHelper.getClipboardContents(Minecraft.getInstance());
        }

        private String getText () {
            if (PageDrawing.focusedWritableTextBox != null) {
                BookOfShadowsAltarTile altarTile = PageDrawing.focusedWritableTextBox.getLeft();
                if (altarTile.currentBook != null) {
                    return altarTile.currentBook.getTextBoxText(PageDrawing.focusedWritableTextBox.getMiddle().toString(), parent.index);
                }
            }
            return "";
        }

        private String getText (BookData bookData){
            if (bookData != null) {
                return bookData.getTextBoxText(parent.parentLocation.toString(), parent.index);
            }

            return "";
        }

        private void setText (String text){
            if (PageDrawing.focusedWritableTextBox != null) {
                BookOfShadowsAltarTile altarTile = PageDrawing.focusedWritableTextBox.getLeft();
                if (altarTile.currentBook != null) {
                    altarTile.currentBook = altarTile.currentBook.updateTextBoxText(PageDrawing.focusedWritableTextBox.getMiddle().toString(), parent.index, text);
                    //send update of current book to server/players
                    HexereiPacketHandler.sendToServer(new UpdateBookDataToServer(altarTile.getBlockPos(), altarTile.currentBook));
                }
            }
    //        this.text = text;
        }

        public void selectWord ( int index, BookData bookData){
            String s = this.getText(bookData);
            this.pageEdit.setSelectionRange(StringSplitter.getWordPosition(s, -1, index, false), StringSplitter.getWordPosition(s, 1, index, false));
        }

        public boolean keyPressed ( int keyCode){
            if (keyCode == 90 && Screen.hasControlDown() && !Screen.hasShiftDown() && !Screen.hasAltDown()) { // escape pressed

                undo();
                return true;
            }
            if (keyCode == 89 && Screen.hasControlDown() && !Screen.hasShiftDown() && !Screen.hasAltDown()) { // escape pressed

                redo();
                return true;
            }
            if (keyCode == 256) { // escape pressed

                PageDrawing.clearFocusedWritableTextBox();
                return true;
            }
            if (Screen.isSelectAll(keyCode)) {
                pageEdit.selectAll();
                return true;
            } else if (Screen.isCopy(keyCode)) {
                pageEdit.copy();
                return true;
            } else if (Screen.isPaste(keyCode)) {
                pageEdit.paste();
                return true;
            } else if (Screen.isCut(keyCode)) {
                pageEdit.cut();
                return true;
            } else {
                net.minecraft.client.gui.font.TextFieldHelper.CursorStep textfieldhelper$cursorstep = Screen.hasControlDown()
                        ? net.minecraft.client.gui.font.TextFieldHelper.CursorStep.WORD
                        : net.minecraft.client.gui.font.TextFieldHelper.CursorStep.CHARACTER;
                switch (keyCode) {
                    case 257:
                    case 335:
                        pageEdit.insertText("\n");
                        return true;
                    case 259:
                        pageEdit.removeFromCursor(-1, textfieldhelper$cursorstep);
                        return true;
                    case 261:
                        pageEdit.removeFromCursor(1, textfieldhelper$cursorstep);
                        return true;
                    case 262:
                        pageEdit.moveBy(1, Screen.hasShiftDown(), textfieldhelper$cursorstep);
                        return true;
                    case 263:
                        pageEdit.moveBy(-1, Screen.hasShiftDown(), textfieldhelper$cursorstep);
                        return true;
                    case 264:
                        keyDown();
                        return true;
                    case 265:
                        keyUp();
                        return true;
                    case 268:
                        keyHome();
                        return true;
                    case 269:
                        keyEnd();
                        return true;
                    default:
                        return false;
                }
            }
        }

        public void keyUp () {
            this.changeLine(-1);
        }

        public void keyDown () {
            this.changeLine(1);
        }

        public void changeLine ( int yChange){
            int i = this.pageEdit.getCursorPos();
            int j = this.getDisplayCache(PageDrawing.focusedWritableTextBox.getLeft().currentBook).changeLine(i, yChange);
            this.pageEdit.setCursorPos(j, Screen.hasShiftDown());
        }

        public void keyHome () {
            if (Screen.hasControlDown()) {
                this.pageEdit.setCursorToStart(Screen.hasShiftDown());
            } else {
                int i = this.pageEdit.getCursorPos();
                int j = this.getDisplayCache(PageDrawing.focusedWritableTextBox.getLeft().currentBook).findLineStart(i);
                this.pageEdit.setCursorPos(j, Screen.hasShiftDown());
            }
        }

        public void keyEnd () {
            if (Screen.hasControlDown()) {
                this.pageEdit.setCursorToEnd(Screen.hasShiftDown());
            } else {
                DisplayCache bookeditscreen$displaycache = this.getDisplayCache(PageDrawing.focusedWritableTextBox.getLeft().currentBook);
                int i = this.pageEdit.getCursorPos();
                int j = bookeditscreen$displaycache.findLineEnd(i);
                this.pageEdit.setCursorPos(j, Screen.hasShiftDown());
            }
        }

        public void clearDisplayCache (UUID uuid){
            this.displayCache.remove(uuid);
        }

        public DisplayCache getDisplayCache (BookData bookData){
            if (!this.displayCache.containsKey(bookData.getUUID())) {
                this.displayCache.put(bookData.getUUID(), this.rebuildDisplayCache(256, bookData));
    //            this.displayCache = this.rebuildDisplayCache(256);
            }

            return this.displayCache.get(bookData.getUUID());
        }


        public Pos2i getCursorPosOf ( int i, BookData bookData){
            String s = this.getText(bookData);
            int k = findLineFromPos(getDisplayCache(bookData).lineStarts, i);
            int l = ClientProxy.font().width(s.substring(Math.clamp(getDisplayCache(bookData).lineStarts[k], 0, s.length()), Math.clamp(i, 0, s.length())));
            Pos2i pos2i = new Pos2i(l, k * 9);
            return pos2i;
        }

        private DisplayCache rebuildDisplayCache ( int width, BookData bookData) {
            String s = this.getText(bookData);
            if (s.isEmpty()) {
                return DisplayCache.EMPTY;
            } else {
                int i = this.pageEdit.getCursorPos();
                int j = this.pageEdit.getSelectionPos();
                IntList intlist = new IntArrayList();
                List<LineInfo> list = Lists.newArrayList();
                MutableInt mutableint = new MutableInt();
                MutableBoolean mutableboolean = new MutableBoolean();
                StringSplitter stringsplitter = ClientProxy.font().getSplitter();
                stringsplitter.splitLines(s, 114, Style.EMPTY, true, (p_98132_, p_98133_, p_98134_) -> {
                    int k3 = mutableint.getAndIncrement();
                    String s2 = s.substring(p_98133_, p_98134_);
                    mutableboolean.setValue(s2.endsWith("\n"));
                    String s3 = StringUtils.stripEnd(s2, " \n");
                    int l3 = k3 * 9;
                    Pos2i bookeditscreen$pos2i1 = this.convertLocalToScreen(new Pos2i(0, l3), width);
                    intlist.add(p_98133_);
                    list.add(new LineInfo(p_98132_, s3, bookeditscreen$pos2i1.x, bookeditscreen$pos2i1.y));
                });
                int[] aint = intlist.toIntArray();
                boolean flag = i == s.length();
                Pos2i bookeditscreen$pos2i;
                if (flag && mutableboolean.isTrue()) {
                    bookeditscreen$pos2i = new Pos2i(0, list.size() * 9);
                } else {
                    int k = findLineFromPos(aint, i);
                    int l = ClientProxy.font().width(s.substring(Math.clamp(aint[k], 0, s.length()), Math.clamp(i, 0, s.length())));
                    bookeditscreen$pos2i = new Pos2i(l, k * 9);
                }

                List<Rect2i> list1 = Lists.newArrayList();
                if (i != j) {
                    int l2 = Math.min(i, j);
                    int i1 = Math.max(i, j);
                    int j1 = findLineFromPos(aint, l2);
                    int k1 = findLineFromPos(aint, i1);
                    if (j1 == k1) {
                        int l1 = j1 * 9;
                        int i2 = aint[j1];
                        list1.add(this.createPartialLineSelection(s, stringsplitter, l2, i1, l1, i2, width));
                    } else {
                        int i3 = j1 + 1 > aint.length ? s.length() : aint[j1 + 1];
                        list1.add(this.createPartialLineSelection(s, stringsplitter, l2, i3, j1 * 9, aint[j1], width));

                        for (int j3 = j1 + 1; j3 < k1; j3++) {
                            int j2 = j3 * 9;
                            String s1 = s.substring(aint[j3], aint[j3 + 1]);
                            int k2 = (int) stringsplitter.stringWidth(s1);
                            list1.add(this.createSelection(new Pos2i(0, j2), new Pos2i(k2, j2 + 9), width));
                        }

                        list1.add(this.createPartialLineSelection(s, stringsplitter, aint[k1], i1, k1 * 9, aint[k1], width));
                    }
                }

                return new DisplayCache(
                        s, bookeditscreen$pos2i, flag, aint, list.toArray(new LineInfo[0]), list1.toArray(new Rect2i[0])
                );
            }
        }

        private Rect2i createPartialLineSelection (String input, StringSplitter splitter,int startPos, int endPos,
            int y, int lineStart, int width){
    //        String s = input.substring(lineStart, startPos);
            String s = input.substring(Math.clamp(lineStart, 0, input.length()), Math.clamp(startPos, 0, input.length()));
    //        String s1 = input.substring(lineStart, endPos);
            String s1 = input.substring(Math.clamp(lineStart, 0, input.length()), Math.clamp(endPos, 0, input.length()));
            Pos2i bookeditscreen$pos2i = new Pos2i((int) splitter.stringWidth(s), y);
            Pos2i bookeditscreen$pos2i1 = new Pos2i((int) splitter.stringWidth(s1), y + 9);
            return this.createSelection(bookeditscreen$pos2i, bookeditscreen$pos2i1, width);
        }

        private Rect2i createSelection (Pos2i corner1, Pos2i corner2,int width){
            Pos2i bookeditscreen$pos2i = this.convertLocalToScreen(corner1, width);
            Pos2i bookeditscreen$pos2i1 = this.convertLocalToScreen(corner2, width);
            int i = Math.min(bookeditscreen$pos2i.x, bookeditscreen$pos2i1.x);
            int j = Math.max(bookeditscreen$pos2i.x, bookeditscreen$pos2i1.x);
            int k = Math.min(bookeditscreen$pos2i.y, bookeditscreen$pos2i1.y);
            int l = Math.max(bookeditscreen$pos2i.y, bookeditscreen$pos2i1.y);
            return new Rect2i(i, k, j - i, l - k);
        }

        public Pos2i convertScreenToLocal (Pos2i screenPos,int width){
            return new Pos2i(screenPos.x - (width - 192) / 2 - 36, screenPos.y - 32);
        }

        public Pos2i convertLocalToScreen (Pos2i localScreenPos,int width){
            return new Pos2i(localScreenPos.x + (width - 192) / 2 + 36, localScreenPos.y + 32);
        }

            @OnlyIn(Dist.CLIENT)
        static public class DisplayCache {
            static final DisplayCache EMPTY;
            private final String fullText;
            final Pos2i cursor;
            final boolean cursorAtEnd;
            private final int[] lineStarts;
            final LineInfo[] lines;
            final Rect2i[] selection;

            public DisplayCache(String fullText, Pos2i cursor, boolean cursorAtEnd, int[] lineStarts, LineInfo[] lines, Rect2i[] selection) {
                this.fullText = fullText;
                this.cursor = cursor;
                this.cursorAtEnd = cursorAtEnd;
                this.lineStarts = lineStarts;
                this.lines = lines;
                this.selection = selection;
            }

            public int getIndexAtPosition(Font font, Pos2i cursorPosition) {
                int i = cursorPosition.y / 9;
                if (i < 0) {
                    return 0;
                } else if (i >= this.lines.length) {
                    return this.fullText.length();
                } else {
                    LineInfo bookeditscreen$lineinfo = this.lines[i];
                    return this.lineStarts[i] + font.getSplitter().plainIndexAtWidth(bookeditscreen$lineinfo.contents, cursorPosition.x, bookeditscreen$lineinfo.style);
                }
            }

            public int changeLine(int xChange, int yChange) {
                int i = findLineFromPos(this.lineStarts, xChange);
                int j = i + yChange;
                int k;
                if (0 <= j && j < this.lineStarts.length) {
                    int l = xChange - this.lineStarts[i];
                    int i1 = this.lines[j].contents.length();
                    k = this.lineStarts[j] + Math.min(l, i1);
                } else {
                    k = xChange;
                }

                return k;
            }

            public int findLineStart(int line) {
                int i = findLineFromPos(this.lineStarts, line);
                return this.lineStarts[i];
            }

            public int findLineEnd(int line) {
                int i = findLineFromPos(this.lineStarts, line);
                return this.lineStarts[i] + this.lines[i].contents.length();
            }

            static {
                EMPTY = new DisplayCache("", new Pos2i(0, 0), true, new int[]{0}, new LineInfo[]{new LineInfo(Style.EMPTY, "", 0, 0)}, new Rect2i[0]);
            }
        }

        static int findLineFromPos ( int[] lineStarts, int find){
            int i = Arrays.binarySearch(lineStarts, find);
            return i < 0 ? -(i + 2) : i;
        }

        @OnlyIn(Dist.CLIENT)
        static class LineInfo {
            final Style style;
            final String contents;
            final Component asComponent;
            final int x;
            final int y;

            public LineInfo(Style style, String contents, int x, int y) {
                this.style = style;
                this.contents = contents;
                this.x = x;
                this.y = y;
                this.asComponent = Component.literal(contents).setStyle(style);
            }
        }

        @OnlyIn(Dist.CLIENT)
        public static class Pos2i {
            public final int x;
            public final int y;

            Pos2i(int x, int y) {
                this.x = x;
                this.y = y;
            }
        }
    }

}
