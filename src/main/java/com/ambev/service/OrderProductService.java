package com.ambev.service;

import java.util.List;

import com.ambev.entity.OrderProduct;

public interface OrderProductService {
	
	OrderProduct save(OrderProduct order);
	List<OrderProduct> findByOrderId(Long id);
	void deleteAllByIdOrder(Long id);

}
