package com.ambev.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ambev.entity.Order;
import com.ambev.entity.OrderProduct;
import com.ambev.repository.OrderRepository;
import com.ambev.service.OrderService;

@Service
public class OrderServiceImpl implements OrderService {
	
	@Autowired
	OrderRepository repo;

	@Override
	public Order save(Order order) {
		return repo.save(order);
	}

	@Override
	public Optional<Order> findByOrderId(Long id) {
		return repo.findById(id);
	}

	@Override
	public Order sumOrder(List<OrderProduct> list) {			
		BigDecimal total = list.stream()
                .map(OrderProduct::getValue)  
                .reduce(BigDecimal.ZERO, BigDecimal::add); 
		
		Order order = list.getFirst().getOrder();
		order.setTotalOrder(total);
		return order;
	}

	@Override
	public List<Order> findBetweenDates(Date startDate, Date endDate) {
		return repo.findAllByDateGreaterThanEqualAndDateLessThanEqual(startDate, endDate);
	}

	@Override
	public void deleteById(Long id) {
		Optional<Order> ord = repo.findById(id);
		if (ord.isPresent()) {
			repo.delete(ord.get());
		}
	}

}
