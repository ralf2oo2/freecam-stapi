package ralf2oo2.freecam.mixin;

import net.minecraft.class_564;
import net.minecraft.class_68;
import net.minecraft.client.Minecraft;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL12;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.lwjgl.opengl.GL11;
import ralf2oo2.freecam.Freecam;

@Mixin(InGameHud.class)
public class InGameHudMixin extends DrawContext {
    @Shadow private Minecraft minecraft;
    @Inject(at = @At("HEAD"), method = "render", cancellable = true)
    private void freecam_renderGui(float bl, boolean i, int j, int par4, CallbackInfo ci){
        if(!Freecam.freecamController.isActive()){
            return;
        }
        this.minecraft.field_2818.method_1843();
        if(Freecam.freecamController.updateSpeed){
            GL11.glPushMatrix();
            class_564 scaledResolution = new class_564(this.minecraft.options, this.minecraft.displayWidth, this.minecraft.displayHeight);
            TextRenderer textRenderer = minecraft.textRenderer;
            String speedString = "Speed: " + Freecam.config.speed;
            textRenderer.drawWithShadow(speedString, scaledResolution.method_1857() / 2 - textRenderer.getWidth(speedString) / 2, scaledResolution.method_1858() - scaledResolution.method_1858() / 8, 0xFFFF55);
            System.out.println(scaledResolution.method_1857());
            System.out.println(scaledResolution.method_1858());
            GL11.glPopMatrix();
            System.out.println(this.minecraft.interactionManager.method_1722());
        }
        if(Freecam.freecamController.savePosition){
            GL11.glPushMatrix();
            class_564 scaledResolution = new class_564(this.minecraft.options, this.minecraft.displayWidth, this.minecraft.displayHeight);
            TextRenderer textRenderer = minecraft.textRenderer;
            String speedString = "Save Camera Position: " + Freecam.freecamController.cameraPositionName;
            textRenderer.drawWithShadow(speedString, scaledResolution.method_1857() / 2 - textRenderer.getWidth(speedString) / 2, scaledResolution.method_1858() - scaledResolution.method_1858() / 8, 0xFFFF55);
            GL11.glPopMatrix();
        }
        if(Freecam.freecamController.loadPosition){
            GL11.glPushMatrix();
            class_564 scaledResolution = new class_564(this.minecraft.options, this.minecraft.displayWidth, this.minecraft.displayHeight);
            TextRenderer textRenderer = minecraft.textRenderer;
            String speedString = "Load Camera Position: " + Freecam.freecamController.cameraPositionName;
            textRenderer.drawWithShadow(speedString, scaledResolution.method_1857() / 2 - textRenderer.getWidth(speedString) / 2, scaledResolution.method_1858() - scaledResolution.method_1858() / 8, 0xFFFF55);
            GL11.glPopMatrix();
        }
        ci.cancel();
    }
}
