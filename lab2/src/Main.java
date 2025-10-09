import java.io.File;

public class Main {
    public static void main(String[] args) {
        File input = new File("E:\\FICE\\3_sem\\AD\\lab2\\input.txt");
        GraphGenerator g = new GraphGenerator();

        g.Write(input);

        /*System.out.println("Started Anneal:");
        AnnealTSP solver1 = new AnnealTSP(g.edges, 1050);
        solver1.Solve();*/

        System.out.println("Started BCTR:");
        BCTR solver2 = new BCTR(g.edges, 1050);
        solver2.Solve();

    }
}