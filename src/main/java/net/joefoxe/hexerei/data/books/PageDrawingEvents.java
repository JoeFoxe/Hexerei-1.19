package net.joefoxe.hexerei.data.books;

import mezz.jei.api.runtime.IRecipesGui;
import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.config.ModKeyBindings;
import net.joefoxe.hexerei.event.ClientEvents;
import net.joefoxe.hexerei.integration.jei.HexereiJei;
import net.joefoxe.hexerei.integration.jei.HexereiJeiCompat;
import net.joefoxe.hexerei.item.data_components.BookData;
import net.joefoxe.hexerei.tileentity.BookOfShadowsAltarTile;
import net.joefoxe.hexerei.util.HexereiPacketHandler;
import net.joefoxe.hexerei.util.message.AskForEntriesAndPagesPacket;
import net.joefoxe.hexerei.util.message.AskForPaintDataToServer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec2;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent;

import java.util.List;
import java.util.Optional;

@EventBusSubscriber(value = Dist.CLIENT, bus = EventBusSubscriber.Bus.GAME)
public class PageDrawingEvents {



    @SubscribeEvent
    public static void clientTickEvent(ClientTickEvent.Pre event) {


        if (Minecraft.getInstance().isPaused())
            return;

        if (BookReloadListener.askForUpdate && Minecraft.getInstance().level != null) {
            BookReloadListener.askForUpdate = false;
            HexereiPacketHandler.sendToServer(new AskForEntriesAndPagesPacket());
            HexereiPacketHandler.sendToServer(new AskForPaintDataToServer());
        }

        boolean pressed = false;

        for (ResourceLocation book : BookManager.getBookLocations()){
            BookEntries bookEntries = BookManager.getBookEntries(book);


            if (bookEntries != null) {
                for (BookChapter bookChapter : bookEntries.chapterList) {
                    for (BookPageEntry bookPageEntry : bookChapter.pages) {

                        BookPage page = BookManager.getBookPages(book, ResourceLocation.parse(bookPageEntry.location));
                        if (page != null) {
                            for (BookPaintElement paintElement : page.paintElements) {
                                if (paintElement.client != null) {
                                    for (PaintSystem paintSystem : paintElement.client.paintSystems.values())
                                        if (paintSystem.shouldTick)
                                            paintSystem.tick();
                                }
                            }
//                                PaintSystem paintSystem = paintElement.client.getPaintSystem(book);
//                                if (paintElement.client != null && paintElement.client.paintSystem.shouldTick)
//                                    paintElement.client.paintSystem.tick();

                            for (BookEntity bookEntity : page.entityList) {

                                if (bookEntity.markedForUpdate) {
                                    bookEntity.markedForUpdate = false;

                                    if (bookEntity.entity instanceof LivingEntity livingEntity) {
                                        if (!bookEntity.entityTagsList.isEmpty() && ClientEvents.getClientTicksWithoutPartial() > bookEntity.entityTagsLastChange + 40) {
                                            bookEntity.entityTagsLastChange = (int) ClientEvents.getClientTicksWithoutPartial();
                                            bookEntity.entityTagsListOn++;
                                            if (bookEntity.entityTagsListOn >= bookEntity.entityTagsList.size())
                                                bookEntity.entityTagsListOn = 0;
                                            int on = bookEntity.entityTagsListOn;

                                            if (bookEntity.entityTagsListOnSet != bookEntity.entityTagsListOn && !bookEntity.entityTagsList.get(on).isEmpty()) {

                                                livingEntity.load(bookEntity.entityTagsList.get(on));

                                                bookEntity.entityTagsListOnSet = bookEntity.entityTagsListOn;

                                            }
                                        }
                                    } else if (bookEntity.entity != null) {
                                        if (!bookEntity.entityTagsList.isEmpty() && ClientEvents.getClientTicksWithoutPartial() > bookEntity.entityTagsLastChange + 40) {
                                            bookEntity.entityTagsLastChange = (int) ClientEvents.getClientTicksWithoutPartial();
                                            bookEntity.entityTagsListOn++;
                                            if (bookEntity.entityTagsListOn >= bookEntity.entityTagsList.size())
                                                bookEntity.entityTagsListOn = 0;
                                            int on = bookEntity.entityTagsListOn;
                                            if (bookEntity.entityTagsListOnSet != on && !bookEntity.entityTagsList.get(on).isEmpty()) {

                                                bookEntity.entity.load(bookEntity.entityTagsList.get(on));

                                                bookEntity.entityTagsListOnSet = bookEntity.entityTagsListOn;
                                            }
                                        }
                                    } else {
                                        Optional<EntityType<?>> optionalEntityType = EntityType.byString(bookEntity.entityType);
                                        if (optionalEntityType.isPresent()) {
                                            Entity entity = optionalEntityType.get().create(Hexerei.proxy.getLevel());

                                            if (entity instanceof LivingEntity livingEntity) {
                                                bookEntity.entity = entity;

                                                if (!bookEntity.entityTags.isEmpty()) {
                                                    livingEntity.load(bookEntity.entityTags);
                                                }
                                            } else {
                                                bookEntity.entity = entity;

                                                if (!bookEntity.entityTags.isEmpty() && entity != null) {
                                                    entity.load(bookEntity.entityTags);
                                                }
                                            }
                                        }
                                    }

                                    MouseHandler handler = Minecraft.getInstance().mouseHandler;
                                    if (bookEntity.entity != null) {
                                        bookEntity.toRotateO = bookEntity.toRotate;
                                        if (bookEntity.clicked) {
                                            bookEntity.toRotate -= (float) handler.getXVelocity() * 5f;
                                            pressed = true;
                                        }
                                        bookEntity.entity.tick();
                                    }
                                    bookEntity.tick();

                                }
                            }
                        }
                    }
                }
            }
        }

        PageDrawing.isClicked = pressed;
        Hexerei.entityClicked = pressed;
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    @OnlyIn(Dist.CLIENT)
    public static void onKeyEvent(InputEvent.Key event) {
//        System.out.println(event.getKey() == ModKeyBindings.bookJEIShowUses.getKey().getValue());


        if (!HexereiJeiCompat.LOADED)
            return;

        if (Minecraft.getInstance().screen != null)
            return;

        Player playerIn = Hexerei.proxy.getPlayer();

        //released
        if (playerIn != null && event.getAction() == 0) {

        }

        //pressed
        if (playerIn != null && event.getAction() == 1) {
            if (event.getKey() != ModKeyBindings.bookJEIShowUses.getKey().getValue() && event.getKey() != ModKeyBindings.bookJEIShowRecipe.getKey().getValue())
                return;
            if ((Minecraft.getInstance().screen instanceof IRecipesGui))
                return;

            double reach = playerIn.getAttribute(Attributes.BLOCK_INTERACTION_RANGE).getValue();

            List<BlockPos> altars = PageDrawing.getAltars(playerIn);

            for (BlockPos pos : altars) {

                BlockEntity blockEntity = playerIn.level().getBlockEntity(pos);

                if (blockEntity instanceof BookOfShadowsAltarTile altarTile && altarTile.turnPage == 0) {

                    Vec2 leftCursor = PageDrawing.getIntersectPoint(Minecraft.getInstance().player.getLookAngle(), Minecraft.getInstance().player.getEyePosition(), altarTile, PageDrawing.PageOn.LEFT_PAGE);
                    Vec2 rightCursor = PageDrawing.getIntersectPoint(Minecraft.getInstance().player.getLookAngle(), Minecraft.getInstance().player.getEyePosition(), altarTile, PageDrawing.PageOn.RIGHT_PAGE);
                    if (leftCursor == null)
                        leftCursor = new Vec2(50, 50);
                    if (rightCursor == null)
                        rightCursor = new Vec2(50, 50);

                    BookData bookData = altarTile.currentBook;

                    if (bookData != null && bookData.isOpened()) {

                        String location1 = "";
                        String location2 = "";
                        BookEntries bookEntries = BookManager.getBookEntries(bookData.getBook());
                        if (bookEntries != null) {
                            int chapter = bookData.getChapter();
                            int page = bookData.getPage();
                            if (page % 2 == 1)
                                page--;

                            if (bookEntries.chapterList.get(chapter).pages.size() > page && page >= 0)
                                location1 = bookEntries.chapterList.get(chapter).pages.get(page).location;
                            if (bookEntries.chapterList.get(chapter).pages.size() > page + 1 && page >= 0)
                                location2 = bookEntries.chapterList.get(chapter).pages.get(page + 1).location;

                            BookPage page1 = BookManager.getBookPages(bookData.getBook(), ResourceLocation.parse(location1));
                            BookPage page2 = BookManager.getBookPages(bookData.getBook(), ResourceLocation.parse(location2));


                            if (page1 != null) {
                                for (BookItemsAndFluids bookItemStackInSlot : page1.itemList) {

                                    if (bookItemStackInSlot.item != null && bookItemStackInSlot.item.isEmpty())
                                        continue;

                                    if (PageDrawing.canInteract(leftCursor.x, leftCursor.y, bookItemStackInSlot.x, bookItemStackInSlot.y, 0.86f, 0.86f, altarTile, PageDrawing.DrawingType.BOOK)) {
//                                    if (PageDrawing.intersectPointItems(bookItemStackInSlot.x, bookItemStackInSlot.y, playerIn.getLookAngle(), playerIn.getEyePosition(), altarTile, PageDrawing.PageOn.LEFT_PAGE) &&
//                                            Math.sqrt(altarTile.getBlockPos().distToCenterSqr(playerIn.getEyePosition())) <= reach) {

                                        if (event.getKey() == ModKeyBindings.bookJEIShowUses.getKey().getValue()) {
                                            if (bookItemStackInSlot.item != null) {
                                                HexereiJei.showUses(bookItemStackInSlot.item);
                                            } else {
                                                HexereiJei.showUses(bookItemStackInSlot.fluid);
                                            }
                                        }
                                        if (event.getKey() == ModKeyBindings.bookJEIShowRecipe.getKey().getValue()) {
                                            if (bookItemStackInSlot.item != null) {
                                                HexereiJei.showRecipe(bookItemStackInSlot.item);
                                            } else {
                                                HexereiJei.showRecipe(bookItemStackInSlot.fluid);
                                            }
                                        }

                                        break;
                                    }
                                }
                            }
                            if (page2 != null) {

                                for (BookItemsAndFluids bookItemStackInSlot : page2.itemList) {

                                    if (bookItemStackInSlot.item == null || bookItemStackInSlot.item.isEmpty())
                                        continue;

                                    if (PageDrawing.canInteract(rightCursor.x, rightCursor.y, bookItemStackInSlot.x, bookItemStackInSlot.y, 0.86f, 0.86f, altarTile, PageDrawing.DrawingType.BOOK)) {
//                                    if (PageDrawing.intersectPointItems(bookItemStackInSlot.x, bookItemStackInSlot.y, playerIn.getLookAngle(), playerIn.getEyePosition(), altarTile, PageDrawing.PageOn.RIGHT_PAGE) &&
//                                            Math.sqrt(altarTile.getBlockPos().distToCenterSqr(playerIn.getEyePosition())) <= reach) {

                                        if (event.getKey() == ModKeyBindings.bookJEIShowUses.getKey().getValue()) {
                                            if (bookItemStackInSlot.item != null) {
                                                HexereiJei.showUses(bookItemStackInSlot.item);
                                            } else {
                                                HexereiJei.showUses(bookItemStackInSlot.fluid);
                                            }
                                        }
                                        if (event.getKey() == ModKeyBindings.bookJEIShowRecipe.getKey().getValue()) {
                                            if (bookItemStackInSlot.item != null) {
                                                HexereiJei.showRecipe(bookItemStackInSlot.item);
                                            } else {
                                                HexereiJei.showRecipe(bookItemStackInSlot.fluid);
                                            }
                                        }

                                        break;
                                    }
                                }
                            }
                        }

                    }
                }
            }

        }
    }

    @SubscribeEvent
//    @OnlyIn(Dist.CLIENT)
    public static void onClickEvent(InputEvent.MouseButton.Pre event) {

//        if (Minecraft.getInstance().screen != null)
//            PageDrawing.isClickedOld = false;
        Player playerIn = Hexerei.proxy.getPlayer();
        if (event.getButton() == 1 && playerIn != null && Minecraft.getInstance().screen == null) {
            if (event.getAction() == 1)
                PageDrawing.clearFocusedWritableTextBox();
//            PageDrawing.isRightPressedOld = false;
//            Hexerei.entityClicked = false;

            List<BlockPos> altars = PageDrawing.getAltars(playerIn);

            for (BlockPos pos : altars) {

                BlockEntity blockEntity = playerIn.level().getBlockEntity(pos);

                if (blockEntity instanceof BookOfShadowsAltarTile altarTile) {

                    Vec2 leftCursor = PageDrawing.getIntersectPoint(playerIn.getLookAngle(), playerIn.getEyePosition(), altarTile, PageDrawing.PageOn.LEFT_PAGE);
                    Vec2 rightCursor = PageDrawing.getIntersectPoint(playerIn.getLookAngle(), playerIn.getEyePosition(), altarTile, PageDrawing.PageOn.RIGHT_PAGE);
                    if (leftCursor == null)
                        leftCursor = new Vec2(50, 50);
                    if (rightCursor == null)
                        rightCursor = new Vec2(50, 50);

                    if (event.getAction() == 1)
                        if (altarTile.drawing.interactClick(altarTile, playerIn, leftCursor.x, leftCursor.y, rightCursor.x, rightCursor.y, PageDrawing.DrawingType.BOOK)) {
                            event.setCanceled(true);
                            playerIn.swing(InteractionHand.MAIN_HAND);
                            break;
                        }

                    if (event.getAction() == 0)
                        if (altarTile.drawing.releaseClick(altarTile, playerIn, leftCursor.x, leftCursor.y, rightCursor.x, rightCursor.y, PageDrawing.DrawingType.BOOK)) {
                            event.setCanceled(true);
                            playerIn.swing(InteractionHand.MAIN_HAND);
                            break;
                        }
                }

            }
        }
//        else if (event.getButton() == 0) {
//            PageDrawing.isLeftPressedOld = event.getAction() == 1;
//        }
//
//        if (playerIn != null && event.getButton() == 1) {
//            PageDrawing.isRightPressedOld = true;
//        }

    }

    @SubscribeEvent
    public static void onPostClickEvent(InputEvent.MouseButton.Post event) {

        Player playerIn = Hexerei.proxy.getPlayer();
        if (playerIn != null && event.getAction() == 0) {
            for (ResourceLocation book : BookManager.getBookLocations()) {
                BookEntries bookEntries = BookManager.getBookEntries(book);

                if (bookEntries != null) {
                    for (BookChapter bookChapter : bookEntries.chapterList) {
                        for (BookPageEntry bookPageEntry : bookChapter.pages) {

                            BookPage page = BookManager.getBookPages(book, ResourceLocation.parse(bookPageEntry.location));
                            if (page != null) {
                                for (BookEntity bookEntity : page.entityList) {
                                    bookEntity.clicked = false;
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    @OnlyIn(Dist.CLIENT)
    public static boolean clickedBack(BookOfShadowsAltarTile altarTile) {

        BookData bookData = altarTile.currentBook;
        int currentPage = bookData.getPage();
        int currentChapter = bookData.getChapter();
        return currentChapter > 0 || currentPage > 1;

    }

    @OnlyIn(Dist.CLIENT)
    public static boolean clickedNext(BookOfShadowsAltarTile altarTile) {

        BookData bookData = altarTile.currentBook;
        BookEntries bookEntries = BookManager.getBookEntries(bookData.getBook());
        int currentPage = bookData.getPage();
        int currentChapter = bookData.getChapter();
        return bookEntries != null && (currentChapter < bookEntries.chapterList.size() - 1 || currentPage < bookEntries.chapterList.get(currentChapter).pages.size() - 2);

    }

}
