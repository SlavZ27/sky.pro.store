package ru.skypro.homework.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.skypro.homework.entity.Comment;
import ru.skypro.homework.entity.Image;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {

    @Query(value = "select * from comment where id_ads=:idAds"
            , nativeQuery = true)
    List<Comment> findAllByIdAds(Integer idAds);
}
