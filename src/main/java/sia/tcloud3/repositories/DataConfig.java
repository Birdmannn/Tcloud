package sia.tcloud3.repositories;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import sia.tcloud3.entity.Ingredient;
import sia.tcloud3.entity.Ingredient.Type;
import sia.tcloud3.entity.Taco;
import sia.tcloud3.entity.TacoOrder;

@Configuration
public class DataConfig {

    @Bean
	ApplicationRunner dataLoader(IngredientRepository repo, TacoRepository tacoRepo,
								 OrderRepository orderRepo) {

    	return args -> {
    		Ingredient flourTortilla = new Ingredient("FLTO", "Flour Tortilla", Type.WRAP, 1000);
    		Ingredient cornTortilla = new Ingredient("COTO", "Corn Tortilla", Type.WRAP, 700);
    		Ingredient groundBeef = new Ingredient("GRBF", "Ground Beef", Type.PROTEIN, 2000);
    		Ingredient carnitas = new Ingredient("CARN", "Carnitas", Type.PROTEIN, 600);
    		Ingredient tomatoes = new Ingredient("TMTO", "Diced Tomatoes", Type.VEGGIES, 1500);
    		Ingredient lettuce = new Ingredient("LETC", "Lettuce", Type.VEGGIES, 1200);
    		Ingredient cheddar = new Ingredient("CHED", "Cheddar", Type.CHEESE, 1800);
    		Ingredient jack = new Ingredient("JACK", "Monterrey Jack", Type.CHEESE, 800);
    		Ingredient salsa = new Ingredient("SLSA", "Salsa", Type.SAUCE, 950);
    		Ingredient sourCream = new Ingredient("SRCR", "Sour Cream", Type.SAUCE, 1100);

            List<Ingredient> ingredients = new ArrayList<>(Arrays.asList(flourTortilla, cornTortilla,
                    groundBeef, carnitas, tomatoes, lettuce, cheddar, jack, salsa, sourCream));

            repo.saveAll(ingredients);


//    		Taco taco1 = new Taco();
//    		taco1.setName("Deletethis");
//    		taco1.setIngredientIDs(Arrays.asList(flourTortilla.getId(), groundBeef.getId(), carnitas.getId(), sourCream.getId(),
//					salsa.getId(), cheddar.getId()));
//    		tacoRepo.save(taco1);
//
//    		Taco taco2 = new Taco();
//    		taco2.setName("Bovine Bounty");
//    		taco2.setCreatedAt(time);
//    		taco2.setIngredients(Arrays.asList(cornTortilla, groundBeef, cheddar, jack, sourCream));
//    		tacoRepo.save(taco2);
//
//    		Taco taco3 = new Taco();
//    		taco3.setName("Veg-Out");
//    		taco3.setCreatedAt(time);
//    		taco3.setIngredients(Arrays.asList(flourTortilla, cornTortilla, tomatoes, lettuce, salsa));
//    		tacoRepo.save(taco3);

//    		TacoOrder order = new TacoOrder();
//    		order.setDeliveryName("Test Name");
//    		order.setDeliveryCity("Test city");
//    		order.setDeliveryState("Test State");
//    		order.setDeliveryStreet("Test street");
//    		order.setDeliveryZip("840102");
//    		order.setCcExpiration("07/24");

//			order.addTacos(taco1);
//			order.addTacos(taco2);
//			order.addTacos(taco3);
//
//			orderRepo.save(order);

    	};

    }

//	@Bean
//	ApplicationListener<ApplicationStartedEvent> dataInitializer(UserRepository userRepository,
//																 AuthenticationService authenticationService) {
//
//		return event -> {
//            if (! userRepository.existsByRole(Users.Role.ADMIN)) {
//				SignUpRequest admin = new SignUpRequest();
//				admin.setEmail("admin@gmail.com");
//				admin.setPassword("admin");
//				admin.setFirstName("pascal");
//				admin.setLastName("torti");
//                Users adminUser = new Users("admin@gmail.com", "admin",
//                        "pascal", "torti", Users.Role.ADMIN);
//                userRepository.save(adminUser);
//				authenticationService.signUp(admin);
//            }
//        };
//	}

//    @Bean
//    DataSource dataSource() {
//    	return DataSourceBuilder.create()
//    			.driverClassName("org.h2.Driver")
//    			.url("jdbc:h2:mem:tacocloud;DB_CLOSE_ON_EXIT=false;MV_STORE=false")
//    			.username("sa")
//    			.password("")
//    			.build();
//    }
}