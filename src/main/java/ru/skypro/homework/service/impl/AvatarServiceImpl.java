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
    private final AvatarRepository avatarRepository;

    public AvatarServiceImpl(
            @Value("${path.to.avatars.folder}") String dirForImages,
            AvatarRepository avatarRepository) {
        this.dirForAvatars = dirForImages;
        this.avatarRepository = avatarRepository;
    }

    public Avatar updateAvatar(Avatar avatar, MultipartFile file, String nameFile) {
        if (avatar == null || avatar.getId() == null) {
            return null;
        }
        Avatar oldAvatar = avatarRepository.findById(avatar.getId()).orElseThrow(() ->
                new AvatarNotFoundException(avatar.getId()));
        Path pathOld = Paths.get(oldAvatar.getPath());
        Path pathNew = generatePath(file, nameFile);
        try {
            Files.write(pathNew, file.getBytes());
            if (Files.exists(pathNew)) {
                Files.deleteIfExists(pathOld);
                avatar.setPath(pathNew.toString());
                avatarRepository.save(avatar);
            }
        } catch (IOException ignored) {
            throw new AvatarNotFoundException("Absent file in Avatar with id = " + avatar.getId());
        } catch (NullPointerException e) {
            throw new AvatarNotFoundException("Absent path in Avatar with id = " + avatar.getId());
        }
        return oldAvatar;
    }

    public Pair<byte[], String> getAvatarData(Avatar avatar) {
        if (avatar == null || avatar.getId() == null) {
            return null;
        }
        avatarRepository.findById(avatar.getId()).orElseThrow(() ->
                new AvatarNotFoundException(avatar.getId()));
        byte[] bytes;
        try {
            bytes = Files.readAllBytes(Paths.get(avatar.getPath()));
        } catch (IOException ignored) {
            throw new AvatarNotFoundException("Absent file in Avatar with id = " + avatar.getId());
        } catch (NullPointerException e) {
            throw new AvatarNotFoundException("Absent path in Avatar with id = " + avatar.getId());
        }
        return Pair.of(bytes, MediaType.IMAGE_JPEG_VALUE);
    }

    private boolean removeAvatarWithFile(Avatar avatar) {
        Path path = Path.of(avatar.getPath());
        try {
            Files.deleteIfExists(path);
        } catch (IOException ignored) {
        }
        avatarRepository.delete(avatar);
        if (Files.exists(path) || avatarRepository.findById(avatar.getId()).isPresent()) {
            return false;
        } else {
            return true;
        }
    }

    private Path generatePath(MultipartFile file, String nameFile) {
        String date = LocalDate.now().toString();
        String extension = Optional.ofNullable(file.getOriginalFilename())
                .map(fileName -> fileName.substring(file.getOriginalFilename().lastIndexOf('.')))
                .orElse("");
        return Paths.get(dirForAvatars).resolve(nameFile + "_" + date + extension);
    }

    public Avatar addAvatar(MultipartFile file, String nameFile) throws IOException {
        byte[] data = file.getBytes();
        Path path = generatePath(file, nameFile);
        Files.write(path, data);
        Avatar avatar = new Avatar();
        avatar.setPath(path.toString());
        return avatarRepository.save(avatar);
    }

    public String getLinkOfAvatar(Avatar avatar) {
        if (avatar == null || avatar.getId() == null) {
            return null;
        }
        avatarRepository.findById(avatar.getId()).orElseThrow(
                () -> new AvatarNotFoundException(avatar.getId()));
        return "/users/me/image";
    }
}
