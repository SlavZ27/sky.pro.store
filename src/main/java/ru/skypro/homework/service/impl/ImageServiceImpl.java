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
import java.time.LocalDate;
import java.util.Optional;

/**
 * This class processes commands related create Image allowing users to create, update, get, delete ads.
 */
@Service
public class ImageServiceImpl {
    private final String dirForImages;
    private final ImageRepository imageRepository;

    public ImageServiceImpl(@Value("${path.to.materials.folder}") String dirForImages,
                            ImageRepository imageRepository) {
        this.dirForImages = dirForImages;
        this.imageRepository = imageRepository;
    }

    /**
     * This method, uses method repository, update image
     * Uses {@link ImageRepository#findById(Object)}
     * Uses {@link ImageRepository#save(Object)}
     * @param image is not null
     * @param file is not null
     * @param nameFile is not null
     * @return oldImage
     */
    public Image updateImage(Image image, MultipartFile file, String nameFile) {
        if (image == null || image.getId() == null) {
            return null;
        }
        Image oldImage = imageRepository.findById(image.getId()).orElseThrow(() ->
                new ImageNotFoundException(image.getId()));
        Path pathOld = Paths.get(oldImage.getPath());
        Path pathNew = generatePath(file, nameFile);
        try {
            Files.write(pathNew, file.getBytes());
            if (Files.exists(pathNew)) {
                Files.deleteIfExists(pathOld);
                image.setPath(pathNew.toString());
                imageRepository.save(image);
            }
        } catch (IOException ignored) {
            throw new ImageNotFoundException("Absent file in Image with id = " + image.getId());
        } catch (NullPointerException e) {
            throw new ImageNotFoundException("Absent path in Image with id = " + image.getId());
        }
        return oldImage;
    }

    /**
     * This method, uses method repository, get image from data
     * Uses {@link ImageRepository#findById(Object)}
     * @param image is not null
     * @return image data
     * @throws ImageNotFoundException if passed non id comment
     */
    public Pair<byte[], String> getImageData(Image image) {
        if (image == null || image.getId() == null) {
            return null;
        }
        imageRepository.findById(image.getId()).orElseThrow(() ->
                new ImageNotFoundException(image.getId()));
        byte[] bytes;
        try {
            bytes = Files.readAllBytes(Paths.get(image.getPath()));
        } catch (IOException ignored) {
            throw new ImageNotFoundException("Absent file in Image with id = " + image.getId());
        } catch (NullPointerException e) {
            throw new ImageNotFoundException("Absent path in Image with id = " + image.getId());
        }
        return Pair.of(bytes, MediaType.IMAGE_JPEG_VALUE);
    }

    /**
     * This method, uses method repository, get image by id image
     * Uses {@link ImageRepository#findById(Object)}
     * @param id is not null
     * @return image
     * @throws ImageNotFoundException if passed non id comment
     */
    public Image getImage(Integer id) {
        if (id == null) {
            return null;
        }
        return imageRepository.findById(id).orElseThrow(() ->
                new ImageNotFoundException(id));
    }

    /**
     * This method, uses method repository, del image by file
     * Uses {@link ImageRepository#delete(Object)}
     * Uses {@link ImageRepository#findById(Object)}
     * @param image is not null
     * @return false or true
     */
    public boolean removeImageWithFile(Image image) {
        Path path = Path.of(image.getPath());
        try {
            Files.deleteIfExists(path);
        } catch (IOException ignored) {
        }
        imageRepository.delete(image);
        if (Files.exists(path) || imageRepository.findById(image.getId()).isPresent()) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * This method generate Path to file image for string.
     * @param file is not null
     * @param nameFile us not null
     * @return Patch with the specified data
     */
    private Path generatePath(MultipartFile file, String nameFile) {
        String date = LocalDate.now().toString();
        String extension = Optional.ofNullable(file.getOriginalFilename())
                .map(fileName -> fileName.substring(file.getOriginalFilename().lastIndexOf('.')))
                .orElse("");
        return Paths.get(dirForImages).resolve(nameFile + "_" + date + extension);
    }

    /**
     * This method, uses method repository, add image
     * Uses {@link ImageRepository#save(Object)}
     * @param file is not null
     * @param nameFile is not null
     * @return image
     * @throws IOException
     */
    public Image addImage(MultipartFile file, String nameFile) throws IOException {
        byte[] data = file.getBytes();
        Path path = generatePath(file, nameFile);
        Files.write(path, data);
        Image image = new Image();
        image.setPath(path.toString());
        image = imageRepository.save(image);
        return image;
    }
}
