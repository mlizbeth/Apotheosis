package io.valhala.tss.Inventory.backend;

import java.awt.List;
import java.util.ArrayList;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface ItemRepository extends JpaRepository<InventoryItem, Long> {
	ArrayList<InventoryItem> findAllByNameContainsIgnoreCase(String i_name);

}
