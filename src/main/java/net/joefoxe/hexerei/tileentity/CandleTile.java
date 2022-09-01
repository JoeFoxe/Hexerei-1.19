package net.joefoxe.hexerei.tileentity;

import net.joefoxe.hexerei.block.custom.Candle;
import net.joefoxe.hexerei.data.candle.AbstractCandleEffect;
import net.joefoxe.hexerei.data.candle.BonemealingCandleEffect;
import net.joefoxe.hexerei.data.candle.CandleData;
import net.joefoxe.hexerei.item.ModItems;
import net.joefoxe.hexerei.util.HexereiPacketHandler;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.joefoxe.hexerei.util.message.TESyncPacket;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraftforge.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.Random;

public class CandleTile extends BlockEntity {

    public NonNullList<CandleData> candles = NonNullList.withSize(4, new CandleData(Candle.BASE_COLOR, false,0,0, 0, 0, CandleData.meltTimerMAX, new BonemealingCandleEffect()));

    public boolean litStateOld;

    public int numberOfCandles;
    public int redstoneAnalogSignal;
    public int redstoneBases;
    public int candleMeltTimerMAX = 6000;
    private boolean startupFlag;

    public Component customName;

    public CandleTile(BlockEntityType<?> tileEntityTypeIn, BlockPos blockPos, BlockState blockState) {
        super(tileEntityTypeIn, blockPos, blockState);
        for(int i = 0; i < candles.size(); i++)
            candles.set(i, new CandleData(Candle.BASE_COLOR, false,0,0, 0, 0, CandleData.meltTimerMAX, new AbstractCandleEffect()));
        startupFlag = false;
        numberOfCandles = 0;
        litStateOld = false;
    }

    public CandleTile(BlockPos blockPos, BlockState blockState) {
        this(ModTileEntities.CANDLE_TILE.get(),blockPos, blockState);
    }

    @Override
    public void load(CompoundTag nbt) {


        if (nbt.contains("candle0", Tag.TAG_COMPOUND)){
            if (nbt.contains("candle0"))
                candles.get(0).load(nbt.getCompound("candle0"));
            if (nbt.contains("candle1"))
                candles.get(1).load(nbt.getCompound("candle1"));
            if (nbt.contains("candle2"))
                candles.get(2).load(nbt.getCompound("candle2"));
            if (nbt.contains("candle3"))
                candles.get(3).load(nbt.getCompound("candle3"));
        }
        else
        {
            /// Legacy loading
            if (nbt.contains("candleType1", Tag.TAG_INT))
                candles.get(0).hasCandle = nbt.getInt("candleType1") != 0;
            if (nbt.contains("candleType2", Tag.TAG_INT))
                candles.get(1).hasCandle = nbt.getInt("candleType2") != 0;
            if (nbt.contains("candleType3", Tag.TAG_INT))
                candles.get(2).hasCandle = nbt.getInt("candleType3") != 0;
            if (nbt.contains("candleType4", Tag.TAG_INT))
                candles.get(3).hasCandle = nbt.getInt("candleType4") != 0;
            if (nbt.contains("candleType1", Tag.TAG_INT)) {
                int type = nbt.getInt("candleType1");
                if(type == 2)
                    candles.get(0).dyeColor = HexereiUtil.getColorValue(DyeColor.BLUE);
                if(type == 3)
                    candles.get(0).dyeColor = HexereiUtil.getColorValue(DyeColor.BLACK);
                if(type == 4)
                    candles.get(0).dyeColor = HexereiUtil.getColorValue(DyeColor.LIME);
                if(type == 5)
                    candles.get(0).dyeColor = HexereiUtil.getColorValue(DyeColor.ORANGE);
                if(type == 6)
                    candles.get(0).dyeColor = HexereiUtil.getColorValue(DyeColor.PINK);
                if(type == 7)
                    candles.get(0).dyeColor = HexereiUtil.getColorValue(DyeColor.PURPLE);
                if(type == 8)
                    candles.get(0).dyeColor = HexereiUtil.getColorValue(DyeColor.RED);
                if(type == 9)
                    candles.get(0).dyeColor = HexereiUtil.getColorValue(DyeColor.CYAN);
                if(type == 10)
                    candles.get(0).dyeColor = HexereiUtil.getColorValue(DyeColor.YELLOW);
            }
            if (nbt.contains("candleType2", Tag.TAG_INT)) {
                int type = nbt.getInt("candleType2");
                if(type == 2)
                    candles.get(1).dyeColor = HexereiUtil.getColorValue(DyeColor.BLUE);
                if(type == 3)
                    candles.get(1).dyeColor = HexereiUtil.getColorValue(DyeColor.BLACK);
                if(type == 4)
                    candles.get(1).dyeColor = HexereiUtil.getColorValue(DyeColor.LIME);
                if(type == 5)
                    candles.get(1).dyeColor = HexereiUtil.getColorValue(DyeColor.ORANGE);
                if(type == 6)
                    candles.get(1).dyeColor = HexereiUtil.getColorValue(DyeColor.PINK);
                if(type == 7)
                    candles.get(1).dyeColor = HexereiUtil.getColorValue(DyeColor.PURPLE);
                if(type == 8)
                    candles.get(1).dyeColor = HexereiUtil.getColorValue(DyeColor.RED);
                if(type == 9)
                    candles.get(1).dyeColor = HexereiUtil.getColorValue(DyeColor.CYAN);
                if(type == 10)
                    candles.get(1).dyeColor = HexereiUtil.getColorValue(DyeColor.YELLOW);
            }
            if (nbt.contains("candleType3", Tag.TAG_INT)) {
                int type = nbt.getInt("candleType3");
                if(type == 2)
                    candles.get(2).dyeColor = HexereiUtil.getColorValue(DyeColor.BLUE);
                if(type == 3)
                    candles.get(2).dyeColor = HexereiUtil.getColorValue(DyeColor.BLACK);
                if(type == 4)
                    candles.get(2).dyeColor = HexereiUtil.getColorValue(DyeColor.LIME);
                if(type == 5)
                    candles.get(2).dyeColor = HexereiUtil.getColorValue(DyeColor.ORANGE);
                if(type == 6)
                    candles.get(2).dyeColor = HexereiUtil.getColorValue(DyeColor.PINK);
                if(type == 7)
                    candles.get(2).dyeColor = HexereiUtil.getColorValue(DyeColor.PURPLE);
                if(type == 8)
                    candles.get(2).dyeColor = HexereiUtil.getColorValue(DyeColor.RED);
                if(type == 9)
                    candles.get(2).dyeColor = HexereiUtil.getColorValue(DyeColor.CYAN);
                if(type == 10)
                    candles.get(2).dyeColor = HexereiUtil.getColorValue(DyeColor.YELLOW);
            }
            if (nbt.contains("candleType4", Tag.TAG_INT)) {
                int type = nbt.getInt("candleType4");
                if(type == 2)
                    candles.get(3).dyeColor = HexereiUtil.getColorValue(DyeColor.BLUE);
                if(type == 3)
                    candles.get(3).dyeColor = HexereiUtil.getColorValue(DyeColor.BLACK);
                if(type == 4)
                    candles.get(3).dyeColor = HexereiUtil.getColorValue(DyeColor.LIME);
                if(type == 5)
                    candles.get(3).dyeColor = HexereiUtil.getColorValue(DyeColor.ORANGE);
                if(type == 6)
                    candles.get(3).dyeColor = HexereiUtil.getColorValue(DyeColor.PINK);
                if(type == 7)
                    candles.get(3).dyeColor = HexereiUtil.getColorValue(DyeColor.PURPLE);
                if(type == 8)
                    candles.get(3).dyeColor = HexereiUtil.getColorValue(DyeColor.RED);
                if(type == 9)
                    candles.get(3).dyeColor = HexereiUtil.getColorValue(DyeColor.CYAN);
                if(type == 10)
                    candles.get(3).dyeColor = HexereiUtil.getColorValue(DyeColor.YELLOW);
            }
            if (nbt.contains("candleHeight1", Tag.TAG_INT))
                candles.get(0).height = nbt.getInt("candleHeight1");
            else
                candles.get(0).height = 7;
            if (nbt.contains("candleHeight2", Tag.TAG_INT))
                candles.get(1).height = nbt.getInt("candleHeight2");
            else
                candles.get(1).height = 7;
            if (nbt.contains("candleHeight3", Tag.TAG_INT))
                candles.get(2).height = nbt.getInt("candleHeight3");
            else
                candles.get(2).height = 7;
            if (nbt.contains("candleHeight4", Tag.TAG_INT))
                candles.get(3).height = nbt.getInt("candleHeight4");
            else
                candles.get(3).height = 7;
            if (nbt.contains("candleLit1", Tag.TAG_BYTE))
                candles.get(0).lit = nbt.getBoolean("candleLit1");
            else
                candles.get(0).lit = false;
            if (nbt.contains("candleLit2", Tag.TAG_BYTE))
                candles.get(1).lit = nbt.getBoolean("candleLit2");
            else
                candles.get(1).lit = false;
            if (nbt.contains("candleLit3", Tag.TAG_BYTE))
                candles.get(2).lit = nbt.getBoolean("candleLit3");
            else
                candles.get(2).lit = false;
            if (nbt.contains("candleLit4", Tag.TAG_BYTE))
                candles.get(3).lit = nbt.getBoolean("candleLit4");
            else
                candles.get(3).lit = false;
            if (nbt.contains("candleMeltTimer1", Tag.TAG_INT))
                candles.get(0).meltTimer = nbt.getInt("candleMeltTimer1");
            if (nbt.contains("candleMeltTimer2", Tag.TAG_INT))
                candles.get(1).meltTimer = nbt.getInt("candleMeltTimer2");
            if (nbt.contains("candleMeltTimer3", Tag.TAG_INT))
                candles.get(2).meltTimer = nbt.getInt("candleMeltTimer3");
            if (nbt.contains("candleMeltTimer4", Tag.TAG_INT))
                candles.get(3).meltTimer = nbt.getInt("candleMeltTimer4");
        }
        super.load(nbt);

    }

