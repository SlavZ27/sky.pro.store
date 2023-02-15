package ru.skypro.homework.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.skypro.homework.dto.FullAdsDto;
import ru.skypro.homework.entity.Ads;
import ru.skypro.homework.entity.Image;


@Mapper(componentModel = "spring")
public interface FullAdsMapper {

    FullAdsMapper INSTANCE = Mappers.getMapper(FullAdsMapper.class);

    @Mapping(target = "pk", source = "ads.id")
    @Mapping(target = "price", source = "ads.price")
    @Mapping(target = "title", source = "ads.title")
    @Mapping(target = "image", source = "images") // is called mapImageToString(Image images)
    @Mapping(target = "description", source = "ads.description")
    @Mapping(target = "phone", expression = "java(ads.getAuthor().getPhone())")
    @Mapping(target = "email", expression = "java(ads.getAuthor().getEmail())")
    @Mapping(target = "authorFirstName", expression = "java(ads.getAuthor().getFirstName())")
    @Mapping(target = "authorLastName", expression = "java(ads.getAuthor().getLastName())")
    FullAdsDto adsToFullAdsDto(Ads ads);

    default String mapImageToString(Image images) {
        if (images == null) {
            throw new IllegalArgumentException();
        }
        return images.getPath();
    }
}
