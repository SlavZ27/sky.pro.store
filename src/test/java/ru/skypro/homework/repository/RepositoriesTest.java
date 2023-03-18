package ru.skypro.homework.repository;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.skypro.homework.Generator;
import ru.skypro.homework.entity.*;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.DisplayName.class)
class RepositoriesTest {
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
    private final Generator generator = new Generator();
    private final Random random = new Random();
    private final Comparator<Ads> adsComparator
            = Comparator.comparing(Ads::getDateTime).reversed();
    private final Comparator<Comment> commentComparator
            = Comparator.comparing(Comment::getDateTime);

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
        assertThat(usersRepository).isNotNull();
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
                .findFirst()
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
                .findFirst()
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
                .findFirst()
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
                .findFirst()
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
                .findFirst()
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
                .findFirst()
                .orElse(null);
        assert actualComment != null;
        Integer idComment = actualComment.getId();
        Integer idAds = actualComment.getAds().getId();
        Comment actual = commentRepository.findAll().stream()
                .filter(comment -> comment.getId().equals(idComment))
                .filter(comment -> comment.getAds().getId().equals(idAds))
                .findFirst().orElse(null);
        Comment expected = commentRepository.findByIdAndAdsId(idAds, idComment).orElse(null);
        assertThat(expected).isNotNull();
        assertThat(actual).isNotNull();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("CommentRepository::deleteAllByAdsId")
    void deleteAllByAdsId() {
        Ads ads = commentRepository.findAll().stream()
                .filter(comment -> comment.getAds() != null && comment.getAds().getId() != null)
                .map(Comment::getAds)
                .findFirst()
                .orElse(null);
        assert ads != null;
        long countCommentAll = commentRepository.findAll().size();
        long countComment = commentRepository.findAll().stream()
                .filter(comment -> comment.getAds() != null
                        && comment.getAds().getId() != null
                        && comment.getAds().getId().equals(ads.getId()))
                .count();
        commentRepository.deleteAllByAdsId(ads.getId());
        assertThat(countCommentAll - countComment).isEqualTo(commentRepository.findAll().size());
        assertThat(commentRepository.findAll().stream()
                .filter(comment -> comment.getAds() != null
                        && comment.getAds().getId() != null
                        && comment.getAds().getId().equals(ads.getId()))
                .count()).
                isEqualTo(0L);
    }

    @Test
    @DisplayName("CommentRepository::getCountAllByAdsId")
    void getCountAllByAdsIdTest() {
        Ads ads = commentRepository.findAll().stream()
                .filter(comment -> comment.getAds() != null && comment.getAds().getId() != null)
                .map(Comment::getAds)
                .findFirst()
                .orElse(null);
        assert ads != null;
        long actual = commentRepository.findAll().stream()
                .filter(comment -> comment.getAds() != null
                        && comment.getAds().getId() != null
                        && comment.getAds().getId().equals(ads.getId()))
                .count();
        long expected = commentRepository.getCountAllByAdsId(ads.getId());
        assertThat(expected).isEqualTo(actual);
        assertThat(actual > 0).isTrue();
    }

    @Test
    @DisplayName("AuthorityRepository::findByUsernameAndAuthority")
    void findByUsernameAndAuthorityTest() {
        Authority authorityAdmin = authorityRepository.findAll().stream()
                .filter(authority -> authority.getAuthority().equals(Role.ADMIN.getRole()))
                .findFirst().orElse(null);
        Authority authorityUser = authorityRepository.findAll().stream()
                .filter(authority -> authority.getAuthority().equals(Role.USER.getRole()))
                .findFirst().orElse(null);
        assert authorityAdmin != null;
        assert authorityUser != null;
        assertThat(
                authorityRepository.findByUsernameAndAuthority(
                        authorityAdmin.getUsername(),
                        authorityAdmin.getAuthority()).orElse(null)
        ).isNotNull();
        assertThat(
                authorityRepository.findByUsernameAndAuthority(
                        authorityUser.getUsername(),
                        authorityUser.getAuthority()).orElse(null)
        ).isNotNull();
        assertThat(
                authorityRepository.findByUsernameAndAuthority(
                        authorityAdmin.getUsername(),
                        authorityUser.getAuthority()).orElse(null)
        ).isNull();
        assertThat(
                authorityRepository.findByUsernameAndAuthority(
                        authorityAdmin.getUsername(),
                        authorityUser.getAuthority()).orElse(null)
        ).isNull();
    }

