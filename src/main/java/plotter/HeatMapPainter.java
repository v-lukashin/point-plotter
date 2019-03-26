package plotter;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.WaypointPainter;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

public class HeatMapPainter extends WaypointPainter<GeoPos> {
    @Override
    protected void doPaint(Graphics2D g, JXMapViewer map, int width, int height) {
        Rectangle viewportBounds = map.getViewportBounds();

        double down = viewportBounds.x;
        double left = viewportBounds.y;
        double up = viewportBounds.x + viewportBounds.width;
        double right = viewportBounds.y + viewportBounds.height;

        float[][] heatMask = new float[width][height];

        for (GeoPos w : getWaypoints()) {
            Point2D point = map.getTileFactory().geoToPixel(w, map.getZoom());

            double lat = point.getX();
            double lon = point.getY();

            if (lat < up && lat > down && lon < right && lon > left)
                Utils.fillData(heatMask, (int) (lat - down), (int) (lon - left), 20);
        }

        BufferedImage bufferedImage = Utils.getImage(heatMask);

        g.drawImage(bufferedImage, null, 0, 0);
    }
}
