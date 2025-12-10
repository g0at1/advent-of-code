package Day8;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Day8Part2 {
    private static class Point {
        long x, y, z;

        Point(long x, long y, long z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }

    private static class Edge {
        int a, b;
        long dist2;

        Edge(int a, int b, long dist2) {
            this.a = a;
            this.b = b;
            this.dist2 = dist2;
        }
    }

    private static class UnionFind {
        int[] parent;
        int[] rank;

        UnionFind(int n) {
            parent = new int[n];
            rank = new int[n];
            for (int i = 0; i < n; i++) {
                parent[i] = i;
                rank[i] = 0;
            }
        }

        int find(int x) {
            if (parent[x] != x) {
                parent[x] = find(parent[x]);
            }
            return parent[x];
        }

        boolean union(int a, int b) {
            int ra = find(a);
            int rb = find(b);
            if (ra == rb) return false;

            if (rank[ra] < rank[rb]) {
                parent[ra] = rb;
            } else if (rank[ra] > rank[rb]) {
                parent[rb] = ra;
            } else {
                parent[rb] = ra;
                rank[ra]++;
            }
            return true;
        }
    }

    public static void main(String[] args) throws IOException {
        List<Point> points = readPoints();
        int n = points.size();
        if (n == 0) {
            System.out.println("No points in input.");
            return;
        }

        List<Edge> edges = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            Point pi = points.get(i);
            for (int j = i + 1; j < n; j++) {
                Point pj = points.get(j);
                long d2 = dist2(pi, pj);
                edges.add(new Edge(i, j, d2));
            }
        }

        edges.sort(Comparator.comparingLong(e -> e.dist2));

        UnionFind uf = new UnionFind(n);
        int components = n;
        Long answer = null;

        for (Edge e : edges) {
            if (uf.union(e.a, e.b)) {
                components--;
                if (components == 1) {
                    long x1 = points.get(e.a).x;
                    long x2 = points.get(e.b).x;
                    answer = x1 * x2;
                    System.out.println("Last connection between points with X = "
                            + x1 + " and " + x2);
                    System.out.println("Product: " + answer);
                    break;
                }
            }
        }

        if (answer == null) {
            System.out.println("Graph never became a single circuit (unexpected).");
        }
    }

    private static List<Point> readPoints() throws IOException {
        List<Point> points = new ArrayList<>();
        try (BufferedReader br = Files.newBufferedReader(Path.of("day8.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split(",");
                if (parts.length != 3) {
                    throw new IllegalArgumentException("Invalid line (need 3 comma-separated values): " + line);
                }
                long x = Long.parseLong(parts[0].trim());
                long y = Long.parseLong(parts[1].trim());
                long z = Long.parseLong(parts[2].trim());
                points.add(new Point(x, y, z));
            }
        }
        return points;
    }

    private static long dist2(Point a, Point b) {
        long dx = a.x - b.x;
        long dy = a.y - b.y;
        long dz = a.z - b.z;
        return dx * dx + dy * dy + dz * dz;
    }
}
