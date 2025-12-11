package Day11;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Day11 {

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

        Map<String, Long> memo = new HashMap<>();
        long paths = countPaths("you", "out", graph, memo);

        System.out.println(paths);
    }

    private static long countPaths(String current,
                                   String target,
                                   Map<String, List<String>> graph,
                                   Map<String, Long> memo) {

        if (current.equals(target)) {
            return 1L;
        }

        if (memo.containsKey(current)) {
            return memo.get(current);
        }

        List<String> neighbors = graph.get(current);
        if (neighbors == null || neighbors.isEmpty()) {
            memo.put(current, 0L);
            return 0L;
        }

        long total = 0L;
        for (String next : neighbors) {
            total += countPaths(next, target, graph, memo);
        }

        memo.put(current, total);
        return total;
    }
}
