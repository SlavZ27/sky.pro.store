package ru.skypro.homework.entity;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.List;
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
    @ToString.Exclude
    @OneToMany(mappedBy = "ads", fetch = FetchType.EAGER)
    private List<Image> images;
    @Column(name = "description")
    private String description;

    public Ads(User author,
               Integer price,
               String title,
               List<Image> images,
               String description) {
        this.author = author;
        this.price = price;
        this.title = title;
        this.images = images;
        this.description = description;
    }

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
