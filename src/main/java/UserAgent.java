public class UserAgent {
    private final String oS;
    private final String browser;

    public UserAgent(String userAgent) {
        this.browser = parseBrowser(userAgent);
        this.oS = parseOS(userAgent);
    }

    private String parseBrowser(String userAgent) {
        if (userAgent == null || userAgent.isBlank()) {
            return "Unknown";
        }

        String cleaned = userAgent.split("[/;]")[0].trim();

        // Основные браузеры
        if (containsAnyIgnoreCase(userAgent, "Firefox")) return "Firefox";
        if (containsAnyIgnoreCase(userAgent, "Chrome")) return "Chrome";
        if (containsAnyIgnoreCase(userAgent, "Safari")) return "Safari";
        if (containsAnyIgnoreCase(userAgent, "Edge")) return "Edge";
        if (containsAnyIgnoreCase(userAgent, "Opera")) return "Opera";
        if (containsAnyIgnoreCase(userAgent, "MSIE", "Trident")) return "Internet Explorer";
        if (containsAnyIgnoreCase(userAgent, "Yandex")) return "Yandex";

        // Мобильные приложения
        if (containsAnyIgnoreCase(userAgent, "WhatsApp")) return "WhatsApp";
        if (containsAnyIgnoreCase(userAgent, "Instagram")) return "Instagram";
        if (containsAnyIgnoreCase(userAgent, "Outlook")) return "Outlook";
        if (containsAnyIgnoreCase(userAgent, "Telegram")) return "Telegram";

        // Боты
        if (isBot(userAgent)) return "Bot";

        return cleaned.isEmpty() ? "Unknown" : cleaned;
    }

    private String parseOS(String userAgent) {
        if (userAgent == null || userAgent.isBlank()) {
            return "Unknown";
        }

        // Windows
        if (containsAnyIgnoreCase(userAgent, "Windows NT 10.0")) return "Windows 10";
        if (containsAnyIgnoreCase(userAgent, "Windows NT 6.3")) return "Windows 8.1";
        if (containsAnyIgnoreCase(userAgent, "Windows NT 6.2")) return "Windows 8";
        if (containsAnyIgnoreCase(userAgent, "Windows NT 6.1")) return "Windows 7";
        if (containsAnyIgnoreCase(userAgent, "Windows NT 6.0")) return "Windows Vista";
        if (containsAnyIgnoreCase(userAgent, "Windows NT 5.1")) return "Windows XP";

        // Другие ОС
        if (containsAnyIgnoreCase(userAgent, "Mac OS X")) return "Mac OS X";
        if (containsAnyIgnoreCase(userAgent, "iPhone", "iPad")) return "iOS";
        if (containsAnyIgnoreCase(userAgent, "Android")) return "Android";
        if (containsAnyIgnoreCase(userAgent, "Linux")) return "Linux";

        // Боты
        if (isBot(userAgent)) return "Bot";

        return "Other";
    }

    private boolean isBot(String userAgent) {
        if (userAgent == null || userAgent.isBlank()) {
            return false;
        }

        String lower = userAgent.toLowerCase();

        // Явные признаки ботов
        return lower.contains("bot") ||
                lower.contains("crawler") ||
                lower.contains("spider") ||
                lower.contains("scraper") ||
                lower.contains("http://") ||
                lower.contains("https://") ||
                lower.contains("+http") ||
                (!lower.contains("mozilla") &&
                        !lower.contains("chrome") &&
                        !lower.contains("safari") &&
                        !lower.contains("opera") &&
                        !lower.contains("edge") &&
                        !lower.contains("firefox"));
    }

    private boolean containsAnyIgnoreCase(String source, String... targets) {
        String lowerSource = source.toLowerCase();
        for (String target : targets) {
            if (lowerSource.contains(target.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    public String getOs() {
        return oS;
    }

    public String getBrowser() {
        return browser;
    }
}