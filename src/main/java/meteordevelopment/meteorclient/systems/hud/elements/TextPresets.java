package meteordevelopment.meteorclient.systems.hud.elements;

import meteordevelopment.meteorclient.systems.hud.Hud;
import meteordevelopment.meteorclient.systems.hud.HudElementInfo;
import meteordevelopment.meteorclient.systems.hud.elements.TextHud;

public class TextPresets {
    public static final HudElementInfo<TextHud> INFO = new HudElementInfo<>(Hud.GROUP, "text2", "Displays arbitrary text with Starscript.", TextPresets::create);

    static {
        addPreset("Kills", "Kills: #1{meteor.kills}", 0);
        addPreset("Deaths", "Deaths: #1{meteor.deaths}", 0);
        addPreset("KDR", "KDR: #1{meteor.kdr}", 0);
        addPreset("Highscore", "Highscore: #1{meteor.highscore}", 0);
        addPreset("Killstreak", "Killstreak: #1{meteor.killstreak}", 0);
        addPreset("Crystals/s", "Crystals/s: #1{meteor.crystalsps}", 0);
    }

    private static TextHud create() {
        return new TextHud(INFO);
    }

    private static HudElementInfo<TextHud>.Preset addPreset(String title, String text, int updateDelay) {
        return INFO.addPreset(title, textHud -> {
            if (text != null) textHud.text.set(text);
            if (updateDelay != -1) textHud.updateDelay.set(updateDelay);
        });
    }
}