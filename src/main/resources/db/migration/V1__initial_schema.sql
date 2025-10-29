CREATE TABLE IF NOT EXISTS `Country` (
  `UID` INT NOT NULL AUTO_INCREMENT,
  `Country_Name` VARCHAR(100) NOT NULL,
  `VAT` DECIMAL(5,2) DEFAULT NULL,
  PRIMARY KEY (`UID`),
  UNIQUE KEY `Country_Name` (`Country_Name`),
  CONSTRAINT `Country_chk_1` CHECK (`VAT` >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE IF NOT EXISTS `City` (
  `Zip_code` VARCHAR(10) NOT NULL,
  `City_name` VARCHAR(100) NOT NULL,
  `Country` INT NOT NULL,
  PRIMARY KEY (`Zip_code`),
  KEY `fk_city_country` (`Country`),
  CONSTRAINT `fk_city_country` FOREIGN KEY (`Country`) REFERENCES `Country` (`UID`)
    ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE IF NOT EXISTS `Stadium` (
  `UID` INT NOT NULL AUTO_INCREMENT,
  `Stadium_name` VARCHAR(150) NOT NULL,
  `Number_of_seats` INT NOT NULL,
  `City` VARCHAR(10) NOT NULL,
  PRIMARY KEY (`UID`),
  KEY `fk_stadium_city` (`City`),
  CONSTRAINT `fk_stadium_city` FOREIGN KEY (`City`) REFERENCES `City` (`Zip_code`)
    ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `Stadium_chk_1` CHECK (`Number_of_seats` > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE IF NOT EXISTS `Football_Club` (
  `Club_UID` INT NOT NULL AUTO_INCREMENT,
  `Club_name` VARCHAR(150) NOT NULL,
  `City` VARCHAR(10) DEFAULT NULL,
  `Total_players` INT DEFAULT 0,
  PRIMARY KEY (`Club_UID`),
  UNIQUE KEY `Club_name` (`Club_name`),
  KEY `fk_club_city` (`City`),
  CONSTRAINT `fk_club_city` FOREIGN KEY (`City`) REFERENCES `City` (`Zip_code`)
    ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `Football_Club_chk_1` CHECK (`Total_players` >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE IF NOT EXISTS `Match` (
  `Match_UID` INT NOT NULL AUTO_INCREMENT,
  `Match_datetime` DATETIME NOT NULL,
  `Stadium` INT NOT NULL,
  `Tickets_left` INT DEFAULT 0,
  PRIMARY KEY (`Match_UID`),
  KEY `fk_match_stadium` (`Stadium`),
  CONSTRAINT `fk_match_stadium` FOREIGN KEY (`Stadium`) REFERENCES `Stadium` (`UID`)
    ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `Match_chk_1` CHECK (`Tickets_left` >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE IF NOT EXISTS `Match_competitors` (
  `Match_ID` INT NOT NULL,
  `Club_ID` INT NOT NULL,
  PRIMARY KEY (`Match_ID`, `Club_ID`),
  KEY `fk_mc_club` (`Club_ID`),
  CONSTRAINT `fk_mc_club` FOREIGN KEY (`Club_ID`) REFERENCES `Football_Club` (`Club_UID`)
    ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_mc_match` FOREIGN KEY (`Match_ID`) REFERENCES `Match` (`Match_UID`)
    ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE IF NOT EXISTS `User` (
  `UID` INT NOT NULL AUTO_INCREMENT,
  `Paypal_ID` VARCHAR(100) DEFAULT NULL,
  `Username` VARCHAR(50) NOT NULL,
  `Email` VARCHAR(100) NOT NULL,
  `Password_hash` CHAR(64) NOT NULL,
  `Full_name` VARCHAR(150) DEFAULT NULL,
  `PIN` CHAR(6) DEFAULT NULL,
  `Authorization_level` ENUM('User','Admin','SuperAdmin') DEFAULT 'User',
  `Creation_timestamp` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  `Country` INT DEFAULT NULL,
  PRIMARY KEY (`UID`),
  UNIQUE KEY `Username` (`Username`),
  UNIQUE KEY `Email` (`Email`),
  KEY `fk_user_country` (`Country`),
  CONSTRAINT `fk_user_country` FOREIGN KEY (`Country`) REFERENCES `Country` (`UID`)
    ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;