package ralf2oo2.freecam.mixin;

import net.minecraft.class_555;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MinecraftApplet;
import net.minecraft.entity.LivingEntity;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ralf2oo2.freecam.Freecam;
import ralf2oo2.freecam.util.CameraPosition;

@Mixin(class_555.class)
public class GameRendererMixin {
	@Shadow
	private Minecraft field_2349;
	private boolean originalViewBobbingState = false;
	@Inject(at = @At("HEAD"), method = "method_1851", cancellable = true)
	private void freecam_cameraPositionHandler(float par1, CallbackInfo ci){
		if(!Freecam.freecamController.isActive()){
			return;
		}
		LivingEntity player = field_2349.field_2807;
		CameraPosition cameraPosition = Freecam.freecamController.updateCameraPosition(player, par1);
		GL11.glRotatef(cameraPosition.pitch, 1f, 0f, 0f);
		GL11.glRotatef(cameraPosition.yaw, 0f, 1f, 0f);
		GL11.glRotatef(-cameraPosition.roll, 0f, 0f, 1f);
		GL11.glTranslatef(-(float)cameraPosition.x, player.eyeHeight - (float)cameraPosition.y, -(float)cameraPosition.z);
		ci.cancel();
	}
	@Inject(at = @At("HEAD"), method = "method_1840")
	private void freecam_cameraEffectHandler(float i, int par2, CallbackInfo ci){
		if(!Freecam.freecamController.isActive()){
			return;
		}
		originalViewBobbingState = field_2349.options.bobView;
		field_2349.options.bobView = false;
	}
	@Inject(at = @At("TAIL"), method = "method_1840")
	private void freecam_cameraEffectHandler2(float i, int par2, CallbackInfo ci){
		if(!Freecam.freecamController.isActive()){
			return;
		}
		field_2349.options.bobView = originalViewBobbingState;
	}
}
