package az.tarkhangurbanli.payment.repository;

import az.tarkhangurbanli.payment.model.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, String> {

    Optional<Payment> findByOrderId(String orderId);

    Optional<Payment> findBySagaId(String sagaId);

}
