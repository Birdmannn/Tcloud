package sia.tcloud3.service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sia.tcloud3.dtos.requests.DesignRequest;
import sia.tcloud3.entity.Ingredient;
import sia.tcloud3.entity.Taco;
import sia.tcloud3.repositories.IngredientRepository;
import sia.tcloud3.repositories.TacoRepository;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class DesignService {

    private final UserService userService;
    private final TacoRepository tacoRepository;
    private final IngredientRepository ingredientRepository;

    public DesignService(UserService userService, TacoRepository tacoRepository, IngredientRepository ingredientRepository) {
        this.userService = userService;
        this.tacoRepository = tacoRepository;
        this.ingredientRepository = ingredientRepository;
    }

    public Taco saveDesign(DesignRequest request, boolean admin) {
//        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        List<String> ingredientIds = request.getIngredientList();
//        List<Ingredient> ingredients = ingredientIds.stream().map(ingredientId -> ingredientRepository.findById(ingredientId)
//                .orElseThrow(() -> new DesignTacoException("Invalid Ingredient ID in List."))).collect(Collectors.toList());
        int cost = ingredientIds.stream().map(id -> ingredientRepository.findById(id).orElseThrow(() ->
                new DesignTacoException("A problem has been encountered whilst loading ingredients for taco cost.")))
                .mapToInt(Ingredient::getCost).sum();

        Long userId = admin ? null : userService.retrieveCurrentUser().getId();
        Taco taco = Taco.builder()
                .name(request.getName())
                .createdAt(Instant.now())
                .userId(userId)
                .ingredientIDs(ingredientIds)
                .cost(new BigDecimal(cost))
                .build();

        return tacoRepository.save(taco);
    }

    public List<Taco> getDesigns() {
        return tacoRepository.findAllByUserId(userService.retrieveCurrentUser().getId());
    }

    public Taco getDesignById(Long id) {
        return tacoRepository.findById(id).orElseThrow(() -> new DesignTacoException("Design not found."));
    }

    public void deleteDesignById(Long tacoId) {
        List<Taco> designs = getDesigns();
        Optional<Long> designId = designs.stream().map(Taco::getId).filter(id -> Objects.equals(id, tacoId)).findFirst();
        Long deleteId = designId.orElseThrow(() -> new DesignTacoException("Could not delete Design. Invalid taco Id"));
        tacoRepository.deleteById(deleteId);
    }

    public void deleteAllDesigns() {
        tacoRepository.deleteAllByUserId(userService.retrieveCurrentUser().getId());
    }

    // TODO: publish Design, no? Well, perhaps set a publish boolean variable in Taco. Till further notice.


    // ------------------------------------------ Nested Exception Class ------------------------------------------------

    public static class DesignTacoException extends RuntimeException {

        public DesignTacoException(String message) {
            super(message);
        }

        public String getMessage() {
            return super.getMessage();
        }
    }
}
