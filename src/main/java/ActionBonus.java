import model.*;

import java.util.*;

import static java.lang.Math.*;


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

        List<Tank> tanks = Arrays.asList(env.world.getTanks());
        tanks.remove(env.self);


        target = null;
        double currentCost = 0;
        HashMap<Tank, Double> friendDistance = new HashMap<Tank, Double>(2);
        for(Bonus bonus : bonuses) {
            double cost = env.self.getDistanceTo(bonus);
            // ближайший к бонусу враг
            double minEnemyDistance = 9999;

            // штраф за танки на пути
            boolean skip = false;
            for(Tank tank : tanks) {
                if (tank.isTeammate() && tank.getId() != env.self.getId() && tank.getCrewHealth() != 0
                        || tank.getHullDurability() != 0) {
                    friendDistance.put(tank, tank.getDistanceTo(bonus));
                }
                // член команды или труп - шут с ним
                if (tank.getPlayerName().equals(env.self.getPlayerName()) || tank.getCrewHealth() == 0
                        || tank.getHullDurability() == 0){
                    continue;
                }
                double enemyDistance = tank.getDistanceTo(bonus);
                if (enemyDistance < minEnemyDistance)
                    minEnemyDistance = enemyDistance;
                // враг недалеко и ближе нас
                if (enemyDistance < 600 && enemyDistance < cost) {
                    skip = true;
                    break;
                }
            }
            if (skip) {
                continue;
            }

            // поищем больных коллег недалеко
            for(Map.Entry<Tank,Double> entry : friendDistance.entrySet()) {
                Tank friend = entry.getKey();
                double distance = entry.getValue();
                boolean need =
                        (bonus.getType() == BonusType.MEDIKIT && friend.getCrewHealth() < env.self.getCrewHealth())
                        || (bonus.getType() == BonusType.REPAIR_KIT && friend.getHullDurability() < env.self.getHullDurability())
                        || (bonus.getType() == BonusType.AMMO_CRATE && friend.getPremiumShellCount() < env.self.getPremiumShellCount());
                if (need && (distance <= cost || distance < minEnemyDistance)) {
                    skip = true;
                    break;
                }
            }

            if (skip) {
                continue;
            }

            // штраф за угол поворота
            cost += 350*(1-abs(1-2*abs(env.self.getAngleTo(bonus))/PI));

            // бонус нужному бонусу
            if (bonus.getType() == BonusType.MEDIKIT){
                if (env.self.getCrewHealth() < 70)
                    cost -= 200;
                else if (env.self.getCrewHealth() < 51)
                    cost -= 500;
                else if (env.self.getCrewHealth() < 40)
                    cost -= 800;
            }
            if (target == null || cost < currentCost){
                target = bonus;
                currentCost = cost;
            }
        }
        if (target == null){
            variant = Variant.none;
            return;
        } else if (currentCost < 400) {
            variant = Variant.bonusNearly;
        } else if (currentCost < 600) {
            variant = Variant.bonusAverage;
        } else {
            variant = Variant.bonusFarAway;
        }

        if ((target.getType() == BonusType.MEDIKIT && env.self.getCrewHealth() < 60) || env.self.getDistanceTo(target) < 200 ) {
            variant = Variant.bonusUrgent;
        }
        if ((target.getType() == BonusType.REPAIR_KIT && env.self.getHullDurability() < 70) || env.self.getDistanceTo(target) < 200 ) {
            variant = Variant.bonusUrgent;
        }
    }

    @Override
    public void perform() {
        if (target == null){
            return;
        }
        env.directMoveTo(target);

    }
}
