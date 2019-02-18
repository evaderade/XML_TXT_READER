import config.DbConfig;
import entity.Contacts;
import entity.Customers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Radosław Kokoszka on 16.02.2019 18:30
 */


public class ProcessTxtFile {

    public static Integer process(String pathToFile) {
        Integer rowInserted = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(Paths.get(pathToFile).toString()));
             Connection connection = DbConfig.getDatabaseConnection();
             StatementFactory statementFactory = StatementFactory.getInstance(connection)) {
            PreparedStatement preparedStatementSaveCustomer = statementFactory.getPreparedStatementSaveCustomer();
            PreparedStatement preparedStatementGetCustomerId = statementFactory.getPreparedStatementGetCustomerId();
            PreparedStatement preparedStatementSaveContact = statementFactory.getPreparedStatementSaveContact();
            String line;
            List<String> data;
            while ((line = br.readLine()) != null) {
                data = Arrays.asList(line.split("\\s*,\\s*"));
                Customers customer = new Customers(data.get(0), data.get(1), data.get(2));
                Long id;
                preparedStatementSaveCustomer.setString(1, customer.getName());
                preparedStatementSaveCustomer.setString(2, customer.getSurname());
                preparedStatementSaveCustomer.setInt(3, customer.getAge());
                preparedStatementSaveCustomer.execute();
                rowInserted = increment(rowInserted);
                try (ResultSet rs = preparedStatementGetCustomerId.executeQuery()) {
                    if (rs.next()) {
                        id = new Long(rs.getInt("id"));
                        rowInserted = saveObject(data, id, preparedStatementSaveContact, rowInserted);
                    }
                }
            }
            System.out.println("Przetwarzanie pliku zostało zakończone");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rowInserted;
    }

    private static Integer saveObject(List<String> data, Long id, PreparedStatement statement, Integer rowInserted) throws SQLException {
        for (int i = 4; i < data.size(); i++) {
            statement.setLong(1, id);
            statement.setString(3, data.get(i));
            if (data.get(i).contains("@jabber")) {
                Contacts contact = new Contacts(id, "3", data.get(i));
                statement.setInt(2, 3);
            } else if (data.get(i).contains("@")) {
                Contacts contact = new Contacts(id, "1", data.get(i));
                statement.setInt(2, 1);
            } else if (data.get(i).matches("(?<!\\w)(\\(?(\\+|00)?48\\)?)?[ -]?\\d{3}[ -]?\\d{3}[ -]?\\d{3}(?!\\w)")) {
                Contacts contact = new Contacts(id, "2", data.get(i));
                statement.setInt(2, 2);
            } else {
                Contacts contact = new Contacts(id, "0", data.get(i));
                statement.setInt(2, 0);
            }
            statement.execute();
            rowInserted = increment(rowInserted);
        }
        return rowInserted;
    }

    private static Integer increment(Integer i) {
        return i + 1;
    }


}
