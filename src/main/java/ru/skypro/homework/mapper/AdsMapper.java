package ru.skypro.homework.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.skypro.homework.dto.AdsDto;
import ru.skypro.homework.dto.ResponseWrapperAdsDto;
import ru.skypro.homework.entity.Ads;

import java.util.List;

/**
 * Provides methods for mapping Ads to Dto`s
 */
@Mapper(componentModel = "spring")
public abstract class AdsMapper {


    /**
     * Maps Ads to AdsDto.
     *
     * @param ads the ads
     * @return the ads dto
     */
    @Mapping(target = "pk", source = "ads.id")
    @Mapping(target = "author", expression = "java(ads.getAuthor().getId())")
    @Mapping(target = "price", source = "ads.price")
    @Mapping(target = "title", source = "ads.title")
    @Mapping(target = "image", source = "ads")
    public abstract AdsDto adsToAdsDto(Ads ads);

    /**
     * Map image to string.
     *
     * @param ads the ads
     * @return String - image path
     */
    String mapImageToString(Ads ads) {
        if (ads.getImage() == null || ads.getImage().getId() == null) {
            return null;
        } else {
            return "/ads/" + ads.getId() + "/image/";
        }
    }

    /**
     * Map list of AdsDto to ResponseWrapperAdsDto.
     *
     * @param list  the list
     * @param count the count
     * @return {@link ResponseWrapperAdsDto}
     */
    @Mapping(target = "results", source = "list")
    public abstract ResponseWrapperAdsDto mapToResponseWrapperAdsDto(List<AdsDto> list, Integer count);

    /**
     * Map list of ads to list of AdsDto.
     *
     * @param listAds the list ads
     * @return the list
     */
    public abstract List<AdsDto> mapListOfAdsToListDTO(List<Ads> listAds);
}
