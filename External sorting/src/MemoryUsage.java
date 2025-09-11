public class MemoryUsage {
    private static final long LIMIT = 150L * 1024 * 1024; // 150 МБ в байтах

    public static void checkMemory() {
        Runtime runtime = Runtime.getRuntime();
        long used = runtime.totalMemory() - runtime.freeMemory();
        System.out.println("Memory used: " + (used / (1024 * 1024)) + " мб");
        if (used > LIMIT) {
            throw new OutOfMemoryError("Превышен лимит 150 МБ: " + (used / (1024 * 1024)) + " МБ");
        }
    }
}
