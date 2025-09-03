package co.com.bancolombia.model.user;
import lombok.*;

@Data
@Builder
public class User {
    private String id;
    private String name;
    private String lastName;
    private String email;
    private String documentNumber;
    private String phone;
    private String idRol;
    private int basicSalary;
}
