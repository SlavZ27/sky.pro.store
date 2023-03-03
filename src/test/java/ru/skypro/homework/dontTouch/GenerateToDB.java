package ru.skypro.homework.dontTouch;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.skypro.homework.Generator;
import ru.skypro.homework.entity.*;
import ru.skypro.homework.repository.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GenerateToDB {
    private final String dirForImages = "./materials_test";
    private final String dirForAvatars = "./avatars_test";
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
    private AuthorityRepository authorityRepository;
    private final Generator generator = new Generator();
    private final Random random = new Random();


    //Uncomment annotation and run this test for generate DB. After generate comment again
    @Test
    void contextLoads() {
        assertThat(adsRepository).isNotNull();
        assertThat(avatarRepository).isNotNull();
        assertThat(commentRepository).isNotNull();
        assertThat(imageRepository).isNotNull();
        assertThat(usersRepository).isNotNull();
        assertThat(authorityRepository).isNotNull();
    }

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
        int countCommentForAdsMax = 5;

        //generate userAdmin
        List<User> userAdminList = new ArrayList<>();
        for (int i = 0; i < countUserAdmin; i++) {
            Avatar avatar = avatarRepository.save(generator.generateAvatarIfNull(null, dirForAvatars));
            User user = usersRepository.save(generator.generateUserRoleAdmin(avatar));
            authorityRepository.save(generator.generateAuthority(user, Role.ROLE_ADMIN));
            userAdminList.add(user);
        }
        //generate user
        List<User> userList = new ArrayList<>();
        for (int i = 0; i < countUser; i++) {
            Avatar avatar = avatarRepository.save(generator.generateAvatarIfNull(null, dirForAvatars));
            User user = usersRepository.save(generator.generateUserRoleUser(avatar));
            authorityRepository.save(generator.generateAuthority(user, Role.ROLE_USER));
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
}
