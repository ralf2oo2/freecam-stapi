package ralf2oo2.freecam.client;

import net.minecraft.entity.LivingEntity;
import ralf2oo2.freecam.util.CameraPosition;

public class FreecamController {
    private boolean active;
    private CameraPosition cameraPosition = new CameraPosition();
    public float cameraSpeed = 10f;
    public float move = 0f;
    public float strafe = 0f;
    public boolean jumping = false;
    public boolean sneaking = false;
    public boolean allowPlayerMovement = false;
    public boolean updateSpeed = false;

    public boolean isActive(){
        return active;
    }
    public void setActive(boolean active){
        this.active = active;
        this.allowPlayerMovement = false;
    }

    public CameraPosition getCameraPosition(){
        return cameraPosition;
    }



    public void setCameraPositionAndRotation(double x, double y, double z, float pitch, float yaw, float roll) {
        this.cameraPosition.x = x;
        this.cameraPosition.y = y;
        this.cameraPosition.z = z;
        this.cameraPosition.pitch = pitch;
        this.cameraPosition.yaw = yaw;
        this.cameraPosition.roll = roll;
    }
    public void setCameraPosition(double x, double y, double z) {
        this.cameraPosition.x = x;
        this.cameraPosition.y = y;
        this.cameraPosition.z = z;
    }

    public void setCameraRotation(float pitch, float yaw, float roll) {
        this.cameraPosition.pitch = pitch;
        this.cameraPosition.yaw = yaw;
        this.cameraPosition.roll = roll;
    }

    private CameraPosition getRelativeCameraPosition(CameraPosition cameraPosition, LivingEntity player, float f1){
        float f2 = player.eyeHeight - 1.62F;

        double d1 = player.prevX + (player.x - player.prevX) * (double)f1;
        double d2 = player.prevY + (player.y - player.prevY) * (double)f1 - (double)f2;
        double d3 = player.prevZ + (player.z - player.prevZ) * (double)f1;


        CameraPosition relativeCameraPosition = new CameraPosition((cameraPosition.x - d1), (cameraPosition.y - d2), (cameraPosition.z - d3), cameraPosition.pitch, cameraPosition.yaw , cameraPosition.roll);

        return relativeCameraPosition;
    }

    public CameraPosition updateCameraPosition(LivingEntity player, float f1){
        return getRelativeCameraPosition(this.cameraPosition, player, f1);
    }
}
