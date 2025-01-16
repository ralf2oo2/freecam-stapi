package ralf2oo2.freecam.mixin;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
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
import java.util.List;
import java.util.stream.Stream;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
	@Shadow
	private Minecraft client;
	float LOW_LIMIT = 0.000167f; // Set to unreasonable value making it pretty much useless
	float HIGH_LIMIT = 0.1f;
	long lastTime = System.nanoTime();

	// Translate camera position
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

	// Disable view bobbing when freecam is active
	@Inject(at = @At("HEAD"), method = "applyViewBobbing", cancellable = true)
	private void freecam_viewBobbingHandler(float par1, CallbackInfo ci){
		if(!Freecam.freecamController.isActive()){
			return;
		}
		ci.cancel();
	}

	// Move freecam
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

		double[] direction = getDirectionFromInput(currentCameraPosition);

		if(FreecamConfig.config.classicMovement){
			freecamController.velocityX = direction[0] != 0 ? direction[0] * FreecamConfig.config.speed : freecamController.velocityX;
			freecamController.velocityY = direction[1] != 0 ? direction[1] * FreecamConfig.config.speed : freecamController.velocityY;
			freecamController.velocityZ = direction[2] != 0 ? direction[2] * FreecamConfig.config.speed : freecamController.velocityZ;
		}
		else {
			freecamController.accelerationX = direction[0] * (double)FreecamConfig.config.speed;
			freecamController.accelerationY = direction[1] * (double)FreecamConfig.config.speed;
			freecamController.accelerationZ = direction[2] * (double)FreecamConfig.config.speed;

			freecamController.velocityX = freecamController.velocityX + freecamController.accelerationX * FreecamConfig.config.drag * deltaTime;
			freecamController.velocityY = freecamController.velocityY + freecamController.accelerationY * FreecamConfig.config.drag * deltaTime;
			freecamController.velocityZ = freecamController.velocityZ + freecamController.accelerationZ * FreecamConfig.config.drag * deltaTime;

			freecamController.accelerationX = 0;
			freecamController.accelerationY = 0;
			freecamController.accelerationZ = 0;
		}

		if(FreecamConfig.config.collision){
			Box box = Freecam.cameraBoundingBox;
			for(int iteration = 0; iteration < 3; iteration++){
				double adjustedVelocityX = freecamController.velocityX * deltaTime;
				double adjustedVelocityY = freecamController.velocityY * deltaTime;
				double adjustedVelocityZ = freecamController.velocityZ * deltaTime;

				int stepX = adjustedVelocityX > 0 ? 1 : -1;
				int stepY = adjustedVelocityY > 0 ? 1 : -1;
				int stepZ = adjustedVelocityZ > 0 ? 1 : -1;

				double cx = adjustedVelocityX;
				double cy = adjustedVelocityY;
				double cz = adjustedVelocityZ;

				List<CollisionResult> potentialCollisions = new ArrayList<>();

				Box movementBox = box.offset(currentCameraPosition.x, currentCameraPosition.y, currentCameraPosition.z);
				movementBox.maxX = stepX > 0 ? movementBox.maxX + cx : movementBox.maxX;
				movementBox.minX = stepX < 0 ? movementBox.minX + cx : movementBox.minX;
				movementBox.maxY = stepY > 0 ? movementBox.maxY + cy : movementBox.maxY;
				movementBox.minY = stepY < 0 ? movementBox.minY + cy : movementBox.minY;
				movementBox.maxZ = stepZ > 0 ? movementBox.maxZ + cz : movementBox.maxZ;
				movementBox.minZ = stepZ < 0 ? movementBox.minZ + cz : movementBox.minZ;

				Stream<BlockPos> blockStream = StationBlockPos.stream(movementBox);

				blockStream.forEach(blockPos -> {
					int id = client.world.getBlockId(blockPos.x, blockPos.y, blockPos.z);

					if (id == 0) return;

					Box blockCollisionBox = Block.BLOCKS[id].getCollisionShape(client.world, blockPos.x, blockPos.y, blockPos.z);
					if(blockCollisionBox == null) return;

					CollisionResult collisionResult = collide(box.offset(currentCameraPosition.x, currentCameraPosition.y, currentCameraPosition.z), blockCollisionBox, adjustedVelocityX, adjustedVelocityY, adjustedVelocityZ);

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
		}

		nextCameraPosition.x += freecamController.velocityX * deltaTime;
		nextCameraPosition.y += freecamController.velocityY * deltaTime;
		nextCameraPosition.z += freecamController.velocityZ * deltaTime;
		freecamController.setCameraPosition(nextCameraPosition.x, nextCameraPosition.y, nextCameraPosition.z);

		Vec3d vec3d = new Vec3d(currentCameraPosition.x, currentCameraPosition.y, currentCameraPosition.z);
		Vec3d vec3d2 = new Vec3d(nextCameraPosition.x, nextCameraPosition.y, nextCameraPosition.z);

		System.out.println(vec3d.distanceTo(vec3d2));


		if(FreecamConfig.config.classicMovement){
			freecamController.velocityX = 0;
			freecamController.velocityY = 0;
			freecamController.velocityZ = 0;
		}
		else {
			freecamController.velocityX -= Math.signum(freecamController.velocityX) * Math.min(
					Math.abs(freecamController.velocityX) * FreecamConfig.config.drag * deltaTime,
					Math.abs(freecamController.velocityX)
			);

			freecamController.velocityY -= Math.signum(freecamController.velocityY) * Math.min(
					Math.abs(freecamController.velocityY) * FreecamConfig.config.drag * deltaTime,
					Math.abs(freecamController.velocityY)
			);

			freecamController.velocityZ -= Math.signum(freecamController.velocityZ) * Math.min(
					Math.abs(freecamController.velocityZ) * FreecamConfig.config.drag * deltaTime,
					Math.abs(freecamController.velocityZ)
			);
		}

		if(client.player.getDistance(nextCameraPosition.x, nextCameraPosition.y, nextCameraPosition.z) < 1){
			Freecam.freecamController.hidePlayer = true;
		}
		else {
			Freecam.freecamController.hidePlayer = false;
		}

	}

	// Gets the direction of movement in all axes, range -1 : 1
	public double[] getDirectionFromInput(CameraPosition currentCameraPosition){
		double directionX = 0d;
		double directionY = 0d;
		double directionZ = 0d;


		float radians = currentCameraPosition.yaw * (float)Math.PI / 180;
		// Forward
		if(Freecam.freecamController.move > 0)
		{
			directionZ -= Math.cos(radians);
			directionX += Math.sin(radians);
		}

		// Backward
		if(Freecam.freecamController.move < 0)
		{
			directionZ += Math.cos(radians);
			directionX -= Math.sin(radians);
		}

		// Left
		if(Freecam.freecamController.strafe > 0)
		{
			directionZ -= Math.sin(radians);
			directionX -= Math.cos(radians);
		}

		// Right
		if(Freecam.freecamController.strafe < 0)
		{
			directionZ += Math.sin(radians);
			directionX += Math.cos(radians);
		}

		double magnitudeXZ = Math.sqrt(directionX * directionX + directionZ * directionZ);
		if (magnitudeXZ > 1) {
			directionX /= magnitudeXZ;
			directionZ /= magnitudeXZ;
		}

		if(Freecam.freecamController.jumping){
			directionY += 1;
		}

		if(Freecam.freecamController.sneaking){
			directionY -= 1;
		}

		return new double[]{directionX, directionY, directionZ};
	}

	public double time(double x, double y){
		return y != 0 ? x / y : (x > 0 ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY);
	}

	// Check collision between 2 boxes
	public CollisionResult collide(Box box, Box box2, double velocityX, double velocityY, double velocityZ){
		double xEntry = time((velocityX > 0 ? box2.minX - box.maxX : box2.maxX - box.minX), velocityX);
		double xExit = time((velocityX > 0 ? box2.maxX - box.minX : box2.minX - box.maxX), velocityX);

		double yEntry = time((velocityY > 0 ? box2.minY - box.maxY : box2.maxY - box.minY), velocityY);
		double yExit = time((velocityY > 0 ? box2.maxY - box.minY : box2.minY - box.maxY), velocityY);

		double zEntry = time((velocityZ > 0 ? box2.minZ - box.maxZ : box2.maxZ - box.minZ), velocityZ);
		double zExit = time((velocityZ > 0 ? box2.maxZ - box.minZ : box2.minZ - box.maxZ), velocityZ);

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

	@Inject(at = @At("HEAD"), method = "renderFirstPersonHand", cancellable = true)
	private void freecam_hudHandler(CallbackInfo ci){
		if(!Freecam.freecamController.isActive()){
			return;
		}
		ci.cancel();
	}
}
