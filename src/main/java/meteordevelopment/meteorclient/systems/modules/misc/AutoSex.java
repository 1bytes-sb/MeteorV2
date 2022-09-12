package meteordevelopment.meteorclient.systems.modules.misc;

import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.events.meteor.MouseButtonEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.config.Config;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.entity.SortPriority;
import meteordevelopment.meteorclient.utils.entity.TargetUtils;
import meteordevelopment.meteorclient.utils.misc.Keybind;
import meteordevelopment.meteorclient.utils.misc.input.KeyAction;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.orbit.EventPriority;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

import java.util.List;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_MIDDLE;

public class AutoSex extends Module{
    public enum Mode {
        MiddleClick,
        BindClick,
        Automatic
    }


    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgSex = settings.createGroup("Auto Sex");


    // General
    private final Setting<Mode> targetMode = sgGeneral.add(new EnumSetting.Builder<Mode>()
            .name("target-mode")
            .description("The mode at which to follow the player.")
            .defaultValue(Mode.BindClick)
            .build()
    );

    private final Setting<Keybind> keybind = sgGeneral.add(new KeybindSetting.Builder()
            .name("keybind")
            .description("What key to press to start following someone.")
            .defaultValue(Keybind.fromKey(-1))
            .visible(() -> targetMode.get() == Mode.BindClick)
            .build()
    );

    private final Setting<SortPriority> priority = sgGeneral.add(new EnumSetting.Builder<SortPriority>()
            .name("target-priority")
            .description("How to select the player to target.")
            .defaultValue(SortPriority.LowestDistance)
            .visible(() -> targetMode.get() == Mode.Automatic)
            .build()
    );

    private final Setting<Boolean> ignoreRange = sgGeneral.add(new BoolSetting.Builder()
            .name("ignore-range")
            .description("Follow the player even if they are out of range.")
            .defaultValue(false)
            .visible(() -> targetMode.get() == Mode.Automatic)
            .build()
    );

    private final Setting<Double> targetRange = sgGeneral.add(new DoubleSetting.Builder()
            .name("target-range")
            .description("The range in which it follows a random player.")
            .defaultValue(10)
            .range(1,50)
            .visible(() -> targetMode.get() == Mode.Automatic && !ignoreRange.get())
            .build()
    );

    private final Setting<Boolean> onlyFriend = sgGeneral.add(new BoolSetting.Builder()
            .name("only-friends")
            .description("Whether or not to only follow friends.")
            .defaultValue(false)
            .build()
    );

    private final Setting<Boolean> onlyOther = sgGeneral.add(new BoolSetting.Builder()
            .name("ignore-friends")
            .description("Whether or not to follow friends.")
            .defaultValue(false)
            .visible(() -> targetMode.get() != Mode.Automatic)
            .build()
    );


    private final Setting<Boolean> message = sgGeneral.add(new BoolSetting.Builder()
            .name("message")
            .description("Sends a message to the player when you start/stop following them.")
            .defaultValue(false)
            .build()
    );


    // Sex
    private final Setting<Boolean> twerkWhenClose = sgSex.add(new BoolSetting.Builder()
            .name("auto-hump")
            .description("Crouch against the target to give the appearance of sex OwO.")
            .defaultValue(false)
            .build()
    );

    private final Setting<Boolean> dirtyTalk = sgSex.add(new BoolSetting.Builder()
            .name("dirty-talk")
            .description("Whisper naughty things in your enemy's ear.")
            .defaultValue(true)
            .visible(message::get)
            .build()
    );

    private final Setting<Boolean> dm = sgGeneral.add(new BoolSetting.Builder()
            .name("private-msg")
            .description("Sends a private chat msg to the person.")
            .defaultValue(false)
            .visible(message::get)
            .build()
    );

    private final Setting<Boolean> pm = sgGeneral.add(new BoolSetting.Builder()
            .name("public-msg")
            .description("Sends a public chat msg.")
            .defaultValue(false)
            .visible(message::get)
            .build()
    );

    private final Setting<Integer> delay = sgSex.add(new IntSetting.Builder()
            .name("delay")
            .description("The delay between specified messages in ticks.")
            .defaultValue(20)
            .min(0)
            .sliderMax(200)
            .visible(message::get)
            .build()
    );

    private final Setting<Boolean> random = sgSex.add(new BoolSetting.Builder()
            .name("randomise")
            .description("Selects a random message from your spam message list.")
            .defaultValue(false)
            .visible(message::get)
            .build()
    );

