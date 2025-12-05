package Day4;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Day4 {
    public static void main(String[] args) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get("day4.txt"));

        int rows = lines.size();
        int cols = lines.getFirst().length();
        char[][] grid = new char[rows][cols];

        for (int i = 0; i < rows; i++) {
            grid[i] = lines.get(i).toCharArray();
        }

        int accessibleCount = 0;

        int[] dx = {-1, -1, -1, 0, 0, 1, 1, 1};
        int[] dy = {-1, 0,  1, -1, 1, -1, 0, 1};

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {

                if (grid[r][c] != '@') {
                    continue;
                }

                int neighborRolls = 0;

                for (int k = 0; k < 8; k++) {
                    int nr = r + dx[k];
                    int nc = c + dy[k];

                    if (nr < 0 || nr >= rows || nc < 0 || nc >= cols) {
                        continue;
                    }

                    if (grid[nr][nc] == '@') {
                        neighborRolls++;
                    }
                }

                if (neighborRolls < 4) {
                    accessibleCount++;
                }
            }
        }

        System.out.println("Accessible rolls: " + accessibleCount);
    }
}
