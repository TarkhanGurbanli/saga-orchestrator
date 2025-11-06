package az.tarkhangurbanli.inventory.repository;

import az.tarkhangurbanli.inventory.model.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, String> {

    Optional<Reservation> findByOrderId(String orderId);

    Optional<Reservation> findBySagaId(String sagaId);
}
