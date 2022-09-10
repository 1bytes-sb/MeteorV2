package meteordevelopment.meteorclient.systems.modules.movement;

import meteordevelopment.meteorclient.events.world.CollisionShapeEvent;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.util.shape.VoxelShapes;
import meteordevelopment.meteorclient.systems.modules.Categories;

public class Noclip extends Module {
	public Noclip() {
		super(Categories.World, "Noclip", "Noclip.");
	}

	@EventHandler
	private void onCollision(CollisionShapeEvent event) {
		if (event.type != CollisionShapeEvent.CollisionType.BLOCK || mc.player == null) return;
		if (event.pos.getY() >= mc.player.getPos().y) {
			event.shape = VoxelShapes.empty();
		}
	}
}
