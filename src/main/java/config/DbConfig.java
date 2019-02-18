package config;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by Radosław Kokoszka on 16.02.2019 16:39
 */


public class DbConfig {

    private static Connection connection = null;
    private static String[] properties;

    public static Connection getDatabaseConnection() throws SQLException, IOException, ClassNotFoundException {
        properties = new PropertiesConfig().getPropValues();
        Class.forName(properties[0]);
        return connection = DriverManager.getConnection(properties[1], properties[2], properties[3]);
    }
}
