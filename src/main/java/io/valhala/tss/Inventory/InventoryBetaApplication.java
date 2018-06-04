package io.valhala.tss.Inventory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("io.valhala")
public class InventoryBetaApplication {

	public static void main(String[] args) {
		SpringApplication.run(InventoryBetaApplication.class, args);
	}
}
