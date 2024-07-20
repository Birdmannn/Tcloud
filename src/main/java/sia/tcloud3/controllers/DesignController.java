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

@Slf4j
@RestController
@RequestMapping(value = "design")
public class DesignController {

    // TODO: only post /design.
    // posting a Taco Design here
    // Create a TacoDesignRequest with List of Ingredients to be added (of each type)
    // maps with the Taco class and enters the TacoDesign
    // TacoDesign class or entity consists of the userId and Taco objects
    // or thinking that TacoDesign should just store the id of the taco and the user's id?

    // Post to save a design that might either be ordered or not

    private final DesignService designService;

    public DesignController(DesignService designService, MarketService marketService) {
        this.designService = designService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Taco saveTaco(@RequestBody DesignRequest request) {
        return designService.saveDesign(request, false);
    }

    @GetMapping
    public ResponseEntity<List<Taco>> getDesigns() {
        List<Taco> tacos = designService.getDesigns();
        return ResponseEntity.ok(tacos);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAllDesigns() {
        designService.deleteAllDesigns();
        return ResponseEntity.noContent().build();
    }

    @GetMapping("{id}")
    public ResponseEntity<Taco> getTacoById(@PathVariable Long id) {
        return ResponseEntity.ok(designService.getDesignById(id));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteTacoById(@PathVariable Long id) {
        designService.deleteDesignById(id);
        return ResponseEntity.noContent().build();
    }
}
