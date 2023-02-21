package ru.skypro.homework.controller;

import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.context.ActiveProfiles;
import ru.skypro.homework.Generator;
import ru.skypro.homework.dto.AdsDto;
import ru.skypro.homework.dto.CommentDto;
import ru.skypro.homework.dto.CreateAdsDto;
import ru.skypro.homework.entity.*;
import ru.skypro.homework.mapper.AdsMapper;
import ru.skypro.homework.mapper.CommentMapper;
import ru.skypro.homework.repository.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AdsApiControllerTest {
    @LocalServerPort
    private int port;
    private final static String REQUEST_MAPPING_STRING = "ads";
    private final static String REQUEST_MAPPING_STRING_COMMENT = "comment";
    private final static String REQUEST_MAPPING_STRING_IMAGE = "image";
    private final String dirForImages;
    private final String dirForAvatars;
    @Autowired
    private AdsApiController adsApiController;
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
    @Autowired
    private AdsMapper adsMapper;
    @Autowired
    private CommentMapper commentMapper;
    private final Generator generator = new Generator();
    private final Random random = new Random();

    AdsApiControllerTest(@Value("${path.to.materials.folder}") String dirForImages, @Value("${path.to.avatars.folder}") String dirForAvatars) {
        this.dirForImages = dirForImages;
        this.dirForAvatars = dirForAvatars;
    }

    @BeforeEach
    public void generateData() {
        commentRepository.deleteAll();
        adsRepository.deleteAll();
        usersRepository.deleteAll();
        imageRepository.deleteAll();
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
            userAdminList.add(usersRepository.save(generator.generateUserRoleAdmin(avatar)));
        }
        //generate user
        List<User> userList = new ArrayList<>();
        for (int i = 0; i < countUser; i++) {
            Avatar avatar = avatarRepository.save(generator.generateAvatarIfNull(null, dirForAvatars));
            userList.add(usersRepository.save(generator.generateUserRoleUser(avatar)));
        }
        //generate ads
        List<Ads> adsList = new ArrayList<>();
        for (User user : userList) {
            int countAds = generator.genInt(countAdsUserMin, countAdsUserMax);
            for (int i = 0; i < countAds; i++) {
                Image image = imageRepository.save(generator.generateImageIfNull(null, dirForImages));
                Ads ads = adsRepository.save(generator.generateAdsIfNull(null, user, image));
                adsList.add(ads);
            }
        }
        //generate comments
        List<User> tempUserList = new ArrayList<>(userList);
        tempUserList.addAll(userAdminList);
        for (Ads ads : adsList) {
            int countComments = generator.genInt(countCommentForAdsMin, countCommentForAdsMax);
            for (int i = 0; i < countComments; i++) {
                commentRepository.save(generator.generateCommentIfNull(
                        null, ads, tempUserList.get(random.nextInt(tempUserList.size()))));
            }
        }
    }

    @AfterEach
    public void clearData() {
        commentRepository.deleteAll();
        adsRepository.deleteAll();
        usersRepository.deleteAll();
        avatarRepository.deleteAll();
        imageRepository.deleteAll();
    }


    @Test
    void contextLoads() {
        assertThat(adsApiController).isNotNull();
        assertThat(adsRepository).isNotNull();
        assertThat(avatarRepository).isNotNull();
        assertThat(commentRepository).isNotNull();
        assertThat(imageRepository).isNotNull();
        assertThat(usersRepository).isNotNull();
        assertThat(testRestTemplate).isNotNull();
    }

    @Test
    void removeAdsTest() throws IOException {
        //actualAds With Image And Comment
        Ads actualAds = adsRepository.findAll().stream()
                .filter(ads ->
                        commentRepository.findAll().stream().
                                anyMatch(comment -> comment.getAds().getId().equals(ads.getId())) &&
                                ads.getImage() != null)
                .findAny().orElse(null);
        assert actualAds != null;
        //create file and set pathStr to images
        String pathStr = dirForImages + "/" + "file_for_removeAdsTest" + ".jpg";
        Path path = Path.of(pathStr);
        if (!Files.exists(path)) {
            Files.createFile(path);
        }
        Image image = actualAds.getImage();
        image.setPath(pathStr);
        imageRepository.save(image);
        //get comment of actualAds
        List<Comment> commentList = commentRepository.findAll().stream()
                .filter(comment -> comment.getAds().getId().equals(actualAds.getId()))
                .collect(Collectors.toList());
        //remember counts of repositories
        int countAds = adsRepository.findAll().size();
        int countImage = imageRepository.findAll().size();
        int countComment = commentRepository.findAll().size();

        String url = "http://localhost:" + port + "/" + REQUEST_MAPPING_STRING + "/" + actualAds.getId();
        ResponseEntity<Void> result = testRestTemplate.exchange(
                url,
                HttpMethod.DELETE,
                null,
                Void.class);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(Files.exists(path)).isFalse();
        assertThat(adsRepository.findAll().size()).isEqualTo(countAds - 1);
        assertThat(imageRepository.findAll().size()).isEqualTo(countImage - 1);
        assertThat(commentRepository.findAll().size()).isEqualTo(countComment - commentList.size());
    }

    @Test
    void removeAdsNegativeTest() {
        //actualAds With Image And Comment
        Ads actualAds = adsRepository.findAll().stream()
                .filter(ads ->
                        commentRepository.findAll().stream().
                                anyMatch(comment -> comment.getAds().getId().equals(ads.getId())) &&
                                ads.getImage() != null)
                .findAny().orElse(null);
        assert actualAds != null;
        //delete comment of actualAds
        commentRepository.deleteAll(commentRepository.findAllByIdAds(actualAds.getId()));
        actualAds = adsRepository.findById(actualAds.getId()).orElse(null);
        adsRepository.delete(actualAds);
        //delete image of actualAds
        imageRepository.delete(actualAds.getImage());
        //remember counts of repositories
        int countAds = adsRepository.findAll().size();
        int countImage = imageRepository.findAll().size();
        int countComment = commentRepository.findAll().size();

        String url = "http://localhost:" + port + "/" + REQUEST_MAPPING_STRING + "/" + actualAds.getId();
        ResponseEntity<Void> result = testRestTemplate.exchange(
                url,
                HttpMethod.DELETE,
                null,
                Void.class);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(adsRepository.findAll().size()).isEqualTo(countAds);
        assertThat(imageRepository.findAll().size()).isEqualTo(countImage);
        assertThat(commentRepository.findAll().size()).isEqualTo(countComment);
    }

    @Test
    void getImageTest() throws IOException {
        //actualAds With Image
        Ads actualAds = adsRepository.findAll().stream()
                .filter(ads -> ads.getImage() != null)
                .findAny().orElse(null);
        assert actualAds != null;
        Image actualImage = actualAds.getImage();
        assert actualImage != null;

        ResponseEntity<byte[]> result = testRestTemplate.exchange(
                "http://localhost:" + port + "/" +
                        REQUEST_MAPPING_STRING + "/" + actualAds.getId() + "/" +
                        REQUEST_MAPPING_STRING_IMAGE,
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
        //actualAds With Image
        Ads actualAds = adsRepository.findAll().stream()
                .filter(ads -> ads.getImage() != null)
                .findAny().orElse(null);
        assert actualAds != null;
        Image actualImage = actualAds.getImage();
        assert actualImage != null;
        actualImage.setPath("null");
        actualImage = imageRepository.save(actualImage);
        ResponseEntity<byte[]> result = testRestTemplate.exchange(
                "http://localhost:" + port + "/" +
                        REQUEST_MAPPING_STRING + "/" + actualAds.getId() + "/" +
                        REQUEST_MAPPING_STRING_IMAGE,
                HttpMethod.GET,
                null,
                byte[].class);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        actualImage.setPath(null);
        actualImage = imageRepository.save(actualImage);
        result = testRestTemplate.exchange(
                "http://localhost:" + port + "/" +
                        REQUEST_MAPPING_STRING + "/" + actualAds.getId() + "/" +
                        REQUEST_MAPPING_STRING_IMAGE,
                HttpMethod.GET,
                null,
                byte[].class);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        actualAds.setImage(null);
        adsRepository.save(actualAds);
        result = testRestTemplate.exchange(
                "http://localhost:" + port + "/" +
                        REQUEST_MAPPING_STRING + "/" + actualAds.getId() + "/" +
                        REQUEST_MAPPING_STRING_IMAGE,
                HttpMethod.GET,
                null,
                byte[].class);
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

}