package com.ambev.dto;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.context.annotation.PropertySource;

import com.ambev.entity.OrderProduct;
import com.ambev.enums.StatusEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@PropertySource("classpath:messages.properties")
public class OrderDTO {
	
	private Long id;
	
	@NotNull(message = "{validation.invalid.date}")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy", locale = "pt-BR", timezone = "Bazil/East")
	private Date date;
	
	private StatusEnum status;
	
	@NotNull(message = "{validation.invalid.costumer.name}")
	private String costumerName;
	
	@NotNull(message = "{validation.invalid.costumer.crn}")
	private String costumerCRN;
	
	@NotEmpty(message = "{validation.invalid.products}")
	private List<OrderProduct> products;
	private BigDecimal totalOrder;

}
