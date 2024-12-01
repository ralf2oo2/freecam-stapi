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
import ralf2oo2.freecam.util.CameraPosition;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
	@Shadow
	private Minecraft client;
	private boolean originalViewBobbingState = false;

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
}
