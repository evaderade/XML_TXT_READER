package config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by Radosław Kokoszka on 16.02.2019 16:46
 */


public class PropertiesConfig {
    String values[] = new String[4];
    InputStream input = null;

    public String[] getPropValues() throws IOException {

        try {
            Properties prop = new Properties();
            input = new FileInputStream("src\\main\\resources\\application.properties");
            prop.load(input);
            setValues(prop);

        } catch (Exception e) {
            System.out.println("Exception: " + e);
        } finally {
            input.close();
        }
        return values;
    }

    private void setValues(Properties prop) {
        values[0] = prop.getProperty("driver");
        values[1] = prop.getProperty("url");
        values[2] = prop.getProperty("user");
        values[3] = prop.getProperty("password");
    }
}
