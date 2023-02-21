package ru.skypro.homework.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import ru.skypro.homework.dto.UserDto;
import ru.skypro.homework.entity.Ads;
import ru.skypro.homework.entity.User;
import ru.skypro.homework.service.impl.AdsServiceImpl;
import ru.skypro.homework.service.impl.UserServiceImpl;

@Mapper(componentModel = "spring")
public abstract class UserMapper {


    @Mapping(target = "id", source = "user.id")
    @Mapping(target = "email", source = "user.email")
    @Mapping(target = "firstName", source = "user.firstName")
    @Mapping(target = "lastName", source = "user.lastName")
    @Mapping(target = "phone", source = "user.phone")
    @Mapping(target = "regDate", source = "user.regDate", dateFormat = "d/MM/yyyy")
    @Mapping(target = "image", source = "user")
    public abstract UserDto userToDto(User user);

    @Mapping(target = "id", source = "userDto.id")
    @Mapping(target = "email", source = "userDto.email")
    @Mapping(target = "firstName", source = "userDto.firstName")
    @Mapping(target = "lastName", source = "userDto.lastName")
    @Mapping(target = "phone", source = "userDto.phone")
    @Mapping(target = "regDate", source = "userDto.regDate", dateFormat = "d/MM/yyyy")
    public abstract User userDtoToUser(UserDto userDto);

    String mapImageToString(User user) {
        if (user.getAvatar() == null || user.getAvatar().getId() == null) {
            return null;
        } else {
            return "/users/" + user.getId() + "/image";
        }
    }
}
