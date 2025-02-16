package ralf2oo2.freecam.util;

public class CollisionResult {
    public double entryTime;
    public int[] normal;

    public CollisionResult(double entryTime, int[] normal) {
        this.entryTime = entryTime;
        this.normal = normal;
    }

    public double getEntryTime(){
        return this.entryTime;
    }
    public int[] getNormal(){
        return this.normal;
    }
}
