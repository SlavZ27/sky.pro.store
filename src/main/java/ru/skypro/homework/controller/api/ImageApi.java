/**
 * NOTE: This class is auto generated by the swagger code generator program (3.0.40).
 * https://github.com/swagger-api/swagger-codegen
 * Do not edit the class manually.
 */
package ru.skypro.homework.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

@javax.annotation.Generated(value = "ru.skypro.homeworkcodegen.v3.generators.java.SpringCodegen", date = "2023-02-06T18:24:36.081075022Z[GMT]")
@Validated
public interface ImageApi {

    @Operation(summary = "updateAdsImage", description = "", tags={ "Изображения" })
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK", content = @Content(mediaType = "application/octet-stream", array = @ArraySchema(schema = @Schema(implementation = byte[].class)))),
        
        @ApiResponse(responseCode = "404", description = "Not Found") })
    @RequestMapping(value = "/image/{id}",
        produces = { "application/octet-stream" }, 
        consumes = { "multipart/form-data" }, 
        method = RequestMethod.PATCH)
    ResponseEntity<List<byte[]>> updateImage(@Parameter(in = ParameterIn.PATH, description = "", required=true, schema=@Schema()) @PathVariable("id") Integer id, @Parameter(description = "file detail") @Valid @RequestPart("file") MultipartFile image);

}

