CREATE SEQUENCE order_schema.orders_seq
    START WITH 1
    INCREMENT BY 50;

SELECT setval('order_schema.orders_seq', COALESCE((SELECT MAX(id) FROM  order_schema.orders), 0) + 1, false);


CREATE SEQUENCE order_schema.order_item_seq
    START WITH 1
    INCREMENT BY 50;

SELECT setval('order_schema.order_item_seq', COALESCE((SELECT MAX(id) FROM  order_schema.order_item), 0) + 1, false);

CREATE SEQUENCE order_schema.outbox_events_seq
    START WITH 1
    INCREMENT BY 50;

SELECT setval('order_schema.outbox_events_seq', COALESCE((SELECT MAX(id) FROM  order_schema.outbox_events), 0) + 1, false);

