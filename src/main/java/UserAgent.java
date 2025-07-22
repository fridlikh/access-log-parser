public class UserAgent {
    private final String oS;
    private final String browser;

    public UserAgent(String getUserAgent) {
        try {
            this.browser = parseBrowser(getUserAgent);
            this.oS = parseOS(getUserAgent);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid UserAgent format: " + getUserAgent, e);
        }
    }

    private String parseBrowser(String userAgent) {
        // Удаляем всё после версии браузера
        String cleaned = userAgent.split("[/;]")[0].trim();

        // Нормализуем названия популярных браузеров
        if (cleaned.contains("Firefox")) return "Firefox";
        if (cleaned.contains("Chrome")) return "Chrome";
        if (cleaned.contains("Safari")) return "Safari";
        if (cleaned.contains("Edge")) return "Edge";
        if (cleaned.contains("Opera")) return "Opera";
        if (cleaned.contains("MSIE") || cleaned.contains("Trident")) return "Internet Explorer";

        // Обработка ботов
        if (isBot(userAgent)) return "Bot";

        return cleaned.isEmpty() ? "Unknown" : cleaned;
    }

    private String parseOS(String userAgent) {
        // Основные ОС
        if (userAgent.contains("Windows NT 10.0")) return "Windows 10";
        if (userAgent.contains("Windows NT 6.3")) return "Windows 8.1";
        if (userAgent.contains("Windows NT 6.2")) return "Windows 8";
        if (userAgent.contains("Windows NT 6.1")) return "Windows 7";
        if (userAgent.contains("Windows NT 6.0")) return "Windows Vista";
        if (userAgent.contains("Windows NT 5.1")) return "Windows XP";
        if (userAgent.contains("Mac OS X")) return "Mac OS X";
        if (userAgent.contains("iPhone") || userAgent.contains("iPad")) return "iOS";
        if (userAgent.contains("Android")) return "Android";
        if (userAgent.contains("Linux")) return "Linux";

        // Виртуальные машины, облака и т.п.
        if (userAgent.contains("Cloud")) return "Cloud Service";
        if (userAgent.contains("Java")) return "Java VM";
        if (userAgent.contains("Dalvik")) return "Android Runtime";

        // Боты и краулеры
        if (isBot(userAgent)) return "Bot";

        // Если ничего не найдено
        return "Other";
    }

    private boolean isBot(String userAgent) {
        String lower = userAgent.toLowerCase();
        return lower.contains("bot") ||
                lower.contains("crawler") ||
                lower.contains("spider") ||
                lower.contains("fetcher") ||
                lower.contains("scraper") ||
                lower.contains("parser") ||
                lower.contains("monitoring") ||
                lower.contains("http://") ||
                lower.contains("https://") ||
                lower.contains("+http") ||
                lower.contains("feed") ||
                lower.contains("rss") ||
                lower.contains("api");
    }

    public String getOs() { return oS; }
    public String getBrowser() { return browser; }
}