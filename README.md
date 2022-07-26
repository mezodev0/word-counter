# word-counter
Open the Main.java file and replace the string called "TOKEN" with your own bot token

```java
public class Main {
    public static void main(String[] args) throws LoginException {
        JDABuilder builder = JDABuilder.createDefault("TOKEN");

        // Disable parts of the cache
        builder.disableCache(CacheFlag.MEMBER_OVERRIDES, CacheFlag.VOICE_STATE);
        // Enable the bulk delete event
        builder.setBulkDeleteSplittingEnabled(false);
        // Disable compression (not recommended)
        builder.setCompression(Compression.NONE);
        // Set activity (like "playing Something")
        builder.setActivity(Activity.watching("messages"));

        builder.addEventListeners(new Count());
        builder.build();
    }
}
```
