package ru.skypro.homework;

import com.github.javafaker.Faker;
import liquibase.license.LicenseService;
import ru.skypro.homework.dto.Role;
import ru.skypro.homework.entity.*;

import java.io.File;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

public class Generator {
    private final Faker faker = new Faker();
    private final Random random = new Random();


    public int genInt() {
        return random.nextInt();
    }

    public int genInt(int max) {
        return random.nextInt(max);
    }

    /**
     * The method returns a random number between the parameters
     * Using {@link Random#nextInt(int)}
     *
     * @param min
     * @param max
     * @return
     */
    public int genInt(int min, int max) {
        return random.nextInt(max - min) + min;
    }

    public byte[] genByte() {
        return faker.avatar().image().getBytes();
    }


    /**
     * @param isPast
     * @param localDateTime
     * @return Method generates the date and time in {@link LocalDateTime} format before or after the parameter
     */
    public LocalDateTime generateDateTime(boolean isPast, LocalDateTime localDateTime) {
        LocalDateTime tldt = LocalDateTime.now();
        int year = tldt.getYear();
        if (isPast) {
            tldt = localDateTime.plusYears(1L);
            while (tldt.isBefore(localDateTime)) {
                LocalDate localDate = LocalDate.of(genInt(year - 2, year), genInt(12), genInt(25));
                LocalTime localTime = LocalTime.of(genInt(23), genInt(59), genInt(59), 0);
                tldt = LocalDateTime.of(localDate, localTime);
            }
        } else {
            tldt = localDateTime.minusYears(1L);
            while (tldt.isAfter(localDateTime)) {
                LocalDate localDate = LocalDate.of(genInt(year - 2, year), genInt(12), genInt(25));
                LocalTime localTime = LocalTime.of(genInt(23), genInt(59), genInt(59), 0);
                tldt = LocalDateTime.of(localDate, localTime);
            }
        }
        return tldt;
    }

    public LocalDate generateDate(boolean isPast, LocalDate localDate) {
        LocalDate tld = LocalDate.now();
        int year = tld.getYear();
        if (isPast) {
            tld = localDate.plusYears(1L);
            while (tld.isBefore(localDate)) {
                tld = LocalDate.of(genInt(year - 2, year), genInt(12), genInt(25));
            }
        } else {
            tld = localDate.minusYears(1L);
            while (tld.isAfter(localDate)) {
                tld = LocalDate.of(genInt(year - 2, year), genInt(12), genInt(25));
            }
        }
        return tld;
    }

    public User generateUserRoleAdmin(Avatar avatar) {
        return generateUser(
                null,
                null,
                null,
                null,
                null,
                null,
                avatar,
                null,
                null,
                Role.ADMIN,
                true);
    }

    public User generateUserRoleUser(Avatar avatar) {
        return generateUser(
                null,
                null,
                null,
                null,
                null,
                null,
                avatar,
                null,
                null,
                Role.USER,
                true);
    }

    public User generateUser(
            Integer idUser,
            String firstName,
            String lastName,
            String email,
            String phone,
            LocalDate regDate,
            Avatar avatar,
            String username,
            String password,
            Role role,
            boolean needGenerate) {
        if (needGenerate) {
            idUser = generateIdIfEmpty(idUser);
            firstName = generateFirstNameIfEmpty(firstName);
            lastName = generateLastNameIfEmpty(lastName);
            email = generateEmailIfEmpty(email);
            phone = generatePhoneIfEmpty(phone);
            regDate = generateDate(true, LocalDate.now());
            avatar = generateAvatarIfNull(avatar);
            username = generateNameIfEmpty(username);
            password = generateNameIfEmpty(password);
            role = generateRoleIfEmpty(role);

        }
        User user = new User();
        user.setId(idUser);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPhone(phone);
        user.setRegDate(regDate);
        user.setAvatar(avatar);
        user.setUsername(username);
        user.setPassword(password);
        user.setRole(role);
        return user;
    }

    public Avatar generateAvatarIfNull(Avatar avatar) {
        if (avatar == null) {
            avatar = new Avatar();
            avatar.setId(genInt());
            avatar.setPath(faker.file().fileName());
        }
        return avatar;
    }

    private List<String> getPathsOfFiles(String pathDir) {
        File dir = new File(pathDir);
        File[] files = dir.listFiles();
        assert files != null;
        return Arrays.stream(files).map(file -> pathDir + "/" + file.getName()).collect(Collectors.toList());
    }

