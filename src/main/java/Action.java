/**
 * Created with IntelliJ IDEA.
 * User: che
 * Date: 03.11.12
 * Time: 12:40
 * Возможное действие танка на поле боя
 */
public abstract class Action implements Comparable<Action> {
    static Env env = MyStrategy.env;
    Variant variant = Variant.none;

    public Variant getVariant() {
        return variant;
    }
    public abstract void estimate();
    public abstract void perform();
    public void tryPerformSecondary() {}

    public int compareTo(Action o) {
        return  this.getVariant().ordinal() - o.getVariant().ordinal();
    }


}
