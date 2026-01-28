INSERT INTO ticket_tier (tier_uid, tier_name, price_modifier)
VALUES
  (UNHEX(REPLACE(UUID(), '-', '')), 'CHILD', 0.70),
  (UNHEX(REPLACE(UUID(), '-', '')), 'ADULT', 1.00),
  (UNHEX(REPLACE(UUID(), '-', '')), 'ELDERLY', 0.80)
ON DUPLICATE KEY UPDATE
  price_modifier = VALUES(price_modifier);
