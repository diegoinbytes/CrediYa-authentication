package co.com.pragma.r2dbc.builder;

import co.com.pragma.r2dbc.UserRepositoryAdapterTest;
import co.com.pragma.r2dbc.entity.UserEntity;

import java.util.UUID;

public class TestUserEntityBuilder {
    private UUID id = UUID.randomUUID();
    private String name = "Test";
    private String lastName = "User";
    private String email = "testuser@mail.com";
    private int documentNumber = 123456789;
    private int phone = 1234567890;
    private int idRol = 1;
    private int basicSalary = 1000;

    public TestUserEntityBuilder withId(UUID id) {
        this.id = id;
        return this;
    }

    public TestUserEntityBuilder withEmail(String email) {
        this.email = email;
        return this;
    }

    public UserEntity build() {
        return UserEntity.builder()
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
