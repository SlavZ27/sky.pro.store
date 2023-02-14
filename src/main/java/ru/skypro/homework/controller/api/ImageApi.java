package ru.skypro.homework.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.ResponseWrapperCommentDto;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@Validated
@RequestMapping(value = "image")
public interface ImageApi {

    @Operation(summary = "updateAdsImage", description = "", tags = {"Изображения"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(
                    mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE,
                    array = @ArraySchema(schema = @Schema(implementation = byte[].class)))),
            @ApiResponse(responseCode = "404", description = "Not Found")})
    @PatchMapping(value = "{idAds}",
            produces = {MediaType.APPLICATION_OCTET_STREAM_VALUE},
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    ResponseEntity<List<byte[]>> updateImage(
            @Parameter(
                    in = ParameterIn.PATH, description = "id of ads", required = true) @PathVariable("idAds")
            Integer idAds,
            @Parameter(description = "file detail") @Valid @RequestPart("image") MultipartFile image);

    @Operation(summary = "getImage", description = "", tags = {"Изображения"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = byte[].class)))),
            @ApiResponse(responseCode = "404", description = "Not Found")})
    @GetMapping(value = "{idImage}",
            produces = {MediaType.APPLICATION_OCTET_STREAM_VALUE})
    ResponseEntity<byte[]> getImage(
            @Parameter(in = ParameterIn.PATH, description = "", required = true) @PathVariable("idImage")
            Integer idImage) throws IOException;


}

