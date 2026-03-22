DELETE FROM reservations;
DELETE FROM products;

INSERT INTO products (id, name, stock, version) VALUES
    (gen_random_uuid(), 'Product1', 10, 0),
    (gen_random_uuid(), 'Product2', 5, 0),
    (gen_random_uuid(), 'Product3', 50, 0),
    (gen_random_uuid(), 'Product4', 0, 0);

INSERT INTO products (id, name, stock, version) VALUES
    ('11111111-1111-1111-1111-111111111111', 'ProductTest', 20, 0);

INSERT INTO reservations (id, product_id, quantity, status, created_at, expires_at) VALUES
    ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '11111111-1111-1111-1111-111111111111',
     2, 'ACTIVE', NOW(), NOW() + INTERVAL '10 minutes');

INSERT INTO reservations (id, product_id, quantity, status, created_at, expires_at) VALUES
    ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', '11111111-1111-1111-1111-111111111111',
     1, 'ACTIVE', NOW() - INTERVAL '20 minutes', NOW() - INTERVAL '10 minutes');