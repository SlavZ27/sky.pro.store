package ru.skypro.homework.service;

import org.springframework.data.util.Pair;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.*;
import ru.skypro.homework.entity.Ads;

import java.io.IOException;

public interface AdsService {
    AdsDto updateAds(Integer adsId, CreateAdsDto createAdsDto);

    CommentDto addCommentsToAds(Integer adsId, CommentDto commentDto, String username);

    CommentDto updateCommentsForAds(Integer adPk, Integer commentId, CommentDto commentDto);

    ResponseWrapperCommentDto getCommentsOfAds(Integer adsId);

    ResponseEntity<Void> removeAds(Integer idAds);

    CommentDto getCommentOfAds(Integer adsId, Integer commentId);

    AdsDto addAds(CreateAdsDto createAdsDto, MultipartFile image, String username) throws IOException;

    FullAdsDto getAds(Integer idAds);

    ResponseWrapperAdsDto getALLAds();

    ResponseWrapperAdsDto getALLAdsOfMe(String username);

    ResponseEntity<Void> removeCommentsForAds(Integer adPk, Integer commentId);

    String getNameFileForImage(Ads ads);

    Pair<byte[], String> updateImageOfAds(Integer idAds, MultipartFile image) throws IOException;

    void updateImageOfAds(Ads ads, MultipartFile image) throws IOException;

    Pair<byte[], String> getImage(Integer idAds);

    ResponseWrapperAdsDto findAdsByTitle(String title);
}
