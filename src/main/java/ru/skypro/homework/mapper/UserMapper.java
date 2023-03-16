package ru.skypro.homework.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.skypro.homework.dto.RegisterReqDto;
import ru.skypro.homework.dto.UserDto;
import ru.skypro.homework.entity.User;


/**
 * Provides methods for mapping User to Dto`s
 */
@Mapper(componentModel = "spring")
public abstract class UserMapper {


    /**
     * User to UserDto.
     *
     * @param user the user
     * @return {@link UserDto}
     */
    @Mapping(target = "id", source = "user.id")
    @Mapping(target = "email", source = "user.email")
    @Mapping(target = "firstName", source = "user.firstName")
    @Mapping(target = "lastName", source = "user.lastName")
    @Mapping(target = "phone", source = "user.phone")
    @Mapping(target = "regDate", source = "user.regDate", dateFormat = "d/MM/yyyy")
    @Mapping(target = "image", source = "user")
    public abstract UserDto userToDto(User user);

    /**
     * UserDto to User.
     *
     * @param userDto the user dto
     * @return {@link User}
     */
    @Mapping(target = "id", source = "userDto.id")
    @Mapping(target = "email", source = "userDto.email")
    @Mapping(target = "firstName", source = "userDto.firstName")
    @Mapping(target = "lastName", source = "userDto.lastName")
    @Mapping(target = "phone", source = "userDto.phone")
    @Mapping(target = "regDate", source = "userDto.regDate", dateFormat = "d/MM/yyyy")
    public abstract User userDtoToUser(UserDto userDto);

    /**
     * RegisterReqDto to User.
     *
     * @param registerReq the register req
     * @param pass        the pass
     * @return {@link User}
     */
    @Mapping(target = "username", source = "registerReq.username")
    @Mapping(target = "password", source = "pass")
    @Mapping(target = "firstName", source = "registerReq.firstName")
    @Mapping(target = "lastName", source = "registerReq.lastName")
    @Mapping(target = "phone", source = "registerReq.phone")
    public abstract User registerReqToUser(RegisterReqDto registerReq, String pass);

    /**
     * Map image to string.
     *
     * @param user the user
     * @return the string - path to image
     */
    String mapImageToString(User user) {
        if (user.getAvatar() == null || user.getAvatar().getId() == null) {
            return null;
        } else {
            return "/users/" + user.getId() + "/image";
        }
    }
}
