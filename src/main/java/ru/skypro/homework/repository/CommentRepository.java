package ru.skypro.homework.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.skypro.homework.entity.Comment;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {

    @Query(value = "select * from comment where id_ads=:idAds order by date_time"
            , nativeQuery = true)
    List<Comment> findAllByIdAdsAndSortDateTime(Integer idAds);

    @Query(value = "select * from comment where id_ads=:idAds"
            , nativeQuery = true)
    List<Comment> findAllByIdAds(Integer idAds);

    @Query(value = "select * from comment where id=:commentId and id_ads=:adsId"
            , nativeQuery = true)
    Optional<Comment> findAllByIdAndAdsId(Integer adsId, Integer commentId);


    @Query(value = "DELETE FROM comment WHERE id_ads=:adsId"
            , nativeQuery = true)
    void deleteAllByAdsId(Integer adsId);

}
