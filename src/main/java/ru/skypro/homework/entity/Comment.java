package ru.skypro.homework.entity;

import lombok.*;
import org.hibernate.Hibernate;
import org.springframework.validation.annotation.Validated;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Validated
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;
    @OneToOne
    @JoinColumn(name = "id_author")
    @NotNull
    private User author;
    @OneToOne
    @NotNull
    @JoinColumn(name = "id_ads")
    private Ads ads;
    @Column(name = "text")
    @NotNull
    private String text;
    @Column(name = "date_time")
    @NotNull
    @PastOrPresent
    private LocalDateTime dateTime; // yyyy-MM-dd HH:mm

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Comment comment = (Comment) o;
        return id != null && Objects.equals(id, comment.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
