package Day7;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class Day7Part2 {
    public static void main(String[] args) throws IOException {
        Path path = Paths.get("day7.txt");
        List<String> lines = Files.readAllLines(path);

        lines.removeIf(String::isBlank);

        if (lines.isEmpty()) {
            System.err.println("Input is empty.");
            return;
        }

        int height = lines.size();
        int width = lines.getFirst().length();

        int startRow = -1;
        int startCol = -1;

        for (int r = 0; r < height; r++) {
            String row = lines.get(r);
            for (int c = 0; c < row.length(); c++) {
                if (row.charAt(c) == 'S') {
                    startRow = r;
                    startCol = c;
                    break;
                }
            }
        }

        if (startRow == -1) {
            System.err.println("No 'S' found in input.");
            return;
        }

        BigInteger splitCount = BigInteger.ZERO;

        BigInteger[] beamsIn = new BigInteger[width];
        for (int i = 0; i < width; i++) {
            beamsIn[i] = BigInteger.ZERO;
        }

        if (startRow + 1 < height) {
            beamsIn[startCol] = BigInteger.ONE;
        }

        for (int r = startRow + 1; r < height; r++) {
            String row = lines.get(r);
            BigInteger[] beamsOut = new BigInteger[width];
            Arrays.fill(beamsOut, BigInteger.ZERO);

            for (int c = 0; c < width; c++) {
                BigInteger count = beamsIn[c];
                if (count == null || count.signum() == 0) {
                    continue;
                }

                char cell = row.charAt(c);

                if (cell == '^') {
                    splitCount = splitCount.add(count);

                    int left = c - 1;
                    int right = c + 1;

                    if (left >= 0) {
                        beamsOut[left] = beamsOut[left].add(count);
                    }
                    if (right < width) {
                        beamsOut[right] = beamsOut[right].add(count);
                    }
                } else {
                    beamsOut[c] = beamsOut[c].add(count);
                }
            }

            beamsIn = beamsOut;
        }

        BigInteger totalTimelines = BigInteger.ONE.add(splitCount);
        System.out.println(totalTimelines);
    }
}
