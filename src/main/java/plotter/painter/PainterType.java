package plotter.painter;

public enum PainterType {
    POINTS("Points"), HEATMAP("HeatMap"), ROUTE("Route"), HYBRID("Hybrid");
    private String name;

    PainterType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
