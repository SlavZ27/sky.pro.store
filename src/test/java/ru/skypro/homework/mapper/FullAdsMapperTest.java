package ru.skypro.homework.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.skypro.homework.dto.FullAdsDto;
import ru.skypro.homework.entity.Ads;
import ru.skypro.homework.entity.Image;
import ru.skypro.homework.entity.User;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FullAdsMapperTest {
    private FullAdsMapper fullAdsMapper = Mappers.getMapper(FullAdsMapper.class);

    @Test
    public void testAdsToFullAdsDto() {
        Ads ads = new Ads();
        ads.setId(111);
        ads.setPrice(15_000);
        ads.setTitle("Title");
        ads.setDescription("Description");

        User user = new User();
        user.setId(111);
        user.setEmail("some@mail.com");
        user.setFirstName("Vasya");
        user.setLastName("Vasin");
        user.setPhone("+79990001122");
        ads.setAuthor(user);

        Image image = new Image();
        Image image1 = new Image();
        image.setPath("/some/path/image.png");
        image1.setPath("/some/path/image1.png");

        List<Image> images = new ArrayList<>();
        images.add(image);
        images.add(image1);
        ads.setImages(images);

        FullAdsDto fullAdsDto = fullAdsMapper.adsToFullAdsDto(ads);

        assertEquals(fullAdsDto.getPk(), 111);
        assertEquals(fullAdsDto.getPrice(), 15_000);
        assertEquals(fullAdsDto.getTitle(), "Title");
        assertEquals(fullAdsDto.getDescription(), "Description");
        assertEquals(fullAdsDto.getAuthorFirstName(), "Vasya");
        assertEquals(fullAdsDto.getAuthorLastName(), "Vasin");
        assertEquals(fullAdsDto.getPhone(), "+79990001122");

        List<String> stringList = new ArrayList<>();
        stringList.add("/some/path/image.png");
        stringList.add("/some/path/image1.png");
        assertEquals(fullAdsDto.getImage(), stringList);
    }
}
