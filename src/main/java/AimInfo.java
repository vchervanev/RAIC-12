import static java.lang.Math.round;
import static java.lang.Math.PI;

/**
* Created with IntelliJ IDEA.
* User: che
* Date: 18.11.12
* Time: 15:20
* To change this template use File | Settings | File Templates.
*/
public class AimInfo {
    public double minAngle;
    public double maxAngle;
    public double radius;

    @Override
    public String toString() {
        return String.format("%d-%d/r", round(minAngle/PI*180), round(maxAngle/PI*180), round(radius));
    }
}
