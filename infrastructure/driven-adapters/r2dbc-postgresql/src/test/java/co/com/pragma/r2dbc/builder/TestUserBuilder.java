package co.com.pragma.r2dbc.builder;

import co.com.pragma.model.user.User;
import co.com.pragma.r2dbc.UserRepositoryAdapterTest;

import java.util.UUID;

public class TestUserBuilder {
    private String id = UUID.randomUUID().toString();
    private String name = "Test";
    private String lastName = "User";
    private String email = "testuser@mail.com";
    private String documentNumber = "123456789";
    private String phone = "1234567890";
    private String idRol = "1";
    private int basicSalary = 1000;

    public TestUserBuilder withId(String id) {
        this.id = id;
        return this;
    }

    public TestUserBuilder withEmail(String email) {
        this.email = email;
        return this;
    }

    public User build() {
        return User.builder()
                .id(this.id)
                .name(this.name)
                .lastName(this.lastName)
                .email(this.email)
                .documentNumber(this.documentNumber)
                .phone(this.phone)
                .idRol(this.idRol)
                .basicSalary(this.basicSalary)
                .build();
    }
}
