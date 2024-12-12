package ralf2oo2.freecam.util;

import net.minecraft.util.math.Box;

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

    // Add two camerapositions together
    public static CameraPosition add(CameraPosition cameraPosition1, CameraPosition cameraPosition2){
        CameraPosition resultingPosition = new CameraPosition();
        resultingPosition.x = cameraPosition1.x + cameraPosition2.x;
        resultingPosition.y = cameraPosition1.y + cameraPosition2.y;
        resultingPosition.z = cameraPosition1.z + cameraPosition2.z;
        resultingPosition.pitch = cameraPosition1.pitch + cameraPosition2.pitch;
        resultingPosition.yaw = cameraPosition1.yaw + cameraPosition2.yaw;
        resultingPosition.roll = cameraPosition1.roll + cameraPosition2.roll;
        return resultingPosition;
    }

    // Subtract two camerapositions
    public static CameraPosition subtract(CameraPosition cameraPosition1, CameraPosition cameraPosition2){
        CameraPosition resultingPosition = new CameraPosition();
        resultingPosition.x = cameraPosition1.x - cameraPosition2.x;
        resultingPosition.y = cameraPosition1.y - cameraPosition2.y;
        resultingPosition.z = cameraPosition1.z - cameraPosition2.z;
        resultingPosition.pitch = cameraPosition1.pitch - cameraPosition2.pitch;
        resultingPosition.yaw = cameraPosition1.yaw - cameraPosition2.yaw;
        resultingPosition.roll = cameraPosition1.roll - cameraPosition2.roll;
        return resultingPosition;
    }

    // Clone cameraposition
    public CameraPosition clone(){
        CameraPosition cameraPosition = new CameraPosition(this.x, this.y, this.z, this.pitch, this.yaw, this.roll);
        return cameraPosition;
    }
}
