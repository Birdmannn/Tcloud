package sia.tcloud3.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sia.tcloud3.service.ImageService;

import java.io.IOException;

@RestController
@RequestMapping("images")
@RequiredArgsConstructor
public class ImageController {
    private final ImageService imageService;

    @PostMapping
    public ResponseEntity<?> uploadImage(@RequestParam("image") MultipartFile file,
                                         @RequestParam(value = "asset", defaultValue = "taco") String asset,
                                         @RequestParam(value = "id", defaultValue = "null") Long id) throws IOException {
        String uploadImage = imageService.uploadImage(file, asset, id);
        return ResponseEntity.ok(uploadImage);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> downloadImage(@PathVariable Long id) {
        byte[] imageData = imageService.downloadImage(id);
        return ResponseEntity.ok()
                .contentType(MediaType.valueOf(MediaType.IMAGE_PNG_VALUE))
                .body(imageData);
    }
}
