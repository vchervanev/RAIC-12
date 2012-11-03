import model.*;

import static java.lang.Math.abs;
import static java.lang.Math.cos;


/**
 * Created with IntelliJ IDEA.
 * User: che
 * Date: 03.11.12
 * Time: 14:24
 * Действие по поеданию бонуса
 */
public class ActionBonus extends Action {
    Bonus target;
    /**Проверка, есть ли бонус, к которому можно стремиться и на сколько это будет успешно */
    @Override
    public void estimate() {
        Bonus[] bonuses = env.world.getBonuses();
        target = null;
        double currentCost = 0;
        for(Bonus bonus : bonuses) {
            double cost = env.self.getDistanceTo(bonus);
            // штраф за угол поворота
            cost += 250*abs(env.self.getAngleTo(bonus))/Math.PI;
            // TODO учитывать текущую скорость (линейную и угловую)
            // TODO учитывать препятствия на пути
            if (target == null || cost < currentCost){
                target = bonus;
                currentCost = cost;
            }
        }
        if (target == null){
            variant = Variant.none;
        } else if (currentCost < 200) {
            variant = Variant.bonusNearly;
        } else if (currentCost < 600) {
            variant = Variant.bonusAverage;
        } else {
            variant = Variant.bonusFarAway;
        }
    }

    @Override
    public void perform() {
        assert target != null;
        env.directMoveTo(target);

    }
}
