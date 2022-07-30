package net.joefoxe.hexerei.item.custom;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.client.renderer.IFirstPersonItemAnimation;
import net.joefoxe.hexerei.client.renderer.IThirdPersonItemAnimation;
import net.joefoxe.hexerei.client.renderer.IThirdPersonItemRenderer;
import net.joefoxe.hexerei.client.renderer.entity.custom.CrowEntity;
import net.joefoxe.hexerei.container.CrowFluteContainer;
import net.joefoxe.hexerei.item.ModItems;
import net.joefoxe.hexerei.sounds.ModSounds;
import net.joefoxe.hexerei.util.HexereiPacketHandler;
import net.joefoxe.hexerei.client.renderer.TwoHandedItemAnimation;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.joefoxe.hexerei.util.message.*;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.model.*;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.function.Predicate;


public class CrowFluteItem extends Item implements Container, IThirdPersonItemAnimation,
        IThirdPersonItemRenderer, IFirstPersonItemAnimation {

    protected final Predicate<CrowEntity> targetEntitySelector;
    private static final Predicate<Entity> field_219989_a = EntitySelector.NO_SPECTATORS.and(Entity::canBeCollidedWith);
    public int commandSelected;
    public int helpCommandSelected;
    boolean initialized = false;


    public CrowFluteItem(Properties properties) {
        super(properties);
        this.targetEntitySelector = input -> true;
        commandSelected = 0;
        helpCommandSelected = 0;
    }

    protected AABB getTargetableArea(double targetDistance, Entity entity) {
        Vec3 renderCenter = new Vec3(entity.getX(), entity.getY(), entity.getZ());
        AABB aabb = new AABB(-targetDistance, -targetDistance, -targetDistance, targetDistance, targetDistance, targetDistance);
        return aabb.move(renderCenter);
    }

    @Override
    public void inventoryTick(ItemStack itemstack, Level level, Entity p_41406_, int p_41407_, boolean p_41408_) {
        if(!itemstack.getOrCreateTag().contains("commandSelected"))
            itemstack.getOrCreateTag().putInt("commandSelected", 0);
        if(!itemstack.getOrCreateTag().contains("helpCommandSelected"))
            itemstack.getOrCreateTag().putInt("helpCommandSelected", 0);
        if(!itemstack.getOrCreateTag().contains("commandMode"))
            itemstack.getOrCreateTag().putBoolean("commandMode", false);
        if(!itemstack.getOrCreateTag().contains("crowList"))
            itemstack.getOrCreateTag().put("crowList", new CompoundTag());


        if(!itemstack.getOrCreateTag().contains("dyeColor1"))
            itemstack.getOrCreateTag().putInt("dyeColor1", -1);
//        else
//            itemstack.getOrCreateTag().putInt("dyeColor1", itemstack.getOrCreateTag().getInt("dyeColor1") + 1 > 16 ? 0 : itemstack.getOrCreateTag().getInt("dyeColor1") + 1);
        if(!itemstack.getOrCreateTag().contains("dyeColor2"))
            itemstack.getOrCreateTag().putInt("dyeColor2", -1);
//        else
//            itemstack.getOrCreateTag().putInt("dyeColor2", itemstack.getOrCreateTag().getInt("dyeColor2") + 1 > 16 ? 0 : itemstack.getOrCreateTag().getInt("dyeColor2") + 1);

        if(!initialized && !level.isClientSide){
            ListTag id = itemstack.getOrCreateTag().getList("crowList", Tag.TAG_COMPOUND);
            for (int i = 0; i < id.size(); i++) {
                CompoundTag tag = id.getCompound(i);
                if(tag.contains("UUID")) {
                    UUID crowId = tag.getUUID("UUID");
                    Entity entity = ((ServerLevel) level).getEntity(crowId);
                    if (entity instanceof CrowEntity)
                        tag.putInt("ID", entity.getId());
                }
            }
        }

        super.inventoryTick(itemstack, level, p_41406_, p_41407_, p_41408_);
    }

    @Override
    public InteractionResult useOn(UseOnContext ctx) {

        Player player = ctx.getPlayer();
        ItemStack itemstack = ctx.getItemInHand();
        if(!player.isShiftKeyDown()) {
            if (itemstack.getOrCreateTag().getInt("commandMode") == 2 && !player.level.isClientSide) {
//                player.displayClientMessage(player.level.getBlockState(ctx.getClickedPos()).getBlock().getName(), true);

                List<CrowEntity> crows = new ArrayList<>();
                ListTag id = itemstack.getOrCreateTag().getList("crowList", Tag.TAG_COMPOUND);
                if(id.size() < 1)
                    return InteractionResult.FAIL;
                for (int i = 0; i < id.size(); i++) {
                    CompoundTag tag = id.getCompound(i);

                    if(tag.contains("UUID")) {
                        UUID crowId = tag.getUUID("UUID");
                        Entity entity = ((ServerLevel) player.level).getEntity(crowId);

                        if (entity instanceof CrowEntity) {
                            tag.putInt("ID", entity.getId());
                            crows.add((CrowEntity) entity);
                            ((CrowEntity) ((ServerLevel) player.level).getEntity(crowId)).setPerchPos(ctx.getClickedPos());
                        } else {
                            id.remove(i);
//                        crows.remove(entity);
                            i = 0;
                        }
                    }
                    else
                    {
                        id.remove(i);
                        i = 0;
                    }
                }

                if(crows.size() > 0) {
                    player.level.playSound(null, player.getX() + player.getLookAngle().x(), player.getY() + player.getEyeHeight(), player.getZ() + player.getLookAngle().z(), ModSounds.CROW_FLUTE.get(), SoundSource.PLAYERS, 1.0F, 0.8F + 0.4F * new Random().nextFloat());
                    player.getCooldowns().addCooldown(this, 20);
                }

                return InteractionResult.SUCCESS;
            }
        }

        return super.useOn(ctx);
    }

    public static ItemStack withColors(int color1, int color2) {
        ItemStack stack = new ItemStack(ModItems.CROW_FLUTE.get());
        stack.getOrCreateTag().putInt("dyeColor1", color1);
        stack.getOrCreateTag().putInt("dyeColor2", color2);

        return stack;
    }

    public interface ItemHandlerConsumer {
        void register(ItemColor handler, ItemLike... items);
    }

    public static int getColorValue(DyeColor color) {
        float[] colors = color.getTextureDiffuseColors();
        int r = (int) (colors[0] * 255.0F);
        int g = (int) (colors[1] * 255.0F);
        int b = (int) (colors[2] * 255.0F);
        return (r << 16) | (g << 8) | b;
    }

    public static DyeColor getColor1(ItemStack stack) {

        DyeColor col = HexereiUtil.getDyeColorNamed(stack.getHoverName().getString(), 0);

        return col == null ? DyeColor.byId(stack.getOrCreateTag().getInt("dyeColor1")) : col;

//        if(stack.getHoverName().getString().equals("jeb_"))
//            return (int)((Hexerei.getClientTicks() / 10) % 16);
//
//        if(stack.getHoverName().getString().equals("les_"))
//            return switch((int)(((Hexerei.getClientTicks())/6) % 15)) {
//                case 4, 5, 3 -> 1;
//                case 7, 8, 6 -> 2;
//                case 10, 11, 9 -> 6;
//                case 13, 14, 12 -> 14;
//                default -> 0;
//            };
//
//        if(stack.getHoverName().getString().equals("bi_"))
//            return switch((int)(((Hexerei.getClientTicks())/4) % 15)) {
//                case 6, 7, 8, 9, 5 -> 10;
//                case 11, 12, 13, 14, 10 -> 11;
//                default -> 2;
//            };
//
//        if(stack.getHoverName().getString().equals("trans_"))
//            return switch((int)(((Hexerei.getClientTicks())/4) % 16)) {
//                case 0, 1, 2, 3 -> 3;
//                case 9, 10, 11, 8 -> 0;
//                default -> 6;
//            };
//
//        if(stack.getHoverName().getString().equals("joe_"))
//            return switch((int)(((Hexerei.getClientTicks())/4) % 16)) {
//                case 0, 3, 2, 1 -> 3;
//                case 5, 4, 6, 7, 15, 12, 13, 14 -> 9;
//                default -> 11;
//            };
//
//        if(!stack.getOrCreateTag().contains("dyeColor1"))
//            return -1;
//        return stack.getOrCreateTag().getInt("dyeColor1");
    }

    public static DyeColor getColor2(ItemStack stack) {

        DyeColor col = HexereiUtil.getDyeColorNamed(stack.getHoverName().getString(), 0, 5);

        return col == null ? DyeColor.byId(stack.getOrCreateTag().getInt("dyeColor2")) : col;
//        if(stack.getHoverName().getString().equals("jeb_"))
//            return (int)(((Hexerei.getClientTicks() + 4)/10) % 16);
//
//        if(stack.getHoverName().getString().equals("les_"))
//            return switch((int)(((Hexerei.getClientTicks() + 4)/6) % 15)) {
//                case 4, 5, 6 -> 1;
//                case 7, 8, 9 -> 2;
//                case 10, 11, 12 -> 6;
//                case 13, 14, 15 -> 14;
//                default -> 0;
//            };
//
//        if(stack.getHoverName().getString().equals("bi_"))
//            return switch((int)(((Hexerei.getClientTicks() + 4)/4) % 15)) {
//                case 6, 7, 8, 9, 10 -> 10;
//                case 11, 12, 13, 14, 15 -> 11;
//                default -> 2;
//            };
//
//        if(stack.getHoverName().getString().equals("trans_"))
//            return switch((int)(((Hexerei.getClientTicks() + 4)/4) % 16)) {
//                case 1, 2, 3, 4 -> 3;
//                case 9, 10, 11, 12 -> 0;
//                default -> 6;
//            };
//
//        if(stack.getHoverName().getString().equals("joe_"))
//            return switch((int)(((Hexerei.getClientTicks() + 4)/4) % 16)) {
//                case 0, 3, 2, 1 -> 3;
//                case 5, 4, 6, 7, 15, 12, 13, 14 -> 9;
//                default -> 11;
//            };
//
//        if(!stack.getOrCreateTag().contains("dyeColor2"))
//            return -1;
//        return stack.getOrCreateTag().getInt("dyeColor2");
    }

    @Mod.EventBusSubscriber(value = Dist.CLIENT, modid = "hexerei", bus = Mod.EventBusSubscriber.Bus.MOD)
    private static class ColorRegisterHandler
    {
        @SubscribeEvent(priority = EventPriority.HIGHEST)
        public static void registerFluteColors(ColorHandlerEvent.Item event)
        {
            ItemHandlerConsumer items = event.getItemColors()::register;

            // s = stack, t = tint-layer
            items.register((s, t) -> t == 1 ? getColorValue(CrowFluteItem.getColor1(s)) : t == 2 ? getColorValue(CrowFluteItem.getColor2(s)) : -1, ModItems.CROW_FLUTE.get());

        }
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return stack.getOrCreateTag().getInt("commandMode") == 2 || stack.getOrCreateTag().getInt("commandMode") == 1;
    }


    @Override
    public UseAnim getUseAnimation(ItemStack pStack) {
        return UseAnim.NONE;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 72000;
    }
//    @Override
//    public boolean canAttackBlock(BlockState state, Level world, BlockPos pos,
//                                  Player p_195938_4_) {
//        return false;
//    }

    public InteractionResultHolder<ItemStack> use(Level level, Player playerIn, InteractionHand handIn) {
        ItemStack itemstack = playerIn.getItemInHand(handIn);

        playerIn.startUsingItem(handIn);
        if(!level.isClientSide){
            if(playerIn.isShiftKeyDown()) {

                ListTag id = itemstack.getOrCreateTag().getList("crowList", Tag.TAG_COMPOUND);
                for (int i = 0; i < id.size(); i++) {
                    CompoundTag tag = id.getCompound(i);
                    if(tag.contains("UUID")) {
                        UUID crowId = tag.getUUID("UUID");
                        Entity entity = ((ServerLevel) level).getEntity(crowId);
                        if (entity instanceof CrowEntity) {
                            tag.putInt("ID", entity.getId());
                        } else {
                            id.remove(i);
//                        crows.remove(entity);
                            i = 0;
                        }
                    }
                }

                MenuProvider containerProvider = createContainerProvider(itemstack, handIn, itemstack.getTag());

                NetworkHooks.openGui((ServerPlayer) playerIn, containerProvider, b -> b.writeNbt(itemstack.getTag()).writeInt(handIn == InteractionHand.MAIN_HAND ? 0 : 1));

            }
            else if (itemstack.getOrCreateTag().getInt("commandMode") == 0)
            {

                List<CrowEntity> crows = new ArrayList<>();
                ListTag id = itemstack.getOrCreateTag().getList("crowList", Tag.TAG_COMPOUND);
                for(int i = 0; i < id.size(); i++){
                    CompoundTag tag = id.getCompound(i);

                    UUID crowId = tag.getUUID("UUID");
                    tag.putInt("ID", ((ServerLevel) playerIn.level).getEntity(crowId).getId());
                    crows.add((CrowEntity) ((ServerLevel) playerIn.level).getEntity(crowId));
                }
                if(crows.size() < 1) {
                    crows = level.getEntitiesOfClass(CrowEntity.class, this.getTargetableArea(64, playerIn), this.targetEntitySelector);
                    crows.removeIf(crow -> !crow.isOwnedBy(playerIn));
                }

                if(crows.size() > 0) {

                    int selected = itemstack.getOrCreateTag().getInt("commandSelected");
                    if (selected == 0) {
                        playerIn.displayClientMessage(Component.translatable("entity.hexerei.crow_flute_set_message", crows.size(), crows.size() > 1 ? "s" : "", Component.translatable("entity.hexerei.crow_command_gui_0")), true);
                        for (CrowEntity crow : crows) {
                            if (crow.isOwnedBy(playerIn)) {
                                crow.setCommandFollow();
                            }
                        }
                    } else if (selected == 1) {
                        playerIn.displayClientMessage(Component.translatable("entity.hexerei.crow_flute_set_message", crows.size(), crows.size() > 1 ? "s" : "", Component.translatable("entity.hexerei.crow_command_gui_1")), true);
                        for (CrowEntity crow : crows) {
                            if (crow.isOwnedBy(playerIn)) {
                                crow.setCommandSit();
                            }
                        }
                    } else if (selected == 2) {
                        playerIn.displayClientMessage(Component.translatable("entity.hexerei.crow_flute_set_message", crows.size(), crows.size() > 1 ? "s" : "", Component.translatable("entity.hexerei.crow_command_gui_2")), true);
                        for (CrowEntity crow : crows) {
                            if (crow.isOwnedBy(playerIn)) {
                                crow.setCommandWander();
                            }
                        }
                    } else if (selected == 3) {
                        playerIn.displayClientMessage(Component.translatable("entity.hexerei.crow_flute_set_message", crows.size(), crows.size() > 1 ? "s" : "", Component.translatable("entity.hexerei.crow_command_gui_3")).append(" (").append(Component.translatable("entity.hexerei.crow_help_command_gui_" + itemstack.getOrCreateTag().getInt("helpCommandSelected"))).append(")"), true);
                        for (CrowEntity crow : crows) {
                            if (crow.isOwnedBy(playerIn)) {
                                crow.setHelpCommand(itemstack.getOrCreateTag().getInt("helpCommandSelected"));
                                crow.setCommandHelp();
                            }
                        }
                    }
                }
                level.playSound(null, playerIn.getX() + playerIn.getLookAngle().x(), playerIn.getY() + playerIn.getEyeHeight(), playerIn.getZ() + playerIn.getLookAngle().z(), ModSounds.CROW_FLUTE.get(), SoundSource.PLAYERS, 1.0F, 0.8F + 0.4F * new Random().nextFloat());
                playerIn.getCooldowns().addCooldown(this, 20);

                return InteractionResultHolder.success(itemstack);
            }
            else if (itemstack.getOrCreateTag().getInt("commandMode") == 1)
            {

                HitResult raytraceresult = getPlayerPOVHitResult(level, playerIn, ClipContext.Fluid.NONE);
                if(raytraceresult.getType() == HitResult.Type.ENTITY)
                {
                    Vec3 vector3d = playerIn.getLookAngle();
                    List<Entity> list = level.getEntities(playerIn, playerIn.getBoundingBox().expandTowards(vector3d.scale(5.0D)).inflate(1.0D), field_219989_a);
                    boolean flag = false;
                    for(Entity entity : list){
                        if(entity instanceof CrowEntity && ((CrowEntity) entity).isOwnedBy(playerIn)) {
                            flag = true;
                            break;
                        }
                    }
                    if (!flag){

//                        level.playSound(null, playerIn.getX() + playerIn.getLookAngle().x(), playerIn.getY() + playerIn.getEyeHeight(), playerIn.getZ() + playerIn.getLookAngle().z(), ModSounds.CROW_FLUTE_SELECT.get(), SoundSource.PLAYERS, 0.25F, 0.1F);
                        playerIn.getCooldowns().addCooldown(this, 5);

                    }
                }
                else
                {
//                    level.playSound(null, playerIn.getX() + playerIn.getLookAngle().x(), playerIn.getY() + playerIn.getEyeHeight(), playerIn.getZ() + playerIn.getLookAngle().z(), ModSounds.CROW_FLUTE_SELECT.get(), SoundSource.PLAYERS, 0.25F, 0.1F);
                    playerIn.getCooldowns().addCooldown(this, 5);
                }
            }
            else if (itemstack.getOrCreateTag().getInt("commandMode") == 2)
            {
//                return InteractionResultHolder.success(itemstack);
                HitResult raytraceresult = getPlayerPOVHitResult(level, playerIn, ClipContext.Fluid.NONE);
//                playerIn.
                if(raytraceresult.getType() == HitResult.Type.BLOCK)
                {
                    ListTag id = itemstack.getOrCreateTag().getList("crowList", Tag.TAG_COMPOUND);

                    if (id.size() < 1){
                        playerIn.displayClientMessage(Component.translatable("entity.hexerei.crow_flute_perch_message_fail_no_crows"), true);
//                        level.playSound(null, playerIn.getX() + playerIn.getLookAngle().x(), playerIn.getY() + playerIn.getEyeHeight(), playerIn.getZ() + playerIn.getLookAngle().z(), ModSounds.CROW_FLUTE_SELECT.get(), SoundSource.PLAYERS, 0.25F, 0.1F);
                        playerIn.getCooldowns().addCooldown(this, 5);
                    }
                    else
                        return InteractionResultHolder.success(itemstack);
                }
                else
                {
//                    level.playSound(null, playerIn.getX() + playerIn.getLookAngle().x(), playerIn.getY() + playerIn.getEyeHeight(), playerIn.getZ() + playerIn.getLookAngle().z(), ModSounds.CROW_FLUTE_SELECT.get(), SoundSource.PLAYERS, 0.25F, 0.1F);
                    playerIn.getCooldowns().addCooldown(this, 5);
                }
            }
            return InteractionResultHolder.fail(itemstack);
        }
        else
        {
            if(!playerIn.isShiftKeyDown()) {
                if (itemstack.getOrCreateTag().getInt("commandMode") == 1) {
//                return InteractionResultHolder.success(itemstack);
                    HitResult raytraceresult = getPlayerPOVHitResult(level, playerIn, ClipContext.Fluid.ANY);


                    if (raytraceresult.getType() != HitResult.Type.ENTITY) {
                        playerIn.displayClientMessage(Component.translatable("entity.hexerei.crow_flute_select_message_fail"), true);
                        playerIn.playSound(ModSounds.CROW_FLUTE_DESELECT.get(), 1, 0.1f);
                        return InteractionResultHolder.fail(itemstack);
                    }
                    if (raytraceresult.getType() == HitResult.Type.ENTITY) {
//                    return InteractionResultHolder.fail(itemstack);

                        Vec3 vector3d = playerIn.getLookAngle();
                        List<Entity> list = level.getEntities(playerIn, playerIn.getBoundingBox().expandTowards(vector3d.scale(5.0D)).inflate(1.0D), field_219989_a);
                        boolean flag = false;
                        for (Entity entity : list) {
                            if (entity instanceof CrowEntity && ((CrowEntity) entity).isOwnedBy(playerIn)) {
                                flag = true;
                                break;
                            }
                        }
                        if (!flag) {
                            playerIn.displayClientMessage(Component.translatable("entity.hexerei.crow_flute_select_message_fail"), true);
                            playerIn.playSound(ModSounds.CROW_FLUTE_DESELECT.get(), 1, 0.1f);
                            return InteractionResultHolder.fail(itemstack);
                        }
                        return InteractionResultHolder.success(itemstack);

                    }
                }
                    if (itemstack.getOrCreateTag().getInt("commandMode") == 2) {
//                return InteractionResultHolder.success(itemstack);
                        HitResult raytraceresult = getPlayerPOVHitResult(level, playerIn, ClipContext.Fluid.NONE);
                        if (raytraceresult.getType() != HitResult.Type.BLOCK) {
                            playerIn.displayClientMessage(Component.translatable("entity.hexerei.crow_flute_perch_message_fail_no_block"), true);
                            playerIn.playSound(ModSounds.CROW_FLUTE_DESELECT.get(), 1, 0.1f);
                            return InteractionResultHolder.fail(itemstack);
                        } else {

                        ListTag id = itemstack.getOrCreateTag().getList("crowList", Tag.TAG_COMPOUND);

                        if (id.size() < 1) {
                            playerIn.displayClientMessage(Component.translatable("entity.hexerei.crow_flute_perch_message_fail_no_crows"), true);
                            playerIn.playSound(ModSounds.CROW_FLUTE_DESELECT.get(), 1, 0.1f);
                            return InteractionResultHolder.fail(itemstack);
                        }
                    }
                }
            }
        }


        return InteractionResultHolder.success(itemstack);
    }


    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flagIn) {

        if(Screen.hasShiftDown()) {
            tooltip.add(Component.translatable("<%s>", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAA6600)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
            tooltip.add(Component.translatable("tooltip.hexerei.crow_flute_shift_1").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
            tooltip.add(Component.translatable("tooltip.hexerei.crow_flute_shift_2").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
            tooltip.add(Component.translatable("tooltip.hexerei.crow_flute_shift_3").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
            tooltip.add(Component.translatable("tooltip.hexerei.crow_flute_shift_4").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
            tooltip.add(Component.translatable("tooltip.hexerei.crow_flute_shift_5").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
        } else {
            tooltip.add(Component.translatable("[%s]", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAAAA00)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));

            if(stack.getOrCreateTag().contains("commandMode")) {
                String command = "";
                if (stack.getOrCreateTag().getInt("commandMode") == 0) {
                    if (stack.getOrCreateTag().getInt("commandSelected") == 0)
                        command = "entity.hexerei.crow_command_gui_0";
                    if (stack.getOrCreateTag().getInt("commandSelected") == 1)
                        command = "entity.hexerei.crow_command_gui_1";
                    if (stack.getOrCreateTag().getInt("commandSelected") == 2)
                        command = "entity.hexerei.crow_command_gui_2";
                    if (stack.getOrCreateTag().getInt("commandSelected") == 3) {
                        if (stack.getOrCreateTag().getInt("helpCommandSelected") == 0)
                            command = "entity.hexerei.crow_help_command_gui_0";
                        if (stack.getOrCreateTag().getInt("helpCommandSelected") == 1)
                            command = "entity.hexerei.crow_help_command_gui_1";
                        if (stack.getOrCreateTag().getInt("helpCommandSelected") == 2)
                            command = "entity.hexerei.crow_help_command_gui_2";
                    }

                } else if (stack.getOrCreateTag().getInt("commandMode") == 1) {
                    command = "entity.hexerei.crow_flute_perch";

                } else if (stack.getOrCreateTag().getInt("commandMode") == 2) {
                    command = "entity.hexerei.crow_flute_select";
                }

                tooltip.add(Component.translatable("-%s-", Component.translatable(command).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAA6600)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
            }
        }

        super.appendHoverText(stack, world, tooltip, flagIn);
    }

    public void setCommand(int command, ItemStack stack, Player player, InteractionHand hand) {

        if (player.level.isClientSide)
            HexereiPacketHandler.sendToServer(new CrowFluteCommandSyncToServer(stack, command, player.getUUID(), hand == InteractionHand.MAIN_HAND ? 0 : 1));

    }

    public void setHelpCommand(int helpCommand, ItemStack stack, Player player, InteractionHand hand) {

        if (player.level.isClientSide)
            HexereiPacketHandler.sendToServer(new CrowFluteHelpCommandSyncToServer(stack, helpCommand, player.getUUID(), hand == InteractionHand.MAIN_HAND ? 0 : 1));

    }

    public void setCommandMode(int mode, ItemStack stack, Player player, InteractionHand hand) {

        if (player.level.isClientSide)
            HexereiPacketHandler.sendToServer(new CrowFluteCommandModeSyncToServer(stack, mode, player.getUUID(), hand == InteractionHand.MAIN_HAND ? 0 : 1));

    }

    public void clearCrowList(ItemStack stack, Player player, InteractionHand hand) {

        if (player.level.isClientSide)
            HexereiPacketHandler.sendToServer(new CrowFluteClearCrowListToServer(stack, player.getUUID(), hand == InteractionHand.MAIN_HAND ? 0 : 1));

    }

    public void clearCrowPerch(ItemStack stack, Player player, InteractionHand hand) {

        if (player.level.isClientSide)
            HexereiPacketHandler.sendToServer(new CrowFluteClearCrowPerchToServer(stack, player.getUUID(), hand == InteractionHand.MAIN_HAND ? 0 : 1));

    }

    @Override
    public int getContainerSize() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public ItemStack getItem(int p_18941_) {
        return null;
    }

    @Override
    public ItemStack removeItem(int p_18942_, int p_18943_) {
        return null;
    }

    @Override
    public ItemStack removeItemNoUpdate(int p_18951_) {
        return null;
    }

    @Override
    public void setItem(int p_18944_, ItemStack p_18945_) {

    }

    @Override
    public void setChanged() {

    }

    @Override
    public boolean stillValid(Player p_18946_) {
        return false;
    }

    @Override
    public void clearContent() {

    }

    private MenuProvider createContainerProvider(ItemStack itemStack, InteractionHand hand, CompoundTag list) {
        return new MenuProvider() {
            @org.jetbrains.annotations.Nullable
            @Override
            public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player) {
                return new CrowFluteContainer(windowId, itemStack, inv, player, hand, list);
            }

            @Override
            public Component getDisplayName() {
                MutableComponent mutablecomponent = (new TextComponent("")).append(itemStack.getHoverName());
                if (itemStack.hasCustomHoverName()) {
                    mutablecomponent.withStyle(ChatFormatting.ITALIC);
                }

                return mutablecomponent;
            }

        };
    }

    public static float wrapRad(float pValue) {
        float p = (float) (Math.PI*2);
        float d0 = pValue % p;
        if (d0 >= Math.PI) {
            d0 -= p;
        }

        if (d0 < -Math.PI) {
            d0 += p;
        }

        return d0;
    }


    @OnlyIn(Dist.CLIENT)
    @Override
    public <T extends LivingEntity> boolean poseRightArm(ItemStack stack, HumanoidModel<T> model, T entity, HumanoidArm mainHand, TwoHandedItemAnimation twoHanded) {
        if (entity.getUseItemRemainingTicks() > 0 && entity.getUseItem().getItem() == this) {
            this.animateHands(model, entity, false);
            twoHanded.bool = true;
            return true;
        }
        return false;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public <T extends LivingEntity> boolean poseLeftArm(ItemStack stack, HumanoidModel<T> model, T entity, HumanoidArm mainHand, TwoHandedItemAnimation twoHanded) {
        if (entity.getUseItemRemainingTicks() > 0 && entity.getUseItem().getItem() == this) {
            this.animateHands(model, entity, true);
            twoHanded.bool = true;
            return true;
        }
        return false;
    }

    @OnlyIn(Dist.CLIENT)
    private <T extends LivingEntity> void animateHands(HumanoidModel<T> model, T entity, boolean leftHand) {

        ModelPart mainHand = leftHand ? model.leftArm : model.rightArm;
        ModelPart offHand = leftHand ? model.rightArm : model.leftArm;

        Vec3 bx = new Vec3(1, 0, 0);
        Vec3 by = new Vec3(0, 1, 0);
        Vec3 bz = new Vec3(0, 0, 1);

        float headXRot = wrapRad(model.head.xRot);
        float headYRot = wrapRad(model.head.yRot);

        //head rot + hand offset from flute
        float downFacingRot = Mth.clamp(headXRot, 0f, 0.8f);

        float xRot = getMaxHeadXRot(headXRot) - (entity.isCrouching() ? 1F : 0.0F)
                - 0.3f + downFacingRot * 0.5f;

        bx = bx.xRot(xRot);
        by = by.xRot(xRot);
        bz = bz.xRot(xRot);

        Vec3 armVec = new Vec3(0, 0, 1);

        float mirror = leftHand ? 1 : -1;

        //Rotate hand vector on y axis
        armVec = armVec.yRot(-0.99f * mirror);

        //change hand vector onto direction vector basis
        Vec3 newV = bx.scale(armVec.x).add(by.scale(armVec.y)).add(bz.scale(armVec.z));


        float yaw = (float) Math.atan2(-newV.x, newV.z);
        float len = (float) newV.length();

        float pitch = (float) Math.asin(newV.y / len);

        float yRot = (yaw + headYRot * 0.8f - 1.6f * mirror) - 0.5f * downFacingRot * mirror;
        mainHand.yRot = yRot;
        mainHand.xRot = (float) (pitch - Math.PI / 2f);


        offHand.yRot = yRot;
//        offHand.yRot = (float) Mth.clamp((wrapRad(mainHand.yRot) + 1 * mirror) * 0.2, -0.15, 0.15) + 1.1f * mirror;
        offHand.xRot = wrapRad(mainHand.xRot - 0.06f);


        //shoulder joint hackery
        float offset = leftHand ? -Mth.clamp(headYRot, -1, 0) :
                Mth.clamp(headYRot, 0, 1);

        // model.rightArm.x = -5.0F + offset * 2f;
        mainHand.z = -offset * 0.95f;

        // model.leftArm.x = -model.rightArm.x;
        // model.leftArm.z = -model.rightArm.z;

        //hax. unbobs left arm
        AnimationUtils.bobModelPart(model.leftArm, entity.tickCount, 1.0F);
        AnimationUtils.bobModelPart(model.rightArm, entity.tickCount, -1.0F);
    }


    public static float getMaxHeadXRot(float xRot) {
        return Mth.clamp(xRot, (-(float) Math.PI / 2.5F), ((float) Math.PI / 2F));
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public <T extends Player, M extends EntityModel<T> & ArmedModel & HeadedModel> void renderThirdPersonItem(
            M parentModel, LivingEntity entity, ItemStack stack, HumanoidArm humanoidArm,
            PoseStack poseStack, MultiBufferSource bufferSource, int light) {

        if (!stack.isEmpty()) {

            ItemTransforms.TransformType transform;

            poseStack.pushPose();

            boolean leftHand = humanoidArm == HumanoidArm.LEFT;
            // entity.swingTime == 0
            if (entity.getUseItem() == stack) {
                ModelPart head = parentModel.getHead();

                //hax
                float oldRot = head.xRot;
                head.xRot = getMaxHeadXRot(wrapRad(oldRot));
//                head.translateAndRotate(poseStack);
                poseStack.translate((double)(head.x / 16.0F), (double)(head.y / 16.0F), (double)(head.z / 16.0F));
                if (head.zRot != 0.0F) {
                    poseStack.mulPose(Vector3f.ZP.rotation(head.zRot/ 1.75f));
                }

                if (head.yRot != 0.0F) {
                    poseStack.mulPose(Vector3f.YP.rotation(head.yRot));
                }

                if (head.xRot != 0.0F) {
                    poseStack.mulPose(Vector3f.XP.rotation(head.xRot/ 1.75f));
                }

                head.xRot = oldRot;





//                parentModel.translateToHand(humanoidArm, poseStack);
                CustomHeadLayer.translateToHead(poseStack, false);
                poseStack.translate((leftHand ? -1 : 1) * 4f / 16f, -6 / 16f, -12 / 16f);
                poseStack.mulPose(Vector3f.YP.rotationDegrees(180+(head.yRot * ((float) Math.PI * 2F) * 10) + (leftHand ? -1 : 1) * 10));
                poseStack.mulPose(Vector3f.ZP.rotationDegrees( (leftHand ? 1 : -1) * 23));
//                poseStack.mulPose(Vector3f.ZP.rotationDegrees( (leftHand ? 1 : -1) * Hexerei.getClientTicks()));
//                System.out.println(Hexerei.getClientTicks());
//                poseStack.mulPose(Vector3f.XP.rotationDegrees(270 + (int)(Math.sin(Hexerei.getClientTicks()/ 10) * 25)));
//                poseStack.translate( 0 / 16f, -8 / 16f, -2 / 16f);
                poseStack.mulPose(Vector3f.XP.rotationDegrees((leftHand ? 1 : 0) * -90));
                poseStack.translate(0, 7f / 16f, 8f / 16f);


//                poseStack.translate(0, -4.25 / 16f, -8.5 / 16f);
//                if (leftHand) poseStack.mulPose(Vector3f.XP.rotationDegrees(-90));

                transform = ItemTransforms.TransformType.HEAD;
            } else {
                //default rendering
                parentModel.translateToHand(humanoidArm, poseStack);
                poseStack.mulPose(Vector3f.XP.rotationDegrees(-90.0F));
                poseStack.mulPose(Vector3f.YP.rotationDegrees(180.0F));

                poseStack.translate((float) (leftHand ? -1 : 1) / 16.0F, 0.125D, -0.625D);

                transform = leftHand ? ItemTransforms.TransformType.THIRD_PERSON_LEFT_HAND : ItemTransforms.TransformType.THIRD_PERSON_RIGHT_HAND;
            }

            Minecraft.getInstance().getItemInHandRenderer().renderItem(entity, stack, transform, leftHand, poseStack, bufferSource, light);

            poseStack.popPose();
        }
    }


    @OnlyIn(Dist.CLIENT)
    @Override
    public void animateItemFirstPerson(LivingEntity entity, ItemStack stack, InteractionHand hand, PoseStack matrixStack, float partialTicks, float pitch, float attackAnim, float handHeight) {
        //is using item
        if (entity.isUsingItem() && entity.getUseItemRemainingTicks() > 0 && entity.getUsedItemHand() == hand) {
            //bow anim
            int mirror = entity.getMainArm() == HumanoidArm.RIGHT ^ hand == InteractionHand.MAIN_HAND ? -1 : 1;

            matrixStack.translate(-0.4 * mirror, 0.2, 0);

            float timeLeft = (float) stack.getUseDuration() - ((float) entity.getUseItemRemainingTicks() - partialTicks + 1.0F);

            float sin = Mth.sin((timeLeft - 0.1F) * 1.3F);

            matrixStack.translate(0, sin * 0.0038F, 0);
            matrixStack.mulPose(Vector3f.ZN.rotationDegrees(90));

            matrixStack.scale(1.0F * mirror, -1.0F * mirror, -1.0F);
        }
    }

}