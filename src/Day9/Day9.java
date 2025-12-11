package Day9;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Day9 {
    private static class Point {
        final long x;
        final long y;

        Point(long x, long y) {
            this.x = x;
            this.y = y;
        }
    }

    public static void main(String[] args) {
        String fileName = "day9.txt";

        List<Point> points = new ArrayList<>();

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
                        points.add(new Point(x, y));
                    });
        } catch (IOException e) {
            System.err.println("Error reading file " + fileName + ": " + e.getMessage());
            return;
        }

        long maxArea = findMaxRectangleArea(points);
        System.out.println(maxArea);
    }

    private static long findMaxRectangleArea(List<Point> points) {
        int n = points.size();
        long maxArea = 0L;

        for (int i = 0; i < n; i++) {
            Point a = points.get(i);
            for (int j = i + 1; j < n; j++) {
                Point b = points.get(j);

                long dx = Math.abs(a.x - b.x);
                long dy = Math.abs(a.y - b.y);

                if (dx == 0 || dy == 0) {
                    continue;
                }

                long area = (dx + 1L) * (dy + 1L);
                if (area > maxArea) {
                    maxArea = area;
                }
            }
        }

        return maxArea;
    }
}
