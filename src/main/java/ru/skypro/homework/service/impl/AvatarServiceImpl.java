package ru.skypro.homework.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.entity.Avatar;
import ru.skypro.homework.exception.AvatarNotFoundException;
import ru.skypro.homework.repository.AvatarRepository;
import ru.skypro.homework.service.AvatarService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * This class handles the command associated with creating an avatar in user,
 * allowing users to create, update, receive, delete avatar.
 */
@Service
@Slf4j
public class AvatarServiceImpl implements AvatarService {
    private final String dirForAvatars;
    private final AvatarRepository avatarRepository;

    /**
     * Instantiates a new Avatar service.
     *
     * @param dirForImages     the dir for images
     * @param avatarRepository the avatar repository
     */
    public AvatarServiceImpl(
            @Value("${path.to.avatars.folder}") String dirForImages,
            AvatarRepository avatarRepository) {
        this.dirForAvatars = dirForImages;
        this.avatarRepository = avatarRepository;
    }

    /**
     * Allows update Avatar.
     * Uses {@link AvatarRepository#findById(Object)}
     * Uses {@link AvatarRepository#save(Object)}
     *
     * @param avatar   is not null
     * @param file     is not null
     * @param nameFile is nut null
     * @return {@link Avatar}
     * @throws AvatarNotFoundException if passed non id avatar
     */
    @Override
    public Avatar updateAvatar(Avatar avatar, MultipartFile file, String nameFile) {
        if (avatar == null || avatar.getId() == null) {
            log.error("An exception occurred! Cause: avatar=null or avatar.Id=null");
            throw new IllegalArgumentException();
        }
        Path pathOld = Paths.get(avatar.getPath());
        Path pathNew = generatePath(file, nameFile);
        try {
            log.debug("Try to write file by new path: {}", pathNew);
            Files.write(pathNew, file.getBytes());
            if (Files.exists(pathNew)) {
                avatar.setPath(pathNew.toString());
                avatar = avatarRepository.save(avatar);
                log.info("Avatar with ID: {} has been updated", avatar.getId());
                Files.deleteIfExists(pathOld);
            }
        } catch (IOException ignored) {
            log.error("Absent file in Avatar with id: {}", avatar.getId());
            throw new AvatarNotFoundException("Absent file in Avatar with id = " + avatar.getId());
        } catch (NullPointerException e) {
            log.error("Absent path in Avatar with id: {}", avatar.getId());
            throw new AvatarNotFoundException("Absent path in Avatar with id = " + avatar.getId());
        }
        return avatar;
    }

    /**
     * Allows get Avatar Data.
     * Uses {@link AvatarRepository#findById(Object)}
     *
     * @param avatar is not null
     * @return the pair - avatar data
     * @throws AvatarNotFoundException if passed non id avatar
     */
    @Override
    public Pair<byte[], String> getAvatarData(Avatar avatar) {
        if (avatar == null || avatar.getId() == null) {
            log.error("An exception occurred! Cause: avatar=null or avatar.Id=null");
            throw new IllegalArgumentException();
        }
        try {
            log.debug("Try to read bytes by path: {}", avatar.getPath());
            return Pair.of(Files.readAllBytes(Paths.get(avatar.getPath())), MediaType.IMAGE_JPEG_VALUE);
        } catch (IOException ignored) {
            log.error("Absent file in Avatar with id: {}", avatar.getId());
            throw new AvatarNotFoundException("Absent file in Avatar with id = " + avatar.getId());
        } catch (NullPointerException e) {
            log.error("Absent path in Avatar with id: {}", avatar.getId());
            throw new AvatarNotFoundException("Absent path in Avatar with id = " + avatar.getId());
        }
    }

    /**
     * Generate Path to file Avatar for string.
     *
     * @param file     is not null
     * @param nameFile is not null
     * @return {@link Path} with the specified data
     */
    @Override
    public Path generatePath(MultipartFile file, String nameFile) {
        return ImageServiceImpl.generatePath(file, nameFile, dirForAvatars);
    }

    /**
     * Create a new Avatar and save it to repository
     *
     * @param file     is not null
     * @param nameFile is not null
     * @return {@link Avatar}
     */
    @Override
    public Avatar addAvatar(MultipartFile file, String nameFile) throws IOException {
        byte[] data = file.getBytes();
        Path path = generatePath(file, nameFile);
        Files.write(path, data);
        Avatar avatar = new Avatar();
        avatar.setPath(path.toString());
        Avatar newAvatar = avatarRepository.save(avatar);
        log.info("New avatar with ID: {} has been added", newAvatar.getId());
        return newAvatar;
    }
}
