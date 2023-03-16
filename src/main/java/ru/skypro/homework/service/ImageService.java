package ru.skypro.homework.service;

import org.springframework.data.util.Pair;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.entity.Image;

import java.io.IOException;
import java.nio.file.Path;


/**
 * Provides methods for image processing
 */
public interface ImageService {
    /**
     * Update image.
     *
     * @param image    the image
     * @param file     the file
     * @param nameFile the name file
     * @return {@link Image}
     */
    Image updateImage(Image image, MultipartFile file, String nameFile);

    /**
     * Gets image data.
     *
     * @param image the image
     * @return the pair - image data
     */
    Pair<byte[], String> getImageData(Image image);

    /**
     * Gets image.
     *
     * @param id the id
     * @return {@link Image}
     */
    Image getImage(Integer id);

    /**
     * Remove image with file.
     *
     * @param image the image
     * @return the boolean
     */
    boolean removeImageWithFile(Image image);

    /**
     * Generate path.
     *
     * @param file     the file
     * @param nameFile the name file
     * @return {@link Path}
     */
    Path generatePath(MultipartFile file, String nameFile);

    /**
     * Add image.
     *
     * @param file     the file
     * @param nameFile the name file
     * @return {@link Image}
     * @throws IOException the io exception
     */
    Image addImage(MultipartFile file, String nameFile) throws IOException;
}
