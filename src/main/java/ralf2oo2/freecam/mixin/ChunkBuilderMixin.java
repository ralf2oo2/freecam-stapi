package ralf2oo2.freecam.mixin;

import net.minecraft.class_68;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.chunk.ChunkBuilder;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ralf2oo2.freecam.Freecam;

@Mixin(ChunkBuilder.class)
public class ChunkBuilderMixin {
    @Shadow public boolean inFrustum;
    // Unused, remove later.
    @Inject(at = @At("HEAD"), method = "updateFrustum", cancellable = true)
    private void freecam_worldRendererHandler(class_68 par1, CallbackInfo ci){
        if(!Freecam.freecamController.isActive()){
            return;
        }
        inFrustum = true;
    }
}
