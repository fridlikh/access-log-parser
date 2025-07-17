import java.time.Duration;
import java.time.LocalDateTime;

public class Statistics {
    private double totalTraffic;
    private LocalDateTime minTime;
    private LocalDateTime maxTime;

    public void addEntry(LogEntry entry) {
        totalTraffic += entry.getBytesSent();

        LocalDateTime entryTime = entry.getReqDateAndTime();
        if (minTime == null || entryTime.isBefore(minTime)) {
            minTime = entryTime;
        }
        if (maxTime == null || entryTime.isAfter(maxTime)) {
            maxTime = entryTime;
        }
    }
    public double getTrafficRate(){
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
    public double getTotalTraffic() { return totalTraffic; }
    public LocalDateTime getMinTime() { return minTime; }
    public LocalDateTime getMaxTime() { return maxTime; }
}
