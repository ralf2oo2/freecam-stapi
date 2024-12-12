package ralf2oo2.freecam.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.hit.HitResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.modificationstation.stationapi.api.util.math.Vec3d;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ralf2oo2.freecam.Freecam;
import ralf2oo2.freecam.FreecamConfig;
import ralf2oo2.freecam.util.CameraPosition;

import java.util.ArrayList;
import java.util.List;

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

		CameraPosition currentCameraPosition = Freecam.freecamController.getCameraPosition();
		CameraPosition nextCameraPosition = currentCameraPosition.clone();

		float radians = currentCameraPosition.yaw * (float)Math.PI / 180;

		// Forward
		if(Freecam.freecamController.move > 0)
		{
			nextCameraPosition.z -= Math.cos(radians) * deltaTime * FreecamConfig.config.speed;
			nextCameraPosition.x += Math.sin(radians) * deltaTime * FreecamConfig.config.speed;
		}

		// Backward
		if(Freecam.freecamController.move < 0)
		{
			nextCameraPosition.z += Math.cos(radians) * deltaTime * FreecamConfig.config.speed;
			nextCameraPosition.x -= Math.sin(radians) * deltaTime * FreecamConfig.config.speed;
		}

		// Left
		if(Freecam.freecamController.strafe > 0)
		{
			nextCameraPosition.z -= Math.sin(radians) * deltaTime * FreecamConfig.config.speed;
			nextCameraPosition.x -= Math.cos(radians) * deltaTime * FreecamConfig.config.speed;
		}

		// Right
		if(Freecam.freecamController.strafe < 0)
		{
			nextCameraPosition.z += Math.sin(radians) * deltaTime * FreecamConfig.config.speed;
			nextCameraPosition.x += Math.cos(radians) * deltaTime * FreecamConfig.config.speed;
		}
		if(Freecam.freecamController.jumping){
			nextCameraPosition.y += deltaTime * FreecamConfig.config.speed;
		}

		if(Freecam.freecamController.sneaking){
			nextCameraPosition.y -= deltaTime * FreecamConfig.config.speed;
		}

		CameraPosition movementVector = CameraPosition.subtract(nextCameraPosition, currentCameraPosition);

		boolean collision = false;
		List<HitResult> hitResults = new ArrayList<>();
		// TODO: 12/12/2024 Only run code when collisions are enabled
		if(true){
			Box box = Freecam.cameraBoundingBox;

			List<Vec3d> corners = new ArrayList<>();

			double xPlane = movementVector.x > 0 ? box.maxX : box.minX;

			double yPlane = movementVector.y > 0 ? box.maxY : box.minY;

			double zPlane = movementVector.z > 0 ? box.maxZ : box.minZ;

			corners.add(new Vec3d(xPlane, yPlane, zPlane));
			corners.add(new Vec3d(xPlane, yPlane, movementVector.z > 0 ? box.minZ : box.maxZ));
			corners.add(new Vec3d(xPlane, movementVector.y > 0 ? box.minY : box.maxY, zPlane));
			corners.add(new Vec3d(xPlane, movementVector.y > 0 ? box.minY : box.maxY, movementVector.z > 0 ? box.minZ : box.maxZ));

			for (Vec3d corner : corners) {
				Vec3d start = new Vec3d(
						corner.x + currentCameraPosition.x,
						corner.y + currentCameraPosition.y,
						corner.z + currentCameraPosition.z
				);
				Vec3d end = new Vec3d(
						corner.x + nextCameraPosition.x,
						corner.y + nextCameraPosition.y,
						corner.z + nextCameraPosition.z
				);
				HitResult hitResult = client.player.world.raycast(net.minecraft.util.math.Vec3d.create(start.x, start.y, start.z), net.minecraft.util.math.Vec3d.create(end.x, end.y, end.z));
				if(hitResult != null && hitResult.type == HitResultType.BLOCK){
					collision = true;
					System.out.println("colliding");
					hitResults.add(hitResult);
				}
			}
		}

		if (!collision) {
			Freecam.freecamController.setCameraPosition(nextCameraPosition.x, nextCameraPosition.y, nextCameraPosition.z);
		} else {

			boolean xBlocked = false;
			boolean yBlocked = false;
			boolean zBlocked = false;

			for(HitResult hitResult : hitResults){
				Vec3d blockedAxis = getBlockedAxis(hitResult.side);
				if(blockedAxis.x != 0){
					xBlocked = true;
				}
				if(blockedAxis.y != 0){
					yBlocked = true;
				}
				if(blockedAxis.z != 0){
					zBlocked = true;
				}
			}

			Vec3d correctedPosition = new Vec3d(currentCameraPosition.x, currentCameraPosition.y, currentCameraPosition.z);
			if (movementVector.x != 0 && !xBlocked) {
				correctedPosition = correctedPosition.add(movementVector.x, 0, 0);
			}
			if (movementVector.y != 0 && !yBlocked) {
				correctedPosition = correctedPosition.add(0, movementVector.y, 0);
			}
			if (movementVector.z != 0 && !zBlocked) {
				correctedPosition = correctedPosition.add(0, 0, movementVector.z);
			}
			Freecam.freecamController.setCameraPosition(correctedPosition.x, correctedPosition.y, correctedPosition.z);
		}
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
}
