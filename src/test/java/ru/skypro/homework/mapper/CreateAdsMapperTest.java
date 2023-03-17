package ru.skypro.homework.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.skypro.homework.dto.CreateAdsDto;
import ru.skypro.homework.entity.Ads;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CreateAdsMapperTest {
    private final CreateAdsMapper createAdsMapper = Mappers.getMapper(CreateAdsMapper.class);

    @Test
    public void testCreateAdsDtoToAds() {
        CreateAdsDto createAdsDto = new CreateAdsDto();
        createAdsDto.setDescription("Test description");
        createAdsDto.setPrice(15_000);
        createAdsDto.setTitle("Title");

        Ads ads = createAdsMapper.createAdsDtoToAds(createAdsDto);

        assertEquals(ads.getDescription(), "Test description");
        assertEquals(ads.getPrice(), 15_000);
        assertEquals(ads.getTitle(), "Title");
    }
}
