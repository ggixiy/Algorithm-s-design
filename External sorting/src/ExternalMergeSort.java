import java.io.File;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.util.Scanner;

public class ExternalMergeSort{
    private static int seriesSize = 1;

    public static void ExternalSort(File a, File b, File c, int NumOfRecs){
        while(true){
            Distribution(a, b, c);
            boolean sorted = Merge(a, b, c, NumOfRecs);
            if(sorted) break;
            seriesSize *= 2;
        }
    }

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
                    inFirstSeries = !inFirstSeries;
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

    public static boolean Merge(File a, File b, File c, int NumOfRecs){
        try (
                PrintWriter writer = new PrintWriter(new FileWriter(a));
                Scanner scannerB = new Scanner(b);
                Scanner scannerC = new Scanner(c);
        )  {
            String lB = scannerB.hasNextLine() ? scannerB.nextLine() : null;
            String lC = scannerC.hasNextLine() ? scannerC.nextLine() : null;

            while(lB != null || lC != null) {
                int seriesSizeB = 0, seriesSizeC = 0;

                while((seriesSizeB < seriesSize && lB != null) || (seriesSizeC < seriesSize && lC != null)){
                    if(seriesSizeB >= seriesSize || lB == null){
                        writer.println(lC);
                        lC = scannerC.hasNextLine() ? scannerC.nextLine() : null;
                        seriesSizeC++;
                    } else if(seriesSizeC >= seriesSize || lC == null){
                        writer.println(lB);
                        lB = scannerB.hasNextLine() ? scannerB.nextLine() : null;
                        seriesSizeB++;
                    } else {
                        int keyB = Integer.parseInt(lB.trim().split("\\s+")[0]);
                        int keyC = Integer.parseInt(lC.trim().split("\\s+")[0]);

                        if(keyB >= keyC){
                            writer.println(lB);
                            lB = scannerB.hasNextLine() ? scannerB.nextLine() : null;
                            seriesSizeB++;
                        } else {
                            writer.println(lC);
                            lC = scannerC.hasNextLine() ? scannerC.nextLine() : null;
                            seriesSizeC++;
                        }
                    }
                }
            }
            return (seriesSize >= NumOfRecs);
        } catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException();
        }
    }
}