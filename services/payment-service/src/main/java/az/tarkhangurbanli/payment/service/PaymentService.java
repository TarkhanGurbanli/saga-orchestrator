package az.tarkhangurbanli.payment.service;

import az.tarkhangurbanli.payment.kafka.publisher.EventPublisher;
import az.tarkhangurbanli.payment.model.command.ProcessPaymentCommand;
import az.tarkhangurbanli.payment.model.command.RefundPaymentCommand;
import az.tarkhangurbanli.payment.model.entity.Payment;
import az.tarkhangurbanli.payment.model.enums.PaymentStatus;
import az.tarkhangurbanli.payment.model.event.PaymentFailedEvent;
import az.tarkhangurbanli.payment.model.event.PaymentProcessedEvent;
import az.tarkhangurbanli.payment.model.event.PaymentRefundedEvent;
import az.tarkhangurbanli.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final EventPublisher eventPublisher;
    private final Random random = new Random();

    @Value("${payment.retry.max-attempts:3}")
    private int maxRetryAttempts;

    @Value("${payment.retry.delay-ms:2000}")
    private long retryDelayMs;

    /**
     * Handle Process Payment Command
     */
    @Transactional
    public void handleProcessPayment(ProcessPaymentCommand command) {
        try {
            String paymentId = UUID.randomUUID().toString();

            log.info("💳 Processing payment for saga: {}, order: {}",
                    command.sagaId(), command.orderId());

            // Check if payment already exists
            if (paymentRepository.findByOrderId(command.orderId()).isPresent()) {
                log.warn("Payment already exists for order: {}", command.orderId());
                return;
            }

            Payment payment = new Payment(
                    paymentId,
                    command.sagaId(),
                    command.orderId(),
                    command.customerId(),
                    command.amount(),
                    PaymentStatus.PROCESSING,
                    0
            );

            paymentRepository.save(payment);

            // Attempt payment with retry logic
            boolean success = attemptPayment(payment);

            if (success) {
                handlePaymentSuccess(payment);
            } else {
                handlePaymentFailure(payment, "Payment processing failed after " + maxRetryAttempts + " attempts");
            }

        } catch (Exception e) {
            log.error("Failed to process payment for saga: {}", command.sagaId(), e);

            PaymentFailedEvent event = new PaymentFailedEvent(
                    UUID.randomUUID().toString(),
                    command.sagaId(),
                    command.orderId(),
                    "Payment processing error: " + e.getMessage()
            );

            eventPublisher.publishPaymentFailedEvent(event);
        }
    }

    /**
     * Attempt payment with retry logic
     */
    private boolean attemptPayment(Payment payment) {
        for (int attempt = 0; attempt < maxRetryAttempts; attempt++) {
            try {
                // Simulate payment gateway call
                // 80% success rate for demo
                boolean success = random.nextInt(100) < 80;

                if (success) {
                    log.info("Payment successful on attempt {}/{}",
                            attempt + 1, maxRetryAttempts);
                    return true;
                }

                log.warn("⚠Payment attempt {}/{} failed",
                        attempt + 1, maxRetryAttempts);

                payment.setRetryCount(attempt + 1);
                paymentRepository.save(payment);

                // Exponential backoff
                if (attempt < maxRetryAttempts - 1) {
                    Thread.sleep(retryDelayMs * (attempt + 1));
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("Payment retry interrupted", e);
                return false;
            } catch (Exception e) {
                log.error("Error during payment attempt", e);
            }
        }

        return false;
    }

    /**
     * Handle successful payment
     */
    private void handlePaymentSuccess(Payment payment) {
        payment.setStatus(PaymentStatus.COMPLETED);
        paymentRepository.save(payment);

        log.info("Payment completed: {}", payment.getPaymentId());

        PaymentProcessedEvent event = new PaymentProcessedEvent(
                UUID.randomUUID().toString(),
                payment.getSagaId(),
                payment.getOrderId(),
                payment.getPaymentId(),
                payment.getAmount()
        );

        eventPublisher.publishPaymentProcessedEvent(event);
    }

    /**
     * Handle payment failure
     */
    private void handlePaymentFailure(Payment payment, String reason) {
        payment.setStatus(PaymentStatus.FAILED);
        payment.setFailureReason(reason);
        paymentRepository.save(payment);

        log.error("Payment failed: {}, reason: {}", payment.getPaymentId(), reason);

        PaymentFailedEvent event = new PaymentFailedEvent(
                UUID.randomUUID().toString(),
                payment.getSagaId(),
                payment.getOrderId(),
                reason
        );

        eventPublisher.publishPaymentFailedEvent(event);
    }

    /**
     * Handle Refund Payment Command (Compensation)
     */
    @Transactional
    public void handleRefundPayment(RefundPaymentCommand command) {
        try {
            log.info("Refunding payment: {} for saga: {}",
                    command.paymentId(), command.sagaId());

            Payment payment = paymentRepository.findById(command.paymentId())
                    .orElseThrow(() -> new RuntimeException("Payment not found: " + command.paymentId()));

            payment.setStatus(PaymentStatus.REFUNDED);
            payment.setRefundReason(command.reason());
            payment.setRefundedAt(LocalDateTime.now());

            paymentRepository.save(payment);

            log.info("Payment refunded successfully: {}", command.paymentId());

            PaymentRefundedEvent event = new PaymentRefundedEvent(
                    UUID.randomUUID().toString(),
                    command.sagaId(),
                    command.orderId(),
                    command.paymentId(),
                    command.amount()
            );

            eventPublisher.publishPaymentRefundedEvent(event);

        } catch (Exception e) {
            log.error("Failed to refund payment: {}", command.paymentId(), e);
        }
    }
}