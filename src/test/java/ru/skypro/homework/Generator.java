package ru.skypro.homework;

import com.github.javafaker.Faker;
import org.apache.commons.io.FileUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.skypro.homework.entity.Role;
import ru.skypro.homework.entity.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class Generator {

    private final String linkToImages = "https://picsum.photos/200";
    private final Faker faker = new Faker();
    private final Random random = new Random();

    private final PasswordEncoder encoder = new BCryptPasswordEncoder();

    public int genInt() {
        return random.nextInt();
    }

    public int genInt(int max) {
        return random.nextInt(max);
    }

    public int genIntWithoutZero(int max) {
        return random.nextInt(max - 1) + 1;
    }

    /**
     * The method returns a random number between the parameters
     * Using {@link Random#nextInt(int)}
     */
    public int genInt(int min, int max) {
        return random.nextInt(max - min) + min;
    }

    public byte[] genByte() {
        return faker.avatar().image().getBytes();
    }

    public LocalDate generateDate(LocalDate startInclusive, LocalDate endExclusive) {
        long startEpochDay = startInclusive.toEpochDay();
        long endEpochDay = endExclusive.toEpochDay();
        long randomDay = ThreadLocalRandom
                .current()
                .nextLong(startEpochDay, endEpochDay);
        return LocalDate.ofEpochDay(randomDay);
    }

    public LocalTime generateTime(LocalTime startTime, LocalTime endTime) {
        int startSeconds = startTime.toSecondOfDay();
        int endSeconds = endTime.toSecondOfDay();
        int randomTime = ThreadLocalRandom
                .current()
                .nextInt(startSeconds, endSeconds);

        return LocalTime.ofSecondOfDay(randomTime);
    }

    public LocalDateTime generateDateTime(LocalDateTime startInclusive, LocalDateTime endExclusive) {
        LocalDate localDate = generateDate(startInclusive.toLocalDate(), endExclusive.toLocalDate());
        LocalTime localTime = generateTime(startInclusive.toLocalTime(), endExclusive.toLocalTime());
        return LocalDateTime.of(localDate, localTime);
    }

    public User generateUser(Avatar avatar, String pas) {
        return generateUser(
                null,
                null,
                null,
                null,
                null,
                null,
                avatar,
                null,
                pas,
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
            boolean needGenerate) {
        if (needGenerate) {
            idUser = generateIdIfEmpty(idUser);
            firstName = generateFirstNameIfEmpty(firstName);
            lastName = generateLastNameIfEmpty(lastName);
            email = generateEmailIfEmpty(email);
            phone = generatePhoneIfEmpty(phone);
            regDate = generateDate(LocalDate.now().minusYears(2), LocalDate.now());
            username = generateEmailIfEmpty(email);
            password = generatePasswordIfEmpty(password, true);

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
        user.setEnabled(true);
        return user;
    }

    public String generatePasswordIfEmpty(String password, boolean bcrypt) {
        if (password == null) {
            password = faker.internet().password();
        }
        if (bcrypt) {
            CharSequence charSequence = new StringBuilder(password);
            return "{bcrypt}" + encoder.encode(charSequence);
        } else {
            return password;
        }
    }

    private String getPathImageNameNotExist(String dirToCopy) {
        String filePath;
        do {
            filePath = faker.file().fileName(
                    dirToCopy, null, "jpg", null);
        } while (Files.exists(Path.of(filePath)));
        return filePath;
    }

    public Avatar generateAvatarIfNull(Avatar avatar, String dirForAvatars, String dirToCopyOrNull) throws IOException {
        if (avatar == null) {
            avatar = new Avatar();
            avatar.setId(generateIdIfEmpty(null));
            if (dirForAvatars == null || dirForAvatars.length() == 0) {
                avatar.setPath(faker.file().fileName());
            } else {
                List<String> pathsOfFiles = getPathsOfFiles(dirForAvatars);
                String pathRandom = pathsOfFiles.get(random.nextInt(pathsOfFiles.size()));
                if (dirToCopyOrNull != null) {
                    File file = new File(dirToCopyOrNull);
                    if (!Files.exists(file.toPath())) {
                        file.mkdirs();
                    }
                    String filePath = getPathImageNameNotExist(dirToCopyOrNull);
                    Files.copy(Path.of(pathRandom), Path.of(filePath));
                    avatar.setPath(filePath);
                } else {
                    avatar.setPath(pathRandom);
                }
            }
        }
        return avatar;
    }

    public Image generateImageIfNull(Image image, String dirForImages, String dirToCopyOrNull) throws IOException {
        if (image == null) {
            String filePath;
            image = new Image();
            image.setId(generateIdIfEmpty(null));
            if (dirForImages == null || dirForImages.length() == 0) {
                if (dirToCopyOrNull != null) {
                    File file = new File(dirToCopyOrNull);
                    if (!Files.exists(file.toPath())) {
                        file.mkdirs();
                    }
                    filePath = getPathImageNameNotExist(dirToCopyOrNull);
                    file = new File(filePath);
                    URL myUrl = new URL(linkToImages);
                    FileUtils.copyURLToFile(myUrl, file);
                } else {
                    filePath = faker.file().fileName();
                }
            } else {
                List<String> pathsOfFiles = getPathsOfFiles(dirForImages);
                String pathRandom = pathsOfFiles.get(random.nextInt(pathsOfFiles.size()));
                if (dirToCopyOrNull != null) {
                    File file = new File(dirToCopyOrNull);
                    if (!Files.exists(file.toPath())) {
                        file.mkdirs();
                    }
                    filePath = getPathImageNameNotExist(dirToCopyOrNull);
                    Files.copy(Path.of(pathRandom), Path.of(filePath));
                } else {
                    filePath = pathRandom;
                }
            }
            image.setPath(filePath);
        }
        return image;
    }

    public byte[] generateDataFileOfImageFromDir(String dirForImages) {
        List<String> pathsOfFiles = getPathsOfFiles(dirForImages);
        try {
            return Files.readAllBytes(Path.of(pathsOfFiles.get(random.nextInt(pathsOfFiles.size()))));
        } catch (IOException ignored) {
        }
        return faker.avatar().image().getBytes();
    }

    public List<String> getPathsOfFiles(String pathDir) {
        File dir = new File(pathDir);
        File[] files = dir.listFiles();
        assert files != null;
        return Arrays.stream(files).map(file -> pathDir + "/" + file.getName()).collect(Collectors.toList());
    }

    public Comment generateCommentIfNull(Comment comment, Ads ads, User author) {
        if (comment == null) {
            comment = new Comment();
            comment.setId(generateIdIfEmpty(null));
            comment.setText(faker.chuckNorris().fact());
            TreeSet<LocalDateTime> lDTSet = new TreeSet<>();
            lDTSet.add(LocalDateTime.now().minusYears(2));
            if (ads != null && ads.getDateTime() != null) {
                lDTSet.add(ads.getDateTime());
            }
            if (author != null && author.getRegDate() != null) {
                lDTSet.add(author.getRegDate().atStartOfDay());
            }
            comment.setDateTime(
                    generateDateTime(
                            lDTSet.last(),
                            LocalDateTime.now()));
            comment.setAds(ads);
            comment.setAuthor(author);
        }
        return comment;
    }

    public Ads generateAdsIfNull(Ads ads, User author, Image image) {
        if (ads == null) {
            ads = new Ads();
            ads.setId(generateIdIfEmpty(null));
            ads.setAuthor(author);
            ads.setImage(image);
            if (author != null && author.getRegDate() != null) {
                ads.setDateTime(
                        generateDateTime(
                                LocalDateTime.of(author.getRegDate(), LocalTime.MIDNIGHT),
                                LocalDateTime.now()));
            } else {
                ads.setDateTime(
                        generateDateTime(
                                LocalDateTime.now().minusYears(2),
                                LocalDateTime.now()));
            }
            ads.setTitle(faker.commerce().productName());
            ads.setPrice(genInt(50_000));
            ads.setDescription(faker.chuckNorris().fact());
        }
        return ads;
    }

    public Ads generateNewAdsFromAds(Ads ads) {
        Ads ads1 = new Ads();
        if (ads != null) {
            ads1.setId(ads.getId());
            ads1.setAuthor(ads.getAuthor());
            ads1.setImage(ads.getImage());
            ads1.setDateTime(ads.getDateTime());
            ads1.setTitle(ads.getTitle());
            ads1.setPrice(ads.getPrice());
            ads1.setDescription(ads.getDescription());
        }
        return ads1;
    }


    /**
     * The method generates a random address if it receives null or an empty string
     * Using {@link Faker#address()#streetAddress}
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
     */
    public String generatePhoneIfEmpty(String phone) {
        if (phone == null || phone.length() == 0) {
            StringBuilder sb = new StringBuilder("+79");
            for (int i = 0; i < 9; i++) {
                sb.append(random.nextInt(10));
            }
            return sb.toString();
        }
        return phone;
    }

    /**
     * The method generates a random name if it receives null or an empty string.
     * Using {@link Faker#name()#username()}
     */
    public String generateNameIfEmpty(String name) {
        if (name == null || name.length() == 0) {
            return faker.name().username();
        }
        return name.substring(0, 29);
    }

    public String generateFirstNameIfEmpty(String name) {
        if (name == null || name.length() == 0) {
            return faker.name().firstName();
        }
        return name.substring(0, 29);
    }

    public String generateLastNameIfEmpty(String name) {
        if (name == null || name.length() == 0) {
            return faker.name().lastName();
        }
        return name.substring(0, 29);
    }


    public Authority generateAuthority(User user, Role role) {
        Authority authority = new Authority();
        if (user == null || user.getUsername() == null) {
            authority.setUsername(faker.name().username());
        } else {
            authority.setUsername(user.getUsername());
        }
        if (role == null) {
            authority.setAuthority(generateRoleIfEmpty(null).toString());
        } else {
            authority.setAuthority(role.getRole());
        }
        authority.setAuthority(authority.getAuthority());
        authority.setId(generateIdIfEmpty(null));
        return authority;
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
     */
    public Integer generateIdIfEmpty(Integer id) {
        if (id == null || id < 0) {
            int idTemp = -1;
            //id with <100 I leave for my needs
            while (idTemp < 0) {
                idTemp = random.nextInt();
            }
            return idTemp;
        }
        return id;
    }

    /**
     * The method generates a random message for telegram if it receives null or an empty string
     * Using {@link Faker#lordOfTheRings()#character()}
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
