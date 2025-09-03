package co.com.pragma.r2dbc.entity;

import lombok.*;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.annotation.Id;

import java.util.UUID;


@Table("app_user")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserEntity {
    @Id
    private UUID id;
    @Column("first_name")
    private String name;
    private String lastName;
    private String email;
    @Column("document_id")
    private int documentNumber;
    private int phone;
    @Column("role_id")
    private int idRol;
    private int basicSalary;
}
