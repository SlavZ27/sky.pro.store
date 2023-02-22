package ru.skypro.homework.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.AdsDto;
import ru.skypro.homework.dto.FullAdsDto;
import ru.skypro.homework.dto.ResponseWrapperAdsDto;
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

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;



/**
 * This class processes commands related create ads allowing users to create, update, get, delete ads.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AdsServiceImpl {
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
     * @param adsId is not null
     * @param createAdsDto is not null
     * @return ads
     * @throws AdsNotFoundException if passed non- existent id
     */
    public AdsDto updateAds(Integer adsId, CreateAdsDto createAdsDto) {
        Ads oldAds = adsRepository.findById(adsId).orElseThrow(() -> {
            log.error("There is not ads with id = " + adsId);
            return new AdsNotFoundException(adsId);
        });
        oldAds.setDescription(createAdsDto.getDescription());
        oldAds.setPrice(createAdsDto.getPrice());
        oldAds.setTitle(createAdsDto.getTitle());
        return adsMapper.adsToAdsDto(adsRepository.save(oldAds));
    }


    /**
     * This method uses method repository add Comments to Ads id
     * Uses {@link AdsRepository#findById(Object)}
     * Uses {@link UserServiceImpl#getRandomUser()}
     * Uses {@link CommentServiceImpl#addCommentsToAds(Integer, Comment)}
     * @param adsId is not null
     * @param commentDto is not null
     * @return adsId, comment
     * @throws AdsNotFoundException if passed non- existent id
     */
    public CommentDto addCommentsToAds(Integer adsId, CommentDto commentDto) {
        adsRepository.findById(adsId).orElseThrow(() -> {
            log.error("There is not ads with id = " + adsId);
            return new AdsNotFoundException(adsId);
        });
        commentDto.setAuthor(userService.getRandomUser().getId());
        Comment comment = commentMapper.dtoToComment(commentDto);
        return commentMapper.commentToDto(commentService.addCommentsToAds(adsId, comment));
    }

    /**
     * This method uses method repository update Comments to Ads id and comment id.
     * Uses {@link AdsRepository#findById(Object)}
     * Uses {@link CommentServiceImpl#updateCommentsForAds(CommentDto, Integer, Integer)}
     * @param adPk is not null
     * @param commentId is not null
     * @param commentDto is not null
     * @return comment
     * @throws AdsNotFoundException if passed non- existent id
     */
    public CommentDto updateCommentsForAds(Integer adPk, Integer commentId, CommentDto commentDto) {
        Ads ads = adsRepository.findById(adPk).orElseThrow(() -> {
            log.error("There is not ads with id = " + adPk);
            return new AdsNotFoundException(adPk);
        });
        return commentMapper.commentToDto(commentService.updateCommentsForAds(commentDto, ads.getId(), commentId));
    }

    /**
     * This method uses method repository get Comments to Ads id.
     * Uses {@link AdsRepository#findById(Object)}
     * Uses {@link CommentServiceImpl#getAllByIdAdsAndSortDateTime(Integer)}
     * @param adsId is not null
     * @return comment
     * @throws AdsNotFoundException if passed non- existent id
     */
    public ResponseWrapperCommentDto getCommentsOfAds(Integer adsId) {
        Ads ads = adsRepository.findById(adsId).orElseThrow(() -> {
            log.error("There is not ads with id = " + adsId);
            return new AdsNotFoundException(adsId);
        });
        List<CommentDto> commentList = commentMapper.mapListOfCommentToListDto(
                commentService.getAllByIdAdsAndSortDateTime(ads.getId()));
        return commentMapper.mapListOfCommentDtoToResponseWrapper(commentList.size(), commentList);
    }

    /**
     * This method uses method repository get Ads to Ads id.
     * Uses {@link AdsRepository#findById(Object)}
     * Uses {@link CommentServiceImpl#removeAllCommentsOfAds(Integer)}
     * Uses {@link AdsRepository#delete(Object)}
     * Uses {@link ImageServiceImpl#removeImageWithFile(Image)}
     * Uses {@link ImageServiceImpl#getImageData(Image)}
     * Uses {@link CommentServiceImpl#getAllByIdAds(Integer)}
     * @param idAds is not null
     * @return HttpStatus.NO_CONTENT or null
     * @throws AdsNotFoundException if passed non- existent id
     */
    public ResponseEntity<Void> removeAds(Integer idAds) {
        //finding
        Ads ads = adsRepository.findById(idAds).orElseThrow(() -> new AdsNotFoundException(idAds));
        //deleting
        commentService.removeAllCommentsOfAds(ads.getId());
        Image imageForDel = ads.getImage();
        adsRepository.delete(ads);
        imageService.removeImageWithFile(imageForDel);
        //checking Ads
        Ads adsMustBeNull = adsRepository.findById(idAds).orElse(null);
        //checking Image
        Image imageMustBeNull;
        try {
            imageMustBeNull = imageService.getImage(imageForDel.getId());
        } catch (ImageNotFoundException e) {
            imageMustBeNull = null;
        }
        //checking File of Image
        boolean existFile = true;
        try {
            imageService.getImageData(imageForDel);
        } catch (ImageNotFoundException e) {
            existFile = false;
        }
        //checking finish
        if (adsMustBeNull == null &&
                imageMustBeNull == null &&
                !existFile) {
            return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
        }
        return null;
    }

    /**
     * This method uses method repository get Comments to Ads id and comment id
     * Uses {@link AdsRepository#findById(Object)}
     * Uses {@link CommentServiceImpl#getCommentOfAds(Integer, Integer)}
     * @param adsId is not null
     * @param commentId is not null
     * @return comment
     * @throws AdsNotFoundException if passed non- existent id
     */
    public CommentDto getCommentOfAds(Integer adsId, Integer commentId) {
        Ads ads = adsRepository.findById(adsId).orElseThrow(() -> {
            log.error("There is not ads with id = " + adsId);
            return new AdsNotFoundException(adsId);
        });
        return commentMapper.commentToDto(commentService.getCommentOfAds(ads.getId(), commentId));
    }

    /**
     * This method uses method repository get Ads to CreateAdsDto
     * Uses {@link UserServiceImpl#getDefaultUser()}
     * Uses {@link AdsRepository#save(Object)}
     * Uses {@link ImageServiceImpl#addImage(MultipartFile, String)}
     * @param createAdsDto is not null
     * @param image is not null
     * @return Ads
     * @throws IOException
     */
    public AdsDto addAds(CreateAdsDto createAdsDto, MultipartFile image) throws IOException {
        User user = userService.getDefaultUser();
        Ads ads = createAdsMapper.createAdsDtoToAds(createAdsDto);
        ads.setAuthor(user);
        ads.setDateTime(LocalDateTime.now());
        ads = adsRepository.save(ads);
        Image addedImage = imageService.addImage(image, "ads_" + ads.getId().toString());
        ads.setImage(addedImage);
        return adsMapper.adsToAdsDto(adsRepository.save(ads));
    }

    /**
     * This method uses method repository get Ads to Ads id
     * Uses {@link AdsRepository#findById(Object)}
     * @param idAds is not null
     * @return ads
     * @throws AdsNotFoundException if passed non- existent id
     */
    public FullAdsDto getAds(Integer idAds) {
        Ads ads = adsRepository.findById(idAds).orElseThrow(() -> new AdsNotFoundException(idAds));
        return fullAdsMapper.adsToFullAdsDto(ads);
    }

    /**
     * This method uses method repository get all Ads
     * Uses {@link AdsRepository#findAllAndSortDateTime()}
     * @return List<Ads>
     */
    public ResponseWrapperAdsDto getALLAds() {
        List<Ads> list = adsRepository.findAllAndSortDateTime();
        List<AdsDto> listDto = adsMapper.mapListOfAdsToListDTO(list);
        return adsMapper.mapToResponseWrapperAdsDto(listDto, listDto.size());
    }

    /**
     * This method uses method repository get Ads for Default user
     * Uses {@link AdsRepository#findAllByUserIdAndSortDateTime(Integer)}
     * @return List<Ads> to default user
     */
    public ResponseWrapperAdsDto getALLAdsOfMe() {
        User user = userService.getDefaultUser();
        List<Ads> list = adsRepository.findAllByUserIdAndSortDateTime(user.getId());
        List<AdsDto> listDto = adsMapper.mapListOfAdsToListDTO(list);
        return adsMapper.mapToResponseWrapperAdsDto(listDto, listDto.size());
    }

    /**
     * This method uses method repository get comment for Ads id and comment id
     * Uses {@link AdsRepository#findById(Object)}
     * Uses {@link CommentServiceImpl#removeCommentForAds(Integer, Integer)}
     * @param adPk is not null
     * @param commentId is not null
     * @return  null or HttpStatus.OK
     * @throws AdsNotFoundException if passed non- existent id
     */
    public ResponseEntity<Void> removeCommentsForAds(Integer adPk, Integer commentId) {
        adsRepository.findById(adPk).orElseThrow(() -> {
            log.error("There is not ads with id = " + adPk);
            return new AdsNotFoundException(adPk);
        });
        commentService.removeCommentForAds(adPk, commentId);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    /**
     * This method uses method repository get name file for image
     * @param ads is not null
     * @return Name file for image
     */
    private String getNameFileForImage(Ads ads) {
        return "ads_" + ads.getId();
    }

    /**
     * This method uses method repository update image for ads id
     * Uses {@link AdsRepository#findById(Object)}
     * Uses {@link ImageServiceImpl#addImage(MultipartFile, String)}
     * Uses {@link ImageServiceImpl#updateImage(Image, MultipartFile, String)}
     * Uses {@link AdsRepository#save(Object)}
     * Uses {@link ImageServiceImpl#getImageData(Image)}
     * @param idAds is not null
     * @param image is not null
     * @return image for ads
     * @throws IOException
     */
    public Pair<byte[], String> updateImageOfAds(Integer idAds, MultipartFile image) throws IOException {
        Ads ads = adsRepository.findById(idAds).orElseThrow(() -> {
            log.error("There is not ads with id = " + idAds);
            return new AdsNotFoundException(idAds);
        });
        if (ads.getImage() == null) {
            ads.setImage(imageService.addImage(image, getNameFileForImage(ads)));
        } else {
            ads.setImage(imageService.updateImage(ads.getImage(), image, getNameFileForImage(ads)));
        }
        ads = adsRepository.save(ads);
        return imageService.getImageData(ads.getImage());
    }

    /**
     * This method uses method repository get image for Ads id
     * Uses {@link AdsRepository#findById(Object)}
     * Uses {@link ImageServiceImpl#getImageData(Image)}
     * @param idAds is not null
     * @return image
     * @throws AdsNotFoundException if passed non- existent id
     * @throws ImageNotFoundException if passed existent id Ads
     */
    public Pair<byte[], String> getImage(Integer idAds) {
        Ads ads = adsRepository.findById(idAds).orElseThrow(() -> {
            log.error("There is not ads with id = " + idAds);
            return new AdsNotFoundException(idAds);
        });
        if (ads.getImage() == null) {
            throw new ImageNotFoundException("Image for ads with id = " + ads.getId() + " is absent");
        } else {
            return imageService.getImageData(ads.getImage());
        }
    }

    /**
     * This method uses method repository find Ads for Title
     * Uses {@link AdsRepository#findByTitleLike(String)}
     * @param title is not null
     * @return ads
     */
    public ResponseWrapperAdsDto findAdsByTitle(String title) {
        List<Ads> list = adsRepository.findByTitleLike(title);
        List<AdsDto> listDto = adsMapper.mapListOfAdsToListDTO(list);
        return adsMapper.mapToResponseWrapperAdsDto(listDto, listDto.size());
    }
}
