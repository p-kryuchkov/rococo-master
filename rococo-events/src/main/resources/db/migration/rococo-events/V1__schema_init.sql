create table if not exists events
(
    id           binary(16)   not null primary key default (UUID_TO_BIN(UUID(), true)),
    event_time   datetime(3)  not null,
    event_type   varchar(20)  not null,
    description  varchar(1000),
    entity_id    binary(16),
    username varchar(50),

    constraint chk_entity_event_type
        check (event_type in ('CREATE', 'UPDATE', 'DELETE', 'GET'))
);

create index idx_event_username on events(username);
create index idx_event_time on events(event_time);
