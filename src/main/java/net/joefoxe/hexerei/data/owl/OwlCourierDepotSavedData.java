package net.joefoxe.hexerei.data.owl;

import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.util.HexereiPacketHandler;
import net.joefoxe.hexerei.util.message.ClientboundOwlCourierDepotDataInventoryPacket;
import net.joefoxe.hexerei.util.message.ClientboundOwlCourierDepotDataPacket;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.saveddata.SavedData;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.*;

//@EventBusSubscriber(modid = Hexerei.MOD_ID)
public class OwlCourierDepotSavedData extends SavedData {
    protected static final String DATA_NAME = Hexerei.MOD_ID + "_owl_courier_depot";

    Map<GlobalPos, OwlCourierDepotData> depots = new HashMap<>();

    public OwlCourierDepotSavedData() {

    }

    public OwlCourierDepotSavedData addOwlCourierDepot(String name, GlobalPos pos) {
        for (Map.Entry<GlobalPos, OwlCourierDepotData> entry : depots.entrySet()) {
            if (entry.getValue().name.equals(name)) {
//                System.out.println("Already contains same name - somehow slipped past the precheck");
                return this;
            }
            if (entry.getKey().equals(pos)) {
//                System.out.println("Already contains same block pos - somehow slipped past the precheck");
                return this;
            }
        }

        depots.put(pos, new OwlCourierDepotData(name));
        syncToClient();
        this.setDirty();
        return this;
    }

    public Map<GlobalPos, OwlCourierDepotData> getDepots() {
        return depots;
    }

    public void syncToClient() {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server != null)
            HexereiPacketHandler.sendToAllPlayers(new ClientboundOwlCourierDepotDataPacket(save(new CompoundTag(), server.registryAccess())), server);
    }

    public void syncToClient(Player player) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server != null && player instanceof ServerPlayer serverPlayer)
            HexereiPacketHandler.sendToPlayerClient(new ClientboundOwlCourierDepotDataPacket(save(new CompoundTag(), server.registryAccess())), serverPlayer);
    }

    public void syncInvToClient(GlobalPos pos) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server != null)
            HexereiPacketHandler.sendToAllPlayers(new ClientboundOwlCourierDepotDataInventoryPacket(invNbt(pos, new CompoundTag())), server);
    }

    public void clearOwlCourierDepot(GlobalPos pos) {
        for (Map.Entry<GlobalPos, OwlCourierDepotData> entry : depots.entrySet()) {
            GlobalPos depotPos = entry.getKey();
            if (pos.equals(depotPos)) {
                depots.remove(entry.getKey());
                //send packet to remove this pos for clients instead of syncing the whole
                this.setDirty();
                syncToClient();
                break;
            }
        }
    }

    public void tick(ServerLevel serverLevel) {
        // maybe check every once in a while if the depots are still existing, maybe some mod moved them somehow
    }


//    @SubscribeEvent
//    public static void serverTickEvent(ServerTickEvent event) {
////        if (event.phase == TickEvent.Phase.START)
////            OwlCourierDepotSavedData.get(event.getServer().overworld()).tick(event.getServer().overworld());
//    }

    private static OwlCourierDepotSavedData create(CompoundTag tag, HolderLookup.Provider registries) {
        OwlCourierDepotSavedData data = new OwlCourierDepotSavedData();
        data.load(tag, registries);
        return data;
    }

    public void load(CompoundTag pCompoundTag, HolderLookup.Provider registries) {

        if (pCompoundTag.contains("depots")) {
            ListTag depotList = pCompoundTag.getList("depots", Tag.TAG_COMPOUND);

            for (int i = 0; i < depotList.size(); i++) {
                CompoundTag depotTag = depotList.getCompound(i);
                String depotName = depotTag.getString("DepotName");

                Optional<GlobalPos> pos = GlobalPos.CODEC.parse(NbtOps.INSTANCE, depotTag.get("Pos")).result();

                OwlCourierDepotData depotData = new OwlCourierDepotData(depotName);
                ContainerHelper.loadAllItems(depotTag, depotData.items, registries);

                pos.ifPresent(globalPos -> depots.put(globalPos, depotData));
            }
        }
    }

    @Override
    public CompoundTag save(CompoundTag pCompoundTag, HolderLookup.Provider registries) {

        ListTag depotList = new ListTag();

        for (Map.Entry<GlobalPos, OwlCourierDepotData> entry : depots.entrySet()) {
            CompoundTag depotTag = new CompoundTag();
            depotTag.putString("DepotName", entry.getValue().name);

            Optional<Tag> tag = GlobalPos.CODEC.encodeStart(NbtOps.INSTANCE, entry.getKey()).result();
            tag.ifPresent(value -> depotTag.put("Pos", value));


            ContainerHelper.saveAllItems(depotTag, entry.getValue().items, registries);
            depotList.add(depotTag);
        }

        pCompoundTag.put("depots", depotList);
        return pCompoundTag;
    }

    public CompoundTag invNbt(GlobalPos pos, CompoundTag pCompoundTag) {

        Optional<Tag> tag = GlobalPos.CODEC.encodeStart(NbtOps.INSTANCE, pos).result();
        tag.ifPresent(value -> pCompoundTag.put("Pos", value));

        ContainerHelper.saveAllItems(pCompoundTag, depots.get(pos).items, Hexerei.DynamicRegistries.get());

        return pCompoundTag;
    }


    public static SavedData.Factory<OwlCourierDepotSavedData> factory() {
        return new SavedData.Factory<>(OwlCourierDepotSavedData::new, OwlCourierDepotSavedData::create, null);
    }

    public static OwlCourierDepotSavedData get(ServerLevel world) {
        return world.getServer().overworld()
                .getDataStorage().computeIfAbsent(factory(), DATA_NAME);
    }
    public static OwlCourierDepotSavedData get() {
        return ServerLifecycleHooks.getCurrentServer().overworld()
                .getDataStorage().computeIfAbsent(factory(), DATA_NAME);
    }
}
