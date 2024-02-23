package ralf2oo2.freecam.mixin;

import net.minecraft.class_555;
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

        System.out.println(deltaTime);
        CameraPosition freecamPosition = Freecam.freecamController.getCameraPosition();

        float radians = freecamPosition.yaw * (float)Math.PI / 180;
        System.out.print("Sin ");
        System.out.println(Math.sin(radians) * Freecam.freecamController.cameraSpeed);
        System.out.print("Cos ");
        System.out.println(Math.cos(radians) * Freecam.freecamController.cameraSpeed);

        // Forward
        if(Freecam.freecamController.move == 1)
        {
            System.out.println("forward");
            freecamPosition.z -= Math.cos(radians) * deltaTime * Freecam.freecamController.cameraSpeed;
            freecamPosition.x += Math.sin(radians) * deltaTime * Freecam.freecamController.cameraSpeed;
        }

        // Backward
        if(Freecam.freecamController.move == -1)
        {
            System.out.println("backward");
            freecamPosition.z += Math.cos(radians) * deltaTime * Freecam.freecamController.cameraSpeed;
            freecamPosition.x -= Math.sin(radians) * deltaTime * Freecam.freecamController.cameraSpeed;
        }

        // Left
        if(Freecam.freecamController.strafe == 1)
        {
            System.out.println("left");
            freecamPosition.z -= Math.sin(radians) * deltaTime * Freecam.freecamController.cameraSpeed;
            freecamPosition.x -= Math.cos(radians) * deltaTime * Freecam.freecamController.cameraSpeed;
        }

        // Right
        if(Freecam.freecamController.strafe == -1)
        {
            System.out.println("right");
            freecamPosition.z += Math.sin(radians) * deltaTime * Freecam.freecamController.cameraSpeed;
            freecamPosition.x += Math.cos(radians) * deltaTime * Freecam.freecamController.cameraSpeed;
        }
        Freecam.freecamController.setCameraPosition(freecamPosition.x, freecamPosition.y, freecamPosition.z);
    }
}
