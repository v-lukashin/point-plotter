package plotter;

import au.com.bytecode.opencsv.CSVParser;
import ch.hsr.geohash.GeoHash;
import ch.hsr.geohash.WGS84Point;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import org.jxmapviewer.viewer.WaypointPainter;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class Utils {
    private static final CSVParser CSV_PARSER = new CSVParser();
    private static final Color[] colors = getGradient(new Color[]{Color.LIMEGREEN, Color.YELLOW, Color.ORANGE, Color.RED}, 5);

    static HashSet<GeoPos> loadCsv(File file) {
        HashSet<GeoPos> positions = new HashSet<>();
        try {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String[] line = CSV_PARSER.parseLine(scanner.nextLine());
                positions.add(new GeoPos(Double.parseDouble(line[0]), Double.parseDouble(line[1])));
            }
        } catch (IOException e) {
        }
        return positions;
    }

    static HashSet<GeoPos> loadGeohash(File file) {
        HashSet<GeoPos> positions = new HashSet<>();
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

    static WaypointPainter<GeoPos> getPainter(Set<GeoPos> points, java.awt.Color color, PainterType type) {
        WaypointPainter<GeoPos> painter;
        if (type == PainterType.POINTS) {
            painter = new WaypointPainter<>();
            painter.setRenderer(new Renderer(color));
        } else painter = new HeatMapPainter();

        painter.setWaypoints(points);
        return painter;
    }

    static void fillData(float[][] data, int x, int y, int radius) {
        for (int i = x - radius; i <= x + radius && i >= 0 && i < data.length; i++) {
            for (int j = y - radius; j <= y + radius && j >= 0 && j < data[i].length; j++) {
                double intensity = Math.sqrt((x - i) * (x - i) + (y - j) * (y - j)) / radius;
                intensity = intensity >= 1 ? 0 : 1 - intensity * intensity;
                data[i][j] += (float) intensity;
            }
        }
    }

    static void setData(WritableImage image, float[][] data) {
        PixelWriter writer = image.getPixelWriter();
        int lengthX = data.length;
        int lengthY = data[0].length;

        float max = 0;
        for (float[] ff : data) for (float f : ff) if (f > max) max = f;

        for (int i = 0; i < lengthX; i++)
            for (int j = 0; j < lengthY; j++) {
                float intensity = data[i][j] / max;
                javafx.scene.paint.Color color;

                if (intensity == 0) color = Color.gray(0, 0);
                else color = colors[Math.round(intensity * (colors.length - 1))];

                writer.setColor(i, j, color);
            }
    }

    static BufferedImage getImage(float[][] data) {
        WritableImage writableImage = new WritableImage(data.length, data[0].length);
        Utils.setData(writableImage, data);

        BufferedImage bufferedImage = new BufferedImage(data.length, data[0].length, BufferedImage.TYPE_INT_ARGB);
        SwingFXUtils.fromFXImage(writableImage, bufferedImage);

        return bufferedImage;
    }

    static Color[] getGradient(Color[] base, int intervalStep) {
        ArrayList<Color> tmp = new ArrayList<>();
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
