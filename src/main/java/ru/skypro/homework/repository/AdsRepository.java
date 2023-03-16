package ru.skypro.homework.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.skypro.homework.entity.Ads;

import java.util.List;


/**
 * The interface Ads repository.
 */
@Repository
public interface AdsRepository extends JpaRepository<Ads, Integer> {

    @Query(value = "select * from ads order by date_time DESC"
            , nativeQuery = true)
    List<Ads> findAllAndSortDateTime();

    @Query(value = "select a from Ads a where a.title like %:title% order by a.dateTime DESC")
    List<Ads> findByTitleLike(String title);

    @Query(value = "select * from ads where id_author=:idAuthor order by date_time DESC"
            , nativeQuery = true)
    List<Ads> findAllByUserIdAndSortDateTime(Integer idAuthor);

    @Query(value = "select * from ads as a inner join users as u on a.id_author = u.id and u.username = :username order by date_time DESC"
            , nativeQuery = true)
    List<Ads> findAllByUsernameAndSortDateTime(String username);

}
