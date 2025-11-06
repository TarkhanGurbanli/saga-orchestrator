package az.tarkhangurbanli.saga.orchestrator;

import az.tarkhangurbanli.saga.kafka.publisher.CommandPublisher;
import az.tarkhangurbanli.saga.model.command.CancelOrderCommand;
import az.tarkhangurbanli.saga.model.command.CreateOrderCommand;
import az.tarkhangurbanli.saga.model.command.ProcessPaymentCommand;
import az.tarkhangurbanli.saga.model.command.RefundPaymentCommand;
import az.tarkhangurbanli.saga.model.command.ReserveInventoryCommand;
import az.tarkhangurbanli.saga.model.dto.request.StartSagaRequest;
import az.tarkhangurbanli.saga.model.entity.SagaInstance;
import az.tarkhangurbanli.saga.model.entity.SagaStep;
import az.tarkhangurbanli.saga.model.enums.SagaStatus;
import az.tarkhangurbanli.saga.model.enums.SagaStepStatus;
import az.tarkhangurbanli.saga.model.enums.SagaStepType;
import az.tarkhangurbanli.saga.model.event.InventoryFailedEvent;
import az.tarkhangurbanli.saga.model.event.InventoryReservedEvent;
import az.tarkhangurbanli.saga.model.event.OrderCancelledEvent;
import az.tarkhangurbanli.saga.model.event.OrderCreatedEvent;
import az.tarkhangurbanli.saga.model.event.PaymentFailedEvent;
import az.tarkhangurbanli.saga.model.event.PaymentProcessedEvent;
import az.tarkhangurbanli.saga.model.event.PaymentRefundedEvent;
import az.tarkhangurbanli.saga.repository.SagaInstanceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SagaOrchestrator {

    private final SagaInstanceRepository sagaInstanceRepository;
    private final CommandPublisher commandPublisher;

    /**
     * Start new saga
     */

    @Transactional
    public SagaInstance startSaga(StartSagaRequest request) {
        String sagaId = UUID.randomUUID().toString();

        log.info("[START-SAGA] Starting new saga: {}", sagaId);

        // Create saga instance
        SagaInstance saga = SagaInstance.builder()
                .sagaId(sagaId)
                .customerId(request.customerId())
                .productId(request.productId())
                .quantity(request.quantity())
                .amount(request.amount())
                .status(SagaStatus.STARTED)
                .retryCount(0)
                .build();

        sagaInstanceRepository.save(saga);

        // Start first app: Create Order
        executeCreateOrder(saga);

        return saga;
    }

    /**
     * Step 1: Create Order
     */
    @Transactional
    public void executeCreateOrder(SagaInstance saga) {
        log.info("[STEP 1]: Creating order for saga: {}", saga.getSagaId());

        SagaStep step = createStep(saga, SagaStepType.CREATE_ORDER, 1);
        saga.setStatus(SagaStatus.ORDER_CREATED);
        saga.addStep(step);
        sagaInstanceRepository.save(saga);

        // Send Command
        String commandId = UUID.randomUUID().toString();
        step.setCommandId(commandId);

        CreateOrderCommand command = new CreateOrderCommand(
                commandId,
                saga.getSagaId(),
                saga.getCustomerId(),
                saga.getProductId(),
                saga.getQuantity(),
                saga.getAmount()
        );

        commandPublisher.publishCreateOrderCommand(command);
    }

    /**
     * Handle Order Created Event
     */
    @Transactional
    public void handleOrderCreated(OrderCreatedEvent event) {
        log.info("✅ Order created for saga: {}", event.sagaId());

        SagaInstance saga = getSaga(event.sagaId());
        saga.setOrderId(event.orderId());
        saga.setStatus(SagaStatus.ORDER_CREATED);

        completeCurrentStep(saga, event.eventId());

        // Move to next step: Process Payment
        executeProcessPayment(saga);
    }

    /**
     * Step 2: Process Payment
     */
    @Transactional
    public void executeProcessPayment(SagaInstance saga) {
        log.info("[STEP 2]: Processing payment for saga: {}", saga.getSagaId());

        SagaStep step = createStep(saga, SagaStepType.PROCESS_PAYMENT, 2);
        saga.setStatus(SagaStatus.PAYMENT_PENDING);
        saga.addStep(step);
        sagaInstanceRepository.save(saga);

        String commandId = UUID.randomUUID().toString();
        step.setCommandId(commandId);

        ProcessPaymentCommand command = new ProcessPaymentCommand(
                commandId,
                saga.getSagaId(),
                saga.getOrderId(),
                saga.getCustomerId(),
                saga.getAmount()
        );

        commandPublisher.publishProcessPaymentCommand(command);
    }

    /**
     * Handle Payment Processed Event
     */
    @Transactional
    public void handlePaymentProcessed(PaymentProcessedEvent event) {
        log.info("✅ Payment processed for saga: {}", event.sagaId());

        SagaInstance saga = getSaga(event.sagaId());
        saga.setPaymentId(event.paymentId());
        saga.setStatus(SagaStatus.PAYMENT_COMPLETED);

        completeCurrentStep(saga, event.eventId());

        // Move to next step: Reserve Inventory
        executeReserveInventory(saga);
    }

    /**
     * Step 3: Reserve Inventory
     */
    @Transactional
    public void executeReserveInventory(SagaInstance saga) {
        log.info("[STEP 3]: Reserving inventory for saga: {}", saga.getSagaId());

        SagaStep step = createStep(saga, SagaStepType.RESERVE_INVENTORY, 3);
        saga.setStatus(SagaStatus.INVENTORY_PENDING);
        saga.addStep(step);
        sagaInstanceRepository.save(saga);

        String commandId = UUID.randomUUID().toString();
        step.setCommandId(commandId);

        ReserveInventoryCommand command = new ReserveInventoryCommand(
                commandId,
                saga.getSagaId(),
                saga.getOrderId(),
                saga.getProductId(),
                saga.getQuantity()
        );

        commandPublisher.publishReserveInventoryCommand(command);
    }

    /**
     * Handle Inventory Reserved Event
     */
    @Transactional
    public void handleInventoryReserved(InventoryReservedEvent event) {
        log.info("✅ Inventory reserved for saga: {}", event.sagaId());

        SagaInstance saga = getSaga(event.sagaId());
        saga.setStatus(SagaStatus.COMPLETED);
        saga.setCompletedAt(LocalDateTime.now());

        completeCurrentStep(saga, event.eventId());
        sagaInstanceRepository.save(saga);

        log.info("🎉 Saga completed successfully: {}", saga.getSagaId());
    }

    /**
     * Handle Payment Failed - Start Compensation
     */
    @Transactional
    public void handlePaymentFailed(PaymentFailedEvent event) {
        log.warn("❌ Payment failed for saga: {}, reason: {}", event.sagaId(), event.reason());

        SagaInstance saga = getSaga(event.sagaId());
        saga.setStatus(SagaStatus.PAYMENT_FAILED);
        saga.setFailureReason(event.reason());

        failCurrentStep(saga, event.reason());

        // Start compensation
        startCompensation(saga, event.reason());
    }

    /**
     * Handle Inventory Failed - Start Compensation
     */
    @Transactional
    public void handleInventoryFailed(InventoryFailedEvent event) {
        log.warn("❌ Inventory reservation failed for saga: {}, reason: {}",
                event.sagaId(), event.reason());

        SagaInstance saga = getSaga(event.sagaId());
        saga.setStatus(SagaStatus.INVENTORY_FAILED);
        saga.setFailureReason(event.reason());

        failCurrentStep(saga, event.reason());

        // Start compensation
        startCompensation(saga, event.reason());
    }

    /**
     * Start Compensation (Rollback)
     */
    @Transactional
    public void startCompensation(SagaInstance saga, String reason) {
        log.warn("⚠️ Starting compensation for saga: {}", saga.getSagaId());

        saga.setStatus(SagaStatus.COMPENSATING);
        sagaInstanceRepository.save(saga);

        // Compensate in reverse order
        if (saga.getPaymentId() != null) {
            compensatePayment(saga, reason);
        } else if (saga.getOrderId() != null) {
            compensateOrder(saga, reason);
        } else {
            completeSagaWithFailure(saga, reason);
        }
    }

    /**
     * Compensate Payment
     */
    @Transactional
    public void compensatePayment(SagaInstance saga, String reason) {
        log.info("↩️ Compensating payment for saga: {}", saga.getSagaId());

        String commandId = UUID.randomUUID().toString();

        RefundPaymentCommand command = new RefundPaymentCommand(
                commandId,
                saga.getSagaId(),
                saga.getOrderId(),
                saga.getPaymentId(),
                saga.getAmount(),
                reason
        );

        commandPublisher.publishRefundPaymentCommand(command);
    }

    /**
     * Handle Payment Refunded
     */
    @Transactional
    public void handlePaymentRefunded(PaymentRefundedEvent event) {
        log.info("✅ Payment refunded for saga: {}", event.sagaId());

        SagaInstance saga = getSaga(event.sagaId());

        // Continue compensation: Cancel Order
        compensateOrder(saga, saga.getFailureReason());
    }

    /**
     * Compensate Order
     */
    @Transactional
    public void compensateOrder(SagaInstance saga, String reason) {
        log.info("↩️ Compensating order for saga: {}", saga.getSagaId());

        String commandId = UUID.randomUUID().toString();

        CancelOrderCommand command = new CancelOrderCommand(
                commandId,
                saga.getSagaId(),
                saga.getOrderId(),
                reason
        );

        commandPublisher.publishCancelOrderCommand(command);
    }

    /**
     * Handle Order Cancelled
     */
    @Transactional
    public void handleOrderCancelled(OrderCancelledEvent event) {
        log.info("✅ Order cancelled for saga: {}", event.sagaId());

        SagaInstance saga = getSaga(event.sagaId());
        completeSagaWithFailure(saga, saga.getFailureReason());
    }

    /**
     * Complete saga with failure
     */
    private void completeSagaWithFailure(SagaInstance saga, String reason) {
        saga.setStatus(SagaStatus.FAILED);
        saga.setFailureReason(reason);
        saga.setCompletedAt(LocalDateTime.now());
        sagaInstanceRepository.save(saga);

        log.error("❌ Saga failed: {}, reason: {}", saga.getSagaId(), reason);
    }

    /**
     * Helper: Create saga step
     */
    private SagaStep createStep(SagaInstance saga, SagaStepType stepType, int order) {
        return SagaStep.builder()
                .sagaInstance(saga)
                .stepType(stepType)
                .status(SagaStepStatus.IN_PROGRESS)
                .stepOrder(order)
                .retryCount(0)
                .build();
    }

    /**
     * Helper: Complete current step
     */
    private void completeCurrentStep(SagaInstance saga, String eventId) {
        saga.getSteps().stream()
                .filter(step -> step.getStatus() == SagaStepStatus.IN_PROGRESS)
                .forEach(step -> {
                    step.setStatus(SagaStepStatus.COMPLETED);
                    step.setEventId(eventId);
                    step.setCompletedAt(LocalDateTime.now());
                });
    }

    /**
     * Helper: Fail current step
     */
    private void failCurrentStep(SagaInstance saga, String reason) {
        saga.getSteps().stream()
                .filter(step -> step.getStatus() == SagaStepStatus.IN_PROGRESS)
                .forEach(step -> {
                    step.setStatus(SagaStepStatus.FAILED);
                    step.setFailureReason(reason);
                });
    }

    /**
     * Helper: Get saga instance
     */
    private SagaInstance getSaga(String sagaId) {
        return sagaInstanceRepository.findById(sagaId)
                .orElseThrow(() -> new RuntimeException("Saga not found: " + sagaId));
    }

}
