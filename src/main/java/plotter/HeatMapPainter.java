package plotter;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.painter.AbstractPainter;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.List;

public class HeatMapPainter extends AbstractPainter<JXMapViewer> {
    private List<GeoPos> points;

    public HeatMapPainter(List<GeoPos> points) {
        this.points = points;
    }

    @Override
    protected void doPaint(Graphics2D g, JXMapViewer map, int width, int height) {
        Rectangle rect = map.getViewportBounds();

        double down = rect.x;
        double left = rect.y;
        double up = rect.x + rect.width;
        double right = rect.y + rect.height;

        float[][] heatMask = new float[width][height];

        for (GeoPos w : points) {
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
