package sia.tcloud3.dtos.response;

import lombok.*;
import lombok.experimental.FieldDefaults;
import sia.tcloud3.entity.Users.Role;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class GetUserResponse {
    String email;
    String firstName;
    String lastName;
    Role role;
    String username;
    String street;
    String city;
    String state;
    String zip;
    String phoneNumber;
}