    public Image generateImageIfNull(Image image, String dirForImages, Ads ads) {

        if (image == null) {
            image = new Image();
            image.setId(genInt());
            if (dirForImages == null || dirForImages.length() == 0) {
                image.setPath(faker.file().fileName());
            } else {
                List<String> pathsOfFiles = getPathsOfFiles(dirForImages);
                image.setPath(pathsOfFiles.get(random.nextInt(pathsOfFiles.size())));
            }
            image.setAds(ads);
        }
        return image;
    }

    public Comment generateCommentIfNull(Comment comment, Ads ads, User user) {
        if (comment == null) {
            comment = new Comment();
            comment.setId(genInt());
            comment.setText(faker.chuckNorris().fact());
            comment.setDateTime(generateDateTime(true, LocalDateTime.now()));
            comment.setAds(ads);
            comment.setAuthor(user);
        }
        return comment;
    }

    public Ads generateAdsIfNull(Ads ads, User author) {
        if (ads == null) {
            ads = new Ads();
            ads.setId(genInt());
            ads.setAuthor(author);
            ads.setTitle(faker.commerce().productName());
            ads.setPrice(Integer.valueOf(genInt(50_000)));
            ads.setDescription(faker.chuckNorris().fact());
        }
        return ads;
    }


    /**
     * The method generates a random address if it receives null or an empty string
     * Using {@link Faker#address()#streetAddress}
     *
     * @param address
     * @return
     */
    public String generateAddressIfEmpty(String address) {
        if (address == null || address.length() == 0) {
            return faker.address().streetAddress();
        }
        return address;
    }

    /**
     * The method generates a random city if it receives null or an empty string
     * Using {@link Faker#address()#city}
     *
     * @param city
     * @return
     */
    public String generateCityIfEmpty(String city) {
        if (city == null || city.length() == 0) {
            return faker.address().city();
        }
        return city;
    }

    /**
     * The method generates a random phone if it receives null or an empty string.
     * Limited to 15 characters due to database rules.
     * Using {@link Faker#phoneNumber()#phoneNumber()}
     *
     * @param phone
     * @return
     */
    public String generatePhoneIfEmpty(String phone) {
        if (phone == null || phone.length() == 0) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 10; i++) {
                sb.append(random.nextInt(10));
            }
            return sb.toString();
        }
        return phone;
    }

    /**
     * The method generates a random name if it receives null or an empty string.
     * Using {@link Faker#name()#username()}
     *
     * @param name
     * @return
     */
    public String generateNameIfEmpty(String name) {
        if (name == null || name.length() == 0) {
            return faker.name().username();
        }
        return name;
    }

    public String generateFirstNameIfEmpty(String name) {
        if (name == null || name.length() == 0) {
            return faker.name().firstName();
        }
        return name;
    }

    public String generateLastNameIfEmpty(String name) {
        if (name == null || name.length() == 0) {
            return faker.name().lastName();
        }
        return name;
    }

    public Role generateRoleIfEmpty(Role role) {
        if (role == null) {
            Role[] roleList = Role.values();
            return roleList[random.nextInt(roleList.length)];
        }
        return role;
    }

    public String generateEmailIfEmpty(String email) {
        if (email == null || email.length() == 0) {
            return faker.internet().emailAddress();
        }
        return email;
    }

    /**
     * The method generates a random id for telegram if it receives null or id<0.
     * Values from 100_000_000 to 999_999_999
     * Using {@link Faker#random()#nextLong()}
     *
     * @param id
     * @return
     */
    public Integer generateIdIfEmpty(Integer id) {
        if (id == null || id < 0) {
            Integer idTemp = -1;
            //id with <100 I leave for my needs
            while (idTemp < 100) {
                idTemp = faker.random().nextInt(999_999_999 - 100_000_000) + 100_000_000;
            }
            return idTemp;
        }
        return id;
    }

    /**
     * The method generates a random message for telegram if it receives null or an empty string
     * Using {@link Faker#lordOfTheRings()#character()}
     *
     * @param message
     * @return
     */
    public String generateMessageIfEmpty(String message) {
        if (message == null || message.length() == 0) {
            return faker.lordOfTheRings().character();
        }
        return message;
    }

    public boolean generateBool() {
        return faker.bool().bool();
    }

    public Boolean generateBoolWithNull() {
        int i = random.nextInt(50);
        if (i < 25) {
            return faker.bool().bool();
        } else {
            return null;
        }
    }
}
