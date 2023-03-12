package ru.skypro.homework.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.junit.jupiter.api.*;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.skypro.homework.Generator;
import ru.skypro.homework.component.UserSecurity;
import ru.skypro.homework.dto.UserDto;
import ru.skypro.homework.entity.Avatar;
import ru.skypro.homework.entity.User;
import ru.skypro.homework.mapper.UserMapperImpl;
import ru.skypro.homework.repository.AuthorityRepository;
import ru.skypro.homework.repository.AvatarRepository;
import ru.skypro.homework.repository.UsersRepository;
import ru.skypro.homework.service.impl.AuthorityService;
import ru.skypro.homework.service.impl.AvatarServiceImpl;
import ru.skypro.homework.service.impl.UserServiceImpl;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UsersApiController.class)
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@TestMethodOrder(MethodOrderer.DisplayName.MethodName.class)
class UsersApiControllerMockMvcTest {
    private final String dirForAvatars;
    @InjectMocks
    private UsersApiController usersApiController;
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext context;
    @MockBean
    private AvatarRepository avatarRepository;
    @MockBean
    private AuthorityRepository authorityRepository;
    @MockBean
    private UsersRepository usersRepository;
    @MockBean(name = "userSecurity")
    private UserSecurity userSecurity;
    @SpyBean
    private UserServiceImpl userService;
    @SpyBean
    private UserMapperImpl userMapper;
    @SpyBean
    private AvatarServiceImpl avatarService;
    @SpyBean
    private AuthorityService authorityService;
    private final Generator generator = new Generator();
    private final ObjectWriter objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();

