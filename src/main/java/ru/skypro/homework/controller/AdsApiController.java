package ru.skypro.homework.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.controller.api.AdsApi;
import ru.skypro.homework.dto.*;

import ru.skypro.homework.service.impl.AdsServiceImpl;

import javax.validation.Valid;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;


@Slf4j
@RestController
@CrossOrigin(value = "http://localhost:3000")
public class AdsApiController implements AdsApi {

    private final ObjectMapper objectMapper;
    private final AdsServiceImpl adsServiceImpl;
    private final HttpServletRequest request;


    public AdsApiController(ObjectMapper objectMapper, AdsServiceImpl adsServiceImpl, HttpServletRequest request) {
        this.objectMapper = objectMapper;
        this.adsServiceImpl = adsServiceImpl;
        this.request = request;
    }

    public ResponseEntity<ResponseWrapperAdsDto> getALLAds() {
        return ResponseEntity.ok(adsServiceImpl.getALLAds());
    }


    public ResponseEntity<AdsDto> addAds(
            @Parameter(in = ParameterIn.DEFAULT, description = "", schema = @Schema()) @RequestPart(value = "properties", required = false) CreateAdsDto properties,
            @Parameter(description = "file detail") @Valid @RequestPart("image") MultipartFile image) throws IOException {
        return ResponseEntity.ok(adsServiceImpl.addAds(properties, image));
    }

    public ResponseEntity<ResponseWrapperCommentDto> getComments(@Parameter(in = ParameterIn.PATH, description = "", required = true, schema = @Schema()) @PathVariable("ad_pk") Integer adPk) {
        return ResponseEntity.ok(adsServiceImpl.getCommentsOfAds(adPk));
    }

    public ResponseEntity<CommentDto> addComments(@Parameter(in = ParameterIn.PATH, description = "", required = true, schema = @Schema()) @PathVariable("ad_pk") Integer adPk, @Parameter(in = ParameterIn.DEFAULT, description = "", required = true, schema = @Schema()) @Valid @RequestBody CommentDto body) {
        CommentDto commentDto = adsServiceImpl.addCommentsToAds(adPk, body);
        return ResponseEntity.ok(commentDto);
    }


    public ResponseEntity<FullAdsDto> getAds(@PathVariable("id") Integer idAds  ){
        return ResponseEntity.ok(adsServiceImpl.getAds(idAds));
    }

    public ResponseEntity<Void> removeAds(@PathVariable("id_ads") Integer idAds) {
        return adsServiceImpl.removeAds(idAds);
    }

    public ResponseEntity<AdsDto> updateAds(@Parameter(in = ParameterIn.PATH, description = "", required = true, schema = @Schema()) @PathVariable("id") Integer id, @Parameter(in = ParameterIn.DEFAULT, description = "", required = true, schema = @Schema()) @Valid @RequestBody CreateAdsDto body) {
        return ResponseEntity.ok(adsServiceImpl.updateAds(id, body));
    }

    public ResponseEntity<CommentDto> getComments(@Parameter(in = ParameterIn.PATH, description = "", required = true, schema = @Schema()) @PathVariable("ad_pk") Integer adPk, @Parameter(in = ParameterIn.PATH, description = "", required = true, schema = @Schema()) @PathVariable("id") Integer id) {
//        return ResponseEntity.ok(adsServiceImpl.getCommentOfAds(adPk, id));
        return new ResponseEntity<CommentDto>(adsServiceImpl.getCommentOfAds(adPk, id), HttpStatus.OK);
    }

    public ResponseEntity<Void> deleteComments(@Parameter(in = ParameterIn.PATH, description = "", required = true, schema = @Schema()) @PathVariable("ad_pk") String adPk, @Parameter(in = ParameterIn.PATH, description = "", required = true, schema = @Schema()) @PathVariable("id") Integer id) {
        String accept = request.getHeader("Accept");
        return new ResponseEntity<Void>(HttpStatus.NOT_IMPLEMENTED);
    }

    public ResponseEntity<CommentDto> updateComments(@Parameter(in = ParameterIn.PATH, description = "", required = true, schema = @Schema()) @PathVariable("ad_pk") Integer adPk, @Parameter(in = ParameterIn.PATH, description = "", required = true, schema = @Schema()) @PathVariable("id") Integer id, @Parameter(in = ParameterIn.DEFAULT, description = "", required = true, schema = @Schema()) @Valid @RequestBody CommentDto body) {
        CommentDto commentDto = adsServiceImpl.updateCommentsForAds(adPk, id, body);
        return ResponseEntity.ok(commentDto);
    }

    public ResponseEntity<ResponseWrapperAdsDto> getAdsMeUsingGET(@Parameter(in = ParameterIn.QUERY, description = "", schema = @Schema()) @Valid @RequestParam(value = "username") String username) {
        return ResponseEntity.ok(adsServiceImpl.getALLAds());
    }
}
