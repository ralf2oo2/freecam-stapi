package ralf2oo2.freecam.util;

public class Util {
    public static float wrapAngle(float angle, float max) {
        while (angle > max) {
            angle -= 2 * max;
        }
        while (angle <= -max) {
            angle += 2 * max;
        }
        return angle;
    }
}
