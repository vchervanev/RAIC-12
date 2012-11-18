import model.*;


/**
 * Created with IntelliJ IDEA.
 * User: che
 * Date: 03.11.12
 * Time: 13:30
 * Все возможные варианты действий, в порядке приоритета
 */
public enum Variant {
    dodge, // бежать
    bonusUrgent, // очень нужный бонус и не очень далеко
    hide,
    aimUrgent, // срочно прицеливаемся
    bonusNearly, // близкий бонус
    aimFast, // немного прицелиться, дальше будет стрельба с близкого расстояния,
    combatPosition, // разворот или маневр
    bonusAverage, // бонус на средней дистанции
    combatPositionSoon, // через 50 тиков (?) появится combatPosition
    bonusFarAway, // далекий бонус
    none
}
