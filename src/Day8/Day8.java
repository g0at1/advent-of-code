package Day8;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Day8 {
    private static final int CONNECTIONS = 1000;

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

        void union(int a, int b) {
            int ra = find(a);
            int rb = find(b);
            if (ra == rb) return;

            if (rank[ra] < rank[rb]) {
                parent[ra] = rb;
            } else if (rank[ra] > rank[rb]) {
                parent[rb] = ra;
            } else {
                parent[rb] = ra;
                rank[ra]++;
            }
        }
    }

    public static void main(String[] args) throws IOException {
        List<Point> points = readPoints();
        int n = points.size();
        if (n == 0) {
            System.out.println("No points in input.");
            return;
        }

        PriorityQueue<Edge> pq = new PriorityQueue<>(
                (e1, e2) -> Long.compare(e2.dist2, e1.dist2)
        );

        for (int i = 0; i < n; i++) {
            Point pi = points.get(i);
            for (int j = i + 1; j < n; j++) {
                Point pj = points.get(j);
                long d2 = dist2(pi, pj);

                if (pq.size() < CONNECTIONS) {
                    pq.add(new Edge(i, j, d2));
                } else if (d2 < pq.peek().dist2) {
                    pq.poll();
                    pq.add(new Edge(i, j, d2));
                }
            }
        }

        List<Edge> edges = new ArrayList<>(pq);
        edges.sort(Comparator.comparingLong(e -> e.dist2));

        UnionFind uf = new UnionFind(n);
        for (Edge e : edges) {
            uf.union(e.a, e.b);
        }

        Map<Integer, Integer> compSizes = new HashMap<>();
        for (int i = 0; i < n; i++) {
            int root = uf.find(i);
            compSizes.merge(root, 1, Integer::sum);
        }

        List<Integer> sizes = new ArrayList<>(compSizes.values());
        sizes.sort(Comparator.reverseOrder());

        if (sizes.size() < 3) {
            System.out.println("Less than 3 circuits; sizes: " + sizes);
            return;
        }

        long product = 1L;
        product *= sizes.get(0);
        product *= sizes.get(1);
        product *= sizes.get(2);

        System.out.println("Sizes of three largest circuits: " +
                sizes.get(0) + ", " + sizes.get(1) + ", " + sizes.get(2));
        System.out.println("Product: " + product);
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
