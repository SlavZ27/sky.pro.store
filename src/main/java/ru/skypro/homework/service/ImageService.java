package ru.skypro.homework.service;

import org.springframework.data.util.Pair;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.entity.Image;

import java.io.IOException;
import java.nio.file.Path;

public interface ImageService {
    Image updateImage(Image image, MultipartFile file, String nameFile);

    Pair<byte[], String> getImageData(Image image);

    Image getImage(Integer id);

    boolean removeImageWithFile(Image image);

    Path generatePath(MultipartFile file, String nameFile);

    Image addImage(MultipartFile file, String nameFile) throws IOException;
}
