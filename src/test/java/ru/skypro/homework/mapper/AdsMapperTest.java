package ru.skypro.homework.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.skypro.homework.dto.AdsDto;
import ru.skypro.homework.entity.Ads;
import ru.skypro.homework.entity.Image;
import ru.skypro.homework.entity.User;
import ru.skypro.homework.service.impl.ImageServiceImpl;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AdsMapperTest {
    @InjectMocks
    private AdsMapper adsMapper = Mappers.getMapper(AdsMapper.class);
    @Mock
    private ImageServiceImpl imageService;

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

        when(imageService.getLinkOfImageOfAds(1)).thenReturn("/image/1");

        AdsDto adsDto = adsMapper.adsToAdsDto(ads);

        assertEquals(adsDto.getPk(), 111);
        assertEquals(adsDto.getPrice(), 15_000);
        assertEquals(adsDto.getTitle(), "Title");
        assertEquals(adsDto.getAuthor(), 111);
        assertEquals(adsDto.getImage(), "/image/" + image.getId());
    }
}
