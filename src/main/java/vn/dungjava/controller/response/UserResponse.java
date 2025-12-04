package vn.dungjava.controller.response;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;
import vn.dungjava.common.Gender;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonPropertyOrder({
        "id",
        "firstName",
        "lastName",
        "gender",
        "dateOfBirth",
        "username",
        "email",
        "phone"
})
public class UserResponse implements Serializable {
    private Long id;
    private String firstName;
    private String lastName;
    private Gender gender;
    private Date dateOfBirth;
    private String username;
    private String email;
    private String phone;
    private String address;
    // more
}
