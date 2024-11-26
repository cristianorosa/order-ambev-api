package com.ambev.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import com.ambev.entity.Order;
import com.ambev.entity.OrderProduct;
import com.ambev.enums.StatusEnum;
import com.ambev.exception.BusinessException;
import com.ambev.repository.OrderProductRepository;
import com.ambev.repository.OrderRepository;

@SpringBootTest
@ActiveProfiles("test")
class OrderServiceTest {
	
	static final Long ID = 1L;
	static final Date DATE = new Date();
	static final String COSTUMER_NAME = "Joker Cervejas Artesanais - Chopp";
	static final String COSTUMER_CRN = "191000000000";
	static final StatusEnum STATUS = StatusEnum.AWAITING_PAYMENT;
	
	@MockBean
	OrderRepository repo;
	
	@MockBean
	OrderProductRepository productRepo;
	
	@Autowired
	OrderService service;
	
	@Test
	void save() throws BusinessException {		
		BDDMockito.given(repo.save(Mockito.any(Order.class))).willReturn(getOrder());
		Order order = service.save(new Order());
		order.setProducts(getProducts(getOrder()));
		
		assertNotNull(order);
		assertEquals(getProducts(getOrder()).getFirst().getId(), order.getProducts().getFirst().getId());
		assertEquals(COSTUMER_NAME, order.getCostumerName());
		assertEquals(COSTUMER_CRN, order.getCostumerCrn());
		assertEquals(DATE, order.getDate());
		assertEquals(STATUS, order.getStatus());
	}
	
	@Test
	void findByOrderId() {
		BDDMockito.given(repo.findById(Mockito.anyLong())).willReturn(Optional.of(getOrder()));
		
		Optional<Order> response = service.findByOrderId(ID);
				
		assertTrue(response.isPresent());
		assertEquals(ID, response.get().getId());
	}
	
	@Test
	void sumOrder() {
		Order response = service.sumOrder(getOrderProducts());
				
		assertNotNull(response);
		assertNotNull(response.getTotalOrder());
	}
	
	private List<OrderProduct> getOrderProducts() {
		return getProducts(getOrder());
	}
	
	private Order getOrder() {
		Order w = new Order();
		w.setId(ID);
		w.setDate(DATE);
		w.setCostumerName(COSTUMER_NAME);
		w.setCostumerCrn(COSTUMER_CRN);
		w.setStatus(STATUS);
		return w;
	}
	
	private List<OrderProduct> getProducts(Order order) {
		List<OrderProduct> products = new ArrayList<>();
		products.add(new OrderProduct(1L, "Cerveja Original Pilsen 600ml Garrafa", 12, BigDecimal.valueOf(8), order));
		products.add(new OrderProduct(2L, "Cerveja Heineken Puro Malte Pilsen 600ml Garrafa", 24, BigDecimal.valueOf(6.5), order));
		products.add(new OrderProduct(3L, "Cervegela Brahma Duplo Malte 600ml Garrafa", 48, BigDecimal.valueOf(9), order));
		return products;
	}
}
