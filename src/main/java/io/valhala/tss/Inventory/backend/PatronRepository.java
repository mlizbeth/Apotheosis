package io.valhala.tss.Inventory.backend;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PatronRepository extends JpaRepository<Patron, Long> {

	Patron findByid(Long id);
	
}
