package ru.skypro.homework.service;

import org.springframework.data.util.Pair;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.*;
import ru.skypro.homework.entity.Ads;

import java.io.IOException;

/**
 * Provides methods related create ads allowing users to create, update, get, delete ads.
 */
public interface AdsService {
    /**
     * Update ads.
     *
     * @param adsId        the ads id
     * @param createAdsDto the create ads dto
     * @return {@link AdsDto}
     */
    AdsDto updateAds(Integer adsId, CreateAdsDto createAdsDto);

    /**
     * Add comments to ads comment dto.
     *
     * @param adsId      the ads id
     * @param commentDto the comment dto
     * @param username   the username
     * @return {@link CommentDto}
     */
    CommentDto addCommentsToAds(Integer adsId, CommentDto commentDto, String username);

    /**
     * Update comments for ads.
     *
     * @param adPk       the ad pk
     * @param commentId  the comment id
     * @param commentDto the comment dto
     * @return {@link CommentDto}
     */
    CommentDto updateCommentsForAds(Integer adPk, Integer commentId, CommentDto commentDto);

    /**
     * Gets comments of ads.
     *
     * @param adsId the ads id
     * @return {@link ResponseWrapperCommentDto}
     */
    ResponseWrapperCommentDto getCommentsOfAds(Integer adsId);

    /**
     * Remove ads response entity.
     *
     * @param idAds the id ads
     * @return the response entity
     */
    ResponseEntity<Void> removeAds(Integer idAds);

    /**
     * Gets comment of ads.
     *
     * @param adsId     the ads id
     * @param commentId the comment id
     * @return {@link CommentDto}
     */
    CommentDto getCommentOfAds(Integer adsId, Integer commentId);

    /**
     * Add ads.
     *
     * @param createAdsDto the createadsdto
     * @param image        the image
     * @param username     the username
     * @return {@link AdsDto}
     * @throws IOException the io exception
     */
    AdsDto addAds(CreateAdsDto createAdsDto, MultipartFile image, String username) throws IOException;

    /**
     * Gets ads.
     *
     * @param idAds the id ads
     * @return {@link FullAdsDto}
     */
    FullAdsDto getAds(Integer idAds);

    /**
     * Gets all ads.
     *
     * @return {@link ResponseWrapperAdsDto}
     */
    ResponseWrapperAdsDto getALLAds();

    /**
     * Gets all ads of me.
     *
     * @param username the username
     * @return {@link ResponseWrapperAdsDto}
     */
    ResponseWrapperAdsDto getALLAdsOfMe(String username);

    /**
     * Remove comments for ads
     *
     * @param adPk      the ad pk
     * @param commentId the comment id
     * @return the response entity
     */
    ResponseEntity<Void> removeCommentsForAds(Integer adPk, Integer commentId);

    /**
     * Gets name file for image.
     *
     * @param ads the ads
     * @return String - the name file for image
     */
    String getNameFileForImage(Ads ads);

    /**
     * Update image of ads.
     *
     * @param idAds the id ads
     * @param image the image
     * @return the pair lf bytes and strings
     * @throws IOException the io exception
     */
    Pair<byte[], String> updateImageOfAds(Integer idAds, MultipartFile image) throws IOException;

    /**
     * Update image of ads.
     *
     * @param ads   the ads
     * @param image the image
     * @throws IOException the io exception
     */
    void updateImageOfAds(Ads ads, MultipartFile image) throws IOException;

    /**
     * Gets image.
     *
     * @param idAds the id ads
     * @return the pair lf bytes and strings
     */
    Pair<byte[], String> getImage(Integer idAds);

    /**
     * Find ads by title.
     *
     * @param title the title
     * @return {@link ResponseWrapperAdsDto}
     */
    ResponseWrapperAdsDto findAdsByTitle(String title);
}
