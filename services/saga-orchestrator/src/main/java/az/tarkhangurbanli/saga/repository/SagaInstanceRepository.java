package az.tarkhangurbanli.saga.repository;

import az.tarkhangurbanli.saga.model.entity.SagaInstance;
import az.tarkhangurbanli.saga.model.enums.SagaStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface SagaInstanceRepository extends JpaRepository<SagaInstance, String> {

    List<SagaInstance> findByCustomerId(String customerId);

    @Query("SELECT s FROM SagaInstance s WHERE s.status IN :statuses")
    List<SagaInstance> findByStatusIn(List<SagaStatus> statuses);

    @Query("SELECT s FROM SagaInstance s WHERE s.status NOT IN ('COMPLETED', 'FAILED', 'CANCELLED') " +
            "AND s.updatedAt < :threshold")
    List<SagaInstance> findStuckSagas(LocalDateTime threshold);

}
