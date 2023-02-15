package ru.skypro.homework.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.skypro.homework.entity.Image;

import java.util.List;


@Repository
public interface ImageRepository extends JpaRepository<Image, Integer> {
    @Query(value = "select * from image where id_ads=:idAds"
            , nativeQuery = true)
    List<Image> findAllByIdAds(Integer idAds);

}
