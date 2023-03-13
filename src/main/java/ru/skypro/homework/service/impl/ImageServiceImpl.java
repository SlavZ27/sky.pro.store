package ru.skypro.homework.service.impl;

import lombok.extern.slf4j.Slf4j;
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
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * This class processes commands related create Image allowing users to create, update, get, delete ads.
 */
@Service
@Slf4j
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
     *
     * @param image    is not null
     * @param file     is not null
     * @param nameFile is not null
     * @return oldImage
     */
    public Image updateImage(Image image, MultipartFile file, String nameFile) {
        if (image == null || image.getId() == null) {
            log.error("An exception occurred! Cause: image=null or image.Id=null");
            throw new IllegalArgumentException();
        }
        Path pathOld = Paths.get(image.getPath());
        Path pathNew = generatePath(file, nameFile);
        try {
            log.info("Try to write file by new path: {}", pathNew);
            Files.write(pathNew, file.getBytes());
            if (Files.exists(pathNew)) {
                image.setPath(pathNew.toString());
                image = imageRepository.save(image);
                log.info("Image with ID: {} has been updated", image.getId());
                Files.deleteIfExists(pathOld);
            }
        } catch (IOException ignored) {
            log.error("Absent file in Avatar with id: {}", image.getId());
            throw new ImageNotFoundException("Absent file in Image with id = " + image.getId());
        } catch (NullPointerException e) {
            log.error("Absent path in Avatar with id: {}", image.getId());
            throw new ImageNotFoundException("Absent path in Image with id = " + image.getId());
        }
        return image;
    }

    /**
     * This method, uses method repository, get image from data
     * Uses {@link ImageRepository#findById(Object)}
     *
     * @param image is not null
     * @return image data
     * @throws ImageNotFoundException if passed non id comment
     */
    public Pair<byte[], String> getImageData(Image image) {
        if (image == null || image.getId() == null) {
            log.error("An exception occurred! Cause: image=null or image.Id=null");
            throw new IllegalArgumentException();
        }
        try {
            log.info("Try to read bytes by path: {}", image.getPath());
            return Pair.of(Files.readAllBytes(Paths.get(image.getPath())), MediaType.IMAGE_JPEG_VALUE);
        } catch (IOException ignored) {
            log.error("Absent file in Image with id: {}", image.getId());
            throw new ImageNotFoundException("Absent file in Image with id = " + image.getId());
        } catch (NullPointerException e) {
            log.error("Absent path in Image with id: {}", image.getId());
            throw new ImageNotFoundException("Absent path in Image with id = " + image.getId());
        }
    }

    /**
     * This method, uses method repository, get image by id image
     * Uses {@link ImageRepository#findById(Object)}
     *
     * @param id is not null
     * @return image
     * @throws ImageNotFoundException if passed non id comment
     */
    public Image getImage(Integer id) {
        if (id == null) {
            log.error("An exception occurred! Cause: image.Id=null");
            throw new IllegalArgumentException();
        }
        return imageRepository.findById(id).orElseThrow(() -> {
            log.error("Image with ID: {} not found", id);
            return new ImageNotFoundException(id);
        });
    }

    /**
     * This method, uses method repository, del image by file
     * Uses {@link ImageRepository#delete(Object)}
     * Uses {@link ImageRepository#findById(Object)}
     *
     * @param image is not null
     */
    public boolean removeImageWithFile(Image image) {
        Path path = Path.of(image.getPath());
        try {
            Files.deleteIfExists(path);
            log.info("Try to delete path = {} if exists", path);
        } catch (IOException ignored) {
            log.error("Something wrong with image path!");
        }
        imageRepository.delete(image);
        log.info("Image with ID: {} have been deleted", image.getId());
        return !Files.exists(path) && imageRepository.findById(image.getId()).isEmpty();
    }

    /**
     * This method generate Path to file image for string.
     *
     * @param file     is not null
     * @param nameFile us not null
     * @return Patch with the specified data
     */
    public Path generatePath(MultipartFile file, String nameFile) {
        return generatePath(file, nameFile, dirForImages);
    }

    public static Path generatePath(MultipartFile file, String nameFile, String dirForImages) {
        String splitter = "-";
        String extension;
        if (file.getOriginalFilename() == null) {
            extension = ".jpg";
        } else {
            extension = Optional.ofNullable(file.getOriginalFilename())
                    .map(fileName -> fileName.substring(file.getOriginalFilename().lastIndexOf('.')))
                    .orElse("");
        }
        int count = 1;
        Path result = Paths.get(dirForImages);
        String way;
        do {
            way = nameFile + splitter + count++ + extension;
        } while (Files.exists(result.resolve(way)));
        return result.resolve(way);
    }

    /**
     * This method, uses method repository, add image
     * Uses {@link ImageRepository#save(Object)}
     *
     * @param file     is not null
     * @param nameFile is not null
     * @return image
     */
    public Image addImage(MultipartFile file, String nameFile) throws IOException {
        byte[] data = file.getBytes();
        Path path = generatePath(file, nameFile);
        Files.write(path, data);
        Image image = new Image();
        image.setPath(path.toString());
        image.setId(null);
        Image newImage = imageRepository.save(image);
        log.info("New image with ID: {} has been added", newImage.getId());
        return newImage;
    }
}
