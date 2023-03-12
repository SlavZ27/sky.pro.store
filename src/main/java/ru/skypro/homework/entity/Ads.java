package ru.skypro.homework.entity;

import lombok.*;
import org.hibernate.Hibernate;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
public class Ads {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;
    @OneToOne
    @JoinColumn(name = "id_author")
    private User author;
    @Column(name = "price")
    private Integer price;
    @Column(name = "title")
    private String title;
    @OneToOne
    @JoinColumn(name = "id_image")
    private Image image;
    @Column(name = "description")
    private String description;
    @Column(name = "date_time")
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
