package com.ambev.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ambev.dto.OrderDTO;
import com.ambev.entity.Order;
import com.ambev.entity.OrderProduct;
import com.ambev.service.OrderProductService;
import com.ambev.service.OrderService;
import com.ambev.util.Response;

import jakarta.validation.Valid;

@RestController
@RequestMapping("order")
@PropertySource("classpath:messages.properties")
public class OrderController {

	@Value("${order.not.found}")
	private String msgOrderNotFound;
	
	@Value("${order.none.found}")
	private String msgNoneOrdersFound;
	
	@Value("${order.erase}")
	private String msgOrderErase;

	@Autowired
	OrderService service;

	@Autowired
	OrderProductService prodService;

	@GetMapping
	public ResponseEntity<Response<OrderDTO>> findOrder(Long order) {
		Response<OrderDTO> response = new Response<>();
		Optional<Order> ord = service.findByOrderId(order);

		if (ord.isEmpty()) {
			response.getErrors().add(String.format(msgOrderNotFound, order));
			return ResponseEntity.badRequest().body(response);
		}

		List<OrderProduct> products = prodService.findByOrderId(order);
		products.forEach(product -> product.setOrder(ord.get()));
		Order ordProducts = service.save(service.sumOrder(products));

		response.setData(getOrderDto(ordProducts));
		return ResponseEntity.ok().body(response);
	}
	
	@GetMapping(value = "/date")
	public ResponseEntity<Response<List<OrderDTO>>> findBetweenDates(
			@DateTimeFormat(pattern = "dd-MM-yyyy") Date startDate,
			@DateTimeFormat(pattern = "dd-MM-yyyy") Date endDate) {
		
		Response<List<OrderDTO>> response = new Response<>();
		
		List<Order> ord = service.findBetweenDates(startDate, endDate);
		if (ord.isEmpty()) {
			response.getErrors().add(msgNoneOrdersFound);
	        return ResponseEntity.badRequest().body(response);
		}
		
		List<OrderDTO> dto = new ArrayList<>();
		ord.forEach(e -> dto.add(getOrderDto(e))); 
				
		response.setData(dto);
		return ResponseEntity.ok().body(response);
	}

	@PostMapping
	public ResponseEntity<Response<OrderDTO>> create(@Valid @RequestBody OrderDTO dto, BindingResult result) {

		Response<OrderDTO> response = new Response<>();

		if (result.hasErrors()) {
			result.getAllErrors().forEach(r -> response.getErrors().add(r.getDefaultMessage()));
			return ResponseEntity.badRequest().body(response);
		}

		Order order = service.save(getOrder(dto));
		List<OrderProduct> products = new ArrayList<>();

		dto.getProducts().forEach(e -> {
			e.setOrder(order);
			products.add(prodService.save(e));
		});
		dto.setProducts(products);
		dto.setId(order.getId());

		response.setData(dto);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}
	
	@PutMapping
	public ResponseEntity<Response<OrderDTO>> update(@Valid @RequestBody OrderDTO dto, BindingResult result) {
		
		Response<OrderDTO> response = new Response<>();
		Optional<Order> order = service.findByOrderId(dto.getId());
		
		if (!order.isPresent()) {
			result.addError(new ObjectError("Order", String.format(msgOrderNotFound, dto.getId())));
		} 
		
		if (result.hasErrors()) {			
			result.getAllErrors().forEach(e -> response.getErrors().add(e.getDefaultMessage()));
			return ResponseEntity.badRequest().body(response);
		}
		
		Order saved = service.save(getOrder(dto));
		prodService.deleteAllByIdOrder(dto.getId());
		dto.getProducts().forEach(e -> {
			e.setOrder(saved);
			prodService.save(e);
		});
		saved.setProducts(dto.getProducts());
		response.setData(getOrderDto(saved));
		
		return ResponseEntity.ok().body(response);
	}
	
	@DeleteMapping(value = "/{order}")
	public ResponseEntity<Response<String>> delete(@PathVariable("order") Long order) {
		Response<String> response = new Response<>();
		
		Optional<Order> walletItem = service.findByOrderId(order);
		
		if (!walletItem.isPresent()) {
			response.getErrors().add(String.format(msgOrderNotFound, order));
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
		}
		
		service.deleteById(order);
		response.setData(String.format(msgOrderErase, order));
		return ResponseEntity.ok().body(response);
	}

	private Order getOrder(OrderDTO dto) {
		Order order = new Order();
		order.setId(dto.getId());
		order.setDate(dto.getDate());
		order.setCostumerName(dto.getCostumerName());
		order.setCostumerCrn(dto.getCostumerCRN());
		order.setStatus(dto.getStatus());
		order.setTotalOrder(dto.getTotalOrder());
		return order;
	}

	private OrderDTO getOrderDto(Order ord) {
		OrderDTO order = new OrderDTO();
		if (ord != null) {
			order.setId(ord.getId());
			order.setDate(ord.getDate());
			order.setCostumerName(ord.getCostumerName());
			order.setCostumerCRN(ord.getCostumerCrn());
			order.setStatus(ord.getStatus());
			order.setProducts(ord.getProducts());
			order.setTotalOrder(ord.getTotalOrder());
		}
		return order;
	}

}
