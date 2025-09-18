import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

public class FileContentGenerator {
    private static Random r = new Random();
    public static int recordsAmount = 32929825;

    public static void GenerateFile(File a){
        try (FileOutputStream file = new
                FileOutputStream(a);)
        {
            for(int i = 0; i < recordsAmount; ++i)
            {
                byte[] str = GenerateRecord();
                file.write(str, 0, str.length);
            }
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    public static byte[] GenerateRecord(){
        int key = r.nextInt(100000);
        String text = GenerateString(5 + r.nextInt(16));
        DateFormat d = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.set(1900 + r.nextInt(126), r.nextInt(12), 1 + r.nextInt(28));
        String date = d.format(c.getTime());
        String record = key + " " + text + " " + date + "\n";
        byte[] recordArr = record.getBytes();
        return recordArr;
    }

    public static String GenerateString(int length){
        String letters = "abcdefghijklmnopqrstuvwxyz";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(letters.charAt(r.nextInt(letters.length())));
        }
        return sb.toString();
    }
}
