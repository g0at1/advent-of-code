package Day5;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Day5 {
    private static class Range {
        long start;
        long end;

        Range(long start, long end) {
            this.start = start;
            this.end = end;
        }

        boolean contains(long value) {
            return value >= start && value <= end;
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
        List<Long> ingredientIds = new ArrayList<>();

        boolean readingRanges = true;

        for (String rawLine : lines) {
            String line = rawLine.trim();

            if (line.isEmpty()) {
                readingRanges = false;
                continue;
            }

            if (readingRanges) {
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
            } else {
                long id = Long.parseLong(line);
                ingredientIds.add(id);
            }
        }

        int freshCount = 0;

        for (long id : ingredientIds) {
            if (isFresh(id, freshRanges)) {
                freshCount++;
            }
        }

        System.out.println("Number of fresh ingredient IDs: " + freshCount);
    }

    private static boolean isFresh(long id, List<Range> ranges) {
        for (Range r : ranges) {
            if (r.contains(id)) {
                return true;
            }
        }
        return false;
    }
}
