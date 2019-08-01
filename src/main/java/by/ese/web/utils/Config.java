package by.ese.web.utils;

import java.io.*;
import java.net.URL;
import java.util.Properties;

/**
 * Helper class to read properties, either from external resources or inner, bundled with app
 */
public class Config extends Properties{

    public Config(String filePath) {
        readProperties(filePath);
    }

    /**
     * Read properties either from external file or from classpath in order:
     * 1.try to read external file
     * 2.try to read resource from classpath
     *
     * @param filePath path to file
     */
    private void readProperties(String filePath) {

        InputStream inputStream = getConfigInputStream(filePath);

        if (inputStream != null) { // Failed to load
            try {
                load(inputStream);
            } catch (IOException ignored) {
            }
        }
    }

    @SuppressWarnings("WeakerAccess")
    public static InputStream getConfigInputStream(String filePath) {
        final File jarFile = new File(Config.class.getProtectionDomain().getCodeSource().getLocation().getPath());
        InputStream inputStream;

        if (jarFile.isFile()) {  // Run with JAR file
            String propertiesPath = jarFile.getParentFile().getAbsolutePath();

            try {  // Check if external config available
                inputStream = new FileInputStream(propertiesPath + "/" + filePath);
            } catch (FileNotFoundException e) { // Use bundled config
                inputStream = Config.class.getClassLoader().getResourceAsStream(filePath);
            }
        } else { // Run with IDE
            final URL url = Config.class.getResource("/config");
            if (url != null) { // if external config available
                inputStream = Config.class.getResourceAsStream("/" + filePath);
            } else { // Use bundled config
                inputStream = Config.class.getResourceAsStream(filePath);
            }
        }
        return inputStream;
    }
}
