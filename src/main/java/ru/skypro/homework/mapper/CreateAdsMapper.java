package ru.skypro.homework.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.skypro.homework.dto.CreateAdsDto;
import ru.skypro.homework.entity.Ads;

@Mapper(componentModel = "spring")
public interface CreateAdsMapper {

    CreateAdsMapper INSTANCE = Mappers.getMapper(CreateAdsMapper.class);

    @Mapping(target = "description", source = "createAdsDto.description")
    @Mapping(target = "price", source = "createAdsDto.price")
    @Mapping(target = "title", source = "createAdsDto.title")
    Ads createAdsDtoToAds(CreateAdsDto createAdsDto);
}
