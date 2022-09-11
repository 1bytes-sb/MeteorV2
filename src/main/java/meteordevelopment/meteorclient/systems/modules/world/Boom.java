package meteordevelopment.meteorclient.systems.modules.world;

import meteordevelopment.meteorclient.events.meteor.MouseButtonEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtDouble;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import meteordevelopment.meteorclient.systems.modules.Categories;

public class Boom extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final Setting<Modes> mode = sgGeneral.add(new EnumSetting.Builder<Modes>()
        .name("mode")
        .description("the mode")
        .defaultValue(Modes.Instant)
        .build());

    private final Setting<Double> speed = sgGeneral.add(new DoubleSetting.Builder()
        .name("speed")
        .description("fastness of thing")
        .defaultValue(10)
        .min(1)
        .sliderMax(10)
        .visible(() -> mode.get() != Modes.Lightning || mode.get() != Modes.Instant || mode.get() != Modes.Arrow)
        .build());

    private final Setting<Integer> power = sgGeneral.add(new IntSetting.Builder()
        .name("power")
        .description("how big explosion")
        .defaultValue(10)
        .min(1)
        .sliderMax(127)
        .visible(() -> mode.get() == Modes.Instant || mode.get() == Modes.Motion)
        .build());

    public Boom() {
        super(Categories.World, "boom", "shoots something where you click");
    }

    @EventHandler
    private void onMouseButton(MouseButtonEvent event) {
        if (mc.options.attackKey.isPressed() && mc.currentScreen == null && mc.player.getAbilities().creativeMode) {
            HitResult hr = mc.cameraEntity.raycast(300, 0, true);
            Vec3d owo = hr.getPos();
            BlockPos pos = new BlockPos(owo);
            ItemStack rst = mc.player.getMainHandStack();
            Vec3d sex = mc.player.getRotationVector().multiply(speed.get());
            BlockHitResult bhr = new BlockHitResult(mc.player.getEyePos(), Direction.DOWN, new BlockPos(mc.player.getEyePos()), false);
            switch (mode.get()) {
                case Instant -> {
                    Vec3d aaa = mc.player.getRotationVector().multiply(100);
                    ItemStack Instant = new ItemStack(Items.SALMON_SPAWN_EGG);
                    NbtCompound tag = new NbtCompound();
                    NbtList Pos = new NbtList();
                    NbtList motion = new NbtList();
                    Pos.add(NbtDouble.of(pos.getX()));
                    Pos.add(NbtDouble.of(pos.getY()));
                    Pos.add(NbtDouble.of(pos.getZ()));
                    motion.add(NbtDouble.of(aaa.x));
                    motion.add(NbtDouble.of(aaa.y));
                    motion.add(NbtDouble.of(aaa.z));
                    tag.put("Pos", Pos);
                    tag.put("Motion", motion);
                    tag.putInt("ExplosionPower", power.get());
                    tag.putString("id", "minecraft:fireball");
                    Instant.setSubNbt("EntityTag", tag);
                    mc.interactionManager.clickCreativeStack(Instant, 36 + mc.player.getInventory().selectedSlot);
                    mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, bhr);
                    mc.interactionManager.clickCreativeStack(rst, 36 + mc.player.getInventory().selectedSlot);
                }
                case Motion -> {
                    ItemStack Motion = new ItemStack(Items.SALMON_SPAWN_EGG);
                    NbtCompound tag = new NbtCompound();
                    NbtList motion = new NbtList();
                    motion.add(NbtDouble.of(sex.x));
                    motion.add(NbtDouble.of(sex.y));
                    motion.add(NbtDouble.of(sex.z));
                    tag.put("Motion", motion);
                    tag.putInt("ExplosionPower", power.get());
                    tag.putString("id", "minecraft:fireball");
                    Motion.setSubNbt("EntityTag", tag);
                    mc.interactionManager.clickCreativeStack(Motion, 36 + mc.player.getInventory().selectedSlot);
                    mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, bhr);
                    mc.interactionManager.clickCreativeStack(rst, 36 + mc.player.getInventory().selectedSlot);
                }
                case Lightning -> {
                    ItemStack Lightning = new ItemStack(Items.SALMON_SPAWN_EGG);
                    NbtCompound tag = new NbtCompound();
                    NbtList Pos = new NbtList();
                    Pos.add(NbtDouble.of(pos.getX()));
                    Pos.add(NbtDouble.of(pos.getY()));
                    Pos.add(NbtDouble.of(pos.getZ()));
                    tag.put("Pos", Pos);
                    tag.putString("id", "minecraft:lightning_bolt");
                    Lightning.setSubNbt("EntityTag", tag);
                    mc.interactionManager.clickCreativeStack(Lightning, 36 + mc.player.getInventory().selectedSlot);
                    mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, bhr);
                    mc.interactionManager.clickCreativeStack(rst, 36 + mc.player.getInventory().selectedSlot);
                }
                case Kitty -> {
                    ItemStack Kitty = new ItemStack(Items.CAT_SPAWN_EGG);
                    NbtCompound tag = new NbtCompound();
                    NbtList motion = new NbtList();
                    motion.add(NbtDouble.of(sex.x));
                    motion.add(NbtDouble.of(sex.y));
                    motion.add(NbtDouble.of(sex.z));
                    tag.put("Motion", motion);
                    Kitty.setSubNbt("EntityTag", tag);
                    mc.interactionManager.clickCreativeStack(Kitty, 36 + mc.player.getInventory().selectedSlot);
                    mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, bhr);
                    mc.interactionManager.clickCreativeStack(rst, 36 + mc.player.getInventory().selectedSlot);
                }
                case Arrow -> {
                    ItemStack Arrow = new ItemStack(Items.SALMON_SPAWN_EGG);
                    NbtCompound tag = new NbtCompound();
                    NbtList speed = new NbtList();
                    speed.add(NbtDouble.of(sex.x));
                    speed.add(NbtDouble.of(sex.y));
                    speed.add(NbtDouble.of(sex.z));
                    tag.put("Motion", speed);
                    tag.putString("id", "minecraft:arrow");
                    Arrow.setSubNbt("EntityTag", tag);
                    mc.interactionManager.clickCreativeStack(Arrow, 36 + mc.player.getInventory().selectedSlot);
                    mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, bhr);
                    mc.interactionManager.clickCreativeStack(rst, 36 + mc.player.getInventory().selectedSlot);
                }
            }
        }
    }
    public enum Modes {
        Instant, Motion, Lightning, Kitty, Arrow
    }
}
