package plotter;

public enum PainterType {
    POINTS("Points"), HEATMAP("HeatMap");
    private String name;

    PainterType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
