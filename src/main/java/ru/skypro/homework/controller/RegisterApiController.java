package ru.skypro.homework.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import ru.skypro.homework.controller.api.RegisterApi;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import ru.skypro.homework.dto.RegisterReqDto;
import ru.skypro.homework.dto.Role;
import ru.skypro.homework.service.AuthService;

import javax.validation.Valid;
import javax.servlet.http.HttpServletRequest;

import static ru.skypro.homework.dto.Role.USER;

@javax.annotation.Generated(value = "ru.skypro.homeworkcodegen.v3.generators.java.SpringCodegen", date = "2023-02-06T18:24:36.081075022Z[GMT]")
@RestController("register")
@CrossOrigin(value = "http://localhost:3000")
public class RegisterApiController implements RegisterApi {

    private static final Logger log = LoggerFactory.getLogger(RegisterApiController.class);

    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;
    private final AuthService authService;

    public RegisterApiController(ObjectMapper objectMapper, HttpServletRequest request, AuthService authService) {
        this.objectMapper = objectMapper;
        this.request = request;
        this.authService = authService;
    }

    public ResponseEntity<Void> register(@Parameter(in = ParameterIn.DEFAULT, description = "", required=true, schema=@Schema()) @Valid @RequestBody RegisterReqDto body) {
        String accept = request.getHeader("Accept");
        Role role = body.getRole() == null ? USER : body.getRole();
        if (authService.register(body, role)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

}
