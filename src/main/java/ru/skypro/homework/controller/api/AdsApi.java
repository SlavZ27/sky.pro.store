package ru.skypro.homework.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.*;

import javax.validation.Valid;
import java.io.IOException;

@Validated
@RequestMapping(value = "ads")
public interface AdsApi {

    @Operation(summary = "", description = "", tags = {"Объявления"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ResponseWrapperAdsDto.class)))})
    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    ResponseEntity<ResponseWrapperAdsDto> getALLAds();

    @Operation(summary = "addAds", description = "", tags = {"Объявления"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AdsDto.class))),

            @ApiResponse(responseCode = "401", description = "Unauthorized"),

            @ApiResponse(responseCode = "403", description = "Forbidden"),

            @ApiResponse(responseCode = "404", description = "Not Found")})
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE},
    produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<AdsDto> addAds(
            @Parameter(
                    in = ParameterIn.DEFAULT, description = "",
                    schema = @Schema(implementation = CreateAdsDto.class)) @RequestPart(
                    value = "properties", required = false) CreateAdsDto properties,
            @Parameter(description = "file detail") @Valid @RequestPart("image") MultipartFile image) throws IOException;

    @Operation(summary = "getComments", description = "", tags = {"Объявления"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ResponseWrapperCommentDto.class))),

            @ApiResponse(responseCode = "404", description = "Not Found")})
    @GetMapping(value = "/{ad_pk}/comments",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    ResponseEntity<ResponseWrapperCommentDto> getComments(@Parameter(in = ParameterIn.PATH, description = "", required = true) @PathVariable("ad_pk") Integer adPk);

    @Operation(summary = "addComments", description = "", tags = {"Объявления"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CommentDto.class))),

            @ApiResponse(responseCode = "401", description = "Unauthorized"),

            @ApiResponse(responseCode = "403", description = "Forbidden"),

            @ApiResponse(responseCode = "404", description = "Not Found")})
    @PostMapping(value = "/{ad_pk}/comments",
            produces = {MediaType.APPLICATION_JSON_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    ResponseEntity<CommentDto> addComments(@Parameter(in = ParameterIn.PATH, description = "", required = true) @PathVariable("ad_pk") Integer adPk, @Parameter(in = ParameterIn.DEFAULT, description = "", required = true, schema = @Schema(implementation = CommentDto.class)) @Valid @RequestBody CommentDto body);

    @Operation(summary = "getFullAd", description = "", tags = {"Объявления"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = FullAdsDto.class))),

            @ApiResponse(responseCode = "404", description = "Not Found")})
    @GetMapping(value = "{id}",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    ResponseEntity<FullAdsDto> getAds(@Parameter(in = ParameterIn.PATH, description = "", required = true) @PathVariable("id") Integer id);

    @Operation(summary = "removeAds", description = "", tags = {"Объявления"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "No Content"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Not Found")})     //was not in the specification
    @DeleteMapping(value = "{id_ads}")
    ResponseEntity<Void> removeAds(@Parameter(in = ParameterIn.PATH, description = "", required = true) @PathVariable("id_ads") Integer idAds);

    @Operation(summary = "updateAds", description = "", tags = {"Объявления"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AdsDto.class))),

            @ApiResponse(responseCode = "401", description = "Unauthorized"),

            @ApiResponse(responseCode = "403", description = "Forbidden"),

            @ApiResponse(responseCode = "404", description = "Not Found")})
    @PatchMapping(value = "/{id}",
            produces = {MediaType.APPLICATION_JSON_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    ResponseEntity<AdsDto> updateAds(@Parameter(in = ParameterIn.PATH, description = "", required = true) @PathVariable("id") Integer id, @Parameter(in = ParameterIn.DEFAULT, description = "", required = true, schema = @Schema(implementation = CreateAdsDto.class)) @Valid @RequestBody CreateAdsDto body);

    @Operation(summary = "getComments", description = "", tags = {"Объявления"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CommentDto.class))),

            @ApiResponse(responseCode = "404", description = "Not Found")})
    @GetMapping(value = "/{ad_pk}/comments/{id}",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    ResponseEntity<CommentDto> getComments(@Parameter(in = ParameterIn.PATH, description = "", required = true) @PathVariable("ad_pk") Integer adPk, @Parameter(in = ParameterIn.PATH, description = "", required = true) @PathVariable("id") Integer id);

    @Operation(summary = "deleteComments", description = "", tags = {"Объявления"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),

            @ApiResponse(responseCode = "401", description = "Unauthorized"),

            @ApiResponse(responseCode = "403", description = "Forbidden"),

            @ApiResponse(responseCode = "404", description = "Not Found")})
    @DeleteMapping(value = "/{ad_pk}/comments/{id}")
    ResponseEntity<Void> deleteComments(@Parameter(in = ParameterIn.PATH, description = "", required = true) @PathVariable("ad_pk") String adPk, @Parameter(in = ParameterIn.PATH, description = "", required = true) @PathVariable("id") Integer id);

    @Operation(summary = "updateComments", description = "", tags = {"Объявления"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CommentDto.class))),

            @ApiResponse(responseCode = "401", description = "Unauthorized"),

            @ApiResponse(responseCode = "403", description = "Forbidden"),

            @ApiResponse(responseCode = "404", description = "Not Found")})
    @PatchMapping(value = "/{ad_pk}/comments/{id}",
            produces = {MediaType.APPLICATION_JSON_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    ResponseEntity<CommentDto> updateComments(@Parameter(in = ParameterIn.PATH, description = "", required = true) @PathVariable("ad_pk") Integer adPk, @Parameter(in = ParameterIn.PATH, description = "", required = true) @PathVariable("id") Integer id, @Parameter(in = ParameterIn.DEFAULT, description = "", required = true, schema = @Schema(implementation = CommentDto.class)) @Valid @RequestBody CommentDto body);

    @Operation(summary = "getAdsMe", description = "", tags = {"Объявления"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ResponseWrapperAdsDto.class))),

            @ApiResponse(responseCode = "401", description = "Unauthorized"),

            @ApiResponse(responseCode = "403", description = "Forbidden"),

            @ApiResponse(responseCode = "404", description = "Not Found")})
    @GetMapping(value = "/me",
            produces = {MediaType.APPLICATION_JSON_VALUE})
//    @PostAuthorize("#username == authentication.principal.username")
    ResponseEntity<ResponseWrapperAdsDto> getAdsMeUsingGET(@Parameter(in = ParameterIn.QUERY, description = "") @Valid @RequestParam(value = "username") String username);
}

