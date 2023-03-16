package ru.skypro.homework.service;

import org.springframework.data.util.Pair;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.entity.Avatar;

import java.io.IOException;
import java.nio.file.Path;


/**
 * Provides methods for avatar processing
 */
public interface AvatarService {
    /**
     * Update avatar.
     *
     * @param avatar   the avatar
     * @param file     the file
     * @param nameFile the name file
     * @return {@link Avatar}
     */
    Avatar updateAvatar(Avatar avatar, MultipartFile file, String nameFile);

    /**
     * Gets avatar data.
     *
     * @param avatar the avatar
     * @return the pair - avatar data
     */
    Pair<byte[], String> getAvatarData(Avatar avatar);

    /**
     * Generate path.
     *
     * @param file     the file
     * @param nameFile the name file
     * @return {@link Path}
     */
    Path generatePath(MultipartFile file, String nameFile);

    /**
     * Add avatar.
     *
     * @param file     the file
     * @param nameFile the name file
     * @return {@link Avatar}
     * @throws IOException the io exception
     */
    Avatar addAvatar(MultipartFile file, String nameFile) throws IOException;
}
