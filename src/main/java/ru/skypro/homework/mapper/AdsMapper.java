package ru.skypro.homework.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import ru.skypro.homework.dto.AdsDto;
import ru.skypro.homework.dto.ResponseWrapperAdsDto;
import ru.skypro.homework.entity.Ads;
import ru.skypro.homework.entity.Image;
import ru.skypro.homework.service.impl.ImageServiceImpl;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public abstract class AdsMapper {
    @Autowired
    private ImageServiceImpl imageService;

    @Mapping(target = "pk", source = "ads.id")
    @Mapping(target = "author", expression = "java(ads.getAuthor().getId())")
    @Mapping(target = "price", source = "ads.price")
    @Mapping(target = "title", source = "ads.title")
    @Mapping(target = "image", source = "ads.image")    //is called mapImageToString()
    public abstract AdsDto adsToAdsDto(Ads ads);

    String mapImageToString(Image image) {
        if (image == null) {
            throw new IllegalArgumentException();
        }
        return imageService.getLinkOfImage(image);
    }

    @Mapping(target = "results", source = "list")
    public abstract ResponseWrapperAdsDto mapToResponseWrapperAdsDto(List<AdsDto> list, Integer count);

    ResponseWrapperAdsDto map(List<AdsDto> list) {
        return mapToResponseWrapperAdsDto(list, list.size());
    }


    public abstract List<AdsDto> mapListOfAdsToListDTO(List<Ads> listAds);
}
