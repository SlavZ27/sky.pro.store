package ru.skypro.homework.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.entity.Ads;
import ru.skypro.homework.entity.Image;
import ru.skypro.homework.entity.User;
import ru.skypro.homework.exception.AdsNotFoundException;
import ru.skypro.homework.exception.ImageNotFoundException;
import ru.skypro.homework.mapper.CreateAdsMapper;
import ru.skypro.homework.mapper.FullAdsMapper;
import ru.skypro.homework.repository.AdsRepository;

import ru.skypro.homework.dto.*;
import ru.skypro.homework.entity.Comment;
import ru.skypro.homework.mapper.AdsMapper;
import ru.skypro.homework.mapper.CommentMapper;
import ru.skypro.homework.service.AdsService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;


/**
 * This class processes commands related create ads allowing users to create, update, get, delete ads.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AdsServiceImpl implements AdsService {
    private final AdsRepository adsRepository;
    private final AdsMapper adsMapper;
    private final CommentMapper commentMapper;
    private final CommentServiceImpl commentService;
    private final ImageServiceImpl imageService;
    private final CreateAdsMapper createAdsMapper;
    private final FullAdsMapper fullAdsMapper;
    private final UserServiceImpl userService;

    /**
     * This method, uses method repository and Mapper, allows update Ads.
     * Uses {@link AdsRepository#findById(Object)}
     * Uses {@link AdsRepository#save(Object)}
     *
     * @param adsId        is not null
     * @param createAdsDto is not null
     * @return ads
     * @throws AdsNotFoundException if passed non- existent id
     */
    @Override
    public AdsDto updateAds(Integer adsId, CreateAdsDto createAdsDto) {
        Ads newAds = createAdsMapper.createAdsDtoToAds(createAdsDto);
        Ads oldAds = adsRepository.findById(adsId).orElseThrow(() -> {
            log.error("Ad with ID: {} not found", adsId);
            return new AdsNotFoundException(adsId);
        });
        oldAds.setDescription(newAds.getDescription());
        oldAds.setPrice(newAds.getPrice());
        oldAds.setTitle(newAds.getTitle());
        AdsDto adsDto = adsMapper.adsToAdsDto(adsRepository.save(oldAds));
        log.info("Ads with ID: {} has been updated", adsId);
        return adsDto;
    }


    /**
     * This method uses method repository add Comments to Ads id
     * Uses {@link AdsRepository#findById(Object)}
     * Uses {@link UserServiceImpl#getUserByUserName}
     * Uses {@link CommentServiceImpl#addCommentsToAds(Ads, Comment, User)}
     *
     * @param adsId      is not null
     * @param commentDto is not null
     * @return adsId, comment
     * @throws AdsNotFoundException if passed non- existent id
     */
    @Override
    public CommentDto addCommentsToAds(Integer adsId, CommentDto commentDto, String username) {
        User user = userService.getUserByUserName(username);
        Ads ads = adsRepository.findById(adsId).orElseThrow(() -> {
            log.error("Ad with ID: {} not found", adsId);
            return new AdsNotFoundException(adsId);
        });
        Comment comment = commentMapper.dtoToComment(commentDto);
        return commentMapper.commentToDto(commentService.addCommentsToAds(ads, comment, user));
    }

    /**
     * This method uses method repository update Comments to Ads id and comment id.
     * Uses {@link AdsRepository#findById(Object)}
     * Uses {@link CommentServiceImpl#updateCommentsForAds(CommentDto, Ads, Integer)}
     *
     * @param adPk       is not null
     * @param commentId  is not null
     * @param commentDto is not null
     * @return comment
     * @throws AdsNotFoundException if passed non- existent id
     */
    @Override
    public CommentDto updateCommentsForAds(Integer adPk, Integer commentId, CommentDto commentDto) {
        Ads ads = adsRepository.findById(adPk).orElseThrow(() -> {
            log.error("Ads with ID: {} not found", adPk);
            return new AdsNotFoundException(adPk);
        });
        return commentMapper.commentToDto(commentService.updateCommentsForAds(commentDto, ads, commentId));
    }

    /**
     * This method uses method repository get Comments to Ads id.
     * Uses {@link AdsRepository#findById(Object)}
     * Uses {@link CommentServiceImpl#getAllByIdAdsAndSortDateTime(Integer)}
     *
     * @param adsId is not null
     * @return comment
     * @throws AdsNotFoundException if passed non- existent id
     */
    @Override
    public ResponseWrapperCommentDto getCommentsOfAds(Integer adsId) {
        Ads ads = adsRepository.findById(adsId).orElseThrow(() -> {
            log.error("Ads with ID: {} not found", adsId);
            return new AdsNotFoundException(adsId);
        });
        List<CommentDto> commentList = commentMapper.mapListOfCommentToListDto(
                commentService.getAllByIdAdsAndSortDateTime(ads.getId())
        );
        return commentMapper.mapListOfCommentDtoToResponseWrapper(commentList.size(), commentList);
    }

    /**
     * This method uses method repository get Ads to Ads id.
     * Uses {@link AdsRepository#findById(Object)}
     * Uses {@link CommentServiceImpl#removeAllCommentsOfAds(Integer)}
     * Uses {@link AdsRepository#delete(Object)}
     * Uses {@link ImageServiceImpl#removeImageWithFile(Image)}
     * Uses {@link ImageServiceImpl#getImageData(Image)}
     *
     * @param idAds is not null
     * @return HttpStatus.NO_CONTENT or null
     * @throws AdsNotFoundException if passed non- existent id
     */
    @Override
    public ResponseEntity<Void> removeAds(Integer idAds) {
        //finding
        Ads ads = adsRepository.findById(idAds).orElseThrow(() -> {
            log.error("Ads with ID: {} not found", idAds);
            return new AdsNotFoundException(idAds);
        });
        //deleting
        commentService.removeAllCommentsOfAds(ads.getId());
        Image imageForDel = ads.getImage();
        adsRepository.delete(ads);
        log.info("Deleted ads with id: " + idAds);
        imageService.removeImageWithFile(imageForDel);
        //checking Ads
        Ads adsMustBeNull = adsRepository.findById(idAds).orElse(null);
        //checking Image
        Image imageMustBeNull;
        try {
            imageMustBeNull = imageService.getImage(imageForDel.getId());
            log.info("Try to get imageMustBeNull by imageID {}", imageForDel.getId());
        } catch (ImageNotFoundException e) {
            imageMustBeNull = null;
            log.error("An exception occurred! Set imageMustBeNull = null");
        }
        //checking File of Image
        boolean existFile = true;
        try {
            imageService.getImageData(imageForDel);
            log.info("Try to get getImageData(imageForDel)");
        } catch (ImageNotFoundException e) {
            existFile = false;
            log.error("An exception occurred! Set existFile = false");
        }
        //checking comments
        int countComments = commentService.getCountByIdAds(idAds);
        //checking finish
        if (adsMustBeNull == null &&
                imageMustBeNull == null &&
                !existFile &&
                countComments == 0) {
            log.info("Ads with ID: {} has been successfully deleted", idAds);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        log.error("Method removeAds(Integer idAds) with ID:{} returned null", idAds);
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * This method uses method repository get Comments to Ads id and comment id
     * Uses {@link AdsRepository#findById(Object)}
     * Uses {@link CommentServiceImpl#getCommentOfAds(Integer, Integer)}
     *
     * @param adsId     is not null
     * @param commentId is not null
     * @return comment
     * @throws AdsNotFoundException if passed non- existent id
     */
    @Override
    public CommentDto getCommentOfAds(Integer adsId, Integer commentId) {
        Ads ads = adsRepository.findById(adsId).orElseThrow(() -> {
            log.error("Ad with ID: {} not found", adsId);
            return new AdsNotFoundException(adsId);
        });
        return commentMapper.commentToDto(commentService.getCommentOfAds(ads.getId(), commentId));
    }

    /**
     * This method uses method repository get Ads to CreateAdsDto
     * Uses {@link UserServiceImpl#getUserByUserName(String)}
     * Uses {@link AdsRepository#save(Object)}
     * Uses {@link ImageServiceImpl#addImage(MultipartFile, String)}
     *
     * @param createAdsDto is not null
     * @param image        is not null
     * @return Ads
     */
    @Override
    public AdsDto addAds(CreateAdsDto createAdsDto, MultipartFile image, String username) throws IOException {
        User user = userService.getUserByUserName(username);
        Ads ads = createAdsMapper.createAdsDtoToAds(createAdsDto);
        ads.setAuthor(user);
        ads.setDateTime(LocalDateTime.now());
        ads.setId(null);
        ads = adsRepository.save(ads);
        log.info("Ads with ID: {} has been created", ads.getId());
        Image addedImage = imageService.addImage(image, getNameFileForImage(ads));
        ads.setImage(addedImage);
        AdsDto adsDto = adsMapper.adsToAdsDto(adsRepository.save(ads));
        log.info("Ads with ID: {} has been added", ads.getId());
        return adsDto;
    }

    /**
     * This method uses method repository get Ads to Ads id
     * Uses {@link AdsRepository#findById(Object)}
     *
     * @param idAds is not null
     * @return ads
     * @throws AdsNotFoundException if passed non- existent id
     */
    @Override
    public FullAdsDto getAds(Integer idAds) {
        Ads ads = adsRepository.findById(idAds).orElseThrow(() -> {
            log.error("Ad with ID: {} not found", idAds);
            return new AdsNotFoundException(idAds);
        });
        return fullAdsMapper.adsToFullAdsDto(ads);
    }

    /**
     * This method uses method repository get all Ads
     * Uses {@link AdsRepository#findAllAndSortDateTime()}
     *
     * @return List<Ads>
     */
    @Override
    public ResponseWrapperAdsDto getALLAds() {
        List<AdsDto> listDto = adsMapper.mapListOfAdsToListDTO(
                adsRepository.findAllAndSortDateTime()
        );
        return adsMapper.mapToResponseWrapperAdsDto(listDto, listDto.size());
    }

    /**
     * This method uses method repository get Ads for Default user
     * Uses {@link AdsRepository#findAllByUserIdAndSortDateTime(Integer)}
     *
     * @return List<Ads> to default user
     */
    @Override
    public ResponseWrapperAdsDto getALLAdsOfMe(String username) {
        List<AdsDto> listDto = adsMapper.mapListOfAdsToListDTO(
                adsRepository.findAllByUsernameAndSortDateTime(username)
        );
        return adsMapper.mapToResponseWrapperAdsDto(listDto, listDto.size());
    }

    /**
     * This method uses method repository get comment for Ads id and comment id
     * Uses {@link AdsRepository#findById(Object)}
     * Uses {@link CommentServiceImpl#removeCommentForAds(Integer, Integer)}
     *
     * @param adPk      is not null
     * @param commentId is not null
     * @return null or HttpStatus.OK
     * @throws AdsNotFoundException if passed non- existent id
     */
    @Override
    public ResponseEntity<Void> removeCommentsForAds(Integer adPk, Integer commentId) {
        adsRepository.findById(adPk).orElseThrow(() -> {
            log.error("Ads with ID: {} not found", adPk);
            return new AdsNotFoundException(adPk);
        });
        commentService.removeCommentForAds(adPk, commentId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * This method uses method repository get name file for image
     *
     * @param ads is not null
     * @return Name file for image
     */
    @Override
    public String getNameFileForImage(Ads ads) {
        return "ads_" + ads.getId();
    }

    /**
     * This method uses method repository update image for ads id
     * Uses {@link AdsRepository#findById(Object)}
     * Uses {@link ImageServiceImpl#addImage(MultipartFile, String)}
     * Uses {@link ImageServiceImpl#updateImage(Image, MultipartFile, String)}
     * Uses {@link AdsRepository#save(Object)}
     * Uses {@link ImageServiceImpl#getImageData(Image)}
     *
     * @param idAds is not null
     * @param image is not null
     * @return image for ads
     */
    @Override
    public Pair<byte[], String> updateImageOfAds(Integer idAds, MultipartFile image) throws IOException {
        Ads ads = adsRepository.findById(idAds).orElseThrow(() -> {
            log.error("Ads with ID: {} not found", idAds);
            return new AdsNotFoundException(idAds);
        });
        updateImageOfAds(ads, image);
        ads = adsRepository.save(ads);
        log.info("Ads with ID: {} has been updated", idAds);
        return imageService.getImageData(ads.getImage());
    }

    @Override
    public void updateImageOfAds(Ads ads, MultipartFile image) throws IOException {
        if (ads.getImage() == null) {
            ads.setImage(imageService.addImage(image, getNameFileForImage(ads)));
            log.info("New image has been added for 'ads' with ID:{}", ads.getId());
        } else {
            ads.setImage(imageService.updateImage(ads.getImage(), image, getNameFileForImage(ads)));
            log.info("Image with ID: {} has been updated for 'ads' with ID:{}", ads.getImage().getId(), ads.getId());
        }
    }

    /**
     * This method uses method repository get image for Ads id
     * Uses {@link AdsRepository#findById(Object)}
     * Uses {@link ImageServiceImpl#getImageData(Image)}
     *
     * @param idAds is not null
     * @return image
     * @throws AdsNotFoundException   if passed non- existent id
     * @throws ImageNotFoundException if passed existent id Ads
     */
    @Override
    public Pair<byte[], String> getImage(Integer idAds) {
        Ads ads = adsRepository.findById(idAds).orElseThrow(() -> {
            log.error("Ads with ID: {} not found", idAds);
            return new AdsNotFoundException(idAds);
        });
        if (ads.getImage() == null) {
            log.error("Image for ads with ID: {} is null", ads.getId());
            throw new ImageNotFoundException("Image for ads with id = " + ads.getId() + " is absent");
        }
        return imageService.getImageData(ads.getImage());
    }

    /**
     * This method uses method repository find Ads for Title
     * Uses {@link AdsRepository#findByTitleLike(String)}
     *
     * @param title is not null
     * @return ads
     */
    @Override
    public ResponseWrapperAdsDto findAdsByTitle(String title) {
        List<AdsDto> listDto = adsMapper.mapListOfAdsToListDTO(
                adsRepository.findByTitleLike(title)
        );
        return adsMapper.mapToResponseWrapperAdsDto(listDto, listDto.size());
    }
}
