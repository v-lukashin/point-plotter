package plotter.painter;


import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.painter.AbstractPainter;
import org.jxmapviewer.viewer.GeoPosition;
import plotter.Utils;
import plotter.model.Geometry;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.Iterator;
import java.util.List;

public class RoutePainter extends AbstractPainter<JXMapViewer> {
    private Color color;

    private List<GeoPosition> points;

    public RoutePainter(List<? extends Geometry> geometries, Color color) {
        this.color = color;

        this.points = Utils.convertToGeoPositions(geometries);
    }

    @Override
    public void doPaint(Graphics2D g, JXMapViewer map, int w, int h) {
        Rectangle rect = map.getViewportBounds();
        g.translate(-rect.x, -rect.y);

        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(4));
        drawRoute(g, map);

        g.setColor(color);
        g.setStroke(new BasicStroke(2));
        drawRoute(g, map);

        g.translate(rect.x, rect.y);
    }

    private void drawRoute(Graphics2D g, JXMapViewer map) {
        Iterator<GeoPosition> it = points.iterator();
        if (!it.hasNext()) return;
        Point2D last = map.getTileFactory().geoToPixel(it.next(), map.getZoom());

        while (it.hasNext()) {
            Point2D curr = map.getTileFactory().geoToPixel(it.next(), map.getZoom());
            g.drawLine((int) last.getX(), (int) last.getY(), (int) curr.getX(), (int) curr.getY());
            last = curr;
        }
    }
}

