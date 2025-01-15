package ralf2oo2.freecam.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.render.Culler;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ralf2oo2.freecam.Freecam;
import ralf2oo2.freecam.FreecamConfig;
import ralf2oo2.freecam.client.model.CameraModel;
import ralf2oo2.freecam.util.CameraPosition;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {
    @Shadow private Minecraft client;
    @Shadow private int entityRenderCooldown;
    @Shadow private TextureManager textureManager;
    private CameraModel cameraModel = new CameraModel();

    @Inject(at = @At("TAIL"), method = "renderEntities")
    private void freecam_cameraRenderer(Vec3d arg2, Culler f, float par3, CallbackInfo ci){
        if (entityRenderCooldown <= 0) {
            if(!Freecam.freecamController.isActive() && FreecamConfig.config.showCamera){
                GL11.glPushMatrix();
                CameraPosition cameraPosition = Freecam.freecamController.getCameraPosition();
                CameraPosition relativeCameraPosition = Freecam.freecamController.getRelativeCameraPosition(cameraPosition, client.player, par3);

                GL11.glTranslatef((float) relativeCameraPosition.x, (float) relativeCameraPosition.y - 0.25f, (float) relativeCameraPosition.z);
                GL11.glRotatef(-relativeCameraPosition.yaw + 180f, 0f, 1f, 0f);
                GL11.glRotatef(relativeCameraPosition.pitch, 1f, 0f, 0f);
                GL11.glRotatef(relativeCameraPosition.roll, 0f, 0f, 1f);

                int cameraTexture = textureManager.getTextureId("/assets/freecam/textures/entity/camera.png");
                textureManager.bindTexture(cameraTexture);

                float brightness = client.world.getNaturalBrightness((int) cameraPosition.x, (int) cameraPosition.y, (int) cameraPosition.z, client.world.getBrightness((int) cameraPosition.x, (int) cameraPosition.y, (int) cameraPosition.z));
                GL11.glColor3f(brightness, brightness, brightness);

                cameraModel.render();
                GL11.glPopMatrix();
            }
            if(Freecam.freecamController.isActive() && !Freecam.freecamController.hidePlayer){
                EntityRenderDispatcher.INSTANCE.render(client.camera, par3);
            }
        }
    }
}
