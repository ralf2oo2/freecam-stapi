package ralf2oo2.freecam.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.LivingEntity;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ralf2oo2.freecam.Freecam;
import ralf2oo2.freecam.FreecamConfig;
import ralf2oo2.freecam.util.CameraPosition;

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
		GL11.glTranslatef(-(float)cameraPosition.x, player.standingEyeHeight - (float)cameraPosition.y, -(float)cameraPosition.z);
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

		CameraPosition freecamPosition = Freecam.freecamController.getCameraPosition();

		float radians = freecamPosition.yaw * (float)Math.PI / 180;

		// Forward
		if(Freecam.freecamController.move > 0)
		{
			freecamPosition.z -= Math.cos(radians) * deltaTime * FreecamConfig.config.speed;
			freecamPosition.x += Math.sin(radians) * deltaTime * FreecamConfig.config.speed;
		}

		// Backward
		if(Freecam.freecamController.move < 0)
		{
			freecamPosition.z += Math.cos(radians) * deltaTime * FreecamConfig.config.speed;
			freecamPosition.x -= Math.sin(radians) * deltaTime * FreecamConfig.config.speed;
		}

		// Left
		if(Freecam.freecamController.strafe > 0)
		{
			freecamPosition.z -= Math.sin(radians) * deltaTime * FreecamConfig.config.speed;
			freecamPosition.x -= Math.cos(radians) * deltaTime * FreecamConfig.config.speed;
		}

		// Right
		if(Freecam.freecamController.strafe < 0)
		{
			freecamPosition.z += Math.sin(radians) * deltaTime * FreecamConfig.config.speed;
			freecamPosition.x += Math.cos(radians) * deltaTime * FreecamConfig.config.speed;
		}
		if(Freecam.freecamController.jumping){
			freecamPosition.y += deltaTime * FreecamConfig.config.speed;
		}

		if(Freecam.freecamController.sneaking){
			freecamPosition.y -= deltaTime * FreecamConfig.config.speed;
		}

		Freecam.freecamController.setCameraPosition(freecamPosition.x, freecamPosition.y, freecamPosition.z);
	}

	@Inject(at = @At("HEAD"), method = "renderFirstPersonHand", cancellable = true)
	private void freecam_hudHandler(CallbackInfo ci){
		if(!Freecam.freecamController.isActive()){
			return;
		}
		ci.cancel();
	}
}
