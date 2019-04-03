package plotter.painter;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.painter.AbstractPainter;
import plotter.model.GeoPos;
import plotter.model.PoiStorage;

import java.awt.*;
import java.util.List;

public class PointsPainter extends AbstractPainter<JXMapViewer> {
    private List<GeoPos> points;
    private Color color;
    private PoiStorage storage;

    public PointsPainter(List<GeoPos> points, Color color) {
        this.points = points;
        this.color = color;
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
