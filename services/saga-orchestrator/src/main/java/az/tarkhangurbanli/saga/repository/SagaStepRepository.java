package az.tarkhangurbanli.saga.repository;

import az.tarkhangurbanli.saga.model.entity.SagaStep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SagaStepRepository extends JpaRepository<SagaStep, Long> {

    @Query("SELECT s FROM SagaStep s WHERE s.sagaInstance.sagaId = :sagaId ORDER BY s.stepOrder")
    List<SagaStep> findBySagaIdOrderByStepOrder(String sagaId);

}
