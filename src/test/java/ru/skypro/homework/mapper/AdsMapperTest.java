package ru.skypro.homework.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.skypro.homework.dto.AdsDto;
import ru.skypro.homework.entity.Ads;
import ru.skypro.homework.entity.Image;
import ru.skypro.homework.entity.User;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AdsMapperTest {
    private final AdsMapper adsMapper = Mappers.getMapper(AdsMapper.class);
    @Test
    public void testAdsToAdsDto() {

        Ads ads = new Ads();
        ads.setId(111);
        ads.setPrice(15_000);
        ads.setTitle("Title");

        Image image = new Image();
        image.setId(1);
        image.setPath("/some/path/image.png");
        ads.setImage(image);

        User user = new User();
        user.setId(111);
        ads.setAuthor(user);

        AdsDto adsDto = adsMapper.adsToAdsDto(ads);

        assertEquals(adsDto.getPk(), 111);
        assertEquals(adsDto.getPrice(), 15_000);
        assertEquals(adsDto.getTitle(), "Title");
        assertEquals(adsDto.getAuthor(), 111);
        assertEquals(adsDto.getImage(), "/ads/111/image/");
    }
}
