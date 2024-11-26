package com.ambev.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Date;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.ambev.entity.Order;
import com.ambev.enums.StatusEnum;

@SpringBootTest
@ActiveProfiles("test")
class OrderRepositoryTest {
	
	private static final Date DATE = new Date();
	static final String COSTUMER_NAME = "Joker Cervejas Artesanais - Chopp";
	static final String COSTUMER_CRN = "191000000000";
	static final StatusEnum STATUS = StatusEnum.AWAITING_PAYMENT;
	
	private Order order;

	@Autowired
	OrderRepository repo;
	
	@BeforeEach
	public void setUp() {
		order = repo.save(newOrder());
	}
	
	@AfterEach
    void tearDown() {
		repo.deleteAll();
    }
	
	@Test
	void saveOrder() {
		Order response = repo.save(newOrder());		
		
		assertNotNull(response);
		assertEquals(COSTUMER_NAME, order.getCostumerName());
		assertEquals(COSTUMER_CRN, order.getCostumerCrn());
		assertEquals(DATE, order.getDate());
		assertEquals(STATUS, order.getStatus());

	}
	
	@Test
	void findByOrderId() {
		Optional<Order> response = repo.findById(order.getId());	
		assertNotNull(response.get());
	}
	
	private Order newOrder() {
		Order w = new Order();
		w.setDate(DATE);
		w.setCostumerName(COSTUMER_NAME);
		w.setCostumerCrn(COSTUMER_CRN);
		w.setStatus(STATUS);
		return w;
	}
}
