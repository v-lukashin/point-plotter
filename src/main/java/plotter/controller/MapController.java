package plotter.controller;

import javafx.application.Platform;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.scene.input.KeyCode;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.cache.FileBasedLocalCache;
import org.jxmapviewer.input.PanMouseInputListener;
import org.jxmapviewer.input.ZoomMouseWheelListenerCursor;
import org.jxmapviewer.painter.AbstractPainter;
import org.jxmapviewer.painter.CompoundPainter;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.TileFactoryInfo;
import plotter.Utils;
import plotter.model.GeoPos;
import plotter.painter.PainterType;

import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.geom.Point2D;
import java.io.File;
import java.util.List;

public class MapController {
    private JXMapViewer mapViewer;
    private CompoundPainter<JXMapViewer> painter;
    private StringProperty title;
    private ObservableMap<String, AbstractPainter> pointsMap;

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

    public ObservableMap<String, AbstractPainter> getPointsMap() {
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

    public void zoomIn() {
        mapViewer.setZoom(mapViewer.getZoom() - 1);
    }

    public void zoomOut() {
        mapViewer.setZoom(mapViewer.getZoom() + 1);
    }

    public void move(KeyCode code) {
        Point2D center = mapViewer.getCenter();
        int width = mapViewer.getWidth();
        int height = mapViewer.getHeight();
        double x = center.getX();
        double y = center.getY();

        switch (code) {
            case UP:
                y -= height / 8;
                break;

            case DOWN:
                y += height / 8;
                break;

            case LEFT:
                x -= width / 8;
                break;

            case RIGHT:
                x += width / 8;
                break;
        }

        mapViewer.setCenter(new Point2D.Double(x, y));
    }

    private GeoPos computeGeoCenter(final List<GeoPos> positions) {
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
        List<GeoPos> points = (file.getName().endsWith(".csv")) ? Utils.loadCsv(file) : Utils.loadGeohash(file);

        mapViewer.setCenterPosition(computeGeoCenter(points));
        mapViewer.setZoom(8);

        AbstractPainter painter = Utils.getPainter(points, color, type);
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
