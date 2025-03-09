package ralf2oo2.freecam.util;

public class CameraPosition {
    public double x;
    public double y;
    public double z;
    public Quaternion rotation;

    public CameraPosition(double x, double y, double z, Quaternion rotation) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.rotation = rotation;
    }

    public CameraPosition(){
        this.x = 0;
        this.y = 0;
        this.z = 0;
        this.rotation = new Quaternion();
    }

    // Add two camerapositions together
    public static CameraPosition add(CameraPosition cameraPosition1, CameraPosition cameraPosition2){
        CameraPosition resultingPosition = new CameraPosition();
        resultingPosition.x = cameraPosition1.x + cameraPosition2.x;
        resultingPosition.y = cameraPosition1.y + cameraPosition2.y;
        resultingPosition.z = cameraPosition1.z + cameraPosition2.z;
        resultingPosition.rotation = cameraPosition1.rotation.multiply(cameraPosition2.rotation);
        return resultingPosition;
    }

    // Subtract two camerapositions
    public static CameraPosition subtract(CameraPosition cameraPosition1, CameraPosition cameraPosition2){
        CameraPosition resultingPosition = new CameraPosition();
        resultingPosition.x = cameraPosition1.x - cameraPosition2.x;
        resultingPosition.y = cameraPosition1.y - cameraPosition2.y;
        resultingPosition.z = cameraPosition1.z - cameraPosition2.z;
        Quaternion inverseQuaternion = cameraPosition2.rotation.invert();
        resultingPosition.rotation = cameraPosition1.rotation.multiply(inverseQuaternion);
        return resultingPosition;
    }

    // Clone cameraposition
    public CameraPosition clone(){
        CameraPosition cameraPosition = new CameraPosition(this.x, this.y, this.z, this.rotation);
        return cameraPosition;
    }
}
