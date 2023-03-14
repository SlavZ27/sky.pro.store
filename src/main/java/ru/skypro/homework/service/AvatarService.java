package ru.skypro.homework.service;

import org.springframework.data.util.Pair;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.entity.Avatar;

import java.io.IOException;
import java.nio.file.Path;

public interface AvatarService {
    Avatar updateAvatar(Avatar avatar, MultipartFile file, String nameFile);

    Pair<byte[], String> getAvatarData(Avatar avatar);

    Path generatePath(MultipartFile file, String nameFile);

    Avatar addAvatar(MultipartFile file, String nameFile) throws IOException;
}
