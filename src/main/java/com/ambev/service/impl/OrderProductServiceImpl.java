package com.ambev.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ambev.entity.OrderProduct;
import com.ambev.repository.OrderProductRepository;
import com.ambev.service.OrderProductService;

@Service
public class OrderProductServiceImpl implements OrderProductService {

	@Autowired
	OrderProductRepository repo;

	@Override
	public OrderProduct save(OrderProduct orderProduct) {
		return repo.save(orderProduct);
	}

	@Override
	public List<OrderProduct> findByOrderId(Long id) {
		return repo.findByOrderId(id);
	}

	@Override
	public void deleteAllByIdOrder(Long id) {
		List<OrderProduct> orders = findByOrderId(id);
		orders.forEach(e -> repo.delete(e));
	}
}
