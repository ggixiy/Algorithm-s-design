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
    }
}