package sia.tcloud3.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import lombok.*;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC, force = true)
public class Ingredient {

	@Id
	private final String id;
	private final String name;
	private final Type type;
	private final Integer cost;

	public enum Type {
		WRAP, PROTEIN, VEGGIES, CHEESE, SAUCE
	}
}
