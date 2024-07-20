package sia.tcloud3.entity;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.*;
import org.springframework.data.rest.core.annotation.RestResource;

@Data
@Entity
@RestResource(rel = "tacos", path = "tacos")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "taco")
public class Taco {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
    @Setter(AccessLevel.NONE)
	private Long id;

	private Instant createdAt = Instant.now();
	
	@NotNull
	@Size(min = 5, message = "Name must be atleast 5 characters long")
	private String name;

    @JsonIgnore
    private Long userId;
	
////	@NotNull
////	@Size(min = 1, message = "You must choose atleast 1 ingredient")
//	@ManyToMany()
////	@JoinTable(name = "taco_ingredients", joinColumns = @JoinColumn(name = "taco_id"))
////	 @JsonIgnore
//	private List<Ingredient> ingredients;

	@ElementCollection
	private List<String> ingredientIDs;

	private BigDecimal cost;

	@JsonIgnore
	private boolean publish;
}
