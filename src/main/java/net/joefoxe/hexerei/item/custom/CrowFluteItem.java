package net.joefoxe.hexerei.item.custom;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.client.renderer.IFirstPersonItemAnimation;
import net.joefoxe.hexerei.client.renderer.IThirdPersonItemAnimation;
import net.joefoxe.hexerei.client.renderer.IThirdPersonItemRenderer;
import net.joefoxe.hexerei.client.renderer.TwoHandedItemAnimation;
import net.joefoxe.hexerei.client.renderer.entity.custom.CrowEntity;
import net.joefoxe.hexerei.container.CrowFluteContainer;
import net.joefoxe.hexerei.item.ModDataComponents;
import net.joefoxe.hexerei.item.ModItems;
import net.joefoxe.hexerei.item.data_components.FluteData;
import net.joefoxe.hexerei.sounds.ModSounds;
import net.joefoxe.hexerei.util.HexereiPacketHandler;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.joefoxe.hexerei.util.message.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.model.*;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
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
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

public class CrowFluteItem extends Item implements IThirdPersonItemAnimation,
        IThirdPersonItemRenderer, IFirstPersonItemAnimation {

    protected final Predicate<CrowEntity> targetEntitySelector;
    private static final Predicate<Entity> field_219989_a = EntitySelector.NO_SPECTATORS.and(Entity::canBeCollidedWith);
    public int commandSelected;
    public int helpCommandSelected;


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
    public void inventoryTick(ItemStack itemstack, Level level, Entity entity, int slotId, boolean isSelected) {
        FluteData data = itemstack.get(ModDataComponents.FLUTE);
        if (data == null) {
            data = FluteData.empty();
            itemstack.set(ModDataComponents.FLUTE, data);
        }

        if(!level.isClientSide){
            List<FluteData.CrowIds> list = data.crowList();
            List<FluteData.CrowIds> newList = new ArrayList<>();

            boolean flag = false;
            for (FluteData.CrowIds crowIds : list) {
                Entity crow = ((ServerLevel) level).getEntity(crowIds.uuid());
                if (crow instanceof CrowEntity && crow.getId() != crowIds.id()) {
                    newList.add(new FluteData.CrowIds(crowIds.uuid(), crow.getId()));
                    flag = true;
                }
            }
            if (flag) {
                FluteData newData = new FluteData(data.commandSelected(), data.helpCommandSelected(), data.commandMode(), newList, data.dyeColor1(), data.dyeColor2());
                itemstack.set(ModDataComponents.FLUTE, newData);
            }

        }

        super.inventoryTick(itemstack, level, entity, slotId, isSelected);
    }

    @Override
    public InteractionResult useOn(UseOnContext ctx) {

        Player player = ctx.getPlayer();
        ItemStack itemstack = ctx.getItemInHand();
        FluteData fluteData = itemstack.get(ModDataComponents.FLUTE);
        if(player != null && !player.isShiftKeyDown()) {
            if (fluteData != null && fluteData.commandMode() == 2) {

                List<CrowEntity> crows = new ArrayList<>();
                List<FluteData.CrowIds> ids = fluteData.crowList();
                if(ids.isEmpty())
                    return InteractionResult.FAIL;
                if(!player.level().isClientSide){
                    List<FluteData.CrowIds> newIds = ids.stream().filter((crowIds) -> ((ServerLevel) player.level()).getEntity(crowIds.uuid()) instanceof CrowEntity).toList();
                    fluteData = new FluteData(fluteData.commandSelected(), fluteData.helpCommandSelected(), fluteData.commandMode(), newIds, fluteData.dyeColor1(), fluteData.dyeColor2());
                    for (FluteData.CrowIds crowIds : newIds) {
                        Entity entity = ((ServerLevel) player.level()).getEntity(crowIds.uuid());
                        if (entity instanceof CrowEntity crow) {
                            crows.add(crow);
                            crow.setPerchPos(ctx.getClickedPos());
                        }
                    }

                    if (!crows.isEmpty()) {
                        player.level().playSound(null, player.getX() + player.getLookAngle().x(), player.getY() + player.getEyeHeight(), player.getZ() + player.getLookAngle().z(), ModSounds.CROW_FLUTE.get(), SoundSource.PLAYERS, 1.0F, 0.8F + 0.4F * new Random().nextFloat());
                        player.getCooldowns().addCooldown(this, 20);
                    }
                    itemstack.set(ModDataComponents.FLUTE, fluteData);
                }

                return InteractionResult.SUCCESS;
            }

        }

        return super.useOn(ctx);
    }

    public static ItemStack withColors(int color1, int color2) {
        ItemStack stack = new ItemStack(ModItems.CROW_FLUTE.get());
        stack.set(ModDataComponents.FLUTE, new FluteData(0, 0, 0, new ArrayList<>(), color1, color2));

        return stack;
    }

    public interface ItemHandlerConsumer {
        void register(ItemColor handler, ItemLike... items);
    }

    public static DyeColor getColor1(ItemStack stack) {

        DyeColor col = HexereiUtil.getDyeColorNamed(stack.getHoverName().getString(), 0);

        FluteData fluteData = stack.getOrDefault(ModDataComponents.FLUTE, FluteData.EMPTY);

        return col == null ? DyeColor.byId(fluteData.dyeColor1()) : col;

    }

    public static DyeColor getColor2(ItemStack stack) {

        DyeColor col = HexereiUtil.getDyeColorNamed(stack.getHoverName().getString(), 0);

        FluteData fluteData = stack.getOrDefault(ModDataComponents.FLUTE, FluteData.EMPTY);

        return col == null ? DyeColor.byId(fluteData.dyeColor2()) : col;
    }

    @EventBusSubscriber(value = Dist.CLIENT, modid = Hexerei.MOD_ID, bus = Bus.MOD)
    private static class ColorRegisterHandler
    {
        @SubscribeEvent(priority = EventPriority.HIGHEST)
        public static void registerFluteColors(RegisterColorHandlersEvent.Item event)
        {
            ItemHandlerConsumer items = event.getItemColors()::register;

            // s = stack, t = tint-layer
            items.register((s, t) -> t == 1 ? CrowFluteItem.getColor1(s).getTextureDiffuseColor() : t == 2 ? CrowFluteItem.getColor2(s).getTextureDiffuseColor() : -1, ModItems.CROW_FLUTE.get());

        }
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        int commandMode = stack.getOrDefault(ModDataComponents.FLUTE, FluteData.EMPTY).commandMode();
        return commandMode == 2 || commandMode == 1;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack pStack) {
        return UseAnim.NONE;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand handIn) {
        ItemStack itemstack = player.getItemInHand(handIn);

        player.startUsingItem(handIn);
        if(player instanceof ServerPlayer serverPlayer){
            FluteData fluteData = itemstack.getOrDefault(ModDataComponents.FLUTE, FluteData.EMPTY);
            if(player.isShiftKeyDown()) {


                List<FluteData.CrowIds> newIds = fluteData.crowList().stream().filter((crowIds) -> ((ServerLevel) player.level()).getEntity(crowIds.uuid()) instanceof CrowEntity).toList();
                fluteData = new FluteData(fluteData.commandSelected(), fluteData.helpCommandSelected(), fluteData.commandMode(), newIds, fluteData.dyeColor1(), fluteData.dyeColor2());

                itemstack.set(ModDataComponents.FLUTE, fluteData);

                MenuProvider containerProvider = createContainerProvider(itemstack, handIn);

                serverPlayer.openMenu(containerProvider, b -> b.writeInt(handIn == InteractionHand.MAIN_HAND ? 0 : 1));

            }
            else if (fluteData.commandMode() == 0)
            {

                List<FluteData.CrowIds> newIds = fluteData.crowList().stream().filter((crowIds) -> ((ServerLevel) player.level()).getEntity(crowIds.uuid()) instanceof CrowEntity).toList();
                fluteData = new FluteData(fluteData.commandSelected(), fluteData.helpCommandSelected(), fluteData.commandMode(), newIds, fluteData.dyeColor1(), fluteData.dyeColor2());
                List<CrowEntity> crows = new ArrayList<>();
                for(FluteData.CrowIds crowIds : fluteData.crowList()) {
                    crows.add((CrowEntity) ((ServerLevel) player.level()).getEntity(crowIds.uuid()));
                }
                if(crows.isEmpty()) {
                    crows = level.getEntitiesOfClass(CrowEntity.class, this.getTargetableArea(64, player), this.targetEntitySelector);
                    crows.removeIf(crow -> !crow.isOwnedBy(player));
                }

                if(!crows.isEmpty()) {

                    int selected = fluteData.commandSelected();
                    if (selected == 0) {
                        player.displayClientMessage(Component.translatable("entity.hexerei.crow_flute_set_message", crows.size(), crows.size() > 1 ? "s" : "", Component.translatable("entity.hexerei.crow_command_gui_0")), true);
                        for (CrowEntity crow : crows) {
                            if (crow.isOwnedBy(player)) {
                                crow.setCommandFollow();
                            }
                        }
                    } else if (selected == 1) {
                        player.displayClientMessage(Component.translatable("entity.hexerei.crow_flute_set_message", crows.size(), crows.size() > 1 ? "s" : "", Component.translatable("entity.hexerei.crow_command_gui_1")), true);
                        for (CrowEntity crow : crows) {
                            if (crow.isOwnedBy(player)) {
                                crow.setCommandSit();
                            }
                        }
                    } else if (selected == 2) {
                        player.displayClientMessage(Component.translatable("entity.hexerei.crow_flute_set_message", crows.size(), crows.size() > 1 ? "s" : "", Component.translatable("entity.hexerei.crow_command_gui_2")), true);
                        for (CrowEntity crow : crows) {
                            if (crow.isOwnedBy(player)) {
                                crow.setCommandWander();
                            }
                        }
                    } else if (selected == 3) {
                        player.displayClientMessage(Component.translatable("entity.hexerei.crow_flute_set_message", crows.size(), crows.size() > 1 ? "s" : "", Component.translatable("entity.hexerei.crow_command_gui_3")).append(" (").append(Component.translatable("entity.hexerei.crow_help_command_gui_" + itemstack.getOrDefault(ModDataComponents.FLUTE, FluteData.EMPTY).helpCommandSelected())).append(")"), true);
                        for (CrowEntity crow : crows) {
                            if (crow.isOwnedBy(player)) {
                                crow.setHelpCommand(itemstack.getOrDefault(ModDataComponents.FLUTE, FluteData.EMPTY).helpCommandSelected());
                                crow.setCommandHelp();
                            }
                        }
                    }
                }
                level.playSound(null, player.getX() + player.getLookAngle().x(), player.getY() + player.getEyeHeight(), player.getZ() + player.getLookAngle().z(), ModSounds.CROW_FLUTE.get(), SoundSource.PLAYERS, 1.0F, 0.8F + 0.4F * new Random().nextFloat());
                player.getCooldowns().addCooldown(this, 20);

                return InteractionResultHolder.success(itemstack);
            }
            else if (itemstack.getOrDefault(ModDataComponents.FLUTE, FluteData.EMPTY).commandMode() == 1)
            {

                HitResult raytraceresult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.NONE);
                if(raytraceresult.getType() == HitResult.Type.ENTITY)
                {
                    Vec3 vector3d = player.getLookAngle();
                    List<Entity> list = level.getEntities(player, player.getBoundingBox().expandTowards(vector3d.scale(5.0D)).inflate(1.0D), field_219989_a);
                    boolean flag = false;
                    for(Entity entity : list){
                        if(entity instanceof CrowEntity && ((CrowEntity) entity).isOwnedBy(player)) {
                            flag = true;
                            break;
                        }
                    }
                    if (!flag){

//                        level.playSound(null, player.getX() + player.getLookAngle().x(), player.getY() + player.getEyeHeight(), player.getZ() + player.getLookAngle().z(), ModSounds.CROW_FLUTE_SELECT.get(), SoundSource.PLAYERS, 0.25F, 0.1F);
                        player.getCooldowns().addCooldown(this, 5);

                    }
                }
                else
                {
//                    level.playSound(null, player.getX() + player.getLookAngle().x(), player.getY() + player.getEyeHeight(), player.getZ() + player.getLookAngle().z(), ModSounds.CROW_FLUTE_SELECT.get(), SoundSource.PLAYERS, 0.25F, 0.1F);
                    player.getCooldowns().addCooldown(this, 5);
                }
            }
            else if (itemstack.getOrDefault(ModDataComponents.FLUTE, FluteData.EMPTY).commandMode() == 2)
            {
//                return InteractionResultHolder.success(itemstack);
                HitResult raytraceresult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.NONE);
//                player.
                if(raytraceresult.getType() == HitResult.Type.BLOCK)
                {

                    if (itemstack.getOrDefault(ModDataComponents.FLUTE, FluteData.EMPTY).crowList().isEmpty()){
                        player.displayClientMessage(Component.translatable("entity.hexerei.crow_flute_perch_message_fail_no_crows"), true);
//                        level.playSound(null, player.getX() + player.getLookAngle().x(), player.getY() + player.getEyeHeight(), player.getZ() + player.getLookAngle().z(), ModSounds.CROW_FLUTE_SELECT.get(), SoundSource.PLAYERS, 0.25F, 0.1F);
                        player.getCooldowns().addCooldown(this, 5);
                    }
                    else
                        return InteractionResultHolder.success(itemstack);
                }
                else
                {
//                    level.playSound(null, player.getX() + player.getLookAngle().x(), player.getY() + player.getEyeHeight(), player.getZ() + player.getLookAngle().z(), ModSounds.CROW_FLUTE_SELECT.get(), SoundSource.PLAYERS, 0.25F, 0.1F);
                    player.getCooldowns().addCooldown(this, 5);
                }
            }
            return InteractionResultHolder.fail(itemstack);
        }
        else
        {
            if(!player.isShiftKeyDown()) {
                if (itemstack.getOrDefault(ModDataComponents.FLUTE, FluteData.EMPTY).commandMode() == 1) {
//                return InteractionResultHolder.success(itemstack);
                    HitResult raytraceresult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.ANY);


                    if (raytraceresult.getType() != HitResult.Type.ENTITY) {
                        player.displayClientMessage(Component.translatable("entity.hexerei.crow_flute_select_message_fail"), true);
                        player.playSound(ModSounds.CROW_FLUTE_DESELECT.get(), 1, 0.1f);
                        return InteractionResultHolder.fail(itemstack);
                    }
                    if (raytraceresult.getType() == HitResult.Type.ENTITY) {
//                    return InteractionResultHolder.fail(itemstack);

                        Vec3 vector3d = player.getLookAngle();
                        List<Entity> list = level.getEntities(player, player.getBoundingBox().expandTowards(vector3d.scale(5.0D)).inflate(1.0D), field_219989_a);
                        boolean flag = false;
                        for (Entity entity : list) {
                            if (entity instanceof CrowEntity && ((CrowEntity) entity).isOwnedBy(player)) {
                                flag = true;
                                break;
                            }
                        }
                        if (!flag) {
                            player.displayClientMessage(Component.translatable("entity.hexerei.crow_flute_select_message_fail"), true);
                            player.playSound(ModSounds.CROW_FLUTE_DESELECT.get(), 1, 0.1f);
                            return InteractionResultHolder.fail(itemstack);
                        }
                        return InteractionResultHolder.success(itemstack);

                    }
                }
                    if (itemstack.getOrDefault(ModDataComponents.FLUTE, FluteData.EMPTY).commandMode() == 2) {
//                return InteractionResultHolder.success(itemstack);
                        HitResult raytraceresult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.NONE);
                        if (raytraceresult.getType() != HitResult.Type.BLOCK) {
                            player.displayClientMessage(Component.translatable("entity.hexerei.crow_flute_perch_message_fail_no_block"), true);
                            player.playSound(ModSounds.CROW_FLUTE_DESELECT.get(), 1, 0.1f);
                            return InteractionResultHolder.fail(itemstack);
                        } else {

                        if (itemstack.getOrDefault(ModDataComponents.FLUTE, FluteData.EMPTY).crowList().isEmpty()) {
                            player.displayClientMessage(Component.translatable("entity.hexerei.crow_flute_perch_message_fail_no_crows"), true);
                            player.playSound(ModSounds.CROW_FLUTE_DESELECT.get(), 1, 0.1f);
                            return InteractionResultHolder.fail(itemstack);
                        }
                        return InteractionResultHolder.fail(itemstack);
                    }
                }
            }
        }


        return InteractionResultHolder.success(itemstack);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {

        if(Screen.hasShiftDown()) {
            tooltipComponents.add(Component.translatable("<%s>", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAA6600)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
            tooltipComponents.add(Component.translatable("tooltip.hexerei.crow_flute_shift_1").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
            tooltipComponents.add(Component.translatable("tooltip.hexerei.crow_flute_shift_2").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
            tooltipComponents.add(Component.translatable("tooltip.hexerei.crow_flute_shift_3").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
            tooltipComponents.add(Component.translatable("tooltip.hexerei.crow_flute_shift_4").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
            tooltipComponents.add(Component.translatable("tooltip.hexerei.crow_flute_shift_5").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
        } else {
            tooltipComponents.add(Component.translatable("[%s]", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAAAA00)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
            FluteData fluteData = stack.getOrDefault(ModDataComponents.FLUTE, FluteData.EMPTY);
            String command = "";
            if (fluteData.commandMode() == 0) {
                if (fluteData.commandSelected() == 0)
                    command = "entity.hexerei.crow_command_gui_0";
                if (fluteData.commandSelected() == 1)
                    command = "entity.hexerei.crow_command_gui_1";
                if (fluteData.commandSelected() == 2)
                    command = "entity.hexerei.crow_command_gui_2";
                if (fluteData.commandSelected() == 3) {
                    if (fluteData.helpCommandSelected() == 0)
                        command = "entity.hexerei.crow_help_command_gui_0";
                    if (fluteData.helpCommandSelected() == 1)
                        command = "entity.hexerei.crow_help_command_gui_1";
                    if (fluteData.helpCommandSelected() == 2)
                        command = "entity.hexerei.crow_help_command_gui_2";
                }

            } else if (fluteData.commandMode() == 1) {
                command = "entity.hexerei.crow_flute_perch";

            } else if (fluteData.commandMode() == 2) {
                command = "entity.hexerei.crow_flute_select";
            }

            tooltipComponents.add(Component.translatable("-%s-", Component.translatable(command).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAA6600)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
        }

        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }

    public void setCommand(int command, ItemStack stack, Player player, InteractionHand hand) {

        if (player.level().isClientSide)
            HexereiPacketHandler.sendToServer(new CrowFluteCommandSyncToServer(stack, command, player.getUUID(), hand == InteractionHand.MAIN_HAND ? 0 : 1));

    }

    public void setHelpCommand(int helpCommand, ItemStack stack, Player player, InteractionHand hand) {

        if (player.level().isClientSide)
            HexereiPacketHandler.sendToServer(new CrowFluteHelpCommandSyncToServer(stack, helpCommand, player.getUUID(), hand == InteractionHand.MAIN_HAND ? 0 : 1));

    }

    public void setCommandMode(int mode, ItemStack stack, Player player, InteractionHand hand) {

        if (player.level().isClientSide)
            HexereiPacketHandler.sendToServer(new CrowFluteCommandModeSyncToServer(stack, mode, player.getUUID(), hand == InteractionHand.MAIN_HAND ? 0 : 1));

    }

    public void clearCrowList(ItemStack stack, Player player, InteractionHand hand) {

        if (player.level().isClientSide)
            HexereiPacketHandler.sendToServer(new CrowFluteClearCrowListToServer(stack, player.getUUID(), hand == InteractionHand.MAIN_HAND ? 0 : 1));

    }

    public void clearCrowPerch(ItemStack stack, Player player, InteractionHand hand) {

        if (player.level().isClientSide)
            HexereiPacketHandler.sendToServer(new CrowFluteClearCrowPerchToServer(stack, player.getUUID(), hand == InteractionHand.MAIN_HAND ? 0 : 1));

    }


    private MenuProvider createContainerProvider(ItemStack itemStack, InteractionHand hand) {
        return new MenuProvider() {
            @org.jetbrains.annotations.Nullable
            @Override
            public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player) {
                return new CrowFluteContainer(windowId, inv, player, hand);
            }

            @Override
            public Component getDisplayName() {
                MutableComponent mutablecomponent = (Component.translatable("")).append(itemStack.getHoverName());

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


    @Override
    public <T extends LivingEntity> boolean poseRightArm(ItemStack stack, HumanoidModel<T> model, T entity, HumanoidArm mainHand, TwoHandedItemAnimation twoHanded) {
        if (entity.getUseItemRemainingTicks() > 0 && entity.getUseItem().getItem() == this) {
            this.animateHands(model, entity, false);
            twoHanded.bool = true;
            return true;
        }
        return false;
    }

    @Override
    public <T extends LivingEntity> boolean poseRightArmMixin(ItemStack stack, AgeableListModel<T> model, T entity, HumanoidArm mainHand, TwoHandedItemAnimation twoHanded) {
        return IThirdPersonItemAnimation.super.poseRightArmMixin(stack, model, entity, mainHand, twoHanded);
    }

    @Override
    public <T extends LivingEntity> boolean poseLeftArm(ItemStack stack, HumanoidModel<T> model, T entity, HumanoidArm mainHand, TwoHandedItemAnimation twoHanded) {
        if (entity.getUseItemRemainingTicks() > 0 && entity.getUseItem().getItem() == this) {
            this.animateHands(model, entity, true);
            twoHanded.bool = true;
            return true;
        }
        return false;
    }

    @Override
    public <T extends LivingEntity> boolean poseleftArmMixin(ItemStack stack, AgeableListModel<T> model, T entity, HumanoidArm mainHand, TwoHandedItemAnimation twoHanded) {
        return IThirdPersonItemAnimation.super.poseleftArmMixin(stack, model, entity, mainHand, twoHanded);
    }

    @Override
    public boolean isTwoHanded() {
        return IThirdPersonItemAnimation.super.isTwoHanded();
    }

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

    @Override
    public <T extends Player, M extends EntityModel<T> & ArmedModel & HeadedModel> void renderThirdPersonItem(
            M parentModel, LivingEntity entity, ItemStack stack, HumanoidArm humanoidArm,
            PoseStack poseStack, MultiBufferSource bufferSource, int light) {

        if (!stack.isEmpty()) {

            ItemDisplayContext transform;

            poseStack.pushPose();

            boolean leftHand = humanoidArm == HumanoidArm.LEFT;
            // entity.swingTime == 0
            if (entity.getUseItem() == stack) {
                ModelPart head = parentModel.getHead();

                //hax
                float oldRot = head.xRot;
                head.xRot = getMaxHeadXRot(wrapRad(oldRot));
//                head.translateAndRotate(poseStack);
                poseStack.translate(head.x / 16.0F, head.y / 16.0F, head.z / 16.0F);
                if (head.zRot != 0.0F) {
                    poseStack.mulPose(Axis.ZP.rotation(head.zRot/ 1.75f));
                }

                if (head.yRot != 0.0F) {
                    poseStack.mulPose(Axis.YP.rotation(head.yRot));
                }

                if (head.xRot != 0.0F) {
                    poseStack.mulPose(Axis.XP.rotation(head.xRot/ 1.75f));
                }

                head.xRot = oldRot;





//                parentModel.translateToHand(humanoidArm, poseStack);
                CustomHeadLayer.translateToHead(poseStack, false);
                poseStack.translate((leftHand ? -1 : 1) * 4f / 16f, -6 / 16f, -12 / 16f);
                poseStack.mulPose(Axis.YP.rotationDegrees(180+(head.yRot * ((float) Math.PI * 2F) * 10) + (leftHand ? -1 : 1) * 10));
                poseStack.mulPose(Axis.ZP.rotationDegrees( (leftHand ? 1 : -1) * 23));
//                poseStack.mulPose(Axis.ZP.rotationDegrees( (leftHand ? 1 : -1) * ClientEvents.getClientTicks()));
//                System.out.println(ClientEvents.getClientTicks());
//                poseStack.mulPose(Axis.XP.rotationDegrees(270 + (int)(Math.sin(ClientEvents.getClientTicks()/ 10) * 25)));
//                poseStack.translate( 0 / 16f, -8 / 16f, -2 / 16f);
                poseStack.mulPose(Axis.XP.rotationDegrees((leftHand ? 1 : 0) * -90));
                poseStack.translate(0, 7f / 16f, 8f / 16f);


//                poseStack.translate(0, -4.25 / 16f, -8.5 / 16f);
//                if (leftHand) poseStack.mulPose(Axis.XP.rotationDegrees(-90));

                transform = ItemDisplayContext.HEAD;
            } else {
                //default rendering
                parentModel.translateToHand(humanoidArm, poseStack);
                poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F));
                poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));

                poseStack.translate((float) (leftHand ? -1 : 1) / 16.0F, 0.125D, -0.625D);

                transform = leftHand ? ItemDisplayContext.THIRD_PERSON_LEFT_HAND : ItemDisplayContext.THIRD_PERSON_RIGHT_HAND;
            }

            Minecraft.getInstance().gameRenderer.itemInHandRenderer.renderItem(entity, stack, transform, leftHand, poseStack, bufferSource, light);

            poseStack.popPose();
        }
    }


    @Override
    public void animateItemFirstPerson(LivingEntity entity, ItemStack stack, InteractionHand hand, PoseStack matrixStack, float partialTicks, float pitch, float attackAnim, float handHeight) {
        //is using item
        if (entity.isUsingItem() && entity.getUseItemRemainingTicks() > 0 && entity.getUsedItemHand() == hand) {
            //bow anim
            int mirror = entity.getMainArm() == HumanoidArm.RIGHT ^ hand == InteractionHand.MAIN_HAND ? -1 : 1;

            matrixStack.translate(-0.4 * mirror, 0.2, 0);

            float timeLeft = (float) stack.getUseDuration(entity) - ((float) entity.getUseItemRemainingTicks() - partialTicks + 1.0F);

            float sin = Mth.sin((timeLeft - 0.1F) * 1.3F);

            matrixStack.translate(0, sin * 0.0038F, 0);
            matrixStack.mulPose(Axis.ZN.rotationDegrees(90));

            matrixStack.scale(1.0F * mirror, -1.0F * mirror, -1.0F);
        }
    }

}