    private final Setting<List<String>> messages = sgSex.add(new StringListSetting.Builder()
            .name("messages")
            .description("Messages to use for dirty talk.")
            .defaultValue(List.of(
                    "God, I love you so much (enemy)~",
                    "Ahhhh! Fuck me harder (enemy)!",
                    "Please put your cock inside me (enemy)!",
                    "I want to choke on your cock (enemy)!",
                    "Oh god, you're so big (enemy)!",
                    "Treat me like a whore!",
                    "Ahhhhn! Fuck me deeper (enemy)!",
                    "Fill me with your spunk (enemy)~!",
                    "Demolish my bussy (enemy)!~"
            ))
            .visible(message::get)
            .build()
    );


    public AutoSex() {
        super(Categories.Misc, "auto-Sex", "Tries to have sex with the player in different ways.");
    }


    private int messageI, timer;


    @Override
    public void onActivate() {
        timer = delay.get();
        messageI = 0;
    }

    boolean isFollowing = false;
    String playerName;
    Entity playerEntity;
    float dis = 1.5f;

    //middle click mode
    @EventHandler
    private void onMouseButton(MouseButtonEvent event) {
        if(targetMode.get() == Mode.MiddleClick){
            if (event.action == KeyAction.Press && event.button == GLFW_MOUSE_BUTTON_MIDDLE && mc.currentScreen == null && mc.targetedEntity != null && mc.targetedEntity instanceof PlayerEntity) {
                if (!isFollowing) {

                    if (!Friends.get().isFriend((PlayerEntity) mc.targetedEntity) && onlyFriend.get()) return;
                    if (Friends.get().isFriend((PlayerEntity) mc.targetedEntity) && onlyOther.get()) return;

                    mc.player.sendChatMessage(Config.get().prefix.get() + "baritone follow player " + mc.targetedEntity.getEntityName(), Text.literal(Config.get().prefix.get() + "baritone follow player " + mc.targetedEntity.getEntityName()));

                    playerName = mc.targetedEntity.getEntityName();
                    playerEntity = mc.targetedEntity;

                    if (message.get()) {
                        startMsg();
                    }

                    isFollowing = true;
                } else {
                    mc.player.sendChatMessage(Config.get().prefix.get() + "baritone stop", Text.literal(Config.get().prefix.get() + "baritone stop"));

                    if (message.get()) {
                        endMsg();
                    }

                    playerName = null;
                    isFollowing = false;
                }
            }
            else if(event.action == KeyAction.Press && event.button == GLFW_MOUSE_BUTTON_MIDDLE && isFollowing){
                mc.player.sendChatMessage(Config.get().prefix.get() + "baritone stop", Text.literal(Config.get().prefix.get() + "baritone stop"));

                if (message.get()) {
                    endMsg();
                }
                playerName = null;
                isFollowing = false;
            }
        }
    }

    int iPublic;
    boolean pressed = false;
    boolean alternate = true;

