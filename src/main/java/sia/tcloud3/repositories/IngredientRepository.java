package sia.tcloud3.repositories;

import org.springframework.data.repository.CrudRepository;

import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import sia.tcloud3.entity.Ingredient;

@RepositoryRestResource
public interface IngredientRepository extends CrudRepository<Ingredient, String> {
    Ingredient findByName(String name);
}
