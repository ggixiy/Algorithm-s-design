import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Random;

public class GraphGenerator {
    private static int vertexes = 100;
    public static int[][] edges = new int[vertexes][vertexes];
    private static Random r = new Random();

    public static void Write(File input){
        try (
            PrintWriter w = new PrintWriter(new FileWriter(input));
        ){
            for(int i = 0; i < vertexes; i++){
                w.print(" " + (i + 1));
            }
            w.print("\n");

            Generate(edges);

            for(int i = 0; i < vertexes; i++){
                StringBuilder sb = new StringBuilder();
                w.print((i + 1) + " ");
                for(int j = 0; j < vertexes; j++){
                    sb.append(edges[i][j]);
                    sb.append(" ");
                }
                String row = sb.toString();
                w.println(row);
            }
        } catch (Exception e){
            throw new RuntimeException(e);
        };
    }

    private static void Generate(int[][] edges){
        for(int i = 0; i < edges.length; i++){
            for(int j = 0; j < edges.length; j++){
                if(edges[i][j] == 0) {
                    if (i == j) continue;
                    edges[i][j] = edges [j][i] = r.nextInt(21) + 10;
                }
            }
        }
    }
}
