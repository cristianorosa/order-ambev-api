package com.ambev.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "TB_ORDER_PRODUCT")
@Getter
@Setter
@JsonIgnoreProperties({ "order" })
public class OrderProduct implements Serializable {

	private static final long serialVersionUID = -5920493416299280592L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	private String name;

	@NotNull
	private Integer quantity;

	@Column(name = "product_value")
	private BigDecimal value;

	@JoinColumn(name = "order_id", referencedColumnName = "id")
	@ManyToOne(fetch = FetchType.LAZY)
	@NotNull
	private Order order = new Order();

	public OrderProduct(Long id, String name, Integer quantity, BigDecimal value, Order order) {
		this.setId(id);
		this.setName(name);
		this.setQuantity(quantity);
		this.setValue(value);
		this.setOrder(order);
	}
	
	public BigDecimal getValue() {
		return this.value;
	}
	
	public OrderProduct() {
		super();
	}

}
