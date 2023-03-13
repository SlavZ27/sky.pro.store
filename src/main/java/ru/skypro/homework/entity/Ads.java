package ru.skypro.homework.entity;

import lombok.*;
import org.hibernate.Hibernate;
import org.springframework.validation.annotation.Validated;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Validated
public class Ads {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;
    @OneToOne
    @JoinColumn(name = "id_author")
    @NotNull
    private User author;
    @Column(name = "price")
    @PositiveOrZero
    @NotNull
    private Integer price;
    @Column(name = "title")
    @NotNull
    private String title;
    @OneToOne
    @JoinColumn(name = "id_image")
    private Image image;
    @Column(name = "description")
    @NotNull
    private String description;
    @Column(name = "date_time")
    @PastOrPresent
    @NotNull
    private LocalDateTime dateTime; // yyyy-MM-dd HH:mm

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Ads ads = (Ads) o;
        return id != null && Objects.equals(id, ads.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
