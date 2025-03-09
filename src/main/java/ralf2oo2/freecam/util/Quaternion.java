package ralf2oo2.freecam.util;

public class Quaternion {
    float w;
    float x;
    float y;
    float z;

    public Quaternion(float w, float x, float y, float z) {
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

    public float norm(){
        return (float)Math.sqrt(this.w * this.w + this.x * this.x + this.y * this.y + this.z * this.z);
    }

    public Quaternion invert() {
        float norm = norm();
        return new Quaternion(this.w / norm, -this.x / norm, -this.y / norm, -this.z / norm);
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
        float pitch, yaw, roll;

        // Pitch
        double sinp = 2 * (w * y - z * x);
        if (Math.abs(sinp) >= 1){
            pitch = (float) Math.toDegrees(Math.copySign(Math.PI / 2, sinp));
        }
        else {
            pitch = (float) Math.toDegrees(Math.asin(sinp));
        }

        // Yaw
        double siny_cosp = 2 * (w * z + x * y);
        double cosy_cosp = 1 - 2 * (y * y + z * z);
        yaw = (float) Math.toDegrees(Math.atan2(siny_cosp, cosy_cosp));

        // Roll
        double sinr_cosp = 2 * (w * x + y * z);
        double cosr_cosp = 1 - 2 * (x * x + y * y);
        roll = (float) Math.toDegrees(Math.atan2(sinr_cosp, cosr_cosp));

        return new float[]{pitch, yaw, roll};
    }

    public float[] toRotationMatrix() {
        float w = this.w;
        float x = this.x;
        float y = this.y;
        float z = this.z;
        
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

    public static Quaternion fromEuler(float pitch, float yaw, float roll) {

        pitch = Util.wrapAngle(pitch, 90);
        yaw = Util.wrapAngle(yaw, 180);
        roll = Util.wrapAngle(roll, 180);

        if(pitch > 0 || yaw > 0 || pitch < 0 || yaw < 0){
            System.out.println(pitch +" e "+ yaw + " e " + roll);
        }
        float halfPitch = pitch * 0.5f;
        float halfYaw = yaw * 0.5f;
        float halfRoll = roll * 0.5f;

        float cX = (float)Math.cos(halfPitch);
        float sX = (float)Math.sin(halfPitch);

        float cY = (float)Math.cos(halfYaw);
        float sY = (float)Math.sin(halfYaw);

        float cZ = (float)Math.cos(halfRoll);
        float sZ = (float)Math.sin(halfRoll);

        float w = sX * sY * sZ + cX * cY * cZ;
        float x = sZ * cX * cY - sX * sY * cZ;
        float y = sX * sZ * cY + sY * cX * cZ;
        float z = sX * cY * cZ - sY * sZ * cX;

        return new Quaternion(x, y, z, w);
    }
}
