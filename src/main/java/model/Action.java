package model;

/**
 * Created with IntelliJ IDEA.
 * User: che
 * Date: 03.11.12
 * Time: 12:40
 * Возможное действие танка на поле боя
 */
public abstract class Action implements Comparable<Action> {
    Variant variant = Variant.none;

    public Variant getVariant() {
        return variant;
    }
    public abstract void estimate();
    public abstract void perform();
    public void tryPerformSecondary() {}

    public int compareTo(Action o) {
        return  o.getVariant().ordinal() - this.getVariant().ordinal();
    }


}
