package ru.skypro.homework.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.skypro.homework.entity.Authority;

import java.util.List;
import java.util.Optional;

/**
 * The interface Authority repository.
 */
public interface AuthorityRepository extends JpaRepository<Authority, String> {

    @Query(value = "select * from authorities where username=:username and authority=:authority"
            , nativeQuery = true)
    Optional<Authority> findByUsernameAndAuthority(String username,String authority);

    @Query(value = "select * from authorities where username=:username"
            , nativeQuery = true)
    List<Authority> getAllByUsername(String username);

}