    @Override
    public void saveAdditional(CompoundTag compound) {
        compound.putInt("effectCooldown", candles.get(0).cooldown);
        compound.put("candle0", candles.get(0).save());
        compound.put("candle1", candles.get(1).save());
        compound.put("candle2", candles.get(2).save());
        compound.put("candle3", candles.get(3).save());
    }

//    @Override
    public CompoundTag save(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("effectCooldown", candles.get(0).cooldown);
        tag.put("candle0", candles.get(0).save());
        tag.put("candle1", candles.get(1).save());
        tag.put("candle2", candles.get(2).save());
        tag.put("candle3", candles.get(3).save());
        return tag;
    }

    public Component getCustomName() {
        return getCustomName(0);
    }
    public Component getCustomName(int slot) {
        return this.candles.get(slot).customName;
    }
    public int getDyeColor(int slot) {
        return this.candles.get(slot).dyeColor;
    }

    public int getDyeColor() {
        return getDyeColor(0);
    }

    public boolean hasCustomName() {
        return customName != null;
    }


    @Override
    public CompoundTag getUpdateTag()
    {
        return this.save(new CompoundTag());
    }

    @Nullable
    public Packet<ClientGamePacketListener> getUpdatePacket() {

        return ClientboundBlockEntityDataPacket.create(this, (tag) -> this.getUpdateTag());
    }

    @Override
    public void onDataPacket(final Connection net, final ClientboundBlockEntityDataPacket pkt)
    {
        this.deserializeNBT(pkt.getTag());
    }


    public static double getDistanceToEntity(Entity entity, BlockPos pos) {
        double deltaX = entity.getX() - pos.getX();
        double deltaY = entity.getY() - pos.getY();
        double deltaZ = entity.getZ() - pos.getZ();

        return Math.sqrt((deltaX * deltaX) + (deltaY * deltaY) + (deltaZ * deltaZ));
    }


    private float moveTo(float input, float movedTo, float speed)
    {
        float distance = movedTo - input;

        if(Math.abs(distance) <= speed)
        {
            return movedTo;
        }

        if(distance > 0)
        {
            input += speed;
        } else {
            input -= speed;
        }

        return input;
    }

//    @Override
//    public double getMaxRenderDistanceSquared() {
//        return 4096D;
//    }

    @Override
    public AABB getRenderBoundingBox() {
        AABB aabb = super.getRenderBoundingBox().inflate(25, 25, 25);
        return aabb;
    }

    public void sync() {

        if(level != null){
            if (!level.isClientSide)
                HexereiPacketHandler.instance.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(worldPosition)), new TESyncPacket(worldPosition, save(new CompoundTag())));

