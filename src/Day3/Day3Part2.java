package Day3;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Day3Part2 {
    private static final int K = 12;

    public static void main(String[] args) throws IOException {
        String filename = "day3.txt";
        BufferedReader br = new BufferedReader(new FileReader(filename));

        String line;
        long total = 0L;

        while ((line = br.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty()) {
                continue;
            }

            long best = maxKDigitsFromLine(line, K);
            total += best;
        }

        br.close();
        System.out.println(total);
    }

    private static long maxKDigitsFromLine(String s, int k) {
        int n = s.length();
        if (n < k) {
            throw new IllegalArgumentException("Line too short to pick " + k + " digits: " + s);
        }

        StringBuilder sb = new StringBuilder(k);
        int startIndex = 0;

        for (int pos = 0; pos < k; pos++) {
            int remainingToPick = k - pos;
            int searchEnd = n - remainingToPick;

            char bestDigit = '0' - 1;
            int bestIdx = -1;

            for (int i = startIndex; i <= searchEnd; i++) {
                char c = s.charAt(i);
                if (c > bestDigit) {
                    bestDigit = c;
                    bestIdx = i;
                    if (bestDigit == '9') {
                        break;
                    }
                }
            }

            if (bestIdx == -1) {
                throw new IllegalStateException("Failed to choose digit at position " + pos + " for line: " + s);
            }

            sb.append(bestDigit);
            startIndex = bestIdx + 1;
        }

        return Long.parseLong(sb.toString());
    }
}
