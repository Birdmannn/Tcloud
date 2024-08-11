package sia.tcloud3.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sia.tcloud3.dtos.requests.DesignRequest;
import sia.tcloud3.entity.Taco;
import sia.tcloud3.service.DesignService;
import sia.tcloud3.service.MarketService;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("market")
public class MarketController {

    private final MarketService marketService;

    public MarketController(MarketService marketService) {
        this.marketService = marketService;
    }

    // TODO: Check how searching is implemented. Return whatever is being searched. Perhaps an Object?
    @GetMapping
    public ResponseEntity<List<Taco>> getAvailableTacos(@RequestParam("search") String keyword,
                                                        @RequestParam(value = "sort", defaultValue = "placedAt") String sort,
                                                        @RequestParam(value = "size", defaultValue = "25") int size) {
        List<Taco> tacos = marketService.getAvailableTacos();
        return tacos.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(tacos);
    }

    @GetMapping("{id}")
    public ResponseEntity<Taco> getTacoById(@PathVariable Long id) {
       Optional<Taco> taco = marketService.getTaco(id);
       return taco.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) {
        marketService.deleteById(id);
    }

    @DeleteMapping
    public void deleteAll() {
        marketService.deleteAll();
    }

    @PostMapping
    public ResponseEntity<Taco> createTaco(@RequestBody DesignRequest request) {
        Taco taco = marketService.createTaco(request);
        return new ResponseEntity<>(taco, HttpStatus.CREATED);
    }
}
