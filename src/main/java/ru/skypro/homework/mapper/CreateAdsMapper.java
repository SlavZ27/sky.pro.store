package ru.skypro.homework.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.skypro.homework.dto.CreateAdsDto;
import ru.skypro.homework.entity.Ads;


/**
 * Provides methods for mapping CreateAdsDto`s to Ads
 */
@Mapper(componentModel = "spring")
public interface CreateAdsMapper {

    /**
     * The constant INSTANCE.
     */
    CreateAdsMapper INSTANCE = Mappers.getMapper(CreateAdsMapper.class);

    /**
     * Map CreateAdsDto to Ads.
     *
     * @param createAdsDto the create ads dto
     * @return {@link Ads}
     */
    @Mapping(target = "description", source = "createAdsDto.description")
    @Mapping(target = "price", source = "createAdsDto.price")
    @Mapping(target = "title", source = "createAdsDto.title")
    Ads createAdsDtoToAds(CreateAdsDto createAdsDto);
}
