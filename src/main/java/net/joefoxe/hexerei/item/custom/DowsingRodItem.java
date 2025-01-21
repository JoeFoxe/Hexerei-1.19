package net.joefoxe.hexerei.item.custom;

import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.datafixers.util.Pair;
import net.joefoxe.hexerei.util.HexereiPacketHandler;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.joefoxe.hexerei.util.ResourceOrTag;
import net.joefoxe.hexerei.util.message.DowsingRodUpdatePositionPacket;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.phys.HitResult;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;

public class DowsingRodItem extends Item {

    public static final TagKey<Biome> BT_SWAMP = createBiomeTag("has_structure/dark_coven_biomes");
    public BlockPos nearestPos = null;
    public boolean swampMode = true;

    private static final int distBetweenChecks = 30;
    private static final int searchOffset = 10;
    private static final int numOfChecks = 120;
    private static final int maxRadiusI = 600;
    private int radiusI = 0;
    private int angleI = 0;
    private BlockPos playerPos;

    public DowsingRodItem(Properties properties) {
        super(properties);
    }


    public static double angleDifference( double angle1, double angle2 )
    {
        double diff = ( angle2 - angle1 + 180 ) % 360 - 180;
        return diff < -180 ? diff + 360 : diff;
    }


    @Override
    public void inventoryTick(ItemStack p_41404_, Level world, Entity entity, int p_41407_, boolean p_41408_) {
        super.inventoryTick(p_41404_, world, entity, p_41407_, p_41408_);

        if(entity instanceof Player){
            if(this.nearestPos== null && ((Player) entity).getMainHandItem() == p_41404_ || ((Player) entity).getOffhandItem() == p_41404_) {
                if (this.swampMode)
                    findSwamp(world, entity);
                else
                    findJungle(world, entity);
            }
        }

    }


    public static final DynamicCommandExceptionType ERROR_INVALID_BIOME = new DynamicCommandExceptionType((p_137850_) -> {
        return Component.translatable("commands.locatebiome.invalid", p_137850_);
    });

    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        ItemStack itemstack = playerIn.getItemInHand(handIn);
        HitResult raytraceresult = getPlayerPOVHitResult(worldIn, playerIn, ClipContext.Fluid.ANY);


        if(playerIn.isSecondaryUseActive()) {
            if(!worldIn.isClientSide){
                playerIn.getCooldowns().addCooldown(this, 20);
                this.swampMode = !this.swampMode;

                String s = "display.hexerei.dowsing_rod_swamp";
                if (!this.swampMode)
                    s = "display.hexerei.dowsing_rod_jungle";

                if (this.swampMode)
                    findSwamp(worldIn, playerIn);
                else
                    findJungle(worldIn, playerIn);


                playerIn.displayClientMessage(Component.translatable(s), true);
            }
            playerIn.swing(handIn);
        }
        else
        {
            if (this.swampMode) {
                findSwamp(worldIn, playerIn);
                playerIn.displayClientMessage(Component.translatable("display.hexerei.dowsing_rod_swamp_new"), true);
            }
            else {
                findJungle(worldIn, playerIn);
                playerIn.displayClientMessage(Component.translatable("display.hexerei.dowsing_rod_jungle_new"), true);
            }
            playerIn.swing(handIn);

        }
        if((playerIn instanceof ServerPlayer serverPlayer) && this.nearestPos != null)
            HexereiPacketHandler.sendToPlayerClient(new DowsingRodUpdatePositionPacket(itemstack, this.nearestPos, this.swampMode), serverPlayer);

        return InteractionResultHolder.pass(itemstack);
    }

    public void findSwamp(Level worldIn, Entity entity)
    {
        if(worldIn instanceof ServerLevel serverLevel){
            ResourceOrTag<Biome> key = ResourceOrTag.get("#c:is_swamp", Registries.BIOME);
            Pair<BlockPos, Holder<Biome>> pair = serverLevel.findClosestBiome3d(key.holderPredicate(), entity.blockPosition(), 6400, 32, 64);
            if(pair != null)
                this.nearestPos = pair.getFirst();
        }
    }

//
//    private static int locateBiome(CommandSourceStack pSource, ResourceOrTagArgument.Result<Biome> pBiome) throws CommandSyntaxException {
//        BlockPos blockpos = BlockPos.containing(pSource.getPosition());
//        Stopwatch stopwatch = Stopwatch.createStarted(Util.TICKER);
//        ResourceOrTag<Biome> rot = ResourceOrTag.get("#c:is_swamp", Registries.BIOME);
//        Pair<BlockPos, Holder<Biome>> pair = pSource.getLevel().findClosestBiome3d(rot.asHolderPredicate(), blockpos, 6400, 32, 64);
//        stopwatch.stop();
//        if (pair == null) {
//            throw ERROR_BIOME_NOT_FOUND.create(pBiome.asPrintable());
//        } else {
//            return showLocateResult(pSource, pBiome, blockpos, pair, "commands.locate.biome.success", true, stopwatch.elapsed());
//        }
//    }


    public void findJungle(Level worldIn, Entity entity)
    {
        if(worldIn instanceof ServerLevel serverLevel){
            ResourceOrTag<Biome> key = ResourceOrTag.get("#minecraft:is_jungle", Registries.BIOME);
            Pair<BlockPos, Holder<Biome>> pair = serverLevel.findClosestBiome3d(key.holderPredicate(), entity.blockPosition(), 6400, 32, 64);
            if(pair != null)
                this.nearestPos = pair.getFirst();
        }
    }

    private static TagKey<Biome> createBiomeTag(String name) {
        return TagKey.create(Registries.BIOME, HexereiUtil.getResource(name));
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        if(Screen.hasShiftDown()) {
            tooltipComponents.add(Component.translatable("<%s>", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAA6600)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
            tooltipComponents.add(Component.translatable("tooltip.hexerei.dowsing_rod_2").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
            tooltipComponents.add(Component.translatable("tooltip.hexerei.dowsing_rod_3").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
            tooltipComponents.add(Component.translatable("tooltip.hexerei.dowsing_rod_4").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
            tooltipComponents.add(Component.translatable("tooltip.hexerei.dowsing_rod_5").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
        } else {
            tooltipComponents.add(Component.translatable("[%s]", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAAAA00)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
            tooltipComponents.add(Component.translatable("tooltip.hexerei.dowsing_rod").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
        }


        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }
}