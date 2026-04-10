CREATE TABLE IF NOT EXISTS notification_schema.notification_outbox_events (
                                                               id uuid NOT NULL,
                                                               aggregate_id character varying(255) NULL,
                                                               created_at timestamp(6) without time zone NULL,
                                                               event_type character varying(255) NULL,
                                                               payload text NULL,
                                                               retry_count integer NOT NULL,
                                                               target_system character varying(255) NULL,
                                                               status character varying(255) NULL,
                                                               topic character varying(255) NULL
);

ALTER TABLE IF EXISTS notification_schema.notification_outbox_events
    ADD CONSTRAINT notification_outbox_events_pkey PRIMARY KEY (id);