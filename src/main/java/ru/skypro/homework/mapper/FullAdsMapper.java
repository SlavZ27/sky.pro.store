package ru.skypro.homework.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.skypro.homework.dto.FullAdsDto;
import ru.skypro.homework.entity.Ads;


/**
 * Provides methods for mapping FullAdsDto`s to Ads
 */
@Mapper(componentModel = "spring")
public abstract class FullAdsMapper {

    /**
     * Map Ads to FullAdsDto
     *
     * @param ads the ads
     * @return {@link FullAdsDto}
     */
    @Mapping(target = "pk", source = "ads.id")
    @Mapping(target = "price", source = "ads.price")
    @Mapping(target = "title", source = "ads.title")
    @Mapping(target = "image", source = "ads") // is called mapImageToString(Image images)
    @Mapping(target = "description", source = "ads.description")
    @Mapping(target = "phone", expression = "java(ads.getAuthor().getPhone())")
    @Mapping(target = "email", expression = "java(ads.getAuthor().getEmail())")
    @Mapping(target = "authorFirstName", expression = "java(ads.getAuthor().getFirstName())")
    @Mapping(target = "authorLastName", expression = "java(ads.getAuthor().getLastName())")
    public abstract FullAdsDto adsToFullAdsDto(Ads ads);

    /**
     * Map image to string .
     *
     * @param ads the ads
     * @return the string - path to image
     */
    String mapImageToString(Ads ads) {
        if (ads.getImage() == null || ads.getImage().getId() == null) {
            return null;
        } else {
            return "/ads/" + ads.getId() + "/image/";
        }
    }
}
