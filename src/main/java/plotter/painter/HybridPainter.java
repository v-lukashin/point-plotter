package plotter.painter;


import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.painter.AbstractPainter;
import plotter.model.Geometry;
import plotter.model.Route;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class HybridPainter extends AbstractPainter<JXMapViewer> {
    private List<Geometry> points;
    private List<Route> routes;

    private List<AbstractPainter<JXMapViewer>> painters;

    public HybridPainter(List<Geometry> geometries, Color color) {
        this.points = new ArrayList<>();
        this.routes = new ArrayList<>();

        for (Geometry geometry : geometries) {
            if (geometry instanceof plotter.model.Point) this.points.add(geometry);
            else if (geometry instanceof Route) this.routes.add((Route) geometry);
        }

        this.painters = new ArrayList<>();
        this.painters.add(new PointsPainter(this.points, color));
        for (Route route : routes) {
            this.painters.add(new RoutePainter(route.points, color));
        }
    }

    @Override
    public void doPaint(Graphics2D g, JXMapViewer map, int w, int h) {
        for (AbstractPainter<JXMapViewer> painter : this.painters) painter.paint(g, map, w, h);
    }
}

