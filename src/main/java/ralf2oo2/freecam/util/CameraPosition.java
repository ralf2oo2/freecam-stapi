package ralf2oo2.freecam.util;

public class CameraPosition {
    public double x;
    public double y;
    public double z;
    public float pitch;
    public float yaw;
    public float roll;

    public CameraPosition(double x, double y, double z, float pitch, float yaw, float roll) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.pitch = pitch;
        this.yaw = yaw;
        this.roll = roll;
    }

    public CameraPosition(){

    }

    public CameraPosition add(CameraPosition cameraPosition){
        this.x += cameraPosition.x;
        this.y += cameraPosition.y;
        this.z += cameraPosition.z;
        this.pitch += cameraPosition.pitch;
        this.yaw += cameraPosition.yaw;
        this.roll += cameraPosition.roll;
        return this;
    }
}
