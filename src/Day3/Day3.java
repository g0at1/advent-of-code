package Day3;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Day3 {
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

            total += maxTwoDigitFromLine(line);
        }

        br.close();
        System.out.println(total);
    }

    private static int maxTwoDigitFromLine(String s) {
        int n = s.length();
        int maxVal = -1;

        for (int i = 0; i < n - 1; i++) {
            int d1 = s.charAt(i) - '0';

            for (int j = i + 1; j < n; j++) {
                int d2 = s.charAt(j) - '0';

                int val = d1 * 10 + d2;
                if (val > maxVal) {
                    maxVal = val;
                }
            }
        }

        return maxVal;
    }
}
