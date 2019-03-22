package plotter;

import au.com.bytecode.opencsv.CSVParser;
import ch.hsr.geohash.GeoHash;
import ch.hsr.geohash.WGS84Point;
import org.jxmapviewer.viewer.WaypointPainter;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class Utils {
    private static final CSVParser CSV_PARSER = new CSVParser();

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

    static WaypointPainter<GeoPos> getPainter(Set<GeoPos> points, Color color) {
        WaypointPainter<GeoPos> painter = new WaypointPainter<>();
        painter.setRenderer(new Renderer(color));
        painter.setWaypoints(points);
        return painter;
    }
}
