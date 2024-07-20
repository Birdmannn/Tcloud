package sia.tcloud3.entity;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.experimental.FieldDefaults;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.*;

@Entity
@Data
@Builder
@NoArgsConstructor(access = AccessLevel.PUBLIC, force = true)
@AllArgsConstructor
//@RestResource(rel = "users", path = "user")
public class Users implements UserDetails {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotEmpty(message = "Email address is required.")
	@Email(message = "The email address is invalid.", flags = {Pattern.Flag.CASE_INSENSITIVE})
	private String email;

	@JsonIgnore
	private String password;

//	@NotEmpty(message = "First name is required.")
//	@Size(min = 2, max = 50, message = "The length of first name must be between 2 and 50.")
	private String firstName;

//	@NotEmpty(message = "Last Name is required.")
//	@Size(min = 2, max = 50, message = "The length of last name must be between 2 and 50")
	private String lastName;

	public Users(String email, String password, String firstName, String lastName, Role role) {
		this.email = email;
		this.password = password;
		this.firstName = firstName;
		this.lastName = lastName;
		this.role = role;
	}

	// have a userRepository for usernames, load all into the client's memory and check for matching
	// usernames, then approve.
	private String username;
	private String street;
	private String city;
	private String state;
	private String zip;
	private String phoneNumber;

	@Enumerated(EnumType.STRING)
	private Role role;

	public enum Role {USER, ADMIN}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// return collection of authorities granted to the user
        assert role != null;
        return Collections.singletonList(new SimpleGrantedAuthority(role.name()));
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return email;
	}
}
