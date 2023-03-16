package ru.skypro.homework.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.skypro.homework.entity.Image;


/**
 * The interface Image repository.
 */
@Repository
public interface ImageRepository extends JpaRepository<Image, Integer> {

}
