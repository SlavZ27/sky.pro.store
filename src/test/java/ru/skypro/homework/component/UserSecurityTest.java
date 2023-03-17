package ru.skypro.homework.component;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import ru.skypro.homework.Generator;
import ru.skypro.homework.entity.*;
import ru.skypro.homework.repository.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.DisplayName.class)
class UserSecurityTest {
    @Value("${path.to.materials.folder}")
    private String dirForImages;
    @Value("${path.to.avatars.folder}")
    private String dirForAvatars;
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
    private UserSecurity userSecurity;
    private final Generator generator = new Generator();
    private final Random random = new Random();

    @BeforeEach
    public void generateData() throws IOException {
        authorityRepository.deleteAll();
        commentRepository.deleteAll();
        adsRepository.deleteAll();
        usersRepository.deleteAll();
        imageRepository.deleteAll();
        avatarRepository.deleteAll();

        int countUserAdmin = 2;
        int countUser = 5;

        int countAdsUserMin = 1;
        int countAdsUserMax = 3;

        int countCommentForAdsMin = 1;
        int countCommentForAdsMax = 10;

        //generate userAdmin
        List<User> userAdminList = new ArrayList<>();
        for (int i = 0; i < countUserAdmin; i++) {
            Avatar avatar = avatarRepository.save(generator.generateAvatarIfNull(
                    null, dirForAvatars, null));
            User user = usersRepository.save(generator.generateUser(avatar, "password"));
            authorityRepository.save(generator.generateAuthority(user, Role.ADMIN));
            userAdminList.add(user);
        }
        //generate user
        List<User> userList = new ArrayList<>();
        for (int i = 0; i < countUser; i++) {
            Avatar avatar = avatarRepository.save(generator.generateAvatarIfNull(
                    null, dirForAvatars, null));
            User user = usersRepository.save(generator.generateUser(avatar, "password"));
            authorityRepository.save(generator.generateAuthority(user, Role.USER));
            userList.add(user);
        }
        //generate ads
        List<Ads> adsList = new ArrayList<>();
        for (User user : userList) {
            int countAds = generator.genInt(countAdsUserMin, countAdsUserMax);
            for (int i = 0; i < countAds; i++) {
                Image image = imageRepository.save(generator.generateImageIfNull(
                        null, dirForImages, null));
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
        assertThat(authorityRepository).isNotNull();
        assertThat(adsRepository).isNotNull();
        assertThat(avatarRepository).isNotNull();
        assertThat(commentRepository).isNotNull();
        assertThat(imageRepository).isNotNull();
        assertThat(userSecurity).isNotNull();
        assertThat(usersRepository).isNotNull();
    }

    @Test
    @WithMockUser(username = "1@gmail.com")
    void isAdsAuthorTest() {
        String username = "1@gmail.com";
        List<Ads> adsList = adsRepository.findAll();
        assertThat(adsList.size() > 1).isEqualTo(true);

        Ads ads = adsList.get(0);
        User user = adsList.get(0).getAuthor();
        user.setUsername(username);
        usersRepository.save(user);

        Ads ads2 = adsList.stream()
                .filter(ads1 -> !ads1.getAuthor().getId().equals(ads.getAuthor().getId()))
                .findAny().orElse(null);
        assert ads2 != null;
        User user2 = ads2.getAuthor();
        assertThat(user2.getUsername()).isNotEqualTo(username);

        assertThat(userSecurity.isAdsAuthor(ads.getId())).isEqualTo(true);
        assertThat(userSecurity.isAdsAuthor(ads2.getId())).isEqualTo(false);
    }

    @Test
    @WithMockUser(username = "1@gmail.com")
    void isCommentAuthorTest() {
        String username = "1@gmail.com";
        List<Comment> commentList = commentRepository.findAll();
        assertThat(commentList.size() > 1).isEqualTo(true);

        Comment comment = commentList.get(0);
        User user = commentList.get(0).getAuthor();
        user.setUsername(username);
        usersRepository.save(user);

        Comment comment2 = commentList.stream()
                .filter(comment1 -> !comment1.getAuthor().getId().equals(comment.getAuthor().getId()))
                .findAny().orElse(null);
        assert comment2 != null;
        User user2 = comment2.getAuthor();
        assertThat(user2.getUsername()).isNotEqualTo(username);

        assertThat(userSecurity.isCommentAuthor(comment.getId())).isEqualTo(true);
        assertThat(userSecurity.isCommentAuthor(comment2.getId())).isEqualTo(false);
    }
}