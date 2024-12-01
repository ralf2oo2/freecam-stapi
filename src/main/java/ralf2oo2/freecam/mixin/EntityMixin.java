package ralf2oo2.freecam.mixin;

import net.glasslauncher.mods.gcapi3.api.GCAPI;
import net.glasslauncher.mods.gcapi3.impl.GlassYamlFile;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ClientPlayerEntity;
import net.modificationstation.stationapi.api.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ralf2oo2.freecam.Freecam;
import ralf2oo2.freecam.FreecamConfig;
import ralf2oo2.freecam.util.CameraPosition;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Mixin(Entity.class)
public class EntityMixin {

    @Inject(at = @At("HEAD"), method = "changeLookDirection", cancellable = true)
    private void freecam_cameraMovementHandler(float yaw, float pitch, CallbackInfo ci){
        if(!((Entity) (Object)this instanceof ClientPlayerEntity) || !Freecam.freecamController.isActive()){
            return;
        }

        // Move freecam rotation
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

        // Update freecam speed
        if(Freecam.freecamController.updateSpeed){
            FreecamConfig.config.speed += yaw / 10;
            FreecamConfig.config.speed = (float)(new BigDecimal(FreecamConfig.config.speed).setScale(1, RoundingMode.HALF_UP).doubleValue());
            if(FreecamConfig.config.speed < 0){
                FreecamConfig.config.speed = (float)0;
            }
            if(FreecamConfig.config.speed > 1000){
                FreecamConfig.config.speed = (float)1000;
            }
            GlassYamlFile modConfigFile = new GlassYamlFile();
            modConfigFile.set ("speed", FreecamConfig.config.speed);
            GCAPI.reloadConfig(Identifier.of("freecam:config").toString(), modConfigFile);
        }

        if(!Freecam.freecamController.allowPlayerMovement){
            ci.cancel();
        }
    }
}
