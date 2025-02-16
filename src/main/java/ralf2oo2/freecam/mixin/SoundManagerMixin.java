package ralf2oo2.freecam.mixin;

import net.minecraft.client.option.GameOptions;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import paulscode.sound.SoundSystem;
import ralf2oo2.freecam.Freecam;
import ralf2oo2.freecam.util.CameraPosition;

@Mixin(SoundManager.class)
public class SoundManagerMixin {
    @Shadow private static SoundSystem soundSystem;

    @Shadow private static boolean started;

    @Shadow private GameOptions gameOptions;
    // WIP doesn't work

    @Inject(at = @At("HEAD"), method = "updateListenerPosition", cancellable = true)
    public void freecam_updateListenerPosition(LivingEntity player, float scale, CallbackInfo ci) {
        if (started && gameOptions.soundVolume != 0.0F && Freecam.freecamController.isActive()) {
            CameraPosition relativeCameraPosition = Freecam.freecamController.getCameraPosition();
            float var3 = relativeCameraPosition.yaw;
            double var4 = relativeCameraPosition.x;
            double var6 = relativeCameraPosition.y;
            double var8 = relativeCameraPosition.z;
            float var10 = MathHelper.cos(-var3 * 0.017453292F - 3.1415927F);
            float var11 = MathHelper.sin(-var3 * 0.017453292F - 3.1415927F);
            float var12 = -var11;
            float var13 = 0.0F;
            float var14 = -var10;
            float var15 = 0.0F;
            float var16 = 1.0F;
            float var17 = 0.0F;
            soundSystem.setListenerPosition((float)var4, (float)var6, (float)var8);
            soundSystem.setListenerOrientation(var12, var13, var14, var15, var16, var17);
            ci.cancel();
        }
    }
}
