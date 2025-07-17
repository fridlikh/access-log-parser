public class UserAgent {
    private final String oS;
    private final String browser;

    public UserAgent(String getUserAgent){
        // Временные переменные для хранения промежуточных результатов парсинга
        String parsedOs;
        String parsedBrowser;

        try {
            String[] UAparts = getUserAgent.split(" ", 10);
            parsedBrowser = UAparts[0];

            int osStart = getUserAgent.indexOf("(") + 1; // +1 чтобы пропустить саму скобку
            int osEnd = getUserAgent.indexOf(")");
            parsedOs = getUserAgent.substring(osStart, osEnd);

            this.browser = parsedBrowser;
            this.oS = parsedOs;
        }
        catch (Exception e) {
            throw new IllegalArgumentException("Failed to parse UserAgent: " + getUserAgent, e);
        }
    }
    ///////////ГЕТТЕРЫ////////////////////////////
    public String getOs() {return oS;}
    public String getBrowser() {return browser;}
    ////////////////ГЕТТЕРЫ////////////////////////;
}
