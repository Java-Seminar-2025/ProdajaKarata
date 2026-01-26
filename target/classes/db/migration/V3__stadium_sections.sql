CREATE TABLE stadium_section (
    section_uid BINARY(16) PRIMARY KEY,
    stadium_uid BINARY(16) NOT NULL,
    section_code VARCHAR(10) NOT NULL,
    section_name VARCHAR(60) NULL,
    stand_name VARCHAR(60) NULL,
    seat_start INT NOT NULL,
    seat_end INT NOT NULL,

    CONSTRAINT fk_section_stadium FOREIGN KEY (stadium_uid) REFERENCES stadium(stadium_uid),
    CONSTRAINT ux_section_stadium_code UNIQUE (stadium_uid, section_code),
    CONSTRAINT ck_section_range CHECK (seat_start >= 1 AND seat_end >= seat_start)
);
