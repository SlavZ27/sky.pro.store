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
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


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


    public CommentDto addCommentsToAds(Integer adsId, CommentDto commentDto) {
        adsRepository.findById(adsId).orElseThrow(() -> {
            log.error("There is not ads with id = " + adsId);
            return new AdsNotFoundException(adsId);
        });
        commentDto.setAuthor(userService.getRandomUser().getId());
        Comment comment = commentMapper.dtoToComment(commentDto);
        return commentMapper.commentToDto(commentService.addCommentsToAds(adsId, comment));
    }

    public CommentDto updateCommentsForAds(Integer adPk, Integer commentId, CommentDto commentDto) {
        Ads ads = adsRepository.findById(adPk).orElseThrow(() -> {
            log.error("There is not ads with id = " + adPk);
            return new AdsNotFoundException(adPk);
        });
        return commentMapper.commentToDto(commentService.updateCommentsForAds(commentDto, ads.getId(), commentId));
    }

    public ResponseWrapperCommentDto getCommentsOfAds(Integer adsId) {
        Ads ads = adsRepository.findById(adsId).orElseThrow(() -> {
            log.error("There is not ads with id = " + adsId);
            return new AdsNotFoundException(adsId);
        });
        List<CommentDto> commentList = commentMapper.mapListOfCommentToListDto(
                commentService.getAllByIdAdsAndSortDateTime(ads.getId()));
        return commentMapper.mapListOfCommentDtoToResponseWrapper(commentList.size(), commentList);
    }

    public ResponseEntity<Void> removeAds(Integer idAds) {
        Ads ads = adsRepository.findById(idAds).orElseThrow(() -> new AdsNotFoundException(idAds));
        commentService.removeAllCommentsOfAds(ads.getId());
        Image imageForDel = ads.getImage();
        adsRepository.delete(ads);
        imageService.removeImageWithFile(imageForDel);
        ads = adsRepository.findById(idAds).orElse(null);
        if (imageService.getImageData(imageForDel) == null &&
                commentService.getAllByIdAds(idAds).size() == 0 &&
                ads == null) {
            return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
        }
        return null;
    }

    public CommentDto getCommentOfAds(Integer adsId, Integer commentId) {
        Ads ads = adsRepository.findById(adsId).orElseThrow(() -> {
            log.error("There is not ads with id = " + adsId);
            return new AdsNotFoundException(adsId);
        });
        return commentMapper.commentToDto(commentService.getCommentOfAds(ads.getId(), commentId));
    }

    public AdsDto addAds(CreateAdsDto createAdsDto, MultipartFile image) throws IOException {
        User user = userService.getDefaultUser();
        Ads ads = createAdsMapper.createAdsDtoToAds(createAdsDto);
        ads.setAuthor(user);
        ads = adsRepository.save(ads);
        Image addedImage = imageService.addImage(image, "ads_" + ads.getId().toString());
        ads.setImage(addedImage);
        return adsMapper.adsToAdsDto(adsRepository.save(ads));
    }

    public FullAdsDto getAds(Integer idAds) {
        Ads ads = adsRepository.findById(idAds).orElseThrow(() -> new AdsNotFoundException(idAds));
        return fullAdsMapper.adsToFullAdsDto(ads);
    }

    public ResponseWrapperAdsDto getALLAds() {
        List<Ads> list = adsRepository.findAllAndSortDateTime();
        List<AdsDto> listDto = adsMapper.mapListOfAdsToListDTO(list);
        return adsMapper.mapToResponseWrapperAdsDto(listDto, listDto.size());
    }

    public ResponseEntity<Void> removeCommentsForAds(Integer adPk, Integer commentId) {
        adsRepository.findById(adPk).orElseThrow(() -> {
            log.error("There is not ads with id = " + adPk);
            return new AdsNotFoundException(adPk);
        });
        commentService.removeCommentForAds(adPk, commentId);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    public Pair<byte[], String> updateImageOfAds(Integer idAds, MultipartFile image) throws IOException {
        Ads ads = adsRepository.findById(idAds).orElseThrow(() -> {
            log.error("There is not ads with id = " + idAds);
            return new AdsNotFoundException(idAds);
        });
        if (ads.getImage() == null) {
            ads.setImage(imageService.addImage(image, ads.getId().toString()));
        } else {
            ads.setImage(imageService.updateImage(ads.getImage(), image, ads.getId().toString()));
        }
        ads = adsRepository.save(ads);
        return imageService.getImageData(ads.getImage());
    }

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

    public ResponseWrapperAdsDto findAdsByTitle(String title) {
        List<Ads> list = adsRepository.findByTitleLike(title);
        List<AdsDto> listDto = adsMapper.mapListOfAdsToListDTO(list);
        return adsMapper.mapToResponseWrapperAdsDto(listDto, listDto.size());
    }
}
