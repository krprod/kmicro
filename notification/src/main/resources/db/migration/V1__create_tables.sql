CREATE TABLE notification_schema.notification_users (
                                           address_id integer NULL,
                                           user_id integer NULL,
                                           created_at timestamp(6) with time zone NOT NULL,
                                           updated_at timestamp(6) with time zone NULL,
                                           id uuid NOT NULL,
                                           city character varying(255) NULL,
                                           contact character varying(255) NULL,
                                           country character varying(255) NULL,
                                           email character varying(255) NULL,
                                           recipient_name character varying(255) NULL,
                                           shipping_address character varying(255) NULL,
                                           state character varying(255) NULL,
                                           zip_code character varying(255) NULL
);

ALTER TABLE notification_schema.notification_users
    ADD CONSTRAINT notification_users_pkey PRIMARY KEY (id);

CREATE TABLE notification_schema.notifications (
                                      priority integer NULL,
                                      recipient_id integer NULL,
                                      retry_count integer NULL,
                                      created_at timestamp(6) with time zone NOT NULL,
                                      scheduled_at timestamp(6) without time zone NULL,
                                      updated_at timestamp(6) with time zone NULL,
                                      id uuid NOT NULL,
                                      channel_type character varying(255) NULL,
                                      fail_reason text NULL,
                                      fragment_path character varying(255) NULL,
                                      recipient_name character varying(255) NULL,
                                      send_to character varying(255) NULL,
                                      status character varying(255) NULL,
                                      subject character varying(255) NULL,
                                      mail_body jsonb NULL,
                                      payload jsonb NULL
);

ALTER TABLE notification_schema.notifications
    ADD CONSTRAINT notifications_pkey PRIMARY KEY (id);

CREATE TABLE notification_schema.notification_templates (
                                               created_at timestamp(6) with time zone NOT NULL,
                                               updated_at timestamp(6) with time zone NULL,
                                               id uuid NOT NULL,
                                               body_content text NULL,
                                               subject_line character varying(255) NULL,
                                               version character varying(255) NULL
);

ALTER TABLE notification_schema.notification_templates
    ADD CONSTRAINT notification_templates_pkey PRIMARY KEY (id);

CREATE TABLE notification_schema.notification_logs (
                                          created_at timestamp(6) with time zone NOT NULL,
                                          notification_id bigint NULL,
                                          updated_at timestamp(6) with time zone NULL,
                                          id uuid NOT NULL,
                                          error_code character varying(255) NULL,
                                          provider_response text NULL
);

ALTER TABLE notification_schema.notification_logs
    ADD CONSTRAINT notification_logs_pkey PRIMARY KEY (id);


CREATE TABLE if not exists notification_schema.shedlock (
                                                            name character varying(64) NOT NULL,
                                                            lock_until timestamp without time zone NOT NULL,
                                                            locked_at timestamp without time zone NOT NULL,
                                                            locked_by character varying(255) NOT NULL
);
ALTER TABLE if exists notification_schema.shedlock
    ADD CONSTRAINT shedlock_pkey PRIMARY KEY (name);