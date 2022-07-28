package net.joefoxe.hexerei.util;

import io.netty.buffer.Unpooled;
import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.tileentity.CofferTile;
import net.joefoxe.hexerei.tileentity.HerbJarTile;
import net.joefoxe.hexerei.tileentity.ModTileEntities;
import net.minecraft.client.model.*;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.IRegistryDelegate;
import net.minecraftforge.registries.RegistryObject;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class HexereiUtil {

    public static float moveTo(float input, float movedTo, float speed)
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

    public static String intToRoman(int number)
    {
        String[] thousands = {"", "M", "MM", "MMM"};
        String[] hundreds = {"", "C", "CC", "CCC", "CD", "D", "DC", "DCC", "DCCC", "CM"};
        String[] tens = {"", "X", "XX", "XXX", "XL", "L", "LX", "LXX", "LXXX", "XC"};
        String[] units = {"", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX"};
        return thousands[number / 1000] + hundreds[(number % 1000) / 100] + tens[(number % 100) / 10] + units[number % 10];
    }

    public static float getAngle(Vec3 pos2, Vec3 pos) {
        float angle = (float) Math.toDegrees(Math.atan2(pos.z() - pos2.z(), pos.x() - pos2.z()));

        if(angle < 0){
            angle += 360;
        }

        return angle;
    }

    public static CompoundTag saveAllItemsWithName(CompoundTag p_18977_, NonNullList<ItemStack> p_18978_, boolean p_18979_, String name) {
        ListTag listtag = new ListTag();

        for(int i = 0; i < p_18978_.size(); ++i) {
            ItemStack itemstack = p_18978_.get(i);
            if (!itemstack.isEmpty()) {
                CompoundTag compoundtag = new CompoundTag();
                compoundtag.putByte("Slot", (byte)i);
                itemstack.save(compoundtag);
                listtag.add(compoundtag);
            }
        }

        if (!listtag.isEmpty() || p_18979_) {
            p_18977_.put(name, listtag);
        }

        return p_18977_;
    }

    public static void loadAllItemsWithName(CompoundTag p_18981_, NonNullList<ItemStack> p_18982_, String name) {
        ListTag listtag = p_18981_.getList(name, 10);

        for(int i = 0; i < listtag.size(); ++i) {
            CompoundTag compoundtag = listtag.getCompound(i);
            int j = compoundtag.getByte("Slot") & 255;
            if (j >= 0 && j < p_18982_.size()) {
                p_18982_.set(j, ItemStack.of(compoundtag));
            }
        }

    }

    public static int getColorValue(DyeColor color) {
        if (color == null)
            return 0;
        float[] colors = color.getTextureDiffuseColors();
        int r = (int) (colors[0] * 255.0F);
        int g = (int) (colors[1] * 255.0F);
        int b = (int) (colors[2] * 255.0F);
        return (r << 16) | (g << 8) | b;
    }

    public static int getColorValue(float r, float g, float b) {
        int r2 = (int) (r * 255.0F);
        int g2 = (int) (g * 255.0F);
        int b2 = (int) (b * 255.0F);
        return (r2 << 16) | (g2 << 8) | b2;
    }


    public static int getColorValueAlpha(float r, float g, float b, float a) {
        int r2 = (int) (r * 255.0F);
        int g2 = (int) (g * 255.0F);
        int b2 = (int) (b * 255.0F);
        int a2 = (int) (a * 255.0F);
        return (a2 << 24) |  (r2 << 16) | (g2 << 8) | b2;
    }


    public static int getColorStatic(ItemStack p_41122_) {
        CompoundTag compoundtag = p_41122_.getTagElement("display");
        return compoundtag != null && compoundtag.contains("color", 99) ? compoundtag.getInt("color") : 0x422F1E;
    }

    public static DyeColor getDyeColorNamed(String name) {
        return getDyeColorNamed(name, 0);
    }

    public static DyeColor getDyeColorNamed(String name, int offset) {

        return getDyeColorNamed(name, offset, 0);
    }

    public static DyeColor getDyeColorNamed(String name, int offset, int offset2) {

        if(name.equals("jeb_"))
            return DyeColor.byId((int)((((Hexerei.getClientTicks() + offset2)/40) + offset) % 16));

        if(name.equals("les_"))
            return DyeColor.byId(switch((int)((((Hexerei.getClientTicks() + offset2)/40) + offset) % 5)) {
                case 1 -> 1;
                case 2 -> 2;
                case 3 -> 6;
                case 4 -> 14;
                default -> 0;
            });

        if(name.equals("bi_"))
            return DyeColor.byId(switch((int)((((Hexerei.getClientTicks() + offset2)/40) + offset) % 3)) {
                case 1 -> 10;
                case 2 -> 11;
                default -> 2;
            });

        if(name.equals("trans_"))
            return DyeColor.byId(switch((int)((((Hexerei.getClientTicks() + offset2)/40) + offset) % 3)) {
                case 1 -> 3;
                case 2 -> 0;
                default -> 6;
            });

        if(name.equals("joe_"))
            return DyeColor.byId(switch((int)((((Hexerei.getClientTicks() + offset2)/40) + offset) % 4)) {
                case 1, 3 -> 3;
                case 2 -> 9;
                default -> 11;
            });

//        if(this.getName().getString().equals("les_"))
//            return DyeColor.byId(switch((int)(((Hexerei.getClientTicks())/40) % 15)) {
//                case 1 -> 0;
//                case 2 -> 0;
//                case 3 -> 0;
//                case 4 -> 1;
//                case 5 -> 1;
//                case 6 -> 1;
//                case 7 -> 2;
//                case 8 -> 2;
//                case 9 -> 2;
//                case 10 -> 6;
//                case 11 -> 6;
//                case 12 -> 6;
//                case 13 -> 14;
//                case 14 -> 14;
//                case 15 -> 14;
//                default -> 0;
//            });


        //DyeColor.byId((int)(((Hexerei.getClientTicks())/40) % 16));
        return null;
    }


    @OnlyIn(Dist.CLIENT)
    public static float getMaxHeadXRot(ModelPart head) {
        return Mth.clamp(head.xRot, (-(float) Math.PI / 2.5F), ((float) Math.PI / 3F));
    }

    @OnlyIn(Dist.CLIENT)
    public static <T extends LivingEntity> void animateHands(HumanoidModel<T> model, T entity, boolean leftHand) {

        ModelPart mainHand = leftHand ? model.leftArm : model.rightArm;
        ModelPart offHand = leftHand ? model.rightArm : model.leftArm;

        Vec3 bx = new Vec3(1, 0, 0);
        Vec3 by = new Vec3(0, 1, 0);
        Vec3 bz = new Vec3(0, 0, 1);

        //head rot + hand offset from flute

        float downFacingRot = Mth.clamp(model.head.xRot, 0f, 0.8f);

        float xRot = getMaxHeadXRot(model.head) - (entity.isCrouching() ? 1F : 0.0F)
                - 0.3f + downFacingRot * 0.5f;

        bx = bx.xRot(xRot);
        by = by.xRot(xRot);
        bz = bz.xRot(xRot);

        Vec3 armVec = new Vec3(0, 0, 1);

        float mirror = leftHand ? -1 : 1;

        armVec = armVec.yRot(-0.99f * mirror);

        Vec3 newV = bx.scale(armVec.x).add(by.scale(armVec.y)).add(bz.scale(armVec.z));


        float yaw = (float) Math.atan2(-newV.x, newV.z);
        float len = (float) newV.length();

        float pitch = (float) Math.asin(newV.y / len);

        mainHand.yRot = (yaw + model.head.yRot * 1.4f - 0.1f * mirror) - 0.5f * downFacingRot * mirror;
        mainHand.xRot = (float) (pitch - Math.PI / 2f);


        offHand.yRot = (float) Mth.clamp((mainHand.yRot - 1 * mirror) * 0.2, -0.15, 0.15) + 1.1f * mirror;
        offHand.xRot = mainHand.xRot - 0.06f;


        //shoulder joint hackery
        float offset = leftHand ? -Mth.clamp(model.head.yRot, -1, 0) :
                Mth.clamp(model.head.yRot, 0, 1);

        // model.rightArm.x = -5.0F + offset * 2f;
        mainHand.z = -offset * 1f;

        // model.leftArm.x = -model.rightArm.x;
        // model.leftArm.z = -model.rightArm.z;

        //hax. unbobs left arm
        AnimationUtils.bobModelPart(model.leftArm, entity.tickCount, 1.0F);
        AnimationUtils.bobModelPart(model.rightArm, entity.tickCount, -1.0F);
    }


    public static float[] rgbIntToFloatArray(int rgbInt) {
        int r = (rgbInt >> 16) & 255;
        int g = (rgbInt >> 8) & 255;
        int b = (rgbInt >> 0) & 255;

        return new float[] {r / 255F, g / 255F, b / 255F};
    }

    public static int[] rgbIntToIntArray(int rgbInt) {
        int r = (rgbInt >> 16) & 255;
        int g = (rgbInt >> 8) & 255;
        int b = (rgbInt >> 0) & 255;

        return new int[] {r, g, b};
    }

    public static boolean entityIsHostile(Entity entity) {
        if (entity.getType().getCategory().equals(MobCategory.MONSTER)) {
            return true;
        }

        return false;
    }

    public static List<BlockPos> getAllTileEntityPositionsNearby(BlockEntityType<?> te, Integer radius, Level world, Entity entity) {
        BlockPos entitypos = entity.blockPosition();

        List<BlockPos> nearby = new ArrayList<BlockPos>();
        List<BlockEntity> tiles = getTileEntitiesAroundPosition(world, entitypos, radius);

        for (BlockEntity tile : tiles) {
            BlockEntityType<?> tileType = tile.getType();
            if (tileType == null) {
                continue;
            }

            if (tileType.equals(te)) {
                BlockPos tilePos = tile.getBlockPos();
                if (tilePos.closerThan(new Vec3i(entity.position().x, entity.position().y, entity.position().z), radius)) {
                    nearby.add(tile.getBlockPos());
                }
            }
        }

        return nearby;
    }

    public static List<BlockPos> getAllToggledCofferAndHerbJarPositionsNearby(Integer radius, Level world, Entity entity) {
        BlockPos entitypos = entity.blockPosition();

        List<BlockPos> nearby = new ArrayList<BlockPos>();
        List<BlockEntity> tiles = getTileEntitiesAroundPosition(world, entitypos, radius);

        for (BlockEntity tile : tiles) {
            BlockEntityType<?> tileType = tile.getType();
            if (tileType == null) {
                continue;
            }

            if (tileType.equals(ModTileEntities.COFFER_TILE.get())) {
                BlockPos tilePos = tile.getBlockPos();
                if (tilePos.closerThan(new Vec3i(entity.position().x, entity.position().y, entity.position().z), radius)) {
                    if (((CofferTile) tile).buttonToggled != 0){

                        for(int i = 0; i < ((CofferTile) tile).itemHandler.getSlots(); i++)
                        {
                                nearby.add(tile.getBlockPos());
                                break;
                        }
                    }
                }
            }else if (tileType.equals(ModTileEntities.HERB_JAR_TILE.get())) {
                BlockPos tilePos = tile.getBlockPos();
                if (tilePos.closerThan(new Vec3i(entity.position().x, entity.position().y, entity.position().z), radius)) {
                    if (((HerbJarTile) tile).buttonToggled != 0){


                        nearby.add(tile.getBlockPos());

                    }
                }
            }
        }

        return nearby;
    }

    private static List<BlockEntity> getTileEntitiesAroundPosition(Level world, BlockPos pos, Integer radius) {
        List<BlockEntity> blockentities = new ArrayList<BlockEntity>();

        int chunkradius = (int)Math.ceil(radius/16.0);
        int chunkPosX = pos.getX() >> 4;
        int chunkPosZ = pos.getZ() >> 4;

        for (int x = chunkPosX - chunkradius; x < chunkPosX + chunkradius; x++) {
            for (int z = chunkPosZ - chunkradius; z < chunkPosZ + chunkradius; z++) {

                if(world.hasChunk(x, z)){
                    Iterator<BlockEntity> iterator = world.getChunk(x, z).getBlockEntities().values().iterator();
                    while (iterator.hasNext()) {
                        BlockEntity be = iterator.next();
                        if (!blockentities.contains(be)) {
                            blockentities.add(be);
                        }
                    }
                }
            }
        }

        return blockentities;
    }

    public static ResourceLocation getResource(String name) {
        return getResource(HexereiConstants.MOD_ID, name);
    }

    public static ResourceLocation getResource(String modId, String name) {
        return new ResourceLocation(modId, name);
    }

    public static String getResourcePath(String name) {
        return getResourcePath(HexereiConstants.MOD_ID, name);
    }

    public static String getResourcePath(String modId, String name) {
        return getResource(modId, name).toString();
    }

    public static FriendlyByteBuf createBuf() {
        return new FriendlyByteBuf(Unpooled.buffer());
    }

    public static <T> T make(Supplier<T> supplier) {
        return supplier.get();
    }

    public static <T> T make(T object, Consumer<T> consumer) {
        consumer.accept(object);
        return object;
    }


    public static BlockState getBlockStateWithExistingProperties(BlockState oldState, BlockState newState) {
        BlockState finalState = newState;
        for (Property<?> property : oldState.getProperties()) {
            if (newState.hasProperty(property)) {
                finalState = newStateWithOldProperty(oldState, finalState, property);
            }
        }
        return finalState;
    }

    public static BlockState setBlockStateWithExistingProperties(Level level, BlockPos pos, BlockState newState, int flags) {
        BlockState oldState = level.getBlockState(pos);
        BlockState finalState = getBlockStateWithExistingProperties(oldState, newState);
        level.sendBlockUpdated(pos, oldState, finalState, flags);
        level.setBlock(pos, finalState, flags);
        return finalState;
    }

    public static <T extends Comparable<T>> BlockState newStateWithOldProperty(BlockState oldState, BlockState newState, Property<T> property) {
        return newState.setValue(property, oldState.getValue(property));
    }

    public static <T extends IForgeRegistryEntry<? super T>> RegistryObject<T> acceptOrElse(RegistryObject<T> opt, Consumer<T> consumer, Runnable orElse) {
        if (opt.isPresent()) {
            consumer.accept(opt.get());
        } else {
            orElse.run();
        }
        return opt;
    }

    public static <T> Optional<T> acceptOrElse(Optional<T> opt, Consumer<T> consumer, Runnable orElse) {
        if (opt.isPresent()) {
            consumer.accept(opt.get());
        } else {
            orElse.run();
        }
        return opt;
    }

    public static <T> boolean allMatch(Iterable<T> input, Predicate<T> matcher) {
        Objects.requireNonNull(matcher);
        for (T e : input) {
            if (!matcher.test(e)) {
                return false;
            }
        }
        return true;
    }

    public static <T> boolean anyMatch(Iterable<T> input, Predicate<T> matcher) {
        Objects.requireNonNull(matcher);
        for (T e : input) {
            if (matcher.test(e)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Takes various registry related objects and returns the
     * registry id of the object it is representing
     */
    public static ResourceLocation getRegistryId(Object obj) {
        if (obj instanceof ResourceLocation) {
            return (ResourceLocation) obj;
        }
        if (obj instanceof String) {
            // Returns null when namespace or path contain invalid charcters
            return ResourceLocation.tryParse((String) obj);
        }
        if (obj instanceof IForgeRegistryEntry) {
            return ((IForgeRegistryEntry) obj).getRegistryName();
        }
        if (obj instanceof IRegistryDelegate) {
            return ((IRegistryDelegate) obj).name();
        }
        if (obj instanceof RegistryObject) {
            return ((RegistryObject) obj).getId();
        }
        return null;
    }
}