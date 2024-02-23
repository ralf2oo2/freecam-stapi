package ralf2oo2.freecam.mixin;

import net.minecraft.client.InteractionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ralf2oo2.freecam.Freecam;

@Mixin(InteractionManager.class)
public class InteractionManagerMixin {
    @Inject(at = @At("HEAD"), method = "method_1722", cancellable = true)
    private void freecam_hudVisibilityHandler(CallbackInfoReturnable<Boolean> cir){
        if(!Freecam.freecamController.isActive()){
            return;
        }
        cir.setReturnValue(false);
    }
}
