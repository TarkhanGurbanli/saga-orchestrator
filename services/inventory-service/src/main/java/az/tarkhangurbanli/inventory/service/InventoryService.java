package az.tarkhangurbanli.inventory.service;

import az.tarkhangurbanli.inventory.kafka.publisher.EventPublisher;
import az.tarkhangurbanli.inventory.model.command.ReleaseInventoryCommand;
import az.tarkhangurbanli.inventory.model.command.ReserveInventoryCommand;
import az.tarkhangurbanli.inventory.model.entity.Inventory;
import az.tarkhangurbanli.inventory.model.entity.Reservation;
import az.tarkhangurbanli.inventory.model.enums.ReservationStatus;
import az.tarkhangurbanli.inventory.model.event.InventoryFailedEvent;
import az.tarkhangurbanli.inventory.model.event.InventoryReleasedEvent;
import az.tarkhangurbanli.inventory.model.event.InventoryReservedEvent;
import az.tarkhangurbanli.inventory.repository.InventoryRepository;
import az.tarkhangurbanli.inventory.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ReservationRepository reservationRepository;
    private final EventPublisher eventPublisher;

    /**
     * Handle Reserve Inventory Command
     */
    @Transactional
    public void handleReserveInventory(ReserveInventoryCommand command) {
        try {
            log.info("Reserving inventory for saga: {}, order: {}, product: {}, quantity: {}",
                    command.sagaId(), command.orderId(), command.productId(), command.quantity());

            // Check if reservation already exists
            if (reservationRepository.findByOrderId(command.orderId()).isPresent()) {
                log.warn("Reservation already exists for order: {}", command.orderId());
                return;
            }

            // Get inventory
            Inventory inventory = inventoryRepository.findById(command.productId())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + command.productId()));

            // Check availability
            if (inventory.getAvailableQuantity() < command.quantity()) {
                handleReservationFailure(command,
                        String.format("Insufficient inventory. Available: %d, Requested: %d",
                                inventory.getAvailableQuantity(), command.quantity()));
                return;
            }

            // Reserve inventory
            inventory.setAvailableQuantity(inventory.getAvailableQuantity() - command.quantity());
            inventory.setReservedQuantity(inventory.getReservedQuantity() + command.quantity());
            inventoryRepository.save(inventory);

            // Create reservation record
            String reservationId = UUID.randomUUID().toString();
            Reservation reservation = new Reservation(
                    reservationId,
                    command.sagaId(),
                    command.orderId(),
                    command.productId(),
                    command.quantity(),
                    ReservationStatus.RESERVED
            );

            reservationRepository.save(reservation);

            log.info("Inventory reserved successfully: {} units of product {}",
                    command.quantity(), command.productId());

            // Publish success event
            InventoryReservedEvent event = new InventoryReservedEvent(
                    UUID.randomUUID().toString(),
                    command.sagaId(),
                    command.orderId(),
                    command.productId(),
                    command.quantity()
            );

            eventPublisher.publishInventoryReservedEvent(event);

        } catch (Exception e) {
            log.error("Failed to reserve inventory for saga: {}", command.sagaId(), e);
            handleReservationFailure(command, "Inventory reservation error: " + e.getMessage());
        }
    }

    /**
     * Handle reservation failure
     */
    private void handleReservationFailure(ReserveInventoryCommand command, String reason) {
        // Create failed reservation record
        String reservationId = UUID.randomUUID().toString();
        Reservation reservation = new Reservation(
                reservationId,
                command.sagaId(),
                command.orderId(),
                command.productId(),
                command.quantity(),
                ReservationStatus.FAILED,
                reason
        );

        reservationRepository.save(reservation);

        log.error("Inventory reservation failed: {}", reason);

        // Publish failure event
        InventoryFailedEvent event = new InventoryFailedEvent(
                UUID.randomUUID().toString(),
                command.sagaId(),
                command.orderId(),
                command.productId(),
                reason
        );

        eventPublisher.publishInventoryFailedEvent(event);
    }

    /**
     * Handle Release Inventory Command (Compensation)
     */
    @Transactional
    public void handleReleaseInventory(ReleaseInventoryCommand command) {
        try {
            log.info("Releasing inventory for saga: {}, order: {}",
                    command.sagaId(), command.orderId());

            Reservation reservation = reservationRepository.findByOrderId(command.orderId())
                    .orElseThrow(() -> new RuntimeException("Reservation not found for order: " + command.orderId()));

            // Only release if it's currently reserved
            if (reservation.getStatus() != ReservationStatus.RESERVED) {
                log.warn("Reservation not in RESERVED status: {}", reservation.getStatus());
                return;
            }

            // Get inventory
            Inventory inventory = inventoryRepository.findById(reservation.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + reservation.getProductId()));

            // Release inventory
            inventory.setAvailableQuantity(inventory.getAvailableQuantity() + reservation.getQuantity());
            inventory.setReservedQuantity(inventory.getReservedQuantity() - reservation.getQuantity());
            inventoryRepository.save(inventory);

            // Update reservation
            reservation.setStatus(ReservationStatus.RELEASED);
            reservation.setReleaseReason(command.reason());
            reservation.setReleasedAt(LocalDateTime.now());
            reservationRepository.save(reservation);

            log.info("Inventory released successfully: {} units of product {}",
                    reservation.getQuantity(), reservation.getProductId());

            // Publish event
            InventoryReleasedEvent event = new InventoryReleasedEvent(
                    UUID.randomUUID().toString(),
                    command.sagaId(),
                    command.orderId(),
                    reservation.getProductId(),
                    reservation.getQuantity()
            );

            eventPublisher.publishInventoryReleasedEvent(event);

        } catch (Exception e) {
            log.error("Failed to release inventory for order: {}", command.orderId(), e);
        }
    }

}
