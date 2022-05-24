-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema si
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema si
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `si` DEFAULT CHARACTER SET utf8 ;
USE `si` ;

-- -----------------------------------------------------
-- Table `si`.`noticia`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `si`.`noticia` (
  `idnoticia` INT NOT NULL AUTO_INCREMENT,
  `titular` VARCHAR(255) NOT NULL,
  `noticia` TEXT NOT NULL,
  `data` DATETIME NOT NULL,
  PRIMARY KEY (`idnoticia`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `si`.`comentari`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `si`.`comentari` (
  `idcomentari` INT NOT NULL AUTO_INCREMENT,
  `usuari` VARCHAR(255) NOT NULL,
  `comentari` TEXT NOT NULL,
  `noticia_idnoticia` INT NOT NULL,
  PRIMARY KEY (`idcomentari`),
  INDEX `fk_comentari_noticia_idx` (`noticia_idnoticia` ASC) VISIBLE,
  CONSTRAINT `fk_comentari_noticia`
    FOREIGN KEY (`noticia_idnoticia`)
    REFERENCES `si`.`noticia` (`idnoticia`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
