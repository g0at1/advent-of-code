package Day2;

import java.io.*;
import java.util.*;

public class Day2Part2 {
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

        Set<Long> seen = new HashSet<>();

        for (int segmentLen = 1; segmentLen <= maxDigits / 2; segmentLen++) {
            long mult = pow10[segmentLen];
            long minBase = pow10[segmentLen - 1];
            long maxBase = mult - 1;

            int maxRepeats = maxDigits / segmentLen;

            for (long base = minBase; base <= maxBase; base++) {
                long candidate = base;

                for (int rep = 2; rep <= maxRepeats; rep++) {
                    candidate = candidate * mult + base;

                    if (candidate > maxEnd) {
                        break;
                    }

                    if (seen.add(candidate)) {
                        if (isInRanges(candidate, ranges)) {
                            total += candidate;
                        }
                    }
                }
            }
        }

        return total;
    }

    private static boolean isInRanges(long value, List<Range> ranges) {
        int lo = 0;
        int hi = ranges.size() - 1;

        while (lo <= hi) {
            int mid = (lo + hi) >>> 1;
            Range r = ranges.get(mid);

            if (value < r.start) {
                hi = mid - 1;
            } else if (value > r.end) {
                lo = mid + 1;
            } else {
                return true;
            }
        }
        return false;
    }
}
