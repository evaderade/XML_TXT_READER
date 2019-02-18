package entity;

import lombok.Data;

/**
 * Created by Radosław Kokoszka on 16.02.2019 16:27
 */

@Data
public class Contacts {

    private Long id;
    private Long id_customer;
    private int type;
    private String contact;

    public Contacts(Long id_customer, String type, String contact) {
        this.id = null;
        this.id_customer = id_customer;
        this.type = Integer.parseInt(type);
        this.contact = contact;
    }
}
