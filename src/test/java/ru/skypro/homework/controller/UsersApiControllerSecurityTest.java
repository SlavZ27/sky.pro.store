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
import org.springframework.web.context.WebApplicationContext;
import ru.skypro.homework.component.UserSecurity;
import ru.skypro.homework.mapper.UserMapperImpl;
import ru.skypro.homework.repository.AuthorityRepository;
import ru.skypro.homework.repository.AvatarRepository;
import ru.skypro.homework.repository.UsersRepository;
import ru.skypro.homework.service.impl.AuthorityService;
import ru.skypro.homework.service.impl.AvatarServiceImpl;
import ru.skypro.homework.service.impl.UserServiceImpl;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UsersApiController.class)
@ActiveProfiles("test1")
@RunWith(SpringJUnit4ClassRunner.class)
@TestMethodOrder(MethodOrderer.DisplayName.MethodName.class)
class UsersApiControllerSecurityTest {
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
    @DisplayName("PATCH http://localhost:8080/users/me with Owner(status != 403)")
    void updateUserWhenUserIsOwnerTest() throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .patch("http://localhost:8080/users/me")
                .with(user("2").roles("USER"))
                .with(csrf());
        MvcResult mvcResult = mockMvc.perform(builder)
                .andReturn();
        assertThat(mvcResult.getResponse().getStatus()).isNotEqualTo(status().isForbidden());
    }

    @Test
    @DisplayName("PATCH http://localhost:8080/users/me with Admin(status != 403)")
    void updateUserWhenUserIsAdminTest() throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .patch("http://localhost:8080/users/me")
                .with(user("2").roles("ADMIN"))
                .with(csrf());
        MvcResult mvcResult = mockMvc.perform(builder)
                .andReturn();
        assertThat(mvcResult.getResponse().getStatus()).isNotEqualTo(status().isForbidden());
    }

    @Test
    @DisplayName("PATCH http://localhost:8080/users/me with notOwner(status = 403)")
    @WithMockUser(username = "1")
    void updateUserWhenUserNotOwnerTest() throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .patch("http://localhost:8080/users/me");
        ResultActions resultActions = mockMvc.perform(builder);
        resultActions
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("PATCH http://localhost:8080/users/me/image with Owner(status != 403)")
    void updateUserImageWhenUserIsOwnerTest() throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .patch("http://localhost:8080/users/me/image")
                .with(user("2").roles("USER"))
                .with(csrf());
        MvcResult mvcResult = mockMvc.perform(builder)
                .andReturn();
        assertThat(mvcResult.getResponse().getStatus()).isNotEqualTo(status().isForbidden());
    }

    @Test
    @DisplayName("PATCH http://localhost:8080/users/me/image with Admin(status != 403)")
    void updateUserImageWhenUserIsAdminTest() throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .patch("http://localhost:8080/users/me/image")
                .with(user("2").roles("ADMIN"))
                .with(csrf());
        MvcResult mvcResult = mockMvc.perform(builder)
                .andReturn();
        assertThat(mvcResult.getResponse().getStatus()).isNotEqualTo(status().isForbidden());
    }

    @Test
    @DisplayName("PATCH http://localhost:8080/users/me/image with notOwner(status = 403)")
    @WithMockUser(username = "1")
    void updateUserImageTest() throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .patch("http://localhost:8080/users/me/image");
        ResultActions resultActions = mockMvc.perform(builder);
        resultActions
                .andExpect(status().isForbidden());
    }
}
