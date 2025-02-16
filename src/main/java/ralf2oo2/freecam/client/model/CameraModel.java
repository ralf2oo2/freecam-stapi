package ralf2oo2.freecam.client.model;

import net.minecraft.client.model.ModelPart;

public class CameraModel {
    ModelPart cameraBase = new ModelPart(0, 0);
    ModelPart cameraLens = new ModelPart(0, 13);
    ModelPart cameraButton = new ModelPart(14, 13);

    // TODO: fix camera model
    public CameraModel(){
        cameraBase.addCuboid(-7.0F, 0.0F, -2.0F, 13, 9, 4, 0.0F);
        cameraLens.addCuboid(-3.0F, 2.0F, 2.0F, 5, 5, 2, 0.0F);
        cameraButton.addCuboid(2.0F, 9.0F, -1.0F, 3, 1, 2, 0.0F);
    }

    public void render(){
        cameraBase.render(0.0625F);
        cameraLens.render(0.0625F);
        cameraButton.render(0.0625F);
    }
}
