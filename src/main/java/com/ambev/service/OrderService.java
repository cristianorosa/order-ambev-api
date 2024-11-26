package com.ambev.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.ambev.entity.Order;
import com.ambev.entity.OrderProduct;

public interface OrderService {

	Order save(Order order);
	Order sumOrder(List<OrderProduct> list);
	Optional<Order> findByOrderId(Long order);
	List<Order> findBetweenDates(Date startDate, Date endDate);
	void deleteById(Long order);
	
}