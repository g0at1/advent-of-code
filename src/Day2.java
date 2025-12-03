import java.io.*;
import java.util.*;

public class Day2 {
    private static class Range {
        long start;
        long end;

        Range(long start, long end) {
            this.start = start;
            this.end = end;
        }
    }

    public static void main(String[] args) throws IOException {
        String line;
        try (BufferedReader br = new BufferedReader(new FileReader("day2.txt"))) {
            line = br.readLine();
        }

        if (line == null || line.trim().isEmpty()) {
            System.out.println("0");
            return;
        }

        String[] parts = line.split(",");
        List<Range> ranges = new ArrayList<>();
        long maxEnd = Long.MIN_VALUE;

        for (String part : parts) {
            part = part.trim();
            if (part.isEmpty()) {
                continue;
            }

            String[] lr = part.split("-");
            if (lr.length != 2) {
                throw new IllegalArgumentException("Invalid range: " + part);
            }

            long start = Long.parseLong(lr[0]);
            long end = Long.parseLong(lr[1]);
            if (end < start) {
                throw new IllegalArgumentException("Range end < start: " + part);
            }

            ranges.add(new Range(start, end));

            if (end > maxEnd) maxEnd = end;
        }

        ranges.sort(Comparator.comparingLong(r -> r.start));
        long sum = sumInvalidIds(ranges, maxEnd);

        System.out.println(sum);
    }

    private static long sumInvalidIds(List<Range> ranges, long maxEnd) {
        long total = 0L;

        long[] pow10 = new long[19];
        pow10[0] = 1;
        for (int i = 1; i < pow10.length; i++)
            pow10[i] = pow10[i - 1] * 10L;

        int maxDigits = String.valueOf(maxEnd).length();
        int rangeIdx = 0;

        for (int halfLen = 1; halfLen <= maxDigits / 2; halfLen++) {
            long minHalf = pow10[halfLen - 1];
            long maxHalf = pow10[halfLen] - 1;
            long mult = pow10[halfLen];

            for (long base = minHalf; base <= maxHalf; base++) {
                long candidate = base * mult + base;

                if (candidate > maxEnd) {
                    break;
                }

                while (rangeIdx < ranges.size() && ranges.get(rangeIdx).end < candidate) {
                    rangeIdx++;
                }

                if (rangeIdx >= ranges.size()) {
                    return total;
                }

                Range r = ranges.get(rangeIdx);

                if (r.start <= candidate && candidate <= r.end) {
                    total += candidate;
                }
            }
        }

        return total;
    }
}