    UsersApiControllerMockMvcTest(@Value("${path.to.avatars.folder}") String dirForAvatars) {
        this.dirForAvatars = dirForAvatars;
    }

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
        when(userSecurity.isAdsAuthor(any())).thenReturn(true);
        when(userSecurity.isCommentAuthor(any())).thenReturn(true);
    }

    @AfterAll
    public static void cleanContext() {
    }

    @Test
    public void contextsLoad() {
        assertThat(usersApiController).isNotNull();
        assertThat(userSecurity).isNotNull();
        assertThat(avatarService).isNotNull();
        assertThat(authorityRepository).isNotNull();
        assertThat(userService).isNotNull();
        assertThat(userMapper).isNotNull();
        assertThat(usersRepository).isNotNull();
        assertThat(avatarRepository).isNotNull();
        assertThat(authorityService).isNotNull();
        assertThat(mockMvc).isNotNull();
    }

    @Test
    @DisplayName("GET http://localhost:8080/users/me 200")
    @WithMockUser(username = "1", authorities = "ROLE_USER")
    void getUserTest() throws Exception {
        String username = "1";
        User user = generator.generateUser(null, null);

        UserDto userDto = userMapper.userToDto(user);
        String userDtoJSON = objectWriter.writeValueAsString(userDto);
        when(usersRepository.findByUsername(username)).thenReturn(Optional.of(user));

        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders
                        .get("http://localhost:8080/users/me")
                        .accept(MediaType.APPLICATION_JSON);
        ResultActions resultActions = mockMvc.perform(builder);
        resultActions
                .andExpect(status().isOk())
                .andExpect(content().json(userDtoJSON));
    }

    @Test
    @DisplayName("GET http://localhost:8080/users/me/image 200")
    @WithMockUser(username = "1", authorities = {"ROLE_USER"})
    void getAvatarTest() throws Exception {
        String username = "1";
        Avatar avatar = generator.generateAvatarIfNull(null, dirForAvatars);
        avatar.setId(111);
        User user = generator.generateUser(avatar, null);

        when(usersRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(avatarRepository.findById(avatar.getId())).thenReturn(Optional.of(avatar));

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .get("http://localhost:8080/users/me/image")
                .contentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        MvcResult mvcResult = mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andReturn();
        assertThat(mvcResult.getResponse().getContentAsByteArray())
                .isEqualTo(Files.readAllBytes(Path.of(avatar.getPath())));
    }

    @Test
    @DisplayName("GET http://localhost:8080/users/{idUser}/image 200")
    @WithMockUser(username = "1", authorities = {"ROLE_USER"})
    void getAvatarOfUserTest() throws Exception {
        Avatar avatar = generator.generateAvatarIfNull(null, dirForAvatars);
        avatar.setId(222);
        User user = generator.generateUser(null, null);
        user.setId(111);
        user.setAvatar(avatar);

        when(usersRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(avatarRepository.findById(avatar.getId())).thenReturn(Optional.of(avatar));

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .get("http://localhost:8080/users/" + user.getId() + "/image")
                .contentType(MediaType.MULTIPART_FORM_DATA);
        MvcResult mvcResult = mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andReturn();
        assertThat(mvcResult.getResponse().getContentAsByteArray())
                .isEqualTo(Files.readAllBytes(Path.of(avatar.getPath())));
    }

    @Test
    @DisplayName("PATCH http://localhost:8080/users/me 200")
    @WithMockUser(username = "1", authorities = {"ROLE_USER"})
    void updateUserTest() throws Exception {
        String username = "1";
        User userOld = generator.generateUser(null, null);
        User userNew = generator.generateUser(null, null);
        userNew.setId(userOld.getId());
        userNew.setEmail("Updated@mail.ru");
        userNew.setFirstName("updatedUser");

        UserDto userOldDto = userMapper.userToDto(userOld);
        UserDto newUserDto = userMapper.userToDto(userNew);

        when(usersRepository.findByUsername(username)).thenReturn(Optional.of(userOld));
        when(usersRepository.save(any(User.class))).thenReturn(userNew);

        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders
                        .patch("http://localhost:8080/users/me", userOld)
                        .content(objectWriter.writeValueAsString(userOldDto))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .with(csrf());
        mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(content().json(objectWriter.writeValueAsString(newUserDto)));
    }

    @Test
    @DisplayName("PATCH http://localhost:8080/users/me 404")
    @WithMockUser(username = "1", authorities = {"ROLE_USER"})
    void updateUserNegativeTest() throws Exception {
        String username = "1";
        User user = generator.generateUser(null, null);
        UserDto userDto = userMapper.userToDto(user);

        when(usersRepository.findByUsername(username)).thenReturn(Optional.empty());
        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders
                        .patch("http://localhost:8080/users/me", user)
                        .content(objectWriter.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .with(csrf());
        mockMvc.perform(builder)
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PATCH http://localhost:8080/users/me/image 200")
    @WithMockUser(username = "1", authorities = {"ROLE_USER"})
    void updateUserImageTest() throws Exception {
        User user = generator.generateUser(null, null);
        Avatar avatar = generator.generateAvatarIfNull(null, dirForAvatars);
        user.setUsername("1");
        user.setAvatar(avatar);

        Path newPathFile = generatePath("user_" + user.getId());

        assertThat(generator.getPathsOfFiles(dirForAvatars).size() >= 2).isTrue();

        byte[] data1 = new byte[0];
        byte[] data2 = data1;
        while (Arrays.equals(data1, data2)) {
            data1 = generator.generateDataFileOfImageFromDir(dirForAvatars);
            data2 = generator.generateDataFileOfImageFromDir(dirForAvatars);
        }
        String pathStr1 = dirForAvatars + "/" + "file_for_updateUserImageTest1" + ".jpg";
        Path path1 = Path.of(pathStr1);
        if (!Files.exists(path1)) {
            Files.write(path1, data1);
        }
        avatar.setPath(pathStr1);

        when(usersRepository.save(user)).thenReturn(user);
        when(usersRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(avatarRepository.findById(avatar.getId())).thenReturn(Optional.of(avatar));
        when(avatarRepository.save(avatar)).thenReturn(avatar);

        MockMultipartFile mockMultipartFile = new MockMultipartFile(
                "image",
                "image.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                data2);
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .multipart(
                        HttpMethod.PATCH,
                        "http://localhost:8080/users/me/image"
                )
                .file(mockMultipartFile)
                .accept(MediaType.MULTIPART_FORM_DATA)
                .with(csrf());
        mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andReturn();

        assertThat(Files.readAllBytes(newPathFile)).isEqualTo(data2);
        assertThat(Files.exists(path1)).isFalse();
        Files.deleteIfExists(path1);
        Files.deleteIfExists(newPathFile);

        user.setAvatar(null);
        when(avatarRepository.save(any(Avatar.class))).thenReturn(avatar);
        mockMultipartFile = new MockMultipartFile(
                "image",
                "image.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                data2);
        builder = MockMvcRequestBuilders
                .multipart(
                        HttpMethod.PATCH,
                        "http://localhost:8080/users/me/image"
                )
                .file(mockMultipartFile)
                .accept(MediaType.MULTIPART_FORM_DATA)
                .with(csrf());
        mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andReturn();
        assertThat(Files.readAllBytes(newPathFile)).isEqualTo(data2);
        assertThat(Files.exists(path1)).isFalse();
        Files.deleteIfExists(path1);
        Files.deleteIfExists(newPathFile);
    }

    @Test
    @DisplayName("PATCH http://localhost:8080/users/me/image 404")
    @WithMockUser(username = "1", authorities = {"ROLE_USER"})
    void updateUserImageNegativeTest() throws Exception {
        when(usersRepository.findByUsername(any())).thenReturn(Optional.empty());

        MockMultipartFile mockMultipartFile = new MockMultipartFile("image", "image.jpg",
                MediaType.IMAGE_JPEG_VALUE, new byte[0]);
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .multipart(
                        HttpMethod.PATCH,
                        "http://localhost:8080/users/me/image"
                )
                .file(mockMultipartFile)
                .accept(MediaType.MULTIPART_FORM_DATA)
                .with(csrf());
        ResultActions resultActions = mockMvc.perform(builder);

        resultActions
                .andExpect(status().isNotFound());
    }

    private Path generatePath(String nameFile) {
        String date = LocalDate.now().toString();
        String extension = ".jpg";
        return Paths.get(dirForAvatars).resolve(nameFile + "_" + date + extension);
    }
}
