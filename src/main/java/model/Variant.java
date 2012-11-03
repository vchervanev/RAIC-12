package model;

/**
 * Created with IntelliJ IDEA.
 * User: che
 * Date: 03.11.12
 * Time: 13:30
 * Все возможные варианты действий, в порядке приоритета
 */
public enum Variant {
    fireAndKill, // стрельба с близкого расстояния
    aimFast, // немного прицелиться, дальше будет стрельба с близкого расстояния,
    fireTryKill, // стрельба со среднего расстояния
    aimAverage, // среднее время для прицеливания
    aimSlow,  // нужно много времени для прицеливания
    fireHardlyKill // стрельба издалекаааа
}
