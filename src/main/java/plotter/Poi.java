package plotter;

import java.awt.geom.Point2D;

public class Poi {
    public int lat;
    public int lon;

    public Poi(int lat, int lon) {
        this.lat = lat;
        this.lon = lon;
    }

    public Poi(Point2D point) {
        this.lat = (int) point.getX();
        this.lon = (int) point.getY();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Poi poi = (Poi) o;

        if (lat != poi.lat) return false;
        return lon == poi.lon;
    }

    @Override
    public int hashCode() {
        return (lat + "," + lon).hashCode();
    }
}
