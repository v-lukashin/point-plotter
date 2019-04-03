package plotter;

import au.com.bytecode.opencsv.CSVParser;
import ch.hsr.geohash.GeoHash;
import ch.hsr.geohash.WGS84Point;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import org.jxmapviewer.painter.AbstractPainter;
import plotter.model.GeoPos;
import plotter.painter.HeatMapPainter;
import plotter.painter.PainterType;
import plotter.painter.PointsPainter;
import plotter.painter.RoutePainter;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Utils {
    private static final CSVParser CSV_PARSER = new CSVParser();
    private static final Color[] colors = getGradient(new Color[]{Color.LIMEGREEN, Color.YELLOW, Color.ORANGE, Color.RED}, 5);
    private static final Color invisible = Color.gray(0, 0);
    private static final int radius = 20;
    private static final double r = radius * radius;

    public static List<GeoPos> loadCsv(File file) {
        List<GeoPos> positions = new ArrayList<>();
        try {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String[] line = CSV_PARSER.parseLine(scanner.nextLine());
                if (line.length > 1)
                    positions.add(new GeoPos(Double.parseDouble(line[0]), Double.parseDouble(line[1])));
            }
        } catch (IOException e) {
        }
        return positions;
    }

    public static List<GeoPos> loadGeohash(File file) {
        List<GeoPos> positions = new ArrayList<>();
        try {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                GeoHash geoHash = GeoHash.fromGeohashString(scanner.nextLine().split(":")[0]);
                WGS84Point point = geoHash.getPoint();
                positions.add(new GeoPos(point.getLatitude(), point.getLongitude()));
            }
        } catch (IOException e) {
        }
        return positions;
    }

    public static AbstractPainter getPainter(List<GeoPos> points, java.awt.Color color, PainterType type) {
        AbstractPainter painter;

        switch (type) {
            case POINTS:
                painter = new PointsPainter(points, color);
                break;
            case HEATMAP:
                painter = new HeatMapPainter(points);
                break;
            case ROUTE:
            default:
                painter = new RoutePainter(points, color);
        }

        return painter;
    }

    public static void fillData(float[][] data, int x, int y, int mult) {
        int minX = Math.max(x - radius, 0);
        int maxX = Math.min(x + radius, data.length - 1);
        int minY = Math.max(y - radius, 0);
        int maxY = Math.min(y + radius, data[0].length - 1);

        for (int i = minX; i <= maxX; i++) {
            for (int j = minY; j <= maxY; j++) {
                int dx = x - i;
                int dy = y - j;
                double intensity = dx * dx + dy * dy;
                intensity = intensity >= r ? 0 : 1 - intensity / r;
                data[i][j] += (float) intensity * mult;
            }
        }
    }

    private static void setData(WritableImage image, float[][] data) {
        PixelWriter writer = image.getPixelWriter();
        int lengthX = data.length;
        int lengthY = data[0].length;

        float max = 0;
        for (float[] ff : data) for (float f : ff) if (f > max) max = f;

        for (int i = 0; i < lengthX; i++)
            for (int j = 0; j < lengthY; j++) {
                float intensity = data[i][j] / max;
                writer.setColor(i, j, intensity == 0 ? invisible : colors[Math.round(intensity * (colors.length - 1))]);
            }
    }

    public static BufferedImage getImage(float[][] data) {
        WritableImage writableImage = new WritableImage(data.length, data[0].length);
        Utils.setData(writableImage, data);

        BufferedImage bufferedImage = new BufferedImage(data.length, data[0].length, BufferedImage.TYPE_INT_ARGB);
        SwingFXUtils.fromFXImage(writableImage, bufferedImage);

        return bufferedImage;
    }

    private static Color[] getGradient(Color[] base, int intervalStep) {
        List<Color> tmp = new ArrayList<>();
        for (int i = 0; i < base.length - 1; i++) {
            Color start = base[i];
            Color end = base[i + 1];

            for (int j = 0; j <= intervalStep; j++) tmp.add(start.interpolate(end, j / (intervalStep + 1.0)));
        }
        tmp.add(base[base.length - 1]);

        Color[] colors = new Color[tmp.size()];
        for (int i = 0; i < colors.length; i++) {
            Color color = tmp.get(i);
            double opacity = ((colors.length - i) * .5 + i * .9) / colors.length;
            colors[i] = new Color(color.getRed(), color.getGreen(), color.getBlue(), opacity);
        }

        return colors;
    }
}
