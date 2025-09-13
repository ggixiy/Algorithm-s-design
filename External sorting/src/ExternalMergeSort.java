import java.io.File;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.util.Scanner;

public class ExternalMergeSort{
    private static int seriesSize = 1;

    public static void Distribution(File a, File b, File c){
        boolean inFirstSeries = true;
        String line;
        int n = 0;

        try (
                Scanner scanner = new Scanner(a);
                PrintWriter writerB = new PrintWriter(new FileWriter(b));
                PrintWriter writerC = new PrintWriter(new FileWriter(c))
        ) {
            while (scanner.hasNextLine()) {
                line = scanner.nextLine();

                if(n == seriesSize){
                    inFirstSeries = false;
                    n = 0;
                }

                if (inFirstSeries) {
                    writerB.println(line);
                }else writerC.println(line);
                n++;
            }
        } catch (Exception e){
            throw new RuntimeException();
        }
    }
}