    @EventHandler (priority = EventPriority.LOW)
    private void onTick(TickEvent.Post event) {

        if(targetMode.get() == Mode.BindClick && keybind != null)
        {
            if(keybind.get().isPressed() && !pressed && !alternate)
            {
                if (isFollowing)
                {
                    mc.player.sendChatMessage(Config.get().prefix.get() + "baritone stop", Text.literal(Config.get().prefix.get() + "baritone stop"));

                    if (message.get()) {
                        endMsg();
                    }

                    pressed = true;
                    alternate = true;
                    playerName = null;
                    isFollowing = false;
                }
            }

            if (!keybind.get().isPressed())
            {
                pressed = false;
            }

            if(keybind.get().isPressed() && !pressed && alternate && mc.currentScreen == null && mc.targetedEntity != null && mc.targetedEntity instanceof PlayerEntity)
            {
                if (!isFollowing) {

                    if (!Friends.get().isFriend((PlayerEntity) mc.targetedEntity) && onlyFriend.get()) return;
                    if (Friends.get().isFriend((PlayerEntity) mc.targetedEntity) && onlyOther.get()) return;

                    mc.player.sendChatMessage(Config.get().prefix.get() + "baritone follow player " + mc.targetedEntity.getEntityName(), Text.literal(Config.get().prefix.get() + "baritone follow player " + mc.targetedEntity.getEntityName()));

                    playerName = mc.targetedEntity.getEntityName();
                    playerEntity = mc.targetedEntity;

                    if (message.get()) {
                        startMsg();
                    }

                    pressed = true;
                    alternate = false;
                    isFollowing = true;
                }
            }
        }

        if(targetMode.get() == Mode.Automatic){

            if (!isFollowing) {
                playerEntity = TargetUtils.getPlayerTarget(targetRange.get(), priority.get());
                if (playerEntity == null) return;
                playerName = playerEntity.getEntityName();

                if (!Friends.get().isFriend((PlayerEntity) playerEntity) && onlyFriend.get()) return;

                mc.player.sendChatMessage(Config.get().prefix.get() + "baritone follow player " + playerName, Text.literal(Config.get().prefix.get() + "baritone follow player " + playerName));

                if (message.get()) {
                    startMsg();
                }

                isFollowing = true;
            }

            if (!playerEntity.isAlive() || (playerEntity.distanceTo(mc.player) > targetRange.get() && !ignoreRange.get())) {
                if (message.get()) {
                    endMsg();
                }

                mc.player.sendChatMessage(Config.get().prefix.get() + "baritone stop", Text.literal(Config.get().prefix.get() + "baritone stop"));
                playerEntity = null;
                playerName = null;
                isFollowing = false;
            }
        }

        if (isFollowing) {
            if (twerkWhenClose.get()){
                if (mc.player.distanceTo(playerEntity) < dis) {
                    if (!Modules.get().get(Twerk.class).isActive()) Modules.get().get(Twerk.class).toggle();
                } else {
                    if (Modules.get().get(Twerk.class).isActive()) Modules.get().get(Twerk.class).toggle();
                }
            }

            if (dirtyTalk.get()) {
                if (messages.get().isEmpty()) return;

                if (timer <= 0) {
                    int i;
                    if (random.get()) {
                        i = Utils.random(0, messages.get().size());
                    } else {
                        if (messageI >= messages.get().size()) messageI = 0;
                        i = messageI++;
                    }

                    iPublic = i;

                    if (message.get()) {
                        followMsg();
                    }

                    timer = delay.get();
                } else {
                    timer--;
                }
            }
        }
        else {
            if (twerkWhenClose.get()){
                if (Modules.get().get(Twerk.class).isActive()) Modules.get().get(Twerk.class).toggle();
            }
        }
    }

    @Override
    public void onDeactivate() {
        if (twerkWhenClose.get()){
            if (Modules.get().get(Twerk.class).isActive())  Modules.get().get(Twerk.class).toggle();
        }

        mc.player.sendChatMessage(Config.get().prefix.get() + "baritone stop", Text.literal(Config.get().prefix.get() + "baritone stop"));
        playerEntity = null;
        playerName = null;
        isFollowing = false;
    }

    public void startMsg()
    {
        if (dirtyTalk.get()) {
            if (dm.get()) {
                mc.player.sendChatMessage("/msg " + playerName + " Come here bby lets have sex uwu", Text.literal("/msg " + playerName + " Come here bby lets have sex uwu"));
            }

            if (pm.get()) {
                mc.player.sendChatMessage("Come here " + playerName + " lets have sex uwu", Text.literal("Come here " + playerName + " lets have sex uwu"));
            }
        } else {
            if (dm.get()) {
                mc.player.sendChatMessage("/msg " + playerName + " I am now following you using Banana+", Text.literal("/msg " + playerName + " I am now following you using Banana+"));
            }

            if (pm.get()) {
                mc.player.sendChatMessage("I am now following " + playerName + " using Banana+", Text.literal("I am now following " + playerName + " using Banana+"));
            }
        }
    }

    public void followMsg()
    {
        if (dm.get()) {
            mc.player.sendChatMessage("/msg " + playerName + " " + messages.get().get(iPublic).replace("(enemy)", playerName), Text.literal("/msg " + playerName + " " + messages.get().get(iPublic).replace("(enemy)", playerName)));
        }

        if (pm.get()) {
            mc.player.sendChatMessage(messages.get().get(iPublic).replace("(enemy)", playerName), Text.literal(messages.get().get(iPublic).replace("(enemy)", playerName)));
        }
    }

    public void endMsg()
    {
        if (dirtyTalk.get()) {
            if (dm.get()) {
                mc.player.sendChatMessage("/msg " + playerName + " See u later bby girl ;*", Text.literal("/msg " + playerName + " See u later bby girl ;*"));
            }

            if (pm.get()) {
                mc.player.sendChatMessage("See u later " + playerName + " xxx ;*", Text.literal("See u later " + playerName + " xxx ;*"));
            }
        } else {
            if (dm.get()) {
                mc.player.sendChatMessage("/msg " + playerName + " I am no longer following you", Text.literal("/msg " + playerName + " I am no longer following you"));
            }

            if (pm.get()) {
                mc.player.sendChatMessage("I am no longer following " + playerName, Text.literal("I am no longer following " + playerName));
            }
        }
    }
}
