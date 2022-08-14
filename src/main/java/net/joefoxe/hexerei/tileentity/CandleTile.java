package net.joefoxe.hexerei.tileentity;

import net.joefoxe.hexerei.block.custom.Candle;
import net.joefoxe.hexerei.data.candle.BonemealingCandleEffect;
import net.joefoxe.hexerei.data.candle.CandleData;
import net.joefoxe.hexerei.util.HexereiPacketHandler;
import net.joefoxe.hexerei.util.message.TESyncPacket;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.client.renderer.texture.Tickable;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraftforge.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Random;
import java.util.function.Function;

public class CandleTile extends BlockEntity {

//    public final ItemStackHandler itemHandler = createHandler();
//    private final LazyOptional<IItemHandler> handler = LazyOptional.of(() -> itemHandler);
    public NonNullList<CandleData> candles = NonNullList.withSize(4, new CandleData(0, false,0,0, 0, 0, CandleData.meltTimerMAX, new BonemealingCandleEffect()));

    public int numberOfCandles;
    public int candleMeltTimerMAX = 6000;
    private boolean startupFlag;

    public CandleTile(BlockEntityType<?> tileEntityTypeIn, BlockPos blockPos, BlockState blockState) {
        super(tileEntityTypeIn, blockPos, blockState);
        for(int i = 0; i < candles.size(); i++)
            candles.set(i, new CandleData(0, false,0,0, 0, 0, CandleData.meltTimerMAX, new BonemealingCandleEffect()));
        startupFlag = false;
        numberOfCandles = 0;
    }

    public CandleTile(BlockPos blockPos, BlockState blockState) {
        this(ModTileEntities.CANDLE_TILE.get(),blockPos, blockState);
    }

//    public CandleTile() {
//        this(ModTileEntities.CANDLE_TILE.get());
//    }

    @Override
    public void load(CompoundTag nbt) {

        if (nbt.contains("candleType1",  Tag.TAG_INT))
            candles.get(0).type = nbt.getInt("candleType1");
        if (nbt.contains("candleType2",  Tag.TAG_INT))
            candles.get(1).type = nbt.getInt("candleType2");
        if (nbt.contains("candleType3",  Tag.TAG_INT))
            candles.get(2).type = nbt.getInt("candleType3");
        if (nbt.contains("candleType4",  Tag.TAG_INT))
            candles.get(3).type = nbt.getInt("candleType4");
        if (nbt.contains("candleHeight1",  Tag.TAG_INT))
            candles.get(0).height = nbt.getInt("candleHeight1");
        if (nbt.contains("candleHeight2",  Tag.TAG_INT))
            candles.get(1).height = nbt.getInt("candleHeight2");
        if (nbt.contains("candleHeight3",  Tag.TAG_INT))
            candles.get(2).height = nbt.getInt("candleHeight3");
        if (nbt.contains("candleHeight4",  Tag.TAG_INT))
            candles.get(3).height = nbt.getInt("candleHeight4");
        if (nbt.contains("candleLit1",  Tag.TAG_BYTE))
            candles.get(0).lit = nbt.getBoolean("candleLit1");
        else
            candles.get(0).lit = false;
        if (nbt.contains("candleLit2",  Tag.TAG_BYTE))
            candles.get(1).lit = nbt.getBoolean("candleLit2");
        else
            candles.get(1).lit = false;
        if (nbt.contains("candleLit3",  Tag.TAG_BYTE))
            candles.get(2).lit = nbt.getBoolean("candleLit3");
        else
            candles.get(2).lit = false;
        if (nbt.contains("candleLit4",  Tag.TAG_BYTE))
            candles.get(3).lit = nbt.getBoolean("candleLit4");
        else
            candles.get(3).lit = false;
        if (nbt.contains("c1andleMeltTimer1",  Tag.TAG_INT))
            candles.get(0).meltTimer = nbt.getInt("c1andleMeltTimer1");
        if (nbt.contains("c1andleMeltTimer2",  Tag.TAG_INT))
            candles.get(1).meltTimer = nbt.getInt("c1andleMeltTimer2");
        if (nbt.contains("c1andleMeltTimer3",  Tag.TAG_INT))
            candles.get(2).meltTimer = nbt.getInt("c1andleMeltTimer3");
        if (nbt.contains("c1andleMeltTimer4",  Tag.TAG_INT))
            candles.get(3).meltTimer = nbt.getInt("c1andleMeltTimer4");
        super.load(nbt);

    }

