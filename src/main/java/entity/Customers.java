package entity;

import lombok.Data;

/**
 * Created by Radosław Kokoszka on 16.02.2019 16:26
 */

@Data
public class Customers {

    private Long id;
    private String name;
    private String surname;
    private int age;

    public Customers(String name, String surname, String age) {
        this.id = null;
        this.name = name;
        this.surname = surname;
        if (age.matches("\\d+")) this.age = Integer.parseInt(age);
        else this.age = 0;
    }
}
