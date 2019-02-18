import lombok.Data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by Radosław Kokoszka on 18.02.2019 12:06
 */

@Data
public class StatementFactory implements AutoCloseable {
    private static StatementFactory instance = null;
    private static final String FOR_SAVING_CUSTOMER = "insert into customers (name, surname, age) value (?,?,?)";
    private static final String FOR_GETTING_CUSTOMER_ID = "select id from customers order by id desc";
    private static final String FOR_SAVING_CONTACT = "insert into contacts (id_customer, type, contact) value (?,?,?)";
    private PreparedStatement preparedStatementSaveCustomer;
    private PreparedStatement preparedStatementGetCustomerId;
    private PreparedStatement preparedStatementSaveContact;

    private StatementFactory() {}

    public static StatementFactory getInstance(Connection connection) throws SQLException {
        if (instance != null) {
            return instance;
        }
        instance = new StatementFactory();
        PreparedStatement preparedStatementSaveCustomer = connection.prepareStatement(FOR_SAVING_CUSTOMER);
        PreparedStatement preparedStatementGetCustomerId = connection.prepareStatement(FOR_GETTING_CUSTOMER_ID);
        PreparedStatement preparedStatementSaveContact = connection.prepareStatement(FOR_SAVING_CONTACT);
        instance.setPreparedStatementSaveCustomer(preparedStatementSaveCustomer);
        instance.setPreparedStatementGetCustomerId(preparedStatementGetCustomerId);
        instance.setPreparedStatementSaveContact(preparedStatementSaveContact);
        return instance;
    }

    @Override
    public void close() throws Exception {
        preparedStatementGetCustomerId.close();
        preparedStatementSaveContact.close();
        preparedStatementSaveCustomer.close();
    }
}
