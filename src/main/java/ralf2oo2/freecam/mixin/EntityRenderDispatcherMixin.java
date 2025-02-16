package ralf2oo2.freecam.mixin;

import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ralf2oo2.freecam.Freecam;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {
    @Shadow public LivingEntity cameraEntity;

    @Inject(at = @At("HEAD"), method = "render(Lnet/minecraft/entity/Entity;F)V", cancellable = true)
    void freecam_cancelPlayerRendering(Entity entity, float par2, CallbackInfo ci) {
        if(Freecam.freecamController.isActive() && Freecam.freecamController.hidePlayer && entity == cameraEntity){
            ci.cancel();
        }
    }
}