    @Override
    public void saveAdditional(CompoundTag compound) {
        if(candles.get(0).type != 0)
            compound.putInt("candleType1", candles.get(0).type);
        if(candles.get(1).type != 0)
            compound.putInt("candleType2", candles.get(1).type);
        if(candles.get(2).type != 0)
            compound.putInt("candleType3", candles.get(2).type);
        if(candles.get(3).type != 0)
            compound.putInt("candleType4", candles.get(3).type);
        if(candles.get(0).height != 0)
            compound.putInt("candleHeight1", candles.get(0).height);
        if(candles.get(1).height != 0)
            compound.putInt("candleHeight2", candles.get(1).height);
        if(candles.get(2).height != 0)
            compound.putInt("candleHeight3", candles.get(2).height);
        if(candles.get(3).height != 0)
            compound.putInt("candleHeight4", candles.get(3).height);
        compound.putBoolean("candleLit1", candles.get(0).lit);
        compound.putBoolean("candleLit2", candles.get(1).lit);
        compound.putBoolean("candleLit3", candles.get(2).lit);
        compound.putBoolean("candleLit4", candles.get(3).lit);
        if(candles.get(0).meltTimer != 0)
            compound.putInt("candleMeltTimer1", candles.get(0).meltTimer);
        if(candles.get(1).meltTimer != 0)
            compound.putInt("candleMeltTimer2", candles.get(1).meltTimer);
        if(candles.get(2).meltTimer != 0)
            compound.putInt("candleMeltTimer3", candles.get(2).meltTimer);
        if(candles.get(3).meltTimer != 0)
            compound.putInt("candleMeltTimer4", candles.get(3).meltTimer);

    }

//    @Override
    public CompoundTag save(CompoundTag tag) {
        super.saveAdditional(tag);
//        ContainerHelper.saveAllItems(tag, this.items);
//        tag.put("inv", itemHandler.serializeNBT());
        if(candles.get(0).type != 0)
            tag.putInt("candleType1", candles.get(0).type);
        if(candles.get(1).type != 0)
            tag.putInt("candleType2", candles.get(1).type);
        if(candles.get(2).type != 0)
            tag.putInt("candleType3", candles.get(2).type);
        if(candles.get(3).type != 0)
            tag.putInt("candleType4", candles.get(3).type);
        if(candles.get(0).height != 0)
            tag.putInt("candleHeight1", candles.get(0).height);
        if(candles.get(1).height != 0)
            tag.putInt("candleHeight2", candles.get(1).height);
        if(candles.get(2).height != 0)
            tag.putInt("candleHeight3", candles.get(2).height);
        if(candles.get(3).height != 0)
            tag.putInt("candleHeight4", candles.get(3).height);
        tag.putBoolean("candleLit1", candles.get(0).lit);
        tag.putBoolean("candleLit2", candles.get(1).lit);
        tag.putBoolean("candleLit3", candles.get(2).lit);
        tag.putBoolean("candleLit4", candles.get(3).lit);
        if(candles.get(0).meltTimer != 0)
            tag.putInt("candleMeltTimer1", candles.get(0).meltTimer);
        if(candles.get(1).meltTimer != 0)
            tag.putInt("candleMeltTimer2", candles.get(1).meltTimer);
        if(candles.get(2).meltTimer != 0)
            tag.putInt("candleMeltTimer3", candles.get(2).meltTimer);
        if(candles.get(3).meltTimer != 0)
            tag.putInt("candleMeltTimer4", candles.get(3).meltTimer);

        return tag;
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

//            if (this.level != null)
//                this.level.sendBlockUpdated(this.worldPosition, this.level.getBlockState(this.worldPosition), this.level.getBlockState(this.worldPosition),
//                        Block.UPDATE_CLIENTS);
        }
    }


    public void setDyeColor(int dyeColor){
        this.candles.get(0).dyeColor = dyeColor;
    }
    public void setDyeColor(int candle, int dyeColor){
        this.candles.get(Math.max(0, Math.min(candle, 3))).dyeColor = dyeColor;
    }


    @Override
    public void setChanged() {
        super.setChanged();
        sync();
    }

