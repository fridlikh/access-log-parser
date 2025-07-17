import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

///C:\Users\lfridlikh\Desktop\test_automatiozation\HW\short_access.log
public class LogEntry {
    private final String ipAddress;
    private final LocalDateTime timestamp;
    private final Methods reqMethod;
    private final String reqURL;
    private final int respCode;
    private final int bytesSent;
    private final String refer;
    private final String userAgent;

    public LogEntry(String logLine) {
        if (logLine == null || logLine.isEmpty()) {
            throw new IllegalArgumentException("Log line cannot be null or empty");
        }

        // Временные переменные для хранения промежуточных результатов парсинга
        String parsedIp;
        LocalDateTime parsedTime;
        Methods parsedMethod;
        String parsedPath;
        int parsedStatus;
        long parsedSize;
        String parsedReferer;
        String parsedAgent;

        try {
            // Разбиваем строку лога на части по пробелам (максимум 16 частей)
            String[] parts = logLine.split(" ", 16);
            if (parts.length < 10) {
                throw new IllegalArgumentException("Not enough parts in log line");
            }

            // 1. IP-адрес
            parsedIp = parts[0];

            // 2. Дата и время
            int timeStart = logLine.indexOf("[") + 1; // +1 чтобы пропустить саму скобку
            int timeEnd = logLine.indexOf("]");
            String timeStr = logLine.substring(timeStart, timeEnd);
            parsedTime = parseDateTime(timeStr); // Используем вспомогательный метод

            // 3. HTTP-запрос
            int requestStart = logLine.indexOf("\"") + 1;
            int requestEnd = logLine.indexOf("\"", requestStart);
            String request = logLine.substring(requestStart, requestEnd);

            // Извлекаем метод (первое слово до пробела)
            int firstSpace = request.indexOf(' ');
            if (firstSpace == -1) {
                throw new IllegalArgumentException("Invalid HTTP request format");
            }
            parsedMethod = Methods.valueOf(request.substring(0, firstSpace));

            // Извлекаем URL (всё между методом и HTTP/версией)
            int lastSpace = request.lastIndexOf(' ');
            if (lastSpace <= firstSpace) {
                throw new IllegalArgumentException("Invalid HTTP request format");
            }
            parsedPath = request.substring(firstSpace + 1, lastSpace);

            // 4. Код статуса и размер ответа
            String tail = logLine.substring(requestEnd + 1).trim();
            String[] tailParts = tail.split("\""); // Разбиваем по кавычкам

            // Статус и размер - часть перед кавычками Referer
            String statusSizePart = tailParts[0].trim();
            int firstSpaceIdx = statusSizePart.indexOf(' ');
            int lastSpaceIdx = statusSizePart.lastIndexOf(' ');

            // Проверяем наличие пробелов
            if (firstSpaceIdx == -1 || lastSpaceIdx == -1) {
                throw new IllegalArgumentException("Missing status or size");
            }

            parsedStatus = Integer.parseInt(statusSizePart.substring(0, firstSpaceIdx));
            parsedSize = Long.parseLong(statusSizePart.substring(lastSpaceIdx + 1));

            // 5. Referer и User-Agent
            String refererPart = "";
            String agentPart = "";

            if (tailParts.length > 1) {
                // Объединяем все части после статуса/размера
                StringBuilder remaining = new StringBuilder();
                for (int i = 1; i < tailParts.length; i++) {
                    remaining.append(tailParts[i]);
                    if (i < tailParts.length - 1) {
                        remaining.append("\""); // Восстанавливаем разбитые кавычки
                    }
                }

                // Разбиваем по пробелам, сохраняя кавычки
                String[] refAgentParts = remaining.toString().trim().split("\\s+", 2);

                // Первая часть - Referer
                if (refAgentParts.length > 0) {
                    refererPart = refAgentParts[0].replaceAll("^\"|\"$", "");
                }

                // Вторая часть - User-Agent
                if (refAgentParts.length > 1) {
                    agentPart = refAgentParts[1].replaceAll("^\"|\"$", "");
                }
            }

            parsedReferer = refererPart.isEmpty() || refererPart.equals("-") ? null : refererPart;
            parsedAgent = agentPart.isEmpty() || agentPart.equals("-") ? null : agentPart;

            // Инициализация final-полей
            this.ipAddress = parsedIp;
            this.timestamp = parsedTime;
            this.reqMethod = parsedMethod;
            this.reqURL = parsedPath;
            this.respCode = parsedStatus;
            this.bytesSent = (int) parsedSize; // Приводим long к int
            this.refer = parsedReferer;
            this.userAgent = parsedAgent;

        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to parse log line: " + logLine, e);
        }
    }

    private LocalDateTime parseDateTime(String dateTimeStr) {
        // Удаляем пробел между временем и часовым поясом
        String normalized = dateTimeStr.replaceFirst(" (\\+|-)", "$1");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ssZ", Locale.ENGLISH);
        try {
            return LocalDateTime.parse(normalized, formatter);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format: " + dateTimeStr, e);
        }
    }

    ///////////ГЕТТЕРЫ////////////////////////////
    public String getIpAddress() {return ipAddress;}
    public String getUserAgent() {return userAgent;}
    public String getRefer() {return refer;}
    public int getBytesSent() {return bytesSent;}
    public int getRespCode() {return respCode;}
    public String getReqURL() {return reqURL;}
    public Methods getReqMethod() {return reqMethod;}
    public LocalDateTime getReqDateAndTime() {return timestamp;}
    ////////////////ГЕТТЕРЫ////////////////////////

    public enum Methods {
        GET("Получение ресурса с сервера"),
        POST("Отправка данных на сервер"),
        PUT("Обновление ресурса на сервере"),
        PATCH("Частично обновляет ресурс"),
        HEAD("Возвращает только заголовки ответа"),
        OPTIONS("Запрашивает информацию о том, какие методы поддерживаются"),
        CONNECT("Используется для установки туннеля"),
        DELETE("Удаление ресурса на сервере"),
        TRACE("Возвращает полученный запрос");

        private final String description;

        Methods(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}