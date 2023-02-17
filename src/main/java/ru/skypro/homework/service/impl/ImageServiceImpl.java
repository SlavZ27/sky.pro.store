package ru.skypro.homework.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.entity.Ads;
import ru.skypro.homework.entity.Image;
import ru.skypro.homework.exception.ImageNotFoundException;
import ru.skypro.homework.repository.ImageRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class ImageServiceImpl {
    private final String dirForImages;
    private final String pathToBackend1;
    private final ImageRepository imageRepository;

    public ImageServiceImpl(@Value("${path.to.materials.folder}") String dirForImages, @Value("${path.to.backend1}") String pathToBackend1, ImageRepository imageRepository) {
        this.dirForImages = dirForImages;
        this.pathToBackend1 = pathToBackend1;
        this.imageRepository = imageRepository;
    }

    public List<byte[]> updateImage(Integer id, MultipartFile image) {
        return null;
    }

    public Pair<byte[], String> getImage(Integer idImage) {
        Image image = imageRepository.findById(idImage).orElseThrow(() -> new ImageNotFoundException(idImage));
        byte[] bytes;
        try {
            bytes = Files.readAllBytes(Paths.get(image.getPath()));
        } catch (IOException | NullPointerException e) {
            throw new ImageNotFoundException(idImage);
        }
        return Pair.of(bytes, MediaType.IMAGE_JPEG_VALUE);
    }

    public void removeImageWithFile(Image image) {
        try {
            Files.deleteIfExists(Path.of(image.getPath()));
        } catch (IOException ignored) {
        }
        imageRepository.delete(image);
    }

    public void removeAllImagesOfAds(Integer idAds) {
        List<Image> imageList = getAllByIdAds(idAds);
        for (Image image : imageList) {
            removeImageWithFile(image);
        }
    }

    public List<Image> getAllByIdAds(Integer idAds) {
        return imageRepository.findAllByIdAds(idAds);
    }
    public String getLinkOfImageOfAds(Integer idImage) {
        Image image = imageRepository.findById(idImage).orElseThrow(() -> new ImageNotFoundException(idImage));
        String link = pathToBackend1 + "image/" + image.getId();
        return link;
    }



    public Image addImage(Ads ads, MultipartFile file) throws IOException {
        byte[] data = file.getBytes();
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String extension = Optional.ofNullable(file.getOriginalFilename()).map(fileName -> fileName.substring(file.getOriginalFilename().lastIndexOf('.')))
                .orElse("");

        Path path = Paths.get(dirForImages).resolve("Ads_" + ads.getId() + "_" + date + extension);
        Files.write(path, data);

        Image image = new Image();
        image.setPath(path.toString());
        image.setAds(ads);

        imageRepository.save(image);
        return image;
    }
}
