package plotter;

import javafx.embed.swing.JFXPanel;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.cache.FileBasedLocalCache;
import org.jxmapviewer.input.PanMouseInputListener;
import org.jxmapviewer.input.ZoomMouseWheelListenerCursor;
import org.jxmapviewer.painter.CompoundPainter;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.TileFactoryInfo;
import org.jxmapviewer.viewer.WaypointPainter;

import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.stream.Collectors;

public class TestView {
    public static void main(String[] args) throws IOException {
        // Create a TileFactoryInfo for OpenStreetMap
        TileFactoryInfo info = new OSMTileFactoryInfo();
        DefaultTileFactory tileFactory = new DefaultTileFactory(info);

        // Setup local file cache
        File cacheDir = new File(System.getProperty("user.home") + File.separator + ".jxmapviewer2");
        tileFactory.setLocalCache(new FileBasedLocalCache(cacheDir, false));

        // Setup JXMapViewer
        final JXMapViewer mapViewer = new JXMapViewer();
        mapViewer.setTileFactory(tileFactory);

        HashSet<GeoPos> points1 = Utils.loadGeohash(new File("part.txt"));
        HashSet<GeoPos> points2 = Utils.loadCsv(new File("BAZA.csv"));

        WaypointPainter<GeoPos> painter1 = Utils.getPainter(points1, Color.BLUE);
        WaypointPainter<GeoPos> painter2 = Utils.getPainter(points2, Color.RED);

        CompoundPainter<JXMapViewer> painter = new CompoundPainter<>(painter1, painter2);
        mapViewer.setOverlayPainter(painter);

        // Set the focus
        mapViewer.setZoom(8);
        mapViewer.zoomToBestFit(points2.stream().map(p -> (GeoPosition) p).collect(Collectors.toSet()), .7);

        // Display the viewer in a JFrame
        final JFrame frame = new JFrame();
        frame.setLayout(new BorderLayout());
        frame.add(mapViewer);
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        // Add interactions
        MouseInputListener mia = new PanMouseInputListener(mapViewer);
        mapViewer.addMouseListener(mia);
        mapViewer.addMouseMotionListener(mia);
        mapViewer.addMouseWheelListener(new ZoomMouseWheelListenerCursor(mapViewer));

        mapViewer.addPropertyChangeListener("zoom", evt -> updateWindowTitle(frame, mapViewer));
        mapViewer.addPropertyChangeListener("center", evt -> updateWindowTitle(frame, mapViewer));
        updateWindowTitle(frame, mapViewer);
    }

    private static void updateWindowTitle(JFrame frame, JXMapViewer mapViewer) {
        double lat = mapViewer.getCenterPosition().getLatitude();
        double lon = mapViewer.getCenterPosition().getLongitude();
        int zoom = mapViewer.getZoom();

        frame.setTitle(String.format("POI (%.2f / %.2f) - Zoom: %d", lat, lon, zoom));
    }

}
