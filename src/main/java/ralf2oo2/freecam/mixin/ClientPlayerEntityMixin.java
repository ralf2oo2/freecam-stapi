package ralf2oo2.freecam.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ClientPlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ralf2oo2.freecam.Freecam;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin extends LivingEntity {
    public ClientPlayerEntityMixin(World arg) {
        super(arg);
    }

    @Inject(at = @At("HEAD"), method = "method_910", cancellable = true)
    private void freecam_cameraMovementHandler(CallbackInfo ci){
        if(!Freecam.freecamController.isActive()){
            return;
        }

        Freecam.freecamController.move = ((ClientPlayerEntity)(Object)this).field_161.field_2533;
        Freecam.freecamController.strafe = ((ClientPlayerEntity)(Object)this).field_161.field_2532;
        Freecam.freecamController.jumping = ((ClientPlayerEntity)(Object)this).field_161.field_2535;

        this.field_1060 = 0f;
        this.field_1029 = 0f;
        this.jumping = false;
        ci.cancel();
    }
}
