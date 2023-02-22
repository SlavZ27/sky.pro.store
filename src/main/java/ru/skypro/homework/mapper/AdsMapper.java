package ru.skypro.homework.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.skypro.homework.dto.AdsDto;
import ru.skypro.homework.dto.ResponseWrapperAdsDto;
import ru.skypro.homework.entity.Ads;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class AdsMapper {


    @Mapping(target = "pk", source = "ads.id")
    @Mapping(target = "author", expression = "java(ads.getAuthor().getId())")
    @Mapping(target = "price", source = "ads.price")
    @Mapping(target = "title", source = "ads.title")
    @Mapping(target = "image", source = "ads")    //is called mapImageToString()
    public abstract AdsDto adsToAdsDto(Ads ads);

    String mapImageToString(Ads ads) {
        if (ads.getImage() == null || ads.getImage().getId() == null) {
            return null;
        } else {
            return "/ads/" + ads.getId() + "/image/";
        }
    }

    @Mapping(target = "results", source = "list")
    public abstract ResponseWrapperAdsDto mapToResponseWrapperAdsDto(List<AdsDto> list, Integer count);

    public abstract List<AdsDto> mapListOfAdsToListDTO(List<Ads> listAds);
}
