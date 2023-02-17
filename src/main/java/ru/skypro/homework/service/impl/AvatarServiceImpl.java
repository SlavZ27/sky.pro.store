package ru.skypro.homework.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.entity.Avatar;
import ru.skypro.homework.entity.Image;
import ru.skypro.homework.exception.AvatarNotFoundException;
import ru.skypro.homework.exception.ImageNotFoundException;
import ru.skypro.homework.repository.AvatarRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class AvatarServiceImpl {
    private final String dirForAvatars;
    private final String pathToBackend1;
    private final AvatarRepository avatarRepository;

    public AvatarServiceImpl(@Value("${path.to.materials.folder}") String dirForImages, @Value("${path.to.backend1}") String pathToBackend1, AvatarRepository avatarRepository) {
        this.dirForAvatars = dirForImages;
        this.pathToBackend1 = pathToBackend1;
        this.avatarRepository = avatarRepository;
    }

    public List<byte[]> updateImage(Integer id, MultipartFile image) {
        return null;
    }

    public Pair<byte[], String> getAvatarData(Avatar avatar) {
        avatarRepository.findById(avatar.getId()).orElseThrow(() ->
                new AvatarNotFoundException(avatar.getId()));
        byte[] bytes;
        try {
            bytes = Files.readAllBytes(Paths.get(avatar.getPath()));
        } catch (IOException | NullPointerException e) {
            throw new AvatarNotFoundException(avatar.getPath().toString());
        }
        return Pair.of(bytes, MediaType.IMAGE_JPEG_VALUE);
    }

    public void removeAvatarWithFile(Avatar avatar) {
        try {
            Files.deleteIfExists(Path.of(avatar.getPath()));
        } catch (IOException ignored) {
        }
        avatarRepository.delete(avatar);
    }

    public Avatar addAvatar(MultipartFile file, Integer idName) throws IOException {
        byte[] data = file.getBytes();
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String extension = Optional.ofNullable(file.getOriginalFilename()).map(fileName -> fileName.substring(file.getOriginalFilename().lastIndexOf('.')))
                .orElse("");
        Path path = Paths.get(dirForAvatars).resolve("Avatar_" + idName + "_" + date + extension);
        Files.write(path, data);
        Avatar avatar = new Avatar();
        avatar.setPath(path.toString());
        avatar = avatarRepository.save(avatar);
        return avatar;
    }

    public String getLinkOfImageOfAds(Avatar avatar) {
        avatarRepository.findById(avatar.getId()).orElseThrow(
                () -> new ImageNotFoundException(avatar.getId()));
//        return pathToBackend1 + "users/me/image";
        return "/users/me/image";
    }
}
