package ru.skypro.homework.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.skypro.homework.dto.AdsDto;
import ru.skypro.homework.entity.Ads;
import ru.skypro.homework.entity.Image;
import ru.skypro.homework.entity.User;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AdsMapperTest {
    private AdsMapper adsMapper = Mappers.getMapper(AdsMapper.class);

    @Test
    public void testAdsToAdsDto() {
        Ads ads = new Ads();
        ads.setId(111);
        ads.setPrice(15_000);
        ads.setTitle("Title");

        User user = new User();
        user.setId(111);
        ads.setAuthor(user);

        Image image = new Image();
        Image image1 = new Image();
        image.setPath("/some/path/image.png");
        image1.setPath("/some/path/image1.png");

        List<Image> images = new ArrayList<>();
        images.add(image);
        images.add(image1);
        ads.setImages(images);

        AdsDto adsDto = adsMapper.adsToAdsDto(ads);

        assertEquals(adsDto.getPk(), 111);
        assertEquals(adsDto.getPrice(), 15_000);
        assertEquals(adsDto.getTitle(), "Title");
        assertEquals(adsDto.getAuthor(), 111);

        List<String> stringList = new ArrayList<>();
        stringList.add("/some/path/image.png");
        stringList.add("/some/path/image1.png");
        assertEquals(adsDto.getImage(), stringList);
    }
}
