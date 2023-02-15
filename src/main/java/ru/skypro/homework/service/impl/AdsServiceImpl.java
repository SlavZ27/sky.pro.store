package ru.skypro.homework.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.skypro.homework.entity.Ads;
import ru.skypro.homework.exception.AdsNotFoundException;
import ru.skypro.homework.repository.AdsRepository;

import ru.skypro.homework.dto.*;
import ru.skypro.homework.entity.Comment;
import ru.skypro.homework.mapper.AdsMapper;
import ru.skypro.homework.mapper.CommentMapper;

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

    public CommentDto updateCommentsForAds(String adPk, Integer commentId, CommentDto commentDto) {
        Ads oldAds = adsRepository.findById(Integer.valueOf(adPk)).orElseThrow(() -> {
            log.error("There is not ads with id = " + adPk);
            return new AdsNotFoundException(Integer.valueOf(adPk));
        });

//        adsMapper.adsToAdsDto(adsRepository.save(oldAds));// зачем тут пересохранять?
        return commentMapper.commentToDto(commentService.updateCommentsForAds(commentDto, Integer.valueOf(adPk), commentId));
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

    public CommentDto getCommentOfAds(String adsId, Integer commentId) {
        Ads oldAds = adsRepository.findById(Integer.valueOf(adsId)).orElseThrow(() -> {
            log.error("There is not ads with id = " + adsId);
            return new AdsNotFoundException(Integer.valueOf(adsId));
        });
        return commentMapper.commentToDto(commentService.getCommentOfAds(Integer.valueOf(adsId), commentId));
    }
}
