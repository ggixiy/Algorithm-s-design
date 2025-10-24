import java.util.List;
import java.util.ArrayList;
import java.util.Random;

public class AnnealTSP {
    private int[][] edges;
    private int n;
    private int limit;
    private static float k = 0.999999f;
    private float T = 1000f;
    List<Integer> finalRoute;
    private Random r = new Random();

    private long startTime;
    private static final long MAX_TIME_MS = 30 * 60 * 1000;

    private int iterations = 0;
    private int nodes = 0;
    private int worseOptionTaken = 0;
    private  int accepted = 0;

    public AnnealTSP(int[][] edges, int limit){
        this.limit = limit;
        this.edges = edges;
        n = edges.length;
    }

    public void Solve(){
        List<Integer> route = PrimaryRoute();
        startTime = System.currentTimeMillis();
        SimulatedAnnealing(route);

        if(GetValue(finalRoute) <= limit){
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < finalRoute.size() - 1; i++) {
                sb.append(finalRoute.get(i) + 1);
                sb.append(" - (");
                sb.append(edges[finalRoute.get(i)][finalRoute.get(i+1)]);
                sb.append(") -> ");
            }
            sb.append(finalRoute.get(finalRoute.size() - 1) + 1);
            sb.append(" - (");
            sb.append(edges[finalRoute.get(finalRoute.size() - 1)][finalRoute.get(0)]);
            sb.append(") -> ");
            sb.append(finalRoute.get(0) + 1);

            System.out.println(sb.toString());
            System.out.println("Path length: " + GetValue(finalRoute));
            System.out.println("Iterations: " + iterations);
            System.out.println("Nodes: " + nodes);
            System.out.println("Worse option was taken: " + worseOptionTaken);
            System.out.println("Accepted: " + accepted);
        } else {
            System.out.println("Couldn`t find solution within limit");
            System.out.println("Iterations: " + iterations);
            System.out.println("Nodes: " + nodes);
            System.out.println("Worse option was taken: " + worseOptionTaken);
            System.out.println("Accepted: " + accepted);
        }
    }

    private void SimulatedAnnealing(List<Integer> route){
        List<Integer> current = route;
        nodes++;
        accepted++;

        while (T > 1e-3 && GetValue(current) > limit && System.currentTimeMillis() - startTime < MAX_TIME_MS){
            iterations++;

            T = Schedule(T);
            List<Integer> next = GetSuccessor(current);
            nodes++;

            int deltaE = GetValue(current) - GetValue(next);
            if(deltaE > 0 ){
                current = next;
                accepted++;
            } else if(Math.exp(deltaE / T) > r.nextDouble()){ // коли темпераутра низька Math.exp(deltaE / T) майже рівне нулю (ймовірність Больцмана)
                current = next;
                accepted++;
                worseOptionTaken++;
            }

        }

        finalRoute = new ArrayList<>(current);
    }

    private List<Integer> PrimaryRoute(){
        List<Integer> route = new ArrayList<>();
        for(int i = 1; i < edges.length; i++) route.add(i);
        java.util.Collections.shuffle(route, new Random());
        route.add(0, 0);
        return route;
    }

    private List<Integer> GetSuccessor(List<Integer> route){
        List<Integer> newRoute = new ArrayList<>(route);

        int size = newRoute.size();
        if(size <= 2) {
            return newRoute;
        }

        int i = 1 + r.nextInt(size - 1);
        int j = 1 + r.nextInt(size - 1);

        if(i == j) {
            j = (j + 1) % (size - 1) + 1;
        }

        int left = Math.min(i, j);
        int right = Math.max(i, j);

        if (r.nextBoolean()) {
            while (left < right) {
                Swap(newRoute, left, right);
                left++;
                right--;
            }
        } else {
            List<Integer> segment = new ArrayList<>(newRoute.subList(left, right + 1));
            newRoute.subList(left, right + 1).clear();

            int availableSize = newRoute.size();
            int insertPos;

            if (availableSize <= 1) {
                insertPos = 1;
            } else {
                insertPos = 1 + r.nextInt(availableSize);
            }

            newRoute.addAll(insertPos, segment);
        }

        return newRoute;
    }

    private int GetValue(List<Integer> route){
        int value = 0;
        for(int i = 0; i < route.size() - 1; i++){
            value += edges[route.get(i)][route.get(i + 1)];
        }
        value += edges[route.get(route.size() - 1)][route.get(0)];
        return value;
    }

    private static void Swap(List<Integer> list, int i, int j){
        int t = list.get(i);
        list.set(i, list.get(j));
        list.set(j, t);
    }

    private static float Schedule(float t){
        return (k * t);
    }
}
