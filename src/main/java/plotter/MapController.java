package plotter;

import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.cache.FileBasedLocalCache;
import org.jxmapviewer.input.PanMouseInputListener;
import org.jxmapviewer.input.ZoomMouseWheelListenerCursor;
import org.jxmapviewer.painter.CompoundPainter;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.TileFactoryInfo;
import org.jxmapviewer.viewer.WaypointPainter;

import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class MapController {
    private JXMapViewer mapViewer;
    private CompoundPainter<JXMapViewer> painter;
    private StringProperty title;
    private ObservableMap<String, WaypointPainter<GeoPos>> pointsMap;

    public MapController() {
        mapViewer = new JXMapViewer();
        painter = new CompoundPainter<>();
        pointsMap = FXCollections.observableHashMap();

        // Create a TileFactoryInfo for OpenStreetMap
        TileFactoryInfo info = new OSMTileFactoryInfo();
        DefaultTileFactory tileFactory = new DefaultTileFactory(info);

        // Setup local file cache
        File cacheDir = new File(System.getProperty("user.home") + File.separator + ".jxmapviewer2");
        tileFactory.setLocalCache(new FileBasedLocalCache(cacheDir, false));

        // Setup JXMapViewer
        mapViewer.setTileFactory(tileFactory);
        mapViewer.setOverlayPainter(this.painter);

        // Add interactions
        MouseInputListener mia = new PanMouseInputListener(mapViewer);
        mapViewer.addMouseListener(mia);
        mapViewer.addMouseMotionListener(mia);
        mapViewer.addMouseWheelListener(new ZoomMouseWheelListenerCursor(mapViewer));

        mapViewer.addPropertyChangeListener("zoom", evt -> updateWindowTitle());
        mapViewer.addPropertyChangeListener("center", evt -> updateWindowTitle());
        updateWindowTitle();
    }

    public JXMapViewer getMapViewer() {
        return mapViewer;
    }

    public ObservableMap<String, WaypointPainter<GeoPos>> getPointsMap() {
        return pointsMap;
    }

    public void setTitle(StringProperty title) {
        this.title = title;
    }

    private void updateWindowTitle() {
        if (title != null) {
            double lat = mapViewer.getCenterPosition().getLatitude();
            double lon = mapViewer.getCenterPosition().getLongitude();
            int zoom = mapViewer.getZoom();

            Platform.runLater(() -> title.setValue(String.format("POI (%.2f / %.2f) - Zoom: %d", lat, lon, zoom)));
        }
    }

    private GeoPos computeGeoCenter(final Set<GeoPos> positions) {
        double sumLat = 0;
        double sumLon = 0;

        for (GeoPos pos : positions) {
            sumLat += pos.getLatitude();
            sumLon += pos.getLongitude();
        }
        double avgLat = sumLat / positions.size();
        double avgLon = sumLon / positions.size();
        return new GeoPos(avgLat, avgLon);
    }

    public void addPoints(File file, Color color, PainterType type) {
        HashSet<GeoPos> points = (file.getName().endsWith(".csv")) ? Utils.loadCsv(file) : Utils.loadGeohash(file);

        mapViewer.setCenterPosition(computeGeoCenter(points));
        mapViewer.setZoom(8);

        WaypointPainter<GeoPos> painter = Utils.getPainter(points, color, type);
        String name = file.getName() + " " + type;
        if (pointsMap.containsKey(name)) {
            int i = 0;
            while (pointsMap.containsKey(name + " " + ++i)) {
            }
            name += " " + i;
        }
        pointsMap.put(name, painter);
        this.painter.addPainter(painter);
    }

    public void removePoints(String fileName) {
        painter.removePainter(pointsMap.remove(fileName));
    }

    public void resetPoints() {
        pointsMap.clear();
        painter.setPainters();
    }
}
