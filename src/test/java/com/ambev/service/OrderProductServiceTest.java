package com.ambev.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import com.ambev.repository.OrderProductRepository;
import com.ambev.repository.OrderRepository;

@SpringBootTest
@ActiveProfiles("test")
class OrderProductServiceTest {

	private static final Long ID = 1L;
	private static final Date DATE = new Date();
	static final String COSTUMER_NAME = "Joker Cervejas Artesanais - Chopp";
	static final String COSTUMER_CRN = "191000000000";
	static final StatusEnum STATUS = StatusEnum.AWAITING_PAYMENT;

	private static final String PRODUCT_NAME_1 = "Cerveja Original Pilsen 600ml Garrafa";
	private static final Integer PRODUCT_QTD_1 = 12;
	private static final BigDecimal PRODUCT_VALUE_1 = BigDecimal.valueOf(8);

	@MockBean
	OrderProductRepository repo;

	@Autowired
	OrderRepository orderRepo;

	@Autowired
	OrderProductService service;

	@Test
	void saveOrderProduct() {
		Order order = orderRepo.save(getOrder());
		BDDMockito.given(repo.save(Mockito.any(OrderProduct.class))).willReturn(getProduct(order));
		OrderProduct orderProducts = service.save(new OrderProduct());

		assertNotNull(orderProducts);
		assertEquals(COSTUMER_NAME, order.getCostumerName());
		assertEquals(COSTUMER_CRN, order.getCostumerCrn());
		assertEquals(DATE, order.getDate());
		assertEquals(STATUS, order.getStatus());
	}

	@Test
	void findByOrderId() {
		Order order = orderRepo.save(getOrder());
		BDDMockito.given(repo.findByOrderId(Mockito.anyLong())).willReturn(getProducts(order));

		List<OrderProduct> response = service.findByOrderId(order.getId());

		assertNotNull(response);
		assertEquals(order.getId(), response.get(0).getOrder().getId());
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

	private OrderProduct getProduct(Order order) {
		return new OrderProduct(ID, PRODUCT_NAME_1, PRODUCT_QTD_1, PRODUCT_VALUE_1, order);
	}

	private List<OrderProduct> getProducts(Order order) {
		List<OrderProduct> res = new ArrayList<>();
		res.add(new OrderProduct(ID, PRODUCT_NAME_1, PRODUCT_QTD_1, PRODUCT_VALUE_1, order));
		return res;
	}
}
