package solution.clear.test.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import java.time.LocalDate;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;
    
    @Email
    @NotBlank
    private String email;
    
    @NotBlank
    private String firstName;
    
    @NotBlank
    private String lastName;
    
    @NotNull
    @Past
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate birthday;
    
    private String address;
    
    private String phone;

    
    @Override
    public int hashCode() {
        return Objects.hash(email, firstName, lastName, birthday, address, phone);
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass())
            return false;
        User user = (User) obj;
        return this == obj || Objects.equals(email, user.email) && 
                compareMeaningfulStringContent(firstName, user.firstName) &&
                compareMeaningfulStringContent(lastName, user.lastName) &&
                Objects.equals(birthday, user.birthday) &&
                compareMeaningfulStringContent(address, user.address) && 
                compareMeaningfulStringContent(phone, user.phone);
    }
    
    
    private static boolean compareMeaningfulStringContent(String s1, String s2) {
        s1 = s1 == null || s1.isBlank() ? "" : s1.trim();
        s2 = s2 == null || s2.isBlank() ? "" : s2.trim();
        return s1.equals(s2);
    }
    
}
