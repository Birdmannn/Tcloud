package sia.tcloud3.dtos.requests;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class UserUpdateRequest {
    String password;
    String firstName;
    String lastName;
    String street;
    String city;
    String state;
    String zip;
    String phoneNumber;
}
