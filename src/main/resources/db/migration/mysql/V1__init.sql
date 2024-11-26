CREATE TABLE ambev.tb_order (
	id INTEGER auto_increment NOT NULL,
	`date` DATETIME NOT NULL,
	status varchar(50) NOT NULL,
	costumer_name varchar(250) NOT NULL,
	costumer_crn varchar(18) NOT NULL,
	total_order NUMERIC(10, 2),
	CONSTRAINT Order_PK PRIMARY KEY (id)
);

CREATE TABLE ambev.tb_order_product (
	id INTEGER auto_increment NOT NULL,
	`name` varchar(250) NOT NULL,
	quantity INTEGER,
	product_value NUMERIC(10, 2),
	order_id INTEGER,
	CONSTRAINT order_product_PK PRIMARY KEY (id),
	foreign key(order_id) references ambev.tb_order(id)
);
