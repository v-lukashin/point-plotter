package plotter.model;

import org.jxmapviewer.viewer.TileFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class PoiStorage {
    private Map<Poi, AtomicInteger>[] byZoom;

    public PoiStorage(List<GeoPos> points, TileFactory factory) {
        Map<Poi, AtomicInteger> pois = new HashMap<>(points.size());
        for (GeoPos point : points) {
            Poi poi = new Poi(factory.geoToPixel(point, 0));
            AtomicInteger counter = pois.get(poi);
            if (counter == null) pois.put(poi, counter = new AtomicInteger(0));
            counter.incrementAndGet();
        }

        int maxZoom = factory.getInfo().getMaximumZoomLevel();
        byZoom = new Map[maxZoom + 1];
        byZoom[0] = pois;
    }

    public Map<Poi, AtomicInteger> getByZoom(int zoomLevel) {
        if (zoomLevel >= 0 && zoomLevel < byZoom.length) {
            if (byZoom[zoomLevel] == null) {
                Map<Poi, AtomicInteger> tmp = new HashMap<>();

                int zoomDivide = (int) Math.pow(2, zoomLevel);

                byZoom[0].forEach((p, c) -> {
                    Poi poi = new Poi(p.lat / zoomDivide, p.lon / zoomDivide);
                    AtomicInteger counter = tmp.get(poi);
                    if (counter == null) tmp.put(poi, counter = new AtomicInteger(0));
                    counter.addAndGet(c.get());
                });
                byZoom[zoomLevel] = tmp;
            }
            return byZoom[zoomLevel];
        } else return null;
    }
}
