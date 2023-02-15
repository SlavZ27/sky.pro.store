package ru.skypro.homework.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.entity.Image;
import ru.skypro.homework.exception.ImageNotFoundException;
import ru.skypro.homework.repository.ImageRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class ImageServiceImpl {
    private final String dirForImages;
    private final ImageRepository imageRepository;

    public ImageServiceImpl(@Value("${path.to.materials.folder}") String dirForImages, ImageRepository imageRepository) {
        this.dirForImages = dirForImages;
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

}
