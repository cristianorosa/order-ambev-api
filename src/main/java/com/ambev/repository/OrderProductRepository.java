package com.ambev.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ambev.entity.Order;
import com.ambev.entity.OrderProduct;

@Repository
public interface OrderProductRepository extends JpaRepository<OrderProduct, Long> {

	List<OrderProduct> findByOrderId(long orderID);
	List<OrderProduct> findByOrder(Order ord);
	
}