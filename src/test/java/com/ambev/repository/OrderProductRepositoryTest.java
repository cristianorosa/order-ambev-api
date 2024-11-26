package com.ambev.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.ambev.entity.Order;
import com.ambev.entity.OrderProduct;
import com.ambev.enums.StatusEnum;

@SpringBootTest
@ActiveProfiles("test")
class OrderProductRepositoryTest {
	
	private static final Long ID = 1L;
	private static final Date DATE = new Date();
	
	private static final String PRODUCT_NAME_1 = "Cerveja Original Pilsen 600ml Garrafa";
	private static final Integer PRODUCT_QTD_1 = 12;
	private static final BigDecimal PRODUCT_VALUE_1 = BigDecimal.valueOf(8);

	@Autowired
	OrderRepository orderRepo;
	
	@Autowired
	OrderProductRepository repo;
	
	@AfterEach
    void tearDown() {
		repo.deleteAll();
		orderRepo.deleteAll();
    }
	
	@Test
	void saveOrderProduct() {
		Order o = orderRepo.save(getOrder());		
		List<OrderProduct> response = repo.saveAll(getProducts(o));
		
		assertNotNull(response);
		assertEquals(PRODUCT_NAME_1, response.getFirst().getName());
		assertEquals(PRODUCT_QTD_1, response.getFirst().getQuantity());
		assertEquals(PRODUCT_VALUE_1, response.getFirst().getValue());
		assertEquals(o.getId(), response.getFirst().getOrder().getId());
	}
	
	private Order getOrder() {
		Order w = new Order();
		w.setId(ID);
		w.setDate(DATE);
		w.setCostumerName("Joker Cervejas Artesanais - Chopp");
		w.setCostumerCrn("191000000000");
		w.setStatus(StatusEnum.AWAITING_PAYMENT);
		return w;
	}

	private List<OrderProduct> getProducts(Order order) {
		List<OrderProduct> products = new ArrayList<>();
		products.add(new OrderProduct(ID, PRODUCT_NAME_1, PRODUCT_QTD_1, PRODUCT_VALUE_1, order));
		products.add(new OrderProduct(2L, "Cerveja Heineken Puro Malte Pilsen 600ml Garrafa", 24, BigDecimal.valueOf(6.5), order));
		products.add(new OrderProduct(3L, "Cervegela Brahma Duplo Malte 600ml Garrafa", 48, BigDecimal.valueOf(9), order));
		return products;
	}
}
