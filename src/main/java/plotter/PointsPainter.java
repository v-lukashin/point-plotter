package plotter;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.painter.AbstractPainter;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.List;

public class PointsPainter extends AbstractPainter<JXMapViewer> {
    private List<GeoPos> points;
    private Color color;

    public PointsPainter(List<GeoPos> points, Color color) {
        this.points = points;
        this.color = color;
    }

    @Override
    protected void doPaint(Graphics2D g, JXMapViewer map, int width, int height) {
        Rectangle rect = map.getViewportBounds();

        g.translate(-rect.getX(), -rect.getY());

        for (GeoPos pos : points) {
            Point2D point = map.getTileFactory().geoToPixel(pos, map.getZoom());

            int x = (int) point.getX() - 2;
            int y = (int) point.getY() - 2;

            g.setColor(this.color);
            g.fillOval(x, y, 4, 4);
        }

        g.translate(rect.getX(), rect.getY());
    }
}
