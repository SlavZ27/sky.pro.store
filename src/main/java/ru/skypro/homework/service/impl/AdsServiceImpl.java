package ru.skypro.homework.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.skypro.homework.entity.Ads;
import ru.skypro.homework.exception.AdsNotFoundException;
import ru.skypro.homework.repository.AdsRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdsServiceImpl {
    private final AdsRepository adsRepository;
    private final ImageServiceImpl imageService;
    private final CommentServiceImpl commentService;

    public ResponseEntity<Void> removeAds(Integer idAds) {
        Ads ads = adsRepository.findById(idAds).orElseThrow(() -> new AdsNotFoundException(idAds));
        imageService.removeAllImagesOfAds(ads.getId());
        commentService.removeAllCommentsOfAds(ads.getId());
        adsRepository.delete(ads);
        ads = adsRepository.findById(idAds).orElse(null);
        if (imageService.getAllByIdAds(idAds).size() == 0 &&
                commentService.getAllByIdAds(idAds).size() == 0 &&
                ads == null) {
            return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
        }
        return null;
    }
}
