CREATE TABLE
    products (
        id UUID NOT NULL,
        NAME VARCHAR(255) NOT NULL,
        stock INTEGER NOT NULL,
        VERSION BIGINT NOT NULL
);


CREATE TABLE
    reservations (
        id UUID NOT NULL,
        product_id UUID NOT NULL,
        quantity INTEGER NOT NULL,
        status VARCHAR(20) NOT NULL,
        created_at TIMESTAMP WITH TIME ZONE NOT NULL,
        expires_at TIMESTAMP WITH TIME ZONE NOT NULL
);


ALTER TABLE products
    ADD CONSTRAINT pk_products PRIMARY KEY (id);


ALTER TABLE reservations
    ADD CONSTRAINT pk_reservations PRIMARY KEY (id);


ALTER TABLE products
    ADD CONSTRAINT uc_products_name UNIQUE (NAME);


ALTER TABLE reservations
    ADD CONSTRAINT fk_reservations_product_id FOREIGN KEY (product_id) REFERENCES products (id);


ALTER TABLE products
    ADD CONSTRAINT chk_products_stock CHECK (stock >= 0);


ALTER TABLE reservations
    ADD CONSTRAINT chk_reservations_quantity CHECK (quantity > 0);