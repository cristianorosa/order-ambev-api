package com.ambev.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.ambev.enums.StatusEnum;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "TB_ORDER")
@Getter
@Setter
public class Order implements Serializable {

	private static final long serialVersionUID = -5920493416299280592L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	private Date date;

	@NotNull
	@Enumerated(EnumType.STRING)
	private StatusEnum status;

	@NotNull
	private String costumerName;

	@NotNull
	private String costumerCrn;

	private BigDecimal totalOrder;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "TB_ORDER_PRODUCT", joinColumns = {
			@JoinColumn(name = "order_id") }, inverseJoinColumns = @JoinColumn(name = "id"))
	private List<OrderProduct> products;
}
