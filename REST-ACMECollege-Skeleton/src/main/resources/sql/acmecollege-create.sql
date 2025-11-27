-- -----------------------------------------------------
-- Drop Schema for ACMECollege Application
--
-- In order for the `cst8277`@`localhost` user to be able to create (or drop) a schema,
-- it needs additional privileges.  If you are using MySQL Workbench, log-in to it as root,
-- click on the 'Administration' tab, select 'Users and Privileges' and find and click the cst8277 user.
-- The 'Administrative Roles' tab has an entry 'DBA' - select it, and click all the individual PRIVILEGES
-- and then click 'Apply'.
--
-- If you wish to use a 'raw' .sql-script instead, you still need to log-in as root,
-- the command to GRANT the appropriate PRIVILEGES is:
-- GRANT ALL PRIVILEGES ON *.* TO `cst8277`@`localhost`;
--
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `acmecollege` DEFAULT CHARACTER SET utf8mb4;
USE `acmecollege`;

-- --------------------------------------------
-- Table `student`
-- -------------------------------------------
CREATE TABLE IF NOT EXISTS `student`(
  `id` INT NOT NULL AUTO_INCREMENT,
  `first_name` VARCHAR(50) NOT NULL,
  `last_name` VARCHAR(50) NOT NULL,
  `email` VARCHAR(100) NULL,
  `phone` VARCHAR(10) NULL,
  `program` VARCHAR(45) NULL,
  `created` DATETIME NULL,
  `updated` DATETIME NULL,
  `version` BIGINT NOT NULL DEFAULT 1,
  PRIMARY KEY (`id`))
ENGINE = InnoDB;

-- --------------------------------------------
-- Table `program`
-- -------------------------------------------
CREATE TABLE IF NOT EXISTS `program`(
  `name` VARCHAR(45) NULL)
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `course`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `course` (
  `course_id` INT NOT NULL AUTO_INCREMENT,
  `course_code` VARCHAR(7) NOT NULL,
  `course_title` VARCHAR(100) NOT NULL,
  `credit_units` INT NOT NULL,
  `online` BIT(1) NOT NULL,
  `created` DATETIME NULL,
  `updated` DATETIME NULL,
  `version` BIGINT NOT NULL DEFAULT 1,
  PRIMARY KEY (`course_id`)
);

-- -----------------------------------------------------
-- Table `professor`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `professor` (
  `professor_id` INT NOT NULL AUTO_INCREMENT,
  `first_name` VARCHAR(50) NOT NULL,
  `last_name` VARCHAR(50) NOT NULL,
  `degree` VARCHAR(45) NULL,
  `created` DATETIME NULL,
  `updated` DATETIME NULL,
  `version` BIGINT NOT NULL DEFAULT 1,
  PRIMARY KEY (`professor_id`)
);

-- -----------------------------------------------------
-- Table `degree`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `degree`(
  `name` VARCHAR(45) NULL)
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table `student_club`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `student_club` (
  `club_id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(100) NOT NULL,
  `description` VARCHAR(100) NOT NULL,
  `academic` BIT(1) NOT NULL,
  `created` DATETIME NULL,
  `updated` DATETIME NULL,
  `version` BIGINT NOT NULL DEFAULT 1,
  PRIMARY KEY (`club_id`),
  UNIQUE INDEX `name_UNIQUE`(`name` ASC) VISIBLE
);

-- -----------------------------------------------------
-- Table `course_registration`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `course_registration` (
  `student_id` INT NOT NULL,
  `course_id` INT NOT NULL,
  `professor_id` INT NULL,
  `year` INT NOT NULL,
  `semester` VARCHAR(6) NOT NULL,
  `letter_grade` VARCHAR(3) NULL,
  `created` DATETIME NULL,
  `updated` DATETIME NULL,
  `version` BIGINT NOT NULL DEFAULT 1,
  PRIMARY KEY (`student_id`, `course_id`),
  INDEX `fk_course_registration_student1_idx`(`student_id` ASC) VISIBLE,
  INDEX `fk_course_registration_course1_idx`(`course_id` ASC) VISIBLE,
  CONSTRAINT `fk_course_registration_student1`
    FOREIGN KEY (`student_id`)
    REFERENCES `student`(`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_course_registration_course1`
    FOREIGN KEY (`course_id`)
    REFERENCES `course`(`course_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_course_registration_professor1`
    FOREIGN KEY (`professor_id`)
    REFERENCES `professor`(`professor_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
);

-- --------------------------------------------
-- Table `semester`
-- -------------------------------------------
CREATE TABLE IF NOT EXISTS `semester`(
  `name` VARCHAR(6) NULL)
ENGINE = InnoDB;

-- --------------------------------------------
-- Table `letter_grade`
-- -------------------------------------------
CREATE TABLE IF NOT EXISTS `letter_grade`(
  `grade` VARCHAR(3) NULL)
ENGINE = InnoDB;

-- Table for the many-to-many relationship between Student and StudentClub
CREATE TABLE IF NOT EXISTS `club_membership` (
  `student_id` INT NOT NULL,
  `club_id` INT NOT NULL,
  PRIMARY KEY (`student_id`, `club_id`),
  INDEX `fk_student_joins_student_club1_idx`(`student_id` ASC) VISIBLE,
  INDEX `fk_student_club_welcomes_student1_idx`(`club_id` ASC) VISIBLE,
  CONSTRAINT `fk_student_joins_student_club1`
    FOREIGN KEY (`student_id`)
    REFERENCES `student`(`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_student_club_welcomes_student1`
    FOREIGN KEY (`club_id`)
    REFERENCES `student_club`(`club_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
);

-- Table for SecurityUser
CREATE TABLE IF NOT EXISTS `security_user` (
  `user_id` INT NOT NULL AUTO_INCREMENT,
  `password_hash` VARCHAR(256) NOT NULL,
  `username` VARCHAR(100) NOT NULL,
  `student_id` INT NULL DEFAULT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE INDEX `user_id_UNIQUE` (`user_id` ASC) VISIBLE,
  UNIQUE INDEX `username_UNIQUE` (`username` ASC) VISIBLE,
  INDEX `fk_security_user_student1_idx` (`student_id` ASC) VISIBLE,
  CONSTRAINT `fk_security_user_student1`
    FOREIGN KEY (`student_id`)
    REFERENCES `student` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
);

-- Table for SecurityRole
CREATE TABLE IF NOT EXISTS `security_role` (
  `role_id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`role_id`),
  UNIQUE INDEX `role_id_UNIQUE` (`role_id` ASC) VISIBLE,
  UNIQUE INDEX `name_UNIQUE` (`name` ASC) VISIBLE
);

-- Table for the many-to-many relationship between SecurityUser and SecurityRole
CREATE TABLE IF NOT EXISTS `user_has_role` (
  `user_id` INT NOT NULL,
  `role_id` INT NOT NULL,
  PRIMARY KEY (`user_id`, `role_id`),
  INDEX `fk_security_user_has_security_role_security_role1_idx` (`role_id` ASC) VISIBLE,
  INDEX `fk_security_user_has_security_role_security_user1_idx` (`user_id` ASC) VISIBLE,
  CONSTRAINT `fk_security_user_has_security_role_security_user1`
    FOREIGN KEY (`user_id`)
    REFERENCES `security_user` (`user_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_security_user_has_security_role_security_role1`
    FOREIGN KEY (`role_id`)
    REFERENCES `security_role` (`role_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
);
