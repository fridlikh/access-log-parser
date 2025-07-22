import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class Statistics {
    private double totalTraffic;
    private LocalDateTime minTime;
    private LocalDateTime maxTime;
    private HashSet<String> uniqURLs = new HashSet<>(); //HashSet - это реализация интерфейса Set из стандартной библиотеки Java (java.util). Хранит уникальные элементы (дубликаты запрещены). Порядок элементов не гарантируется (может меняться при добавлении/удалении).
    private HashMap<String, Integer> osCounts = new HashMap<>(); // Новая переменная для подсчета ОС

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
    }

        public Map<String, Double> getOsStatistics () {
            Map<String, Double> osStats = new HashMap<>(); // Создаём пустую HashMap, где: Ключ (String) - название операционной системы. Значение (Double) - доля этой ОС от общего количества (от 0 до 1)
            int total = osCounts.values().stream().mapToInt(Integer::intValue).sum();
/*
osCounts.values() - получаем коллекцию всех значений из мапы osCounts (это количества для каждой ОС)
.stream() - преобразуем в поток (Stream API)
.mapToInt Создаёт "конвейер для примитивов" Это метод потока, который: Меняет тип потока с Stream<Integer> (поток объектов) → IntStream (поток примитивов int). Даёт доступ к "примитивным" операциям (.sum(), .average() и т.д.)
(Integer::intValue) Преобразует объект в примитив. Это функция-преобразователь, которая: берёт объект Integer → вызывает его метод .intValue() → возвращает int
.sum() - суммируем все значения
Итог: в total получаем общее количество всех записей об ОС.
 */
            if (total > 0) { // Избегаем деления на ноль
                osCounts.forEach((os, count) ->
                        osStats.put(os, (double) count / total)
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

    public String getSimpleOsStatistics() {
        Map<String, Double> osStats = getOsStatistics();
        if (osStats.isEmpty()) {
            return "No OS statistics available";
        }

        StringBuilder result = new StringBuilder();
        result.append("OS Statistics:\n");

        for (Map.Entry<String, Double> entry : osStats.entrySet()) {
            // Форматируем процент с 2 знаками после запятой
            String percent = String.format("%.2f%%", entry.getValue() * 100);
            result.append(entry.getKey()).append(": ").append(percent).append("\n");
        }

        return result.toString();
    }

        public double getTrafficRate () {
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

        public double getTotalTraffic () {
            return totalTraffic;
        }

        public LocalDateTime getMinTime () {
            return minTime;
        }

        public LocalDateTime getMaxTime () {
            return maxTime;
        }

        public HashSet<String> getUniqURLs () {
            return new HashSet<>(uniqURLs); // Защитная копия
        }
    }
