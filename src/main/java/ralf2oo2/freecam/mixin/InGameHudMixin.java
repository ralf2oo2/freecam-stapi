package ralf2oo2.freecam.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.ScreenScaler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.lwjgl.opengl.GL11;
import ralf2oo2.freecam.Freecam;
import ralf2oo2.freecam.FreecamConfig;

@Mixin(InGameHud.class)
public class InGameHudMixin extends DrawContext {
    @Shadow private Minecraft minecraft;

    // Render hud text
    @Inject(at = @At("HEAD"), method = "render", cancellable = true)
    private void freecam_renderGui(float bl, boolean i, int j, int par4, CallbackInfo ci){
        if(!Freecam.freecamController.isActive()){
            return;
        }
        this.minecraft.gameRenderer.setupHudRender();
        if(Freecam.freecamController.updateSpeed){
            GL11.glPushMatrix();
            ScreenScaler scaledResolution = new ScreenScaler(this.minecraft.options, this.minecraft.displayWidth, this.minecraft.displayHeight);
            TextRenderer textRenderer = minecraft.textRenderer;
            String speedString = "Speed: " + FreecamConfig.config.speed;
            textRenderer.drawWithShadow(speedString, scaledResolution.getScaledWidth() / 2 - textRenderer.getWidth(speedString) / 2, scaledResolution.getScaledHeight() - scaledResolution.getScaledHeight() / 8, 0xFFFF55);
            GL11.glPopMatrix();
        }
        if(Freecam.freecamController.savePosition){
            GL11.glPushMatrix();
            ScreenScaler scaledResolution = new ScreenScaler(this.minecraft.options, this.minecraft.displayWidth, this.minecraft.displayHeight);
            TextRenderer textRenderer = minecraft.textRenderer;
            String speedString = "Save Camera Position: " + Freecam.freecamController.cameraPositionName;
            textRenderer.drawWithShadow(speedString, scaledResolution.getScaledWidth() / 2 - textRenderer.getWidth(speedString) / 2, scaledResolution.getScaledHeight() - scaledResolution.getScaledHeight() / 8, 0xFFFF55);
            GL11.glPopMatrix();
        }
        if(Freecam.freecamController.loadPosition){
            GL11.glPushMatrix();
            ScreenScaler scaledResolution = new ScreenScaler(this.minecraft.options, this.minecraft.displayWidth, this.minecraft.displayHeight);
            TextRenderer textRenderer = minecraft.textRenderer;
            String speedString = "Load Camera Position: " + Freecam.freecamController.cameraPositionName;
            textRenderer.drawWithShadow(speedString, scaledResolution.getScaledWidth() / 2 - textRenderer.getWidth(speedString) / 2, scaledResolution.getScaledHeight() - scaledResolution.getScaledHeight() / 8, 0xFFFF55);
            GL11.glPopMatrix();
        }
        ci.cancel();
    }
}
