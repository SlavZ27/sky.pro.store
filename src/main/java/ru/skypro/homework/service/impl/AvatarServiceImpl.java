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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Optional;

/**
 * This class handles the command associated with creating an avatar in user, allowing users to create, update, receive, delete avatar.
 */
@Slf4j
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

    /**
     * This method, used method repository, allows update Avatar.
     * Uses {@link AvatarRepository#findById(Object)}
     * Uses {@link AvatarRepository#save(Object)}
     * @param avatar is not null
     * @param file is not null
     * @param nameFile is nut null
     * @return Avatar
     * @throws AvatarNotFoundException if passed non id avatar
     */
    public Avatar updateAvatar(Avatar avatar, MultipartFile file, String nameFile) {
        log.debug("Method update Avatar was invoked");
        if (avatar == null || avatar.getId() == null) {
            return null;
        }
        Avatar oldAvatar = avatarRepository.findById(avatar.getId()).orElseThrow(() ->{
            log.error("There is not Avatar with id = " + avatar.getId());
                return new AvatarNotFoundException(avatar.getId());
        });
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

    /**
     * This method, used method repository, allows get Avatar Data.
     * Uses {@link AvatarRepository#findById(Object)}
     * @param avatar is not null
     * @return Avatar
     * @throws AvatarNotFoundException if passed non id avatar
     */
    public Pair<byte[], String> getAvatarData(Avatar avatar) {
        log.debug("Method getAvatarDate Avatar was invoked");
        if (avatar == null || avatar.getId() == null) {
            return null;
        }
        avatarRepository.findById(avatar.getId()).orElseThrow(() ->{
                log.error("There is not Avatar with id = " + avatar.getId());
                return new AvatarNotFoundException(avatar.getId());
        });
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

    /**
     * This method, used method repository, allows get Image.
     * Uses {@link AvatarRepository#findById(Object)}
     * @param id is not null
     * @return Image or null
     * @throws AvatarNotFoundException if passed non id avatar
     */
    public Avatar getImage(Integer id) {
        log.debug("Method getImage Avatar was invoked");
        if (id == null) {
            return null;
        }
        return avatarRepository.findById(id).orElseThrow(() ->{
            log.error("There is not Avatar with id = " + id);
               return new AvatarNotFoundException(id);
        });
    }

    /**
     * This method, used method repository, remove Avatar with file
     * Uses {@link AvatarRepository#delete(Object)}
     * Uses {@link AvatarRepository#findById(Object)}
     * @param avatar is not null
     * @return true or false
     */
    private boolean removeAvatarWithFile(Avatar avatar) {
        log.debug("Method getImage Avatar was invoked");
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

    /**
     * This method generate Path to file Avatar for string.
     * @param file is not null
     * @param nameFile us not null
     * @return Patch with the specified data
     */
    private Path generatePath(MultipartFile file, String nameFile) {
        log.debug("Method generatePath Avatar was invoked");
        String date = LocalDate.now().toString();
        String extension = Optional.ofNullable(file.getOriginalFilename())
                .map(fileName -> fileName.substring(file.getOriginalFilename().lastIndexOf('.')))
                .orElse("");
        return Paths.get(dirForAvatars).resolve(nameFile + "_" + date + extension);
    }

    /**
     * This method, used method repository, add Avatar.
     * @param file is not null
     * @param nameFile is not null
     * @return Avatar
     * @throws IOException
     */
    public Avatar addAvatar(MultipartFile file, String nameFile) throws IOException {
        log.debug("Method addAvatar was invoked");
        byte[] data = file.getBytes();
        Path path = generatePath(file, nameFile);
        Files.write(path, data);
        Avatar avatar = new Avatar();
        avatar.setPath(path.toString());
        return avatarRepository.save(avatar);
    }

    /**
     * This method, used method repository, get link of Avatar.
     * {@link AvatarRepository#findById(Object)}
     * @param avatar is not null
     * @return lint of Avatar.
     * @throws AvatarNotFoundException if passed non id avatar
     */
    public String getLinkOfAvatar(Avatar avatar) {
        log.debug("Method getLinkOfAvatar was invoked");
        if (avatar == null || avatar.getId() == null) {
            return null;
        }
        avatarRepository.findById(avatar.getId()).orElseThrow(
                () ->{
                    log.error("There is not Avatar with id = " + avatar.getId());
                   return new AvatarNotFoundException(avatar.getId());
                });
        return "/users/me/image";
    }


}
