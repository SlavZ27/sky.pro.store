package ru.skypro.homework.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.controller.api.ImageApi;
import ru.skypro.homework.service.impl.ImageServiceImpl;

import javax.validation.Valid;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@CrossOrigin(value = "http://localhost:3000")
public class ImageApiController implements ImageApi {

    private final ImageServiceImpl imageService;

    public ImageApiController(ImageServiceImpl imageService) {
        this.imageService = imageService;
    }

    @Override
    public ResponseEntity<byte[]> updateImage(Integer id, MultipartFile image) {
        Pair<byte[], String> pair = imageService.updateImage(id, image);
        return read(pair);
    }

    @Override
    public ResponseEntity<byte[]> getImage(Integer idImage) {
        Pair<byte[], String> pair = imageService.getImage(idImage);
        return read(pair);
    }

    private ResponseEntity<byte[]> read(Pair<byte[], String> pair) {
        return ResponseEntity.ok()
                .contentLength(pair.getFirst().length)
                .contentType(MediaType.parseMediaType(pair.getSecond()))
                .body(pair.getFirst());
    }
}
