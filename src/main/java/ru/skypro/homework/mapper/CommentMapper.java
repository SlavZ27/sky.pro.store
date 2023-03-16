package ru.skypro.homework.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import ru.skypro.homework.dto.CommentDto;
import ru.skypro.homework.dto.ResponseWrapperCommentDto;
import ru.skypro.homework.entity.Comment;
import ru.skypro.homework.entity.User;
import ru.skypro.homework.exception.UserNotFoundException;
import ru.skypro.homework.repository.UsersRepository;

import java.util.List;


/**
 * Provides methods for mapping Comment to Dto`s
 */
@Mapper(componentModel = "spring")
public abstract class CommentMapper {

    /**
     * The Users repository.
     */
    @Autowired
    protected UsersRepository usersRepository;

    /**
     * Map Comment to CommentDto
     *
     * @param comment the comment
     * @return CommentDto
     */
    @Mapping(target = "pk", source = "comment.id")
    @Mapping(target = "author", expression = "java(comment.getAuthor().getId())")
    @Mapping(target = "text", source = "comment.text")
    @Mapping(target = "createdAt", source = "comment.dateTime", dateFormat = "yyyy-MM-dd HH:mm")
    public abstract CommentDto commentToDto(Comment comment);

    /**
     * Map CommentDto to Comment.
     *
     * @param commentDto the comment dto
     * @return {@link Comment}
     */
    @Mapping(target = "id", source = "commentDto.pk")
    @Mapping(target = "author", source = "commentDto") //is called dtoToUser()
    @Mapping(target = "text", source = "commentDto.text")
    @Mapping(target = "dateTime", source = "commentDto.createdAt", dateFormat = "yyyy-MM-dd HH:mm")
    public abstract Comment dtoToComment(CommentDto commentDto);

    /**
     * Map CommentDto to User
     *
     * @param commentDto the comment dto
     * @return {@link User}
     */
    User dtoToUser(CommentDto commentDto) {
        if (commentDto == null || commentDto.getAuthor() == null) {
            return null;
        }
        User author = null;
        if (commentDto.getAuthor() != null) {
            author = usersRepository.findById(commentDto.getAuthor()).orElseThrow(() ->
                    new UserNotFoundException(String.valueOf(commentDto.getAuthor())));
        }
        return author;
    }

    /**
     * Map list of CommentDto to ResponseWrapperCommentDto.
     *
     * @param count          the count
     * @param commentDtoList the comment dto list
     * @return {@link ResponseWrapperCommentDto}
     */
    @Mapping(target = "results", source = "commentDtoList")
    public abstract ResponseWrapperCommentDto mapListOfCommentDtoToResponseWrapper(Integer count, List<CommentDto> commentDtoList);

    /**
     * Map list of Comment to list CommentDto.
     *
     * @param commentList the comment list
     * @return the list of CommentDto
     */
    public abstract List<CommentDto> mapListOfCommentToListDto(List<Comment> commentList);

}
