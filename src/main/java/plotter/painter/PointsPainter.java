package plotter.painter;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.painter.AbstractPainter;
import org.jxmapviewer.viewer.GeoPosition;
import plotter.Utils;
import plotter.model.Geometry;
import plotter.model.PoiStorage;

import java.awt.*;
import java.util.List;

public class PointsPainter extends AbstractPainter<JXMapViewer> {
    private List<GeoPosition> points;
    private Color color;
    private PoiStorage storage;

    public PointsPainter(List<Geometry> geometries, Color color) {
        this.color = color;

        this.points = Utils.convertToGeoPositions(geometries);
    }

    @Override
    protected void doPaint(Graphics2D g, JXMapViewer map, int width, int height) {
        if (storage == null) {
            storage = new PoiStorage(points, map.getTileFactory());
            points = null;
        }
        Rectangle rect = map.getViewportBounds();

        g.translate(-rect.getX(), -rect.getY());

        storage.getByZoom(map.getZoom()).forEach((w, c) -> {
            g.setColor(this.color);
            g.fillOval(w.lat - 2, w.lon - 2, 4, 4);
        });

        g.translate(rect.getX(), rect.getY());
    }
}
