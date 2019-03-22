package plotter;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.WaypointRenderer;

import java.awt.*;
import java.awt.geom.Point2D;

class Renderer implements WaypointRenderer<GeoPos> {
    private Color color;

    public Renderer(Color color) {
        this.color = color;
    }

    @Override
    public void paintWaypoint(Graphics2D g, JXMapViewer map, GeoPos w) {
        Point2D point = map.getTileFactory().geoToPixel(w, map.getZoom());

        int x = (int) point.getX() - 2;
        int y = (int) point.getY() - 2;

        g.fillOval(x, y, 4, 4);
        g.setColor(this.color);
    }
}
