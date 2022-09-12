package meteordevelopment.meteorclient.systems.modules.misc;

import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.world.Timer;
import meteordevelopment.orbit.EventHandler;

import static meteordevelopment.meteorclient.utils.TimerUtils.getTPSMatch;

public class TPSSync extends Module {

    public TPSSync() {
        super(Categories.Misc, "tps-sync", "Adds a general TPS Sync module.");
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (!isActive()) return;

        Modules.get().get(Timer.class).setOverride(getTPSMatch(true));
    }
}
