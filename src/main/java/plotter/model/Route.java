package plotter.model;

import java.util.List;

public class Route extends Geometry {
    public List<Point> points;
    
    public Route(List<Point> points) {
        this.points = points;
    }

    @Override
    public Point getCenter() {
        double sumLat = 0;
        double sumLon = 0;

        for (Point pos : points) {
            sumLat += pos.lat;
            sumLon += pos.lon;
        }

        double avgLat = sumLat / points.size();
        double avgLon = sumLon / points.size();
        return new Point(avgLat, avgLon);
    }
}
