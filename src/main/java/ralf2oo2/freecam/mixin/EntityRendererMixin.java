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
    float LOW_LIMIT = 0.000167f; // Set to unreasonable value making it pretty much useless
    float HIGH_LIMIT = 0.1f;
    long lastTime = System.nanoTime();
    @Inject(at = @At("TAIL"), method = "method_1844", cancellable = true)
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
            freecamPosition.z -= Math.cos(radians) * deltaTime * Freecam.config.speed;
            freecamPosition.x += Math.sin(radians) * deltaTime * Freecam.config.speed;
        }

        // Backward
        if(Freecam.freecamController.move < 0)
        {
            freecamPosition.z += Math.cos(radians) * deltaTime * Freecam.config.speed;
            freecamPosition.x -= Math.sin(radians) * deltaTime * Freecam.config.speed;
        }

        // Left
        if(Freecam.freecamController.strafe > 0)
        {
            freecamPosition.z -= Math.sin(radians) * deltaTime * Freecam.config.speed;
            freecamPosition.x -= Math.cos(radians) * deltaTime * Freecam.config.speed;
        }

        // Right
        if(Freecam.freecamController.strafe < 0)
        {
            freecamPosition.z += Math.sin(radians) * deltaTime * Freecam.config.speed;
            freecamPosition.x += Math.cos(radians) * deltaTime * Freecam.config.speed;
        }
        if(Freecam.freecamController.jumping){
            freecamPosition.y += deltaTime * Freecam.config.speed;
        }

        if(Freecam.freecamController.sneaking){
            freecamPosition.y -= deltaTime * Freecam.config.speed;
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
}
