-- MySQL dump 10.13  Distrib 5.7.12, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: ichatdemp
-- ------------------------------------------------------
-- Server version	5.7.17

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `chatrecord`
--

DROP TABLE IF EXISTS `chatrecord`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `chatrecord` (
  `recordID` int(11) NOT NULL,
  `recordstate` int(11) DEFAULT NULL,
  `content` varchar(200) DEFAULT NULL,
  `ReceiveUser` varchar(45) DEFAULT NULL,
  `SendUser` varchar(45) DEFAULT NULL,
  `times` datetime DEFAULT NULL,
  `ExpireDate` date DEFAULT NULL,
  `PicturePath` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`recordID`),
  KEY `username_idx` (`SendUser`),
  CONSTRAINT `sender` FOREIGN KEY (`SendUser`) REFERENCES `login` (`UserName`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chatrecord`
--

LOCK TABLES `chatrecord` WRITE;
/*!40000 ALTER TABLE `chatrecord` DISABLE KEYS */;
INSERT INTO `chatrecord` VALUES (1,1,'testtest content','Buser','Auser','2017-04-09 23:59:59','9999-12-31',NULL),(2,1,'jjjj\n4.248\n\0\0\0\0\0\0\0\0\0','Buser','Auser','2017-04-12 17:43:52','9999-12-31',''),(3,1,'','Buser','Auser','2017-04-12 19:39:28','9999-12-31','D:\\img_record\\1491997168177.jpg'),(4,1,'jjjkk','Buser','Auser','2017-04-12 21:11:58','9999-12-31',''),(5,1,'ttt','Buser','aaaa','2017-04-16 11:31:58','9999-12-31',''),(6,1,'uuu','Buser','aaaa','2017-04-16 11:33:13','9999-12-31',''),(7,1,'ll','Buser','aaaa','2017-04-16 11:35:41','9999-12-31','');
/*!40000 ALTER TABLE `chatrecord` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2017-04-18 23:57:47
