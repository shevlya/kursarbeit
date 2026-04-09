package ru.ssau.srestapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.ssau.srestapp.entity.UserInterest;
import ru.ssau.srestapp.entity.UserInterestId;

import java.util.List;

public interface UserInterestRepository extends JpaRepository<UserInterest, UserInterestId> {

    @Query("SELECT ui FROM UserInterest ui JOIN FETCH ui.idEventCategory WHERE ui.idUser.idUser = :userId")
    List<UserInterest> findByUserId(@Param("userId") Long userId);

    boolean existsByIdUser_IdUserAndIdEventCategory_IdEventCategory(Long userId, Long categoryId);

    void deleteByIdUser_IdUserAndIdEventCategory_IdEventCategory(Long userId, Long categoryId);

    void deleteAllByIdUser_IdUser(Long userId);

    @Query("SELECT ui FROM UserInterest ui JOIN FETCH ui.idEventCategory WHERE ui.idUser.idUser = :userId")
    List<UserInterest> findActiveInterestsByUserId(@Param("userId") Long userId);
}
