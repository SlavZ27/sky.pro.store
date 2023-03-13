package ru.skypro.homework.controller;

import org.junit.jupiter.api.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.skypro.homework.Generator;
import ru.skypro.homework.dto.LoginReqDto;
import ru.skypro.homework.dto.NewPasswordDto;
import ru.skypro.homework.dto.RegisterReqDto;
import ru.skypro.homework.entity.*;
import ru.skypro.homework.repository.*;
import ru.skypro.homework.service.impl.AuthServiceImpl;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.DisplayName.class)
class AuthControllerTest {
    @Autowired
    private AuthorityRepository authorityRepository;
    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private TestRestTemplate testRestTemplate;
    private final Generator generator = new Generator();
    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    @BeforeEach
    public void generateData() {
        authorityRepository.deleteAll();
        usersRepository.deleteAll();

        int countUserAdmin = 2;
        int countUser = 5;

        //generate userAdmin
        for (int i = 0; i < countUserAdmin; i++) {
            User user = usersRepository.save(generator.generateUser(null, "password"));
            authorityRepository.save(generator.generateAuthority(user, Role.ADMIN));
        }
        //generate user
        for (int i = 0; i < countUser; i++) {
            User user = usersRepository.save(generator.generateUser(null, "password"));
            authorityRepository.save(generator.generateAuthority(user, Role.USER));
        }
    }

    @AfterEach
    public void clearData() {
        authorityRepository.deleteAll();
        usersRepository.deleteAll();
    }

    @Test
    void contextLoads() {
        assertThat(authorityRepository).isNotNull();
        assertThat(usersRepository).isNotNull();
    }

    @Test
    @DisplayName("GET http://localhost:8080/register 201")
    public void registerTest() {
        String firstName = "FirstName";
        String lastName = "LastName";
        String phone = "+79111111111";
        String role = Role.USER.getRole();
        String username = "username@gmail.com";
        String password = "password";

        List<User> userList = usersRepository.findAll().stream()
                .filter(user -> user.getUsername().equals(username))
                .collect(Collectors.toList());
        List<Authority> authorityList = authorityRepository.findAll().stream()
                .filter(authority -> authority.getUsername().equals(username))
                .collect(Collectors.toList());
        assertThat(userList.size()).isEqualTo(0);
        assertThat(authorityList.size()).isEqualTo(0);

        RegisterReqDto registerReqDto = new RegisterReqDto();
        registerReqDto.setFirstName(firstName);
        registerReqDto.setLastName(lastName);
        registerReqDto.setPhone(phone);
        registerReqDto.setRole(role);
        registerReqDto.setUsername(username);
        registerReqDto.setPassword(password);

        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPhone(phone);
        user.setUsername(username);
        user.setPassword(AuthServiceImpl.PAS_PREFIX + passwordEncoder.encode(password));
        user.setEnabled(true);
        user.setRegDate(LocalDate.now());

        ResponseEntity<Void> response = testRestTemplate.
                postForEntity("/register", registerReqDto, Void.class);
        userList = usersRepository.findAll().stream()
                .filter(user1 -> user1.getUsername().equals(username))
                .collect(Collectors.toList());
        authorityList = authorityRepository.findAll().stream()
                .filter(authority -> authority.getUsername().equals(username))
                .collect(Collectors.toList());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(userList.size()).isEqualTo(1);
        assertThat(userList.get(0))
                .usingRecursiveComparison()
                .ignoringFields("id", "password")
                .isEqualTo(user);
        assertThat(
                passwordEncoder.matches(
                        password,
                        userList.get(0).getPassword().substring(8)))
                .isTrue();
        assertThat(authorityList.size()).isEqualTo(1);
        assertThat(authorityList.get(0).getAuthority()).isEqualTo(Role.USER.getRole());
    }

    @Test
    @DisplayName("GET http://localhost:8080/register 409")
    public void registerNegativeTest() {
        User userExistUsername = usersRepository.findAll().stream().findAny().orElse(null);
        assert userExistUsername != null;

        RegisterReqDto registerReqDto = new RegisterReqDto();
        registerReqDto.setFirstName(userExistUsername.getFirstName());
        registerReqDto.setLastName(userExistUsername.getLastName());
        registerReqDto.setPhone(userExistUsername.getPhone());
        registerReqDto.setRole(Role.USER.getRole());
        registerReqDto.setUsername(userExistUsername.getUsername());
        registerReqDto.setPassword("password");

        int countUser = usersRepository.findAll().size();
        int countAuth = authorityRepository.findAll().size();

        ResponseEntity<Void> response = testRestTemplate.
                postForEntity("/register", registerReqDto, Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(usersRepository.findAll().size()).isEqualTo(countUser);
        assertThat(authorityRepository.findAll().size()).isEqualTo(countAuth);
    }

    @Test
    @DisplayName("GET http://localhost:8080/login 200")
    public void loginTest() {
        User userExistUsername = usersRepository.findAll().stream().findAny().orElse(null);
        assert userExistUsername != null;
        LoginReqDto loginReqDto = new LoginReqDto();
        loginReqDto.setUsername(userExistUsername.getUsername());
        loginReqDto.setPassword("password");
        ResponseEntity<Void> response = testRestTemplate.
                postForEntity("/login", loginReqDto, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("GET http://localhost:8080/login 403")
    public void loginNegativeTest() {
        String username = "0987654321@gmail.com";
        assertThat(usersRepository.findAll().stream()
                .filter(user -> user.getUsername().equals(username))
                .findAny().isEmpty()).isTrue();
        LoginReqDto loginReqDto = new LoginReqDto();
        loginReqDto.setUsername(username);
        loginReqDto.setPassword("password");
        ResponseEntity<Void> response = testRestTemplate.
                postForEntity("/login", loginReqDto, Void.class);
        assertThat(response.getStatusCode()).
                isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    @DisplayName("GET http://localhost:8080/users/set_password 200")
    public void setPasswordTest() {
        User userExistUsername = usersRepository.findAll().stream().findAny().orElse(null);
        assert userExistUsername != null;
        assertThat(usersRepository.findAll().stream()
                .filter(user -> user.getUsername().equals(userExistUsername.getUsername()))
                .count()).isEqualTo(1);
        String passwordOld = "password";
        String passwordNew = "1234567890";
        NewPasswordDto newPasswordDto = new NewPasswordDto();
        newPasswordDto.setCurrentPassword(passwordOld);
        newPasswordDto.setNewPassword(passwordNew);
        ResponseEntity<NewPasswordDto> response = testRestTemplate
                .withBasicAuth(userExistUsername.getUsername(), passwordOld)
                .postForEntity("/users/set_password", newPasswordDto, NewPasswordDto.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<User> users = usersRepository.findAll().stream()
                .filter(user -> user.getUsername().equals(userExistUsername.getUsername()))
                .collect(Collectors.toList());
        assertThat(users.size()).isEqualTo(1);
        assertThat(
                passwordEncoder.matches(
                        passwordNew,
                        users.get(0).getPassword().substring(8)))
                .isTrue();
    }
}