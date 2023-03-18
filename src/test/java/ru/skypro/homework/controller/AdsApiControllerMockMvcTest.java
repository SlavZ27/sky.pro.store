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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.skypro.homework.Generator;
import ru.skypro.homework.component.UserSecurity;
import ru.skypro.homework.dto.*;
import ru.skypro.homework.entity.Ads;
import ru.skypro.homework.entity.Comment;
import ru.skypro.homework.entity.Image;
import ru.skypro.homework.entity.User;
import ru.skypro.homework.mapper.*;
import ru.skypro.homework.repository.*;
import ru.skypro.homework.service.impl.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdsApiController.class)
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@TestMethodOrder(MethodOrderer.DisplayName.class)
class AdsApiControllerMockMvcTest {
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
    private AuthorityServiceImpl authorityService;
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
    private final ObjectWriter objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");


    AdsApiControllerMockMvcTest(@Value("${path.to.materials.folder}") String dirForImages) {
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
    @DisplayName("PATCH http://localhost:8080/ads/{id}/image 200")
    @WithMockUser(username = "1", authorities = {"ROLE_USER"})
    void updateImageTest() throws Exception {
        //get List of Files into dirForImages
        List<String> files = generator.getPathsOfFiles(dirForImages);
        //create user
        User user = generator.generateUser(null, null);
        //create image
        Image image = new Image();
        image.setId(1);
        //create ads
        Ads ads = generator.generateAdsIfNull(null, user, image);
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
        //fix path of image
        image.setPath(pathStr1);
        //create file
        MockMultipartFile mockMultipartFile = new MockMultipartFile(
                "image",
                "image.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                data2);
        //create mock
        when(adsRepository.save(ads)).thenReturn(ads);
        when(adsRepository.findById(ads.getId())).thenReturn(Optional.of(ads));
        when(imageRepository.findById(image.getId())).thenReturn(Optional.of(image));
        when(imageRepository.save(image)).thenReturn(image);
        //config request
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .multipart(
                        HttpMethod.PATCH,
                        "http://localhost:8080/ads/" + ads.getId() + "/image"
                )
                .file(mockMultipartFile)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .with(csrf());
        //check with standard parameters  (ads exist, image exist)
        MvcResult mvcResult = mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andReturn();
        //check data2 was return
        assertThat(mvcResult.getResponse().getContentAsByteArray()).isEqualTo(data2);
        //check +1 new file
        List<String> filesNew = generator.getPathsOfFiles(dirForImages);
        assertThat(files.size() + 1).isEqualTo(filesNew.size());
        //get one new element
        String newFile = filesNew.stream()
                .filter(s -> !files.contains(s))
                .findAny().orElse(null);
        assert newFile != null;
        Path newPath = Path.of(newFile);
        //check data2 was saved to newPath
        assertThat(Files.readAllBytes(newPath)).isEqualTo(data2);
        //check old file was del
        assertThat(Files.exists(path1)).isFalse();
        //clean
        Files.deleteIfExists(path1);
        Files.deleteIfExists(newPath);
        //check with standard parameters  (ads exist, image non-exist)
        Ads adsWithoutImage = generator.generateNewAdsFromAds(ads);
        adsWithoutImage.setImage(null);
        when(adsRepository.findById(ads.getId())).thenReturn(Optional.of(adsWithoutImage));
        when(adsRepository.save(any(Ads.class))).thenReturn(ads);
        when(imageRepository.save(any(Image.class))).thenReturn(image);
        mvcResult = mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andReturn();
        //check data2 was return
        assertThat(mvcResult.getResponse().getContentAsByteArray()).isEqualTo(data2);
        //check +1 new file
        filesNew = generator.getPathsOfFiles(dirForImages);
        assertThat(files.size() + 1).isEqualTo(filesNew.size());
        //get one new element
        newFile = filesNew.stream()
                .filter(s -> !files.contains(s))
                .findAny().orElse(null);
        assert newFile != null;
        newPath = Path.of(newFile);
        //check data2 was saved to newPath
        assertThat(Files.readAllBytes(newPath)).isEqualTo(data2);
        //clean
        Files.deleteIfExists(path1);
        Files.deleteIfExists(newPath);
        verify(imageRepository, times(1)).save(image);
        verify(adsRepository, times(2)).save(ads);
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
        User user = generator.generateUser(null, null);
        Ads ads = generator.generateAdsIfNull(null, user, null);
        ads.setImage(null);
        when(adsRepository.findById(ads.getId())).thenReturn(Optional.empty());
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("http://localhost:8080/ads/" + ads.getId() + "/image")
                                .contentType(MediaType.APPLICATION_OCTET_STREAM_VALUE))
                .andExpect(status().isNotFound());
        when(adsRepository.findById(ads.getId())).thenReturn(Optional.of(ads));
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("http://localhost:8080/ads/" + ads.getId() + "/image")
                                .contentType(MediaType.APPLICATION_OCTET_STREAM_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET http://localhost:8080/ads/ME 200")
    @WithMockUser(username = "1", authorities = "ROLE_USER")
    void getALLAdsOfMeTest() throws Exception {
        String username = "1";
        User user = generator.generateUser(null, null);
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
        User user = generator.generateUser(null, null);
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
    @DisplayName("GET http://localhost:8080/ads/{id}/comments 200")
    @WithMockUser(username = "1", authorities = "ROLE_USER")
    void getCommentsTest() throws Exception {
        User owner = generator.generateUser(null, null);
        Ads ads = generator.generateAdsIfNull(null, owner, null);
        int indexAds = ads.getId();

        List<Comment> commentList = new ArrayList<>();
        int countComments = 5;
        for (int i = 0; i < countComments; i++) {
            User user = generator.generateUser(null, null);
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
    @DisplayName("GET http://localhost:8080/ads/{id}/comments 200")
    @WithMockUser(username = "1", authorities = "ROLE_USER")
    void getCommentsNegativeTest() throws Exception {
        when(adsRepository.findById(anyInt())).thenReturn(Optional.empty());
        mockMvc.perform(MockMvcRequestBuilders
                        .get("http://localhost:8080/ads/" + random.nextInt() + "/comments")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET http://localhost:8080/ads 200")
    @WithMockUser(username = "1", authorities = "ROLE_USER")
    void getALLAdsTest() throws Exception {
        List<Ads> adsList = new ArrayList<>();
        int countAds = 10;
        for (int i = 0; i < countAds; i++) {
            User user = generator.generateUser(null, null);
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
        User user = generator.generateUser(null, null);
        Image image = generator.generateImageIfNull(null, dirForImages);
        Ads ads = generator.generateAdsIfNull(null, user, image);

        when(adsRepository.findById(ads.getId())).thenReturn(Optional.of(ads));
        when(imageRepository.findById(image.getId())).thenReturn(Optional.of(image));

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .get("http://localhost:8080/ads/" + ads.getId() + "/image")
                .contentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
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
        //create user image ads
        User owner = generator.generateUser(null, null);
        Image image = generator.generateImageIfNull(null, dirForImages);
        int indexImage = image.getId();
        Ads ads = generator.generateAdsIfNull(null, owner, image);
        int indexAds = ads.getId();
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
        verify(adsRepository, times(1)).delete(ads);
        verify(commentRepository, times(1)).deleteAllByAdsId(ads.getId());
        verify(imageRepository, times(1)).delete(image);
        assertThat(Files.exists(path1)).isFalse();
    }

    @Test
    @DisplayName("POST http://localhost:8080/ads 200")
    @WithMockUser(username = "1", authorities = {"ROLE_USER"})
    void addAdsTest() throws Exception {
        //create user
        String username = "1";
        User user = generator.generateUser(null, null);
        user.setUsername(username);
        //create ads
        Ads ads = generator.generateAdsIfNull(null, user, null);
        //create createAdsDto
        CreateAdsDto createAdsDto = new CreateAdsDto();
        createAdsDto.setPrice(ads.getPrice());
        createAdsDto.setTitle(ads.getTitle());
        createAdsDto.setDescription(ads.getDescription());
        //create adsDto
        AdsDto adsDto = new AdsDto();
        adsDto.setTitle(ads.getTitle());
        adsDto.setPrice(ads.getPrice());
        adsDto.setAuthor(ads.getAuthor().getId());
        adsDto.setImage("/ads/" + ads.getId() + "/image/");
        adsDto.setPk(ads.getId());
        //need min 1 image in dirForImages
        assertThat(generator.getPathsOfFiles(dirForImages).size() >= 1).isTrue();
        //get images data
        byte[] data = generator.generateDataFileOfImageFromDir(dirForImages);
        //create picture from data for Image
        String pathStr = dirForImages + "/" + "file_for_addAdsTest" + ".jpg";
        Path path = Path.of(pathStr);
        if (!Files.exists(path)) {
            Files.write(path, data);
        }
        //create Mock file and JSON
        MockMultipartFile mockMultipartFile =
                new MockMultipartFile("image", "image.jpg",
                        MediaType.IMAGE_JPEG_VALUE, data);
        MockMultipartFile JSON =
                new MockMultipartFile("properties", null,
                        MediaType.APPLICATION_JSON_VALUE, objectWriter.writeValueAsString(createAdsDto).getBytes());
        //create image
        Image image = new Image();
        image.setId(1);
        image.setPath(imageService.generatePath(mockMultipartFile, "ads_" + ads.getId()).toString());
        //create mock
        when(usersRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(adsRepository.save(any(Ads.class))).thenReturn(ads);
        when(imageRepository.save(any(Image.class))).thenReturn(image);

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .multipart(
                        HttpMethod.POST,
                        "http://localhost:8080/ads"
                )
                .file(mockMultipartFile)
                .file(JSON)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .with(csrf());
        mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(content().json(objectWriter.writeValueAsString(adsDto)));
        assertThat(Files.readAllBytes(path)).isEqualTo(data);
        assertThat(Files.readAllBytes(Path.of(image.getPath()))).isEqualTo(data);
        Files.deleteIfExists(path);
        Files.deleteIfExists(Path.of(image.getPath()));
        verify(adsRepository, times(1)).save(ads);
//        verify(imageRepository, times(1)).save(image);
    }

    @Test
    @DisplayName("POST http://localhost:8080/ads/{id}/comments 200")
    @WithMockUser(username = "1", authorities = {"ROLE_USER"})
    void addCommentsTest() throws Exception {
        //create user
        String username = "1";
        User user = generator.generateUser(null, null);
        user.setUsername(username);
        //create ads
        Ads ads = generator.generateAdsIfNull(null, user, null);
        //create comment
        Comment comment = generator.generateCommentIfNull(null, ads, user);
        //create commentDto
        CommentDto commentDto = new CommentDto();
        commentDto.setAuthor(comment.getAuthor().getId());
        commentDto.setText(comment.getText());
        commentDto.setCreatedAt(dateTimeFormatter.format(comment.getDateTime()));
        commentDto.setPk(comment.getId());

        when(usersRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(usersRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(adsRepository.findById(ads.getId())).thenReturn(Optional.of(ads));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .post("http://localhost:8080/ads/" + ads.getId() + "/comments")
                .content(objectWriter.writeValueAsString(commentDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(csrf());
        mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(content().json(objectWriter.writeValueAsString(commentDto)));
        verify(commentRepository, times(1)).save(comment);
    }

    @Test
    @DisplayName("POST http://localhost:8080/ads/{id}/comments 404")
    @WithMockUser(username = "1", authorities = {"ROLE_USER"})
    void addCommentNegativeTest() throws Exception {
        //create user
        User user = generator.generateUser(null, null);
        user.setUsername("1");
        //create ads
        Ads ads = generator.generateAdsIfNull(null, user, null);
        //create comment
        Comment comment = generator.generateCommentIfNull(null, ads, user);
        //create commentDto
        CommentDto commentDto = new CommentDto();
        commentDto.setAuthor(comment.getAuthor().getId());
        commentDto.setText(comment.getText());
        commentDto.setCreatedAt(dateTimeFormatter.format(comment.getDateTime()));
        commentDto.setPk(comment.getId());

        when(usersRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        when(adsRepository.findById(ads.getId())).thenReturn(Optional.empty());

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .post("http://localhost:8080/ads/" + ads.getId() + "/comments")
                .content(objectWriter.writeValueAsString(commentDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(csrf());
        mockMvc.perform(builder)
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET http://localhost:8080/ads/{idAds}/comments/{idComment} 200")
    @WithMockUser(username = "1", authorities = "ROLE_USER")
    void getCommentTest() throws Exception {
        User user = generator.generateUser(null, null);
        Ads ads = generator.generateAdsIfNull(null, user, null);
        int idAds = ads.getId();
        Comment comment = generator.generateCommentIfNull(null, ads, user);
        int idComment = comment.getId();
        CommentDto commentDto = commentMapper.commentToDto(comment);
        when(adsRepository.findById(idAds)).thenReturn(Optional.of(ads));
        when(commentRepository.findByIdAndAdsId(idAds, idComment)).thenReturn(Optional.of(comment));
        mockMvc.perform(MockMvcRequestBuilders
                        .get("http://localhost:8080/ads/" + idAds + "/comments/" + idComment)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(content().json(objectWriter.writeValueAsString(commentDto)));
    }

    @Test
    @DisplayName("GET http://localhost:8080/ads/{idAds}/comments/{idComment} 404")
    @WithMockUser(username = "1", authorities = "ROLE_USER")
    void getCommentNegativeTest() throws Exception {
        User user = generator.generateUser(null, null);
        Ads ads = generator.generateAdsIfNull(null, user, null);
        int idAds = ads.getId();
        Comment comment = generator.generateCommentIfNull(null, ads, user);
        int idComment = comment.getId();
        when(adsRepository.findById(idAds)).thenReturn(Optional.empty());
        mockMvc.perform(MockMvcRequestBuilders
                        .get("http://localhost:8080/ads/" + idAds + "/comments/" + idComment)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isNotFound());
        when(adsRepository.findById(idAds)).thenReturn(Optional.of(ads));
        when(commentRepository.findByIdAndAdsId(idAds, idComment)).thenReturn(Optional.empty());
        mockMvc.perform(MockMvcRequestBuilders
                        .get("http://localhost:8080/ads/" + idAds + "/comments/" + idComment)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET http://localhost:8080/ads/by-title?title={\"title\"} 200")
    @WithMockUser(username = "1", authorities = "ROLE_USER")
    void findByTitleLikeTest() throws Exception {
        String title = "title";
        int countAds = random.nextInt(10);
        List<Ads> adsList = new ArrayList<>();
        for (int i = 0; i < countAds; i++) {
            User user = generator.generateUser(null, null);
            adsList.add(generator.generateAdsIfNull(null, user, null));
        }
        ResponseWrapperAdsDto responseWrapperAdsDto = adsMapper.mapToResponseWrapperAdsDto(
                adsList.stream()
                        .map(ads -> adsMapper.adsToAdsDto(ads))
                        .collect(Collectors.toList()),
                adsList.size());
        when(adsRepository.findByTitleLike(title)).thenReturn(adsList);
        mockMvc.perform(MockMvcRequestBuilders
                        .get("http://localhost:8080/ads/by-title?title=" + title)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(content().json(objectWriter.writeValueAsString(responseWrapperAdsDto)));
    }

    @Test
    @DisplayName("PATCH http://localhost:8080/ads/{id}/comments/{id} 200")
    @WithMockUser(username = "1", authorities = {"ROLE_USER"})
    void updateCommentsTest() throws Exception {
        //create user
        User user = generator.generateUser(null, null);
        //create ads
        Ads ads = generator.generateAdsIfNull(null, user, null);
        //create comment
        String newText = generator.generateMessageIfEmpty(null);
        Comment commentOld = generator.generateCommentIfNull(null, ads, user);
        Comment commentNew = generator.generateCommentIfNull(null, ads, user);
        commentNew.setId(commentOld.getId());
        commentNew.setText(newText);
        //create commentDto
        CommentDto commentDtoOld = commentMapper.commentToDto(commentOld);
        CommentDto commentDtoNew = commentMapper.commentToDto(commentNew);

        when(adsRepository.findById(ads.getId())).thenReturn(Optional.of(ads));
        when(commentRepository.findByIdAndAdsId(ads.getId(), commentOld.getId())).thenReturn(Optional.of(commentOld));
        when(commentRepository.save(any(Comment.class))).thenReturn(commentNew);
        when(usersRepository.findById(user.getId())).thenReturn(Optional.of(user));

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .patch("http://localhost:8080/ads/" + ads.getId() + "/comments/" + commentOld.getId())
                .content(objectWriter.writeValueAsString(commentDtoOld))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(csrf());
        mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(content().json(objectWriter.writeValueAsString(commentDtoNew)));
        verify(commentRepository, times(1)).save(commentNew);
    }

    @Test
    @DisplayName("PATCH http://localhost:8080/ads/{id}/comments/{id} 404")
    @WithMockUser(username = "1", authorities = {"ROLE_USER"})
    void updateCommentsNegativeTest() throws Exception {
        User owner = generator.generateUser(null, null);
        Ads ads = generator.generateAdsIfNull(null, owner, null);
        Comment comment = generator.generateCommentIfNull(null, ads, owner);
        CommentDto commentDto = commentMapper.commentToDto(comment);
        when(adsRepository.findById(ads.getId())).thenReturn(Optional.empty());
        int randomInt = random.nextInt();
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .patch("http://localhost:8080/ads/" + ads.getId() + "/comments/" + comment.getId())
                .content(objectWriter.writeValueAsString(commentDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(csrf());
        mockMvc.perform(builder)
                .andExpect(status().isNotFound());
        when(usersRepository.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(adsRepository.findById(ads.getId())).thenReturn(Optional.of(ads));
        when(commentRepository.findByIdAndAdsId(ads.getId(), randomInt)).thenReturn(Optional.empty());
        mockMvc.perform(builder)
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE http://localhost:8080/ads/{idAds}/comments/{idComments} 200")
    @WithMockUser(username = "1", authorities = "ROLE_USER")
    void deleteCommentsTest() throws Exception {
        User owner = generator.generateUser(null, null);
        Ads ads = generator.generateAdsIfNull(null, owner, null);
        Comment comment = generator.generateCommentIfNull(null, ads, owner);

        when(adsRepository.findById(ads.getId())).thenReturn(Optional.of(ads));
        when(commentRepository.findByIdAndAdsId(ads.getId(), comment.getId())).thenReturn(Optional.of(comment));

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .delete("http://localhost:8080/ads/" + ads.getId() + "/comments/" + comment.getId())
                .with(csrf());
        ResultActions resultActions = mockMvc.perform(builder);
        resultActions
                .andExpect(status().isOk());
        verify(commentRepository, times(1)).delete(comment);
    }

    @Test
    @DisplayName("DELETE http://localhost:8080/ads/{idAds}/comments/{idComments} 404")
    @WithMockUser(username = "1", authorities = "ROLE_USER")
    void deleteCommentsNegativeTest() throws Exception {
        User owner = generator.generateUser(null, null);
        Ads ads = generator.generateAdsIfNull(null, owner, null);
        when(adsRepository.findById(ads.getId())).thenReturn(Optional.empty());
        int randomInt = random.nextInt();
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .delete("http://localhost:8080/ads/" + ads.getId() + "/comments/" + randomInt)
                .with(csrf());
        mockMvc.perform(builder)
                .andExpect(status().isNotFound());
        when(adsRepository.findById(ads.getId())).thenReturn(Optional.of(ads));
        when(commentRepository.findByIdAndAdsId(ads.getId(), randomInt)).thenReturn(Optional.empty());
        mockMvc.perform(builder)
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PATCH http://localhost:8080/ads/{id} 200")
    @WithMockUser(username = "1", authorities = {"ROLE_USER"})
    void updateAdsTest() throws Exception {
        User user = generator.generateUser(null, null);
        Image image = generator.generateImageIfNull(null, dirForImages);
        Ads ads = generator.generateAdsIfNull(null, user, image);
        //create createAdsDto
        CreateAdsDto createAdsDto = new CreateAdsDto();
        createAdsDto.setPrice(ads.getPrice());
        createAdsDto.setTitle(ads.getTitle());
        createAdsDto.setDescription(ads.getDescription());
        //create adsDto
        AdsDto adsDto = new AdsDto();
        adsDto.setTitle(ads.getTitle());
        adsDto.setPrice(ads.getPrice());
        adsDto.setAuthor(ads.getAuthor().getId());
        adsDto.setImage("/ads/" + ads.getId() + "/image/");
        adsDto.setPk(ads.getId());

        when(adsRepository.findById(ads.getId())).thenReturn(Optional.of(ads));
        when(adsRepository.save(any(Ads.class))).thenReturn(ads);

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .patch("http://localhost:8080/ads/" + ads.getId())
                .content(objectWriter.writeValueAsString(createAdsDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(csrf());
        mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(content().json(objectWriter.writeValueAsString(adsDto)));
        verify(adsRepository, times(1)).save(ads);
    }

    @Test
    @DisplayName("PATCH http://localhost:8080/ads/{id} 404")
    @WithMockUser(username = "1", authorities = {"ROLE_USER"})
    void updateAdsNegativeTest() throws Exception {
        User user = generator.generateUser(null, null);
        Image image = generator.generateImageIfNull(null, dirForImages);
        Ads ads = generator.generateAdsIfNull(null, user, image);
        //create createAdsDto
        CreateAdsDto createAdsDto = new CreateAdsDto();
        createAdsDto.setPrice(ads.getPrice());
        createAdsDto.setTitle(ads.getTitle());
        createAdsDto.setDescription(ads.getDescription());

        when(adsRepository.findById(ads.getId())).thenReturn(Optional.empty());

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders
                .patch("http://localhost:8080/ads/" + ads.getId())
                .content(objectWriter.writeValueAsString(createAdsDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .with(csrf());
        mockMvc.perform(builder)
                .andExpect(status().isNotFound());
    }
}

