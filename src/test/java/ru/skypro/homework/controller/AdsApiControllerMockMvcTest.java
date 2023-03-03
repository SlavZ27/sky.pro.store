package ru.skypro.homework.controller;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.Generator;
import ru.skypro.homework.config.WebSecurityConfig;
import ru.skypro.homework.entity.Ads;
import ru.skypro.homework.entity.Avatar;
import ru.skypro.homework.entity.Image;
import ru.skypro.homework.entity.User;
import ru.skypro.homework.mapper.*;
import ru.skypro.homework.repository.*;
import ru.skypro.homework.service.impl.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;
import java.util.Random;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = {AdsApiController.class})
@ActiveProfiles("test")
@Import(value = WebSecurityConfig.class)
public class AdsApiControllerMockMvcTest {
    private final static String REQUEST_MAPPING_STRING = "ads";
    private final static String REQUEST_MAPPING_STRING_COMMENT = "comment";
    private final static String REQUEST_MAPPING_STRING_IMAGE = "image";
    private final String dirForImages;
    private final String dirForAvatars;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ImageRepository imageRepository;
    @MockBean
    private AdsRepository adsRepository;
    @MockBean
    private CommentRepository commentRepository;
    @MockBean
    private AvatarRepository avatarRepository;
    @MockBean
    private UsersRepository usersRepository;
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
    @InjectMocks
    private AdsApiController adsApiController;
    private final Generator generator = new Generator();
    private final Random random = new Random();

    AdsApiControllerMockMvcTest(@Value("${path.to.materials.folder}") String dirForImages, @Value("${path.to.avatars.folder}") String dirForAvatars) {
        this.dirForImages = dirForImages;
        this.dirForAvatars = dirForAvatars;
    }

    @Test
    public void contextsLoad() {
        assertThat(adsApiController).isNotNull();
        assertThat(imageService).isNotNull();
        assertThat(avatarService).isNotNull();
        assertThat(commentService).isNotNull();
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
    void updateImageTest() throws Exception {
        User user = generator.generateUserRoleUser(null, null);
        user.setId(111);
        Image image = generator.generateImageIfNull(null, dirForImages);
        image.setId(222);
        Ads ads = generator.generateAdsIfNull(null, user, image);
        ads.setId(333);
        Path newPathFile = generatePath("ads_333");

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
        //check with standard parameters  (ads exist, image exist)
        String url = "http://localhost:8080/" +
                REQUEST_MAPPING_STRING + "/" + ads.getId() + "/" +
                REQUEST_MAPPING_STRING_IMAGE;

        MockMultipartFile mockMultipartFile = new MockMultipartFile("image", "image.jpg",
                MediaType.IMAGE_JPEG_VALUE, data2);

        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.multipart(HttpMethod.PATCH, url).file(mockMultipartFile).contentType(MediaType.MULTIPART_FORM_DATA).with(csrf());
        mockMvc.perform(builder).andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        assertThat(Files.readAllBytes(newPathFile)).isEqualTo(data2);
        assertThat(Files.exists(path1)).isFalse();
        Files.deleteIfExists(path1);
        Files.deleteIfExists(newPathFile);

        //check with standard parameters  (ads exist, image non-exist)
        ads.setImage(null);

        when(imageRepository.save(any(Image.class))).thenReturn(image);
        url = "http://localhost:8080/" +
                REQUEST_MAPPING_STRING + "/" + ads.getId() + "/" +
                REQUEST_MAPPING_STRING_IMAGE;

        mockMultipartFile = new MockMultipartFile("image", "image.jpg",
                MediaType.IMAGE_JPEG_VALUE, data2);

        builder =
                MockMvcRequestBuilders.multipart(HttpMethod.PATCH, url).file(mockMultipartFile).contentType(MediaType.MULTIPART_FORM_DATA).with(csrf());
        mockMvc.perform(builder).andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        assertThat(Files.readAllBytes(newPathFile)).isEqualTo(data2);
        assertThat(Files.exists(path1)).isFalse();
        Files.deleteIfExists(path1);
        Files.deleteIfExists(newPathFile);
    }

    @Test
    void updateImageNegativeTest() throws Exception {
        when(adsRepository.findById(anyInt())).thenReturn(Optional.empty());

        //check with standard parameters  (ads exist, image exist)
        String url = "http://localhost:8080/" +
                REQUEST_MAPPING_STRING + "/" + random.nextInt() + "/" +
                REQUEST_MAPPING_STRING_IMAGE;

        MockMultipartFile mockMultipartFile = new MockMultipartFile("image", "image.jpg",
                MediaType.IMAGE_JPEG_VALUE, new byte[0]);

        MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.multipart(HttpMethod.PATCH, url)
                        .file(mockMultipartFile).contentType(MediaType.MULTIPART_FORM_DATA).with(csrf());
        mockMvc.perform(builder)
                .andExpect(status().isNotFound());
    }

    private Path generatePath(String nameFile) {
        String date = LocalDate.now().toString();
        String extension = ".jpg";
        return Paths.get(dirForImages).resolve(nameFile + "_" + date + extension);
    }
}
