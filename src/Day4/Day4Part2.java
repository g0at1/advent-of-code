package Day4;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Day4Part2 {
    public static void main(String[] args) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get("day4.txt"));

        int rows = lines.size();
        int cols = lines.getFirst().length();
        char[][] grid = new char[rows][cols];

        for (int i = 0; i < rows; i++) {
            String row = lines.get(i);
            if (row.length() != cols) {
                throw new IllegalArgumentException("All rows must have the same length");
            }
            grid[i] = row.toCharArray();
        }

        int[] dx = {-1, -1, -1, 0, 0, 1, 1, 1};
        int[] dy = {-1,  0,  1, -1, 1, -1, 0, 1};

        int totalRemoved = 0;

        while (true) {
            boolean[][] toRemove = new boolean[rows][cols];
            int thisRoundRemoved = 0;

            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    if (grid[r][c] != '@') {
                        continue;
                    }

                    int neighbors = 0;
                    for (int k = 0; k < 8; k++) {
                        int nr = r + dx[k];
                        int nc = c + dy[k];

                        if (nr < 0 || nr >= rows || nc < 0 || nc >= cols) {
                            continue;
                        }
                        if (grid[nr][nc] == '@') {
                            neighbors++;
                        }
                    }

                    if (neighbors < 4) {
                        toRemove[r][c] = true;
                        thisRoundRemoved++;
                    }
                }
            }

            if (thisRoundRemoved == 0) {
                break;
            }

            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    if (toRemove[r][c]) {
                        grid[r][c] = '.';
                    }
                }
            }

            totalRemoved += thisRoundRemoved;
        }

        System.out.println("Total rolls removed: " + totalRemoved);
    }
}
