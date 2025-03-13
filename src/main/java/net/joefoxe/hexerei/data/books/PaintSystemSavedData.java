package net.joefoxe.hexerei.data.books;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.util.HexereiPacketHandler;
import net.joefoxe.hexerei.util.HexereiUtil;
import net.joefoxe.hexerei.util.message.ClientboundPaintData;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.saveddata.SavedData;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PaintSystemSavedData extends SavedData {
    protected static final String DATA_NAME = Hexerei.MOD_ID + "_book_paint_data";
    private Map<BookPageIdentifier, PaintData> paintDataMap = new HashMap<>();

    public static final Codec<PaintSystemSavedData> CODEC = Codec.unboundedMap(
            BookPageIdentifier.CODEC,
            PaintData.CODEC
    ).xmap(
            map -> {
                PaintSystemSavedData data = new PaintSystemSavedData();
                data.paintDataMap.putAll(map);
                return data;
            },
            data -> data.paintDataMap
    );

    private static PaintSystemSavedData create(CompoundTag tag, HolderLookup.Provider registries) {
        PaintSystemSavedData data = new PaintSystemSavedData();
        data.load(tag, registries);
        return data;
    }
    // Load/save methods and utilities
    public void load(CompoundTag tag, HolderLookup.Provider registries) {
        paintDataMap.clear();
        if (tag.contains("paintDataMap")){
            ListTag listTag = tag.getList("paintDataMap", Tag.TAG_COMPOUND);
            for (Tag value : listTag) {
                PaintData pd = PaintData.CODEC.parse(NbtOps.INSTANCE, value).getOrThrow();
                paintDataMap.put(new BookPageIdentifier(pd.page, pd.uuid), pd);
            }
        }
    }

    @Override
    public @NotNull CompoundTag save(CompoundTag tag, HolderLookup.Provider provider) {
        ListTag listTag = new ListTag();
        for(PaintData data : this.paintDataMap.values())
            listTag.add(PaintData.CODEC.encodeStart(NbtOps.INSTANCE, data).getOrThrow());
        tag.put("paintDataMap", listTag);
//        DataResult<Tag> result = CODEC.encodeStart(NbtOps.INSTANCE, this);
        return tag;
    }

    public void sendToClient(ServerPlayer player) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server != null) {
            for (PaintData data : paintDataMap.values()) {
                HexereiPacketHandler.sendToPlayerClient(new ClientboundPaintData(data), player);
            }
        }
    }

    public static void sendToClients() {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();

        if (server != null) {
            for (PaintData data : get().paintDataMap.values()) {
                HexereiPacketHandler.sendToAllPlayers(new ClientboundPaintData(data), server);
            }
        }
    }

    public PaintData getOrCreatePaintData(ResourceLocation bookLoc, UUID uuid) {
        BookPageIdentifier bookPageIdentifier = new BookPageIdentifier(bookLoc, uuid);
        if (!paintDataMap.containsKey(bookPageIdentifier))
            paintDataMap.put(bookPageIdentifier, new PaintData(32, 32, new ArrayList<>(), bookLoc, uuid));
        return paintDataMap.get(bookPageIdentifier);
    }

    public PaintData getFirst() {
        return paintDataMap.values().stream().findFirst().orElseGet(() -> new PaintData(16, 16, new ArrayList<>(), HexereiUtil.getResource("book_of_colors/page1"), new UUID(0, 0)));
    }

    public void putPaintData(PaintData data) {
        paintDataMap.put(new BookPageIdentifier(data.page, data.uuid), data);
        setDirty();
    }

    public record BookPageIdentifier(ResourceLocation bookLocation, UUID uuid) {
        public static final Codec<BookPageIdentifier> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("page").forGetter(BookPageIdentifier::bookLocation),
                UUIDUtil.CODEC.fieldOf("uuid").forGetter(BookPageIdentifier::uuid)
        ).apply(instance, BookPageIdentifier::new));

        public static final StreamCodec<ByteBuf, BookPageIdentifier> STREAM_CODEC = ByteBufCodecs.fromCodec(CODEC);
    }

    public static SavedData.Factory<PaintSystemSavedData> factory() {
        return new SavedData.Factory<>(PaintSystemSavedData::new, PaintSystemSavedData::create, null);
    }

    public static PaintSystemSavedData get() {
        return ServerLifecycleHooks.getCurrentServer().overworld()
                .getDataStorage().computeIfAbsent(factory(), DATA_NAME);
    }
}