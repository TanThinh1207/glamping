CREATE TABLE ` user `
(
    `
    id `           integer PRIMARY KEY, `
    email `        varchar(255), `
    passoword `    varchar(255), `
    first_name `   varchar(255), `
    last_name `    varchar(255), `
    phone_number ` varchar(255), `
    address `      varchar(255), `
    role `         varchar(255), `
    created_at `   timestamp, `
    status `       boolean
);

CREATE TABLE ` feedbacks `
(
    `
    id `         integer PRIMARY KEY, `
    id_boking `  integer, `
    id_user `    integer, `
    comment `    varchar(255), `
    rating `     integer, `
    created_at ` timestamp
);

CREATE TABLE ` booking `
(
    `
    id `           integer PRIMARY KEY, `
    id_user `      integer, `
    id_camp `      integer, `
    created_at `   timestamp, `
    check_in_at `  timestamp, `
    check_out_at ` timestamp, `
    status `       varchar(255), `
    total_amount ` decimal
);

CREATE TABLE ` camp_area `
(
    `
    id `         integer PRIMARY KEY, `
    name `       varchar(255), `
    address `    varchar(255), `
    latitude `   decimal, `
    longitude `  decimal, `
    created_at ` timestamp, `
    quantity `   integer, `
    status `     varchar(255), `
    id_user `    integer
);

CREATE TABLE ` camp `
(
    `
    id `           integer PRIMARY KEY, `
    name `         varchar(255), `
    description `  text, `
    created_at `   timestamp, `
    status `       boolean, `
    capacity `     integer, `
    price `        decimal, `
    rate `         decimal, `
    updated_at `   timestamp, `
    id_camp_area ` integer
);

CREATE TABLE ` payment `
(
    `
    id `             integer PRIMARY KEY, `
    id_booking `     integer, `
    payment_method ` varchar(255), `
    total_amount `   decimal, `
    status `         varchar(255), `
    id_transaction ` varchar(255), `
    completed_at `   timestamp
);

CREATE TABLE ` service `
(
    `
    id `          integer PRIMARY KEY, `
    name `        varchar(255), `
    description ` text, `
    price `       decimal, `
    status `      boolean, `
    updated_at `  timestamp
);

CREATE TABLE ` booking_service `
(
    `
    id_booking ` integer, `
    id_service ` integer, `
    name `       varchar(255), `
    quantity `   decimal
);

CREATE TABLE ` option `
(
    `
    id `          integer, `
    name `        varchar(255), `
    description ` text, `
    status `      boolean
);

CREATE TABLE ` camp_option `
(
    `
    id_option ` integer, `
    id_camp `   integer, `
    status `    boolean,
    PRIMARY KEY (` id_option `, ` id_camp `)
);

CREATE TABLE ` image `
(
    `
    id `           integer PRIMARY KEY, `
    id_camp_area ` intger, `
    path `         varchar(255)
);

CREATE TABLE ` report `
(
    `
    id `           integer, `
    id_camp_area ` integer, `
    id_user `      integer, `
    status `       varchar(255), `
    created_at `   timestamp, `
    message `      text, `
    report_type `  varchar(255)
);

ALTER TABLE ` feedbacks `
    ADD FOREIGN KEY (` id_user `) REFERENCES ` user ` (` id `);

ALTER TABLE ` booking `
    ADD FOREIGN KEY (` id_user `) REFERENCES ` user ` (` id `);

ALTER TABLE ` camp_area `
    ADD FOREIGN KEY (` id_user `) REFERENCES ` user ` (` id `);

ALTER TABLE ` camp `
    ADD FOREIGN KEY (` id_camp_area `) REFERENCES ` camp_area ` (` id `);

ALTER TABLE ` payment `
    ADD FOREIGN KEY (` id_booking `) REFERENCES ` booking ` (` id `);

ALTER TABLE ` booking_service `
    ADD FOREIGN KEY (` id_booking `) REFERENCES ` booking ` (` id `);

ALTER TABLE ` booking_service `
    ADD FOREIGN KEY (` id_service `) REFERENCES ` service ` (` id `);

ALTER TABLE ` camp_option `
    ADD FOREIGN KEY (` id_camp `) REFERENCES ` camp ` (` id `);

ALTER TABLE ` camp_option `
    ADD FOREIGN KEY (` id_option `) REFERENCES ` option ` (` id `);

ALTER TABLE ` image `
    ADD FOREIGN KEY (` id_camp_area `) REFERENCES ` camp_area ` (` id `);

ALTER TABLE ` report `
    ADD FOREIGN KEY (` id_camp_area `) REFERENCES ` camp_area ` (` id `);

ALTER TABLE ` report `
    ADD FOREIGN KEY (` id_user `) REFERENCES ` user ` (` id `);