            if (this.level != null)
                this.level.sendBlockUpdated(this.worldPosition, this.level.getBlockState(this.worldPosition), this.level.getBlockState(this.worldPosition),
                        Block.UPDATE_CLIENTS);
        }
    }


    public void setDyeColor(int dyeColor){
        this.candles.get(0).dyeColor = dyeColor;
    }
    public void setHeight(int height){
        this.candles.get(0).height = height;
    }
    public void setDyeColor(int candle, int dyeColor){
        this.candles.get(Math.max(0, Math.min(candle, 3))).dyeColor = dyeColor;
    }
    public void setHeight(int candle, int height){
        this.candles.get(Math.max(0, Math.min(candle, 3))).height = height;
    }


    @Override
    public void setChanged() {
        super.setChanged();
        sync();
    }

    public int updateAnalog(){
        int temp = 0;
        int level_of_candles = 0;
        for (int i = 0; i < 4; i++)
            if (candles.get(i).hasCandle)
                level_of_candles += candles.get(i).height;

        float candles = level_of_candles;
        float max = 28;
        float percent = (candles / max);
        temp += (int) Math.ceil(percent * 15);

        if(this.redstoneAnalogSignal != temp) {

            this.redstoneAnalogSignal = temp;
            for(Direction direction : Direction.values()) {
                level.updateNeighborsAt(getBlockPos().relative(direction), this.getBlockState().getBlock());
            }
        }


        if (this.level != null)
            this.level.sendBlockUpdated(this.worldPosition, this.level.getBlockState(this.worldPosition), this.level.getBlockState(this.worldPosition),
                    Block.UPDATE_CLIENTS);

        return temp;

    }




    public void entityInside(Entity entity) {
        BlockPos blockpos = this.getBlockPos();
        if (entity instanceof Projectile projectile) {
            if (Shapes.joinIsNotEmpty(Shapes.create(entity.getBoundingBox().move((double)(-blockpos.getX()), (double)(-blockpos.getY()), (double)(-blockpos.getZ()))), Candle.getShape(getBlockState()), BooleanOp.AND)) {
                if(projectile.isOnFire() && level != null){
                    if (candles.get(0).hasCandle)
                        candles.get(0).lit = true;
                    if (candles.get(1).hasCandle)
                        candles.get(1).lit = true;
                    if (candles.get(2).hasCandle)
                        candles.get(2).lit = true;
                    if (candles.get(3).hasCandle)
                        candles.get(3).lit = true;
                }
            }
        }
    }


