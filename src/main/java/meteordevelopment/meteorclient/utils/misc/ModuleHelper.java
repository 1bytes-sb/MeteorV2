package meteordevelopment.meteorclient.utils.misc;

import meteordevelopment.meteorclient.systems.modules.chat.AutoEZ;
import meteordevelopment.meteorclient.systems.modules.chat.PopCounter2;
import meteordevelopment.meteorclient.systems.modules.combat.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.movement.speed.Speed;
import net.minecraft.entity.player.PlayerEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ModuleHelper {


    public static void queueEZ(PlayerEntity target) {
        Modules.get().get(AutoEZ.class).qEz(target);
    }

    public static List<ReaperModule> combatModules = new ArrayList<>(Arrays.asList(
        Modules.get().get(AnchorGod.class),
        Modules.get().get(Surroundbreaker.class),
        //Modules.get().get(AutoCrystal.class),
        Modules.get().get(BedGod.class)
        //Modules.get().get(CevBreaker.class),
        //Modules.get().get(PistonAura.class),
    ));

    public static void disableCombat() {
        combatModules.forEach(reaperModule -> {
            if (reaperModule.isActive()) reaperModule.toggle();
        });
    }

    public static void disableMovement() {
        Speed speed = Modules.get().get(Speed.class);
        TargetStrafe targetStrafe = Modules.get().get(TargetStrafe.class);
        if (speed.isActive()) speed.toggle();
        if (targetStrafe.isActive()) targetStrafe.toggle();
    }


    public static void disableCombat(Module parent) {
        for (Module m : combatModules) {
            if (m.equals(parent)) continue;
            if (m.isActive()) m.toggle();
        }
    }

}
