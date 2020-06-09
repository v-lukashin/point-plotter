package plotter.model;

public class Point extends Geometry {
    public double lat;
    public double lon;

    public Point(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    @Override
    public Point getCenter() {
        return this;
    }
}
