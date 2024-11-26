package com.ambev.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.ambev.dto.OrderDTO;
import com.ambev.entity.Order;
import com.ambev.entity.OrderProduct;
import com.ambev.enums.StatusEnum;
import com.ambev.service.OrderProductService;
import com.ambev.service.OrderService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@PropertySource("classpath:messages.properties")
class OrderControllerTest {
	
	@Value("${order.not.found}")
	private String msgOrderNotFound;
	@Value("${validation.invalid.products}")
	private String msgOrderInvalidProducts;
	@Value("${order.erase}")
	private String msgOrderErase;
	
	
	
	private static final Long ID = 1L;
	private static final Date DATE = new Date();
	static final String COSTUMER_NAME = "Joker Cervejas Artesanais - Chopp";
	static final String COSTUMER_CRN = "191000000000";
	static final StatusEnum STATUS = StatusEnum.AWAITING_PAYMENT;
	private static final LocalDate TODAY = LocalDate.now();
	
	private static final String URL = "/order";
	
	private static final String PRODUCT_NAME_1 = "Cerveja Original Pilsen 600ml Garrafa";
	private static final Integer PRODUCT_QTD_1 = 12;
	private static final BigDecimal PRODUCT_VALUE_1 = BigDecimal.valueOf(8);
	
	@MockBean
	OrderService service;
	
	@MockBean
	OrderProductService prodService;

	@Autowired
	MockMvc mvc;
	
