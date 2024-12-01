package ralf2oo2.freecam.mixin;

import net.minecraft.client.input.Input;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ralf2oo2.freecam.Freecam;

@Mixin(KeyboardInput.class)
public class MovementInputFromOptionsMixin extends Input {
    @Shadow private boolean[] keys;

    // Reroute player movement to freecam
    @Inject(at = @At("HEAD"), method = "update", cancellable = true)
    private void freecam_cameraMovementHandler(PlayerEntity par1, CallbackInfo ci) {
        if(!Freecam.freecamController.isActive()){
            return;
        }
        if(!Freecam.freecamController.allowPlayerMovement){
            float move = 0f;
            float strafe = 0f;
            movementSideways = 0f;
            movementForward = 0f;
            jumping = false;
            sneaking = false;

            if(this.keys[0]) {
                ++move;
            }

            if(this.keys[1]) {
                --move;
            }

            if(this.keys[2]) {
                ++strafe;
            }

            if(this.keys[3]) {
                --strafe;
            }

            Freecam.freecamController.move = move;
            Freecam.freecamController.strafe = strafe;
            Freecam.freecamController.jumping = keys[4];
            Freecam.freecamController.sneaking = keys[5];
        }
        if(!Freecam.freecamController.allowPlayerMovement){
            ci.cancel();
        }
    }
}
