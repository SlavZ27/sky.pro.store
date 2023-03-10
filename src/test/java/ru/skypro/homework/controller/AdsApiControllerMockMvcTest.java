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
import org.springframework.http.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.skypro.homework.Generator;
import ru.skypro.homework.component.UserSecurity;
import ru.skypro.homework.dto.FullAdsDto;
import ru.skypro.homework.dto.ResponseWrapperAdsDto;
import ru.skypro.homework.dto.ResponseWrapperCommentDto;
import ru.skypro.homework.entity.Ads;
import ru.skypro.homework.entity.Comment;
import ru.skypro.homework.entity.Image;
import ru.skypro.homework.entity.User;
import ru.skypro.homework.mapper.*;
import ru.skypro.homework.repository.*;
import ru.skypro.homework.service.impl.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdsApiController.class)
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@TestMethodOrder(MethodOrderer.DisplayName.MethodName.class)
class AdsApiControllerMockMvcTest {
    private final String dirForImages;
    private final String dirForAvatars;
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
    ObjectWriter objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();

    AdsApiControllerMockMvcTest(@Value("${path.to.materials.folder}") String dirForImages, @Value("${path.to.avatars.folder}") String dirForAvatars) {
        this.dirForImages = dirForImages;
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
        assertThat(adsRepository).isNotNull();
        assertThat(imageRepository).isNotNull();
        assertThat(mockMvc).isNotNull();
    }


    @Test
    @DisplayName("PATCH http://localhost:8080/ads/{id}/image 200")
    @WithMockUser(username = "1", authorities = {"ROLE_USER"})
    void updateImageTest() throws Exception {
        User user = generator.generateUserRoleUser(null, null);
        user.setId(111);
        Image image = generator.generateImageIfNull(null, dirForImages);
        image.setId(222);
        Ads ads = generator.generateAdsIfNull(null, user, image);
        ads.setId(333);
        Path newPathFile = generatePath("ads_" + ads.getId());

        //need min 2 image in dirForImages
        assertThat(generator.getPathsOfFiles(dirForImages).size() >= 2).isTrue();
        //get 2 different images
        byte[] data1 = new byte[0];
        byte[] data2 = data1;
        while (Arrays.equals(data1, data2)) {
            data1 = generator.generateDataFileOfImageFromDir(dirForImages);
            data2 = generator.generateDataFileOfImageFromDir(dirForImages);
        }
        //create picture from data1 for actualImage
        String pathStr1 = dirForImages + "/" + "file_for_updateImageTest1" + ".jpg";
        Path path1 = Path.of(pathStr1);
        if (!Files.exists(path1)) {
            Files.write(path1, data1);
        }
        image.setPath(pathStr1);

        when(adsRepository.save(ads)).thenReturn(ads);
        when(adsRepository.findById(ads.getId())).thenReturn(Optional.of(ads));
        when(imageRepository.findById(image.getId())).thenReturn(Optional.of(image));
        when(imageRepository.save(image)).thenReturn(image);
        //check with standard parameters  (ads exist, image exist)

        MockMultipartFile mockMultipartFile = new MockMultipartFile("image", "image.jpg",
                MediaType.IMAGE_JPEG_VALUE, data2);

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .multipart(
                        HttpMethod.PATCH,
                        "http://localhost:8080/ads/" + ads.getId() + "/image"
                )
                .file(mockMultipartFile)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .with(csrf());
        MvcResult mvcResult = mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andReturn();
        assertThat(mvcResult.getResponse().getContentAsByteArray()).isEqualTo(data2);


        assertThat(Files.readAllBytes(newPathFile)).isEqualTo(data2);
        assertThat(Files.exists(path1)).isFalse();
        Files.deleteIfExists(path1);
        Files.deleteIfExists(newPathFile);

        //check with standard parameters  (ads exist, image non-exist)
        ads.setImage(null);
        when(imageRepository.save(any(Image.class))).thenReturn(image);
        mockMultipartFile = new MockMultipartFile("image", "image.jpg",
                MediaType.IMAGE_JPEG_VALUE, data2);
        builder = MockMvcRequestBuilders
                .multipart(
                        HttpMethod.PATCH,
                        "http://localhost:8080/ads/" + ads.getId() + "/image"
                )
                .file(mockMultipartFile)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .with(csrf());
        mvcResult = mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andReturn();
        assertThat(mvcResult.getResponse().getContentAsByteArray()).isEqualTo(data2);
        assertThat(Files.readAllBytes(newPathFile)).isEqualTo(data2);
        assertThat(Files.exists(path1)).isFalse();
        Files.deleteIfExists(path1);
        Files.deleteIfExists(newPathFile);
    }

