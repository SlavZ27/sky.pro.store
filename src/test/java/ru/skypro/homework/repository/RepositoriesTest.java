package ru.skypro.homework.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import ru.skypro.homework.Generator;
import ru.skypro.homework.controller.AdsApiController;
import ru.skypro.homework.entity.*;
import ru.skypro.homework.mapper.AdsMapper;
import ru.skypro.homework.mapper.CommentMapper;
import ru.skypro.homework.mapper.FullAdsMapper;
import ru.skypro.homework.repository.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RepositoriesTest {
    @LocalServerPort
    private int port;
    @Value("${path.to.materials.folder}")
    private String dirForImages;
    @Value("${path.to.avatars.folder}")
    private String dirForAvatars;
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
    private AuthorityRepository authorityRepository;
    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private TestRestTemplate testRestTemplate;
    @Autowired
    private AdsMapper adsMapper;
    @Autowired
    private FullAdsMapper fullAdsMapper;
    @Autowired
    private CommentMapper commentMapper;
    private final Generator generator = new Generator();
    private final Random random = new Random();
    private final Comparator<Ads> adsComparator
            = Comparator.comparing(Ads::getDateTime).reversed();
    private final Comparator<Comment> commentComparator
            = Comparator.comparing(Comment::getDateTime);

    @BeforeEach
    public void generateData() {
        authorityRepository.deleteAll();
        commentRepository.deleteAll();
        adsRepository.deleteAll();
        usersRepository.deleteAll();
        imageRepository.deleteAll();
        avatarRepository.deleteAll();

        int countUserAdmin = 0;
        int countUser = 20;

        int countAdsUserMin = 0;
        int countAdsUserMax = 5;

        int countCommentForAdsMin = 0;
        int countCommentForAdsMax = 10;

        //generate userAdmin
        List<User> userAdminList = new ArrayList<>();
        for (int i = 0; i < countUserAdmin; i++) {
            Avatar avatar = avatarRepository.save(generator.generateAvatarIfNull(null, dirForAvatars));
            User user = usersRepository.save(generator.generateUserRoleAdmin(avatar, "password"));
            authorityRepository.save(generator.generateAuthority(user, Role.ADMIN));
            userAdminList.add(user);
        }
        //generate user
        List<User> userList = new ArrayList<>();
        for (int i = 0; i < countUser; i++) {
            Avatar avatar = avatarRepository.save(generator.generateAvatarIfNull(null, dirForAvatars));
            User user = usersRepository.save(generator.generateUserRoleUser(avatar, "password"));
            authorityRepository.save(generator.generateAuthority(user, Role.USER));
            userList.add(user);
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
        authorityRepository.deleteAll();
        commentRepository.deleteAll();
        adsRepository.deleteAll();
        usersRepository.deleteAll();
        imageRepository.deleteAll();
        avatarRepository.deleteAll();
    }

    @Test
    void contextLoads() {
        assertThat(adsApiController).isNotNull();
        assertThat(authorityRepository).isNotNull();
        assertThat(adsRepository).isNotNull();
        assertThat(avatarRepository).isNotNull();
        assertThat(commentRepository).isNotNull();
        assertThat(imageRepository).isNotNull();
        assertThat(usersRepository).isNotNull();
        assertThat(testRestTemplate).isNotNull();
    }

    @Test
    @DisplayName("AdsRepository::findAllAndSortDateTime")
    void findAllAndSortDateTimeTest() {
        List<Ads> actual = adsRepository.findAll();
        actual.sort(adsComparator);
        List<Ads> expected = adsRepository.findAllAndSortDateTime();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("AdsRepository::findByTitleLike")
    void findByTitleLikeTest() {
        String title = adsRepository.findAll().stream()
                .findAny()
                .map(Ads::getTitle)
                .orElse(null);
        assert title != null;
        int left = title.length() - 1;
        int right = 0;
        while (left > right) {
            left = random.nextInt(title.length() - 1);
            right = random.nextInt(title.length() - 1);
        }
        String partTitle = title.substring(left, right);
        List<Ads> actual = adsRepository.findAll().stream()
                .filter(ads -> ads.getTitle().contains(partTitle))
                .sorted(adsComparator)
                .collect(Collectors.toList());
        List<Ads> expected = adsRepository.findByTitleLike(partTitle);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("AdsRepository::findAllByUserIdAndSortDateTime")
    void findAllByUserIdAndSortDateTimeTest() {
        Integer idAuthor = adsRepository.findAll().stream()
                .findAny()
                .map(ads -> ads.getAuthor().getId())
                .orElse(null);
        List<Ads> actual = adsRepository.findAll().stream()
                .filter(ads -> ads.getAuthor().getId().equals(idAuthor))
                .sorted(adsComparator)
                .collect(Collectors.toList());
        List<Ads> expected = adsRepository.findAllByUserIdAndSortDateTime(idAuthor);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("AdsRepository::findAllByUsernameAndSortDateTime")
    void findAllByUsernameAndSortDateTimeTest() {
        String username = adsRepository.findAll().stream()
                .findAny()
                .map(ads -> ads.getAuthor().getUsername())
                .orElse(null);
        List<Ads> actual = adsRepository.findAll().stream()
                .filter(ads -> ads.getAuthor().getUsername().equals(username))
                .sorted(adsComparator)
                .collect(Collectors.toList());
        List<Ads> expected = adsRepository.findAllByUsernameAndSortDateTime(username);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("CommentRepository::findAllByIdAdsAndSortDateTime")
    void findAllByIdAdsAndSortDateTimeTest() {
        Integer idAds = commentRepository.findAll().stream()
                .findAny()
                .map(comment -> comment.getAds().getId())
                .orElse(null);
        List<Comment> actual = commentRepository.findAll().stream()
                .filter(comment -> comment.getAds().getId().equals(idAds))
                .sorted(commentComparator)
                .collect(Collectors.toList());
        List<Comment> expected = commentRepository.findAllByIdAdsAndSortDateTime(idAds);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("CommentRepository::findAllByIdAds")
    void findAllByIdAdsTest() {
        Integer idAds = commentRepository.findAll().stream()
                .findAny()
                .map(comment -> comment.getAds().getId())
                .orElse(null);
        List<Comment> actual = commentRepository.findAll().stream()
                .filter(comment -> comment.getAds().getId().equals(idAds))
                .collect(Collectors.toList());
        List<Comment> expected = commentRepository.findAllByIdAds(idAds);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("CommentRepository::findByIdAndAdsId")
    void findByIdAndAdsIdTest() {
        Comment actualComment = commentRepository.findAll().stream()
                .filter(comment -> comment.getAds() != null && comment.getAds().getId() != null)
                .findAny()
                .orElse(null);
        Integer idComment = actualComment.getId();
        Integer idAds = actualComment.getAds().getId();
        Comment actual = commentRepository.findAll().stream()
                .filter(comment -> comment.getId().equals(idComment))
                .filter(comment -> comment.getAds().getId().equals(idAds))
                .findAny().orElse(null);
        Comment expected = commentRepository.findByIdAndAdsId(idAds,idComment).orElse(null);
        assertThat(expected).isNotNull();
        assertThat(actual).isNotNull();
        assertThat(actual).isEqualTo(expected);
    }
}