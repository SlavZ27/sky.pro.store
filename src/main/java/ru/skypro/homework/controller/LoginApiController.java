package ru.skypro.homework.controller;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.web.bind.annotation.CrossOrigin;
import ru.skypro.homework.controller.api.LoginApi;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import ru.skypro.homework.dto.LoginReqDto;
import ru.skypro.homework.service.AuthService;

import javax.validation.Valid;
import javax.servlet.http.HttpServletRequest;

@javax.annotation.Generated(value = "ru.skypro.homeworkcodegen.v3.generators.java.SpringCodegen", date = "2023-02-06T18:24:36.081075022Z[GMT]")
@RestController("login")
@CrossOrigin(value = "http://localhost:3000")
public class LoginApiController implements LoginApi {

    private static final Logger log = LoggerFactory.getLogger(LoginApiController.class);

    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;

    private final AuthService authService;

    public LoginApiController(ObjectMapper objectMapper, HttpServletRequest request, AuthService authService) {
        this.objectMapper = objectMapper;
        this.request = request;
        this.authService = authService;
    }

    public ResponseEntity<Object> login(@Parameter(in = ParameterIn.DEFAULT, description = "", required=true, schema=@Schema()) @Valid @RequestBody LoginReqDto body) {
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            if (authService.login(body.getUsername(), body.getPassword())) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }

        return new ResponseEntity<Object>(HttpStatus.NOT_IMPLEMENTED);
    }

}
