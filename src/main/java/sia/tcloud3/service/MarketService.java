package sia.tcloud3.service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import sia.tcloud3.dtos.requests.DesignRequest;
import sia.tcloud3.entity.Ingredient;
import sia.tcloud3.entity.Taco;
import sia.tcloud3.repositories.IngredientRepository;
import sia.tcloud3.repositories.TacoRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class MarketService {

    private final TacoRepository tacoRepository;
    private final DesignService designService;

    public MarketService(TacoRepository tacoRepository, DesignService designService) {
        this.tacoRepository = tacoRepository;
        this.designService = designService;
    }

    public List<Taco> getAvailableTacos() {
        return tacoRepository.findAllByUserId(null);
    }

    public Optional<Taco> getTaco(Long id) {
        return tacoRepository.findById(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteById(Long id) {
        tacoRepository.deleteById(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void deleteAll() {
        tacoRepository.deleteAllByUserId(null);
        List<Taco> tacos = getAvailableTacos();
        tacoRepository.deleteAll(tacos);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Taco createTaco(DesignRequest request) {
        return designService.saveDesign(request, true);
    }

    // TODO: Implement search.
}
