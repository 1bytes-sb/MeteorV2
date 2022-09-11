package meteordevelopment.meteorclient.systems.modules.world;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.meteorclient.systems.modules.Categories;

public class Voider extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Integer> radius = sgGeneral.add(new IntSetting.Builder()
        .name("radius")
        .description("radius")
        .defaultValue(90)
        .sliderRange(1, 90)
        .build());

    public Voider() {
        super(Categories.World, "voider", "erekrjskjfofhsfhqe");
    }

    int i = 319;

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if (!(mc.player.hasPermissionLevel(4))) {
            toggle();
            error("must have op");
        }
        i--;
        ChatUtils.sendPlayerMsg("/fill ~-" + radius.get() + " " + i + " ~-" + radius.get() + " ~" + radius.get() + " " + i + " ~" + radius.get() + " air");
        if (i == -64) {
            i = 319;
            toggle();
        }
    }
}
