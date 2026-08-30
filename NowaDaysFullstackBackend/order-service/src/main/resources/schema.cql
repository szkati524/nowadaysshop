CREATE KEYSPACE IF NOT EXISTS order_keyspace
WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};

CREATE TABLE IF NOT EXISTS order_keyspace.orders (
    id uuid PRIMARY KEY,
    user_id uuid,
    product_id uuid,
    quantity int,
    total_price decimal,
    status text,
    created_at timestamp
);