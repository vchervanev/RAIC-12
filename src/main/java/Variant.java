import model.*;


/**
 * Created with IntelliJ IDEA.
 * User: che
 * Date: 03.11.12
 * Time: 13:30
 * Все возможные варианты действий, в порядке приоритета
 */
public enum Variant {
    fireAndKill, // стрельба с близкого расстояния
    bonusUrgent, // очень нужный бонус и не очень далеко
    bonusNearly, // близкий бонус
    aimFast, // немного прицелиться, дальше будет стрельба с близкого расстояния,
    fireTryKill, // стрельба со среднего расстояния
    aimAverage, // среднее время для прицеливания
    bonusAverage, // бонус на средней дистанции
    aimSlow,  // нужно много времени для прицеливания
    bonusFarAway, // далекий бонус
    fireHardlyKill, // стрельба издалекаааа
    aimSlowest, // долгий перезаряд
    none
}
