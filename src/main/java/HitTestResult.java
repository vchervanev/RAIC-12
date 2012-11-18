/**
 * Created with IntelliJ IDEA.
 * User: che
 * Date: 14.11.12
 * Time: 21:55
 */
public class HitTestResult {

    public int tickCount;
    public TankSideType tankSideType;
    public double hitAngle;
    public Point point;
    public Section section;


    public HitTestResult(int tickCount, TankSideType tankSideType, double hitAngle, Point point, Section section) {
        this.point = point;
        this.tickCount = tickCount;
        this.tankSideType = tankSideType;
        this.hitAngle = hitAngle;
        this.section = section;
    }

    @Override
    public boolean equals(Object object) {
        if (! (object instanceof HitTestResult))
            return false;
        HitTestResult hitTestResult =  (HitTestResult)object;
        return hitTestResult.tickCount == this.tickCount &&  hitTestResult.tankSideType == this.tankSideType &&
                hitTestResult.hitAngle == this.hitAngle;
    }

    @Override
    public String toString(){
        if (this.equals(Miss))
            return "Miss";
        else
            return "Hit in " + tickCount + " tick(s) at " + tankSideType.toString() + " on " + hitAngle + " angle";
    }

    public static final HitTestResult Miss = new HitTestResult(-1, TankSideType.Face, 0, Point.NaP, null);
}
