package ralf2oo2.freecam.mixin;

import net.minecraft.class_555;
import net.minecraft.class_564;
import net.minecraft.client.Minecraft;
import net.minecraft.client.font.TextRenderer;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ralf2oo2.freecam.Freecam;
import ralf2oo2.freecam.util.CameraPosition;

@Mixin(class_555.class)
public class EntityRendererMixin {
    @Shadow private long field_2334;
    @Shadow private Minecraft field_2349;
    private boolean originalHideHudState = false;
    float LOW_LIMIT = 0.000167f;          // Keep At/Below 60fps
    float HIGH_LIMIT = 0.1f;
    long lastTime = System.nanoTime();
    @Inject(at = @At("TAIL"), method = "method_1844", cancellable = true)
    private void freecam_updateHandler(CallbackInfo ci){
        if(!Freecam.freecamController.isActive()){
            return;
        }
        moveCamera();
    }
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
    private void moveCamera(){
        float deltaTime = getDeltaTime();

        CameraPosition freecamPosition = Freecam.freecamController.getCameraPosition();

        float radians = freecamPosition.yaw * (float)Math.PI / 180;

        // Forward
        if(Freecam.freecamController.move > 0)
        {
            freecamPosition.z -= Math.cos(radians) * deltaTime * Freecam.freecamController.cameraSpeed;
            freecamPosition.x += Math.sin(radians) * deltaTime * Freecam.freecamController.cameraSpeed;
        }

        // Backward
        if(Freecam.freecamController.move < 0)
        {
            freecamPosition.z += Math.cos(radians) * deltaTime * Freecam.freecamController.cameraSpeed;
            freecamPosition.x -= Math.sin(radians) * deltaTime * Freecam.freecamController.cameraSpeed;
        }

        // Left
        if(Freecam.freecamController.strafe > 0)
        {
            freecamPosition.z -= Math.sin(radians) * deltaTime * Freecam.freecamController.cameraSpeed;
            freecamPosition.x -= Math.cos(radians) * deltaTime * Freecam.freecamController.cameraSpeed;
        }

        // Right
        if(Freecam.freecamController.strafe < 0)
        {
            freecamPosition.z += Math.sin(radians) * deltaTime * Freecam.freecamController.cameraSpeed;
            freecamPosition.x += Math.cos(radians) * deltaTime * Freecam.freecamController.cameraSpeed;
        }
        if(Freecam.freecamController.jumping){
            freecamPosition.y += deltaTime * Freecam.freecamController.cameraSpeed;
        }

        if(Freecam.freecamController.sneaking){
            freecamPosition.y -= deltaTime * Freecam.freecamController.cameraSpeed;
        }

        Freecam.freecamController.setCameraPosition(freecamPosition.x, freecamPosition.y, freecamPosition.z);
    }
    @Inject(at = @At("HEAD"), method = "method_1845", cancellable = true)
    private void freecam_hudHandler(CallbackInfo ci){
        if(!Freecam.freecamController.isActive()){
            return;
        }
        ci.cancel();
    }

//    @Inject(at = @At("HEAD"), method = "method_1844")
//    private void freecam_hideHudHandler(float par1, CallbackInfo ci){
//        if(!Freecam.freecamController.isActive()){
//            return;
//        }
//        originalHideHudState = field_2349.options.hideHud;
//
//        field_2349.options.hideHud = true;
//    }
//
//    @Inject(at = @At("TAIL"), method = "method_1844")
//    private void freecam_hideHudHandler2(float par1, CallbackInfo ci){
//        if(!Freecam.freecamController.isActive()){
//            return;
//        }
//        field_2349.options.hideHud = originalHideHudState;
//    }
}
