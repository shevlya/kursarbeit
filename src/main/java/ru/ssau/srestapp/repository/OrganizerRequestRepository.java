package ru.ssau.srestapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.ssau.srestapp.entity.OrganizerRequest;
import ru.ssau.srestapp.entity.RequestStatus;

import java.util.List;
import java.util.Optional;

public interface OrganizerRequestRepository extends JpaRepository<OrganizerRequest, Long> {

    @Query("SELECT o FROM OrganizerRequest o WHERE o.user.idUser = :userId ORDER BY o.submittedAt DESC")
    Optional<OrganizerRequest> findLatestByUserId(@Param("userId") Long userId);

    @Query("SELECT o FROM OrganizerRequest o WHERE o.user.idUser = :userId AND o.requestStatus IN :statuses")
    Optional<OrganizerRequest> findByUserIdAndActiveStatus(@Param("userId") Long userId, @Param("statuses") List<RequestStatus> statuses);

    @Query("SELECT o FROM OrganizerRequest o JOIN FETCH o.user JOIN FETCH o.requestStatus ORDER BY o.submittedAt DESC")
    List<OrganizerRequest> findAllWithUser();

    List<OrganizerRequest> findByRequestStatus(RequestStatus status);

    @Query("SELECT o FROM OrganizerRequest o WHERE o.user.idUser = :userId ORDER BY o.submittedAt DESC")
    List<OrganizerRequest> findByUserId(@Param("userId") Long userId);

    long countByRequestStatus(RequestStatus status);
}
