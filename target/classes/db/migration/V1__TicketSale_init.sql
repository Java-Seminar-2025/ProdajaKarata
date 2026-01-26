CREATE TABLE country (
    country_uid BINARY(16) NOT NULL,
    country_name VARCHAR(20) NOT NULL,
    vat DOUBLE NOT NULL,
    PRIMARY KEY (country_uid)
);

CREATE TABLE city (
    city_uid BINARY(16) NOT NULL,
    zip_code VARCHAR(20) NOT NULL,
    city_name VARCHAR(20) NOT NULL,
    country_uid BINARY(16) NOT NULL,
    PRIMARY KEY (city_uid),
    UNIQUE (zip_code),
    CONSTRAINT fk_city_country FOREIGN KEY (country_uid) REFERENCES country (country_uid) ON DELETE CASCADE
);

CREATE TABLE user (
    user_uid BINARY(16) NOT NULL,
    username VARCHAR(20) NOT NULL,
    e_mail VARCHAR(30) NOT NULL,
    password_hash VARCHAR(60) NOT NULL,
    full_name VARCHAR(40) NOT NULL,
    pin VARCHAR(11),
    authorization_level VARCHAR(20) DEFAULT 'user',
    creation_timestamp DATETIME(6),
    country_uid BINARY(16),
    PRIMARY KEY (user_uid),
    UNIQUE (username),
    UNIQUE (e_mail),
    CONSTRAINT fk_user_country FOREIGN KEY (country_uid) REFERENCES country (country_uid) ON DELETE SET NULL
);

CREATE TABLE invoice (
    invoice_uid BINARY(16) NOT NULL,
    paypal_payment_id VARCHAR(40) NOT NULL,
    user_uid BINARY(16),
    payment_status VARCHAR(20) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    purchase_qty INT NOT NULL,
    created_at DATETIME(6),
    PRIMARY KEY (invoice_uid),
    CONSTRAINT fk_invoice_user FOREIGN KEY (user_uid) REFERENCES user (user_uid) ON DELETE SET NULL
);

CREATE TABLE football_club (
    club_uid BINARY(16) NOT NULL,
    club_name VARCHAR(20) NOT NULL,
    total_players INT NOT NULL,
    PRIMARY KEY (club_uid)
);

CREATE TABLE stadium (
    stadium_uid BINARY(16) NOT NULL,
    stadium_name VARCHAR(20) NOT NULL,
    number_of_seats INT NOT NULL,
    city_uid BINARY(16),
    PRIMARY KEY (stadium_uid),
    CONSTRAINT fk_stadium_city FOREIGN KEY (city_uid) REFERENCES city (city_uid) ON DELETE CASCADE
);

CREATE TABLE `match` (
    match_uid BINARY(16) NOT NULL,
    match_datetime DATETIME(6),
    base_ticket_price_usd DECIMAL(10, 2) NOT NULL,
    stadium_uid BINARY(16),
    home_club_uid BINARY(16) NOT NULL,
    away_club_uid BINARY(16) NOT NULL,
    PRIMARY KEY (match_uid),
    CONSTRAINT fk_match_stadium FOREIGN KEY (stadium_uid) REFERENCES stadium (stadium_uid) ON DELETE SET NULL,
    CONSTRAINT fk_match_home_club FOREIGN KEY (home_club_uid) REFERENCES football_club (club_uid),
    CONSTRAINT fk_match_away_club FOREIGN KEY (away_club_uid) REFERENCES football_club (club_uid)
);

CREATE TABLE ticket_tier (
    tier_uid BINARY(16) NOT NULL,
    tier_name VARCHAR(20),
    price_modifier DOUBLE NOT NULL,
    PRIMARY KEY (tier_uid),
    UNIQUE (tier_name)
);

CREATE TABLE ticket (
    ticket_uid BINARY(16) NOT NULL,
    user_uid BINARY(16) NOT NULL,
    owner_name VARCHAR(20) NOT NULL,
    pin VARCHAR(11) NOT NULL,
    tier_uid BINARY(16),
    status VARCHAR(20),
    creation_datetime DATETIME(6),
    invoice_uid BINARY(16),
    PRIMARY KEY (ticket_uid),
    CONSTRAINT fk_ticket_user FOREIGN KEY (user_uid) REFERENCES user (user_uid) ON DELETE CASCADE,
    CONSTRAINT fk_ticket_tier FOREIGN KEY (tier_uid) REFERENCES ticket_tier (tier_uid) ON DELETE SET NULL,
    CONSTRAINT fk_ticket_invoice FOREIGN KEY (invoice_uid) REFERENCES invoice (invoice_uid) ON DELETE SET NULL
);

CREATE TABLE seat_reservation (
    match_uid BINARY(16) NOT NULL,
    seat_number INT NOT NULL,
    ticket_uid BINARY(16) NOT NULL,
    PRIMARY KEY (match_uid, seat_number),
    UNIQUE (ticket_uid),
    CONSTRAINT fk_seat_reservation_match FOREIGN KEY (match_uid) REFERENCES `match` (match_uid) ON DELETE CASCADE,
    CONSTRAINT fk_seat_reservation_ticket FOREIGN KEY (ticket_uid) REFERENCES ticket (ticket_uid) ON DELETE CASCADE
);