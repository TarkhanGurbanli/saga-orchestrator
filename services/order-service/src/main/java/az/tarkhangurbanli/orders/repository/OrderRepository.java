package az.tarkhangurbanli.orders.repository;

import az.tarkhangurbanli.orders.model.entity.Order;
import az.tarkhangurbanli.orders.model.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, String> {

    List<Order> findByCustomerId(String customerId);

    Optional<Order> findBySagaId(String sagaId);

    List<Order> findByStatus(OrderStatus status);
}
