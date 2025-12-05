package Day5;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Day5Part2 {
    private static class Range {
        long start;
        long end;

        Range(long start, long end) {
            this.start = start;
            this.end = end;
        }
    }

    public static void main(String[] args) {
        String fileName = "day5.txt";

        List<String> lines;
        try {
            lines = Files.readAllLines(Paths.get(fileName));
        } catch (IOException e) {
            System.err.println("Error reading file " + fileName + ": " + e.getMessage());
            return;
        }

        List<Range> freshRanges = new ArrayList<>();

        for (String rawLine : lines) {
            String line = rawLine.trim();
            if (line.isEmpty()) {
                break;
            }

            String[] parts = line.split("-");
            if (parts.length != 2) {
                throw new IllegalArgumentException("Invalid range line: " + line);
            }

            long start = Long.parseLong(parts[0].trim());
            long end = Long.parseLong(parts[1].trim());

            if (end < start) {
                throw new IllegalArgumentException("Range end < start in line: " + line);
            }

            freshRanges.add(new Range(start, end));
        }

        if (freshRanges.isEmpty()) {
            System.out.println("Total fresh IDs: 0");
            return;
        }

        freshRanges.sort(Comparator
                .comparingLong((Range r) -> r.start)
                .thenComparingLong(r -> r.end));

        long totalFreshIds = getTotalFreshIds(freshRanges);

        System.out.println("Total number of ingredient IDs considered fresh: " + totalFreshIds);
    }

    private static long getTotalFreshIds(List<Range> freshRanges) {
        long totalFreshIds = 0;

        long currentStart = freshRanges.getFirst().start;
        long currentEnd = freshRanges.getFirst().end;

        for (int i = 1; i < freshRanges.size(); i++) {
            Range r = freshRanges.get(i);

            if (r.start <= currentEnd + 1) {
                if (r.end > currentEnd) {
                    currentEnd = r.end;
                }
            } else {
                totalFreshIds += (currentEnd - currentStart + 1);
                currentStart = r.start;
                currentEnd = r.end;
            }
        }

        totalFreshIds += (currentEnd - currentStart + 1);
        return totalFreshIds;
    }
}
