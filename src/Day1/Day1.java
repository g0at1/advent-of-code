package Day1;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Day1 {
    public static void main(String[] args) {
        final int MOD = 100;
        int position = 50;
        int zeroCount = 0;

        String filename = "day1.txt";

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;

            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                char direction = line.charAt(0);
                int dist = Integer.parseInt(line.substring(1).trim());

                if (direction == 'R') {
                    position = (position + dist) % MOD;
                } else if (direction == 'L') {
                    position = (position - dist) % MOD;
                    if (position < 0)  {
                        position += MOD;
                    }
                } else {
                    throw new IllegalArgumentException("Invalid rotation: " + line);
                }

                if (position == 0) {
                    zeroCount++;
                }
            }

            System.out.println("Password: " + zeroCount);

        } catch (IOException e) {
            System.err.println("Error reading " + filename);
            e.printStackTrace();
        }
    }
}