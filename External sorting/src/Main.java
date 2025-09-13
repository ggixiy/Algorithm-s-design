import java.io.FileOutputStream;

class Main{
    public static void main(String[] args){
        /*MemoryUsage m = new MemoryUsage();
        TestMemory t = new TestMemory();
        m.checkMemory();
        System.out.println("Max heap: " + Runtime.getRuntime().maxMemory() / (1024 * 1024) + " MB");

        FileGenerator generator = new FileGenerator();
        generator.GenerateFile();
        m.checkMemory();*/

        try (FileOutputStream file = new
                FileOutputStream("E:\\projects\\AD\\External sorting\\B.txt");
        FileOutputStream file2 = new
                FileOutputStream("E:\\projects\\AD\\External sorting\\C.txt");)
        {
            FileGenerator generator = new FileGenerator();
            ExternalMergeSort sort = new ExternalMergeSort();

        } catch (Exception e) {
            throw new RuntimeException();
        }



    }
}