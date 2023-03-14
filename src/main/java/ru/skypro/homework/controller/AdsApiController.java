package ru.skypro.homework.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.repository.query.Param;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.*;

import ru.skypro.homework.service.impl.AdsServiceImpl;

import java.io.IOException;


@Slf4j
@RestController
@CrossOrigin(value = "http://localhost:3000")
@RequestMapping(value = "ads")
@Validated
public class AdsApiController {

    private final AdsServiceImpl adsServiceImpl;

    public AdsApiController(AdsServiceImpl adsServiceImpl) {
        this.adsServiceImpl = adsServiceImpl;
    }

    @Operation(
            summary = "getALLAds",
            description = "return all Ads",
            tags = {"Объявления"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "OK",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ResponseWrapperAdsDto.class)))})
    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    //      GET http://localhost:8080/ads/
    // available for the unauthenticated
    public ResponseEntity<ResponseWrapperAdsDto> getALLAds() {
        return ResponseEntity.ok(adsServiceImpl.getALLAds());
    }

    @Operation(
            summary = "addAds",
            description = "add new ad. returns the same ad",
            tags = {"Объявления"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",
                    description = "Created",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AdsDto.class))),
            @ApiResponse(responseCode = "401",
                    description = "Unauthorized"),
            @ApiResponse(responseCode = "403",
                    description = "Forbidden"),
            @ApiResponse(responseCode = "404",
                    description = "Not Found")})
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    //      POST http://localhost:8080/ads/
    // available only for the authenticated
    public ResponseEntity<AdsDto> addAds(
            @Parameter(description = "JSON for create new ad")
            @RequestPart(value = "properties", required = false) @Validated CreateAdsDto properties,
            @Parameter(description = "file detail")
            @RequestPart("image") MultipartFile image,
            Authentication authentication) throws IOException {
        return ResponseEntity.ok(adsServiceImpl.addAds(properties, image, authentication.getName()));
    }

    @Operation(
            summary = "getComments",
            description = "returns all comments by ad",
            tags = {"Объявления"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "OK",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ResponseWrapperCommentDto.class))),
            @ApiResponse(responseCode = "404",
                    description = "Not Found")})
    @GetMapping(value = "/{ad_pk}/comments",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    //      GET http://localhost:8080/ads/{ad_pk}/comments
    // available only for the authenticated
    public ResponseEntity<ResponseWrapperCommentDto> getComments(
            @Parameter(in = ParameterIn.PATH,
                    description = "id of ad")
            @PathVariable("ad_pk") Integer adPk) {
        return ResponseEntity.ok(adsServiceImpl.getCommentsOfAds(adPk));
    }

    @Operation(
            summary = "addComments",
            description = "add new comment. returns the same comment",
            tags = {"Объявления"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "OK",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CommentDto.class))),
            @ApiResponse(responseCode = "401",
                    description = "Unauthorized"),
            @ApiResponse(responseCode = "403",
                    description = "Forbidden"),
            @ApiResponse(responseCode = "404",
                    description = "Not Found")})
    @PostMapping(value = "/{ad_pk}/comments",
            produces = {MediaType.APPLICATION_JSON_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    //  available only for the authenticated
    public ResponseEntity<CommentDto> addComments(
            @Parameter(in = ParameterIn.PATH,
                    description = "id of ad to process")
            @PathVariable("ad_pk") Integer adPk,
            @Parameter(description = "JSON for create new comment")
            @RequestBody @Validated CommentDto body,
            Authentication authentication) {
        CommentDto commentDto = adsServiceImpl.addCommentsToAds(adPk, body, authentication.getName());
        return ResponseEntity.ok(commentDto);
    }

    @Operation(
            summary = "getFullAd",
            description = "return Full info about ad",
            tags = {"Объявления"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "OK",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = FullAdsDto.class))),
            @ApiResponse(responseCode = "404",
                    description = "Not Found")})
    @GetMapping(value = "{id}", produces = {MediaType.APPLICATION_JSON_VALUE})
    //      GET http://localhost:8080/ads/{ad_pk}
    // // available only for the authenticated
    public ResponseEntity<FullAdsDto> getAds(
            @Parameter(in = ParameterIn.PATH,
                    description = "id of ad to process")
            @PathVariable("id") Integer idAds) {
        return ResponseEntity.ok(adsServiceImpl.getAds(idAds));
    }

    @Operation(
            summary = "removeAds",
            description = "delete ad with comment and image",
            tags = {"Объявления"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204",
                    description = "No Content"),
            @ApiResponse(responseCode = "401",
                    description = "Unauthorized"),
            @ApiResponse(responseCode = "403",
                    description = "Forbidden"),
            @ApiResponse(responseCode = "404",
                    description = "Not Found")})
    @DeleteMapping(value = "{id_ads}")
    // DELETE http://localhost:8080/ads/{id}
    // available only to the admin or the user who created this ad
    @PreAuthorize("@userSecurity.isAdsAuthor(#idAds) or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> removeAds(
            @Parameter(in = ParameterIn.PATH,
                    description = "id of ad to process")
            @PathVariable("id_ads") Integer idAds) {
        return adsServiceImpl.removeAds(idAds);
    }

    @Operation(
            summary = "updateAds",
            description = "update info about ad without image",
            tags = {"Объявления"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "OK",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AdsDto.class))),
            @ApiResponse(responseCode = "401",
                    description = "Unauthorized"),
            @ApiResponse(responseCode = "403",
                    description = "Forbidden"),
            @ApiResponse(responseCode = "404",
                    description = "Not Found")})
    @PatchMapping(value = "/{id}",
            produces = {MediaType.APPLICATION_JSON_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    //PATCH http://localhost:8080/ads/{id}
    //    available only to the admin or the user who created this ad
    @PreAuthorize("@userSecurity.isAdsAuthor(#id) or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<AdsDto> updateAds(
            @Parameter(in = ParameterIn.PATH,
                    description = "id of ad to process")
            @PathVariable("id") Integer id,
            @Parameter(description = "JSON for update ads")
            @RequestBody @Validated CreateAdsDto body) {
        return ResponseEntity.ok(adsServiceImpl.updateAds(id, body));
    }

    @Operation(
            summary = "getComments",
            description = "return comment by id of ad",
            tags = {"Объявления"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "OK",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CommentDto.class))),
            @ApiResponse(responseCode = "404",
                    description = "Not Found")})
    @GetMapping(value = "/{ad_pk}/comments/{id}",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    //      GET http://localhost:8080/ads/{idAds}/comments/{idComment}
    // available only for the authenticated users
    public ResponseEntity<CommentDto> getComment(
            @Parameter(in = ParameterIn.PATH,
                    description = "id of ad")
            @PathVariable("ad_pk") Integer adPk,
            @Parameter(in = ParameterIn.PATH,
                    description = "id of comment to return")
            @PathVariable("id") Integer id) {
        return ResponseEntity.ok(adsServiceImpl.getCommentOfAds(adPk, id));
    }

    @Operation(
            summary = "deleteComments",
            description = "delete comment by id of ad and id of comment",
            tags = {"Объявления"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "OK"),
            @ApiResponse(responseCode = "401",
                    description = "Unauthorized"),
            @ApiResponse(responseCode = "403",
                    description = "Forbidden"),
            @ApiResponse(responseCode = "404",
                    description = "Not Found")})
    @DeleteMapping(value = "/{ad_pk}/comments/{id}")
