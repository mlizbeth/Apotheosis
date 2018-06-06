package io.valhala.tss.Inventory.backend;

import java.awt.List;
import java.util.ArrayList;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<InventoryItem, Long> {
	InventoryItem findByItemCode(long itemCode);
	InventoryItem findByItemName(String itemName);
	InventoryItem findByItemCodeContains(long itemCode);
	//ArrayList<InventoryItem> findAllByItemNameStartsWithIgnoreCase(String itemName);
	ArrayList<InventoryItem> findAllByItemNameContainsIgnoreCase(String itemName);
	ArrayList<InventoryItem> findAllByItemCodeContains(Long itemCode);
}
