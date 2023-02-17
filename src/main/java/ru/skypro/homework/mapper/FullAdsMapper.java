package ru.skypro.homework.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import ru.skypro.homework.dto.FullAdsDto;
import ru.skypro.homework.entity.Ads;
import ru.skypro.homework.entity.Image;
import ru.skypro.homework.service.impl.ImageServiceImpl;

import java.util.List;


@Mapper(componentModel = "spring")
public abstract class FullAdsMapper {

    @Autowired
    private ImageServiceImpl imageService;


    @Mapping(target = "pk", source = "ads.id")
    @Mapping(target = "price", source = "ads.price")
    @Mapping(target = "title", source = "ads.title")
    @Mapping(target = "image", source = "images") // is called mapImageToString(Image images)
    @Mapping(target = "description", source = "ads.description")
    @Mapping(target = "phone", expression = "java(ads.getAuthor().getPhone())")
    @Mapping(target = "email", expression = "java(ads.getAuthor().getEmail())")
    @Mapping(target = "authorFirstName", expression = "java(ads.getAuthor().getFirstName())")
    @Mapping(target = "authorLastName", expression = "java(ads.getAuthor().getLastName())")
    public abstract FullAdsDto adsToFullAdsDto(Ads ads);

     String mapImageToString(List<Image> images) {
        if (images == null) {
            throw new IllegalArgumentException();
        }
        return imageService.getLinkOfImageOfAds(images.get(0).getId());
    }
}