    @Test
    @DisplayName("AuthorityRepository::getAllByUsername")
    void getAllByUsernameTest() {
        //get 3 authorities
        List<Authority> authorities = authorityRepository.findAll().stream()
                .limit(3)
                .collect(Collectors.toList());
        assertThat(authorities.size()).isEqualTo(3);
        //delete authorities
        authorityRepository.deleteAll(authorities);
        //create admin
        Authority authorityAdmin = new Authority();
        authorityAdmin.setUsername(authorities.get(0).getUsername());
        authorityAdmin.setAuthority(Role.ADMIN.getRole());
        authorityAdmin = authorityRepository.save(authorityAdmin);
        //create user
        Authority authorityUser = new Authority();
        authorityUser.setUsername(authorities.get(1).getUsername());
        authorityUser.setAuthority(Role.USER.getRole());
        authorityUser = authorityRepository.save(authorityUser);
        //create admin user
        Authority authorityAdminUser1 = new Authority();
        Authority authorityAdminUser2 = new Authority();
        authorityAdminUser1.setUsername(authorities.get(2).getUsername());
        authorityAdminUser2.setUsername(authorities.get(2).getUsername());
        authorityAdminUser1.setAuthority(Role.USER.getRole());
        authorityAdminUser2.setAuthority(Role.ADMIN.getRole());
        authorityAdminUser1 = authorityRepository.save(authorityAdminUser1);
        authorityAdminUser2 = authorityRepository.save(authorityAdminUser2);


        List<Authority> expectAdmin = authorityRepository.getAllByUsername(authorityAdmin.getUsername());
        List<Authority> expectUser = authorityRepository.getAllByUsername(authorityUser.getUsername());
        List<Authority> expectAdminUser = authorityRepository.getAllByUsername(authorityAdminUser1.getUsername());

        assertThat(expectAdmin)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(List.of(authorityAdmin));
        assertThat(expectUser)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(List.of(authorityUser));
        assertThat(expectAdminUser)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(List.of(authorityAdminUser1, authorityAdminUser2));
    }

    @Test
    @DisplayName("UsersRepository::findByUsername")
    void findByUsernameTest() {
        User user = usersRepository.findAll().stream()
                .findFirst()
                .orElse(null);
        assert user != null;
        assertThat(user).isEqualTo(usersRepository.findByUsername(user.getUsername()).orElse(null));
        String username = null;
        boolean existName = true;
        while (existName) {
            username = String.valueOf(random.nextInt());
            String finalUsername = username;
            existName = usersRepository.findAll().stream()
                    .anyMatch(user1 -> user1.getUsername().equals(finalUsername));
        }
        assertThat(usersRepository.findByUsername(username).orElse(null)).isNull();
    }

    @Test
    @DisplayName("UsersRepository::getCount")
    void getCountTest() {
        assertThat(usersRepository.findAll().size()).isEqualTo(usersRepository.getCount());
    }

    @Test
    @DisplayName("UsersRepository::isAdsAuthor")
    void isAdsAuthorTest() {
        Ads ads = adsRepository.findAll().stream()
                .findFirst()
                .orElse(null);
        assert ads != null;
        assert ads.getAuthor() != null;
        Ads ads2 = adsRepository.findAll().stream()
                .filter(ads1 -> !ads1.getAuthor().getId().equals(ads.getAuthor().getId()))
                .findFirst()
                .orElse(null);
        assert ads2 != null;
        assertThat(usersRepository.isAdsAuthor(ads.getId(), ads.getAuthor().getUsername())).isTrue();
        assertThat(usersRepository.isAdsAuthor(ads2.getId(), ads.getAuthor().getUsername())).isFalse();
    }

    @Test
    @DisplayName("UsersRepository::isCommentAuthor")
    void isCommentAuthorTest() {
        Comment comment = commentRepository.findAll().stream()
                .findFirst()
                .orElse(null);
        assert comment != null;
        assert comment.getAuthor() != null;
        Comment comment2 = commentRepository.findAll().stream()
                .filter(comment1 -> !comment1.getAuthor().getId().equals(comment.getAuthor().getId()))
                .findFirst()
                .orElse(null);
        assert comment2 != null;
        assertThat(usersRepository.isCommentAuthor(comment.getId(), comment.getAuthor().getUsername())).isTrue();
        assertThat(usersRepository.isCommentAuthor(comment2.getId(), comment.getAuthor().getUsername())).isFalse();
    }
}