package Day10;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day10 {
    private static final Pattern LIGHTS_PATTERN = Pattern.compile("\\[(.+?)]");
    private static final Pattern BUTTON_PATTERN = Pattern.compile("\\(([^)]*)\\)");

    public static void main(String[] args) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get("day10.txt"));

        long totalPresses = 0;
        for (String line : lines) {
            String trimmed = line.trim();
            if (trimmed.isEmpty()) continue;
            totalPresses += solveMachineBruteForce(trimmed);
        }

        System.out.println(totalPresses);
    }

    private static int solveMachineBruteForce(String line) {
        Matcher lm = LIGHTS_PATTERN.matcher(line);
        if (!lm.find()) {
            throw new IllegalArgumentException("No indicator light diagram in line: " + line);
        }
        String lightsStr = lm.group(1).trim();
        int nLights = lightsStr.length();

        boolean[] goal = new boolean[nLights];
        for (int i = 0; i < nLights; i++) {
            char c = lightsStr.charAt(i);
            if (c == '#') {
                goal[i] = true;
            } else if (c == '.') {
                goal[i] = false;
            } else {
                throw new IllegalArgumentException("Invalid light char '" + c + "' in: " + lightsStr);
            }
        }

        List<int[]> buttons = new ArrayList<>();
        Matcher bm = BUTTON_PATTERN.matcher(line);
        while (bm.find()) {
            String inside = bm.group(1).trim();
            if (inside.isEmpty()) {
                buttons.add(new int[0]);
            } else {
                String[] parts = inside.split(",");
                int[] idxs = new int[parts.length];
                for (int i = 0; i < parts.length; i++) {
                    String p = parts[i].trim();
                    int val = Integer.parseInt(p);
                    if (val < 0 || val >= nLights) {
                        throw new IllegalArgumentException("Button index out of range: " + val +
                                " for lights count " + nLights + " in line: " + line);
                    }
                    idxs[i] = val;
                }
                buttons.add(idxs);
            }
        }

        int nButtons = buttons.size();
        if (nButtons == 0) {
            for (boolean g : goal) {
                if (g) {
                    throw new IllegalStateException("No buttons but non-zero goal in line: " + line);
                }
            }
            return 0;
        }

        int minPresses = Integer.MAX_VALUE;

        int maxMask = 1 << nButtons;
        boolean[] state = new boolean[nLights];

        for (int mask = 0; mask < maxMask; mask++) {
            int presses = Integer.bitCount(mask);
            if (presses >= minPresses) continue;

            Arrays.fill(state, false);

            for (int bIndex = 0; bIndex < nButtons; bIndex++) {
                if ((mask & (1 << bIndex)) != 0) {
                    int[] toggles = buttons.get(bIndex);
                    for (int lightIndex : toggles) {
                        state[lightIndex] = !state[lightIndex];
                    }
                }
            }

            boolean ok = true;
            for (int i = 0; i < nLights; i++) {
                if (state[i] != goal[i]) {
                    ok = false;
                    break;
                }
            }

            if (ok) {
                minPresses = presses;
                if (minPresses == 0) break;
            }
        }

        if (minPresses == Integer.MAX_VALUE) {
            throw new IllegalStateException("No solution found for line: " + line);
        }

        return minPresses;
    }
}
