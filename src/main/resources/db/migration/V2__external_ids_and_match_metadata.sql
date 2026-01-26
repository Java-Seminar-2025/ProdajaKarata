ALTER TABLE football_club
    MODIFY club_name VARCHAR(100) NOT NULL;

ALTER TABLE football_club
    ADD COLUMN source VARCHAR(30) NULL,
    ADD COLUMN source_team_id VARCHAR(40) NULL;

CREATE UNIQUE INDEX ux_club_source_team
    ON football_club (source, source_team_id);

ALTER TABLE `match`
    ADD COLUMN source VARCHAR(30) NULL,
    ADD COLUMN source_match_id VARCHAR(40) NULL,
    ADD COLUMN competition_code VARCHAR(20) NULL,
    ADD COLUMN status VARCHAR(20) NULL;

CREATE UNIQUE INDEX ux_match_source_match
    ON `match` (source, source_match_id);
