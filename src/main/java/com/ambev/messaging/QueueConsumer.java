package com.ambev.messaging;

import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.ambev.dto.OrderDTO;
import com.ambev.entity.Order;
import com.ambev.service.OrderProductService;
import com.ambev.service.OrderService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.validation.ConstraintViolationException;

@Component
public class QueueConsumer {
	
	@Autowired
	OrderService service;
	
	@Autowired
	OrderProductService prodService;
	
	@Autowired
	QueueSender sender;
	
	Logger logger = LoggerFactory.getLogger(QueueConsumer.class);

    @RabbitListener(queues = {"${queue.name.consumer}"})
    public void receive(@Payload String fileBody) {
    	ObjectMapper mapper = new ObjectMapper();
    	try {
			OrderDTO dto = mapper.readValue(fileBody, OrderDTO.class);
			
			Order order = service.save(getOrder(dto));
			dto.getProducts().forEach(e -> {
				e.setOrder(order);
				prodService.save(e);
			});
			Order ord = service.save(service.sumOrder(dto.getProducts()));
			ord.setProducts(dto.getProducts());
			sender.send(mapper.writeValueAsString(ord));
		} catch (JsonProcessingException e) {
			logger.error(e.getMessage());
		} catch (ConstraintViolationException e) {
			 String resultado = e.getConstraintViolations().stream().toList().stream()
			            .map(msg -> "O campo " + msg.getPropertyPath() + " "+ msg.getMessage()) 
			            .collect(Collectors.joining("\n")); 
			logger.error(resultado);
		}
    }
    
    private Order getOrder(OrderDTO dto) {
		Order order = new Order();
		order.setDate(dto.getDate());
		order.setCostumerName(dto.getCostumerName());
		order.setCostumerCrn(dto.getCostumerCRN());
		order.setStatus(dto.getStatus());
		return order;
	}

}
