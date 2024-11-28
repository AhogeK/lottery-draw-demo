-- MySQL dump 10.13  Distrib 9.0.1, for macos14.4 (arm64)
--
-- Host: 192.168.50.116    Database: lottery_draw_demo
-- ------------------------------------------------------
-- Server version	8.0.40-0ubuntu0.22.04.1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT = @@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS = @@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION = @@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE = @@TIME_ZONE */;
/*!40103 SET TIME_ZONE = '+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS = @@UNIQUE_CHECKS, UNIQUE_CHECKS = 0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS = @@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS = 0 */;
/*!40101 SET @OLD_SQL_MODE = @@SQL_MODE, SQL_MODE = 'NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES = @@SQL_NOTES, SQL_NOTES = 0 */;

--
-- Table structure for table `lottery_data`
--

DROP TABLE IF EXISTS `lottery_data`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `lottery_data`
(
    `id`                       bigint unsigned                                             NOT NULL AUTO_INCREMENT COMMENT '主键',
    `lottery_draw_time`        date                                                        NOT NULL COMMENT '彩票出球日',
    `lottery_draw_number`      varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '彩票球号',
    `lottery_draw_number_type` int unsigned                                                NOT NULL COMMENT '彩票球号类型（0-6 分别代表前区的五个球与后区的两个球）',
    `sort`                     tinyint unsigned                                            NOT NULL DEFAULT '0' COMMENT '出球顺序',
    PRIMARY KEY (`id`),
    CONSTRAINT `check_lottery_draw_number_type` CHECK ((`lottery_draw_number_type` in (0, 1, 2, 3, 4, 5, 6)))
) ENGINE = InnoDB
  AUTO_INCREMENT = 18586
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='彩票数据';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `self_chosen`
--

DROP TABLE IF EXISTS `self_chosen`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `self_chosen`
(
    `id`                  bigint unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
    `draw_time`           date            NOT NULL DEFAULT (curdate()) COMMENT '自选时间',
    `number`              varchar(2)      NOT NULL COMMENT '号码',
    `number_type`         int unsigned    NOT NULL COMMENT '号码序号',
    `sort`                tinyint unsigned         DEFAULT NULL COMMENT '出球顺序',
    `prize`               tinyint unsigned         DEFAULT NULL COMMENT '奖项 0-未中奖 其余数字与奖级一致',
    `is_historical_first` tinyint(1)               DEFAULT NULL COMMENT '是否是历史第一次',
    PRIMARY KEY (`id`),
    CONSTRAINT `check_is_historical_first` CHECK (((`is_historical_first` is null) or (`is_historical_first` = 0) or
                                                   (`is_historical_first` = 1))),
    CONSTRAINT `check_number_type` CHECK ((`number_type` in (0, 1, 2, 3, 4, 5, 6))),
    CONSTRAINT `check_prize` CHECK (((`prize` is null) or ((`prize` >= 0) and (`prize` <= 9))))
) ENGINE = InnoDB
  AUTO_INCREMENT = 64
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='个人自选';
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE = @OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE = @OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS = @OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS = @OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT = @OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS = @OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION = @OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES = @OLD_SQL_NOTES */;

-- Dump completed on 2024-11-28 15:47:22
