package ru.skypro.homework.service.impl;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.skypro.homework.entity.Ads;
import ru.skypro.homework.entity.Image;
import ru.skypro.homework.exception.NotFoundException;
import ru.skypro.homework.mapper.AdsMapper;
import ru.skypro.homework.repository.AdsRepository;
import ru.skypro.homework.repository.CommentRepository;
import ru.skypro.homework.repository.ImageRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdsServiceImpl {
    private final AdsRepository adsRepository;
    private final ImageRepository imageRepository;
    private final CommentRepository commentRepository;

    public void removeAds(Integer id_ads) throws NotFoundException {
        Ads ads = adsRepository.findById(id_ads).orElse(null);
        if (ads == null) {
            throw new NotFoundException(id_ads, "not found");
        }
        List<Integer> id_images = ads.getImages().stream().map(Image::getId).collect(Collectors.toList());

        imageRepository.findAllById(id_images).stream().map(i -> {
            try {
                return Files.deleteIfExists(Path.of(i.getPath()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        commentRepository.deleteById(id_ads);
        imageRepository.deleteAllById(id_images);
        adsRepository.deleteById(id_ads);
    }
}
