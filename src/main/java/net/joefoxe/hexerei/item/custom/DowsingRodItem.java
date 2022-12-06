package net.joefoxe.hexerei.item.custom;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.joefoxe.hexerei.util.HexereiPacketHandler;
import net.joefoxe.hexerei.util.message.DowsingRodUpdatePositionPacket;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
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
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.List;

public class DowsingRodItem extends Item {

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
        }
        else
        {
            if (this.swampMode)
                findSwamp(worldIn, playerIn);
            else
                findJungle(worldIn, playerIn);

        }
        if(!worldIn.isClientSide && this.nearestPos != null)
            HexereiPacketHandler.instance.send(PacketDistributor.TRACKING_CHUNK.with(() -> worldIn.getChunkAt(playerIn.blockPosition())), new DowsingRodUpdatePositionPacket(itemstack, this.nearestPos, this.swampMode));

        return InteractionResultHolder.pass(itemstack);
    }

    public void findSwamp(Level worldIn, Entity entity)
    {
        if(!worldIn.isClientSide){
            Biome biome = null;
            try {
                biome = entity.getServer().registryAccess().registryOrThrow(Registry.BIOME_REGISTRY).getOptional(Biomes.SWAMP).orElseThrow(() -> {
                    return ERROR_INVALID_BIOME.create(Biomes.SWAMP);
                });
            } catch (CommandSyntaxException e) {
                e.printStackTrace();
            }

            this.nearestPos = findNearestBiome(entity, new ResourceLocation("minecraft:swamp"));

        }
    }



    public void findJungle(Level worldIn, Entity entity)
    {
        if(!worldIn.isClientSide){
            Biome biome = null;
            try {
                biome = entity.getServer().registryAccess().registryOrThrow(Registry.BIOME_REGISTRY).getOptional(Biomes.JUNGLE).orElseThrow(() -> {
                    return ERROR_INVALID_BIOME.create(Biomes.SWAMP);
                });
            } catch (CommandSyntaxException e) {
                e.printStackTrace();
            }


            this.nearestPos = findNearestBiome(entity, new ResourceLocation("minecraft:jungle"));
//            this.nearestPos = ((ServerLevel) worldIn).findNearestBiome(biome, entity.blockPosition(), 6400, 8);

        }
    }


    @Nullable
    private BlockPos findNearestBiome(Entity player, ResourceLocation biome) {

        BlockPos pos1 = player.blockPosition();

        this.playerPos = player.blockPosition();
        // If we are in a valid spot, we point to ourselves.
        BlockPos coordinates = getChunkCoordinates(pos1, player.level, biome);
        if (coordinates != null) {
            angleI = 0;
            radiusI = 0;
            return pos1;
        }

        double angleDif = (2.0f * Math.asin(distBetweenChecks / (2.0 * distBetweenChecks * (radiusI + 1))));

        angleDif = 2.0 * Math.PI / Math.round(2.0 * Math.PI / angleDif);

        for (int i = 0; i < numOfChecks; i++) {

            double angle = angleDif * this.angleI;
            if (angle > 2.0 * Math.PI) {
                this.angleI = 0;
                this.radiusI++;
                if (this.radiusI > this.maxRadiusI) {
                    this.angleI = 0;
                    this.radiusI = 0;
                    this.playerPos = pos1;
                }
                return null;
            } else {
                this.angleI++;
            }

            BlockPos pos = playerPos.offset(Math.round((float) (distBetweenChecks * (radiusI + 1) * Math.cos(angle))), 0, Math.round((float) (distBetweenChecks * (radiusI + 1) * Math.sin(angle))));

            coordinates = getChunkCoordinates(pos, player.level, biome);
            if (coordinates != null) {
                angleI = 0;
                radiusI = 0;
                return coordinates;
            }
        }

        return null;
    }

    @Nullable
    private static BlockPos getChunkCoordinates(BlockPos pos, Level world, ResourceLocation biomes) {
        Biome biome;

        Biome biomeToFind = world.registryAccess().registry(Registry.BIOME_REGISTRY).get().get(biomes);
        if(biomeToFind == null)
            return null;

        biome = world.getBiome(pos).value();
        if (!biomeToFind.equals(biome)) {
            return null;
        }

        biome = world.getBiome(pos.offset(-searchOffset, 0, 0)).value();
        if (!biomeToFind.equals(biome)) {
            return null;
        }

        biome = world.getBiome(pos.offset(searchOffset, 0, 0)).value();
        if (!biomeToFind.equals(biome)) {
            return null;
        }

        biome = world.getBiome(pos.offset(0, 0, -searchOffset)).value();
        if (!biomeToFind.equals(biome)) {
            return null;
        }

        biome = world.getBiome(pos.offset(0, 0, searchOffset)).value();
        if (!biomeToFind.equals(biome)) {
            return null;
        }

        return pos;
    }



    //BlockPos blockpos1 = p_137843_.getLevel().findNearestBiome(biome, blockpos, 6400, 8);
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flagIn) {
        if(Screen.hasShiftDown()) {
            tooltip.add(Component.translatable("<%s>", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAA6600)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
            tooltip.add(Component.translatable("tooltip.hexerei.dowsing_rod_2").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
            tooltip.add(Component.translatable("tooltip.hexerei.dowsing_rod_3").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
            tooltip.add(Component.translatable("tooltip.hexerei.dowsing_rod_4").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
            tooltip.add(Component.translatable("tooltip.hexerei.dowsing_rod_5").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
        } else {
            tooltip.add(Component.translatable("[%s]", Component.translatable("tooltip.hexerei.shift").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0xAAAA00)))).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
            tooltip.add(Component.translatable("tooltip.hexerei.dowsing_rod").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x999999))));
        }


        super.appendHoverText(stack, world, tooltip, flagIn);
    }
}