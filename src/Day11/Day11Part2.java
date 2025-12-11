package Day11;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Day11Part2 {

    public static void main(String[] args) throws IOException {
        String fileName = "day11.txt";
        if (args.length > 0) {
            fileName = args[0];
        }

        List<String> lines = Files.readAllLines(Paths.get(fileName));
        Map<String, List<String>> graph = new HashMap<>();

        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) {
                continue;
            }

            String[] parts = line.split(":", 2);
            if (parts.length != 2) {
                throw new IllegalArgumentException("Invalid line: " + line);
            }

            String from = parts[0].trim();
            String right = parts[1].trim();

            List<String> neighbors = new ArrayList<>();
            if (!right.isEmpty()) {
                String[] tokens = right.split("\\s+");
                for (String token : tokens) {
                    if (!token.isEmpty()) {
                        neighbors.add(token);
                    }
                }
            }

            graph.put(from, neighbors);
        }

        Map<String, long[][]> memo = new HashMap<>();
        long result = dfs("svr", false, false, graph, memo);

        System.out.println(result);
    }

    private static long dfs(String current,
                            boolean hasDac,
                            boolean hasFft,
                            Map<String, List<String>> graph,
                            Map<String, long[][]> memo) {

        if ("dac".equals(current)) {
            hasDac = true;
        }
        if ("fft".equals(current)) {
            hasFft = true;
        }

        if ("out".equals(current)) {
            return (hasDac && hasFft) ? 1L : 0L;
        }

        int iDac = hasDac ? 1 : 0;
        int iFft = hasFft ? 1 : 0;

        long[][] nodeMemo = memo.get(current);
        if (nodeMemo == null) {
            nodeMemo = new long[][]{
                    { -1L, -1L },
                    { -1L, -1L }
            };
            memo.put(current, nodeMemo);
        } else if (nodeMemo[iDac][iFft] != -1L) {
            return nodeMemo[iDac][iFft];
        }

        List<String> neighbors = graph.get(current);
        if (neighbors == null || neighbors.isEmpty()) {
            nodeMemo[iDac][iFft] = 0L;
            return 0L;
        }

        long total = 0L;
        for (String next : neighbors) {
            total += dfs(next, hasDac, hasFft, graph, memo);
        }

        nodeMemo[iDac][iFft] = total;
        return total;
    }
}
