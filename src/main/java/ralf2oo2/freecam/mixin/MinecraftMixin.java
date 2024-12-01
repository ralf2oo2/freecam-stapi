package ralf2oo2.freecam.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ralf2oo2.freecam.Freecam;

@Mixin(Minecraft.class)
public class MinecraftMixin {

    // Load camerapositions from json when entering world
    @Inject(at = @At("TAIL"), method = "setWorld(Lnet/minecraft/world/World;Ljava/lang/String;Lnet/minecraft/entity/player/PlayerEntity;)V")
    private void freecam_savedCameraPositionLoader(World string, String arg2, PlayerEntity par3, CallbackInfo ci){
        Freecam.freecamController.loadSavedCameraPositions(string);
        Freecam.freecamController.cameraPositionSet = false;
        Freecam.freecamController.setActive(false);
    }
}