//    @Override
    public void tick() {
        Random random = new Random();

        int candlesLit = 0;

        if(level != null && level.getBlockEntity(worldPosition) instanceof CandleTile)
        {
            if (candles.get(0).getEffect() != null)
                candles.get(0).getEffect().tick(level, this, candles.get(0));
            if (candles.get(1).getEffect() != null)
                candles.get(1).getEffect().tick(level, this, candles.get(0));
            if (candles.get(2).getEffect() != null)
                candles.get(2).getEffect().tick(level, this, candles.get(0));
            if (candles.get(3).getEffect() != null)
                candles.get(3).getEffect().tick(level, this, candles.get(0));
            if(candles.get(0).lit)
                candlesLit++;
            if(candles.get(1).lit)
                candlesLit++;
            if(candles.get(2).lit)
                candlesLit++;
            if(candles.get(3).lit)
                candlesLit++;
        }

        if(level.getBlockState(worldPosition).hasProperty(Candle.CANDLES_LIT))
            level.setBlock(worldPosition, level.getBlockState(worldPosition).setValue(Candle.CANDLES_LIT, candlesLit).setValue(Candle.LIT, candlesLit > 0), 3);


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
            candles.get(0).type = this.getBlockState().getValue(Candle.SLOT_ONE_TYPE);
            candles.get(0).height = 7;
            candles.get(0).meltTimer = candleMeltTimerMAX;
            startupFlag = true;
        }


        numberOfCandles = 0;

        if(candles.get(0).type != 0)
            numberOfCandles++;
        if(candles.get(1).type != 0)
            numberOfCandles++;
        if(candles.get(2).type != 0)
            numberOfCandles++;
        if(candles.get(3).type != 0)
            numberOfCandles++;


        if(candles.get(0).type != 0)
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


                    if (random.nextInt(10) == 0)
                        level.addParticle(ParticleTypes.FLAME, worldPosition.getX() + 0.5f + xOffset, worldPosition.getY() + 3f / 16f + (float) candles.get(0).height / 16f, worldPosition.getZ() + 0.5f + zOffset, (random.nextDouble() - 0.5d) / 50d, (random.nextDouble() + 0.5d) * 0.015d, (random.nextDouble() - 0.5d) / 50d);
                    if (random.nextInt(10) == 0)
                        level.addParticle(ParticleTypes.SMOKE, worldPosition.getX() + 0.5f + xOffset, worldPosition.getY() + 3f / 16f + (float) candles.get(0).height / 16f, worldPosition.getZ() + 0.5f + zOffset, (random.nextDouble() - 0.5d) / 50d, (random.nextDouble() + 0.5d) * 0.045d, (random.nextDouble() - 0.5d) / 50d);

                } else
                {
                    if (random.nextInt(10) == 0)
                        level.addParticle(ParticleTypes.FLAME, worldPosition.getX() + 0.5f + candles.get(0).x, worldPosition.getY() + 3f / 16f + (float) candles.get(0).height / 16f + candles.get(0).y, worldPosition.getZ() + 0.5f + candles.get(0).z, (random.nextDouble() - 0.5d) / 50d, (random.nextDouble() + 0.5d) * 0.015d, (random.nextDouble() - 0.5d) / 50d);
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
                        candles.get(0).type = 0;
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
        if(candles.get(1).type != 0)
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
                        candles.get(1).type = 0;
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
        if(candles.get(2).type != 0)
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
                        candles.get(2).type = 0;
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
        if(candles.get(3).type != 0)
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
                        candles.get(3).type = 0;
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


        if(candles.get(0).type == 0 && candles.get(1).type == 0 && candles.get(2).type == 0 && candles.get(3).type == 0)
            this.getLevel().destroyBlock(this.getBlockPos(), false);

        if(level.isClientSide) {

            return;
        }

    }

    public void updateCandleSlots() {
//        if(candles.get(0).type == 0)
//        {
//            candles.get(0).type = candles.get(1).type;
//            candles.get(0).height = candles.get(1).height;
//            candles.get(0).meltTimer = candles.get(1).meltTimer;
//            candles.get(0).lit = candles.get(1).lit;
//            candles.get(1).type = 0;
//            candles.get(1).lit = 0;
//            candles.get(1).height = 7;
//            candles.get(1).meltTimer = candleMeltTimerMAX;
//        }
//        if(candles.get(1).type == 0)
//        {
//            candles.get(1).type = candles.get(2).type;
//            candles.get(1).height = candles.get(2).height;
//            candles.get(1).meltTimer = candles.get(2).meltTimer;
//            candles.get(1).lit = candles.get(2).lit;
//            candles.get(2).type = 0;
//            candles.get(2).lit = 0;
//            candles.get(2).height = 7;
//            candles.get(2).meltTimer = candleMeltTimerMAX;
//        }
//        if(candles.get(2).type == 0)
//        {
//            candles.get(2).type = candles.get(3).type;
//            candles.get(2).height = candles.get(3).height;
//            candles.get(2).meltTimer = candles.get(3).meltTimer;
//            candles.get(2).lit = candles.get(3).lit;
//            candles.get(3).type = 0;
//            candles.get(3).lit = 0;
//            candles.get(3).height = 7;
//            candles.get(3).meltTimer = candleMeltTimerMAX;
//        }
    }


}
