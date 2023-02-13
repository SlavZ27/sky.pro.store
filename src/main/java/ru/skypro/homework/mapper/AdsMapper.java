package ru.skypro.homework.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.skypro.homework.dto.AdsDto;
import ru.skypro.homework.entity.Ads;
import ru.skypro.homework.entity.Image;

@Mapper
public interface AdsMapper {

    AdsMapper INSTANCE = Mappers.getMapper(AdsMapper.class);

    @Mapping(target = "pk", source = "ads.id")
    @Mapping(target = "author", expression = "java(ads.getAuthor().getId())")
    @Mapping(target = "price", source = "ads.price")
    @Mapping(target = "title", source = "ads.title")
    @Mapping(target = "image", source = "images") //is called mapImageToString()
    AdsDto adsToAdsDto(Ads ads);

    default String mapImageToString(Image images) {
        if (images == null) {
            throw new IllegalArgumentException();
        }
        return images.getPath();
    }
}
