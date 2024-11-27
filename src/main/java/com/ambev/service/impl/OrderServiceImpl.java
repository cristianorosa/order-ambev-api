package com.ambev.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import com.ambev.entity.Order;
import com.ambev.entity.OrderProduct;
import com.ambev.exception.BusinessException;
import com.ambev.repository.OrderRepository;
import com.ambev.service.OrderService;

@Service
@PropertySource("classpath:messages.properties")
public class OrderServiceImpl implements OrderService {
	
	@Value("${validation.invalid.order.exists}")
	private String msgOrderExits;
	
	@Autowired
	OrderRepository repo;

	@Override
	public Order save(Order order) throws BusinessException {
		Optional<Order> exist = repo.findById(order.getId() != null ? order.getId() : 0);
		
		if (exist.isEmpty()) {
			List<Order> ord = repo.findAllByCostumerCrnAndDateGreaterThanEqualAndDateLessThanEqual(order.getCostumerCrn(), order.getDate(), order.getDate());
			
			if (!ord.isEmpty()) {
				throw new BusinessException(msgOrderExits);
			}
		}
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
