INSERT INTO ticket_tier (tier_uid, tier_name, price_modifier)
VALUES
  (UUID_TO_BIN(UUID()), 'CHILD', 0.70),
  (UUID_TO_BIN(UUID()), 'ADULT', 1.00),
  (UUID_TO_BIN(UUID()), 'ELDERLY', 0.80);

