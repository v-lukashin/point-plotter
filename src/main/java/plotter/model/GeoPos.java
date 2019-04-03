package plotter.model;

import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.Waypoint;

public class GeoPos extends GeoPosition implements Waypoint {
    public GeoPos(double latitude, double longitude) {
        super(latitude, longitude);
    }

    @Override
    public GeoPosition getPosition() {
        return this;
    }
}
