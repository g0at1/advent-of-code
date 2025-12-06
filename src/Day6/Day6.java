package Day6;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Day6 {
    public static void main(String[] args) {
        String filename = "day6.txt";

        try {
            List<String> lines = Files.readAllLines(Path.of(filename));
            if (lines.isEmpty()) {
                System.out.println("0");
                return;
            }

            int width = 0;
            for (String line : lines) {
                width = Math.max(width, line.length());
            }

            List<String> grid = new ArrayList<>();
            for (String line : lines) {
                if (line.length() < width) {
                    StringBuilder sb = new StringBuilder(line);
                    while (sb.length() < width) {
                        sb.append(' ');
                    }
                    grid.add(sb.toString());
                } else {
                    grid.add(line);
                }
            }

            int height = grid.size();

            boolean[] isSeparator = new boolean[width];
            for (int col = 0; col < width; col++) {
                boolean allSpaces = true;
                for (String s : grid) {
                    if (s.charAt(col) != ' ') {
                        allSpaces = false;
                        break;
                    }
                }
                isSeparator[col] = allSpaces;
            }

            List<int[]> problemRanges = new ArrayList<>();
            int col = 0;
            while (col < width) {
                while (col < width && isSeparator[col]) {
                    col++;
                }
                if (col >= width) break;
                int start = col;
                while (col < width && !isSeparator[col]) {
                    col++;
                }
                int end = col - 1;
                problemRanges.add(new int[]{start, end});
            }

            long grandTotal = getGrandTotal(problemRanges, height, grid);

            System.out.println(grandTotal);

        } catch (IOException e) {
            System.err.println("Failed to read file '" + filename + "': " + e.getMessage());
        }
    }

    private static long getGrandTotal(List<int[]> problemRanges, int height, List<String> grid) {
        long grandTotal = 0L;

        for (int[] range : problemRanges) {
            int startCol = range[0];
            int endCol = range[1];

            List<Long> operands = new ArrayList<>();
            Character op = null;

            for (int row = 0; row < height; row++) {
                String segment = grid.get(row).substring(startCol, endCol + 1);
                String trimmed = segment.trim();

                if (trimmed.isEmpty()) {
                    continue;
                }

                int plusIndex = segment.indexOf('+');
                int mulIndex = segment.indexOf('*');

                if (plusIndex != -1 || mulIndex != -1) {
                    op = (plusIndex != -1) ? '+' : '*';
                    break;
                } else {
                    if (!trimmed.matches("\\d+")) {
                        throw new IllegalStateException("Invalid number line: '" + segment + "'");
                    }
                    operands.add(Long.parseLong(trimmed));
                }
            }

            if (op == null) {
                throw new IllegalStateException("No operator found for problem in columns " + startCol + "-" + endCol);
            }
            if (operands.isEmpty()) {
                throw new IllegalStateException("No operands found for problem in columns " + startCol + "-" + endCol);
            }

            long result;
            if (op == '+') {
                long sum = 0L;
                for (long v : operands) {
                    sum += v;
                }
                result = sum;
            } else {
                long product = 1L;
                for (long v : operands) {
                    product *= v;
                }
                result = product;
            }

            grandTotal += result;
        }
        return grandTotal;
    }
}
