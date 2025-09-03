package co.com.pragma.model.user;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
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
