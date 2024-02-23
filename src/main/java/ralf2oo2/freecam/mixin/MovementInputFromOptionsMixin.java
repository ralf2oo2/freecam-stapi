package ralf2oo2.freecam.mixin;

import net.minecraft.class_41;
import net.minecraft.class_413;
import net.minecraft.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ralf2oo2.freecam.Freecam;

@Mixin(class_413.class)
public class MovementInputFromOptionsMixin extends class_41 {
    @Shadow private boolean[] field_1661;

    @Inject(at = @At("HEAD"), method = "method_1942", cancellable = true)
    private void freecam_cameraMovementHandler(PlayerEntity par1, CallbackInfo ci) {
        if(!Freecam.freecamController.isActive()){
            return;
        }
        if(!Freecam.freecamController.allowPlayerMovement){
            float move = 0f;
            float strafe = 0f;
            field_2532 = 0f;
            field_2533 = 0f;
            field_2535 = false;
            field_2536 = false;

            if(this.field_1661[0]) {
                ++move;
            }

            if(this.field_1661[1]) {
                --move;
            }

            if(this.field_1661[2]) {
                ++strafe;
            }

            if(this.field_1661[3]) {
                --strafe;
            }

            Freecam.freecamController.move = move;
            Freecam.freecamController.strafe = strafe;
            Freecam.freecamController.jumping = field_1661[4];
            Freecam.freecamController.sneaking = field_1661[5];
        }
        if(!Freecam.freecamController.allowPlayerMovement){
            ci.cancel();
        }
    }
}
