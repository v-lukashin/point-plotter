package plotter.painter;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.painter.AbstractPainter;
import plotter.Utils;
import plotter.model.GeoPos;
import plotter.model.PoiStorage;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class HeatMapPainter extends AbstractPainter<JXMapViewer> {
    private List<GeoPos> points;
    private PoiStorage storage;

    public HeatMapPainter(List<GeoPos> points) {
        this.points = points;
    }

    @Override
    protected void doPaint(Graphics2D g, JXMapViewer map, int width, int height) {
        if (storage == null) {
            storage = new PoiStorage(points, map.getTileFactory());
            points = null;
        }

        Rectangle rect = map.getViewportBounds();

        double down = rect.x;
        double left = rect.y;
        double up = rect.x + rect.width;
        double right = rect.y + rect.height;

        float[][] heatMask = new float[width][height];

        storage.getByZoom(map.getZoom()).forEach((w, c) -> {
            if (w.lat < up && w.lat > down && w.lon < right && w.lon > left)
                Utils.fillData(heatMask, (int) (w.lat - down), (int) (w.lon - left), c.get());
        });

        BufferedImage bufferedImage = Utils.getImage(heatMask);

        g.drawImage(bufferedImage, null, 0, 0);
    }
}
