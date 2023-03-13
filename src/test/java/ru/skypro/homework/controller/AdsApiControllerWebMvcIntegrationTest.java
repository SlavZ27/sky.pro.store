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
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import org.springframework.web.context.WebApplicationContext;
import ru.skypro.homework.Generator;
import ru.skypro.homework.component.UserSecurity;
import ru.skypro.homework.entity.Ads;
import ru.skypro.homework.entity.Image;
import ru.skypro.homework.entity.User;
import ru.skypro.homework.mapper.*;
import ru.skypro.homework.repository.*;
import ru.skypro.homework.service.impl.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdsApiController.class)
@ActiveProfiles("test1")
@RunWith(SpringJUnit4ClassRunner.class)
@TestMethodOrder(MethodOrderer.DisplayName.class)
class AdsApiControllerWebMvcIntegrationTest {
    private final String dirForImages;
    @InjectMocks
    private AdsApiController adsApiController;
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext context;
    @MockBean
    private ImageRepository imageRepository;
    @MockBean
    private AdsRepository adsRepository;
    @MockBean
    private CommentRepository commentRepository;
    @MockBean
    private AvatarRepository avatarRepository;
    @MockBean
    private AuthorityRepository authorityRepository;
    @MockBean
    private UsersRepository usersRepository;
    @MockBean(name = "userSecurity")
    private UserSecurity userSecurity;
    @SpyBean
    private AdsMapperImpl adsMapper;
    @SpyBean
    private CommentMapperImpl commentMapper;
    @SpyBean
    private UserMapperImpl userMapper;
    @SpyBean
    private AuthorityService authorityService;
    @SpyBean
    private CreateAdsMapperImpl createAdsMapper;
    @SpyBean
    private FullAdsMapperImpl fullAdsMapper;
    @SpyBean
    private AdsServiceImpl adsService;
    @SpyBean
    private UserServiceImpl userService;
    @SpyBean
    private CommentServiceImpl commentService;
    @SpyBean
    private AvatarServiceImpl avatarService;
    @SpyBean
    private ImageServiceImpl imageService;
    private final Generator generator = new Generator();
    private final Random random = new Random();
//    private final ObjectWriter objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
//    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");


    AdsApiControllerWebMvcIntegrationTest(@Value("${path.to.materials.folder}") String dirForImages) {
        this.dirForImages = dirForImages;
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
        assertThat(adsApiController).isNotNull();
        assertThat(userSecurity).isNotNull();
        assertThat(imageService).isNotNull();
        assertThat(avatarService).isNotNull();
        assertThat(commentService).isNotNull();
        assertThat(authorityRepository).isNotNull();
        assertThat(userService).isNotNull();
        assertThat(adsService).isNotNull();
        assertThat(fullAdsMapper).isNotNull();
        assertThat(createAdsMapper).isNotNull();
        assertThat(userMapper).isNotNull();
        assertThat(commentMapper).isNotNull();
        assertThat(adsMapper).isNotNull();
        assertThat(usersRepository).isNotNull();
        assertThat(avatarRepository).isNotNull();
        assertThat(commentRepository).isNotNull();
        assertThat(authorityService).isNotNull();
        assertThat(adsRepository).isNotNull();
        assertThat(imageRepository).isNotNull();
        assertThat(mockMvc).isNotNull();
    }

    @Test
    @DisplayName("DELETE http://localhost:8080/ads/{id} 204")
    void removeAdsWhenUserIsAuthorTest() throws Exception {
        User author = generator.generateUser(null, null);
        Image image = generator.generateImageIfNull(null, dirForImages);

        Ads ads = generator.generateAdsIfNull(null, author, image);
        int indexAds = ads.getId();

        when(adsRepository.findById(indexAds)).thenReturn(Optional.of(ads)).thenReturn(Optional.empty());


        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .delete("http://localhost:8080/ads/" + indexAds)
                .with(user("2").roles("USER"))
                .with(csrf());
        ResultActions resultActions = mockMvc.perform(builder);
        resultActions
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE http://localhost:8080/ads/{id} 204")
    void removeAdsWhenUserIsAdminTest() throws Exception {
        User author = generator.generateUser(null, null);
        Image image = generator.generateImageIfNull(null, dirForImages);

        Ads ads = generator.generateAdsIfNull(null, author, image);
        int indexAds = ads.getId();

        when(adsRepository.findById(indexAds)).thenReturn(Optional.of(ads)).thenReturn(Optional.empty());

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .delete("http://localhost:8080/ads/" + indexAds)
                .with(user("admin").roles("ADMIN"))
                .with(csrf());
        ResultActions resultActions = mockMvc.perform(builder);
        resultActions
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE http://localhost:8080/ads/{id} 403")
    @WithMockUser(username = "notAuthor", authorities = "ROLE_USER")
    void removeAdsWhenUserIsNotAuthorTest() throws Exception {
        int index = random.nextInt();

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .delete("http://localhost:8080/ads/" + index);
        ResultActions resultActions = mockMvc.perform(builder);
        resultActions
                .andExpect(status().isForbidden());
    }
}