    @Test
    @DisplayName("PATCH http://localhost:8080/ads/{id}/image 404")
    @WithMockUser(username = "1", authorities = {"ROLE_USER"})
    void updateImageNegativeTest() throws Exception {
        int index = random.nextInt();
        when(adsRepository.findById(index)).thenReturn(Optional.empty());

        MockMultipartFile mockMultipartFile = new MockMultipartFile("image", "image.jpg",
                MediaType.IMAGE_JPEG_VALUE, new byte[0]);
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .multipart(
                        HttpMethod.PATCH,
                        "http://localhost:8080/ads/" + index + "/image"
                )
                .file(mockMultipartFile)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .with(csrf());
        ResultActions resultActions = mockMvc.perform(builder);

        resultActions
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET http://localhost:8080/ads/{id}/image 404")
    @WithMockUser(username = "1", authorities = {"ROLE_USER"})
    void getImageNegativeTest() throws Exception {
        int index = random.nextInt();
        when(adsRepository.findById(index)).thenReturn(Optional.empty());
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .get("http://localhost:8080/ads/" + index + "/image")
                .contentType(MediaType.MULTIPART_FORM_DATA);
        ResultActions resultActions = mockMvc.perform(builder);
        resultActions
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET http://localhost:8080/ads/ME 200")
    @WithMockUser(username = "1", authorities = "ROLE_USER")
    void getALLAdsOfMeTest() throws Exception {
        String username = "1";
        User user = generator.generateUser(
                1,
                "user",
                "user",
                "user@gmail.com",
                "11111111",
                LocalDate.now(),
                null,
                "username",
                null,
                true);
        int countAds = 2;
        List<Ads> adsList = new ArrayList<>();
        for (int i = 0; i < countAds; i++) {
            adsList.add(generator.generateAdsIfNull(null, user, null));
        }
        ResponseWrapperAdsDto actual = adsMapper.mapToResponseWrapperAdsDto(
                adsMapper.mapListOfAdsToListDTO(adsList), adsList.size());
        String actualJSON = objectWriter.writeValueAsString(actual);
        when(adsRepository.findAllByUsernameAndSortDateTime(username)).thenReturn(adsList);

        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders
                        .get("http://localhost:8080/ads/me")
                        .accept(MediaType.APPLICATION_JSON);
        ResultActions resultActions = mockMvc.perform(builder);
        resultActions
                .andExpect(status().isOk())
                .andExpect(content().json(actualJSON));
    }

    @Test
    @DisplayName("GET http://localhost:8080/ads/{id} 404")
    @WithMockUser(username = "1", authorities = "ROLE_USER")
    void getAdsNegativeTest() throws Exception {
        int index = random.nextInt();
        when(adsRepository.findById(index)).thenReturn(Optional.empty());
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .get("http://localhost:8080/ads/" + index)
                .contentType(MediaType.MULTIPART_FORM_DATA);
        ResultActions resultActions = mockMvc.perform(builder);
        resultActions
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET http://localhost:8080/ads/{id} 200")
    @WithMockUser(username = "1", authorities = "ROLE_USER")
    void getAdsTest() throws Exception {
        int index = random.nextInt();
        User user = generator.generateUserRoleUser(null, null);
        Image image = generator.generateImageIfNull(null, dirForImages);
        Ads adsWithImage = generator.generateAdsIfNull(null, user, image);
        Ads adsWithoutImage = generator.generateAdsIfNull(null, user, null);

        FullAdsDto adsWithImageDto = fullAdsMapper.adsToFullAdsDto(adsWithImage);
        FullAdsDto adsWithoutImageDto = fullAdsMapper.adsToFullAdsDto(adsWithoutImage);
        String adsWithImageDtoJSON = objectWriter.writeValueAsString(adsWithImageDto);
        String adsWithoutImageDtoJSON = objectWriter.writeValueAsString(adsWithoutImageDto);
        when(adsRepository.findById(index)).thenReturn(Optional.of(adsWithImage));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("http://localhost:8080/ads/" + index)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(content().json(adsWithImageDtoJSON));
        when(adsRepository.findById(index)).thenReturn(Optional.of(adsWithoutImage));
        mockMvc.perform(MockMvcRequestBuilders
                        .get("http://localhost:8080/ads/" + index)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(content().json(adsWithoutImageDtoJSON));
    }

    @Test
    @DisplayName("DELETE http://localhost:8080/ads/{id} 404")
    @WithMockUser(username = "1", authorities = "ROLE_USER")
    void removeAdsNegativeTest() throws Exception {
        int index = random.nextInt();
        when(adsRepository.findById(index)).thenReturn(Optional.empty());
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .delete("http://localhost:8080/ads/" + index)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .with(csrf());
        ResultActions resultActions = mockMvc.perform(builder);
        resultActions
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET http://localhost:8080/ads/{id}/comments 404")
    @WithMockUser(username = "1", authorities = "ROLE_USER")
    void getCommentsNegativeTest() throws Exception {
        int indexAds = random.nextInt();
        when(adsRepository.findById(indexAds)).thenReturn(Optional.empty());
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .get("http://localhost:8080/ads/" + indexAds)
                .contentType(MediaType.MULTIPART_FORM_DATA);
        ResultActions resultActions = mockMvc.perform(builder);
        resultActions
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET http://localhost:8080/ads/{id}/comments 200")
    @WithMockUser(username = "1", authorities = "ROLE_USER")
    void getCommentsTest() throws Exception {
        int indexAds = random.nextInt();
        User owner = generator.generateUserRoleUser(null, null);
        Ads ads = generator.generateAdsIfNull(null, owner, null);
        ads.setId(indexAds);

        List<Comment> commentList = new ArrayList<>();
        int countComments = 5;
        for (int i = 0; i < countComments; i++) {
            User user = generator.generateUserRoleUser(null, null);
            commentList.add(generator.generateCommentIfNull(null, ads, user));
        }
        ResponseWrapperCommentDto actualWithComment = commentMapper.mapListOfCommentDtoToResponseWrapper(
                commentList.size(),
                commentList.stream()
                        .map(comment -> commentMapper.commentToDto(comment))
                        .collect(Collectors.toList()));
        String actualJSONWithComment = objectWriter.writeValueAsString(actualWithComment);

        when(adsRepository.findById(indexAds)).thenReturn(Optional.of(ads));
        when(commentRepository.findAllByIdAdsAndSortDateTime(indexAds)).thenReturn(commentList);
        mockMvc.perform(MockMvcRequestBuilders
                        .get("http://localhost:8080/ads/" + indexAds + "/comments")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(actualJSONWithComment));

        commentList.clear();
        ResponseWrapperCommentDto actualWithoutComment = commentMapper.mapListOfCommentDtoToResponseWrapper(
                0,
                new ArrayList<>());
        String actualJSONWithoutComment = objectWriter.writeValueAsString(actualWithoutComment);
        mockMvc.perform(MockMvcRequestBuilders
                        .get("http://localhost:8080/ads/" + indexAds + "/comments")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(actualJSONWithoutComment));
    }

    @Test
    @DisplayName("GET http://localhost:8080/ads 200")
    @WithMockUser(username = "1", authorities = "ROLE_USER")
    void getALLAdsTest() throws Exception {
        List<Ads> adsList = new ArrayList<>();
        int countAds = 10;
        for (int i = 0; i < countAds; i++) {
            User user = generator.generateUserRoleUser(null, null);
            adsList.add(generator.generateAdsIfNull(null, user, null));
        }
        ResponseWrapperAdsDto actual = adsMapper.mapToResponseWrapperAdsDto(
                adsMapper.mapListOfAdsToListDTO(adsList), adsList.size());
        String actualJSON = objectWriter.writeValueAsString(actual);
        when(adsRepository.findAllAndSortDateTime()).thenReturn(adsList);

        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders
                        .get("http://localhost:8080/ads")
                        .accept(MediaType.APPLICATION_JSON);
        ResultActions resultActions = mockMvc.perform(builder);
        resultActions
                .andExpect(status().isOk())
                .andExpect(content().json(actualJSON));
    }

    @Test
    @DisplayName("GET http://localhost:8080/ads/{id}/image 200")
    @WithMockUser(username = "1", authorities = {"ROLE_USER"})
    void getImageTest() throws Exception {
        User user = generator.generateUserRoleUser(null, null);
        user.setId(111);
        Image image = generator.generateImageIfNull(null, dirForImages);
        image.setId(222);
        Ads ads = generator.generateAdsIfNull(null, user, image);
        ads.setId(333);

        when(adsRepository.findById(ads.getId())).thenReturn(Optional.of(ads));
        when(imageRepository.findById(image.getId())).thenReturn(Optional.of(image));

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .get("http://localhost:8080/ads/" + ads.getId() + "/image")
                .contentType(MediaType.MULTIPART_FORM_DATA);
        MvcResult mvcResult = mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andReturn();
        assertThat(mvcResult.getResponse().getContentAsByteArray())
                .isEqualTo(Files.readAllBytes(Path.of(image.getPath())));
    }

    @Test
    @DisplayName("DELETE http://localhost:8080/ads/{id} 204")
    @WithMockUser(username = "1", authorities = "ROLE_USER")
    void removeAdsTest() throws Exception {
        int indexAds = random.nextInt();
        int indexImage = random.nextInt();
        //create user image ads
        User owner = generator.generateUserRoleUser(null, null);
        Image image = generator.generateImageIfNull(null, dirForImages);
        image.setId(indexImage);
        Ads ads = generator.generateAdsIfNull(null, owner, image);
        ads.setId(indexAds);
        //create comments
        List<Comment> commentList = new ArrayList<>();
        int countComments = 5;
        for (int i = 0; i < countComments; i++) {
            commentList.add(generator.generateCommentIfNull(null, ads, owner));
        }
        //copy new picture file for del and save to image
        String pathStr1 = dirForImages + "/" + "file_for_removeAdsTest" + ".jpg";
        Path path1 = Path.of(pathStr1);
        if (!Files.exists(path1)) {
            Files.write(path1, Files.readAllBytes(Path.of(image.getPath())));
        }
        image.setPath(pathStr1);

        //before clear and after clear
        when(adsRepository.findById(indexAds)).thenReturn(Optional.of(ads)).thenReturn(Optional.empty());
        //after clear
        when(imageRepository.findById(indexImage)).thenReturn(Optional.empty());
        when(commentRepository.getCountAllByAdsId(indexAds)).thenReturn(0);
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .delete("http://localhost:8080/ads/" + indexAds)
                .with(csrf());
        ResultActions resultActions = mockMvc.perform(builder);
        resultActions
                .andExpect(status().isNoContent());
    }

    private Path generatePath(String nameFile) {
        String date = LocalDate.now().toString();
        String extension = ".jpg";
        return Paths.get(dirForImages).resolve(nameFile + "_" + date + extension);
    }
}

