import java.util.*;

public class BCTR {
    private int[][] edges;
    private int n;
    private static int limit;
    private boolean[] visited;
    private int totalCost = Integer.MAX_VALUE;
    private List<Integer> finalRoute;
    private boolean found = false;

    private int iterations = 0;
    private int deadEnds = 0;
    private int nodes = 0;
    private int nodesInMemory = 0;

    private long startTime;
    private static final long MAX_TIME_MS = 30 * 60 * 1000;

    public BCTR(int[][] edges, int limit) {
        this.edges = edges;
        this.n = edges.length;
        this.visited = new boolean[n];
        this.limit = limit;
    }

    public void Solve(){
        int start = 0;
        visited[start] = true;

        List<Integer> route = new ArrayList<>();
        route.add(start);

        startTime = System.currentTimeMillis();

        Backtracking(start, 0, route);
        if(totalCost == Integer.MAX_VALUE){
            System.out.println("Couldn't find solution within given limit");
            System.out.println("Iterations: " + iterations);
            System.out.println("Nodes: " + nodes);
            System.out.println("Max noodes in memory: " + nodesInMemory);
            System.out.println("Dead ends: " + deadEnds);
        } else{
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
            System.out.println("Path length: " + totalCost);
            System.out.println("Iterations: " + iterations);
            System.out.println("Nodes: " + nodes);
            System.out.println("Max noodes in memory: " + nodesInMemory);
            System.out.println("Dead ends: " + deadEnds);
        }
    }

    private void Backtracking(int current, int cost, List<Integer> route){
        iterations++;

        if (System.currentTimeMillis() - startTime > MAX_TIME_MS) {
            found = true;
            deadEnds++;
            return;
        }

        nodesInMemory = Math.max(nodesInMemory, route.size());
        if (found) return;

        if(AllCitiesVisited()){
            int finalCost = cost + edges[current][route.get(0)];
            if (finalCost < limit) {
                totalCost = finalCost;
                finalRoute = new ArrayList<>(route);
                found = true;
            } else deadEnds++;
            return;
        }

        Integer[] sortedNeighbors = SortNeighbors(current);
        //Integer[] sortedNeighbors = SortNeighbors2(current);

        nodes++;
        for(int i = 0; i < sortedNeighbors.length; i++){
            if(!visited[sortedNeighbors[i]]){

                int newCost = cost + edges[current][sortedNeighbors[i]];

                if(newCost >= limit){
                    deadEnds++;
                    break;
                }

                visited[sortedNeighbors[i]] = true;
                route.add(sortedNeighbors[i]);
                Backtracking(sortedNeighbors[i], newCost, route);
                if(found) break;
                route.remove(route.size() - 1);
                visited[sortedNeighbors[i]] = false;
            }
        }
    }

    private boolean AllCitiesVisited(){
        boolean allVisited = true;
        for(int i = 0; i < visited.length; i++){
            if(!visited[i]){
                allVisited = false;
            }
        }

        return allVisited;
    }

    private Integer[] SortNeighbors(int current){
        Integer[] neighbors = new Integer[n];

        for (int i = 0; i < n; i++) {
            neighbors[i] = i;
        }

        Arrays.sort(neighbors, (a, b) -> Integer.compare(edges[current][a], edges[current][b]));

        return neighbors;
    }

    private Integer[] SortNeighbors2(int current){
        Integer[] neighbors = new Integer[n];
        for (int i = 0; i < n; i++) neighbors[i] = i;

        final double alpha = 0.3; // коефіцієнт впливу "lookahead"

        Arrays.sort(neighbors, (a, b) -> {
            if (a == current) return 1;
            if (b == current) return -1;
            if (visited[a] && !visited[b]) return 1;
            if (visited[b] && !visited[a]) return -1;

            // --- Рахуємо середню відстань до НЕвідвіданих вершин ---
            double sumA = 0, sumB = 0;
            int countA = 0, countB = 0;

            for (int j = 0; j < n; j++) {
                if (j == a || visited[j]) continue;
                sumA += edges[a][j];
                countA++;
            }
            for (int j = 0; j < n; j++) {
                if (j == b || visited[j]) continue;
                sumB += edges[b][j];
                countB++;
            }

            double nextA = (countA > 0) ? sumA / countA : 0;
            double nextB = (countB > 0) ? sumB / countB : 0;

            // --- Оцінюємо сумарний "score" з урахуванням локальної та прогнозованої вартості ---
            double scoreA = edges[current][a] + alpha * nextA;
            double scoreB = edges[current][b] + alpha * nextB;

            return Double.compare(scoreA, scoreB);
        });

        return neighbors;
    }
}
