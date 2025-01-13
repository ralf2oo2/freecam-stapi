package ralf2oo2.freecam.mixin;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.hit.HitResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.modificationstation.stationapi.api.block.BlockState;
import net.modificationstation.stationapi.api.util.math.StationBlockPos;
import net.modificationstation.stationapi.api.util.math.Vec3d;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ralf2oo2.freecam.Freecam;
import ralf2oo2.freecam.FreecamConfig;
import ralf2oo2.freecam.client.FreecamController;
import ralf2oo2.freecam.util.CameraPosition;
import ralf2oo2.freecam.util.CollisionResult;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
	@Shadow
	private Minecraft client;
	private boolean originalViewBobbingState = false;
	float LOW_LIMIT = 0.000167f; // Set to unreasonable value making it pretty much useless
	float HIGH_LIMIT = 0.1f;
	long lastTime = System.nanoTime();

	// Move freecam
	@Inject(at = @At("HEAD"), method = "applyCameraTransform", cancellable = true)
	private void freecam_cameraPositionHandler(float par1, CallbackInfo ci){
		if(!Freecam.freecamController.isActive()){
			return;
		}
		LivingEntity player = client.camera;
		CameraPosition cameraPosition = Freecam.freecamController.updateCameraPosition(player, par1);
		GL11.glRotatef(cameraPosition.pitch, 1f, 0f, 0f);
		GL11.glRotatef(cameraPosition.yaw, 0f, 1f, 0f);
		GL11.glRotatef(-cameraPosition.roll, 0f, 0f, 1f);
		GL11.glTranslatef(-(float)cameraPosition.x, -(float)cameraPosition.y, -(float)cameraPosition.z);
		ci.cancel();
	}

	// Disable viewbobbing while camera is active
	@Inject(at = @At("HEAD"), method = "renderWorld")
	private void freecam_cameraEffectHandler(float i, int par2, CallbackInfo ci){
		if(!Freecam.freecamController.isActive()){
			return;
		}
		originalViewBobbingState = client.options.bobView;
		client.options.bobView = false;
	}

	// Re-enable viewbobbing
	@Inject(at = @At("TAIL"), method = "renderWorld")
	private void freecam_cameraEffectHandler2(float i, int par2, CallbackInfo ci){
		if(!Freecam.freecamController.isActive()){
			return;
		}
		client.options.bobView = originalViewBobbingState;
	}
	@Inject(at = @At("TAIL"), method = "onFrameUpdate", cancellable = true)
	private void freecam_updateHandler(CallbackInfo ci){
		if(!Freecam.freecamController.isActive()){
			return;
		}
		moveCamera();
	}

	// Get deltatime
	private float getDeltaTime(){
		long currentTime = System.nanoTime();
		float deltaTime = (currentTime - lastTime) / 1000000000.0f;
		if(deltaTime < LOW_LIMIT){
			deltaTime = LOW_LIMIT;
		}
		else if ( deltaTime > HIGH_LIMIT ) {
			deltaTime = HIGH_LIMIT;
		}
		lastTime = currentTime;
		return deltaTime;
	}

	// Move freecam in relation to camera rotation
	private void moveCamera(){
		float deltaTime = getDeltaTime();

		FreecamController freecamController = Freecam.freecamController;

		CameraPosition currentCameraPosition = Freecam.freecamController.getCameraPosition();
		CameraPosition nextCameraPosition = currentCameraPosition.clone();

		float radians = currentCameraPosition.yaw * (float)Math.PI / 180;

		freecamController.velocityX = 0;
		freecamController.velocityY = 0;
		freecamController.velocityZ = 0;

		// Forward
		if(Freecam.freecamController.move > 0)
		{
			freecamController.velocityZ -= Math.cos(radians) * deltaTime * FreecamConfig.config.speed;
			freecamController.velocityX += Math.sin(radians) * deltaTime * FreecamConfig.config.speed;
		}

		// Backward
		if(Freecam.freecamController.move < 0)
		{
			freecamController.velocityZ += Math.cos(radians) * deltaTime * FreecamConfig.config.speed;
			freecamController.velocityX -= Math.sin(radians) * deltaTime * FreecamConfig.config.speed;
		}

		// Left
		if(Freecam.freecamController.strafe > 0)
		{
			freecamController.velocityZ -= Math.sin(radians) * deltaTime * FreecamConfig.config.speed;
			freecamController.velocityX -= Math.cos(radians) * deltaTime * FreecamConfig.config.speed;
		}

		// Right
		if(Freecam.freecamController.strafe < 0)
		{
			freecamController.velocityZ += Math.sin(radians) * deltaTime * FreecamConfig.config.speed;
			freecamController.velocityX += Math.cos(radians) * deltaTime * FreecamConfig.config.speed;
		}
		if(Freecam.freecamController.jumping){
			freecamController.velocityY += deltaTime * FreecamConfig.config.speed;
		}

		if(Freecam.freecamController.sneaking){
			freecamController.velocityY -= deltaTime * FreecamConfig.config.speed;
		}

		double adjustedVelocityX = freecamController.velocityX;
		double adjustedVelocityY = freecamController.velocityY;
		double adjustedVelocityZ = freecamController.velocityZ;


		boolean collision = false;
		// TODO: 12/12/2024 Only run code when collisions are enabled
		Box box = Freecam.cameraBoundingBox;
		for(int iteration = 0; iteration < 3; iteration++){
			int stepX = adjustedVelocityX > 0 ? 1 : -1;
			int stepY = adjustedVelocityY > 0 ? 1 : -1;
			int stepZ = adjustedVelocityZ > 0 ? 1 : -1;

			int x = (int) currentCameraPosition.x;
			int y = (int) currentCameraPosition.y;
			int z = (int) currentCameraPosition.z;
			double cx = adjustedVelocityX;
			double cy = adjustedVelocityY;
			double cz = adjustedVelocityZ;

			List<CollisionResult> potentialCollisions = new ArrayList<>();

			Box movementBox = box.offset(x, y, z);
			movementBox.maxX = stepX > 0 ? movementBox.maxX + cx : movementBox.maxX;
			movementBox.minX = stepX < 0 ? movementBox.minX - cx : movementBox.minX;
			movementBox.maxY = stepY > 0 ? movementBox.maxY + cy : movementBox.maxY;
			movementBox.minY = stepY < 0 ? movementBox.minY - cy : movementBox.minY;
			movementBox.maxZ = stepZ > 0 ? movementBox.maxZ + cz : movementBox.maxZ;
			movementBox.minZ = stepZ < 0 ? movementBox.minZ - cz : movementBox.minZ;

			Stream<BlockPos> blockStream = StationBlockPos.stream(movementBox);

			blockStream.forEach(blockPos -> {
				int id = client.world.getBlockId(blockPos.x, blockPos.y, blockPos.z);

				if (id == 0) return;
				client.world.setBlock(blockPos.x, blockPos.y, blockPos.z, Block.DIAMOND_BLOCK.id);

				Box blockCollisionBox = Block.BLOCKS[id].getCollisionShape(client.world, blockPos.x, blockPos.y, blockPos.z);
				if(blockCollisionBox == null) return;

				CollisionResult collisionResult = collide(box.offset(x, y, z), blockCollisionBox, adjustedVelocityX, adjustedVelocityY, adjustedVelocityZ);

				if (collisionResult == null) return;

				potentialCollisions.add(collisionResult);
			});

			if(potentialCollisions.isEmpty()) break;

			CollisionResult earliestCollision = potentialCollisions.stream()
					.min(Comparator.comparingDouble(CollisionResult::getEntryTime))
					.orElse(null);

			double entryTime = earliestCollision.getEntryTime() - 0.001;
			int[] normal = earliestCollision.getNormal();

			if (normal[0] != 0) {
				freecamController.velocityX = 0;
				nextCameraPosition.x += adjustedVelocityX * entryTime;
			}
			if (normal[1] != 0) {
				freecamController.velocityY = 0;
				nextCameraPosition.y += adjustedVelocityY * entryTime;
			}
			if (normal[2] != 0) {
				freecamController.velocityZ = 0;
				nextCameraPosition.z += adjustedVelocityZ * entryTime;
			}
		}
		nextCameraPosition.x += freecamController.velocityX;
		nextCameraPosition.y += freecamController.velocityY;
		nextCameraPosition.z += freecamController.velocityZ;
		freecamController.setCameraPosition(nextCameraPosition.x, nextCameraPosition.y, nextCameraPosition.z);
	}

	public double time(double x, double y){
		return y != 0 ? x / y : (x > 0 ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY);
	}

	public CollisionResult collide(Box box, Box box2, double velocityX, double velocityY, double velocityZ){
		double xEntry = time((velocityX > 0 ? box2.minX - box.maxX : box2.maxX - box.minX), velocityX);
		double xExit = time((velocityX > 0 ? box2.maxX - box.minX : box2.minX - box.maxX), velocityX);

		double yEntry = time((velocityY > 0 ? box2.minY - box.maxY : box2.maxY - box.minY), velocityY);
		double yExit = time((velocityY > 0 ? box2.maxY - box.minY : box2.minY - box.maxY), velocityY);

		double zEntry = time((velocityZ > 0 ? box2.minZ - box.maxZ : box2.maxZ - box.minZ), velocityZ);
		double zExit = time((velocityZ > 0 ? box2.maxZ - box.minZ : box2.minZ - box.maxZ), velocityZ);
		System.out.println(box.intersects(box2));
		System.out.println("VelocityX " + velocityX + " VelocityY " + velocityY + " VelocityZ " + velocityZ);
		System.out.println("xEntry " + xEntry + " xExit " + xExit + " yEntry " + yEntry + " yExit " + yExit + " zEntry " + zEntry + " zExit " + zExit);
		if (xEntry < 0 && yEntry < 0 && zEntry < 0) {
			return null;
		}

		if (xEntry > 1 || yEntry > 1 || zEntry > 1) {
			return null;
		}

		double entry = Math.max(Math.max(xEntry, yEntry), zEntry);
		double exit = Math.min(Math.min(xExit, yExit), zExit);

		if (entry > exit) {
			return null;
		}

		int normalX = (entry == xEntry) ? (velocityX > 0 ? -1 : 1) : 0;
		int normalY = (entry == yEntry) ? (velocityY > 0 ? -1 : 1) : 0;
		int normalZ = (entry == zEntry) ? (velocityZ > 0 ? -1 : 1) : 0;

		return new CollisionResult(entry, new int[]{ normalX, normalY, normalZ });
	}

	public List<HitResult> raycast(CameraPosition pos1, CameraPosition pos2){
		List<HitResult> hitResults = new ArrayList<>();
		Box box = Freecam.cameraBoundingBox;

		CameraPosition movementVector = CameraPosition.subtract(pos2, pos1);

		List<Vec3d> corners = new ArrayList<>();

		double xPlane = movementVector.x > 0 ? box.maxX : box.minX;

		double yPlane = movementVector.y > 0 ? box.maxY : box.minY;

		double zPlane = movementVector.z > 0 ? box.maxZ : box.minZ;

		corners.add(new Vec3d(xPlane, yPlane, zPlane));
		corners.add(new Vec3d(xPlane, yPlane, movementVector.z > 0 ? box.minZ : box.maxZ));
		corners.add(new Vec3d(xPlane, movementVector.y > 0 ? box.minY : box.maxY, zPlane));
		corners.add(new Vec3d(xPlane, movementVector.y > 0 ? box.minY : box.maxY, movementVector.z > 0 ? box.minZ : box.maxZ));
//		corners.add(new Vec3d(box.minX, box.minY, box.minZ));
//		corners.add(new Vec3d(box.minX, box.minY, box.maxZ));
//		corners.add(new Vec3d(box.minX, box.maxY, box.minZ));
//		corners.add(new Vec3d(box.minX, box.maxY, box.maxZ));
//		corners.add(new Vec3d(box.maxX, box.minY, box.minZ));
//		corners.add(new Vec3d(box.maxX, box.minY, box.maxZ));
//		corners.add(new Vec3d(box.maxX, box.maxY, box.minZ));
//		corners.add(new Vec3d(box.maxX, box.maxY, box.maxZ));

		for (Vec3d corner : corners) {
			Vec3d start = new Vec3d(
					corner.x + pos1.x,
					corner.y + pos1.y,
					corner.z + pos1.z
			);
			Vec3d end = new Vec3d(
					corner.x + pos2.x,
					corner.y + pos2.y,
					corner.z + pos2.z
			);
			HitResult hitResult = client.player.world.raycast(net.minecraft.util.math.Vec3d.create(start.x, start.y, start.z), net.minecraft.util.math.Vec3d.create(end.x, end.y, end.z));
			if(hitResult != null && hitResult.type == HitResultType.BLOCK){
				//System.out.println("colliding");
				hitResults.add(hitResult);
			}
		}
		return hitResults;
	}

	public Vec3d getBlockedAxis(int blockSide) {
		switch (blockSide) {
			case 0: // bottom
				return new Vec3d(0, 1, 0);
			case 1: // top
				return new Vec3d(0, -1, 0);
			case 2: // +Z
				return new Vec3d(0, 0, 1);
			case 3: // -Z
				return new Vec3d(0, 0, -1);
			case 4: // -X
				return new Vec3d(1, 0, 0);
			case 5: // +X
				return new Vec3d(-1, 0, 0);
			default:
				return new Vec3d(0, 0, 0);
		}
	}

	@Inject(at = @At("HEAD"), method = "renderFirstPersonHand", cancellable = true)
	private void freecam_hudHandler(CallbackInfo ci){
		if(!Freecam.freecamController.isActive()){
			return;
		}
		ci.cancel();
	}

	public boolean pushOutOfBlocks(CameraPosition cameraPosition, double x, double y, double z) {
		Box bounds = Freecam.cameraBoundingBox;

		double width = bounds.maxX - bounds.minX;
		double height = bounds.maxY - bounds.minY;

		int i7 = MathHelper.floor(x);
		int i8 = MathHelper.floor(y);
		int i9 = MathHelper.floor(z);
		double d10 = x - (double)i7;
		double d12 = y - (double)i8;
		double d14 = z - (double)i9;
		if(client.world.shouldSuffocate(i7, i8, i9)) {
			boolean z16 = !client.world.shouldSuffocate(i7 - 1, i8, i9);
			boolean z17 = !client.world.shouldSuffocate(i7 + 1, i8, i9);
			boolean z18 = !client.world.shouldSuffocate(i7, i8 - 1, i9);
			boolean z19 = !client.world.shouldSuffocate(i7, i8 + 1, i9);
			boolean z20 = !client.world.shouldSuffocate(i7, i8, i9 - 1);
			boolean z21 = !client.world.shouldSuffocate(i7, i8, i9 + 1);
			byte b22 = -1;
			double d23 = 9999.0D;
			if(z16 && d10 < d23) {
				d23 = d10;
				b22 = 0;
			}

			if(z17 && 1.0D - d10 < d23) {
				d23 = 1.0D - d10;
				b22 = 1;
			}

			if(z18 && d12 < d23) {
				d23 = d12;
				b22 = 2;
			}

			if(z19 && 1.0D - d12 < d23) {
				d23 = 1.0D - d12;
				b22 = 3;
			}

			if(z20 && d14 < d23) {
				d23 = d14;
				b22 = 4;
			}

			if(z21 && 1.0D - d14 < d23) {
				d23 = 1.0D - d14;
				b22 = 5;
			}

			float f25 = 0.1f;
			if(b22 == 0) {
				cameraPosition.x = Math.floor(cameraPosition.x) - width - f25;
			}

			if(b22 == 1) {
				cameraPosition.x = Math.ceil(cameraPosition.x) + width + f25;
			}

			if(b22 == 2) {
				cameraPosition.y = Math.floor(cameraPosition.y) - height - f25;
			}

			if(b22 == 3) {
				cameraPosition.y = Math.ceil(cameraPosition.y) + height + f25;
			}

			if(b22 == 4) {
				cameraPosition.z = Math.floor(cameraPosition.z) - width - f25;
			}

			if(b22 == 5) {
				cameraPosition.z = Math.ceil(cameraPosition.z) + width + f25;
			}
			return true;
		}

		return false;
	}
}
