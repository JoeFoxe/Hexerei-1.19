package net.joefoxe.hexerei.event;

import net.joefoxe.hexerei.Hexerei;
import net.joefoxe.hexerei.command.ToggleLightCommand;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

/**
 * For queuing deferred or over-time tasks. Tick refers to the Server or Client Tick event.
 */
@SuppressWarnings("ForLoopReplaceableByForEach")
@Mod.EventBusSubscriber(modid = Hexerei.MOD_ID)
public class EventQueue {
    List<ITimedEvent> events;

    public void tick(TickEvent tickEvent) {
        if (events == null || events.isEmpty()) {
            return;
        }

        List<ITimedEvent> stale = new ArrayList<>();
        // Enhanced-for or iterator will cause a concurrent modification.
        for (int i = 0; i < events.size(); i++) {
            ITimedEvent event = events.get(i);
            if (event.isExpired()) {
                stale.add(event);
            } else {
                event.tickEvent(tickEvent);
            }
        }
        this.events.removeAll(stale);
    }

    public void addEvent(ITimedEvent event) {
        if (events == null)
            events = new ArrayList<>();
        events.add(event);
    }

    public static EventQueue getServerInstance() {
        if (serverQueue == null)
            serverQueue = new EventQueue();
        return serverQueue;
    }

    public static EventQueue getClientQueue() {
        if (clientQueue == null)
            clientQueue = new EventQueue();
        return clientQueue;
    }


    // Tear down on world unload
    public void clear() {
        this.events = null;
    }

    // Split these because our integrated servers are CURSED and both tick.
    private static EventQueue serverQueue;
    private static EventQueue clientQueue;

    private EventQueue() {
        events = new ArrayList<>();
    }

    @SubscribeEvent
    public static void serverTick(TickEvent.ServerTickEvent e) {

        if (e.phase != TickEvent.Phase.END)
            return;

        EventQueue.getServerInstance().tick(e);
    }

    @SubscribeEvent
    public static void clientTickEvent(TickEvent.ClientTickEvent e) {

        if (e.phase != TickEvent.Phase.END)
            return;

        EventQueue.getClientQueue().tick(e);
    }

    @SubscribeEvent
    public static void commandRegister(RegisterCommandsEvent event) {
        ToggleLightCommand.register(event.getDispatcher());
    }
}
