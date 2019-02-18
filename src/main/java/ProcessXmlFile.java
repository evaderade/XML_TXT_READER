import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import config.DbConfig;
import entity.Contacts;
import entity.Customers;
import org.json.JSONObject;
import org.json.XML;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

/**
 * Created by Radosław Kokoszka on 16.02.2019 18:47
 */

public class ProcessXmlFile {
    private static final String PHONE_PL_REGEX = "(?<!\\w)(\\(?(\\+|00)?48\\)?)?[ -]?\\d{3}[ -]?\\d{3}[ -]?\\d{3}(?!\\w)";
    public static Integer process(String pathToFile) {
        File file = new File(pathToFile);
        Integer rowInserted = 0;
        try (FileInputStream fin = new FileInputStream(file);
             Connection connection = DbConfig.getDatabaseConnection();
             StatementFactory statementFactory = StatementFactory.getInstance(connection)) {
            PreparedStatement preparedStatementSaveCustomer = statementFactory.getPreparedStatementSaveCustomer();
            PreparedStatement preparedStatementGetCustomerId = statementFactory.getPreparedStatementGetCustomerId();
            PreparedStatement preparedStatementSaveContact = statementFactory.getPreparedStatementSaveContact();
            byte[] xmlData = new byte[(int) file.length()];
            fin.read(xmlData);
            String xml = new String(xmlData, "UTF-8");
            JSONObject xmlJSONObj = XML.toJSONObject(xml);
            String jsonString = xmlJSONObj.toString(4);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(jsonString).get("persons").get("person");
            ResultSet rs = null;
            if (jsonNode.isArray()) {
                for (JsonNode objNode : jsonNode) {
                    rowInserted = saveObject(objNode, preparedStatementSaveCustomer, preparedStatementGetCustomerId, preparedStatementSaveContact, rowInserted);
                }

            } else {
                rowInserted = saveObject(jsonNode, preparedStatementSaveCustomer, preparedStatementGetCustomerId, preparedStatementSaveContact, rowInserted);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("Przetwarzanie pliku zostało zakończone!");
        }
        return rowInserted;
    }

    private static Integer saveObject(JsonNode jsonNode, PreparedStatement preparedStatementSaveCustomer, PreparedStatement preparedStatementGetCustomerId, PreparedStatement preparedStatementSaveContact, Integer rowInserted) throws SQLException {
        String name = "";
        String surname = "";
        String age = "";
        String city = "";
        if (jsonNode.has("name")) {
            name = jsonNode.get("name").asText();
        }
        if (jsonNode.has("surname")) {
            surname = jsonNode.get("surname").asText();
        }
        if (jsonNode.has("age")) {
            age = jsonNode.get("age").asText();
        }
        if (jsonNode.has("city")) {
            city = jsonNode.get("city").asText();
        }
        Customers customer = new Customers(name, surname, age);
        Long id = null;
        preparedStatementSaveCustomer.setString(1, customer.getName());
        preparedStatementSaveCustomer.setString(2, customer.getSurname());
        preparedStatementSaveCustomer.setInt(3, customer.getAge());
        preparedStatementSaveCustomer.execute();
        rowInserted = increment(rowInserted);
        try (ResultSet rs = preparedStatementGetCustomerId.executeQuery()) {
            if (rs.next()) {
                id = new Long(rs.getInt("id"));
            }
        }
        if (jsonNode.has("contacts")) {
            JsonNode contactNode = jsonNode.get("contacts");
            Iterator<String> iterator = contactNode.fieldNames();
            while (iterator.hasNext()) {
                String fieldName = iterator.next();
                JsonNode contactType = contactNode.get(fieldName);
                if (contactType.isArray()) {
                    for (JsonNode concreteContactNode : contactType) {
                        rowInserted = saveContact(concreteContactNode, id, preparedStatementSaveContact, rowInserted);
                    }
                } else {
                    rowInserted = saveContact(contactType, id, preparedStatementSaveContact, rowInserted);
                }
            }
        }
        return rowInserted;
    }

    private static Integer saveContact(JsonNode jsonNode, Long id, PreparedStatement preparedStatementSaveContact, Integer rowInserted) throws SQLException {
        preparedStatementSaveContact.setLong(1, id);
        preparedStatementSaveContact.setString(3, jsonNode.asText());
        if (jsonNode.asText().contains("@jabber")) {
            Contacts contact = new Contacts(id, "3", jsonNode.asText());
            preparedStatementSaveContact.setInt(2, 3);
        } else if (jsonNode.asText().contains("@")) {
            Contacts contact = new Contacts(id, "1", jsonNode.asText());
            preparedStatementSaveContact.setInt(2, 1);
        } else if (jsonNode.asText().matches(PHONE_PL_REGEX)) {
            Contacts contact = new Contacts(id, "2", jsonNode.asText());
            preparedStatementSaveContact.setInt(2, 2);
        } else {
            Contacts contact = new Contacts(id, "0", jsonNode.asText());
            preparedStatementSaveContact.setInt(2, 0);
        }
        preparedStatementSaveContact.execute();
        rowInserted = increment(rowInserted);
        return rowInserted;
    }

    private static Integer increment(Integer i) {
        return i + 1;
    }
}