class Main{
    public static void main(String[] args){
        MemoryUsage m = new MemoryUsage();
        TestMemory t = new TestMemory();
        m.checkMemory();
        t.func();
        for(int i = 0; i < 10;  i++){
            System.out.println("Hello world" + i);
        }
        m.checkMemory();
        System.out.println("Max heap: " + Runtime.getRuntime().maxMemory() / (1024 * 1024) + " MB");

        System.out.println("Max heap: " + Runtime.getRuntime().maxMemory() / (1024 * 1024) + " MB");
        try {
            byte[] arr = new byte[200 * 1024 * 1024]; // ~200 MB
            System.out.println("Array allocated: " + arr.length);
        } catch (OutOfMemoryError e) {
            System.err.println("OOM: " + e.getMessage());
        }
    }
}