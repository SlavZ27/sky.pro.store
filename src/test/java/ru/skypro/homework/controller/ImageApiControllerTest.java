package ru.skypro.homework.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import ru.skypro.homework.Generator;
import ru.skypro.homework.entity.Ads;
import ru.skypro.homework.entity.Avatar;
import ru.skypro.homework.entity.Image;
import ru.skypro.homework.entity.User;
import ru.skypro.homework.repository.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ImageApiControllerTest {
    @LocalServerPort
    private int port;
    private final static String REQUEST_MAPPING_STRING = "image";
    private final String dirForImages;
    private final String dirForAvatars;

    @Autowired
    private ImageApiController imageApiController;
    @Autowired
    private AdsRepository adsRepository;
    @Autowired
    private AvatarRepository avatarRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private ImageRepository imageRepository;
    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private TestRestTemplate testRestTemplate;
    private final Generator generator = new Generator();
    private final Random random = new Random();

    ImageApiControllerTest(@Value("${path.to.materials.folder}") String dirForImages, @Value("${path.to.avatars.folder}") String dirForAvatars) {
        this.dirForImages = dirForImages;
        this.dirForAvatars = dirForAvatars;
    }

    @BeforeEach
    public void generateData() {
        imageRepository.deleteAll();
        commentRepository.deleteAll();
        adsRepository.deleteAll();
        usersRepository.deleteAll();
        avatarRepository.deleteAll();

        int countUserAdmin = 5;
        int countUser = 100;

        int countAdsUserMin = 0;
        int countAdsUserMax = 5;

        int countCommentForAdsMin = 0;
        int countCommentForAdsMax = 20;

        //generate userAdmin
        List<User> userAdminList = new ArrayList<>();
        for (int i = 0; i < countUserAdmin; i++) {
            Avatar avatar = avatarRepository.save(generator.generateAvatarIfNull(null, dirForAvatars));
            userAdminList.add(usersRepository.save(generator.generateUserRoleAdmin(avatar, dirForAvatars)));
        }
        //generate user
        List<User> userList = new ArrayList<>();
        for (int i = 0; i < countUser; i++) {
            Avatar avatar = avatarRepository.save(generator.generateAvatarIfNull(null, dirForAvatars));
            userList.add(usersRepository.save(generator.generateUserRoleUser(avatar, dirForAvatars)));
        }
        //generate ads
        List<Ads> adsList = new ArrayList<>();
        for (User user : userList) {
            int countAds = generator.genInt(countAdsUserMin, countAdsUserMax);
            int countImage = 1;
            for (int i = 0; i < countAds; i++) {
                Ads ads = adsRepository.save(generator.generateAdsIfNull(null, user));
                adsList.add(ads);
                for (int i1 = 0; i1 < countImage; i1++) {
                    imageRepository.save(generator.generateImageIfNull(null, dirForImages, ads));
                }
            }
        }
        //generate comments
        List<User> tempUserList = new ArrayList<>(userList);
        tempUserList.addAll(userAdminList);
        for (Ads ads : adsList) {
            int countComments = generator.genInt(countCommentForAdsMin, countCommentForAdsMax);
            for (int i = 0; i < countComments; i++) {
                commentRepository.save(generator.generateCommentIfNull(
                        null,
                        adsList.get(random.nextInt(adsList.size())),
                        tempUserList.get(random.nextInt(tempUserList.size()))));
            }
        }
    }

    @AfterEach
    public void clearData() {
        imageRepository.deleteAll();
        commentRepository.deleteAll();
        adsRepository.deleteAll();
        usersRepository.deleteAll();
        avatarRepository.deleteAll();
    }


    @Test
    void contextLoads() {
        assertThat(imageApiController).isNotNull();
        assertThat(adsRepository).isNotNull();
        assertThat(avatarRepository).isNotNull();
        assertThat(commentRepository).isNotNull();
        assertThat(imageRepository).isNotNull();
        assertThat(usersRepository).isNotNull();
        assertThat(testRestTemplate).isNotNull();
    }

    @Test
    void getImageTest() throws IOException {
        Image actualImage = imageRepository.findAll().stream().findAny().orElse(null);
        assert actualImage != null;

        ResponseEntity<byte[]> result = testRestTemplate.exchange(
                "http://localhost:" + port + "/" + REQUEST_MAPPING_STRING + "/" + actualImage.getId(),
                HttpMethod.GET,
                null,
                byte[].class);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody())
                .isEqualTo(Files.readAllBytes(Paths.get(actualImage.getPath())));
        assertThat(result.getBody().length)
                .isEqualTo(Files.readAllBytes(Paths.get(actualImage.getPath())).length);
    }

    @Test
    void getImageNegativeTest() {
        Image actualImage = imageRepository.findAll().stream().findAny().orElse(null);
        assert actualImage != null;
        actualImage.setPath("null");
        actualImage = imageRepository.save(actualImage);
        ResponseEntity<byte[]> result = testRestTemplate.exchange(
                "http://localhost:" + port + "/" + REQUEST_MAPPING_STRING + "/" + actualImage.getId(),
                HttpMethod.GET,
                null,
                byte[].class);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        actualImage.setPath(null);
        actualImage = imageRepository.save(actualImage);
        result = testRestTemplate.exchange(
                "http://localhost:" + port + "/" + REQUEST_MAPPING_STRING + "/" + actualImage.getId(),
                HttpMethod.GET,
                null,
                byte[].class);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        imageRepository.delete(actualImage);
        result = testRestTemplate.exchange(
                "http://localhost:" + port + "/" + REQUEST_MAPPING_STRING + "/" + actualImage.getId(),
                HttpMethod.GET,
                null,
                byte[].class);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}