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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AdsApiControllerTest {
    @LocalServerPort
    private int port;
    private final static String REQUEST_MAPPING_STRING = "ads";
    private final static String REQUEST_MAPPING_STRING_COMMENT = "comment";
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

    @Before("")
    public void setup() {
        testRestTemplate.getRestTemplate().setRequestFactory(new HttpComponentsClientHttpRequestFactory());
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
                .filter(ads -> commentRepository.findAllByIdAds(ads.getId()).size() > 0 &&
                        imageRepository.findAllByIdAds(ads.getId()).size() > 0)
                .findAny().orElse(null);
        assert actualAds != null;
        //get image of actualAds
        List<Image> imageList = imageRepository.findAllByIdAds(actualAds.getId());
        //create file and set pathStr to images
        String pathStr = dirForImages + "/" + "file_for_removeAdsTest" + ".jpg";
        Path path = Path.of(pathStr);
        if (!Files.exists(path)) {
            Files.createFile(path);
        }
        for (Image image : imageList) {
            image.setPath(pathStr);
            imageRepository.save(image);
        }
        //get comment of actualAds
        List<Comment> commentList = commentRepository.findAllByIdAds(actualAds.getId());
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
        assertThat(imageRepository.findAll().size()).isEqualTo(countImage - imageList.size());
        assertThat(commentRepository.findAll().size()).isEqualTo(countComment - commentList.size());
    }

    @Test
    void removeAdsNegativeTest() {
        //actualAds With Image And Comment
        Ads actualAds = adsRepository.findAll().stream()
                .filter(ads -> commentRepository.findAllByIdAds(ads.getId()).size() > 0 &&
                        imageRepository.findAllByIdAds(ads.getId()).size() > 0)
                .findAny().orElse(null);
        assert actualAds != null;
        //delete image and comment of actualAds
        imageRepository.deleteAll(imageRepository.findAllByIdAds(actualAds.getId()));
        commentRepository.deleteAll(commentRepository.findAllByIdAds(actualAds.getId()));
        actualAds = adsRepository.findById(actualAds.getId()).orElse(null);
        adsRepository.delete(actualAds);
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
    public void updateAdsTest() {
        //actualAds With Image And Comment
        Ads actualAds = adsRepository.findAll().stream()
                .filter(ads -> commentRepository.findAllByIdAds(ads.getId()).size() > 0 &&
                        imageRepository.findAllByIdAds(ads.getId()).size() > 0)
                .findAny().orElse(null);
        assert actualAds != null;

        AdsDto adsDto = adsMapper.adsToAdsDto(actualAds);

        String url = "http://localhost:" + port + "/" + REQUEST_MAPPING_STRING + "/" + adsDto.getPk();
        ResponseEntity<AdsDto> getForEntityResponse = testRestTemplate.getForEntity(url, AdsDto.class, adsDto.getPk());
        assertThat(getForEntityResponse.getBody()).isNotNull();

        CreateAdsDto createAdsDto = new CreateAdsDto();
        createAdsDto.setDescription("Updated description");
        createAdsDto.setPrice(15_000);
        createAdsDto.setTitle("Updated Title");

        actualAds.setDescription(createAdsDto.getDescription());
        actualAds.setPrice(createAdsDto.getPrice());
        actualAds.setTitle(createAdsDto.getTitle());

        adsDto = adsMapper.adsToAdsDto(actualAds);

        ResponseEntity<AdsDto> result = testRestTemplate.exchange(
                url,
                HttpMethod.PATCH,
                new HttpEntity<>(adsDto),
                AdsDto.class
        );


        assertThat(result.getBody()).usingRecursiveComparison().isEqualTo(adsDto);
        assertThat(result.getBody().getTitle()).isEqualTo(createAdsDto.getTitle());
        assertThat(result.getBody().getPrice()).isEqualTo(createAdsDto.getPrice());
        assertThat(result.getBody().getAuthor()).usingRecursiveComparison().isEqualTo(actualAds.getAuthor().getId());
    }

    @Test
    public void updateCommentsTest() {
//actualAds With Image And Comment
        Ads actualAds = adsRepository.findAll().stream()
                .filter(ads -> commentRepository.findAllByIdAds(ads.getId()).size() > 0 &&
                        imageRepository.findAllByIdAds(ads.getId()).size() > 0)
                .findAny().orElse(null);
        assert actualAds != null;

        AdsDto adsDto = adsMapper.adsToAdsDto(actualAds);

        CommentDto commentToPatch = new CommentDto();
        commentToPatch.setCreatedAt("2023-02-15 16:20");
        commentToPatch.setText("Text to patch");
        commentToPatch.setPk(1);

        Comment comment = commentRepository.findAllByIdAds(actualAds.getId()).stream()
                .findAny().orElse(null);
        assert comment != null;

        comment.setAds(actualAds);
        comment.setAuthor(actualAds.getAuthor());
        comment.setText(commentToPatch.getText());
        comment.setDateTime(LocalDateTime.parse(commentToPatch.getCreatedAt(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));

        String url = "http://localhost:" + port + "/" + REQUEST_MAPPING_STRING + "/" + adsDto.getPk() + "/" + REQUEST_MAPPING_STRING_COMMENT + "/" + comment.getId();
        ResponseEntity<Comment> getForEntityResponse = testRestTemplate.getForEntity(url, Comment.class, comment.getId());
//        assertThat(getForEntityResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getForEntityResponse.getBody()).isNotNull();

//        comment.setAds(actualAds);
//        comment.setAuthor(actualAds.getAuthor());
//        comment.setText(commentToPatch.getText());
//        comment.setDateTime(LocalDateTime.parse(commentToPatch.getCreatedAt(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));

        CommentDto newCommentDto = commentMapper.commentToDto(comment);

        ResponseEntity<CommentDto> result = testRestTemplate.exchange(
                url,
                HttpMethod.PATCH,
                new HttpEntity<>(newCommentDto),
                CommentDto.class
        );

//        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody()).usingRecursiveComparison().isEqualTo(newCommentDto);
        assertThat(result.getBody().getPk()).isEqualTo(newCommentDto.getPk());
        assertThat(result.getBody().getText()).isEqualTo(newCommentDto.getText());
        assertThat(result.getBody().getAuthor()).usingRecursiveComparison().isEqualTo(actualAds.getAuthor().getId());
        assertThat(result.getBody().getCreatedAt()).isEqualTo(newCommentDto.getCreatedAt());
    }

}