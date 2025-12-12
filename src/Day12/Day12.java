package Day12;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class Day12 {
    static class Shape {
        final int index;
        final List<String> rows;
        final List<Orientation> orientations;

        Shape(int index, List<String> rows, List<Orientation> orientations) {
            this.index = index;
            this.rows = rows;
            this.orientations = orientations;
        }

        int area() {
            return orientations.getFirst().cells.length / 2;
        }
    }

    static class Orientation {
        final int w, h;
        final int[] cells;

        Orientation(int w, int h, int[] cells) {
            this.w = w;
            this.h = h;
            this.cells = cells;
        }

        int filledCount() {
            return cells.length / 2;
        }
    }

    static class Region {
        final int w, h;
        final int[] counts;

        Region(int w, int h, int[] counts) {
            this.w = w;
            this.h = h;
            this.counts = counts;
        }
    }

    static class Placement {
        final int[] idx;
        Placement(int[] idx) { this.idx = idx; }
    }

    public static void main(String[] args) throws Exception {
        String fileName = "day12.txt";
        List<String> lines = Files.readAllLines(Path.of(fileName));

        ParseResult parsed = parse(lines);
        List<Shape> shapes = parsed.shapes;
        List<Region> regions = parsed.regions;

        int ok = 0;
        for (Region r : regions) {
            if (canFitRegion(r, shapes)) ok++;
        }
        System.out.println(ok);
    }

    static class ParseResult {
        final List<Shape> shapes;
        final List<Region> regions;
        ParseResult(List<Shape> shapes, List<Region> regions) {
            this.shapes = shapes;
            this.regions = regions;
        }
    }

    static ParseResult parse(List<String> lines) {
        int i = 0;

        Map<Integer, List<String>> shapeRows = new HashMap<>();
        int maxShapeIndex = -1;

        while (i < lines.size()) {
            String line = lines.get(i).trim();
            if (line.isEmpty()) { i++; continue; }

            boolean looksLikeRegion = line.contains("x") && line.contains(":") && !line.endsWith(":");
            boolean looksLikeShapeHeader = line.endsWith(":") && isAllDigits(line.substring(0, line.length() - 1));

            if (looksLikeRegion) break;
            if (!looksLikeShapeHeader) break;

            int idx = Integer.parseInt(line.substring(0, line.length() - 1));
            maxShapeIndex = Math.max(maxShapeIndex, idx);
            i++;

            List<String> rows = new ArrayList<>();
            while (i < lines.size()) {
                String r = lines.get(i);
                if (r.trim().isEmpty()) break;
                rows.add(r.trim());
                i++;
            }
            shapeRows.put(idx, rows);
            while (i < lines.size() && lines.get(i).trim().isEmpty()) i++;
        }

        int shapeCount = maxShapeIndex + 1;
        List<Shape> shapes = new ArrayList<>(shapeCount);
        for (int s = 0; s < shapeCount; s++) {
            List<String> rows = shapeRows.get(s);
            if (rows == null) throw new IllegalArgumentException("Missing shape index " + s);
            List<Orientation> orientations = buildUniqueOrientations(rows);
            shapes.add(new Shape(s, rows, orientations));
        }

        List<Region> regions = new ArrayList<>();
        while (i < lines.size()) {
            String line = lines.get(i).trim();
            i++;
            if (line.isEmpty()) continue;

            int colon = line.indexOf(':');
            if (colon < 0) continue;

            String dim = line.substring(0, colon).trim(); // e.g. "12x5"
            String rest = line.substring(colon + 1).trim(); // counts

            String[] wh = dim.split("x");
            int w = Integer.parseInt(wh[0].trim());
            int h = Integer.parseInt(wh[1].trim());

            String[] parts = rest.isEmpty() ? new String[0] : rest.split("\\s+");
            if (parts.length != shapeCount) {
                throw new IllegalArgumentException("Region counts length " + parts.length +
                        " != number of shapes " + shapeCount + " for line: " + line);
            }
            int[] counts = new int[shapeCount];
            for (int k = 0; k < shapeCount; k++) counts[k] = Integer.parseInt(parts[k]);
            regions.add(new Region(w, h, counts));
        }

        return new ParseResult(shapes, regions);
    }

    static boolean isAllDigits(String s) {
        if (s.isEmpty()) return false;
        for (int i = 0; i < s.length(); i++) if (!Character.isDigit(s.charAt(i))) return false;
        return true;
    }

    static List<Orientation> buildUniqueOrientations(List<String> rows) {
        List<int[]> base = new ArrayList<>();
        int h = rows.size();
        int w = rows.getFirst().length();
        for (int y = 0; y < h; y++) {
            String r = rows.get(y);
            for (int x = 0; x < r.length(); x++) {
                if (r.charAt(x) == '#') base.add(new int[]{x, y});
            }
        }

        Set<String> seen = new HashSet<>();
        List<Orientation> out = new ArrayList<>();

        for (int rot = 0; rot < 4; rot++) {
            for (int flip = 0; flip < 2; flip++) {
                List<int[]> pts = new ArrayList<>(base.size());
                for (int[] p : base) {
                    int x = p[0], y = p[1];

                    int rx, ry;
                    ry = switch (rot) {
                        case 0 -> {
                            rx = x;
                            yield y;
                        }
                        case 1 -> {
                            rx = y;
                            yield -x;
                        }
                        case 2 -> {
                            rx = -x;
                            yield -y;
                        }
                        case 3 -> {
                            rx = -y;
                            yield x;
                        }
                        default -> throw new IllegalStateException();
                    };

                    if (flip == 1) rx = -rx;

                    pts.add(new int[]{rx, ry});
                }

                Orientation norm = normalizeOrientation(pts);
                String key = orientationKey(norm);
                if (seen.add(key)) out.add(norm);
            }
        }
        return out;
    }

    static Orientation normalizeOrientation(List<int[]> pts) {
        int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE;

        for (int[] p : pts) {
            minX = Math.min(minX, p[0]);
            minY = Math.min(minY, p[1]);
            maxX = Math.max(maxX, p[0]);
            maxY = Math.max(maxY, p[1]);
        }

        int w = (maxX - minX) + 1;
        int h = (maxY - minY) + 1;

        int[] cells = new int[pts.size() * 2];
        for (int i = 0; i < pts.size(); i++) {
            int nx = pts.get(i)[0] - minX;
            int ny = pts.get(i)[1] - minY;
            cells[2 * i] = nx;
            cells[2 * i + 1] = ny;
        }

        Integer[] order = new Integer[pts.size()];
        for (int i = 0; i < order.length; i++) order[i] = i;
        Arrays.sort(order, Comparator.comparingInt(a -> cells[2 * a] * 10_000 + cells[2 * a + 1]));
        int[] sorted = new int[cells.length];
        for (int i = 0; i < order.length; i++) {
            sorted[2 * i] = cells[2 * order[i]];
            sorted[2 * i + 1] = cells[2 * order[i] + 1];
        }

        return new Orientation(w, h, sorted);
    }

    static String orientationKey(Orientation o) {
        StringBuilder sb = new StringBuilder();
        sb.append(o.w).append('x').append(o.h).append(':');
        for (int i = 0; i < o.cells.length; i += 2) {
            sb.append(o.cells[i]).append(',').append(o.cells[i + 1]).append(';');
        }
        return sb.toString();
    }

    static boolean canFitRegion(Region region, List<Shape> shapes) {
        int W = region.w, H = region.h;
        int totalCells = W * H;

        long requiredArea = 0;
        for (int s = 0; s < shapes.size(); s++) {
            requiredArea += (long) region.counts[s] * shapes.get(s).area();
        }
        if (requiredArea > totalCells) return false;

        @SuppressWarnings("unchecked")
        ArrayList<Placement>[] placements = new ArrayList[shapes.size()];
        for (int s = 0; s < shapes.size(); s++) {
            placements[s] = buildPlacementsForShape(shapes.get(s), W, H);
            if (region.counts[s] > 0 && placements[s].isEmpty()) return false;
        }

        int[] remaining = Arrays.copyOf(region.counts, region.counts.length);

        boolean[] occ = new boolean[totalCells];
        int filled = 0;

        int[] area = new int[shapes.size()];
        for (int s = 0; s < shapes.size(); s++) area[s] = shapes.get(s).area();

        return dfs(occ, filled, remaining, placements, area, totalCells);
    }

    static ArrayList<Placement> buildPlacementsForShape(Shape shape, int W, int H) {
        ArrayList<Placement> list = new ArrayList<>();
        for (Orientation o : shape.orientations) {
            for (int y0 = 0; y0 <= H - o.h; y0++) {
                for (int x0 = 0; x0 <= W - o.w; x0++) {
                    int n = o.filledCount();
                    int[] idx = new int[n];
                    for (int k = 0; k < n; k++) {
                        int x = o.cells[2 * k] + x0;
                        int y = o.cells[2 * k + 1] + y0;
                        idx[k] = y * W + x;
                    }
                    list.add(new Placement(idx));
                }
            }
        }
        return list;
    }

    static boolean dfs(boolean[] occ,
                       int filled,
                       int[] remaining,
                       ArrayList<Placement>[] placements,
                       int[] area,
                       int totalCells) {

        int remainingPieces = 0;
        long remainingArea = 0;
        for (int s = 0; s < remaining.length; s++) {
            if (remaining[s] > 0) {
                remainingPieces += remaining[s];
                remainingArea += (long) remaining[s] * area[s];
            }
        }
        if (remainingPieces == 0) return true;

        int free = totalCells - filled;
        if (remainingArea > free) return false;

        int chosenShape = -1;
        int bestOptions = Integer.MAX_VALUE;

        for (int s = 0; s < remaining.length; s++) {
            if (remaining[s] <= 0) continue;

            int options = 0;
            for (Placement p : placements[s]) {
                if (!overlaps(occ, p.idx)) {
                    options++;
                    if (options >= bestOptions) break;
                }
            }

            if (options == 0) return false;
            if (options < bestOptions) {
                bestOptions = options;
                chosenShape = s;
                if (bestOptions == 1) break;
            }
        }

        remaining[chosenShape]--;
        for (Placement p : placements[chosenShape]) {
            if (overlaps(occ, p.idx)) continue;

            for (int idx : p.idx) occ[idx] = true;
            if (dfs(occ, filled + p.idx.length, remaining, placements, area, totalCells)) return true;
            for (int idx : p.idx) occ[idx] = false;
        }
        remaining[chosenShape]++;

        return false;
    }

    static boolean overlaps(boolean[] occ, int[] idx) {
        for (int c : idx) if (occ[c]) return true;
        return false;
    }
}
