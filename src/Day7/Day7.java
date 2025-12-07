package Day7;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Day7 {
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
            System.err.println("No starting position 'S' found in input.");
            return;
        }

        long splitCount = 0L;

        Set<Integer> beamsIn = new HashSet<>();
        if (startRow + 1 < height) {
            beamsIn.add(startCol);
        }

        for (int r = startRow + 1; r < height && !beamsIn.isEmpty(); r++) {
            String row = lines.get(r);
            Set<Integer> beamsOut = new HashSet<>();

            for (int col : beamsIn) {
                if (col < 0 || col >= width) {
                    continue;
                }

                char cell = row.charAt(col);

                if (cell == '^') {
                    splitCount++;

                    int left = col - 1;
                    int right = col + 1;

                    if (left >= 0) {
                        beamsOut.add(left);
                    }
                    if (right < width) {
                        beamsOut.add(right);
                    }
                } else {
                    beamsOut.add(col);
                }
            }

            beamsIn = beamsOut;
        }

        System.out.println(splitCount);
    }
}
