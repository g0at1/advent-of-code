package Day9;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Day9Part2 {
    private static class Point {
        final long x;
        final long y;

        Point(long x, long y) {
            this.x = x;
            this.y = y;
        }
    }

    private static class RectCandidate {
        final Point a;
        final Point b;
        final long area;

        RectCandidate(Point a, Point b) {
            this.a = a;
            this.b = b;
            this.area = computeArea(a, b);
        }
    }

    public static void main(String[] args) {
        String fileName = "day9.txt";

        List<Point> redPoints = new ArrayList<>();

        try {
            Files.lines(Paths.get(fileName))
                    .map(String::trim)
                    .filter(line -> !line.isEmpty())
                    .forEach(line -> {
                        String[] parts = line.split(",");
                        if (parts.length != 2) {
                            throw new IllegalArgumentException("Invalid line: " + line);
                        }
                        long x = Long.parseLong(parts[0].trim());
                        long y = Long.parseLong(parts[1].trim());
                        redPoints.add(new Point(x, y));
                    });
        } catch (IOException e) {
            System.err.println("Error reading file " + fileName + ": " + e.getMessage());
            return;
        }

        if (redPoints.size() < 2) {
            System.out.println(0);
            return;
        }

        List<Point[]> edges = buildEdges(redPoints);

        List<RectCandidate> candidates = buildCandidates(redPoints);

        candidates.sort(Comparator.comparingLong((RectCandidate rc) -> rc.area).reversed());

        long answer = findLargestValidRectangle(candidates, edges);
        System.out.println(answer);
    }

    private static List<Point[]> buildEdges(List<Point> points) {
        int n = points.size();
        List<Point[]> edges = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            Point a = points.get(i);
            Point b = points.get((i + 1) % n);
            edges.add(new Point[]{a, b});
        }
        return edges;
    }

    private static List<RectCandidate> buildCandidates(List<Point> points) {
        int n = points.size();
        List<RectCandidate> candidates = new ArrayList<>(n * (n - 1) / 2);
        for (int i = 0; i < n; i++) {
            Point a = points.get(i);
            for (int j = i + 1; j < n; j++) {
                Point b = points.get(j);
                candidates.add(new RectCandidate(a, b));
            }
        }
        return candidates;
    }

    private static long computeArea(Point a, Point b) {
        long dx = Math.abs(a.x - b.x);
        long dy = Math.abs(a.y - b.y);
        return (dx + 1L) * (dy + 1L);
    }

    private static long findLargestValidRectangle(List<RectCandidate> candidates, List<Point[]> edges) {
        long maxArea = 0;

        for (RectCandidate cand : candidates) {
            long area = cand.area;

            if (area <= maxArea) {
                break;
            }

            if (rectangleValid(cand.a, cand.b, edges)) {
                maxArea = area;
            }
        }

        return maxArea;
    }

    private static boolean rectangleValid(Point a, Point b, List<Point[]> edges) {
        for (Point[] edge : edges) {
            if (edgeCrossesRectangle(edge[0], edge[1], a, b)) {
                return false;
            }
        }
        return true;
    }

    private static boolean edgeCrossesRectangle(Point e1, Point e2, Point r1, Point r2) {
        long ex1 = e1.x, ey1 = e1.y;
        long ex2 = e2.x, ey2 = e2.y;
        long rx1 = r1.x, ry1 = r1.y;
        long rx2 = r2.x, ry2 = r2.y;

        if (ex1 == ex2) {
            long x = ex1;

            long eyMin = Math.min(ey1, ey2);
            long eyMax = Math.max(ey1, ey2);
            long ryMin = Math.min(ry1, ry2);
            long ryMax = Math.max(ry1, ry2);

            long rxMin = Math.min(rx1, rx2);
            long rxMax = Math.max(rx1, rx2);

            boolean between = (x > rxMin) && (x < rxMax);

            long overlapMin = Math.max(eyMin, ryMin);
            long overlapMax = Math.min(eyMax, ryMax);
            boolean overlap = overlapMin < overlapMax;

            return between && overlap;
        } else if (ey1 == ey2) {
            long y = ey1;

            long exMin = Math.min(ex1, ex2);
            long exMax = Math.max(ex1, ex2);
            long rxMin = Math.min(rx1, rx2);
            long rxMax = Math.max(rx1, rx2);

            long ryMin = Math.min(ry1, ry2);
            long ryMax = Math.max(ry1, ry2);

            boolean between = (y > ryMin) && (y < ryMax);

            long overlapMin = Math.max(exMin, rxMin);
            long overlapMax = Math.min(exMax, rxMax);
            boolean overlap = overlapMin < overlapMax;

            return between && overlap;
        } else {
            throw new IllegalStateException("Non axis-aligned edge: (" +
                    ex1 + "," + ey1 + ") -> (" + ex2 + "," + ey2 + ")");
        }
    }
}
