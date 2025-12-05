package Day1;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Day1Part2 {
    public static void main(String[] args) {
        final int MOD = 100;
        int position = 50;
        long zeroCount = 0;

        String filename = "day1.txt";

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;

            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                char direction = line.charAt(0);
                int dist = Integer.parseInt(line.substring(1).trim());

                if (direction == 'R') {
                    zeroCount += countZeroHits(position, dist, true);

                    position = (position + dist) % MOD;
                } else if (direction == 'L') {
                    zeroCount += countZeroHits(position, dist, false);

                    position = (position - dist) % MOD;
                    if (position < 0) {
                        position += MOD;
                    }
                } else {
                    throw new IllegalArgumentException("Invalid rotation: " + line);
                }
            }

            System.out.println("Password: " + zeroCount);

        } catch (IOException e) {
            System.err.println("Error reading " + filename);
            e.printStackTrace();
        }
    }

    private static int countZeroHits(int position, int dist, boolean right) {
        int rem;

        if (right) {
            rem = (100 - position) % 100;
        } else {
            rem = position % 100;
        }

        int first = (rem != 0) ? rem : 100;

        if (first > dist) {
            return 0;
        }

        return 1 + (dist - first) / 100;
    }
}
