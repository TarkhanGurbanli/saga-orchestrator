package az.tarkhangurbanli.inventory.repository;

import az.tarkhangurbanli.inventory.model.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryRepository extends JpaRepository<Inventory, String> {
}
