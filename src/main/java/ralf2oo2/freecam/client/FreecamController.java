package ralf2oo2.freecam.client;

import net.minecraft.entity.LivingEntity;
import ralf2oo2.freecam.util.CameraPosition;

import java.util.HashMap;

public class FreecamController {
    private boolean active;
    private CameraPosition cameraPosition = new CameraPosition();
    private HashMap<String, CameraPosition> savedCameraPositions = new HashMap<>();
    public float move = 0f;
    public float strafe = 0f;
    public boolean cameraPositionSet = false;
    public boolean jumping = false;
    public boolean sneaking = false;
    public boolean allowPlayerMovement = false;
    public boolean updateSpeed = false;
    public boolean savePosition = false;
    public boolean loadPosition = false;
    public String cameraPositionName = "";

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
    public void saveCameraPosition(String name){
        savedCameraPositions.put(name, cameraPosition.clone());
        System.out.println("Saving");
    }
    public void loadCameraPosition(String name){
        if(!savedCameraPositions.containsKey(name)){
            return;
        }
        cameraPosition = savedCameraPositions.get(name).clone();
        System.out.println(savedCameraPositions.get(name).x);
        System.out.println("Loading");
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
