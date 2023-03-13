package ru.skypro.homework.controller;

import org.junit.jupiter.api.*;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

import org.springframework.web.context.WebApplicationContext;
import ru.skypro.homework.component.UserSecurity;
import ru.skypro.homework.mapper.*;
import ru.skypro.homework.repository.*;
import ru.skypro.homework.service.impl.*;

import java.util.Random;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdsApiController.class)
@ActiveProfiles("test1")
@RunWith(SpringJUnit4ClassRunner.class)
@TestMethodOrder(MethodOrderer.DisplayName.class)
class AdsApiControllerWebMvcIntegrationTest {
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
    private final Random random = new Random();


    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
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
    @DisplayName("DELETE http://localhost:8080/ads/{id} with Author(status != 403)")
    void removeAdsWhenUserIsAuthorTest() throws Exception {
        int index = random.nextInt();
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .delete("http://localhost:8080/ads/" + index)
                .with(user("2").roles("USER"))
                .with(csrf());
        MvcResult mvcResult = mockMvc.perform(builder)
                .andReturn();
        assertThat(mvcResult.getResponse().getStatus()).isNotEqualTo(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE http://localhost:8080/ads/{id} with Admin(status != 403)")
    void removeAdsWhenUserIsAdminTest() throws Exception {
        int index = random.nextInt();
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .delete("http://localhost:8080/ads/" + index)
                .with(user("admin").roles("ADMIN"))
                .with(csrf());
        MvcResult mvcResult = mockMvc.perform(builder)
                .andReturn();
        assertThat(mvcResult.getResponse().getStatus()).isNotEqualTo(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE http://localhost:8080/ads/{id} with notAuthor(status = 403)")
    @WithMockUser(username = "notAuthor", authorities = "ROLE_USER")
    void removeAdsWhenUserIsNotAuthorTest() throws Exception {
        int index = random.nextInt();
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .delete("http://localhost:8080/ads/" + index);
        ResultActions resultActions = mockMvc.perform(builder);
        resultActions
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("DELETE http://localhost:8080/ads/{idAds}/comments/{idComments} with Author(status != 403)")
    @WithMockUser(username = "author")
    void deleteCommentsWhenUserIsAuthorTest() throws Exception {
        int index = random.nextInt();
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .delete("http://localhost:8080/ads/" + index + "/comments/" + index)
                .with(user("2").roles("USER"))
                .with(csrf());
        MvcResult mvcResult = mockMvc.perform(builder)
                .andReturn();
        assertThat(mvcResult.getResponse().getStatus()).isNotEqualTo(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE http://localhost:8080/ads/{idAds}/comments/{idComments} with Admin(status != 403)")
    @WithMockUser(username = "admin")
    void deleteCommentsWhenUserIsAdminTest() throws Exception {
        int index = random.nextInt();
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .delete("http://localhost:8080/ads/" + index + "/comments/" + index)
                .with(user("2").roles("ADMIN"))
                .with(csrf());
        MvcResult mvcResult = mockMvc.perform(builder)
                .andReturn();
        assertThat(mvcResult.getResponse().getStatus()).isNotEqualTo(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE http://localhost:8080/ads/{idAds}/comments/{idComments} with notAuthor(status = 403)")
    @WithMockUser(username = "notAuthor")
    void deleteCommentsWhenNotAuthorTest() throws Exception {
        int index = random.nextInt();
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .delete("http://localhost:8080/ads/" + index + "/comments/" + index);
        ResultActions resultActions = mockMvc.perform(builder);
        resultActions
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("PATCH http://localhost:8080/ads/{id} with Author(status != 403)")
    @WithMockUser(username = "author")
    void updateAdsWhenUserIsAuthorTest() throws Exception {
        int index = random.nextInt();
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .patch("http://localhost:8080/ads/" + index)
                .with(user("2").roles("USER"))
                .with(csrf());
        MvcResult mvcResult = mockMvc.perform(builder)
                .andReturn();
        assertThat(mvcResult.getResponse().getStatus()).isNotEqualTo(status().isNoContent());
    }

    @Test
    @DisplayName("PATCH http://localhost:8080/ads/{id} with Admin(status != 403)")
    @WithMockUser(username = "admin")
    void updateAdsWhenUserIsAdminTest() throws Exception {
        int index = random.nextInt();
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .patch("http://localhost:8080/ads/" + index)
                .with(user("2").roles("ADMIN"))
                .with(csrf());
        MvcResult mvcResult = mockMvc.perform(builder)
                .andReturn();
        assertThat(mvcResult.getResponse().getStatus()).isNotEqualTo(status().isNoContent());
    }

    @Test
    @DisplayName("PATCH http://localhost:8080/ads/{id} with notAuthor(status = 403)")
    @WithMockUser(username = "notAuthor")
    void updateAdsTestWhenUserNotAuthor() throws Exception {
        int index = random.nextInt();
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .patch("http://localhost:8080/ads/" + index);
        ResultActions resultActions = mockMvc.perform(builder);
        resultActions
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("PATCH http://localhost:8080/ads/{id}/comments/{id} with Author(status != 403)")
    @WithMockUser(username = "author")
    void updateCommentsWhenUserIsAuthorTest() throws Exception {
        int index = random.nextInt();
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .patch("http://localhost:8080/ads/" + index + "/comments/" + index)
                .with(user("2").roles("USER"))
                .with(csrf());
        MvcResult mvcResult = mockMvc.perform(builder)
                .andReturn();
        assertThat(mvcResult.getResponse().getStatus()).isNotEqualTo(status().isNoContent());
    }

    @Test
    @DisplayName("PATCH http://localhost:8080/ads/{id}/comments/{id} with Author(status != 403)")
    @WithMockUser(username = "admin")
    void updateCommentsWhenUserIsAdminTest() throws Exception {
        int index = random.nextInt();
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .patch("http://localhost:8080/ads/" + index + "/comments/" + index)
                .with(user("2").roles("ADMIN"))
                .with(csrf());
        MvcResult mvcResult = mockMvc.perform(builder)
                .andReturn();
        assertThat(mvcResult.getResponse().getStatus()).isNotEqualTo(status().isNoContent());
    }

    @Test
    @DisplayName("PATCH http://localhost:8080/ads/{id}/comments/{id} with notAuthor(status = 403)")
    @WithMockUser(username = "notAuthor")
    void updateCommentsWhenUserNotAuthorTest() throws Exception {
        int index = random.nextInt();
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .patch("http://localhost:8080/ads/" + index + "/comments/" + index);
        ResultActions resultActions = mockMvc.perform(builder);
        resultActions
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("PATCH http://localhost:8080/ads/{id}/image with Author(status != 403)")
    @WithMockUser(username = "Author")
    void updateImageWhenUserIsAuthorTest() throws Exception {
        int index = random.nextInt();
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .patch("http://localhost:8080/ads/" + index + "/image")
                .with(user("2").roles("USER"))
                .with(csrf());
        MvcResult mvcResult = mockMvc.perform(builder)
                .andReturn();
        assertThat(mvcResult.getResponse().getStatus()).isNotEqualTo(status().isNoContent());
    }

    @Test
    @DisplayName("PATCH http://localhost:8080/ads/{id}/image with Admin(status != 403)")
    @WithMockUser(username = "admin")
    void updateImageWhenUserIsAdminTest() throws Exception {
        int index = random.nextInt();
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .patch("http://localhost:8080/ads/" + index + "/image")
                .with(user("2").roles("ADMIN"))
                .with(csrf());
        MvcResult mvcResult = mockMvc.perform(builder)
                .andReturn();
        assertThat(mvcResult.getResponse().getStatus()).isNotEqualTo(status().isNoContent());
    }

    @Test
    @DisplayName("PATCH http://localhost:8080/ads/{id}/image with notAuthor(status = 403)")
    @WithMockUser(username = "notAuthor")
    void updateImageWhenUserNotAuthorTest() throws Exception {
        int index = random.nextInt();
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .patch("http://localhost:8080/ads/" + index + "/image");
        ResultActions resultActions = mockMvc.perform(builder);
        resultActions
                .andExpect(status().isForbidden());
    }
}
