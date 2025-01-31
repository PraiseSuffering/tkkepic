package tkk.epic.utils;

public class MathHelper {

    public static double[] dotYP(double r,double yaw,double ptich){
        double yawCos = Math.cos((Math.PI / 180)*(-yaw));
        double yawSin = Math.sin((Math.PI / 180)*(-yaw));
        double pitchCos = Math.cos((Math.PI / 180)*(ptich));
        double pitchSin = Math.sin((Math.PI / 180)*(ptich));
        double y= -r * pitchSin;
        double z= r * pitchCos;
        double x= z * yawSin;
        z= z * yawCos;
        return new double[]{x,y,z};

    }
    public static double[] inverseDotYP(double x,double y,double z){
        double r = Math.sqrt(x*x + y*y + z*z);

        // 计算yaw
        double yaw = Math.atan2(-x,z) * (180 / Math.PI);
        if (yaw < 0) {
            yaw += 360;
        }

        // 计算pitch
        double pitch = Math.asin(-y / r) * (180 / Math.PI);

        return new double[]{yaw,pitch};

    }
}
