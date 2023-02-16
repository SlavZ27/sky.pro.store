package ru.skypro.homework.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.entity.Ads;
import ru.skypro.homework.entity.Image;
import ru.skypro.homework.entity.User;
import ru.skypro.homework.exception.AdsNotFoundException;
import ru.skypro.homework.mapper.CreateAdsMapper;
import ru.skypro.homework.repository.AdsRepository;

import ru.skypro.homework.dto.*;
import ru.skypro.homework.entity.Comment;
import ru.skypro.homework.mapper.AdsMapper;
import ru.skypro.homework.mapper.CommentMapper;
import ru.skypro.homework.repository.UsersRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
    private final UsersRepository usersRepository;

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
        Ads oldAds = adsRepository.findById(adsId).orElseThrow(() -> {
            log.error("There is not ads with id = " + adsId);
            return new AdsNotFoundException(adsId);
        });

        Comment comment = commentMapper.dtoToComment(commentDto);

        return commentMapper.commentToDto(commentService.addCommentsToAds(adsId, comment));
    }

    public CommentDto updateCommentsForAds(Integer adPk, Integer commentId, CommentDto commentDto) {
        Ads oldAds = adsRepository.findById(adPk).orElseThrow(() -> {
            log.error("There is not ads with id = " + adPk);
            return new AdsNotFoundException(adPk);
        });
        return commentMapper.commentToDto(commentService.updateCommentsForAds(commentDto, adPk, commentId));
    }

    public ResponseWrapperCommentDto getCommentsOfAds(Integer adsId) {
        Ads oldAds = adsRepository.findById(adsId).orElseThrow(() -> {
            log.error("There is not ads with id = " + adsId);
            return new AdsNotFoundException(adsId);
        });
        List<CommentDto> commentList = commentMapper.mapListOfCommentToListDto(commentService.listComment(adsId));
        return commentMapper.mapListOfCommentDtoToResponseWrapper(commentList.size(), commentList);
    }

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

    public CommentDto getCommentOfAds(Integer adsId, Integer commentId) {
        Ads oldAds = adsRepository.findById(adsId).orElseThrow(() -> {
            log.error("There is not ads with id = " + adsId);
            return new AdsNotFoundException(adsId);
        });
        return commentMapper.commentToDto(commentService.getCommentOfAds(adsId, commentId));
    }

    public AdsDto addAds(CreateAdsDto createAdsDto, MultipartFile image) throws IOException {
//        if(createAdsDto == null)// какие здесь нужны проверки??
        Ads ads = createAdsMapper.createAdsDtoToAds(createAdsDto);

        User user = usersRepository.findById(3).orElseThrow(() -> new AdsNotFoundException(ads.getId()));//for test
        ads.setAuthor(user);
        adsRepository.save(ads);

        List<Image> images = new ArrayList<>();
        images.add(imageService.addImage(ads, image));
        ads.setImages(images);

        return adsMapper.adsToAdsDto(ads);
    }
}
