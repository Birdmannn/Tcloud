package sia.tcloud3.entity;

import java.math.BigDecimal;
import java.util.*;
import java.io.Serializable;
import java.time.Instant;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.CreditCardNumber;
import org.springframework.data.rest.core.annotation.RestResource;

@Data
@Entity
@RestResource(rel = "taco-orders", path = "taco-orders")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TacoOrder implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private Long id;
	private Instant placedAt;

	private Long userId;

	@NotBlank(message = "*Delivery name is required")
	private String deliveryName;
	
	@NotBlank(message = "*Street is required")
	private String deliveryStreet;
	
	@NotBlank(message = "*City is required")
	private String deliveryCity;
	
	@NotBlank(message = "*State is required")
	private String deliveryState;
	
	@NotBlank(message = "*Zip code is required")
	private String deliveryZip;

	private BigDecimal cost;

//	@CreditCardNumber(message = "*Not a valid credit card number")
//	private String ccNumber;
//
//	@Pattern(regexp = "^(0[1-9]|1[0-2])([/])([2-9][0-9])", message = "*Must be formatted MM/YY")
//	private String ccExpiration;	// http://www.regularexpressions.info/
//
//	@Digits(integer = 3, fraction = 0, message = "*Invalid CVV")
//	private String ccCVV;
//
	@ElementCollection
	private List<Long> tacoIds;
	
}