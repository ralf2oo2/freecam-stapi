package ralf2oo2.freecam.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.render.Culler;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ralf2oo2.freecam.Freecam;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {
    @Shadow private Minecraft client;
    private boolean originalThirdPersonState = false;

    // Enable thirdperson while freecam is active
    @Inject(at = @At("HEAD"), method = "renderEntities")
    private void freecam_thirdPersonHelper(Vec3d arg2, Culler f, float par3, CallbackInfo ci){
        if(!Freecam.freecamController.isActive()){
            return;
        }
        originalThirdPersonState = client.options.thirdPerson;
        client.options.thirdPerson = true;
    }

    // Setting thirdperson back to original state
    @Inject(at = @At("TAIL"), method = "renderEntities")
    private void freecam_thirdPersonHelper2(Vec3d arg2, Culler f, float par3, CallbackInfo ci){
        if(!Freecam.freecamController.isActive()){
            return;
        }
        client.options.thirdPerson = originalThirdPersonState;
    }
}
