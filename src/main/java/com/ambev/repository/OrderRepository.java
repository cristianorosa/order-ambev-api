package com.ambev.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ambev.entity.Order;


@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

	List<Order> findAllByDateGreaterThanEqualAndDateLessThanEqual(Date startDate, Date endDate);

	List<Order> findAllByCostumerCrnAndDateGreaterThanEqualAndDateLessThanEqual(String costumerCrn, Date date, Date date2);
		
}
