package az.tarkhangurbanli.saga.service;

import az.tarkhangurbanli.saga.model.dto.request.StartSagaRequest;
import az.tarkhangurbanli.saga.model.dto.response.SagaResponse;
import az.tarkhangurbanli.saga.model.dto.response.SagaStepInfo;
import az.tarkhangurbanli.saga.model.entity.SagaInstance;
import az.tarkhangurbanli.saga.orchestrator.SagaOrchestrator;
import az.tarkhangurbanli.saga.repository.SagaInstanceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SagaService {

    private final SagaOrchestrator sagaOrchestrator;
    private final SagaInstanceRepository sagaInstanceRepository;

    /**
     * Start new saga
     */
    @Transactional
    public SagaResponse startSaga(StartSagaRequest request) {
        log.info("Starting new saga for customer: {}", request.customerId());

        SagaInstance saga = sagaOrchestrator.startSaga(request);

        return mapToResponse(saga);
    }

    /**
     * Get saga by ID
     */
    @Transactional(readOnly = true)
    public SagaResponse getSaga(String sagaId) {
        SagaInstance saga = sagaInstanceRepository.findById(sagaId)
                .orElseThrow(() -> new RuntimeException("Saga not found: " + sagaId));

        return mapToResponse(saga);
    }

    /**
     * Get all sagas for customer
     */
    @Transactional(readOnly = true)
    public List<SagaResponse> getCustomerSagas(String customerId) {
        return sagaInstanceRepository.findByCustomerId(customerId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    /**
     * Get all sagas
     */
    @Transactional(readOnly = true)
    public List<SagaResponse> getAllSagas() {
        return sagaInstanceRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    /**
     * Map entity to response
     */
    private SagaResponse mapToResponse(SagaInstance saga) {
        List<SagaStepInfo> steps = saga.getSteps().stream()
                .map(step -> new SagaStepInfo(
                        step.getStepType().name(),
                        step.getStatus().name(),
                        step.getStepOrder(),
                        step.getFailureReason(),
                        step.getCompletedAt()
                ))
                .toList();

        return new SagaResponse(
                saga.getSagaId(),
                saga.getCustomerId(),
                saga.getOrderId(),
                saga.getProductId(),
                saga.getQuantity(),
                saga.getAmount(),
                saga.getStatus(),
                saga.getFailureReason(),
                saga.getCreatedAt(),
                saga.getUpdatedAt(),
                saga.getCompletedAt(),
                steps
        );
    }

}
