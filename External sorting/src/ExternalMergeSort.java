import java.io.*;
import java.util.Scanner;

public class ExternalMergeSort{
    private static int seriesSize = 1;

    public static void ExternalSort(File a, File b, File c, int NumOfRecs){
        while(true){
            Distribute(a, b, c);
            boolean sorted = Merge(a, b, c, NumOfRecs);
            if(sorted) break;
            seriesSize *= 2;
        }
    }

    public static void Distribute(File a, File b, File c){
        boolean inFirstSeries = true;
        String line;
        int n = 0;
        int seriesCount = 0;

        try (
                BufferedReader reader = new BufferedReader(new FileReader(a));
                PrintWriter writerB = new PrintWriter(new FileWriter(b));
                PrintWriter writerC = new PrintWriter(new FileWriter(c))
        ) {
            while ((line = reader.readLine()) != null) {
                if(n == seriesSize){
                    inFirstSeries = !inFirstSeries;
                    n = 0;
                    seriesCount++;
                }

                if (inFirstSeries) {
                    writerB.println(line);
                }else writerC.println(line);
                n++;
            }

            if(n > 0) {
                seriesCount++;
            }
        } catch (Exception e){
            throw new RuntimeException();
        }
    }

    public static boolean Merge(File a, File b, File c, int NumOfRecs){
        int seriesCount = 0;
        try (
                PrintWriter writer = new PrintWriter(new FileWriter(a));
                BufferedReader readerB = new BufferedReader(new FileReader(b));
                BufferedReader readerC = new BufferedReader(new FileReader(c));
        )  {
            String lB = readerB.readLine();
            String lC = readerC.readLine();

            while(lB != null || lC != null) {
                int seriesSizeB = 0, seriesSizeC = 0;
                seriesCount++;

                while((seriesSizeB < seriesSize && lB != null) || (seriesSizeC < seriesSize && lC != null)){
                    if(seriesSizeB >= seriesSize || lB == null){
                        writer.println(lC);
                        lC = readerC.readLine();
                        seriesSizeC++;
                    } else if(seriesSizeC >= seriesSize || lC == null){
                        writer.println(lB);
                        lB = readerB.readLine();
                        seriesSizeB++;
                    } else {
                        int keyB = Integer.parseInt(lB.trim().split("\\s+")[0]);
                        int keyC = Integer.parseInt(lC.trim().split("\\s+")[0]);

                        if(keyB >= keyC){
                            writer.println(lB);
                            lB = readerB.readLine();
                            seriesSizeB++;
                        } else {
                            writer.println(lC);
                            lC = readerC.readLine();
                            seriesSizeC++;
                        }
                    }
                }
            }
            return (seriesCount == 1);
        } catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException();
        }
    }
}