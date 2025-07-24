import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Statistics {
    private double totalTraffic;
    private LocalDateTime minTime;
    private LocalDateTime maxTime;
    private Set<String> uniqURLs = new HashSet<>(); //HashSet - это реализация интерфейса Set из стандартной библиотеки Java (java.util). Хранит уникальные элементы (дубликаты запрещены). Порядок элементов не гарантируется (может меняться при добавлении/удалении).
    private Set<String> notFoundURLs = new HashSet<>();
    private Map<String, Integer> osCounts = new HashMap<>(); // Новая переменная для подсчета ОС
    private Map<String, Integer> browserCounts = new HashMap<>();

    public void addEntry(LogEntry entry) {
        totalTraffic += entry.getBytesSent();

        LocalDateTime entryTime = entry.getReqDateAndTime();
        if (minTime == null || entryTime.isBefore(minTime)) {
            minTime = entryTime;
        }
        if (maxTime == null || entryTime.isAfter(maxTime)) {
            maxTime = entryTime;
        }
        // Сохраняем уникальные урлы в список
        if (entry.getRespCode() == 200) {
            uniqURLs.add(entry.getReqURL());
        }
        // Сохраняем битые урлы в список
        if (entry.getRespCode() == 404) {
            notFoundURLs.add(entry.getReqURL());
        }

        // Добавляем подсчет ОС
        try {
            UserAgent userAgent = new UserAgent(entry.getUserAgent()); // Создаем экземпляр
            String os = userAgent.getOs();
            osCounts.put(os, osCounts.getOrDefault(os, 0) + 1);
            /*Проверяет, есть ли уже такая ОС в нашей мапе: osCounts.getOrDefault(os, 0) пытается получить значение по ключу os.
          Если ключа нет, возвращает 0 (значение по умолчанию)
          Увеличивает счётчик на 1:
          К полученному значению (или 0) прибавляется 1
          Сохраняет новое значение:
          put() обновляет запись в мапе, связывая ОС (os) с новым значением счётчика*/
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid UserAgent: " + e.getMessage());
        }

        try {
            UserAgent userAgent = new UserAgent(entry.getUserAgent()); // Создаем экземпляр
            String browserName = userAgent.getBrowser();
            browserCounts.put(browserName, browserCounts.getOrDefault(browserName, 0) + 1); //Все аналогично блоку ОС, но для браузеров
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid UserAgent: " + e.getMessage());
        }
    }

    public Map<String, Double> getOsStatistics() {
        Map<String, Double> osStats = new HashMap<>(); // Создаём пустую HashMap, где: Ключ (String) - название операционной системы. Значение (Double) - доля этой ОС от общего количества (от 0 до 1)
        int totalOS = osCounts.values().stream().mapToInt(Integer::intValue).sum();
/*
osCounts.values() - получаем коллекцию всех значений из мапы osCounts (это количества для каждой ОС)
.stream() - преобразуем в поток (Stream API)
.mapToInt Создаёт "конвейер для примитивов" Это метод потока, который: Меняет тип потока с Stream<Integer> (поток объектов) → IntStream (поток примитивов int). Даёт доступ к "примитивным" операциям (.sum(), .average() и т.д.)
(Integer::intValue) Преобразует объект в примитив. Это функция-преобразователь, которая: берёт объект Integer → вызывает его метод .intValue() → возвращает int
.sum() - суммируем все значения
Итог: в total получаем общее количество всех записей об ОС.
 */
        if (totalOS > 0) { // Избегаем деления на ноль
            osCounts.forEach((os, count) ->
                    osStats.put(os, (double) count / totalOS)
            );
            /*
            osCounts.forEach - перебираем все пары (ОС → количество) в исходной мапе
            Для каждой пары:
            os - название ОС (ключ)
            count - сколько раз встретилась (значение)

            (double) count / total - вычисляем долю:
            (double) - явное приведение к double, чтобы было дробное деление
            count / total - делим количество конкретной ОС на общее количество
             */
        }

        return osStats;
    }

    // Тут всё аналогично как для ОС, но для браузеров
    public Map<String, Double> getBrowserStatistics() {
        Map<String, Double> browserStats = new HashMap<>();
        int totalBrowser = browserCounts.values().stream().mapToInt(Integer::intValue).sum();

        if (totalBrowser > 0) { // Избегаем деления на ноль
            browserCounts.forEach((os, count) ->
                    browserStats.put(os, (double) count / totalBrowser)
            );
        }
        return browserStats;
    }

    public String getSimpleOsStatistics() {
        Map<String, Double> osStats = getOsStatistics();
        if (osStats.isEmpty()) {
            return "No OS statistics available";
        }

        StringBuilder result = new StringBuilder();
        result.append("OS Statistics:\n");

        for (Map.Entry<String, Double> entry : osStats.entrySet()) {
            // Форматируем процент с 4 знаками после запятой
            String percent = String.format("%.4f%%", entry.getValue() * 100);
            result.append(entry.getKey()).append(": ").append(percent).append("\n");
        }

        return result.toString();
    }

    public String getSimpleBrowserStatistics() {
        Map<String, Double> browserStats = getBrowserStatistics();
        if (browserStats.isEmpty()) {
            return "No browser statistics available";
        }

        StringBuilder result = new StringBuilder();
        result.append("Browser Statistics:\n");

        for (Map.Entry<String, Double> entry : browserStats.entrySet()) {
            // Форматируем процент с 4 знаками после запятой
            String percent = String.format("%.4f%%", entry.getValue() * 100);
            result.append(entry.getKey()).append(": ").append(percent).append("\n");
        }

        return result.toString();
    }

    public double getTrafficRate() {
        // Вычисляем продолжительность между minTime и maxTime
        Duration duration = Duration.between(minTime, maxTime);

        // Преобразуем в часы (с дробной частью)
        double hours = duration.toMillis() / (1000.0 * 60 * 60);

        // Избегаем деления на ноль (если все записи имеют одинаковое время)
        if (hours == 0) {
            return totalTraffic; // Все запросы были в один момент времени
        }

        // Возвращаем средний объем трафика в час
        return totalTraffic / hours;
    }

    public double getTotalTraffic() {
        return totalTraffic;
    }

    public LocalDateTime getMinTime() {
        return minTime;
    }

    public LocalDateTime getMaxTime() {
        return maxTime;
    }

    public HashSet<String> getUniqURLs() {
        return new HashSet<>(uniqURLs); // Защитная копия
    }

    public HashSet<String> getnotFoundURLs() {
        return new HashSet<>(notFoundURLs); // Защитная копия
    }
}
