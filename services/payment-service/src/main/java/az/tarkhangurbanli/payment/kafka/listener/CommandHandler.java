package az.tarkhangurbanli.payment.kafka.listener;

import az.tarkhangurbanli.payment.model.command.ProcessPaymentCommand;
import az.tarkhangurbanli.payment.model.command.RefundPaymentCommand;
import az.tarkhangurbanli.payment.service.PaymentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CommandHandler {

    private final PaymentService paymentService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "payment-commands", groupId = "payment-service-group")
    public void handleCommand(@Payload String message, Acknowledgment ack) {
        try {
            log.debug("Received payment command");

            var jsonNode = objectMapper.readTree(message);
            String commandType = jsonNode.get("commandType").asText();

            switch (commandType) {
                case "PROCESS_PAYMENT" -> {
                    ProcessPaymentCommand cmd = objectMapper.readValue(message, ProcessPaymentCommand.class);
                    paymentService.handleProcessPayment(cmd);
                }
                case "REFUND_PAYMENT" -> {
                    RefundPaymentCommand cmd = objectMapper.readValue(message, RefundPaymentCommand.class);
                    paymentService.handleRefundPayment(cmd);
                }
                default -> log.warn("Unknown command type: {}", commandType);
            }

            ack.acknowledge();

        } catch (Exception e) {
            log.error("Error handling payment command", e);
            ack.acknowledge();
        }
    }

}