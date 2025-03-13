package net.joefoxe.hexerei.data.books;

import com.mojang.blaze3d.platform.NativeImage;
import net.joefoxe.hexerei.event.ClientEvents;
import net.joefoxe.hexerei.util.HexereiPacketHandler;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.joefoxe.hexerei.util.message.PaintDataToServer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public class PaintSystem {

    // global clipboard image
    public static BufferedImage clipboardImage; // sadly this is not the OS clipboard
    public static Rectangle clipboardBounds;
    public static BufferedImage clipboardMask;

    // Main system properties
    private final List<Layer> layers = new ArrayList<>();
    private Layer activeLayer;
    public final ResourceLocation parentLocation;
    private UUID uuid;
    public int width;
    public int height;
    private static Colors colors = new Colors(List.of());
    private float colorsVisibility;
    private float colorsVisibilityOld;
    private Brush brush;
    private Selection selection;
    public List<Button> buttons = new ArrayList<>();
    private ValueSliders valueSliders;
    public BufferedImage compositeImage;
    public BufferedImage strokeMask; // Temporary mask to track drawn pixels
    public Brush.Type strokeType;
    private BufferedImage movingSelection; // Temporary image for the moving selection
    private Pos2i movingSelectionOffset = new Pos2i(0, 0); // Offset of the moving selection
    private Pos2i movingSelectionClickedPos = new Pos2i(0, 0); // Offset of the moving selection
    private boolean skipNextRelease;
    private boolean dirty;
    private boolean updateToServer = false;
    private static Tool currentTool = Tool.BRUSH; // Default tool
    private final List<Tool> tools = Arrays.asList(Tool.values());
    public float cursorX = -10, cursorY = -10;
    public float cursorXOld = -10, cursorYOld = -10;
    public boolean shouldTick = false;
    public boolean toolsVisible = false;
    public float toolVisibility = 0f;
    public float toolVisibilityOld = 0f;
    public boolean locked = false;
    public UUID lockedByUUID = new UUID(0, 0);
    public Component lockedByName = Component.empty();

    // Fields for undo/redo system integration
    private DrawAction currentDrawAction = null;
    private Rectangle initialSelectionBounds = null;
    private BufferedImage initialSelectionMask = null;


    public PaintSystem(int width, int height, ResourceLocation parentLocation, UUID uuid) {
        this.parentLocation = parentLocation;
        this.uuid = uuid;
        this.width = width;
        this.height = height;
        this.brush = new Brush(this);
        this.selection = new Selection(this);
        this.addLayer(width, height);
        this.valueSliders = new ValueSliders(this);
        this.valueSliders.updateColorSliders(colors.getColor());
        this.valueSliders.updateHardnessSlider(Brush.hardness);
        this.valueSliders.updateBrushSizeSlider(Brush.size);
        this.valueSliders.updateToleranceSlider(Brush.tolerance);
        this.movingSelection = null;

        this.buttons.add(new Button(-0.5f, 0f,5.5f, 0f,32, 32, value -> value / 2 * 1.01f,
                "hexerei:textures/book/paint_tools/selection_box.png",
                "hexerei:textures/book/paint_tools/selection_box_hover.png",
                "hexerei:textures/book/paint_tools/selection_box.png",
                paintSystem -> paintSystem.setCurrentTool(Tool.SELECTION),
                Component.translatable("Select").withStyle(ChatFormatting.GRAY),
                ps -> {},
                ps -> ps.getCurrentTool() == Tool.SELECTION,
                ps -> false,
                ps -> toolsVisible
        ));

        this.buttons.add(new Button(-0.5f, 0.8f,5.5f, 0.8f,32, 32, value -> value / 2 * 1.01f,
                "hexerei:textures/book/paint_tools/magic_wand.png",
                "hexerei:textures/book/paint_tools/magic_wand_hover.png",
                "hexerei:textures/book/paint_tools/magic_wand.png",
                paintSystem -> paintSystem.setCurrentTool(Tool.MAGIC_WAND),
                Component.translatable("Magic Wand").withStyle(ChatFormatting.GRAY),
                ps -> {},
                ps -> ps.getCurrentTool() == Tool.MAGIC_WAND,
                ps -> false,
                ps -> toolsVisible
        ));

        this.buttons.add(new Button(-0.5f, 1.6f,5.5f, 1.6f,32, 32, value -> value / 2 * 1.01f,
                "hexerei:textures/book/paint_tools/move.png",
                "hexerei:textures/book/paint_tools/move_hover.png",
                "hexerei:textures/book/paint_tools/move.png",
                paintSystem -> paintSystem.setCurrentTool(Tool.MOVE),
                Component.translatable("Move").withStyle(ChatFormatting.GRAY),
                ps -> {},
                ps -> ps.getCurrentTool() == Tool.MOVE,
                ps -> false,
                ps -> toolsVisible
        ));

        this.buttons.add(new Button(-0.5f, 2.4f,5.5f, 2.4f,32, 32, value -> value / 2 * 1.01f,
                "hexerei:textures/book/paint_tools/brush.png",
                "hexerei:textures/book/paint_tools/brush_hover.png",
                "hexerei:textures/book/paint_tools/brush.png",
                paintSystem -> paintSystem.setCurrentTool(Tool.BRUSH),
                Component.translatable("Brush").withStyle(ChatFormatting.GRAY),
                ps -> {},
                ps -> ps.getCurrentTool() == Tool.BRUSH,
                ps -> false,
                ps -> toolsVisible
        ));

        this.buttons.add(new Button(-0.5f, 3.2f,5.5f, 3.2f,32, 32, value -> value / 2 * 1.01f,
                "hexerei:textures/book/paint_tools/eraser.png",
                "hexerei:textures/book/paint_tools/eraser_hover.png",
                "hexerei:textures/book/paint_tools/eraser.png",
                paintSystem -> paintSystem.setCurrentTool(Tool.ERASER),
                Component.translatable("Eraser").withStyle(ChatFormatting.GRAY),
                ps -> {},
                ps -> ps.getCurrentTool() == Tool.ERASER,
                ps -> false,
                ps -> toolsVisible
        ));

        this.buttons.add(new Button(-0.5f, 4f,5.5f, 4f,32, 32, value -> value / 2 * 1.01f,
                "hexerei:textures/book/paint_tools/fill.png",
                "hexerei:textures/book/paint_tools/fill_hover.png",
                "hexerei:textures/book/paint_tools/fill.png",
                paintSystem -> paintSystem.setCurrentTool(Tool.FILL),
                Component.translatable("Fill").withStyle(ChatFormatting.GRAY),
                ps -> {},
                ps -> ps.getCurrentTool() == Tool.FILL,
                ps -> false,
                ps -> toolsVisible
        ));

        this.buttons.add(new Button(-0.5f, 4.8f,5.5f, 4.8f,32, 32, value -> value / 2 * 1.01f,
                "hexerei:textures/book/paint_tools/color_picker.png",
                "hexerei:textures/book/paint_tools/color_picker_hover.png",
                "hexerei:textures/book/paint_tools/color_picker.png",
                paintSystem -> paintSystem.setCurrentTool(Tool.EYEDROPPER),
                Component.translatable("Color Picker").withStyle(ChatFormatting.GRAY),
                ps -> {},
                ps -> ps.getCurrentTool() == Tool.EYEDROPPER,
                ps -> false,
                ps -> toolsVisible
        ));

        this.buttons.add(new ToggleButton(-0.5f, 6.2f,5.5f, 6.2f,32, 32, value -> value / 2 * 1.01f,
                "hexerei:textures/book/paint_tools/visible.png",
                "hexerei:textures/book/paint_tools/visible_hover.png",
                "hexerei:textures/book/paint_tools/visible.png",
                "hexerei:textures/book/paint_tools/visible_toggled.png",
                "hexerei:textures/book/paint_tools/visible_toggled_hover.png",
                "hexerei:textures/book/paint_tools/visible_toggled.png",
                paintSystem -> toolsVisible = !toolsVisible,
                paintSystem -> toolsVisible = !toolsVisible,
                Component.translatable("Tool Visibility").withStyle(ChatFormatting.GRAY),
                ps -> {},
                ps -> false,
                ps -> locked,
                ps -> !locked,
                ps -> !toolsVisible
        ));

        AtomicReference<Float> lockY = new AtomicReference<>(0f);
        AtomicReference<Float> lockYO = new AtomicReference<>(0f);
        AtomicReference<Float> lockYTarget = new AtomicReference<>(0f);
        this.buttons.add(new ToggleButton(
                (ps, partial) -> -0.5f, (ps, partial) -> 5.4f + HexereiUtil.easeInOutCubic(Mth.lerp(partial, lockYO.get(), lockY.get())) * 0.8f,
                (ps, partial) -> 5.5f, (ps, partial) -> 5.4f + HexereiUtil.easeInOutCubic(Mth.lerp(partial, lockYO.get(), lockY.get())) * 0.8f,
                32, 32, value -> value / 2 * 1.01f,
                "hexerei:textures/book/paint_tools/locked.png",
                "hexerei:textures/book/paint_tools/locked_hover.png",
                "hexerei:textures/book/paint_tools/locked.png",
                "hexerei:textures/book/paint_tools/locked_toggled.png",
                "hexerei:textures/book/paint_tools/locked_toggled_hover.png",
                "hexerei:textures/book/paint_tools/locked_toggled.png",
                paintSystem -> {
                    if (Minecraft.getInstance().player != null) {
                        locked = true;
                        lockedByName = Minecraft.getInstance().player.getName();
                        lockedByUUID = Minecraft.getInstance().player.getUUID();
                        HexereiPacketHandler.sendToServer(new PaintDataToServer(paintSystem.toPaintData()));
                    }
                },
                paintSystem -> {
                    if (Minecraft.getInstance().player != null && lockedByUUID != null && lockedByUUID.equals(Minecraft.getInstance().player.getUUID())){
                        locked = false;
                        lockedByName = Component.empty();
                        lockedByUUID = new UUID(0, 0);
                        HexereiPacketHandler.sendToServer(new PaintDataToServer(paintSystem.toPaintData()));
                    }
                },
                Component.translatable("Lock").withStyle(ChatFormatting.GRAY),
                ps -> {
                    lockYO.set(lockY.get());
                    lockYTarget.set(ps.locked ? 1f : 0f);
                    lockY.set(HexereiUtil.moveTo(lockY.get(), lockYTarget.get(), Math.abs(lockYTarget.get() - lockY.get()) / 5f + (0.01f)));
                },
                ps -> false,
                ps -> false,
                ps -> !toolsVisible,
                ps -> locked
        ){

            @Override
            public List<Component> getTooltipList() {
                List<Component> components = new ArrayList<>();
                if (locked) {
                    components.add(Component.translatable("Unlock").withStyle(ChatFormatting.GRAY));
                    components.add(Component.translatable("Locked by: ").withStyle(ChatFormatting.DARK_GRAY).append(lockedByName));
                } else {
                    components.add(Component.translatable("Lock").withStyle(ChatFormatting.GRAY));
                }
                return components;
            }
        });

        this.buttons.add(new Button(0.6f, -1f,0.6f, -1f,32, 32, value -> value / 2 * 1.01f,
                "hexerei:textures/book/paint_tools/undo.png",
                "hexerei:textures/book/paint_tools/undo_hover.png",
                "hexerei:textures/book/paint_tools/undo_disabled.png",
                paintSystem -> paintSystem.actionManager.undo(),
                Component.translatable("Undo").withStyle(ChatFormatting.GRAY),
                ps -> {},
                ps -> false,
                ps -> ps.actionManager.undoStack.isEmpty(),
                ps -> toolsVisible
        ));

        this.buttons.add(new Button(1.5f, -1f,1.5f, -1f,32, 32, value -> value / 2 * 1.01f,
                "hexerei:textures/book/paint_tools/redo.png",
                "hexerei:textures/book/paint_tools/redo_hover.png",
                "hexerei:textures/book/paint_tools/redo_disabled.png",
                paintSystem -> paintSystem.actionManager.redo(),
                Component.translatable("Redo").withStyle(ChatFormatting.GRAY),
                ps -> {},
                ps -> false,
                ps -> ps.actionManager.redoStack.isEmpty(),
                ps -> toolsVisible
        ));

        this.buttons.add(new Button(2.4f, -1f,2.4f, -1f,32, 32, value -> value / 2 * 1.01f,
                "hexerei:textures/book/paint_tools/cut.png",
                "hexerei:textures/book/paint_tools/cut_hover.png",
                "hexerei:textures/book/paint_tools/cut_disabled.png",
                PaintSystem::cutSelectionToClipboard,
                Component.translatable("Cut").withStyle(ChatFormatting.GRAY),
                ps -> {},
                ps -> false,
                ps -> ps.selection.isEmpty(),
                ps -> toolsVisible
        ));

        this.buttons.add(new Button(3.3f, -1f,3.3f, -1f,32, 32, value -> value / 2 * 1.01f,
                "hexerei:textures/book/paint_tools/copy.png",
                "hexerei:textures/book/paint_tools/copy_hover.png",
                "hexerei:textures/book/paint_tools/copy_disabled.png",
                PaintSystem::copySelectionToClipboard,
                Component.translatable("Copy").withStyle(ChatFormatting.GRAY),
                ps -> {},
                ps -> false,
                ps -> ps.selection.isEmpty(),
                ps -> toolsVisible
        ));

        this.buttons.add(new Button(4.2f, -1f,4.2f, -1f,32, 32, value -> value / 2 * 1.01f,
                "hexerei:textures/book/paint_tools/paste.png",
                "hexerei:textures/book/paint_tools/paste_hover.png",
                "hexerei:textures/book/paint_tools/paste_disabled.png",
                PaintSystem::pasteClipboard,
                Component.translatable("Paste").withStyle(ChatFormatting.GRAY),
                ps -> {},
                ps -> false,
                ps -> PaintSystem.clipboardImage == null,
                ps -> toolsVisible
        ));

        // Magic Wand tools
        this.buttons.add(new Button(0.6f, 7.1f,1.4f, 7.1f,32, 32, value -> value / 2 * 1.01f,
                "hexerei:textures/book/paint_tools/magic_wand_replace.png",
                "hexerei:textures/book/paint_tools/magic_wand_replace_hover.png",
                "hexerei:textures/book/paint_tools/magic_wand_replace.png",
                (ps) -> ps.selection.setType(Selection.Type.REPLACE),
                Component.translatable("Replace").withStyle(ChatFormatting.GRAY),
                ps -> {},
                ps -> ps.selection.getType() == Selection.Type.REPLACE,
                ps -> ps.getCurrentTool() != Tool.MAGIC_WAND,
                ps -> ps.getCurrentTool() == Tool.MAGIC_WAND && toolsVisible
        ));

        this.buttons.add(new Button(1.5f, 7.1f,2.3f, 7.1f,32, 32, value -> value / 2 * 1.01f,
                "hexerei:textures/book/paint_tools/magic_wand_add.png",
                "hexerei:textures/book/paint_tools/magic_wand_add_hover.png",
                "hexerei:textures/book/paint_tools/magic_wand_add.png",
                (ps) -> ps.selection.setType(Selection.Type.ADD),
                Component.translatable("Add").withStyle(ChatFormatting.GRAY),
                ps -> {},
                ps -> ps.selection.getType() == Selection.Type.ADD,
                ps -> ps.getCurrentTool() != Tool.MAGIC_WAND,
                ps -> ps.getCurrentTool() == Tool.MAGIC_WAND && toolsVisible
        ));

        this.buttons.add(new Button(2.4f, 7.1f,3.2f, 7.1f,32, 32, value -> value / 2 * 1.01f,
                "hexerei:textures/book/paint_tools/magic_wand_remove.png",
                "hexerei:textures/book/paint_tools/magic_wand_remove_hover.png",
                "hexerei:textures/book/paint_tools/magic_wand_remove.png",
                (ps) -> ps.selection.setType(Selection.Type.REMOVE),
                Component.translatable("Remove").withStyle(ChatFormatting.GRAY),
                ps -> {},
                ps -> ps.selection.getType() == Selection.Type.REMOVE,
                ps -> ps.getCurrentTool() != Tool.MAGIC_WAND,
                ps -> ps.getCurrentTool() == Tool.MAGIC_WAND && toolsVisible
        ));

        this.buttons.add(new ToggleButton(3.55f, 6.95f,4.35f, 6.95f,18, 18, value -> value / 2 * 0.9f,
                "hexerei:textures/book/paint_tools/global_toggled.png",
                "hexerei:textures/book/paint_tools/global_toggled_hover.png",
                "hexerei:textures/book/paint_tools/global_toggled.png",
                "hexerei:textures/book/paint_tools/global_detoggled.png",
                "hexerei:textures/book/paint_tools/global_detoggled_hover.png",
                "hexerei:textures/book/paint_tools/global_detoggled.png",
                (ps) -> ps.selection.setGlobal(!ps.selection.getGlobal()),
                (ps) -> ps.selection.setGlobal(!ps.selection.getGlobal()),
                Component.translatable("Toggle Global").withStyle(ChatFormatting.GRAY),
                ps -> {},
                ps -> false,
                ps -> ps.getCurrentTool() != Tool.MAGIC_WAND,
                ps -> ps.getCurrentTool() == Tool.MAGIC_WAND && toolsVisible,
                ps -> !ps.selection.getGlobal()
        ));
        // End Magic Wand tools

        // Selection Box tools
        this.buttons.add(new Button(0.6f, 7.1f,1.4f, 7.1f,32, 32, value -> value / 2 * 1.01f,
                "hexerei:textures/book/paint_tools/selection_box.png",
                "hexerei:textures/book/paint_tools/selection_box_hover.png",
                "hexerei:textures/book/paint_tools/selection_box.png",
                (ps) -> ps.selection.setType(Selection.Type.REPLACE),
                Component.translatable("Replace").withStyle(ChatFormatting.GRAY),
                ps -> {},
                ps -> ps.selection.getType() == Selection.Type.REPLACE,
                ps -> false,
                ps -> ps.getCurrentTool() == Tool.SELECTION && toolsVisible
        ));

        this.buttons.add(new Button(1.5f, 7.1f,2.3f, 7.1f,32, 32, value -> value / 2 * 1.01f,
                "hexerei:textures/book/paint_tools/selection_box_add.png",
                "hexerei:textures/book/paint_tools/selection_box_add_hover.png",
                "hexerei:textures/book/paint_tools/selection_box_add.png",
                (ps) -> ps.selection.setType(Selection.Type.ADD),
                Component.translatable("Add").withStyle(ChatFormatting.GRAY),
                ps -> {},
                ps -> ps.selection.getType() == Selection.Type.ADD,
                ps -> ps.getCurrentTool() != Tool.SELECTION,
                ps -> ps.getCurrentTool() == Tool.SELECTION && toolsVisible
        ));

        this.buttons.add(new Button(2.4f, 7.1f,3.2f, 7.1f,32, 32, value -> value / 2 * 1.01f,
                "hexerei:textures/book/paint_tools/selection_box_remove.png",
                "hexerei:textures/book/paint_tools/selection_box_remove_hover.png",
                "hexerei:textures/book/paint_tools/selection_box_remove.png",
                (ps) -> ps.selection.setType(Selection.Type.REMOVE),
                Component.translatable("Remove").withStyle(ChatFormatting.GRAY),
                ps -> {},
                ps -> ps.selection.getType() == Selection.Type.REMOVE,
                ps -> ps.getCurrentTool() != Tool.SELECTION,
                ps -> ps.getCurrentTool() == Tool.SELECTION && toolsVisible
        ));

        this.buttons.add(new Button(3.3f, 7.1f,4.1f, 7.1f,32, 32, value -> value / 2 * 1.01f,
                "hexerei:textures/book/paint_tools/selection_box_deselect.png",
                "hexerei:textures/book/paint_tools/selection_box_deselect_hover.png",
                "hexerei:textures/book/paint_tools/selection_box_deselect_disabled.png",
                (ps) -> ps.selection.deselect(),
                Component.translatable("Clear").withStyle(ChatFormatting.GRAY),
                ps -> {},
                ps -> false,
                ps -> selection.isEmpty(),
                ps -> ps.getCurrentTool() == Tool.SELECTION && toolsVisible
        ));
        // End Selection Box tools

        // Fill tools
        this.buttons.add(new ToggleButton(3.55f, 6.95f,4.35f, 6.95f,18, 18, value -> value / 2,
                "hexerei:textures/book/paint_tools/global_toggled.png",
                "hexerei:textures/book/paint_tools/global_toggled_hover.png",
                "hexerei:textures/book/paint_tools/global_toggled.png",
                "hexerei:textures/book/paint_tools/global_detoggled.png",
                "hexerei:textures/book/paint_tools/global_detoggled_hover.png",
                "hexerei:textures/book/paint_tools/global_detoggled.png",
                (ps) -> ps.brush.setGlobal(!ps.brush.getGlobal()),
                (ps) -> ps.brush.setGlobal(!ps.brush.getGlobal()),
                Component.translatable("Toggle Global").withStyle(ChatFormatting.GRAY),
                ps -> {},
                ps -> false,
                ps -> ps.getCurrentTool() != Tool.FILL,
                ps -> ps.getCurrentTool() == Tool.FILL && toolsVisible,
                ps -> !ps.brush.getGlobal()
        ));
        // End Fill tools
    }


    public PaintData toPaintData() {
        List<PaintData.LayerData> layerDataList = new ArrayList<>();

        for(Layer layer : layers) {

            int[] pixels = new int[layer.pixels.getWidth() * layer.pixels.getHeight()];
            layer.pixels.getRGB(0, 0, layer.pixels.getWidth(), layer.pixels.getHeight(), pixels, 0, layer.pixels.getWidth());

            layerDataList.add(new PaintData.LayerData(
                    layer.pixels.getWidth(),
                    layer.pixels.getHeight(),
                    Arrays.stream(pixels).boxed().toList(),
                    layer.opacity,
                    layer.blendMode.name(),
                    layer.name
            ));
        }

            return new PaintData(width, height, layerDataList, parentLocation, uuid, locked, lockedByUUID, lockedByName);
    }

    public void fromPaintData(PaintData data) {
        layers.clear();
        setActiveLayer(null);
        this.locked = data.locked;
        this.lockedByUUID = data.lockedByUUID;
        this.lockedByName = data.lockedByName;
        for(PaintData.LayerData layerData : data.getLayers()) {
            BufferedImage image = new BufferedImage(
                    layerData.width(),
                    layerData.height(),
                    BufferedImage.TYPE_INT_ARGB
            );
            image.setRGB(0, 0, layerData.width(), layerData.height(),
                    layerData.pixels().stream().mapToInt(Integer::intValue).toArray() , 0, layerData.width());

            Layer layer = new Layer();
            layer.pixels = image;
            layer.opacity = layerData.opacity();
            layer.blendMode = BlendMode.valueOf(layerData.blendMode());
            layer.name = layerData.name();
            addLayer(layer);
        }

        dirty = true;
        rebuildComposite();
        if (compositeImage != null) {
            Object tex = Minecraft.getInstance().getTextureManager().getTexture(getImageLocation());
            if (tex instanceof DynamicTexture dynamicTexture) {
                dynamicTexture.setPixels(convertToNativeImage(compositeImage));
                dynamicTexture.upload();
            }
        }

    }

    public static class Pos2i {
        public final int x;
        public final int y;

        Pos2i(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    /* --- Undo/Redo System --- */
    public interface Action {
        void undo();
        void redo();
    }

    // The ActionManager maintains undo and redo stacks.
    public class ActionManager {
        private final Stack<Action> undoStack = new Stack<>();
        private final Stack<Action> redoStack = new Stack<>();
        private static final int MAX_SIZE = 30;
        private Action currentAction = null;

        public void beginAction(Action action) {
            currentAction = action;
        }

        public void commitAction() {
            if (currentAction != null) {
                undoStack.push(currentAction);
                if (undoStack.size() > MAX_SIZE)
                    undoStack.removeFirst();
                redoStack.clear();
                currentAction = null;
            }
        }

        public void undo() {
            if (!undoStack.isEmpty()) {
                Action action = undoStack.pop();
                action.undo();
                redoStack.push(action);
            }
            activeLayer.dirty = true;
        }

        public void redo() {
            if (!redoStack.isEmpty()) {
                Action action = redoStack.pop();
                action.redo();
                undoStack.push(action);
            }
            activeLayer.dirty = true;
        }
    }
    public ActionManager actionManager = new ActionManager();

    // Helper method for deep-copying a BufferedImage
    public static BufferedImage deepCopy(BufferedImage bi) {
        if (bi == null) return null;
        ColorModel cm = bi.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = bi.copyData(null);
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }

    public class SelectionAndDrawAction implements Action {
        private Layer layer;
        private int index;
        private BufferedImage beforeImage;
        private BufferedImage afterImage;
        private Rectangle oldBounds;
        private BufferedImage oldMask;
        private Rectangle newBounds;
        private BufferedImage newMask;

        public SelectionAndDrawAction(Layer layer, Selection selection) {
            this.layer = layer;
            this.index = getLayers().indexOf(layer);
            // Create a copy so that later modifications do not change this snapshot.
            this.beforeImage = deepCopy(layer.pixels);
            // for the selection area
            this.oldBounds = selection.bounds == null ? null : new Rectangle(selection.bounds);
            this.oldMask = (selection.mask != null ? deepCopy(selection.mask) : null);
        }

        // Call this after applying the brush so we capture the "after" state.
        public void captureAfter(Selection selection) {
            this.afterImage = deepCopy(layer.pixels);
            this.newBounds = selection.bounds == null ? null : new Rectangle(selection.bounds);
            this.newMask = (selection.mask != null ? deepCopy(selection.mask) : null);
        }

        @Override
        public void undo() {
            Layer layer = getLayers().get(index);
            Graphics2D g = layer.pixels.createGraphics();
            g.setBackground(new Color((Color.BLACK.getRGB() & 0x00FFFFFF), true));
            g.clearRect(0, 0, layer.pixels.getWidth(), layer.pixels.getHeight());
            g.drawImage(beforeImage, 0, 0, null);
            g.dispose();
            layer.dirty = true;
            selection.bounds.setLocation(0, 0);
            selection.setSelectionMask((oldMask != null ? deepCopy(oldMask) : null));
            selection.bounds = oldBounds == null ? new Rectangle(0, 0, 0, 0) : new Rectangle(oldBounds);
            updateToServer = true;
        }

        @Override
        public void redo() {
            Layer layer = getLayers().get(index);
            Graphics2D g = layer.pixels.createGraphics();
            g.setBackground(new Color((Color.BLACK.getRGB() & 0x00FFFFFF), true));
            g.clearRect(0, 0, layer.pixels.getWidth(), layer.pixels.getHeight());
            g.drawImage(afterImage, 0, 0, null);
            g.dispose();
            layer.dirty = true;
            selection.bounds.setLocation(0, 0);
            selection.setSelectionMask((newMask != null ? deepCopy(newMask) : null));
            selection.bounds = newBounds == null ? new Rectangle(0, 0, 0, 0) : new Rectangle(newBounds);
            updateToServer = true;
        }
    }

    public class DrawAction implements Action {
        private Layer layer;
        private int index;
        private BufferedImage beforeImage;
        private BufferedImage afterImage;

        public DrawAction(Layer layer, BufferedImage beforeImage) {
            this.layer = layer;
            this.index = getLayers().indexOf(layer);
            // Create a copy so that later modifications do not change this snapshot.
            this.beforeImage = deepCopy(beforeImage);
        }

        // Call this after applying the brush so we capture the "after" state.
        public void captureAfter() {
            this.afterImage = deepCopy(layer.pixels.getSubimage(0, 0, layer.pixels.getWidth(), layer.pixels.getHeight()));
        }

        @Override
        public void undo() {
            Layer layer = getLayers().get(index);
            layer.pixels = deepCopy(beforeImage);
            layer.dirty = true;
            updateToServer = true;
        }

        @Override
        public void redo() {
            Layer layer = getLayers().get(index);
            layer.pixels = deepCopy(afterImage);
            layer.dirty = true;
            updateToServer = true;
        }
    }

    // An action for selection changes.
    public class SelectionAction implements Action {
        private Rectangle oldBounds;
        private BufferedImage oldMask;
        private Rectangle newBounds;
        private BufferedImage newMask;

        public SelectionAction(Rectangle oldBounds, BufferedImage oldMask, Rectangle newBounds, BufferedImage newMask) {
            this.oldBounds = oldBounds == null ? null : new Rectangle(oldBounds);
            this.oldMask = (oldMask != null ? deepCopy(oldMask) : null);
            this.newBounds = newBounds == null ? null : new Rectangle(newBounds);
            this.newMask = (newMask != null ? deepCopy(newMask) : null);
        }

        @Override
        public void undo() {
            selection.bounds.setLocation(0, 0);
            selection.setSelectionMask((oldMask != null ? deepCopy(oldMask) : null));
            selection.bounds = oldBounds == null ? new Rectangle(0, 0, 0, 0) : new Rectangle(oldBounds);
        }

        @Override
        public void redo() {
            selection.bounds.setLocation(0, 0);
            selection.setSelectionMask((newMask != null ? deepCopy(newMask) : null));
            selection.bounds = newBounds == null ? new Rectangle(0, 0, 0, 0) : new Rectangle(newBounds);
        }
    }

    public enum Tool {
        BRUSH("Brush"),
        ERASER("Eraser"),
        SELECTION("Selection"),
        MOVE("Move"),
        FILL("Fill"),
        EYEDROPPER("Eyedropper"),
        MAGIC_WAND("Magic Wand");

        private final String name;
        Tool(String name) { this.name = name; }
        public String getName() { return name; }
        public boolean shouldShowColorSliders() {
            return this == BRUSH || this == ERASER || this == EYEDROPPER || this == FILL;
        }
        public boolean shouldShowBrushSliders() {
            return this == BRUSH || this == ERASER;
        }
        public boolean shouldShowToleranceSliders() {
            return this == FILL || this == MAGIC_WAND;
        }
        public boolean shouldSelectionShowMask() {
            return !(this == BRUSH || this == ERASER || this == EYEDROPPER || this == FILL);
        }
    }

    public class ToggleButton extends Button {

        public Function<PaintSystem, Boolean> toggled;
        public Consumer<PaintSystem> toggledOnClick;
        public String toggledTexture;
        public String toggledHoverTexture;
        public String toggledDisabledTexture;

        ToggleButton(float lx, float ly, float rx, float ry, float width, float height, Function<Float, Float> scale,
                     String texture, String hoverTexture, String disabledTexture, String toggledTexture, String toggledHoverTexture, String toggledDisabledTexture,
                     Consumer<PaintSystem> onClick, Consumer<PaintSystem> toggledOnClick,
                     Component tooltip, Consumer<PaintSystem> onTick, Function<PaintSystem, Boolean> selected, Function<PaintSystem, Boolean> disabled, Function<PaintSystem, Boolean> visible, Function<PaintSystem, Boolean> toggled) {
            super(lx, ly, rx, ry, width, height, scale, texture, hoverTexture, disabledTexture, onClick, tooltip, onTick, selected, disabled, visible);
            this.toggledTexture = toggledTexture;
            this.toggledHoverTexture = toggledHoverTexture;
            this.toggledDisabledTexture = toggledDisabledTexture;
            this.toggledOnClick = toggledOnClick;
            this.toggled = toggled;
        }

        ToggleButton(BiFunction<PaintSystem, Float, Float> lx, BiFunction<PaintSystem, Float, Float> ly, BiFunction<PaintSystem, Float, Float> rx, BiFunction<PaintSystem, Float, Float> ry,
                     float width, float height, Function<Float, Float> scale,
                     String texture, String hoverTexture, String disabledTexture, String toggledTexture, String toggledHoverTexture, String toggledDisabledTexture,
                     Consumer<PaintSystem> onClick, Consumer<PaintSystem> toggledOnClick,
                     Component tooltip, Consumer<PaintSystem> onTick, Function<PaintSystem, Boolean> selected, Function<PaintSystem, Boolean> disabled, Function<PaintSystem, Boolean> visible, Function<PaintSystem, Boolean> toggled) {
            super(lx, ly, rx, ry, width, height, scale, texture, hoverTexture, disabledTexture, onClick, tooltip, onTick, selected, disabled, visible);
            this.toggledTexture = toggledTexture;
            this.toggledHoverTexture = toggledHoverTexture;
            this.toggledDisabledTexture = toggledDisabledTexture;
            this.toggledOnClick = toggledOnClick;
            this.toggled = toggled;
        }

        public boolean getToggled(PaintSystem paintSystem) {
            return toggled.apply(paintSystem);
        }

        @Override
        public String getTexture(PaintSystem paintSystem) {
            return getToggled(paintSystem) ? toggledTexture : super.getTexture(paintSystem);
        }

        @Override
        public String getDisabledTexture(PaintSystem paintSystem) {
            return getToggled(paintSystem) ? toggledDisabledTexture : super.getDisabledTexture(paintSystem);
        }

        @Override
        public String getHoverTexture(PaintSystem paintSystem) {
            return getToggled(paintSystem) ? toggledHoverTexture : super.getHoverTexture(paintSystem);
        }

        @Override
        public Consumer<PaintSystem> getOnClick(PaintSystem paintSystem) {
            return getToggled(paintSystem) ? toggledOnClick :  super.getOnClick(paintSystem);
        }
    }

    public class Button {

        public BiFunction<PaintSystem, Float, Float> lx;
        public BiFunction<PaintSystem, Float, Float> ly;
        public BiFunction<PaintSystem, Float, Float> rx;
        public BiFunction<PaintSystem, Float, Float> ry;
        public float width;
        public float height;
        public Function<Float, Float> scale;
        public String texture;
        public String hoverTexture;
        public String disabledTexture;
        public Consumer<PaintSystem> onClick;
        public Component tooltip;
        public Consumer<PaintSystem> onTick;
        public Function<PaintSystem, Boolean> selected;
        public Function<PaintSystem, Boolean> disabled;
        public Function<PaintSystem, Boolean> visible;
        public float visibility = 0f;
        public float visibilityOld = 0f;
        public boolean clicked = false;
        public float clickedScale = 1;
        Button(float lx, float ly, float rx, float ry, float width, float height, Function<Float, Float> scale,
               String texture, String hoverTexture, String disabledTexture,
               Consumer<PaintSystem> onClick,
               Component tooltip,
               Consumer<PaintSystem> onTick,
               Function<PaintSystem, Boolean> selected,
               Function<PaintSystem, Boolean> disabled,
               Function<PaintSystem, Boolean> visible) {
            this.lx = (ps, partial) -> lx;
            this.ly = (ps, partial) -> ly;
            this.rx = (ps, partial) -> rx;
            this.ry = (ps, partial) -> ry;
            this.width = width;
            this.height = height;
            this.scale = scale;
            this.texture = texture;
            this.hoverTexture = hoverTexture;
            this.disabledTexture = disabledTexture;
            this.onClick = onClick;
            this.tooltip = tooltip;
            this.selected = selected;
            this.disabled = disabled;
            this.visible = visible;
            this.onTick = onTick;
        }
        Button(BiFunction<PaintSystem, Float, Float> lx, BiFunction<PaintSystem, Float, Float> ly, BiFunction<PaintSystem, Float, Float> rx, BiFunction<PaintSystem, Float, Float> ry,
               float width, float height, Function<Float, Float> scale,
               String texture, String hoverTexture, String disabledTexture,
               Consumer<PaintSystem> onClick,
               Component tooltip,
               Consumer<PaintSystem> onTick,
               Function<PaintSystem, Boolean> selected,
               Function<PaintSystem, Boolean> disabled,
               Function<PaintSystem, Boolean> visible) {
            this.lx = lx;
            this.ly = ly;
            this.rx = rx;
            this.ry = ry;
            this.width = width;
            this.height = height;
            this.scale = scale;
            this.texture = texture;
            this.hoverTexture = hoverTexture;
            this.disabledTexture = disabledTexture;
            this.onClick = onClick;
            this.tooltip = tooltip;
            this.selected = selected;
            this.disabled = disabled;
            this.visible = visible;
            this.onTick = onTick;
        }

        public float getX(PaintSystem paintSystem, PageDrawing.PageOn pageOn, float partial) {
            return pageOn.isOnLeftSide() ? lx.apply(paintSystem, partial) : rx.apply(paintSystem, partial);
        }

        public float getY(PaintSystem paintSystem, PageDrawing.PageOn pageOn, float partial) {
            return pageOn.isOnLeftSide() ? ly.apply(paintSystem, partial) : ry.apply(paintSystem, partial);
        }

        public Component getTooltip() {
            return tooltip;
        }

        public List<Component> getTooltipList() {
            return List.of(getTooltip());
        }

        public String getDisabledTexture(PaintSystem paintSystem) {
            return disabledTexture;
        }

        public String getTexture(PaintSystem paintSystem) {
            return texture;
        }

        public String getHoverTexture(PaintSystem paintSystem) {
            return hoverTexture;
        }

        public boolean shouldRender(PaintSystem paintSystem) {
            return this.visibility > 0;
        }

        public float getVisibility(float partial) {
            return Math.max(0, HexereiUtil.easeInOutCubic(Mth.lerp(partial, this.visibilityOld, this.visibility)));
        }

        public boolean isVisible(PaintSystem paintSystem) {
            return visible.apply(paintSystem);
        }

        public boolean getDisabled(PaintSystem paintSystem) {
            return disabled.apply(paintSystem);
        }

        public void onClick(PaintSystem paintSystem) {
            getOnClick(paintSystem).accept(paintSystem);
        }

        public Consumer<PaintSystem> getOnClick(PaintSystem paintSystem) {
            return onClick;
        }

        public float getScale(float val) {
            return scale.apply(val) * clickedScale;
        }

        public void tick(PaintSystem ps) {
            this.onTick.accept(ps);
            this.visibilityOld = visibility;
            if (isVisible(ps)) {
                visibility = HexereiUtil.moveTo(visibility, 1, 0.01f + Math.clamp(Math.abs(visibility - 1), 0, 1) * 0.15f);
            } else {
                visibility = HexereiUtil.moveTo(visibility, -1, 0.01f + Math.clamp(Math.abs(visibility - 1), 0, 1) * 0.25f);
            }
            if (clicked) {
                clickedScale = HexereiUtil.moveTo(clickedScale, 0.75f, 0.01f + Math.abs(clickedScale - 0.75f) * 0.5f);
                if (clickedScale == 0.75f)
                    clicked = false;
            } else {
                clickedScale = HexereiUtil.moveTo(clickedScale, 1, 0.01f + Math.abs(clickedScale - 0.75f) * 2f);
            }
        }
    }

    public class ValueSlider {
        public float lx, ly, rx, ry, width, height;
        private boolean horizontal;
        private float value = 0.5f; // Default value (0 to 1)
        private boolean dragging = false;
        private boolean hovering = false;
        private float hoveringScale = 1;
        private float hoveringScaleOld = 1;
        private Function<PaintSystem, Integer> color1;
        private Function<PaintSystem, Integer> color2;
        private Function<PaintSystem, Integer> sliderColor;
        private Function<PaintSystem, Boolean> visible;
        public float visibility = 0f;
        public float visibilityOld = 0f;
        private Function<PaintSystem, Component> tooltip;
        private boolean isSpecialHueSlider = false;

        ValueSlider(float lx, float ly, float rx, float ry, float width, float height, Function<PaintSystem, Integer> color1, Function<PaintSystem, Integer> color2, Function<PaintSystem, Integer> sliderColor, Function<PaintSystem, Boolean> visible, Function<PaintSystem, Component> tooltip, boolean horizontal) {
            this.visible = visible;
            this.color1 = color1;
            this.color2 = color2;
            this.tooltip = tooltip;
            this.sliderColor = sliderColor;
            this.lx = lx; this.ly = ly;
            this.rx = rx; this.ry = ry;
            this.width = width; this.height = height;
            this.horizontal = horizontal;
        }

        ValueSlider(float lx, float ly, float rx, float ry, float width, float height, Function<PaintSystem, Integer> color1, Function<PaintSystem, Integer> color2, Function<PaintSystem, Integer> sliderColor, Function<PaintSystem, Boolean> visible, Function<PaintSystem, Component> tooltip, boolean horizontal, boolean isSpecialHueSlider) {
            this(lx, ly, rx, ry, width, height, color1, color2, sliderColor, visible, tooltip, horizontal);
            this.isSpecialHueSlider = isSpecialHueSlider;
        }

        public float getX(PageDrawing.PageOn pageOn) {
            return pageOn.isOnLeftSide() ? lx : rx;
        }

        public float getY(PageDrawing.PageOn pageOn) {
            return pageOn.isOnLeftSide() ? ly : ry;
        }

        public float getVisibility(float partial) {
            return Math.max(0, HexereiUtil.easeInOutCubic(Mth.lerp(partial, this.visibilityOld, this.visibility)));
        }

        public void tick(PaintSystem ps) {

            this.visibilityOld = visibility;
            if (isVisible(ps)) {
                visibility = HexereiUtil.moveTo(visibility, 1, 0.01f + Math.clamp(Math.abs(visibility - 1), 0, 1) * 0.15f);
            } else {
                visibility = HexereiUtil.moveTo(visibility, -1, 0.01f + Math.clamp(Math.abs(visibility - 1), 0, 1) * 0.25f);
            }
            this.hoveringScaleOld = this.hoveringScale;
            if (hovering) {
                hoveringScale = HexereiUtil.moveTo(hoveringScale, 1, 0.01f + Math.abs(hoveringScale - 1) * 0.1f);
            } else {
                hoveringScale = HexereiUtil.moveTo(hoveringScale, 0, 0.01f + Math.abs(hoveringScale - 0) * 0.025f);
            }
            hovering = false;
        }

        public boolean isSpecialHueSlider() {
            return isSpecialHueSlider;
        }

        public Component getTooltip(PaintSystem ps) {
            return tooltip.apply(ps);
        }

        public boolean shouldRender(PaintSystem ps) {
            return this.visibility > 0;
        }

        public boolean isVisible(PaintSystem ps) {
            return visible.apply(ps);
        }

        public int getColor1(PaintSystem ps) {
            return color1.apply(ps);
        }

        public int getColor2(PaintSystem ps) {
            return color2.apply(ps);
        }

        public int getSliderColor(PaintSystem ps) {
            return sliderColor.apply(ps);
        }

        public void setHovering() {
            this.hovering = true;
        }

        public float getHoveringScale(float partial) {

            float val = HexereiUtil.easeInOutCubic(Mth.lerp(partial, hoveringScaleOld, hoveringScale));
            return val * 1.25f + 1;
        }

        public boolean isHorizontal() { return horizontal; }
        public boolean isDragging() { return dragging; }

        private boolean click(float cursorX, float cursorY, PageDrawing.PageOn pageOn) {
            float scale = 1;
            float w1 = this.width / 326 * 2.55f * scale / 0.062f;
            float h1 = this.height / 326 * 2.55f * scale / 0.062f;
            float x1 = this.getX(pageOn) + 0.025f - (pageOn.isOnLeftSide() ? 0 : 0.09f);
            float y1 = this.getY(pageOn) - 0.5f - h1 / 2;
            float u = (cursorX - x1) / w1;
            float v = (cursorY - y1) / h1;
            boolean clicked = (u >= 0 && u <= 1) && (v >= 0 && v <= 1);
            this.dragging = clicked;
            return clicked;
        }
        public void updateValue(float cursorX, float cursorY, PageDrawing.PageOn pageOn) {
            float scale = 1;
            float w1 = this.width / 326 * 2.55f * scale / 0.062f;
            float h1 = this.height / 326 * 2.55f * scale / 0.062f;
            float x1 = this.getX(pageOn) + 0.025f - (pageOn.isOnLeftSide() ? 0 : 0.09f);
            float y1 = this.getY(pageOn) - 0.5f;
            float u = (cursorX - x1) / w1;
            float v = (cursorY - y1 + h1 / 2) / h1;
            if (horizontal) {
                value = Math.max(0, Math.min(1, u));
            } else {
                value = Math.max(0, Math.min(1, 1 - v));
            }
            if (listener != null) listener.onValueChanged(value);
        }
        public interface SliderListener {
            void onValueChanged(float value);
        }
        private SliderListener listener;
        public void setSliderListener(SliderListener listener) { this.listener = listener; }
        public float getValue() { return value; }
        public void setValue(float value) { this.value = value; }
    }

    public class ValueSliders {
        private final ValueSlider hueSlider, saturationSlider, brightnessSlider, alphaSlider, hardnessSlider, brushSizeSlider, toleranceSlider;
        private final List<ValueSlider> sliders = new ArrayList<>();
        private final PaintSystem parent;
        public ValueSliders(PaintSystem parent) {
            this.parent = parent;
            float x = 1.75f, y = 7.75f, height = 0.33f, rightOffset = 0.8f;
            hueSlider = new ValueSlider(x, y, x + rightOffset, y, 8, 2.5f,
                    (ps) -> -1,
                    (ps) -> -1,
                    (ps) -> Color.HSBtoRGB(getHueSlider().getValue(), 1, 1),
                    (ps) -> ps.getCurrentTool().shouldShowColorSliders() && toolsVisible,
                    (ps) -> Component.translatable("Hue - %s", (int) (getHueSlider().getValue() * 360)).withStyle(ChatFormatting.GRAY),
                    true,
                    true);
            saturationSlider = new ValueSlider(x, y + height, x + rightOffset, y + height, 8, 2.5f,
                    (ps) -> Color.HSBtoRGB(ps.getValueSliders().getHueSlider().getValue(), 0, ps.getValueSliders().getBrightnessSlider().getValue()),
                    (ps) -> Color.HSBtoRGB(ps.getValueSliders().getHueSlider().getValue(), 1, ps.getValueSliders().getBrightnessSlider().getValue()),
                    (ps) -> (255 << 24) | (ps.getColor() & 0x00FFFFFF),
                    (ps) -> ps.getCurrentTool().shouldShowColorSliders() && toolsVisible,
                    (ps) -> Component.translatable("Saturation - %s%%", (int) (getSaturationSlider().getValue() * 100)).withStyle(ChatFormatting.GRAY),
                    true);
            brightnessSlider = new ValueSlider(x, y + height * 2, x + rightOffset, y + height * 2, 8, 2.5f,
                    (ps) -> Color.HSBtoRGB(ps.getValueSliders().getHueSlider().getValue(), ps.getValueSliders().getSaturationSlider().getValue(), 0),
                    (ps) -> Color.HSBtoRGB(ps.getValueSliders().getHueSlider().getValue(), ps.getValueSliders().getSaturationSlider().getValue(), 1),
                    (ps) -> (255 << 24) | (ps.getColor() & 0x00FFFFFF),
                    (ps) -> ps.getCurrentTool().shouldShowColorSliders() && toolsVisible,
                    (ps) -> Component.translatable("Brightness - %s%%", (int) (getBrightnessSlider().getValue() * 100)).withStyle(ChatFormatting.GRAY),
                    true);
            alphaSlider = new ValueSlider(x + 1.2f, y + height, x + 1.2f + rightOffset, y + height, 2.5f, 6.5f,
                    (ps) -> (ps.getColor() & 0x00FFFFFF),
                    (ps) -> (255 << 24) | (ps.getColor() & 0x00FFFFFF),
                    (ps) -> ps.getColor(),
                    (ps) -> ps.getCurrentTool().shouldShowColorSliders() && toolsVisible,
                    (ps) -> Component.translatable("Alpha - %s%%", (int) (getAlphaSlider().getValue() * 100)).withStyle(ChatFormatting.GRAY),
                    false);
            hardnessSlider = new ValueSlider(x + 1.75f, y + height * 1.5f, x + 1.75f + rightOffset, y + height * 1.5f, 8, 2.5f,
                    (ps) -> 0xFF1D544D,
                    (ps) -> 0xFF4BD8C8,
                    (ps) -> 0xFF38A094,
                    (ps) -> ps.getCurrentTool().shouldShowBrushSliders() && toolsVisible,
                    (ps) -> Component.translatable("Hardness - %s%%", (int) (getHardnessSlider().getValue() * 100)).withStyle(ChatFormatting.GRAY),
                    true);
            brushSizeSlider = new ValueSlider(x + 1.75f, y + height * 0.5f, x + 1.75f + rightOffset, y + height * 0.5f, 8, 2.5f,
                    (ps) -> 0xFF1D544D,
                    (ps) -> 0xFF4BD8C8,
                    (ps) -> 0xFF38A094,
                    (ps) -> ps.getCurrentTool().shouldShowBrushSliders() && toolsVisible,
                    (ps) -> Component.translatable("Brush Size - %s", (int) (getBrushSizeSlider().getValue() * ps.getActiveLayer().pixels.getWidth() / 4f + 1)).withStyle(ChatFormatting.GRAY),
                    true);
            toleranceSlider = new ValueSlider(x + 1.75f, y + height * 1.9f, x + 1.75f + rightOffset, y + height * 1.9f, 8, 2.5f,
                    (ps) -> 0xFF1D544D,
                    (ps) -> 0xFF4BD8C8,
                    (ps) -> 0xFF38A094,
                    (ps) -> ps.getCurrentTool().shouldShowToleranceSliders() && toolsVisible,
                    (ps) -> Component.translatable("Tolerance - %s%%", (int) (getToleranceSlider().getValue() * 100)).withStyle(ChatFormatting.GRAY),
                    true);

            hueSlider.setSliderListener(value -> updateColor());
            saturationSlider.setSliderListener(value -> updateColor());
            brightnessSlider.setSliderListener(value -> updateColor());
            alphaSlider.setSliderListener(value -> updateColor());
            hardnessSlider.setSliderListener(value -> updateHardness());
            brushSizeSlider.setSliderListener(value -> updateBrushSize());
            toleranceSlider.setSliderListener(value -> updateTolerance());

            sliders.add(hueSlider);
            sliders.add(saturationSlider);
            sliders.add(brightnessSlider);
            sliders.add(alphaSlider);
            sliders.add(hardnessSlider);
            sliders.add(brushSizeSlider);
            sliders.add(toleranceSlider);
        }

        public List<ValueSlider> getSliders() {
            return sliders;
        }

        private void updateColor() {
            float h = hueSlider.getValue(), s = saturationSlider.getValue(), v = brightnessSlider.getValue(), a = alphaSlider.getValue();

            parent.setColor(((int)(a * 255) << 24) | (Color.HSBtoRGB(h, s, v) & 0x00FFFFFF));
            updateColorSliders(parent.getColor());
        }
        private void updateHardness() {
            parent.getBrush().hardness = hardnessSlider.getValue();
        }
        private void updateHardnessSlider(float hardness) { hardnessSlider.setValue(hardness); }
        private void updateBrushSize() {
            parent.getBrush().size = (int)(brushSizeSlider.getValue() * parent.width / 4f);
        }
        private void updateBrushSizeSlider(int size) { brushSizeSlider.setValue(size / (parent.width / 4f)); }
        private void updateTolerance() {
            parent.getBrush().tolerance = toleranceSlider.getValue();
        }
        private void updateToleranceSlider(float tolerance) { toleranceSlider.setValue(tolerance); }
        public void updateColorSliders(int col) {
            if (hueSlider.dragging || saturationSlider.dragging || brightnessSlider.dragging || alphaSlider.dragging)
                return;
            float[] colors = HexereiUtil.rgbaIntToFloatArray(col);
            float[] hsv = new float[3];// = HexereiUtil.rgbToHsl(colors[0], colors[1], colors[2]);
            Color.RGBtoHSB( (int)(colors[0] * 255),  (int)(colors[1] * 255), (int)(colors[2] * 255), hsv);
            if (hsv[1] != 0)
                hueSlider.setValue(hsv[0]);
            if (hsv[2] != 0)
                saturationSlider.setValue(hsv[1]);
            brightnessSlider.setValue(hsv[2]);
            alphaSlider.setValue(colors[3]);
        }
        public ValueSlider getHueSlider() { return hueSlider; }
        public ValueSlider getSaturationSlider() { return saturationSlider; }
        public ValueSlider getBrightnessSlider() { return brightnessSlider; }
        public ValueSlider getAlphaSlider() { return alphaSlider; }
        public ValueSlider getHardnessSlider() { return hardnessSlider; }
        public ValueSlider getBrushSizeSlider() { return brushSizeSlider; }
        public ValueSlider getToleranceSlider() { return toleranceSlider; }
        public void release() {
            hueSlider.dragging = false;
            saturationSlider.dragging = false;
            brightnessSlider.dragging = false;
            alphaSlider.dragging = false;
            hardnessSlider.dragging = false;
            brushSizeSlider.dragging = false;
            toleranceSlider.dragging = false;
        }
        public boolean click(float cursorX, float cursorY, PageDrawing.PageOn pageOn) {
            if (parent.getCurrentTool().shouldShowColorSliders()){
                if (hueSlider.click(cursorX, cursorY, pageOn)) return true;
                if (saturationSlider.click(cursorX, cursorY, pageOn)) return true;
                if (brightnessSlider.click(cursorX, cursorY, pageOn)) return true;
                if (alphaSlider.click(cursorX, cursorY, pageOn)) return true;
            }
            if (parent.getCurrentTool().shouldShowBrushSliders()){
                if (hardnessSlider.click(cursorX, cursorY, pageOn)) return true;
                if (brushSizeSlider.click(cursorX, cursorY, pageOn)) return true;
            }
            if (parent.getCurrentTool().shouldShowToleranceSliders()){
                if (toleranceSlider.click(cursorX, cursorY, pageOn)) return true;
            }
            return false;
        }
    }

    // Layer management
    public static class Layer {
        public BufferedImage pixels;
        public float opacity = 1.0f;
        public BlendMode blendMode = BlendMode.NORMAL;
        public boolean dirty = true;
        public String name = "";
    }


    class Selection {

        private Rectangle bounds;
        private BufferedImage mask;
        public Pos2i anchor;
        public boolean adjustingSelection;
        private PaintSystem parent;
        List<Point> edgePoints = new ArrayList<>();
        public int cursorX = 0, cursorY = 0, cursorXOld = 0, cursorYOld = 0;
        public static Type type = Type.REPLACE;

        public static boolean global = false; // for magic wand use
        public enum Type {
            REPLACE, ADD, REMOVE
        }

        public Selection(PaintSystem parent) {
            this.parent = parent;
            this.bounds = new Rectangle(0, 0, 0, 0);
            this.mask = null;
            this.anchor = new Pos2i(0, 0);
            this.adjustingSelection = false;
        }

        public void setType(Type type) {
            Selection.type = type;
        }

        public Type getType() {
            return type;
        }

        public void setGlobal(boolean global) {
            Selection.global = global;
        }

        public boolean getGlobal() {
            return global;
        }

        public BufferedImage getMask() {
            return mask;
        }

        public Rectangle getBounds() {
            return bounds;
        }

        public boolean isEmpty() {
            if (adjustingSelection) {
                switch (selection.getType()) {
                    case REPLACE -> {
                        return mask == null || (bounds.width == 1 && bounds.height == 1);
                    }
                    case ADD, REMOVE -> {
                        return !(!(mask == null || (bounds.width == 1 && bounds.height == 1)) || !(initialSelectionMask == null || (initialSelectionBounds.width == 1 && initialSelectionBounds.height == 1)));
                    }
                }
            }
            return mask == null || (bounds.width == 1 && bounds.height == 1);
        }

        public void setBoundsByOffset(int x, int y) {
            bounds.setBounds(x, y, bounds.width, bounds.height);
        }

        public void updateBoundsAfterMove(Pos2i offset) {
            bounds.setLocation(offset.x, offset.y);
        }

        public void initializeBounds(int x, int y) {
            bounds = new Rectangle(x, y, 0, 0);
        }

        public Rectangle getUpdateRectangleBounds(Pos2i start, Pos2i end) {
            int x1 = Math.min(start.x, end.x);
            int y1 = Math.min(start.y, end.y);
            int width = Math.abs(end.x - start.x) + 1;
            int height = Math.abs(end.y - start.y) + 1;

            return new Rectangle(x1, y1, width, height);
        }

        public void clearMask() {
            this.mask = null;
            this.edgePoints.clear();
        }

        public void createRectangleMask() {
            BufferedImage mask = new BufferedImage(bounds.width, bounds.height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = mask.createGraphics();
            g2d.setColor(new Color(0, 0, 0, 255)); // Solid color for mask
            g2d.fillRect(0, 0, bounds.width, bounds.height); // Draw the selection rectangle
            g2d.dispose();
            setSelectionMask(mask);
        }

        public void clear() {
            clearMask();
            this.bounds.setBounds(0, 0, 0, 0);
        }

        public void deselect() {
            initialSelectionBounds = new Rectangle(selection.bounds);
            initialSelectionMask = (selection.mask != null ? deepCopy(selection.mask) : null);
            clearMask();
            this.bounds.setBounds(0, 0, 0, 0);
            SelectionAction action = new SelectionAction(
                    initialSelectionBounds, initialSelectionMask,
                    selection.bounds, selection.mask
            );
            actionManager.beginAction(action);
            actionManager.commitAction();
            dirty = true;
        }

        public void updateEdgePoints() {

            Rectangle bounds = this.bounds;
            BufferedImage mask = this.mask;
            if (selection.getType() == Type.ADD && selection.adjustingSelection) {
                if (initialSelectionMask == null && mask == null) return;

                Rectangle combinedBounds = selection.isEmpty() ? initialSelectionBounds : (Rectangle) selection.bounds.createUnion(initialSelectionBounds);
                BufferedImage combinedMask = new BufferedImage(combinedBounds.width, combinedBounds.height, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = combinedMask.createGraphics();

                if (mask != null)
                    g.drawImage(selection.mask,
                            selection.bounds.x - combinedBounds.x, selection.bounds.y - combinedBounds.y, selection.mask.getWidth(), selection.mask.getHeight(),
                            null);
                if (initialSelectionMask != null)
                    g.drawImage(initialSelectionMask,
                            initialSelectionBounds.x - combinedBounds.x, initialSelectionBounds.y - combinedBounds.y, initialSelectionMask.getWidth(), initialSelectionMask.getHeight(),
                            null);
                g.dispose();

                bounds = combinedBounds;
                mask = combinedMask;
//                selection.bounds = combinedBounds;
//                selection.setSelectionMask(combinedMask);

            } else if (selection.getType() == Type.REMOVE && selection.adjustingSelection) {
                if (initialSelectionMask == null && mask == null) return;

                Rectangle combinedBounds = selection.isEmpty() ? initialSelectionBounds : (Rectangle) selection.bounds.createUnion(initialSelectionBounds);
                BufferedImage combinedMask = new BufferedImage(combinedBounds.width, combinedBounds.height, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = combinedMask.createGraphics();
                if (initialSelectionMask != null)
                    g.drawImage(initialSelectionMask,
                            initialSelectionBounds.x - combinedBounds.x, initialSelectionBounds.y - combinedBounds.y, initialSelectionMask.getWidth(), initialSelectionMask.getHeight(),
                            null);

                for (int y = 0; y < bounds.height; y++) {
                    for (int x = 0; x < bounds.width; x++) {
                        int globalX = bounds.x + x;
                        int globalY = bounds.y + y;

                        // Ensure coordinates are within layer and mask bounds
                        if (globalX >= 0 && globalX < width &&
                                globalY >= 0 && globalY < height &&
                                x >= 0 && x < mask.getWidth() &&
                                y >= 0 && y < mask.getHeight()) {

                            // Check if the mask has a non-transparent pixel
                            int maskAlpha = (mask.getRGB(x, y) >> 24) & 0xFF;
                            if (maskAlpha > 0) {
                                combinedMask.setRGB(globalX - combinedBounds.x, globalY - combinedBounds.y, 0x00000000); // Set pixel to fully transparent
                            }
                        }
                    }
                }

                g.dispose();

                bounds = combinedBounds;
                mask = combinedMask;
//                selection.bounds = combinedBounds;
//                selection.setSelectionMask(combinedMask);


            }
            if (mask == null)
                return;


            this.edgePoints = orderEdgePoints(findEdgePoints(mask).stream().toList());
        }

        public void setSelectionMask(BufferedImage mask) {
            this.mask = mask;
            if (mask != null) {
                cropMaskToSelection();
            } else {
                clear();
                updateEdgePoints();
            }
        }

        public BufferedImage generateMagicWandMask(Layer layer, int x, int y, float tolerance, boolean global) {
            if (layer == null || layer.pixels == null) return null;
            int width = layer.pixels.getWidth();
            int height = layer.pixels.getHeight();
            if (x < 0 || x >= width || y < 0 || y >= height) return null;

            BufferedImage mask = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            int targetColor = layer.pixels.getRGB(x, y);
            float[] targetHSV = rgbToHsv(targetColor);

            if (global) {
                for (int py = 0; py < height; py++) {
                    for (int px = 0; px < width; px++) {
                        int currentColor = layer.pixels.getRGB(px, py);
                        int alpha = ((currentColor >> 24) & 0xFF);
                        int targetAlpha = ((targetColor >> 24) & 0xFF);
                        float difference = calculateColorDifference(targetHSV,
                                rgbToHsv(currentColor));
                        if ((alpha == 0 && targetAlpha == 0) || difference <= tolerance) {
                            mask.setRGB(px, py, 0xFF000000);
                        }
                    }
                }
            } else {
                Stack<Point> stack = new Stack<>();
                boolean[][] visited = new boolean[width][height];
                stack.push(new Point(x, y));

                while (!stack.isEmpty()) {
                    Point p = stack.pop();
                    int px = p.x, py = p.y;
                    if (px < 0 || px >= width || py < 0 || py >= height || visited[px][py]) continue;

                    visited[px][py] = true;
                    int currentColor = layer.pixels.getRGB(px, py);
                    int alpha = ((currentColor >> 24) & 0xFF);
                    int targetAlpha = ((targetColor >> 24) & 0xFF);
                    float difference = calculateColorDifference(targetHSV,
                            rgbToHsv(currentColor));

                    if ((alpha == 0 && targetAlpha == 0) || difference <= tolerance) {
                        mask.setRGB(px, py, 0xFF000000);
                        if (px > 0) stack.push(new Point(px - 1, py));
                        if (px < width - 1) stack.push(new Point(px + 1, py));
                        if (py > 0) stack.push(new Point(px, py - 1));
                        if (py < height - 1) stack.push(new Point(px, py + 1));
                    }
                }
            }
            return mask;
        }

        public BufferedImage extractSelectedArea(Layer layer) {
            if (isEmpty() || layer == null || layer.pixels == null || mask == null) return null;

            BufferedImage selectedArea = new BufferedImage(bounds.width, bounds.height, BufferedImage.TYPE_INT_ARGB);
            for (int y = 0; y < bounds.height; y++) {
                for (int x = 0; x < bounds.width; x++) {
                    int maskX = x;
                    int maskY = y;
                    // Bounds should align with mask dimensions, so no need to check outside the mask area
                    int maskAlpha = (mask.getRGB(maskX, maskY) >> 24) & 0xFF;
                    if (maskAlpha > 0) {
                        int globalX = bounds.x + x;
                        int globalY = bounds.y + y;
                        if (globalX >= 0 && globalX < layer.pixels.getWidth() && globalY >= 0 && globalY < layer.pixels.getHeight()) {
                            int pixel = layer.pixels.getRGB(globalX, globalY);
                            selectedArea.setRGB(x, y, pixel);
                        }
                    }
                }
            }
            return selectedArea;
        }


        public void pasteToLayer(Layer layer, BufferedImage image) {
            if (layer == null || image == null) return;
            Graphics2D g2d = layer.pixels.createGraphics();
            g2d.drawImage(image, bounds.x, bounds.y, null);
            g2d.dispose();
        }

        public void deleteFromLayer(Layer layer) {
            if (isEmpty() || layer == null || layer.pixels == null) return;

            for (int y = 0; y < bounds.height; y++) {
                for (int x = 0; x < bounds.width; x++) {
                    int globalX = bounds.x + x;
                    int globalY = bounds.y + y;

                    // Ensure coordinates are within layer and mask bounds
                    if (globalX >= 0 && globalX < layer.pixels.getWidth() &&
                            globalY >= 0 && globalY < layer.pixels.getHeight() &&
                            x >= 0 && x < mask.getWidth() &&
                            y >= 0 && y < mask.getHeight()) {

                        // Check if the mask has a non-transparent pixel
                        int maskAlpha = (mask.getRGB(x, y) >> 24) & 0xFF;
                        if (maskAlpha > 0) {
                            layer.pixels.setRGB(globalX, globalY, 0x00000000); // Set pixel to fully transparent
                        }
                    }
                }
            }
        }


        // Crop the mask and update bounds to fit the selection
        private void cropMaskToSelection() {
            if (mask == null) {
                clear();
                dirty = true;
                return;
            }
            int minX = mask.getWidth(), minY = mask.getHeight();
            int maxX = 0, maxY = 0;

            boolean empty = true;
            for (int y = 0; y < mask.getHeight(); y++) {
                for (int x = 0; x < mask.getWidth(); x++) {
                    if (bounds.x + x > width) continue;
                    if (bounds.y + y > height) continue;
                    int alpha = (mask.getRGB(x, y) >> 24) & 0xFF;
                    if (alpha > 0) { // Selected pixel
                        empty = false;
                        if (x < minX) minX = x;
                        if (y < minY) minY = y;
                        if (x > maxX) maxX = x;
                        if (y > maxY) maxY = y;
                    }
                }
            }

            if (minX <= maxX && minY <= maxY && !empty) {
                // Crop mask and update bounds
                int width = maxX - minX + 1;
                int height = maxY - minY + 1;
                BufferedImage croppedMask = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        int pixel = mask.getRGB(minX + x, minY + y);
                        croppedMask.setRGB(x, y, pixel);
                    }
                }

                this.mask = croppedMask;
                this.bounds = new Rectangle(bounds.x + minX, bounds.y + minY, width, height);
            } else {
                clear();
                dirty = true;
            }
            updateEdgePoints();
        }

        public void render(Graphics2D g2d) {

            Rectangle bounds = this.bounds;
            BufferedImage mask = this.mask;
            if (selection.getType() == Type.REPLACE || !selection.adjustingSelection) {
                if (mask == null) return;
            }
            else if (selection.getType() == Type.ADD) {
                if (initialSelectionMask == null && mask == null) return;

                Rectangle combinedBounds = selection.isEmpty() ? initialSelectionBounds : (Rectangle) selection.bounds.createUnion(initialSelectionBounds);
                BufferedImage combinedMask = new BufferedImage(combinedBounds.width, combinedBounds.height, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = combinedMask.createGraphics();

                if (mask != null)
                   g.drawImage(selection.mask,
                        selection.bounds.x - combinedBounds.x, selection.bounds.y - combinedBounds.y, selection.mask.getWidth(), selection.mask.getHeight(),
                        null);
                if (initialSelectionMask != null)
                    g.drawImage(initialSelectionMask,
                        initialSelectionBounds.x - combinedBounds.x, initialSelectionBounds.y - combinedBounds.y, initialSelectionMask.getWidth(), initialSelectionMask.getHeight(),
                        null);
                g.dispose();

                bounds = combinedBounds;
                mask = combinedMask;
//                selection.bounds = combinedBounds;
//                selection.setSelectionMask(combinedMask);

            } else if (selection.getType() == Type.REMOVE) {
                if (initialSelectionMask == null && mask == null) return;

                Rectangle combinedBounds = selection.isEmpty() ? initialSelectionBounds : (Rectangle) selection.bounds.createUnion(initialSelectionBounds);
                BufferedImage combinedMask = new BufferedImage(combinedBounds.width, combinedBounds.height, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = combinedMask.createGraphics();
                if (initialSelectionMask != null)
                    g.drawImage(initialSelectionMask,
                            initialSelectionBounds.x - combinedBounds.x, initialSelectionBounds.y - combinedBounds.y, initialSelectionMask.getWidth(), initialSelectionMask.getHeight(),
                            null);

                for (int y = 0; y < bounds.height; y++) {
                    for (int x = 0; x < bounds.width; x++) {
                        int globalX = bounds.x + x;
                        int globalY = bounds.y + y;

                        // Ensure coordinates are within layer and mask bounds
                        if (globalX >= 0 && globalX < width &&
                                globalY >= 0 && globalY < height &&
                                x >= 0 && x < mask.getWidth() &&
                                y >= 0 && y < mask.getHeight()) {

                            // Check if the mask has a non-transparent pixel
                            int maskAlpha = (mask.getRGB(x, y) >> 24) & 0xFF;
                            if (maskAlpha > 0) {
//                                combinedMask.setRGB(globalX - combinedBounds.x, globalY - combinedBounds.y, 0x1059FFEE); // Set pixel to fully transparent
                                Color tealTint = new Color(24 << 24 | 0xEF0300, true);
                                g2d.setColor(tealTint);
                                g2d.fillRect(globalX, globalY, 1, 1);
                            }
                        }
                    }
                }


                g.dispose();

                bounds = combinedBounds;
                mask = combinedMask;
//                selection.bounds = combinedBounds;
//                selection.setSelectionMask(combinedMask);
            }

//            g2d.setComposite(AlphaComposite.);


            BufferedImage img = deepCopy(mask);
            if (getCurrentTool().shouldSelectionShowMask() && movingSelection == null){
                Color tealTint = new Color(48 << 24 | 0x59FFEE, true);
                // Draw the mask overlay as a teal tint
                for (int y = 0; y < img.getHeight(); y++) {
                    for (int x = 0; x < img.getWidth(); x++) {
                        int alpha = (img.getRGB(x, y) >> 24) & 0xFF;
                        if (alpha > 0) {
                            img.setRGB(x, y, tealTint.getRGB());
                        }
                    }
                }
                g2d.drawImage(img, bounds.x, bounds.y, null);


            }


            // Render a dashed stroke around the selection
            renderDashedOutline(g2d, bounds);
        }

        private void renderDashedOutline(Graphics2D g2d, Rectangle bounds) {

            int dotLengthMAX = 5;
            int skipLengthMAX = 1;
//                    g2d.setColor(new Color(128 << 24 | Color.DARK_GRAY.getRGB(), true));

            float clientTicks = ClientEvents.getClientTicks();
            // Custom dashed outline rendering
            for (int i = 0; i < edgePoints.size(); i++) {
                if ((i + (clientTicks * 0.5f)) % (dotLengthMAX + skipLengthMAX) > dotLengthMAX - 1)
                    continue;
                Point p = edgePoints.get(i);

                float hue = (i / (float) edgePoints.size() + (clientTicks * 0.01f)) % 1.0f;
                int col = ((movingSelection != null || getCurrentTool().shouldShowColorSliders()) ? 48 : 128) << 24 | (Color.getHSBColor(hue, 1.0f, 1.0f).getRGB() & 0x00FFFFFF);
                Color color = new Color(col, true);
                g2d.setColor(color);
                int renderX = bounds.x + p.x;
                int renderY = bounds.y + p.y;
                g2d.fillRect(renderX, renderY, 1, 1);


            }
        }

        private List<Point> orderEdgePoints(List<Point> edgePoints) {
            if (edgePoints.isEmpty()) return Collections.emptyList();

            List<Point> orderedPoints = new ArrayList<>();
            Set<Point> visited = new HashSet<>();

            Point currentPoint = edgePoints.getFirst();
            orderedPoints.add(currentPoint);
            visited.add(currentPoint);

            while (orderedPoints.size() < edgePoints.size()) {
                List<Point> unvisited = edgePoints.stream()
                        .filter(p -> !visited.contains(p)).toList();

                if (unvisited.isEmpty()) break;

                // Find minimal distance
                double minDist = Double.MAX_VALUE;
                for (Point p : unvisited) {
                    double d = currentPoint.distance(p);
                    if (d < minDist) {
                        minDist = d;
                    }
                }

                Point finalCurrentPoint1 = currentPoint;
                double finalMinDist = minDist;
                List<Point> minDistPoints = unvisited.stream()
                        .filter(p -> finalCurrentPoint1.distance(p) == finalMinDist).toList();

                String prevDirection;
                if (orderedPoints.size() >= 2) {
                    Point prevPrev = orderedPoints.get(orderedPoints.size() - 2);
                    Point prev = orderedPoints.getLast();
                    int dx = prev.x - prevPrev.x;
                    int dy = prev.y - prevPrev.y;
                    prevDirection = computeDirection(dx, dy);
                } else {
                    prevDirection = null;
                }

                List<Point> sameDirCandidates = new ArrayList<>();
                if (prevDirection != null) {
                    Point finalCurrentPoint = currentPoint;
                    sameDirCandidates = minDistPoints.stream()
                            .filter(p -> {
                                int pDx = p.x - finalCurrentPoint.x;
                                int pDy = p.y - finalCurrentPoint.y;
                                String dir = computeDirection(pDx, pDy);
                                return dir != null && dir.equals(prevDirection);
                            }).toList();
                }

                List<Point> candidatesToConsider = new ArrayList<>(sameDirCandidates.isEmpty() ? minDistPoints : sameDirCandidates);

                // Sort candidates based on direction priority
                candidatesToConsider.sort(getDirectionComparator(currentPoint));

                if (!candidatesToConsider.isEmpty()) {
                    Point nearestPoint = candidatesToConsider.getFirst();
                    orderedPoints.add(nearestPoint);
                    visited.add(nearestPoint);
                    currentPoint = nearestPoint;
                } else {
                    break;
                }
            }

            return orderedPoints;
        }

        private String computeDirection(int dx, int dy) {
            if (dx > 0 && dy == 0) {
                return "E";
            } else if (dx < 0 && dy == 0) {
                return "W";
            } else if (dy > 0 && dx == 0) {
                return "S";
            } else if (dy < 0 && dx == 0) {
                return "N";
            } else {
                return null;
            }
        }

        private Comparator<Point> getDirectionComparator(Point currentPoint) {
            return (a, b) -> {
                int aDir = getDirectionPriority(currentPoint, a);
                int bDir = getDirectionPriority(currentPoint, b);

                if (aDir != bDir) {
                    return Integer.compare(aDir, bDir);
                } else {
                    // Same direction, compare coordinates
                    if (aDir == 1 || aDir == 2) { // North or South
                        int cmpX = Integer.compare(a.x, b.x);
                        if (cmpX != 0) {
                            return cmpX;
                        }
                        if (aDir == 1) { // North: prioritize lower y
                            return Integer.compare(a.y, b.y);
                        } else { // South: prioritize higher y
                            return Integer.compare(b.y, a.y);
                        }
                    } else { // East or West
                        int cmpY = Integer.compare(a.y, b.y);
                        if (cmpY != 0) {
                            return cmpY;
                        }
                        if (aDir == 3) { // East: prioritize higher x
                            return Integer.compare(a.x, b.x);
                        } else { // West: prioritize lower x
                            return Integer.compare(b.x, a.x);
                        }
                    }
                }
            };
        }

        private int getDirectionPriority(Point current, Point p) {
            int dx = p.x - current.x;
            int dy = p.y - current.y;
            if (dy < 0) return 1; // North
            else if (dy > 0) return 2; // South
            else if (dx > 0) return 3; // East
            else if (dx < 0) return 4; // West
            else return 5; // same point (should not occur)
        }


        private Set<Point> findEdgePoints(BufferedImage mask) {
            if (mask == null)
                return Set.of();

            Set<Point> edgePoints = new HashSet<>();

            int width = mask.getWidth();
            int height = mask.getHeight();

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int alpha = (mask.getRGB(x, y) >> 24) & 0xFF;
                    if (alpha > 0 && isEdgePixel(mask, x, y, width, height)) {
                        edgePoints.add(new Point(x, y));
                    }
                }
            }

            return edgePoints;
        }

        private boolean isEdgePixel(BufferedImage mask, int x, int y, int width, int height) {
            if ((mask.getRGB(x, y) >> 24 & 0xFF) == 0) return false;

            // If the pixel is at the boundary of the mask, it's automatically an edge
            if (x == 0 || y == 0 || x == width - 1 || y == height - 1) return true;

            // Check neighboring pixels
            int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
            for (int[] d : directions) {
                int nx = x + d[0];
                int ny = y + d[1];
                if (nx >= 0 && ny >= 0 && nx < width && ny < height) {
                    int neighborAlpha = (mask.getRGB(nx, ny) >> 24) & 0xFF;
                    if (neighborAlpha == 0) return true;
                }
            }

            return false;
        }

    }


    public float getColorsVisibility(float partial) {
        return Math.max(0, HexereiUtil.easeInOutCubic(Mth.lerp(partial, this.colorsVisibilityOld, this.colorsVisibility)));
    }

    public static class Colors {

        public List<ColorSelection> colors = new ArrayList<>();

        private final ColorSelectionPosData colorSelectionPosData1 = new ColorSelectionPosData(new Vec3(0.9f, 7.75f + 0.33f - 0.11f, 0), 4f, 4f);
        private final ColorSelectionPosData colorSelectionPosData2 = new ColorSelectionPosData(colorSelectionPosData1.pos.add(0.25, 0.15, -0.00008f), colorSelectionPosData1.width - (0.75f), colorSelectionPosData1.height - (0.75f));
        private final ColorSelectionPosData colorSelectionPosData3 = new ColorSelectionPosData(colorSelectionPosData2.pos.add(0.25, 0.15, -0.00008f), colorSelectionPosData2.width - (0.75f), colorSelectionPosData2.height - (0.75f));

        float offset = 0.8f;

        public Colors(List<ColorSelection> colors) {
            this.colors.add(new ColorSelection(0xFF000000, colorSelectionPosData1.copy()));
            this.colors.add(new ColorSelection(0xFFFFFFFF, colorSelectionPosData2.copy()));
            this.colors.add(new ColorSelection(0xFF808080, colorSelectionPosData3.copy()));
            this.colors.addAll(colors);
        }

        public void tick() {
            for(ColorSelection colorSelection : colors) {
                colorSelection.tick();
            }
        }

        public int getColor() {
            return this.colors.getFirst().getColor();
        }

        public void setColor(int col) {
            this.colors.getFirst().setColor(col);
        }

        public void cycleColor(PaintSystem paintSystem) {
            ColorSelection first = this.colors.removeFirst();
            this.colors.addLast(first);
            //update color sliders
            paintSystem.getValueSliders().updateColorSliders(getColor());
            updateColorSelectionTargetPos();
        }

        public void cycleColorBack(PaintSystem paintSystem) {
            ColorSelection last = this.colors.removeLast();
            this.colors.addFirst(last);
            //update color sliders
            paintSystem.getValueSliders().updateColorSliders(getColor());
            updateColorSelectionTargetPos();
        }

        public void updateColorSelectionTargetPos() {
            colors.get(0).target = colorSelectionPosData1;
            colors.get(1).target = colorSelectionPosData2;
            colors.get(2).target = colorSelectionPosData3;
        }

        public static class ColorSelection {

            public ColorSelectionPosData target;
            public ColorSelectionPosData colorPosData;
            public ColorSelectionPosData colorPosDataOld;
            public int color;

            public ColorSelection(int color, ColorSelectionPosData colorPosData) {
                this.color = color;
                this.colorPosData = colorPosData;
                this.colorPosDataOld = colorPosData;
                this.target = colorPosData;
            }

            public void tick() {
                colorPosDataOld = colorPosData.copy();
                float x = HexereiUtil.moveTo((float) colorPosData.pos.x, (float) target.pos.x, 0.001f + 0.175f * Mth.abs((float) (target.pos.x - colorPosData.pos.x)));
                float y = HexereiUtil.moveTo((float) colorPosData.pos.y, (float) target.pos.y, 0.001f + 0.175f * Mth.abs((float) (target.pos.y - colorPosData.pos.y)));
                float z = HexereiUtil.moveTo((float) colorPosData.pos.z, (float) target.pos.z, 0.001f);
                float w = HexereiUtil.moveTo(colorPosData.width, target.width, 0.25f);
                float h = HexereiUtil.moveTo(colorPosData.height, target.height, 0.25f);
                colorPosData = new ColorSelectionPosData(new Vec3(x, y, z), w, h);
            }

            public int getColor() {
                return color;
            }

            public void setColor(int color) {
                this.color = color;
            }
        }

        public static class ColorSelectionPosData {
            public float width, height;
            public Vec3 pos;

            public ColorSelectionPosData(Vec3 pos, float width, float height) {
                this.pos = pos;
                this.width = width;
                this.height = height;
            }

            public ColorSelectionPosData copy() {
                return new ColorSelectionPosData(pos, width, height);
            }
        }
    }

    // Brush management
    public static class Brush {
        public enum Type {DRAW, ERASE}
        public PaintSystem parent;
        public Type type = Type.DRAW;
        public static int size = 5;
        public static float hardness = 1f;
        public static float tolerance = 0f;
        public float cursorX = 0, cursorY = 0, cursorXOld = 0, cursorYOld = 0;
        public boolean drawing = false;
        public static boolean global = false; // for fill use
        public Brush(PaintSystem parent) { this.parent = parent; }
        public void apply(float x, float y) {
            applyBrushMask(parent.strokeMask, x, y);
        }

        private BufferedImage generateBrushMask() {
            return generateBrushMask(false);
        }

        private BufferedImage generateBrushMask(boolean ignoreColorAlpha) {
            int diameter = size + 1;
            BufferedImage brushImage = new BufferedImage(diameter, diameter, BufferedImage.TYPE_INT_ARGB);

            int color = parent.getColor();
            float radius = diameter / 2.0f;
            float centerX = (diameter - 1) / 2.0f;
            float centerY = centerX;
            float innerRadius = hardness * radius;

            for (int x = 0; x < diameter; x++) {
                for (int y = 0; y < diameter; y++) {
                    // Calculate distance from center (sub-pixel accurate)
                    float dx = x - centerX;
                    float dy = y - centerY;
                    float distance = (float) Math.sqrt(dx * dx + dy * dy);

                    int alpha = (int)(calculateAlpha(distance, radius, innerRadius) / 255f * (ignoreColorAlpha ? 255 : (color >> 24 & 255)));

                    brushImage.setRGB(x, y, (alpha << 24) | color & 0x00FFFFFF); // Black with calculated alpha
                }
            }

            return brushImage;
        }

        private int calculateAlpha(float distance, float radius, float innerRadius) {
            if (distance <= innerRadius) {
                // Full opacity inside inner radius
                return 255;
            } else if (distance <= radius) {
                // Linear falloff between inner radius and edge
                float t = (distance - innerRadius) / (radius - innerRadius);
                return (int) (255 * (1 - t));
            }
            // Transparent outside brush radius
            return 0;
        }

        private void applyBrushMask(BufferedImage strokeMask, float x, float y) {
            int diameter = size + 1;
            BufferedImage brushImage = generateBrushMask();

            int drawX = (int) (x - diameter / 2.0f + 0.5f);
            int drawY = (int) (y - diameter / 2.0f + 0.5f);

            // Iterate through brush pixels
            for (int bx = 0; bx < brushImage.getWidth(); bx++) {
                for (int by = 0; by < brushImage.getHeight(); by++) {
                    int sx = drawX + bx;
                    int sy = drawY + by;

                    if (sx < 0 || sx >= strokeMask.getWidth() ||
                            sy < 0 || sy >= strokeMask.getHeight()) continue;
                    //fix the bounds checking
                    if (parent.selection.mask != null) {
                        int posX = sx - parent.selection.bounds.x;
                        int posY = sy - parent.selection.bounds.y;
                        if (posX >= parent.selection.mask.getWidth() || posY >= parent.selection.mask.getHeight() || posX < 0 || posY < 0) {
                            continue;
                        } else {
                            if (((parent.selection.mask.getRGB(posX, posY) >> 24) & 0xFF) == 0)
                                continue;
                        }
                    }

                    int brushPixel = brushImage.getRGB(bx, by);
                    int brushAlpha = (brushPixel >>> 24);

                    int strokePixel = strokeMask.getRGB(sx, sy);
                    int strokeAlpha = (strokePixel >>> 24);

                    if (brushAlpha > strokeAlpha) {
                        strokeMask.setRGB(sx, sy, brushPixel);
                    }
                }
            }
        }


        public void setGlobal(boolean global) {
            Brush.global = global;
        }

        public boolean getGlobal() {
            return global;
        }
    }

    // Blend modes (add more as needed)
    public enum BlendMode {
        NORMAL {
            @Override
            public int apply(int srcColor, int a, int r, int g, int b) {
                int destA = (srcColor >> 24) & 0xFF;
                int destR = (srcColor >> 16) & 0xFF;
                int destG = (srcColor >> 8) & 0xFF;
                int destB = srcColor & 0xFF;
                if (a == 255 || destA == 0) {
                    return (a << 24) | (r << 16) | (g << 8) | b;
                }
                if (a == 0) return srcColor;
                float alpha = a / 255.0f;
                float invAlpha = 1.0f - alpha;
                int blendedA = (int)(a + destA * invAlpha);
                int blendedR = (int)(r * alpha + destR * invAlpha);
                int blendedG = (int)(g * alpha + destG * invAlpha);
                int blendedB = (int)(b * alpha + destB * invAlpha);
                return (blendedA << 24) | (blendedR << 16) | (blendedG << 8) | blendedB;
            }
        },
        OVERLAY {
            @Override
            public int apply(int srcColor, int a, int r, int g, int b) {
                int sr = (srcColor >> 16) & 0xFF;
                int sg = (srcColor >> 8) & 0xFF;
                int sb = srcColor & 0xFF;
                int or = (sr * r) / 255;
                int og = (sg * g) / 255;
                int ob = (sb * b) / 255;
                return (a << 24) | (or << 16) | (og << 8) | ob;
            }
        };
        public abstract int apply(int srcColor, int a, int r, int g, int b);
    }

    // Getters and tool management methods
    public ValueSliders getValueSliders() { return valueSliders; }
    public Tool getCurrentTool() { return currentTool; }
    public void setCurrentTool(Tool tool) { currentTool = tool; }
    public void setCurrentToolById(int id) {
        if (id >= 0 && id < tools.size()) {
            currentTool = tools.get(id);
        }
    }
    public void nextTool() {
        int currentIndex = tools.indexOf(currentTool);
        int nextIndex = (currentIndex + 1) % tools.size();
        currentTool = tools.get(nextIndex);
    }
    public void previousTool() {
        int currentIndex = tools.indexOf(currentTool);
        int prevIndex = (currentIndex - 1 + tools.size()) % tools.size();
        currentTool = tools.get(prevIndex);
    }
    public List<Tool> getTools() { return tools; }

    public int getColor() {
        return colors.getColor();
    }

    public void setColor(int col) {
        colors.setColor(col);
    }

    // Input event methods
    public void click(float xPixel, float yPixel) {
        switch (currentTool) {
            case MAGIC_WAND:

                if (xPixel < 0 || xPixel >= width || yPixel < 0 || yPixel >= height) break;
                if (activeLayer != null) {
                    // Capture initial state for undo
                    initialSelectionBounds = new Rectangle(selection.bounds);
                    initialSelectionMask = (selection.mask != null ? deepCopy(selection.mask) : null);

                    // Generate new mask
                    BufferedImage newMask = selection.generateMagicWandMask(
                            activeLayer, (int)xPixel, (int)yPixel, Brush.tolerance, selection.getGlobal()
                    );

                    selection.bounds.x = 0;
                    selection.bounds.y = 0;
                    // Update selection
                    selection.setSelectionMask(newMask);

                    if (selection.getType() == Selection.Type.ADD) {
                        if (initialSelectionMask != null){
                            Rectangle combinedBounds = initialSelectionBounds;
                            if (!selection.bounds.isEmpty())
                                combinedBounds = (Rectangle) selection.bounds.createUnion(initialSelectionBounds);
                            BufferedImage combinedMask = new BufferedImage(combinedBounds.width, combinedBounds.height, BufferedImage.TYPE_INT_ARGB);
                            Graphics2D g = combinedMask.createGraphics();

                            if (selection.mask != null)
                                g.drawImage(selection.mask,
                                        selection.bounds.x - combinedBounds.x, selection.bounds.y - combinedBounds.y, selection.mask.getWidth(), selection.mask.getHeight(),
                                        null);
                            if (initialSelectionMask != null)
                                g.drawImage(initialSelectionMask,
                                        initialSelectionBounds.x - combinedBounds.x, initialSelectionBounds.y - combinedBounds.y, initialSelectionMask.getWidth(), initialSelectionMask.getHeight(),
                                        null);
                            g.dispose();

                            selection.bounds = combinedBounds;
                            selection.setSelectionMask(combinedMask);
                            selection.cropMaskToSelection();
                        }
                    } else if (selection.getType() == Selection.Type.REMOVE) {
                        if (initialSelectionMask == null) {
                            selection.clear();
                            return;
                        }

                        Rectangle combinedBounds = selection.isEmpty() ? initialSelectionBounds : (Rectangle) selection.bounds.createUnion(initialSelectionBounds);
                        BufferedImage combinedMask = new BufferedImage(combinedBounds.width, combinedBounds.height, BufferedImage.TYPE_INT_ARGB);
                        Graphics2D g = combinedMask.createGraphics();
                        if (initialSelectionMask != null)
                            g.drawImage(initialSelectionMask,
                                    initialSelectionBounds.x - combinedBounds.x, initialSelectionBounds.y - combinedBounds.y, initialSelectionMask.getWidth(), initialSelectionMask.getHeight(),
                                    null);

                        for (int y1 = 0; y1 < selection.bounds.height; y1++) {
                            for (int x1 = 0; x1 < selection.bounds.width; x1++) {
                                int globalX = selection.bounds.x + x1;
                                int globalY = selection.bounds.y + y1;

                                // Ensure coordinates are within layer and mask bounds
                                if (globalX >= 0 && globalX < width &&
                                        globalY >= 0 && globalY < height &&
                                        x1 >= 0 && x1 < selection.mask.getWidth() &&
                                        y1 >= 0 && y1 < selection.mask.getHeight() &&
                                        globalX - combinedBounds.x >= 0 && globalX - combinedBounds.x < combinedBounds.width &&
                                        globalY - combinedBounds.y >= 0 && globalY - combinedBounds.y < combinedBounds.height) {

                                    // Check if the mask has a non-transparent pixel
                                    int maskAlpha = (selection.mask.getRGB(x1, y1) >> 24) & 0xFF;
                                    if (maskAlpha > 0) {
                                        combinedMask.setRGB(globalX - combinedBounds.x, globalY - combinedBounds.y, 0x00000000); // Set pixel to fully transparent
                                    }
                                }
                            }
                        }


                        g.dispose();

                        selection.bounds = combinedBounds;
                        selection.setSelectionMask(combinedMask);
                        selection.cropMaskToSelection();
                    }


                    // Commit undo action
                    SelectionAction action = new SelectionAction(
                            initialSelectionBounds, initialSelectionMask,
                            selection.bounds, selection.mask
                    );
                    actionManager.beginAction(action);
                    actionManager.commitAction();
                }
                break;
            case BRUSH:
            case ERASER:
                brush.cursorX = xPixel;
                brush.cursorY = yPixel;
                brush.cursorXOld = xPixel;
                brush.cursorYOld = yPixel;
                brush.type = getCurrentTool() == Tool.ERASER ? Brush.Type.ERASE : Brush.Type.DRAW;
                brush.drawing = true;
                // Begin a new draw action
                if (activeLayer != null) {
                    currentDrawAction = new DrawAction(activeLayer, deepCopy(activeLayer.pixels));
                    actionManager.beginAction(currentDrawAction);
                }
                draw(xPixel, yPixel, xPixel, yPixel);
                break;
            case SELECTION:
                if (selection.adjustingSelection)
                    break;
                // Capture initial selection state for undo
                initialSelectionBounds = new Rectangle(selection.bounds);
                initialSelectionMask = (selection.mask != null ? deepCopy(selection.mask) : null);
                startSelection((int)xPixel, (int)yPixel);
                break;
            case MOVE:
                if (!selection.isEmpty() && selection.bounds.contains(xPixel, yPixel)) {
                    if (movingSelection == null) {
                        startMoveSelection((int) xPixel, (int) yPixel);
                    } else {
                        endMoveSelection();
                    }
                }
                break;
            case FILL:
                if (xPixel < 0 || xPixel >= width || yPixel < 0 || yPixel >= height) break;
                // Begin a fill action
                if (activeLayer != null) {
                    DrawAction fillAction = new DrawAction(activeLayer, deepCopy(activeLayer.pixels));
                    actionManager.beginAction(fillAction);
                    floodFill(activeLayer, (int)xPixel, (int)yPixel, getColor(), Brush.tolerance);
                    fillAction.captureAfter();
                    actionManager.commitAction();
                }
                break;
            case EYEDROPPER:
                if (xPixel < 0 || xPixel >= width || yPixel < 0 || yPixel >= height) break;
                setColor(pickColor(activeLayer, (int)xPixel, (int)yPixel));
                valueSliders.updateColorSliders(getColor());
                break;
        }
    }

    public void hover(float xPixel, float yPixel) {
        cursorX = xPixel;
        cursorY = yPixel;
        switch (currentTool) {
            case BRUSH:
            case ERASER:
                if (brush.drawing) {
                    brush.cursorX = xPixel;
                    brush.cursorY = yPixel;
                }
                break;
            case SELECTION:
                if (selection.adjustingSelection) {
                    selection.cursorX = (int) xPixel;
                    selection.cursorY = (int) yPixel;
                }
                break;
            case MOVE:
                if (movingSelection != null) {
                    updateMoveSelection((int) xPixel, (int) yPixel);
                }
                break;
        }
    }

    public void released(int xPixel, int yPixel) {
        switch (currentTool) {
            case BRUSH:
            case ERASER:
                brush.drawing = false;
                endDrawing();
                break;
            case SELECTION:
                if (!selection.adjustingSelection)
                    break;
                if ((initialSelectionBounds == null || initialSelectionBounds.isEmpty()) && (selection.bounds == null || selection.bounds.isEmpty())) {
                    selection.adjustingSelection = false;
                    break;
                }
                endSelection(xPixel, yPixel);
                break;
            case MOVE:
                if (skipNextRelease) {
                    skipNextRelease = false;
                    break;
                }
                if (movingSelection != null) {
                    endMoveSelection();
                }
                break;
        }
    }

    public ResourceLocation getImageLocation() {
        return ResourceLocation.parse(parentLocation.toString() + "/" + uuid.toString());
    }

    // Main update loop
    public void tick() {

        this.toolVisibilityOld = toolVisibility;
        if (toolsVisible) {
            toolVisibility = HexereiUtil.moveTo(toolVisibility, 1, 0.01f + Math.clamp(Math.abs(toolVisibility - 1), 0, 1) * 0.15f);
        } else {
            toolVisibility = HexereiUtil.moveTo(toolVisibility, -1, 0.01f + Math.clamp(Math.abs(toolVisibility - 1), 0, 1) * 0.25f);
        }

        shouldTick = false;
        if (this.cursorXOld != this.cursorX || this.cursorYOld != this.cursorY) {
            this.cursorXOld = this.cursorX;
            this.cursorYOld = this.cursorY;
            if (getCurrentTool().shouldShowBrushSliders() || selection.adjustingSelection)
                this.dirty = true;
        }
        this.getColors().tick();
        this.getValueSliders().updateColorSliders(this.getColor());
        this.getValueSliders().updateBrushSizeSlider(Brush.size);
        this.getValueSliders().updateToleranceSlider(Brush.tolerance);
        this.getValueSliders().updateHardnessSlider(Brush.hardness);
        this.colorsVisibilityOld = colorsVisibility;
        if (getCurrentTool().shouldShowColorSliders() && toolsVisible) {
            colorsVisibility = HexereiUtil.moveTo(colorsVisibility, 1, 0.01f + Math.clamp(Math.abs(colorsVisibility - 1), 0, 1) * 0.15f);
        } else {
            colorsVisibility = HexereiUtil.moveTo(colorsVisibility, -1, 0.01f + Math.clamp(Math.abs(colorsVisibility - 1), 0, 1) * 0.25f);
        }
        for(Button button : this.buttons) {
            button.tick(this);
        }
        for(ValueSlider slider : this.valueSliders.sliders) {
            slider.tick(this);
        }
        boolean anyDirty = false;
        if (dirty) {
            anyDirty = true;
            dirty = false;
        }
        if (brush.drawing) {
            if (brush.cursorXOld != cursorX || brush.cursorYOld != cursorY) {
                draw(brush.cursorXOld, brush.cursorYOld, cursorX, cursorY);
                brush.cursorXOld = brush.cursorX;
                brush.cursorYOld = brush.cursorY;
            }
        } else {
            endDrawing();
        }
        if (selection.adjustingSelection) {
            updateSelection(selection.cursorX, selection.cursorY);
            anyDirty = true;
        }
        for (Layer layer : layers) {
            if (layer.dirty) {
                anyDirty = true;
                break;
            }
        }
        if ((selection.bounds.width) * (selection.bounds.height) > 0)
            anyDirty = true;
        if (!anyDirty) return;
        rebuildComposite();
        if (compositeImage != null) {
            Object tex = Minecraft.getInstance().getTextureManager().getTexture(getImageLocation());
            if (tex instanceof DynamicTexture dynamicTexture) {
                dynamicTexture.setPixels(convertToNativeImage(compositeImage));
                dynamicTexture.upload();
            }
        }

        if (updateToServer) {
            updateToServer = false;
            PaintData paintData = toPaintData();
            HexereiPacketHandler.sendToServer(new PaintDataToServer(paintData));

        }
    }

    public int pickColor(Layer layer, int x, int y) {
        if (layer == null || layer.pixels == null) return 0x00000000;
        int width = layer.pixels.getWidth();
        int height = layer.pixels.getHeight();
        if (x >= 0 && x < width && y >= 0 && y < height) {
            return layer.pixels.getRGB(x, y);
        }
        return 0x00000000;
    }

    public void floodFill(Layer layer, int x, int y, int newColor, float tolerance) {
        startStroke(activeLayer.pixels.getWidth(), activeLayer.pixels.getHeight(), Brush.Type.DRAW);

        if (layer == null || layer.pixels == null) return;
        int width = layer.pixels.getWidth();
        int height = layer.pixels.getHeight();
        if (x < 0 || x >= width || y < 0 || y >= height) return;
        int targetColor = layer.pixels.getRGB(x, y);
        float[] targetHSV = rgbToHsv(targetColor);
        if (!brush.getGlobal()){
            Stack<Point> stack = new Stack<>();
            stack.push(new Point(x, y));
            boolean[][] visited = new boolean[width][height];
            while (!stack.isEmpty()) {
                Point p = stack.pop();
                int px = p.x;
                int py = p.y;
                if (!selection.isEmpty() && (!selection.bounds.contains(px, py) || ((selection.mask.getRGB(px - selection.bounds.x, py - selection.bounds.y) >> 24) & 0xFF) == 0))
                    continue;
                if (px < 0 || px >= width || py < 0 || py >= height || visited[px][py]) {
                    continue;
                }
                visited[px][py] = true;
                int currentColor = layer.pixels.getRGB(px, py);
                float[] currentHSV = rgbToHsv(currentColor);
                int alpha = ((currentColor >> 24) & 0xFF);
                int targetAlpha = ((targetColor >> 24) & 0xFF);
                float difference = calculateColorDifference(targetHSV, currentHSV);
                if ((alpha == 0 && targetAlpha == 0) || difference <= tolerance) {
                    strokeMask.setRGB(px, py, newColor);
                    if (px > 0) stack.push(new Point(px - 1, py));
                    if (px < width - 1) stack.push(new Point(px + 1, py));
                    if (py > 0) stack.push(new Point(px, py - 1));
                    if (py < height - 1) stack.push(new Point(px, py + 1));
                }
            }
        } else {
            for (int px = 0; px < layer.pixels.getWidth(); px++){
                for (int py = 0; py < layer.pixels.getHeight(); py++){
                    if (!selection.isEmpty() && (!selection.bounds.contains(px, py) || ((selection.mask.getRGB(px - selection.bounds.x, py - selection.bounds.y) >> 24) & 0xFF) == 0))
                        continue;
                    int currentColor = layer.pixels.getRGB(px, py);
                    float[] currentHSV = rgbToHsv(currentColor);
                    int alpha = ((currentColor >> 24) & 0xFF);
                    int targetAlpha = ((targetColor >> 24) & 0xFF);
                    float difference = calculateColorDifference(targetHSV, currentHSV);
                    if ((alpha == 0 && targetAlpha == 0) || difference <= tolerance) {
                        strokeMask.setRGB(px, py, newColor);
                    }
                }
            }

        }
        endStroke();
        layer.dirty = true;
    }

    private float[] rgbToHsv(int rgb) {
        int r = (rgb >> 16) & 0xFF, g = (rgb >> 8) & 0xFF, b = rgb & 0xFF, a = (rgb >> 24) & 0xFF;
        float rNorm = r / 255f, gNorm = g / 255f, bNorm = b / 255f, aNorm = a / 255f;
        float max = Math.max(rNorm, Math.max(gNorm, bNorm));
        float min = Math.min(rNorm, Math.min(gNorm, bNorm));
        float delta = max - min;
        float h = 0, s, v = max;
        if (delta != 0) {
            s = delta / max;
            if (rNorm == max) h = (gNorm - bNorm) / delta;
            else if (gNorm == max) h = 2 + (bNorm - rNorm) / delta;
            else h = 4 + (rNorm - gNorm) / delta;
            h *= 60;
            if (h < 0) h += 360;
        } else {
            s = 0;
            h = -1;
        }
        return new float[]{h, s, v, aNorm};
    }

    private float calculateColorDifference(float[] hsv1, float[] hsv2) {
        // Hue difference on a circular scale.
        float dh = Math.abs(hsv1[0] - hsv2[0]);
        if (dh > 180f) {
            dh = 360f - dh;
        }
        dh = dh / 360f;

        // Average alpha, used to scale the hue difference.
        float alphaAvg = (hsv1[3] + hsv2[3]) / 2.0f;
        // Scale the hue difference by the average alpha.
        dh *= alphaAvg;

        // Compute absolute differences for saturation, value, and alpha.
        float ds = Math.abs(hsv1[1] - hsv2[1]);
        float dv = Math.abs(hsv1[2] - hsv2[2]);
        float da = Math.abs(hsv1[3] - hsv2[3]);

        // Define weights for each component. These can be tuned to match desired tolerance behavior.
        float weightH = 0.55f * (alphaAvg);
        float weightS = 0.25f * (alphaAvg);
        float weightV = 0.25f * (alphaAvg);
        float weightA = 0.3f;  // Adjust weight for alpha as needed.

        // Compute a weighted Euclidean difference.
        float diff = (float) Math.sqrt(
                weightH * dh * dh +
                        weightS * ds * ds +
                        weightV * dv * dv +
                        weightA * da * da
        );

        return diff;
    }

    private void startMoveSelection(int x, int y) {

        SelectionAndDrawAction selAction = new SelectionAndDrawAction(activeLayer, selection);
        actionManager.beginAction(selAction);

        // Copy current selection into movingSelection

        movingSelection = copySelection(); // Get pixels based on the mask and bounds
        movingSelectionClickedPos = new Pos2i(x - selection.bounds.x, y - selection.bounds.y);
        movingSelectionOffset = new Pos2i(x - movingSelectionClickedPos.x, y - movingSelectionClickedPos.y);

        // Clear the pixels in the original selection area
        selection.deleteFromLayer(activeLayer);
        activeLayer.dirty = true;
    }

    private void updateMoveSelection(int x, int y) {
        // Calculate the new offset based on cursor position
        movingSelectionOffset = new Pos2i(x - movingSelectionClickedPos.x, y - movingSelectionClickedPos.y);

        // Update selection mask position based on movement offset
        selection.bounds.x = movingSelectionOffset.x;
        selection.bounds.y = movingSelectionOffset.y;
    }

    private void endMoveSelection() {
        if (movingSelection == null) return;

        // Paste the moved selection back to the active layer at the new position.
        Graphics2D g2d = activeLayer.pixels.createGraphics();

        deleteSelection();

        g2d.drawImage(movingSelection, movingSelectionOffset.x, movingSelectionOffset.y, null);
        g2d.dispose();

        // Clear the temporary moving selection state.
        movingSelection = null;
        activeLayer.dirty = true;

        // Set the selection's new top-left based on the moved offset.
        int oldX = movingSelectionOffset.x;
        int oldY = movingSelectionOffset.y;

        // Retrieve active layer dimensions.
        int layerWidth = activeLayer.pixels.getWidth();
        int layerHeight = activeLayer.pixels.getHeight();

        // Clamp the selection bounds to lie within the layer.
        int newX = Math.max(oldX, 0);
        int newY = Math.max(oldY, 0);
        int newWidth = Math.max(0, Math.min(selection.bounds.width, layerWidth - newX));
        int newHeight = Math.max(0, Math.min(selection.bounds.height, layerHeight - newY));

        if (newWidth == 0 || newHeight == 0) {
            // If the updated bounds is empty then clear the selection
            selection.clear();
        } else {
            // Update the selection bounds.
            selection.bounds = new Rectangle(newX, newY, newWidth, newHeight);

            // If there's a selection mask, crop it so that it only covers the new bounds.
            if (selection.mask != null) {
                // Compute the offset within the mask that corresponds to the new top-left.
                int offsetX = newX - oldX;
                int offsetY = newY - oldY;

                // Create a new mask with the clamped dimensions.
                BufferedImage newMask = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = newMask.createGraphics();
                // Draw the relevant portion of the old mask onto the new mask.
                g.drawImage(selection.mask,
                        0, 0, newWidth, newHeight,  // Destination rectangle in new mask
                        offsetX, offsetY, offsetX + newWidth, offsetY + newHeight,  // Source rectangle in old mask
                        null);
                g.dispose();

                selection.mask = newMask;
                selection.cropMaskToSelection();
                selection.edgePoints = selection.orderEdgePoints(selection.findEdgePoints(selection.mask).stream().toList());
            }
        }

        if (actionManager.currentAction instanceof SelectionAndDrawAction selectionAndDrawAction) {
            selectionAndDrawAction.captureAfter(selection);
            actionManager.commitAction();
        }
        updateToServer = true;
    }


    public void startSelection(int x, int y) {
        selection.cursorX = x;
        selection.cursorY = y;
        selection.cursorXOld = x;
        selection.cursorYOld = y;
        selection.adjustingSelection = true;
//        if (selection.getType() == Selection.Type.NORMAL) {
        selection.initializeBounds(x, y);
        selection.clearMask();
//        }
        selection.anchor = new Pos2i(Math.clamp(x, 0, activeLayer.pixels.getWidth()), Math.clamp(y, 0, activeLayer.pixels.getHeight()));
        selection.updateEdgePoints();
        dirty = true;
    }

    public void updateSelection(int x, int y) {
        Rectangle newBounds = selection.getUpdateRectangleBounds(selection.anchor, new Pos2i(Math.clamp(x, 0, activeLayer.pixels.getWidth() - 1), Math.clamp(y, 0, activeLayer.pixels.getHeight() - 1)));
        selection.bounds = newBounds;
        if (selection.bounds.getWidth() > 1 || selection.bounds.getHeight() > 1) {
            selection.createRectangleMask();
        } else {
            selection.setSelectionMask(null);
        }
//        if (selection.getType() == Selection.Type.REPLACE) {
//
//        } else {
//
//        }
    }

    public void endSelection(int x, int y) {
        if (selection.adjustingSelection) {
            selection.adjustingSelection = false;
            updateSelection(x, y);
            if (activeLayer != null && activeLayer.pixels != null) {
                if (selection.bounds.getWidth() > 1 || selection.bounds.getHeight() > 1)
                    selection.createRectangleMask();
                else {
                    selection.setSelectionMask(null);
                }
            }
            dirty = true;
        }
        if (selection.getType() == Selection.Type.ADD) {
            if (initialSelectionMask != null){
                Rectangle combinedBounds = selection.isEmpty() ? initialSelectionBounds : (Rectangle) selection.bounds.createUnion(initialSelectionBounds);
                BufferedImage combinedMask = new BufferedImage(combinedBounds.width, combinedBounds.height, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = combinedMask.createGraphics();

                if (selection.mask != null)
                    g.drawImage(selection.mask,
                        selection.bounds.x - combinedBounds.x, selection.bounds.y - combinedBounds.y, selection.mask.getWidth(), selection.mask.getHeight(),
                        null);
                if (initialSelectionMask != null)
                    g.drawImage(initialSelectionMask,
                        initialSelectionBounds.x - combinedBounds.x, initialSelectionBounds.y - combinedBounds.y, initialSelectionMask.getWidth(), initialSelectionMask.getHeight(),
                        null);
                g.dispose();

                selection.bounds = combinedBounds;
                selection.setSelectionMask(combinedMask);
                selection.cropMaskToSelection();
            }
        } else if (selection.getType() == Selection.Type.REMOVE) {
            if (initialSelectionMask == null) {
                selection.clear();
                return;
            }

            Rectangle combinedBounds = selection.isEmpty() ? initialSelectionBounds : (Rectangle) selection.bounds.createUnion(initialSelectionBounds);
            BufferedImage combinedMask = new BufferedImage(combinedBounds.width, combinedBounds.height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = combinedMask.createGraphics();
            if (initialSelectionMask != null)
                g.drawImage(initialSelectionMask,
                        initialSelectionBounds.x - combinedBounds.x, initialSelectionBounds.y - combinedBounds.y, initialSelectionMask.getWidth(), initialSelectionMask.getHeight(),
                        null);

            for (int y1 = 0; y1 < selection.bounds.height; y1++) {
                for (int x1 = 0; x1 < selection.bounds.width; x1++) {
                    int globalX = selection.bounds.x + x1;
                    int globalY = selection.bounds.y + y1;

                    // Ensure coordinates are within layer and mask bounds
                    if (globalX >= 0 && globalX < width &&
                            globalY >= 0 && globalY < height &&
                            x1 >= 0 && x1 < selection.mask.getWidth() &&
                            y1 >= 0 && y1 < selection.mask.getHeight() &&
                            globalX - combinedBounds.x >= 0 && globalX - combinedBounds.x < combinedBounds.width &&
                            globalY - combinedBounds.y >= 0 && globalY - combinedBounds.y < combinedBounds.height) {

                        // Check if the mask has a non-transparent pixel
                        int maskAlpha = (selection.mask.getRGB(x1, y1) >> 24) & 0xFF;
                        if (maskAlpha > 0) {
                            combinedMask.setRGB(globalX - combinedBounds.x, globalY - combinedBounds.y, 0x00000000); // Set pixel to fully transparent
                        }
                    }
                }
            }


            g.dispose();

            selection.bounds = combinedBounds;
            selection.setSelectionMask(combinedMask);
//                selection.bounds = combinedBounds;
//                selection.setSelectionMask(combinedMask);
        }

        if (initialSelectionMask == null && selection.mask == null)
            return; // exit out early and stop action committed

        // Commit selection change action
        SelectionAction selAction = new SelectionAction(initialSelectionBounds, initialSelectionMask, selection.bounds, selection.mask);
        actionManager.beginAction(selAction);
        actionManager.commitAction();
    }

    public BufferedImage copySelection() {
        if (activeLayer == null || selection.isEmpty()) return null;
        return selection.extractSelectedArea(activeLayer);
    }

    public boolean canCopy() {
        return selection.bounds != null && selection.mask != null;
    }

    public void copySelectionToClipboard() {
        if (!canCopy())
            return;
        clipboardImage = copySelection();
        clipboardBounds = new Rectangle(selection.bounds);
        clipboardMask = deepCopy(selection.mask);

    }

    public void cutSelectionToClipboard() {
        if (!canCopy())
            return;
        SelectionAndDrawAction action = new SelectionAndDrawAction(activeLayer, selection);
        actionManager.beginAction(action);
        copySelectionToClipboard();
        deleteSelection();
        selection.clear();
        activeLayer.dirty = true;
        action.captureAfter(selection);
        actionManager.commitAction();
        updateToServer = true;
    }

    public void pasteClipboard() {
        // dont paste if no clipboard or missing a clipboard piece
        if (clipboardImage == null || clipboardBounds == null || clipboardMask == null)
            return;

        // if somehow has a movingSelection, then end that move action and start this one.
        if (movingSelection != null)
            endMoveSelection();

        SelectionAndDrawAction action = new SelectionAndDrawAction(activeLayer, selection);
        actionManager.beginAction(action);
        movingSelection = deepCopy(clipboardImage);
        selection.mask = deepCopy(clipboardMask);
        selection.bounds = clipboardBounds == null ? null : new Rectangle(clipboardBounds);
        selection.updateEdgePoints();

        movingSelectionClickedPos = new Pos2i(movingSelection.getWidth() / 2, movingSelection.getHeight() / 2);
        movingSelectionOffset = new Pos2i((int)cursorX - movingSelectionClickedPos.x, (int)cursorY - movingSelectionClickedPos.y);
        setCurrentTool(Tool.MOVE);
        updateMoveSelection((int) cursorX, (int) cursorY);
        skipNextRelease = true;
    }

    public void deleteSelection() {
        selection.deleteFromLayer(activeLayer);
    }

    public void startStroke(int width, int height, Brush.Type strokeType) {
        this.strokeType = strokeType;
        this.strokeMask = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    }

    public void endStroke() {
        if (strokeMask == null || activeLayer == null) return;
        boolean changed = false;
        for (int y = 0; y < strokeMask.getHeight(); y++) {
            for (int x = 0; x < strokeMask.getWidth(); x++) {
                switch (strokeType) {
                    case ERASE -> {
                        int maskPixel = strokeMask.getRGB(x, y);
                        int maskA = (maskPixel >> 24) & 0xFF;
                        int pixel = activeLayer.pixels.getRGB(x, y);
                        int destA = (pixel >> 24) & 0xFF;
                        if (destA == 0 || maskA == 0) continue;
                        int newA = Math.max(0, destA - maskA);
                        int newPixel = (newA << 24) | (pixel & 0x00FFFFFF);
                        activeLayer.pixels.setRGB(x, y, newPixel);
                        changed = true;
                    }
                    case DRAW -> {
                        int maskPixel = strokeMask.getRGB(x, y);
                        int maskA = (maskPixel >> 24) & 0xFF;
                        if (maskA > 0) {
                            int layerPixel = activeLayer.pixels.getRGB(x, y);
                            if (activeLayer.blendMode != null) {
                                maskPixel = activeLayer.blendMode.apply(
                                        layerPixel,
                                        maskA,
                                        (maskPixel >> 16) & 0xFF,
                                        (maskPixel >> 8) & 0xFF,
                                        maskPixel & 0xFF
                                );
                            }
                            activeLayer.pixels.setRGB(x, y, maskPixel);
                            changed = true;
                        }
                    }
                }
            }
        }
        strokeMask = null;
        activeLayer.dirty = true;
        if (changed)
            updateToServer = true;
        // Capture after state for the current draw action and commit it.
        if (currentDrawAction != null) {
            currentDrawAction.captureAfter();
            if (changed)
                actionManager.commitAction();
            currentDrawAction = null;
        }
    }

    public static NativeImage convertToNativeImage(BufferedImage bufferedImage) {
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();

        NativeImage nativeImage = new NativeImage(NativeImage.Format.RGBA, width, height, false);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int argb = bufferedImage.getRGB(x, y);
                int a = (argb >> 24) & 0xFF;
                int r = (argb >> 16) & 0xFF;
                int g = (argb >> 8) & 0xFF;
                int b = argb & 0xFF;
                int abgrColor = (a << 24) | (b << 16) | (g << 8) | r;
                nativeImage.setPixelRGBA(x, y, abgrColor);
            }
        }
        return nativeImage;
    }

    public void addAndUpdateTexture() {
        this.rebuildComposite();
        DynamicTexture dynamicTexture = new DynamicTexture(PaintSystem.convertToNativeImage(this.compositeImage));
        Minecraft.getInstance().getTextureManager().register(this.getImageLocation(), dynamicTexture);
    }

    public void rebuildComposite() {
        if (layers.isEmpty()) return;
        int width = this.width;
        int height = this.height;
        compositeImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = compositeImage.createGraphics();
        for (Layer layer : layers) {
            BufferedImage temp = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    int pixel = layer.pixels.getRGB(x, y);
                    int a = (pixel >> 24) & 0xFF;
                    if (layer.opacity != 1.0f) {
                        a = (int)(a * layer.opacity);
                    }
                    if (layer == activeLayer && strokeMask != null) {
                        if (strokeType == Brush.Type.ERASE) {
                            int maskPixel = strokeMask.getRGB(x, y);
                            int maskA = (maskPixel >> 24) & 0xFF;
                            int destA = (pixel >> 24) & 0xFF;
                            if (a != 0 && maskA != 0) {
                                int newA = Math.max(0, destA - maskA);
                                int newPixel = (newA << 24) | (pixel & 0x00FFFFFF);
                                pixel = newPixel;
                                a = newA;
                            }
                        }
                    }

                    int existing = compositeImage.getRGB(x, y);
                    if (layer.blendMode != null) {
                        pixel = layer.blendMode.apply(existing, a, (pixel >> 16) & 0xFF, (pixel >> 8) & 0xFF, pixel & 0xFF);
                    }
                    temp.setRGB(x, y, pixel);
                }
            }
            if (layer == activeLayer && movingSelection != null) {
                for (int y = 0; y < selection.bounds.height; y++) {
                    for (int x = 0; x < selection.bounds.width; x++) {
                        int globalX = selection.bounds.x + x;
                        int globalY = selection.bounds.y + y;

                        // Ensure coordinates are within layer and mask bounds
                        if (globalX >= 0 && globalX < temp.getWidth() &&
                                globalY >= 0 && globalY < temp.getHeight() &&
                                x < selection.mask.getWidth() &&
                                y < selection.mask.getHeight()) {

                            // Check if the mask has a non-transparent pixel
                            int maskAlpha = (selection.mask.getRGB(x, y) >> 24) & 0xFF;
                            if (maskAlpha > 0) {
                                temp.setRGB(globalX, globalY, 0x00000000); // Set pixel to fully transparent
                            }
                        }
                    }
                }
                Graphics2D g = temp.createGraphics();
                g.drawImage(movingSelection, movingSelectionOffset.x, movingSelectionOffset.y, null);
            }
            g2d.drawImage(temp, 0, 0, null);
            if (layer == activeLayer && strokeMask != null) {
                if (brush.type == Brush.Type.DRAW) {
                    for (int x = 0; x < width; x++) {
                        for (int y = 0; y < height; y++) {
                            int maskPixel = strokeMask.getRGB(x, y);
                            int maskA = (maskPixel >> 24) & 0xFF;
                            if (maskA > 0) {
                                int existingPixel = compositeImage.getRGB(x, y);
                                if (layer.blendMode != null) {
                                    maskPixel = layer.blendMode.apply(existingPixel, maskA, (maskPixel >> 16) & 0xFF, (maskPixel >> 8) & 0xFF, maskPixel & 0xFF);
                                }
                                compositeImage.setRGB(x, y, maskPixel);
                            }
                        }
                    }
                }
            }
            layer.dirty = false;
        }

        if (selection != null && toolsVisible) {
            selection.render(g2d);
        }

        if (currentTool.shouldShowBrushSliders() && toolsVisible) {
            int diameter = brush.size + 1;
            int drawX = (int) (cursorX - diameter / 2.0f + 0.5f);
            int drawY = (int) (cursorY - diameter / 2.0f + 0.5f);
            BufferedImage brushImage = brush.generateBrushMask(true);
            brushImage = adjustImage(brushImage, 0.35f, Color.GRAY);


            if (selection.mask != null){
                for (int y = 0; y < brushImage.getWidth(); y++) {
                    for (int x = 0; x < brushImage.getHeight(); x++) {
                        int globalX = drawX + x;
                        int globalY = drawY + y;

                        // Ensure coordinates are within layer and mask bounds
                        if (
                                (globalX - selection.bounds.x >= 0 && globalX - selection.bounds.x < selection.mask.getWidth() &&
                                globalY - selection.bounds.y >= 0 && globalY - selection.bounds.y < selection.mask.getHeight() &&
                                globalX >= selection.bounds.x && globalX < selection.mask.getWidth() + selection.bounds.x &&
                                globalY >= selection.bounds.y && globalY < selection.mask.getHeight() + selection.bounds.y)) {

                            // Check if the mask has a non-transparent pixel
                            int maskAlpha = (selection.mask.getRGB(globalX - selection.bounds.x, globalY - selection.bounds.y) >> 24) & 0xFF;
                            if (maskAlpha == 0) {
                                brushImage.setRGB(x, y, 0x00000000); // Set pixel to fully transparent
                            }
                        } else {
                            brushImage.setRGB(x, y, 0x00000000); // Set pixel to fully transparent
                        }
                    }
                }
            }
            g2d.drawImage(brushImage, drawX, drawY, null);
        }

        g2d.dispose();
    }

    public BufferedImage adjustImage(BufferedImage image, float alphaFactor, Color newColor) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = image.getRGB(x, y);
                // Extract the original alpha (0-255)
                int origAlpha = (pixel >> 24) & 0xFF;
                // Adjust alpha using the factor (e.g., 0.5 for 50% transparency)
                int newAlpha = (int)(origAlpha * alphaFactor);
                // Use the RGB components from newColor
                int r = newColor.getRed();
                int g = newColor.getGreen();
                int b = newColor.getBlue();
                int outPixel = (newAlpha << 24) | (r << 16) | (g << 8) | b;

                result.setRGB(x, y, outPixel);
            }
        }
        return result;
    }

    public void addLayer(int width, int height) {
        Layer layer = new Layer();
        layer.pixels = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        layers.add(layer);
        if (activeLayer == null) {
            activeLayer = layer;
        }
    }

    public void addLayer(Layer layer) {
        layers.add(layer);
        if (activeLayer == null) {
            activeLayer = layer;
        }
    }

    public void setActiveLayer(int index) {
        if (index >= 0 && index < layers.size()) {
            activeLayer = layers.get(index);
        }
    }

    public void endDrawing() {
        if (strokeMask != null) {
            endStroke();
        }
    }

    public void draw(float lastX, float lastY, float x, float y) {
        if (activeLayer == null || brush == null) return;
        if (strokeMask == null) {
            startStroke(activeLayer.pixels.getWidth(), activeLayer.pixels.getHeight(), brush.type);
        }
        drawLine(lastX, lastY, x, y);
        activeLayer.dirty = true;
    }

    private void drawLine(float x0, float y0, float x1, float y1) {
        // Use doubles for higher precision during iteration
        float currentX = x0;
        float currentY = y0;

        int targetX = (int) Math.ceil(x1);
        int targetY = (int) Math.ceil(y1);

        int lastX = (int) Math.ceil(currentX);
        int lastY = (int) Math.ceil(currentY);

        brush.apply(currentX, currentY); // Draw the first brush position

        brush.apply(x1, y1); // Draw the last brush position

        double dx = targetX - Math.ceil(currentX);
        double dy = targetY - Math.ceil(currentY);
        double distance = Math.sqrt(dx * dx + dy * dy);

        // Avoid division by zero for degenerate lines
        if (distance == 0) return;

        float stepX = (float) dx / (float) distance;
        float stepY = (float) dy / (float) distance;

        int i = 0;
        // Ensure we walk through every pixel the line touches
        while (Math.ceil(currentX) != targetX || Math.ceil(currentY) != targetY) {
            i++;
            if (i + 0.5f > distance) break;
            currentX += stepX;
            currentY += stepY;

            int newX = (int) currentX;
            int newY = (int) currentY;

            // Only draw when entering a new pixel
            if (newX != lastX || newY != lastY) {
                brush.apply(newX, newY);
                lastX = newX;
                lastY = newY;
            }
        }
    }

    public void setToolsVisible(boolean toolsVisible) {
        if (this.toolsVisible != toolsVisible) {
            this.toolsVisible = toolsVisible;
            dirty = true;
        }
    }

    public BufferedImage getMovingSelection() {
        return movingSelection;
    }

    public List<Layer> getLayers() { return layers; }
    public Layer getActiveLayer() { return activeLayer; }
    public void setActiveLayer(Layer activeLayer) { this.activeLayer = activeLayer; }
    public Brush getBrush() { return brush; }
    public Colors getColors() { return colors; }
}