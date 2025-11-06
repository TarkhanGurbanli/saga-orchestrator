package az.tarkhangurbanli.inventory.initializer;

import az.tarkhangurbanli.inventory.model.entity.Inventory;
import az.tarkhangurbanli.inventory.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    @Bean
    public CommandLineRunner initInventoryData(InventoryRepository inventoryRepository) {
        return args -> {
            if (inventoryRepository.count() == 0) {
                log.info("Initializing inventory data...");

                List<Inventory> products = List.of(
                        new Inventory(
                                "product-001",
                                "MacBook Pro 16\"",
                                50,
                                0
                        ),
                        new Inventory(
                                "product-002",
                                "iPhone 15 Pro",
                                100,
                                0
                        ),
                        new Inventory(
                                "product-003",
                                "AirPods Pro",
                                200,
                                0
                        ),
                        new Inventory(
                                "product-004",
                                "iPad Pro 12.9\"",
                                75,
                                0
                        ),
                        new Inventory(
                                "product-005",
                                "Apple Watch Ultra",
                                150,
                                0
                        )
                );

                inventoryRepository.saveAll(products);
                log.info("Initialized {} products in inventory", products.size());
            } else {
                log.info("Inventory data already exists");
            }
        };
    }

}