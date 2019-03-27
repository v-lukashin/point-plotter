package plotter;

import javafx.collections.ObservableMap;
import org.jxmapviewer.viewer.WaypointPainter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;

class Setup {
    static final MapController mapController = new MapController();
    static final AtomicReference<File> lastDir = new AtomicReference<>();
//    static ObservableMap<String, WaypointPainter<GeoPos>> pointsMap;

    static {
        load();
    }

    static void store() {
        File dir = Setup.lastDir.get();
        if (dir != null) {
            Properties properties = new Properties();
            properties.setProperty("lastdir", dir.getAbsolutePath());
            try {
                properties.store(new FileOutputStream("cache"), "");
            } catch (IOException ignored) {
            }
        }
    }

    private static void load() {
        try {
            Properties properties = new Properties();
            properties.load(new FileInputStream("cache"));
            String path = properties.getProperty("lastdir", null);
            if (path != null) lastDir.set(new File(path));
        } catch (IOException ignored) {
        }
    }
}