	@Test
	void saveOrder() throws Exception {				
		BDDMockito.given(service.save(Mockito.any(Order.class))).willReturn(getOrderProducts());
		BDDMockito.given(prodService.save(Mockito.any(OrderProduct.class))).willReturn(getProduct());
		
		mvc.perform(MockMvcRequestBuilders.post(URL)
				.content(getJsonPayLoad())
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.data.id").value(ID))
			.andExpect(jsonPath("$.data.costumerName").value(COSTUMER_NAME))
			.andExpect(jsonPath("$.data.costumerCRN").value(COSTUMER_CRN))
			.andExpect(jsonPath("$.data.status").value(STATUS.getValue()));
	}
	
	@Test
	void getOrderById() throws Exception {
		BDDMockito.given(service.findByOrderId(Mockito.anyLong())).willReturn(Optional.of(getOrder()));
		BDDMockito.given(service.sumOrder(Mockito.anyList())).willReturn(getOrderProducts());
		BDDMockito.given(prodService.findByOrderId(Mockito.anyLong())).willReturn(getProducts());
		BDDMockito.given(service.save(Mockito.any(Order.class))).willReturn(getOrderProducts());
		
		mvc.perform(MockMvcRequestBuilders.get(URL + "?order=" + ID)				
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.totalOrder").value(getOrderProducts().getTotalOrder()));
	}
	
	@Test
	void getOrderByIdNotExists() throws Exception {
		BDDMockito.given(service.findByOrderId(Mockito.anyLong())).willReturn(Optional.empty());
		BDDMockito.given(service.sumOrder(Mockito.anyList())).willReturn(getOrderProducts());
		BDDMockito.given(prodService.findByOrderId(Mockito.anyLong())).willReturn(getProducts());
		BDDMockito.given(service.save(Mockito.any(Order.class))).willReturn(getOrderProducts());
		
		mvc.perform(MockMvcRequestBuilders.get(URL + "?order=0")				
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.errors").value(String.format(msgOrderNotFound, 0)));
	}
	
	private Order getOrderProducts() {
		Order order = new Order();
		order.setId(ID);
		order.setDate(DATE);
		order.setCostumerName(COSTUMER_NAME);
		order.setCostumerCrn(COSTUMER_CRN);
		order.setStatus(STATUS);
		
		List<OrderProduct> op = getProducts(); 
		order.setProducts(op);
		BigDecimal total = op.stream()
                .map(OrderProduct::getValue)  
                .reduce(BigDecimal.ZERO, BigDecimal::add);
		order.setTotalOrder(total);
		return order;
	}

	@Test
	void saveOrderNoProduct() throws Exception {				
		BDDMockito.given(service.save(Mockito.any(Order.class))).willReturn(getOrder());
		
		mvc.perform(MockMvcRequestBuilders.post(URL)
				.content(getJsonPayLoadNoProduct())
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.errors").value(msgOrderInvalidProducts));
			
	}
	
	@Test
	void updateInvalid() throws Exception {
		BDDMockito.given(service.findByOrderId(Mockito.anyLong())).willReturn(Optional.empty());

		mvc.perform(MockMvcRequestBuilders.put(URL)
				.content(getJsonPayLoad(0L, DATE, COSTUMER_NAME, COSTUMER_CRN, STATUS))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.data").doesNotExist())
			.andExpect(jsonPath("$.errors[0]").value(String.format(msgOrderNotFound, 0L)));
	}
	
	String getJsonPayLoad(Long id, Date date, String costumerName, String costumerCRN, StatusEnum status)
			throws JsonProcessingException {
		
		OrderDTO w = new OrderDTO();
		w.setId(id);
		w.setDate(date);
		w.setCostumerName(costumerName);
		w.setCostumerCRN(costumerCRN);
		w.setStatus(status);
		
		List<OrderProduct> op = new ArrayList<>(); 
		op.add(new OrderProduct(ID, PRODUCT_NAME_1, PRODUCT_QTD_1, PRODUCT_VALUE_1, getOrderProducts()));
		w.setProducts(op);
		
		ObjectMapper om = new ObjectMapper();
		return om.writeValueAsString(w);
	}
	
	@Test
	void update() throws Exception {
		BDDMockito.given(service.findByOrderId(Mockito.anyLong())).willReturn(Optional.of(getOrder()));
		BDDMockito.given(service.save(Mockito.any(Order.class)))
				.willReturn(getOrder());

		mvc.perform(MockMvcRequestBuilders.put(URL)
				.content(getJsonPayLoad(0L, DATE, COSTUMER_NAME, COSTUMER_CRN, STATUS))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.id").value(ID))
			.andExpect(jsonPath("$.data.date").value(TODAY.format(dateFormatter())))
			.andExpect(jsonPath("$.data.costumerName").value(COSTUMER_NAME))
			.andExpect(jsonPath("$.data.costumerCRN").value(COSTUMER_CRN))
			.andExpect(jsonPath("$.data.status").value(STATUS.getValue()));
	}
	
	DateTimeFormatter dateFormatter() {
		return DateTimeFormatter.ofPattern("dd-MM-yyyy");
	}
	
	
	@Test
	void findBetweenDates() throws Exception {		
		Order ord = getOrder();
		ord.setProducts(getProducts());
		
		String startDate = TODAY.format(dateFormatter());
		String endDate = (TODAY.plusDays(5)).format(dateFormatter());

		BDDMockito.given(service.findBetweenDates(Mockito.any(Date.class), Mockito.any(Date.class))).willReturn(List.of(ord));
				mvc.perform(MockMvcRequestBuilders.get(URL + "/date?startDate=" + startDate + "&endDate=" + endDate)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.[0].id").value(ID))
			.andExpect(jsonPath("$.data.[0].date").value(TODAY.format(dateFormatter())))
			.andExpect(jsonPath("$.data.[0].costumerName").value(COSTUMER_NAME))
			.andExpect(jsonPath("$.data.[0].costumerCRN").value(COSTUMER_CRN))
			.andExpect(jsonPath("$.data.[0].status").value(STATUS.getValue()));

	}
	
	@Test
	void delete() throws Exception {
		BDDMockito.given(service.findByOrderId(Mockito.anyLong())).willReturn(Optional.of(getOrder()));

		mvc.perform(MockMvcRequestBuilders.delete(URL + "/1")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data").value(String.format(msgOrderErase, 1)));
	}
	
	@Test
	void deleteInvalid() throws Exception {
		BDDMockito.given(service.findByOrderId(Mockito.anyLong())).willReturn(Optional.empty());

		mvc.perform(MockMvcRequestBuilders.delete(URL + "/0")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.data").doesNotExist())
			.andExpect(jsonPath("$.errors[0]").value(String.format(msgOrderNotFound, 0L)));
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
	
	private OrderProduct getProduct() {
		return new OrderProduct(ID, PRODUCT_NAME_1, PRODUCT_QTD_1, PRODUCT_VALUE_1, getOrderProducts());
	}
	
	private List<OrderProduct> getProducts() {
		return List.of(
				new OrderProduct(ID, PRODUCT_NAME_1, PRODUCT_QTD_1, PRODUCT_VALUE_1, getOrder()),
				new OrderProduct(2L, "Cerveja Heineken Puro Malte Pilsen 600ml Garrafa", 24, BigDecimal.valueOf(6.5), getOrder()),
				new OrderProduct(3L, "Cervegela Brahma Duplo Malte 600ml Garrafa", 48, BigDecimal.valueOf(9), getOrder())
		);
	}
	
	String getJsonPayLoadNoProduct() throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		OrderDTO order = new OrderDTO();
		order.setId(ID);
		order.setDate(DATE);
		order.setCostumerName(COSTUMER_NAME);
		order.setCostumerCRN(COSTUMER_CRN);
		order.setStatus(STATUS);
		
		return mapper.writeValueAsString(order);
	}
	
	String getJsonPayLoad() throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		OrderDTO order = new OrderDTO();
		order.setId(ID);
		order.setDate(DATE);
		order.setCostumerName(COSTUMER_NAME);
		order.setCostumerCRN(COSTUMER_CRN);
		order.setStatus(STATUS);
		
		List<OrderProduct> op = new ArrayList<>(); 
		op.add(new OrderProduct(ID, PRODUCT_NAME_1, PRODUCT_QTD_1, PRODUCT_VALUE_1, getOrderProducts()));
		order.setProducts(op);
		return mapper.writeValueAsString(order);
	}
}
