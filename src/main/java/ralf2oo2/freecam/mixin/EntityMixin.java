package ralf2oo2.freecam.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ralf2oo2.freecam.Freecam;
import ralf2oo2.freecam.client.FreecamController;
import ralf2oo2.freecam.util.CameraPosition;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

@Mixin(Entity.class)
public class EntityMixin {

    @Inject(at = @At("HEAD"), method = "method_1362", cancellable = true)
    private void freecam_cameraMovementHandler(float yaw, float pitch, CallbackInfo ci){
        if(!((Entity) (Object)this instanceof ClientPlayerEntity) || !Freecam.freecamController.isActive()){
            return;
        }

        if(!Freecam.freecamController.allowPlayerMovement && !Freecam.freecamController.updateSpeed){
            CameraPosition freecamPosition = Freecam.freecamController.getCameraPosition();

            float var3 = freecamPosition.pitch;
            float var4 = freecamPosition.yaw;
            freecamPosition.yaw = (float)((double)freecamPosition.yaw + (double)yaw * 0.15);
            freecamPosition.pitch = (float)((double)freecamPosition.pitch - (double)pitch * 0.15);
            if (freecamPosition.pitch < -90.0F) {
                freecamPosition.pitch = -90.0F;
            }

            if (freecamPosition.pitch > 90.0F) {
                freecamPosition.pitch = 90.0F;
            }
            if(freecamPosition.yaw > 360){
                freecamPosition.yaw -= 360;
            }
            if(freecamPosition.yaw < 0){
                freecamPosition.yaw = 360 + freecamPosition.yaw;
            }
            Freecam.freecamController.setCameraRotation(freecamPosition.pitch, freecamPosition.yaw, freecamPosition.roll);
        }

        if(Freecam.freecamController.updateSpeed){
            Freecam.freecamController.cameraSpeed += yaw / 10;
            Freecam.freecamController.cameraSpeed = (float)(new BigDecimal(Freecam.freecamController.cameraSpeed).setScale(1, RoundingMode.HALF_UP).doubleValue());
            if(Freecam.freecamController.cameraSpeed < 0){
                Freecam.freecamController.cameraSpeed = 0;
            }
        }

        if(!Freecam.freecamController.allowPlayerMovement){
            ci.cancel();
        }
    }
}