//    @PreAuthorize("@userSecurity.isCommentAuthor(#id)")
    @PreAuthorize("@userSecurity.isCommentAuthor(#id) or hasAuthority('ROLE_ADMIN')")
//    available only to the admin or the user who created this comment
    public ResponseEntity<Void> deleteComments(
            @Parameter(in = ParameterIn.PATH,
                    description = "id of ad")
            @PathVariable("ad_pk") Integer adPk,
            @Parameter(in = ParameterIn.PATH,
                    description = "id of comment to return")
            @PathVariable("id") Integer id) {
        return adsServiceImpl.removeCommentsForAds(adPk, id);
    }


    @Operation(
            summary = "updateComments",
            description = "update comment by id of ad and id of comment",
            tags = {"Объявления"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "OK",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CommentDto.class))),
            @ApiResponse(responseCode = "401",
                    description = "Unauthorized"),
            @ApiResponse(responseCode = "403",
                    description = "Forbidden"),
            @ApiResponse(responseCode = "404",
                    description = "Not Found")})
    @PatchMapping(value = "/{ad_pk}/comments/{id}",
            produces = {MediaType.APPLICATION_JSON_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    //    available only to the admin or the user who created this comment
    // PATCH http://localhost:8080/ads/{id}/comments
    @PreAuthorize("@userSecurity.isCommentAuthor(#id) or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<CommentDto> updateComments(
            @Parameter(in = ParameterIn.PATH,
                    description = "id of ad")
            @PathVariable("ad_pk") Integer adPk,
            @Parameter(in = ParameterIn.PATH,
                    description = "id of comment to return")
            @PathVariable("id") Integer id,
            @Parameter(description = "JSON for update comment")
            @RequestBody @Validated CommentDto body) {
        CommentDto commentDto = adsServiceImpl.updateCommentsForAds(adPk, id, body);
        return ResponseEntity.ok(commentDto);
    }

    @Operation(
            summary = "getAdsMe",
            description = "return all ads by user with authentication",
            tags = {"Объявления"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "OK",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ResponseWrapperAdsDto.class))),
            @ApiResponse(responseCode = "401",
                    description = "Unauthorized"),
            @ApiResponse(responseCode = "403",
                    description = "Forbidden"),
            @ApiResponse(responseCode = "404",
                    description = "Not Found")})
    @GetMapping(value = "/me", produces = {MediaType.APPLICATION_JSON_VALUE})
    //      http://localhost:8080/ads/me
    public ResponseEntity<ResponseWrapperAdsDto> getAdsMeUsingGET(Authentication authentication) {
        return ResponseEntity.ok(adsServiceImpl.getALLAdsOfMe(authentication.getName()));
    }


    @Operation(
            summary = "updateAdsImage",
            description = "update image of ad",
            tags = {"Изображения"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "OK", content = @Content(
                    mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE,
                    array = @ArraySchema(schema = @Schema(implementation = byte[].class)))),
            @ApiResponse(responseCode = "404",
                    description = "Not Found")})
    @PatchMapping(value = "{idAds}/image",
            produces = {MediaType.APPLICATION_OCTET_STREAM_VALUE},
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    // available only to the admin or the user who created ad with this image
    //PATCH http://localhost:8080/ads/{id}/image
    @PreAuthorize("@userSecurity.isAdsAuthor(#idAds) or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<byte[]> updateImage(
            @Parameter(in = ParameterIn.PATH,
                    description = "id of ad to process")
            @PathVariable("idAds") Integer idAds,
            @Parameter(description = "new image for replace old image")
            @RequestPart MultipartFile image) throws IOException {
        Pair<byte[], String> pair = adsServiceImpl.updateImageOfAds(idAds, image);
        return read(pair);
    }

    @Operation(
            summary = "getImage",
            description = "return data of image of ad",
            tags = {"Изображения"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "OK",
                    content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = byte[].class)))),
            @ApiResponse(responseCode = "404",
                    description = "Not Found")})
    @GetMapping(value = "{idAds}/image",
            produces = {MediaType.APPLICATION_OCTET_STREAM_VALUE})
    // available for everyone
    public ResponseEntity<byte[]> getImage(
            @Parameter(in = ParameterIn.PATH,
                    description = "id of ad to process")
            @PathVariable("idAds") Integer idAds) {
        Pair<byte[], String> pair = adsServiceImpl.getImage(idAds);
        return read(pair);
    }

    @Operation(
            summary = "findByTitleLike",
            description = "returns list of ads based on a partial title match",
            tags = {"Объявления"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "OK",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ResponseWrapperAdsDto.class)))})
    // available for everyone
    @GetMapping(value = "/by-title", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ResponseWrapperAdsDto> findByTitleLike(
            @Parameter(description = "title for find ads")
            @Param("title") String title) {
        return ResponseEntity.ok(adsServiceImpl.findAdsByTitle(title));
    }

    private ResponseEntity<byte[]> read(Pair<byte[], String> pair) {
        return ResponseEntity.ok()
                .contentLength(pair.getFirst().length)
                .contentType(MediaType.parseMediaType(pair.getSecond()))
                .body(pair.getFirst());
    }
}