//    @Override
    public void tick() {
        Random random = new Random();

        int candlesLit = 0;




        if(level != null && level.getBlockEntity(worldPosition) instanceof CandleTile)
        {
            if (candles.get(0).getEffect() != null) {
                candles.get(0).getEffect().tick(level, this, candles.get(0));
            }
            if (candles.get(1).getEffect() != null)
                candles.get(1).getEffect().tick(level, this, candles.get(1));
            if (candles.get(2).getEffect() != null)
                candles.get(2).getEffect().tick(level, this, candles.get(2));
            if (candles.get(3).getEffect() != null)
                candles.get(3).getEffect().tick(level, this, candles.get(3));
            if(candles.get(0).lit)
                candlesLit++;
            if(candles.get(1).lit)
                candlesLit++;
            if(candles.get(2).lit)
                candlesLit++;
            if(candles.get(3).lit)
                candlesLit++;
        }

        BlockState state = level.getBlockState(worldPosition);
        if(state.hasProperty(Candle.CANDLES_LIT))
            level.setBlock(worldPosition, state.setValue(Candle.CANDLES_LIT, candlesLit).setValue(Candle.LIT, candlesLit > 0), 3);


        {
            int temp = 0;
            int level_of_candles = 0;
            for (int i = 0; i < 4; i++)
                if (candles.get(i).hasCandle)
                    level_of_candles += candles.get(i).height;

            float candles = level_of_candles;
            float max = 28;
            float percent = (candles / max);
            temp += (int) Math.ceil(percent * 15);

            if(this.redstoneAnalogSignal != temp) {

                this.redstoneAnalogSignal = temp;
                for(Direction direction : Direction.values()) {
                    level.updateNeighborsAt(getBlockPos().relative(direction), this.getBlockState().getBlock());
                }
            }

        }

//        if(state.hasProperty(Candle.SIGNAL)) {
//            int level_of_candles = 0;
//            for(int i = 0; i < 4; i++){
//                if (candles.get(i).hasCandle) {
//                    level_of_candles += candles.get(i).height;
//                }
//            }
//
//            float candles = level_of_candles;
//            float max = 28;
//            float percent = (candles/max);
//            int toReturn = (int)Math.ceil(percent * 15);
//            if(state.getValue(Candle.SIGNAL) != toReturn)
//                level.setBlock(worldPosition, state.setValue(Candle.SIGNAL, toReturn), 3);
//        }

        {

            int hasRedstoneBase = 0;
            for(int i = 0; i < 4; i++){
                if (candles.get(i).base.layer != null && candles.get(i).base.layer.toString().equals("minecraft:textures/block/redstone_block.png")) {
                    hasRedstoneBase += 1;
                }
            }
            if(this.redstoneBases != hasRedstoneBase) {
                this.redstoneBases = hasRedstoneBase;
                float percent = hasRedstoneBase / 4f;
                int redstonValue = (int) Math.ceil(percent * 15);
                level.setBlock(worldPosition, state.setValue(Candle.POWER, redstonValue), 3);
                for (Direction direction : Direction.values()) {
                    level.updateNeighborsAt(getBlockPos().relative(direction), getBlockState().getBlock());
                }
            }
        }

        if(level.isClientSide) {
            for(CandleData candleData : candles){

                if (candleData.returnToBlock) {
                    candleData.x = moveTo(candleData.x, 0, 0.125f);
                    candleData.y = moveTo(candleData.y, 0, 0.025f);
                    candleData.z = moveTo(candleData.z, 0, 0.125f);
                }
            }
        }

        if(!startupFlag) {
            if(!getBlockState().getBlock().asItem().equals(ModItems.CANDLE.get())){
                candles.get(0).height = 7;
                candles.get(0).hasCandle = true;
            }
            candles.get(0).hasCandle = true;
            candles.get(0).meltTimer = candleMeltTimerMAX;
            startupFlag = true;
        }


        numberOfCandles = 0;

        if(candles.get(0).hasCandle)
            numberOfCandles++;
        if(candles.get(1).hasCandle)
            numberOfCandles++;
        if(candles.get(2).hasCandle)
            numberOfCandles++;
        if(candles.get(3).hasCandle)
            numberOfCandles++;


        if(candles.get(0).hasCandle)
        {

            if(candles.get(0).lit) {
                float xOffset = 0;
                float zOffset = 0;

                if (numberOfCandles == 4) {
                    if (getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.NORTH) {
                        xOffset = 3f / 16f;
                        zOffset = 2f / 16f;
                    }if (getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.SOUTH) {
                        xOffset = -3f / 16f;
                        zOffset = -2f / 16f;
                    }if (getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.EAST) {
                        xOffset = -2f / 16f;
                        zOffset = 3f / 16f;
                    }if (getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.WEST) {
                        xOffset = 2f / 16f;
                        zOffset = -3f / 16f;
                    }
                }
                else if (numberOfCandles == 3) {
                    if (getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.NORTH) {
                        xOffset = -1f / 16f;
                        zOffset = 3f / 16f;
                    }if (getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.SOUTH) {
                        xOffset = 1f / 16f;
                        zOffset = -3f / 16f;
                    }if (getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.EAST) {
                        xOffset = -3f / 16f;
                        zOffset = -1f / 16f;
                    }if (getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.WEST) {
                        xOffset = 3f / 16f;
                        zOffset = 1f / 16f;
                    }
                }
                else if (numberOfCandles == 2) {

                    if (getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.NORTH) {
                        xOffset = 3f / 16f;
                        zOffset = -2f / 16f;
                    }if (getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.SOUTH) {
                        xOffset = -3f / 16f;
                        zOffset = 2f / 16f;
                    }if (getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.EAST) {
                        xOffset = 2f / 16f;
                        zOffset = 3f / 16f;
                    }if (getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.WEST) {
                        xOffset = -2f / 16f;
                        zOffset = -3f / 16f;
                    }
                }
                else if (numberOfCandles == 1) {
                    xOffset = 0;
                    zOffset = 0;
                }

                if(!(candles.get(0).x != 0 || candles.get(0).y != 0 || candles.get(0).z != 0)) {


                    if (random.nextInt(10) == 0 && candles.get(0).getEffect().particle != null)
                        level.addParticle(candles.get(0).getEffect().particle != null ? candles.get(0).getEffect().particle : ParticleTypes.FLAME, worldPosition.getX() + 0.5f + xOffset, worldPosition.getY() + 3f / 16f + (float) candles.get(0).height / 16f, worldPosition.getZ() + 0.5f + zOffset, (random.nextDouble() - 0.5d) / 50d, (random.nextDouble() + 0.5d) * 0.015d, (random.nextDouble() - 0.5d) / 50d);
                    if (random.nextInt(10) == 0)
                        level.addParticle(ParticleTypes.FLAME, worldPosition.getX() + 0.5f + xOffset, worldPosition.getY() + 3f / 16f + (float) candles.get(0).height / 16f, worldPosition.getZ() + 0.5f + zOffset, (random.nextDouble() - 0.5d) / 50d, (random.nextDouble() + 0.5d) * 0.015d, (random.nextDouble() - 0.5d) / 50d);
                    if (random.nextInt(10) == 0)
                        level.addParticle(ParticleTypes.SMOKE, worldPosition.getX() + 0.5f + xOffset, worldPosition.getY() + 3f / 16f + (float) candles.get(0).height / 16f, worldPosition.getZ() + 0.5f + zOffset, (random.nextDouble() - 0.5d) / 50d, (random.nextDouble() + 0.5d) * 0.045d, (random.nextDouble() - 0.5d) / 50d);

                } else
                {
                    if (random.nextInt(10) == 0 && candles.get(0).getEffect().particle != null)
                        level.addParticle(candles.get(0).getEffect().particle != null ? candles.get(0).getEffect().particle : ParticleTypes.FLAME, worldPosition.getX() + 0.5f + xOffset, worldPosition.getY() + 3f / 16f + (float) candles.get(0).height / 16f, worldPosition.getZ() + 0.5f + zOffset, (random.nextDouble() - 0.5d) / 50d, (random.nextDouble() + 0.5d) * 0.015d, (random.nextDouble() - 0.5d) / 50d);
                    if (random.nextInt(10) == 0)
                        level.addParticle(ParticleTypes.FLAME, worldPosition.getX() + 0.5f + xOffset, worldPosition.getY() + 3f / 16f + (float) candles.get(0).height / 16f, worldPosition.getZ() + 0.5f + zOffset, (random.nextDouble() - 0.5d) / 50d, (random.nextDouble() + 0.5d) * 0.015d, (random.nextDouble() - 0.5d) / 50d);
                    if (random.nextInt(10) == 0)
                        level.addParticle(ParticleTypes.SMOKE, worldPosition.getX() + 0.5f + candles.get(0).x, worldPosition.getY() + 3f / 16f + (float) candles.get(0).height / 16f + candles.get(0).y, worldPosition.getZ() + 0.5f + candles.get(0).z, (random.nextDouble() - 0.5d) / 50d, (random.nextDouble() + 0.5d) * 0.045d, (random.nextDouble() - 0.5d) / 50d);
                }
                candles.get(0).meltTimer--;
                if (candles.get(0).meltTimer <= 0) {
                    candles.get(0).meltTimer = candleMeltTimerMAX;
                    candles.get(0).height--;

                    if(!(candles.get(0).x != 0 || candles.get(0).y != 0 || candles.get(0).z != 0)) {
                        level.addParticle(ParticleTypes.LARGE_SMOKE, worldPosition.getX() + 0.5f + xOffset, worldPosition.getY() + 3f / 16f + (float) candles.get(0).height / 16f, worldPosition.getZ() + 0.5f + zOffset, (random.nextDouble() - 0.5d) / 50d, (random.nextDouble() + 0.5d) * 0.015d, (random.nextDouble() - 0.5d) / 50d);
                    } else
                    {
                        level.addParticle(ParticleTypes.LARGE_SMOKE, worldPosition.getX() + 0.5f + candles.get(0).x, worldPosition.getY() + 3f / 16f + (float) candles.get(0).height / 16f + candles.get(0).y, worldPosition.getZ() + 0.5f + candles.get(0).z, (random.nextDouble() - 0.5d) / 50d, (random.nextDouble() + 0.5d) * 0.015d, (random.nextDouble() - 0.5d) / 50d);
                    }


                    if (candles.get(0).height <= 0) {
                        candles.get(0).hasCandle = false;
                        updateCandleSlots();
                        BlockState blockstate = this.getLevel().getBlockState(this.getBlockPos());
                        if (!level.isClientSide())
                            this.getLevel().setBlock(this.getBlockPos(), this.getBlockState().setValue(Candle.CANDLES, Integer.valueOf(Math.max(1, blockstate.getValue(Candle.CANDLES) - 1))),1);
                        level.playSound((Player) null, worldPosition, SoundEvents.STONE_BREAK, SoundSource.BLOCKS, 1.0F, random.nextFloat() * 0.4F + 1.0F);

                        if(!(candles.get(0).x != 0 || candles.get(0).y != 0 || candles.get(0).z != 0)) {
                            level.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, level.getBlockState(worldPosition)), worldPosition.getX() + 0.5f + xOffset, worldPosition.getY() + 0.2d, worldPosition.getZ() + 0.5f + zOffset, (random.nextDouble() - 0.5d) / 50d, (random.nextDouble() + 0.5d) * 0.045d, (random.nextDouble() - 0.5d) / 50d);
                            level.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, level.getBlockState(worldPosition)), worldPosition.getX() + 0.5f + xOffset, worldPosition.getY() + 0.2d, worldPosition.getZ() + 0.5f + zOffset, (random.nextDouble() - 0.5d) / 50d, (random.nextDouble() + 0.5d) * 0.045d, (random.nextDouble() - 0.5d) / 50d);
                        } else
                        {
                            level.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, level.getBlockState(worldPosition)), worldPosition.getX() + 0.5f + candles.get(0).x, worldPosition.getY() + 3f / 16f + (float) candles.get(0).height / 16f + candles.get(0).y, worldPosition.getZ() + 0.5f + candles.get(0).z, (random.nextDouble() - 0.5d) / 50d, (random.nextDouble() + 0.5d) * 0.045d, (random.nextDouble() - 0.5d) / 50d);
                            level.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, level.getBlockState(worldPosition)), worldPosition.getX() + 0.5f + candles.get(0).x, worldPosition.getY() + 3f / 16f + (float) candles.get(0).height / 16f + candles.get(0).y, worldPosition.getZ() + 0.5f + candles.get(0).z, (random.nextDouble() - 0.5d) / 50d, (random.nextDouble() + 0.5d) * 0.045d, (random.nextDouble() - 0.5d) / 50d);
                        }

                        //candles.get(0).height = 0;
                    }
                }
            }
        }
        if(candles.get(1).hasCandle)
        {

            if(candles.get(1).lit) {
                float xOffset = 0;
                float zOffset = 0;


//                if(tileEntityIn.numberOfCandles == 4)
//                    matrixStackIn.translate(-2f/16f , 0f/16f, -3f/16f);
//                else if(tileEntityIn.numberOfCandles == 3)
//                    matrixStackIn.translate(3f/16f , 0f/16f, 1f/16f);
//                else if(tileEntityIn.numberOfCandles == 2)
//                    matrixStackIn.translate(-3f/16f , 0f/16f, 3f/16f);
                if (numberOfCandles == 4) {
                    if (getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.NORTH) {
                        xOffset = -2f / 16f;
                        zOffset = -3f / 16f;
                    }if (getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.SOUTH) {
                        xOffset = 2f / 16f;
                        zOffset = 3f / 16f;
                    }if (getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.EAST) {
                        xOffset = 3f / 16f;
                        zOffset = -2f / 16f;
                    }if (getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.WEST) {
                        xOffset = -3f / 16f;
                        zOffset = 2f / 16f;
                    }
                }
                else if (numberOfCandles == 3) {
                    if (getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.NORTH) {
                        xOffset = 3f / 16f;
                        zOffset = 1f / 16f;
                    }if (getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.SOUTH) {
                        xOffset = -3f / 16f;
                        zOffset = -1f / 16f;
                    }if (getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.EAST) {
                        xOffset = -1f / 16f;
                        zOffset = 3f / 16f;
                    }if (getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.WEST) {
                        xOffset = 1f / 16f;
                        zOffset = -3f / 16f;
                    }
                }
                else if (numberOfCandles == 2) {

                    if (getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.NORTH) {
                        xOffset = -3f / 16f;
                        zOffset = 3f / 16f;
                    }if (getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.SOUTH) {
                        xOffset = 3f / 16f;
                        zOffset = -3f / 16f;
                    }if (getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.EAST) {
                        xOffset = -3f / 16f;
                        zOffset = -3f / 16f;
                    }if (getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.WEST) {
                        xOffset = 3f / 16f;
                        zOffset = 3f / 16f;
                    }
                }
                else if (numberOfCandles == 1) {
                    xOffset = 0;
                    zOffset = 0;
                }

                if(!(candles.get(1).x != 0 || candles.get(1).y != 0 || candles.get(1).z != 0)) {


                    if (random.nextInt(10) == 0)
                        level.addParticle(ParticleTypes.FLAME, worldPosition.getX() + 0.5f + xOffset, worldPosition.getY() + 3f / 16f + (float) candles.get(1).height / 16f, worldPosition.getZ() + 0.5f + zOffset, (random.nextDouble() - 0.5d) / 50d, (random.nextDouble() + 0.5d) * 0.015d, (random.nextDouble() - 0.5d) / 50d);
                    if (random.nextInt(10) == 0)
                        level.addParticle(ParticleTypes.SMOKE, worldPosition.getX() + 0.5f + xOffset, worldPosition.getY() + 3f / 16f + (float) candles.get(1).height / 16f, worldPosition.getZ() + 0.5f + zOffset, (random.nextDouble() - 0.5d) / 50d, (random.nextDouble() + 0.5d) * 0.045d, (random.nextDouble() - 0.5d) / 50d);

                } else
                {
                    if (random.nextInt(10) == 0)
                        level.addParticle(ParticleTypes.FLAME, worldPosition.getX() + 0.5f + candles.get(1).x, worldPosition.getY() + 3f / 16f + (float) candles.get(1).height / 16f + candles.get(1).y, worldPosition.getZ() + 0.5f + candles.get(1).z, (random.nextDouble() - 0.5d) / 50d, (random.nextDouble() + 0.5d) * 0.015d, (random.nextDouble() - 0.5d) / 50d);
                    if (random.nextInt(10) == 0)
                        level.addParticle(ParticleTypes.SMOKE, worldPosition.getX() + 0.5f + candles.get(1).x, worldPosition.getY() + 3f / 16f + (float) candles.get(1).height / 16f + candles.get(1).y, worldPosition.getZ() + 0.5f + candles.get(1).z, (random.nextDouble() - 0.5d) / 50d, (random.nextDouble() + 0.5d) * 0.045d, (random.nextDouble() - 0.5d) / 50d);
                }
                candles.get(1).meltTimer--;
                if (candles.get(1).meltTimer <= 0) {
                    candles.get(1).meltTimer = candleMeltTimerMAX;
                    candles.get(1).height--;

                    if(!(candles.get(1).x != 0 || candles.get(1).y != 0 || candles.get(1).z != 0)) {
                        level.addParticle(ParticleTypes.LARGE_SMOKE, worldPosition.getX() + 0.5f + xOffset, worldPosition.getY() + 3f / 16f + (float) candles.get(1).height / 16f, worldPosition.getZ() + 0.5f + zOffset, (random.nextDouble() - 0.5d) / 50d, (random.nextDouble() + 0.5d) * 0.015d, (random.nextDouble() - 0.5d) / 50d);
                    } else
                    {
                        level.addParticle(ParticleTypes.LARGE_SMOKE, worldPosition.getX() + 0.5f + candles.get(1).x, worldPosition.getY() + 3f / 16f + (float) candles.get(1).height / 16f + candles.get(1).y, worldPosition.getZ() + 0.5f + candles.get(1).z, (random.nextDouble() - 0.5d) / 50d, (random.nextDouble() + 0.5d) * 0.015d, (random.nextDouble() - 0.5d) / 50d);
                    }


                    if (candles.get(1).height <= 0) {
                        candles.get(1).hasCandle = false;
                        updateCandleSlots();
                        BlockState blockstate = this.getLevel().getBlockState(this.getBlockPos());
                        if (!level.isClientSide())
                            this.getLevel().setBlock(this.getBlockPos(), this.getBlockState().setValue(Candle.CANDLES, Integer.valueOf(Math.max(1, blockstate.getValue(Candle.CANDLES) - 1))), 1);
                        level.playSound((Player) null, worldPosition, SoundEvents.STONE_BREAK, SoundSource.BLOCKS, 1.0F, random.nextFloat() * 0.4F + 1.0F);
                        if(!(candles.get(1).x != 0 || candles.get(1).y != 0 || candles.get(1).z != 0)) {
                            level.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, level.getBlockState(worldPosition)), worldPosition.getX() + 0.5f + xOffset, worldPosition.getY() + 0.2d, worldPosition.getZ() + 0.5f + zOffset, (random.nextDouble() - 0.5d) / 50d, (random.nextDouble() + 0.5d) * 0.045d, (random.nextDouble() - 0.5d) / 50d);
                            level.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, level.getBlockState(worldPosition)), worldPosition.getX() + 0.5f + xOffset, worldPosition.getY() + 0.2d, worldPosition.getZ() + 0.5f + zOffset, (random.nextDouble() - 0.5d) / 50d, (random.nextDouble() + 0.5d) * 0.045d, (random.nextDouble() - 0.5d) / 50d);
                        } else
                        {
                            level.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, level.getBlockState(worldPosition)), worldPosition.getX() + 0.5f + candles.get(1).x, worldPosition.getY() + 3f / 16f + (float) candles.get(1).height / 16f + candles.get(1).y, worldPosition.getZ() + 0.5f + candles.get(1).z, (random.nextDouble() - 0.5d) / 50d, (random.nextDouble() + 0.5d) * 0.045d, (random.nextDouble() - 0.5d) / 50d);
                            level.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, level.getBlockState(worldPosition)), worldPosition.getX() + 0.5f + candles.get(1).x, worldPosition.getY() + 3f / 16f + (float) candles.get(1).height / 16f + candles.get(1).y, worldPosition.getZ() + 0.5f + candles.get(1).z, (random.nextDouble() - 0.5d) / 50d, (random.nextDouble() + 0.5d) * 0.045d, (random.nextDouble() - 0.5d) / 50d);
                        }
                        //candles.get(0).height = 0;
                    }
                }
            }
        }
        if(candles.get(2).hasCandle)
        {

            if(candles.get(2).lit) {
                float xOffset = 0;
                float zOffset = 0;


                if (numberOfCandles == 4) {
                    if (getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.NORTH) {
                        xOffset = -2f / 16f;
                        zOffset = 2f / 16f;
                    }if (getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.SOUTH) {
                        xOffset = 2f / 16f;
                        zOffset = -2f / 16f;
                    }if (getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.EAST) {
                        xOffset = 2f / 16f;
                        zOffset = 2f / 16f;
                    }if (getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.WEST) {
                        xOffset = -2f / 16f;
                        zOffset = -2f / 16f;
                    }
                }
                else if (numberOfCandles == 3) {
                    if (getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.NORTH) {
                        xOffset = -2f / 16f;
                        zOffset = -3f / 16f;
                    }if (getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.SOUTH) {
                        xOffset = 2f / 16f;
                        zOffset = 3f / 16f;
                    }if (getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.EAST) {
                        xOffset = 3f / 16f;
                        zOffset = -2f / 16f;
                    }if (getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.WEST) {
                        xOffset = -3f / 16f;
                        zOffset = 2f / 16f;
                    }
                }

                if(!(candles.get(2).x != 0 || candles.get(2).y != 0 || candles.get(2).z != 0)) {


                    if (random.nextInt(10) == 0)
                        level.addParticle(ParticleTypes.FLAME, worldPosition.getX() + 0.5f + xOffset, worldPosition.getY() + 3f / 16f + (float) candles.get(2).height / 16f, worldPosition.getZ() + 0.5f + zOffset, (random.nextDouble() - 0.5d) / 50d, (random.nextDouble() + 0.5d) * 0.015d, (random.nextDouble() - 0.5d) / 50d);
                    if (random.nextInt(10) == 0)
                        level.addParticle(ParticleTypes.SMOKE, worldPosition.getX() + 0.5f + xOffset, worldPosition.getY() + 3f / 16f + (float) candles.get(2).height / 16f, worldPosition.getZ() + 0.5f + zOffset, (random.nextDouble() - 0.5d) / 50d, (random.nextDouble() + 0.5d) * 0.045d, (random.nextDouble() - 0.5d) / 50d);

                } else
                {
                    if (random.nextInt(10) == 0)
                        level.addParticle(ParticleTypes.FLAME, worldPosition.getX() + 0.5f + candles.get(2).x, worldPosition.getY() + 3f / 16f + (float) candles.get(2).height / 16f + candles.get(2).y, worldPosition.getZ() + 0.5f + candles.get(2).z, (random.nextDouble() - 0.5d) / 50d, (random.nextDouble() + 0.5d) * 0.015d, (random.nextDouble() - 0.5d) / 50d);
                    if (random.nextInt(10) == 0)
                        level.addParticle(ParticleTypes.SMOKE, worldPosition.getX() + 0.5f + candles.get(2).x, worldPosition.getY() + 3f / 16f + (float) candles.get(2).height / 16f + candles.get(2).y, worldPosition.getZ() + 0.5f + candles.get(2).z, (random.nextDouble() - 0.5d) / 50d, (random.nextDouble() + 0.5d) * 0.045d, (random.nextDouble() - 0.5d) / 50d);
                }
                candles.get(2).meltTimer--;
                if (candles.get(2).meltTimer <= 0) {
                    candles.get(2).meltTimer = candleMeltTimerMAX;
                    candles.get(2).height--;

                    if(!(candles.get(2).x != 0 || candles.get(2).y != 0 || candles.get(2).z != 0)) {
                        level.addParticle(ParticleTypes.LARGE_SMOKE, worldPosition.getX() + 0.5f + xOffset, worldPosition.getY() + 3f / 16f + (float) candles.get(2).height / 16f, worldPosition.getZ() + 0.5f + zOffset, (random.nextDouble() - 0.5d) / 50d, (random.nextDouble() + 0.5d) * 0.015d, (random.nextDouble() - 0.5d) / 50d);
                    } else
                    {
                        level.addParticle(ParticleTypes.LARGE_SMOKE, worldPosition.getX() + 0.5f + candles.get(2).x, worldPosition.getY() + 3f / 16f + (float) candles.get(2).height / 16f + candles.get(2).y, worldPosition.getZ() + 0.5f + candles.get(2).z, (random.nextDouble() - 0.5d) / 50d, (random.nextDouble() + 0.5d) * 0.015d, (random.nextDouble() - 0.5d) / 50d);
                    }


                    if (candles.get(2).height <= 0) {
                        candles.get(2).hasCandle = false;
                        updateCandleSlots();
                        BlockState blockstate = this.getLevel().getBlockState(this.getBlockPos());
                        if (!level.isClientSide())
                            this.getLevel().setBlock(this.getBlockPos(), this.getBlockState().setValue(Candle.CANDLES, Integer.valueOf(Math.max(1, blockstate.getValue(Candle.CANDLES) - 1))), 1);
                        level.playSound((Player) null, worldPosition, SoundEvents.STONE_BREAK, SoundSource.BLOCKS, 1.0F, random.nextFloat() * 0.4F + 1.0F);
                        if(!(candles.get(2).x != 0 || candles.get(2).y != 0 || candles.get(2).z != 0)) {
                            level.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, level.getBlockState(worldPosition)), worldPosition.getX() + 0.5f + xOffset, worldPosition.getY() + 0.2d, worldPosition.getZ() + 0.5f + zOffset, (random.nextDouble() - 0.5d) / 50d, (random.nextDouble() + 0.5d) * 0.045d, (random.nextDouble() - 0.5d) / 50d);
                            level.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, level.getBlockState(worldPosition)), worldPosition.getX() + 0.5f + xOffset, worldPosition.getY() + 0.2d, worldPosition.getZ() + 0.5f + zOffset, (random.nextDouble() - 0.5d) / 50d, (random.nextDouble() + 0.5d) * 0.045d, (random.nextDouble() - 0.5d) / 50d);
                        } else
                        {
                            level.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, level.getBlockState(worldPosition)), worldPosition.getX() + 0.5f + candles.get(2).x, worldPosition.getY() + 3f / 16f + (float) candles.get(2).height / 16f + candles.get(2).y, worldPosition.getZ() + 0.5f + candles.get(2).z, (random.nextDouble() - 0.5d) / 50d, (random.nextDouble() + 0.5d) * 0.045d, (random.nextDouble() - 0.5d) / 50d);
                            level.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, level.getBlockState(worldPosition)), worldPosition.getX() + 0.5f + candles.get(2).x, worldPosition.getY() + 3f / 16f + (float) candles.get(2).height / 16f + candles.get(2).y, worldPosition.getZ() + 0.5f + candles.get(2).z, (random.nextDouble() - 0.5d) / 50d, (random.nextDouble() + 0.5d) * 0.045d, (random.nextDouble() - 0.5d) / 50d);
                        }
                        //candles.get(0).height = 0;
                    }
                }
            }
        }
        if(candles.get(3).hasCandle)
        {

            if(candles.get(3).lit) {
                float xOffset = 0;
                float zOffset = 0;


                if (numberOfCandles == 4) {
                    if (getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.NORTH) {
                        xOffset = 3f / 16f;
                        zOffset = -2f / 16f;
                    }if (getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.SOUTH) {
                        xOffset = -3f / 16f;
                        zOffset = 2f / 16f;
                    }if (getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.EAST) {
                        xOffset = -2f / 16f;
                        zOffset = 3f / 16f;
                    }if (getBlockState().getValue(HorizontalDirectionalBlock.FACING) == Direction.WEST) {
                        xOffset = 2f / 16f;
                        zOffset = -3f / 16f;
                    }
                }

                if(!(candles.get(3).x != 0 || candles.get(3).y != 0 || candles.get(3).z != 0)) {


                    if (random.nextInt(10) == 0)
                        level.addParticle(ParticleTypes.FLAME, worldPosition.getX() + 0.5f + xOffset, worldPosition.getY() + 3f / 16f + (float) candles.get(3).height / 16f, worldPosition.getZ() + 0.5f + zOffset, (random.nextDouble() - 0.5d) / 50d, (random.nextDouble() + 0.5d) * 0.015d, (random.nextDouble() - 0.5d) / 50d);
                    if (random.nextInt(10) == 0)
                        level.addParticle(ParticleTypes.SMOKE, worldPosition.getX() + 0.5f + xOffset, worldPosition.getY() + 3f / 16f + (float) candles.get(3).height / 16f, worldPosition.getZ() + 0.5f + zOffset, (random.nextDouble() - 0.5d) / 50d, (random.nextDouble() + 0.5d) * 0.045d, (random.nextDouble() - 0.5d) / 50d);

                } else
                {
                    if (random.nextInt(10) == 0)
                        level.addParticle(ParticleTypes.FLAME, worldPosition.getX() + 0.5f + candles.get(3).x, worldPosition.getY() + 3f / 16f + (float) candles.get(3).height / 16f + candles.get(3).y, worldPosition.getZ() + 0.5f + candles.get(3).z, (random.nextDouble() - 0.5d) / 50d, (random.nextDouble() + 0.5d) * 0.015d, (random.nextDouble() - 0.5d) / 50d);
                    if (random.nextInt(10) == 0)
                        level.addParticle(ParticleTypes.SMOKE, worldPosition.getX() + 0.5f + candles.get(3).x, worldPosition.getY() + 3f / 16f + (float) candles.get(3).height / 16f + candles.get(3).y, worldPosition.getZ() + 0.5f + candles.get(3).z, (random.nextDouble() - 0.5d) / 50d, (random.nextDouble() + 0.5d) * 0.045d, (random.nextDouble() - 0.5d) / 50d);
                }
                candles.get(3).meltTimer--;
                if (candles.get(3).meltTimer <= 0) {
                    candles.get(3).meltTimer = candleMeltTimerMAX;
                    candles.get(3).height--;

                    if(!(candles.get(3).x != 0 || candles.get(3).y != 0 || candles.get(3).z != 0)) {
                        level.addParticle(ParticleTypes.LARGE_SMOKE, worldPosition.getX() + 0.5f + xOffset, worldPosition.getY() + 3f / 16f + (float) candles.get(3).height / 16f, worldPosition.getZ() + 0.5f + zOffset, (random.nextDouble() - 0.5d) / 50d, (random.nextDouble() + 0.5d) * 0.015d, (random.nextDouble() - 0.5d) / 50d);
                    } else
                    {
                        level.addParticle(ParticleTypes.LARGE_SMOKE, worldPosition.getX() + 0.5f + candles.get(3).x, worldPosition.getY() + 3f / 16f + (float) candles.get(3).height / 16f + candles.get(3).y, worldPosition.getZ() + 0.5f + candles.get(3).z, (random.nextDouble() - 0.5d) / 50d, (random.nextDouble() + 0.5d) * 0.015d, (random.nextDouble() - 0.5d) / 50d);
                    }


                    if (candles.get(3).height <= 0) {
                        candles.get(3).hasCandle = false;
                        updateCandleSlots();
                        BlockState blockstate = this.getLevel().getBlockState(this.getBlockPos());
                        if (!level.isClientSide())
                            this.getLevel().setBlock(this.getBlockPos(), this.getBlockState().setValue(Candle.CANDLES, Integer.valueOf(Math.max(1, blockstate.getValue(Candle.CANDLES) - 1))), 1);
                        level.playSound((Player) null, worldPosition, SoundEvents.STONE_BREAK, SoundSource.BLOCKS, 1.0F, random.nextFloat() * 0.4F + 1.0F);
                        if(!(candles.get(3).x != 0 || candles.get(3).y != 0 || candles.get(3).z != 0)) {
                            level.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, level.getBlockState(worldPosition)), worldPosition.getX() + 0.5f + xOffset, worldPosition.getY() + 0.2d, worldPosition.getZ() + 0.5f + zOffset, (random.nextDouble() - 0.5d) / 50d, (random.nextDouble() + 0.5d) * 0.045d, (random.nextDouble() - 0.5d) / 50d);
                            level.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, level.getBlockState(worldPosition)), worldPosition.getX() + 0.5f + xOffset, worldPosition.getY() + 0.2d, worldPosition.getZ() + 0.5f + zOffset, (random.nextDouble() - 0.5d) / 50d, (random.nextDouble() + 0.5d) * 0.045d, (random.nextDouble() - 0.5d) / 50d);
                        } else
                        {
                            level.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, level.getBlockState(worldPosition)), worldPosition.getX() + 0.5f + candles.get(3).x, worldPosition.getY() + 3f / 16f + (float) candles.get(3).height / 16f + candles.get(3).y, worldPosition.getZ() + 0.5f + candles.get(3).z, (random.nextDouble() - 0.5d) / 50d, (random.nextDouble() + 0.5d) * 0.045d, (random.nextDouble() - 0.5d) / 50d);
                            level.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, level.getBlockState(worldPosition)), worldPosition.getX() + 0.5f + candles.get(3).x, worldPosition.getY() + 3f / 16f + (float) candles.get(3).height / 16f + candles.get(3).y, worldPosition.getZ() + 0.5f + candles.get(3).z, (random.nextDouble() - 0.5d) / 50d, (random.nextDouble() + 0.5d) * 0.045d, (random.nextDouble() - 0.5d) / 50d);
                        }
                        //candles.get(0).height = 0;
                    }
                }
            }
        }

        candles.get(0).returnToBlock = true;
        candles.get(1).returnToBlock = true;
        candles.get(2).returnToBlock = true;
        candles.get(3).returnToBlock = true;


        if(!candles.get(0).hasCandle && !candles.get(1).hasCandle && !candles.get(2).hasCandle && !candles.get(3).hasCandle)
            if(this.getLevel() != null)
                this.getLevel().destroyBlock(this.getBlockPos(), false);



        litStateOld = getBlockState().getValue(Candle.LIT);

    }

    public void updateCandleSlots() {
        if(!candles.get(0).hasCandle)
            updateCandleSlot(0);
        if(!candles.get(1).hasCandle)
            updateCandleSlot(1);
        if(!candles.get(2).hasCandle)
            updateCandleSlot(2);
    }

    public void updateCandleSlot(int slot){

        candles.get(slot).dyeColor = candles.get(slot + 1).dyeColor;
        candles.get(slot).height = candles.get(slot + 1).height;
        candles.get(slot).meltTimer = candles.get(slot + 1).meltTimer;
        candles.get(slot).glow = candles.get(slot + 1).glow;
        candles.get(slot).base = candles.get(slot + 1).base;
        candles.get(slot).herb = candles.get(slot + 1).herb;
        candles.get(slot).swirl = candles.get(slot + 1).swirl;
        candles.get(slot).lit = candles.get(slot + 1).lit;
        candles.get(slot).hasCandle = candles.get(slot + 1).hasCandle;
        candles.get(slot).customName = candles.get(slot + 1).customName;
        candles.get(slot).returnToBlock = candles.get(slot + 1).returnToBlock;
        candles.get(slot + 1).hasCandle = false;
        candles.get(slot + 1).dyeColor = Candle.BASE_COLOR;
        candles.get(slot + 1).glow.layer = null;
        candles.get(slot + 1).base.layer = null;
        candles.get(slot + 1).herb.layer = null;
        candles.get(slot + 1).swirl.layer = null;
        candles.get(slot + 1).lit = false;
        candles.get(slot + 1).meltTimer = candleMeltTimerMAX;
    }


}
