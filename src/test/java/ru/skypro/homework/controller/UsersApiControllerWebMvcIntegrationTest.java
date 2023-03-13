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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.skypro.homework.Generator;
import ru.skypro.homework.component.UserSecurity;
import ru.skypro.homework.mapper.UserMapperImpl;
import ru.skypro.homework.repository.AuthorityRepository;
import ru.skypro.homework.repository.AvatarRepository;
import ru.skypro.homework.repository.UsersRepository;
import ru.skypro.homework.service.impl.AuthorityService;
import ru.skypro.homework.service.impl.AvatarServiceImpl;
import ru.skypro.homework.service.impl.UserServiceImpl;

import java.util.Random;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UsersApiController.class)
@ActiveProfiles("test1")
@RunWith(SpringJUnit4ClassRunner.class)
@TestMethodOrder(MethodOrderer.DisplayName.MethodName.class)
class UsersApiControllerWebMvcIntegrationTest {
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

    UsersApiControllerWebMvcIntegrationTest(@Value("${path.to.avatars.folder}") String dirForAvatars) {
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
    @DisplayName("PATCH http://localhost:8080/users/me 403")
    @WithMockUser(username = "1")
    void updateUserWhenUserNotOwnerTest() throws Exception {

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .patch("http://localhost:8080/users/me");
        ResultActions resultActions = mockMvc.perform(builder);
        resultActions
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("PATCH http://localhost:8080/users/me/image 403")
    @WithMockUser(username = "1")
    void updateUserImageTest() throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .patch("http://localhost:8080/users/me/image");
        ResultActions resultActions = mockMvc.perform(builder);
        resultActions
                .andExpect(status().isForbidden());
    }
}
