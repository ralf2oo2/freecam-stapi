package ralf2oo2.freecam.util;

import org.lwjgl.util.vector.Vector3f;

public class Quaternion {
    public double w;
    public double x;
    public double y;
    public double z;

    public Quaternion(double w, double x, double y, double z) {
        this.w = w;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Quaternion(){
        this.w = 1;
        this.x = 0;
        this.y = 0;
        this.z = 0;
    }

    public Quaternion(Quaternion origin){
        this.w = origin.w;
        this.x = origin.x;
        this.y = origin.y;
        this.z = origin.z;
    }

    public float norm(){
        return (float)Math.sqrt(this.w * this.w + this.x * this.x + this.y * this.y + this.z * this.z);
    }

    public Quaternion invert() {
        float norm = norm();
        return new Quaternion(this.w / norm, -this.x / norm, -this.y / norm, -this.z / norm);
    }

    public Quaternion inverse() {
        double normSquared = this.norm();
        if (normSquared == 0) {
            return new Quaternion();
        }

        Quaternion conjugate = this.conjugate();
        return new Quaternion(conjugate.x / normSquared, conjugate.y / normSquared, conjugate.z / normSquared, conjugate.w / normSquared);
    }

    public Quaternion conjugate() {
        return new Quaternion(-x, -y, -z, w);
    }

    public void hamiltonProduct(Quaternion other) {
        double f = this.x;
        double g = this.y;
        double h = this.z;
        double i = this.w;
        double j = other.x;
        double k = other.y;
        double l = other.z;
        double m = other.w;
        this.x = i * j + f * m + g * l - h * k;
        this.y = i * k - f * l + g * m + h * j;
        this.z = i * l + f * k - g * j + h * m;
        this.w = i * m - f * j - g * k - h * l;
    }

    public static Quaternion fromUpVector(Vector3f upVector) {
        Vector3f defaultUp = new Vector3f(0, 1, 0); // TODO: replace with world rotation
        upVector = upVector.normalise(upVector);

        Vector3f axis = new Vector3f();
        Vector3f.cross(defaultUp, upVector, axis);
        double angle = Math.acos(Vector3f.dot(defaultUp, upVector));


        if (angle == 0) {
            return new Quaternion(0, 0, 0, 1);
        }

        if (angle == Math.PI) {
            axis = new Vector3f(1, 0, 0);
            if (Math.abs(Vector3f.dot(defaultUp, axis)) > 0.99) {
                axis = new Vector3f(0, 0, 1);
            }
        }
        return fromAxisAngleRad(axis, (float)angle);
    }

    public Quaternion normalize() {
        float norm = norm();
        if (norm == 0) return new Quaternion();
        return new Quaternion(w / norm, x / norm, y / norm, z / norm);
    }

    public Quaternion multiply(Quaternion q2){
        return new Quaternion(
                this.w * q2.w - this.x * q2.x - this.y * q2.y - this.z * q2.z,
                this.w * q2.x + this.x * q2.w + this.y * q2.z - this.z * q2.y,
                this.w * q2.y - this.x * q2.z + this.y * q2.w + this.z * q2.x,
                this.w * q2.z + this.x * q2.y - this.y * q2.x + this.z * q2.w
                );
    }

    public float[] toEulerAngles() {
        return new float[]{
                (float) this.toPitch(),
                (float) this.toYaw(),
                (float) this.toRoll()
        };
    }

    public double toPitch() {
        double pitchRad = Math.atan2(
                2*x*w - 2*y*z,
                1 - 2*x*x - 2*z*z
        );
        return Math.toDegrees(pitchRad);
    }

    public double toRoll() {
        double test = x*y + z*w;

        double rolRad = Math.asin(2*test);
        return Math.toDegrees(rolRad);
    }

    public double toYaw() {

        double yawRad =  Math.atan2(
                2 * y * w - 2 * x * z,
                1 - 2 * y * y - 2 * z * z
        );
        return (Math.toDegrees(yawRad));
    }

        public float[] toRotationMatrix() {
        float w = (float) this.w;
        float x = (float)this.x;
        float y = (float)this.y;
        float z = (float)this.z;
        
        float xx = x * x;
        float yy = y * y;
        float zz = z * z;
        float xy = x * y;
        float xz = x * z;
        float yz = y * z;
        float wx = w * x;
        float wy = w * y;
        float wz = w * z;

        float[] matrix = new float[16];

        matrix[0] = 1.0f - 2.0f * (yy + zz);
        matrix[1] = 2.0f * (xy - wz);
        matrix[2] = 2.0f * (xz + wy);
        matrix[3] = 0.0f;

        matrix[4] = 2.0f * (xy + wz);
        matrix[5] = 1.0f - 2.0f * (xx + zz);
        matrix[6] = 2.0f * (yz - wx);
        matrix[7] = 0.0f;

        matrix[8] = 2.0f * (xz - wy);
        matrix[9] = 2.0f * (yz + wx);
        matrix[10] = 1.0f - 2.0f * (xx + yy);
        matrix[11] = 0.0f;

        matrix[12] = 0.0f;
        matrix[13] = 0.0f;
        matrix[14] = 0.0f;
        matrix[15] = 1.0f;

        return matrix;
    }

    public Quaternion(float w, float x, float y, float z) {
        this.w = w;
        this.x = x;
        this.y = y;
        this.z = z;
    }


    public static Quaternion fromAxisAngleRad(Vector3f axis, float angle) {
        axis.normalise();

        float halfAngle = angle / 2;
        float sinHalfAngle = (float) Math.sin(halfAngle);
        float cosHalfAngle = (float) Math.cos(halfAngle);

        float qx = axis.x * sinHalfAngle;
        float qy = axis.y * sinHalfAngle;
        float qz = axis.z * sinHalfAngle;
        float qw = cosHalfAngle;

        return new Quaternion(qw, qx, qy, qz);
    }

    public static Quaternion fromEuler(float pitch, float yaw, float roll) {
        double pitchWrapped = Math.toRadians(pitch);
        double yawWrapped = Math.toRadians(yaw);
        double rollWrapped = Math.toRadians(roll);

        double cy = Math.cos(yawWrapped * 0.5);
        double sy = Math.sin(yawWrapped * 0.5);
        double cp = Math.cos(pitchWrapped * 0.5);
        double sp = Math.sin(pitchWrapped * 0.5);
        double cr = Math.cos(rollWrapped * 0.5);
        double sr = Math.sin(rollWrapped * 0.5);

        double w = cy * cp * cr + sy * sp * sr;
        double x = cy * sp * cr - sy * cp * sr;
        double y = sy * cp * cr + cy * sp * sr;
        double z = cy * cp * sr - sy * sp * cr;

        return new Quaternion(w, x, y, z);
    }
}
