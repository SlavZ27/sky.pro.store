package ru.skypro.homework.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ContextConfiguration;
import ru.skypro.homework.dto.CommentDto;
import ru.skypro.homework.entity.Comment;
import ru.skypro.homework.entity.User;
import ru.skypro.homework.repository.UsersRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = {
        CommentMapperImpl.class
})
public class CommentMapperTest {

//    @Spy
//    private CommentMapper commentMapper = Mappers.getMapper(CommentMapper.class);

    @InjectMocks
    private CommentMapperImpl commentMapperTest;

    @Mock
    private UsersRepository usersRepository;

    @Test
    public void commentToDtoTest() {
        Comment comment = new Comment();
        comment.setId(111);
        comment.setText("Test text");
        comment.setDateTime(LocalDateTime.parse("2023-02-11 15:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));

        User user = new User();
        user.setId(111);
        user.setEmail("some@mail.com");
        user.setFirstName("Vasya");
        user.setLastName("Vasin");
        user.setPhone("+79990001122");
        comment.setAuthor(user);

        CommentDto commentDto = commentMapperTest.commentToDto(comment);
        when(usersRepository.findById(111)).thenReturn(Optional.of(user));
        Comment comment2 = commentMapperTest.dtoToComment(commentDto);

        assertEquals(commentDto.getPk(), 111);
        assertEquals(commentDto.getText(), "Test text");
        assertEquals(commentDto.getCreatedAt(), "2023-02-11 15:00");
        assertEquals(commentDto.getAuthor(), 111);
        assertThat(comment)
                .usingRecursiveComparison().ignoringFields("id")
                .isEqualTo(comment2);
    }

    @Test
    public void dtoToCommentTest() {
        CommentDto commentDto = new CommentDto();
        commentDto.setPk(111);
        commentDto.setText("Test text");
        commentDto.setCreatedAt("2023-02-11 15:00");

        User user = new User();
        user.setId(111);
        when(usersRepository.findById(111)).thenReturn(Optional.of(user));
        commentDto.setAuthor(user.getId());

        Comment comment = commentMapperTest.dtoToComment(commentDto);
        assertEquals(comment.getId(), 111);
        assertEquals(comment.getText(), "Test text");
        assertEquals(comment.getDateTime(), LocalDateTime.parse("2023-02-11 15:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        assertEquals(comment.getAuthor(), user);
    }
}
