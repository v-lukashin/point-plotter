package plotter;

import plotter.controller.MapController;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;

public class Setup {
    public static final MapController mapController = new MapController();
    public static final AtomicReference<File> lastDir = new AtomicReference<>();

    static {
        load();
    }

    public static void store() {
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
