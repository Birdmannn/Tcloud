package sia.tcloud3.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.io.IOUtils;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sia.tcloud3.entity.Image;
import sia.tcloud3.entity.Taco;
import sia.tcloud3.entity.Users;
import sia.tcloud3.repositories.ImageRepository;
import sia.tcloud3.repositories.TacoRepository;
import sia.tcloud3.repositories.UserRepository;
import sia.tcloud3.service.utils.ImageUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Optional;
import java.util.zip.DataFormatException;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageService {

    private final ImageRepository imageRepository;
    private final TacoRepository tacoRepository;

    public String uploadImage(MultipartFile imageFile, String asset, Long id) throws IOException {
        if (id != null && ! tacoRepository.existsById(id))
            throw new IllegalStateException("No Taco found with id: " + id);
        Image image = Image.builder()
                .name(imageFile.getOriginalFilename())
                .type(imageFile.getContentType())
                .imageData(ImageUtils.compressImage(imageFile.getBytes()))
                .asset(asset.toUpperCase())
                .assetId(id)
                .build();
        imageRepository.save(image);
        log.info("image save: success");
        return "file uploaded successfully: " + imageFile.getOriginalFilename();
    }

    public byte[] downloadImage(Long id) {
        Optional<Image> dbImage = imageRepository.findById(id);

        return dbImage.map(image -> {
            try {
                return ImageUtils.decompressImage(image.getImageData());
            } catch (DataFormatException | IOException e) {
                throw new IllegalStateException("Could not download image: " + image.getName());
            }
        }).orElse(null);
    }

    // TODO: Update this method to save picture with url
    public void doThis(String url, Users user, String asset) throws MalformedURLException {
        try {
            URL pictureUrl = URI.create(url).toURL();
            InputStream imageInputStream = pictureUrl.openStream();
            Image image = Image.builder()
                    .asset(asset.toUpperCase())
                    .assetId(user.getId())
                    .imageData(ImageUtils.compressImage(IOUtils.toByteArray(imageInputStream))).build();
        } catch (IOException e) {

        }
    }
}
