import java.io.File;
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

        File b = new File("E:\\projects\\AD\\External sorting\\B.txt");
        File c = new File("E:\\projects\\AD\\External sorting\\C.txt");

        try (FileOutputStream file1 = new
                FileOutputStream(b);
        FileOutputStream file2 = new
                FileOutputStream(c);)
        {
            FileGenerator generator = new FileGenerator();
            ExternalMergeSort sort = new ExternalMergeSort();
            generator.GenerateFile();
            sort.Distribution(generator.a, b, c);

        } catch (Exception e) {
            throw new RuntimeException();
        }



    }
}