-- MySQL dump 10.13  Distrib 5.7.31, for Linux (x86_64)
--
-- Host: localhost    Database: cl21002101
-- ------------------------------------------------------
-- Server version	5.7.31

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
-- Current Database: `cl21002101`
--

/*!40000 DROP DATABASE IF EXISTS `cl21002101`*/;

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `cl21002101` /*!40100 DEFAULT CHARACTER SET utf8mb4 */;

USE `cl21002101`;

--
-- Table structure for table `config`
--

DROP TABLE IF EXISTS `config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(100) NOT NULL COMMENT '配置参数名称',
  `value` varchar(100) DEFAULT NULL COMMENT '配置参数值',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COMMENT='配置文件';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `config`
--

LOCK TABLES `config` WRITE;
/*!40000 ALTER TABLE `config` DISABLE KEYS */;
INSERT INTO `config` VALUES (1,'swiper1','file/swiperPicture1.jpg'),(2,'swiper2','file/swiperPicture2.jpg'),(3,'swiper3','file/swiperPicture3.jpg');
/*!40000 ALTER TABLE `config` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `exampaper`
--

DROP TABLE IF EXISTS `exampaper`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `exampaper` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `name` varchar(200) NOT NULL COMMENT '试卷名称',
  `time` int(11) NOT NULL COMMENT '考试时长(分钟)',
  `status` varchar(200) DEFAULT NULL COMMENT '试卷状态',
  `tijiaofangshi` varchar(50) DEFAULT '在线答题' COMMENT '提交方式（在线答题/pdf附件/线下提交）',
  `pdfurl` varchar(255) DEFAULT NULL COMMENT 'PDF附件',
  `xianxiashuoming` text DEFAULT NULL COMMENT '线下提交说明',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='试卷管理';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `exampaper`
--

LOCK TABLES `exampaper` WRITE;
/*!40000 ALTER TABLE `exampaper` DISABLE KEYS */;
INSERT INTO `exampaper` VALUES (1,'2024-03-05 13:53:21','试卷1',120,'1','在线答题',NULL,NULL);
/*!40000 ALTER TABLE `exampaper` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `examquestion`
--

DROP TABLE IF EXISTS `examquestion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `examquestion` (
  `id` bigint(20) NOT NULL COMMENT '主键',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `paperid` bigint(20) NOT NULL COMMENT '试卷id（外键）',
  `papername` varchar(200) NOT NULL COMMENT '试卷名称',
  `questionname` varchar(200) NOT NULL COMMENT '试题名称',
  `options` longtext COMMENT '选项，json字符串',
  `score` bigint(20) DEFAULT NULL COMMENT '分值',
  `answer` varchar(200) DEFAULT NULL COMMENT '正确答案',
  `analysis` longtext COMMENT '答案解析',
  `type` bigint(20) DEFAULT NULL COMMENT '试题类型',
  `sequence` bigint(20) DEFAULT NULL COMMENT '试题排序，值越大排越前面',
  `images` varchar(1000) DEFAULT NULL COMMENT '题目图片/PDF附件（逗号分隔多个文件路径）',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='试题管理';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `examquestion`
--

LOCK TABLES `examquestion` WRITE;
/*!40000 ALTER TABLE `examquestion` DISABLE KEYS */;
INSERT INTO `examquestion` (`id`,`addtime`,`paperid`,`papername`,`questionname`,`options`,`score`,`answer`,`analysis`,`type`,`sequence`) VALUES (1,'2024-03-05 13:53:21',1,'试卷1','1+1等于多少?','[{\"text\":\"2\",\"code\":\"A\"},{\"text\":\"11\",\"code\":\"B\"},{\"text\":\"十\",\"code\":\"C\"},{\"text\":\"王\",\"code\":\"D\"}]',20,'A','2',0,1),(2,'2024-03-05 13:53:21',1,'试卷1','2-1等于1，对吗?','[{\"text\":\"对\",\"code\":\"A\"},{\"text\":\"错\",\"code\":\"B\"}]',20,'A','2-1等于1',2,2),(3,'2024-03-05 13:53:21',1,'试卷1','下面哪些数是质数？。','[{\"text\":\"2\",\"code\":\"A\"},{\"text\":\"3\",\"code\":\"B\"},{\"text\":\"4\",\"code\":\"C\"},{\"text\":\"5\",\"code\":\"D\"}]',30,'A,B,D','2,3,5属于质数',1,3),(4,'2024-03-05 13:53:21',1,'试卷1','最小的自然是( )。','[]',30,'0','最小的自然数是0',3,4),(1709647082650,'2024-03-05 13:58:02',1,'试卷1','1+1=2','[{\"text\":\"对\",\"code\":\"A\"},{\"text\":\"错\",\"code\":\"B\"}]',10,'对','1',2,1);
/*!40000 ALTER TABLE `examquestion` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `examrecord`
--

DROP TABLE IF EXISTS `examrecord`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `examrecord` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `username` varchar(200) NOT NULL COMMENT '用户名',
  `paperid` bigint(20) NOT NULL COMMENT '试卷id（外键）',
  `papername` varchar(200) NOT NULL COMMENT '试卷名称',
  `questionid` bigint(20) NOT NULL COMMENT '试题id（外键）',
  `questionname` varchar(200) NOT NULL COMMENT '试题名称',
  `options` longtext COMMENT '选项，json字符串',
  `score` bigint(20) DEFAULT NULL COMMENT '分值',
  `answer` varchar(200) DEFAULT NULL COMMENT '正确答案',
  `analysis` longtext COMMENT '答案解析',
  `myscore` bigint(20) DEFAULT NULL COMMENT '试题得分',
  `myanswer` varchar(200) DEFAULT NULL COMMENT '考生答案',
  `myanswerfiles` text COMMENT '解答题作答附件，多个用逗号分隔',
  `userid` bigint(20) NOT NULL COMMENT '用户id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1709647125865 DEFAULT CHARSET=utf8 COMMENT='考试记录';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `examrecord`
--

LOCK TABLES `examrecord` WRITE;
/*!40000 ALTER TABLE `examrecord` DISABLE KEYS */;
INSERT INTO `examrecord` (`id`,`addtime`,`username`,`paperid`,`papername`,`questionid`,`questionname`,`options`,`score`,`answer`,`analysis`,`myscore`,`myanswer`,`userid`) VALUES (1709647111943,'2024-03-05 13:58:31','1',1,'试卷1',1,'1+1等于多少?','[{\"text\":\"2\",\"code\":\"A\"},{\"text\":\"11\",\"code\":\"B\"},{\"text\":\"十\",\"code\":\"C\"},{\"text\":\"王\",\"code\":\"D\"}]',20,'A','2',0,'C',1709647024367),(1709647115096,'2024-03-05 13:58:34','1',1,'试卷1',1709647082650,'1+1=2','[{\"text\":\"对\",\"code\":\"A\"},{\"text\":\"错\",\"code\":\"B\"}]',10,'对','1',0,'B',1709647024367),(1709647118353,'2024-03-05 13:58:38','1',1,'试卷1',2,'2-1等于1，对吗?','[{\"text\":\"对\",\"code\":\"A\"},{\"text\":\"错\",\"code\":\"B\"}]',20,'A','2-1等于1',20,'A',1709647024367),(1709647123113,'2024-03-05 13:58:42','1',1,'试卷1',3,'下面哪些数是质数？。','[{\"text\":\"2\",\"code\":\"A\"},{\"text\":\"3\",\"code\":\"B\"},{\"text\":\"4\",\"code\":\"C\"},{\"text\":\"5\",\"code\":\"D\"}]',30,'A,B,D','2,3,5属于质数',0,'B,C',1709647024367),(1709647125864,'2024-03-05 13:58:45','1',1,'试卷1',4,'最小的自然是( )。','[]',30,'0','最小的自然数是0',30,'0',1709647024367);
/*!40000 ALTER TABLE `examrecord` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `jiaoshi`
--

DROP TABLE IF EXISTS `jiaoshi`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jiaoshi` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `jiaoshigonghao` varchar(200) NOT NULL COMMENT '教师工号',
  `jiaoshimima` varchar(200) NOT NULL COMMENT '教师密码',
  `jiaoshixingming` varchar(200) NOT NULL COMMENT '教师姓名',
  `touxiang` longtext COMMENT '头像',
  `xingbie` varchar(200) DEFAULT NULL COMMENT '性别',
  `lianxidianhua` varchar(200) DEFAULT NULL COMMENT '联系电话',
  PRIMARY KEY (`id`),
  UNIQUE KEY `jiaoshigonghao` (`jiaoshigonghao`)
) ENGINE=InnoDB AUTO_INCREMENT=1709646985613 DEFAULT CHARSET=utf8 COMMENT='教师';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `jiaoshi`
--

LOCK TABLES `jiaoshi` WRITE;
/*!40000 ALTER TABLE `jiaoshi` DISABLE KEYS */;
INSERT INTO `jiaoshi` VALUES (91,'2024-03-05 13:53:21','教师工号1','123456','教师姓名1','file/jiaoshiTouxiang1.jpg','男','19819881111'),(92,'2024-03-05 13:53:21','教师工号2','123456','教师姓名2','file/jiaoshiTouxiang2.jpg','男','19819881112'),(93,'2024-03-05 13:53:21','教师工号3','123456','教师姓名3','file/jiaoshiTouxiang3.jpg','男','19819881113'),(94,'2024-03-05 13:53:21','教师工号4','123456','教师姓名4','file/jiaoshiTouxiang4.jpg','男','19819881114'),(95,'2024-03-05 13:53:21','教师工号5','123456','教师姓名5','file/jiaoshiTouxiang5.jpg','男','19819881115'),(96,'2024-03-05 13:53:21','教师工号6','123456','教师姓名6','file/jiaoshiTouxiang6.jpg','男','19819881116'),(1709646985612,'2024-03-05 13:56:25','3','3','3老师','file/1709646977986.jpg','女','13622224445');
/*!40000 ALTER TABLE `jiaoshi` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `menu`
--

DROP TABLE IF EXISTS `menu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `menu` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `menujson` longtext COMMENT '菜单',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='菜单';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `menu`
--

LOCK TABLES `menu` WRITE;
/*!40000 ALTER TABLE `menu` DISABLE KEYS */;
INSERT INTO `menu` VALUES (1,'2024-03-05 13:53:21','[{\"backMenu\":[{\"child\":[{\"appFrontIcon\":\"cuIcon-time\",\"buttons\":[\"新增\",\"查看\",\"修改\",\"删除\"],\"menu\":\"公告版\",\"menuJump\":\"列表\",\"tableName\":\"news\"}],\"fontClass\":\"icon-common44\",\"menu\":\"公告版管理\",\"unicode\":\"&#xef28;\"},{\"child\":[{\"appFrontIcon\":\"cuIcon-keyboard\",\"buttons\":[\"新增\",\"查看\",\"修改\",\"删除\"],\"menu\":\"管理员\",\"menuJump\":\"列表\",\"tableName\":\"users\"},{\"appFrontIcon\":\"cuIcon-news\",\"buttons\":[\"新增\",\"查看\",\"修改\",\"删除\"],\"menu\":\"学生\",\"menuJump\":\"列表\",\"tableName\":\"xuesheng\"},{\"appFrontIcon\":\"cuIcon-keyboard\",\"buttons\":[\"新增\",\"查看\",\"修改\",\"删除\"],\"menu\":\"家长\",\"menuJump\":\"列表\",\"tableName\":\"jiazhang\"},{\"appFrontIcon\":\"cuIcon-camera\",\"buttons\":[\"新增\",\"查看\",\"修改\",\"删除\"],\"menu\":\"教师\",\"menuJump\":\"列表\",\"tableName\":\"jiaoshi\"}],\"fontClass\":\"icon-common27\",\"menu\":\"管理员管理\",\"unicode\":\"&#xee2c;\"},{\"child\":[{\"appFrontIcon\":\"cuIcon-vipcard\",\"buttons\":[\"查看\",\"成绩统计\",\"首页总数\",\"首页统计\"],\"menu\":\"学生成绩\",\"menuJump\":\"列表\",\"tableName\":\"xueshengchengji\"}],\"fontClass\":\"icon-common40\",\"menu\":\"学生成绩管理\",\"unicode\":\"&#xeebb;\"}],\"frontMenu\":[],\"hasBackLogin\":\"是\",\"hasBackRegister\":\"否\",\"hasFrontLogin\":\"否\",\"hasFrontRegister\":\"否\",\"roleName\":\"管理员\",\"tableName\":\"users\"},{\"backMenu\":[{\"child\":[{\"appFrontIcon\":\"cuIcon-time\",\"buttons\":[\"查看\"],\"menu\":\"公告版\",\"menuJump\":\"列表\",\"tableName\":\"news\"}],\"fontClass\":\"icon-common44\",\"menu\":\"公告版管理\",\"unicode\":\"&#xef28;\"},{\"child\":[{\"appFrontIcon\":\"cuIcon-addressbook\",\"buttons\":[\"查看\"],\"menu\":\"试卷列表\",\"menuJump\":\"12\",\"tableName\":\"exampaper\"},{\"appFrontIcon\":\"cuIcon-present\",\"buttons\":[\"查看\"],\"menu\":\"考试记录\",\"tableName\":\"examrecord\"},{\"appFrontIcon\":\"cuIcon-present\",\"buttons\":[\"查看\"],\"menu\":\"错题本\",\"menuJump\":\"22\",\"tableName\":\"examrecord\"}],\"fontClass\":\"icon-common25\",\"menu\":\"考试管理\",\"unicode\":\"&#xee09;\"},{\"child\":[{\"appFrontIcon\":\"cuIcon-vipcard\",\"buttons\":[\"查看\"],\"menu\":\"学生成绩\",\"menuJump\":\"列表\",\"tableName\":\"xueshengchengji\"}],\"fontClass\":\"icon-common40\",\"menu\":\"学生成绩管理\",\"unicode\":\"&#xeebb;\"}],\"frontMenu\":[],\"hasBackLogin\":\"是\",\"hasBackRegister\":\"是\",\"hasFrontLogin\":\"否\",\"hasFrontRegister\":\"否\",\"roleName\":\"学生\",\"tableName\":\"xuesheng\"},{\"backMenu\":[{\"child\":[{\"appFrontIcon\":\"cuIcon-time\",\"buttons\":[\"查看\"],\"menu\":\"公告版\",\"menuJump\":\"列表\",\"tableName\":\"news\"}],\"fontClass\":\"icon-common44\",\"menu\":\"公告版管理\",\"unicode\":\"&#xef28;\"},{\"child\":[{\"appFrontIcon\":\"cuIcon-vipcard\",\"buttons\":[\"查看\"],\"menu\":\"学生成绩\",\"menuJump\":\"列表\",\"tableName\":\"xueshengchengji\"}],\"fontClass\":\"icon-common40\",\"menu\":\"学生成绩管理\",\"unicode\":\"&#xeebb;\"}],\"frontMenu\":[],\"hasBackLogin\":\"是\",\"hasBackRegister\":\"是\",\"hasFrontLogin\":\"否\",\"hasFrontRegister\":\"否\",\"roleName\":\"家长\",\"tableName\":\"jiazhang\"},{\"backMenu\":[{\"child\":[{\"appFrontIcon\":\"cuIcon-time\",\"buttons\":[\"查看\"],\"menu\":\"公告版\",\"menuJump\":\"列表\",\"tableName\":\"news\"}],\"fontClass\":\"icon-common44\",\"menu\":\"公告版管理\",\"unicode\":\"&#xef28;\"},{\"child\":[{\"appFrontIcon\":\"cuIcon-news\",\"buttons\":[\"查看\"],\"menu\":\"学生\",\"menuJump\":\"列表\",\"tableName\":\"xuesheng\"},{\"appFrontIcon\":\"cuIcon-keyboard\",\"buttons\":[\"查看\"],\"menu\":\"家长\",\"menuJump\":\"列表\",\"tableName\":\"jiazhang\"}],\"fontClass\":\"icon-common27\",\"menu\":\"管理员管理\",\"unicode\":\"&#xee2c;\"},{\"child\":[{\"appFrontIcon\":\"cuIcon-phone\",\"buttons\":[\"新增\",\"查看\",\"修改\",\"删除\",\"打印\",\"导出\"],\"menu\":\"试题管理\",\"menuJump\":\"列表\",\"tableName\":\"examquestion\"}],\"fontClass\":\"icon-common25\",\"menu\":\"试题管理\",\"unicode\":\"&#xee09;\"},{\"child\":[{\"appFrontIcon\":\"cuIcon-addressbook\",\"buttons\":[\"查看\",\"组卷\",\"删除\",\"修改\",\"新增\"],\"menu\":\"试卷管理\",\"tableName\":\"exampaper\"}],\"fontClass\":\"icon-common39\",\"menu\":\"试卷管理\",\"unicode\":\"&#xeeba;\"},{\"child\":[{\"appFrontIcon\":\"cuIcon-addressbook\",\"buttons\":[\"查看\"],\"menu\":\"试卷列表\",\"menuJump\":\"12\",\"tableName\":\"exampaper\"},{\"appFrontIcon\":\"cuIcon-present\",\"buttons\":[\"查看\",\"批卷\"],\"menu\":\"考试记录\",\"tableName\":\"examrecord\"},{\"appFrontIcon\":\"cuIcon-present\",\"buttons\":[\"查看\"],\"menu\":\"错题本\",\"menuJump\":\"22\",\"tableName\":\"examrecord\"}],\"fontClass\":\"icon-common25\",\"menu\":\"考试管理\",\"unicode\":\"&#xee09;\"},{\"child\":[{\"appFrontIcon\":\"cuIcon-vipcard\",\"buttons\":[\"查看\",\"新增\",\"修改\",\"删除\",\"成绩统计\",\"首页总数\",\"首页统计\"],\"menu\":\"学生成绩\",\"menuJump\":\"列表\",\"tableName\":\"xueshengchengji\"}],\"fontClass\":\"icon-common40\",\"menu\":\"学生成绩管理\",\"unicode\":\"&#xeebb;\"}],\"frontMenu\":[],\"hasBackLogin\":\"是\",\"hasBackRegister\":\"是\",\"hasFrontLogin\":\"否\",\"hasFrontRegister\":\"否\",\"roleName\":\"教师\",\"tableName\":\"jiaoshi\"}]');
/*!40000 ALTER TABLE `menu` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `news`
--

DROP TABLE IF EXISTS `news`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `news` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `title` varchar(200) NOT NULL COMMENT '标题',
  `introduction` longtext COMMENT '简介',
  `picture` longtext NOT NULL COMMENT '图片',
  `content` longtext NOT NULL COMMENT '内容',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1709647051172 DEFAULT CHARSET=utf8 COMMENT='公告版';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `news`
--

LOCK TABLES `news` WRITE;
/*!40000 ALTER TABLE `news` DISABLE KEYS */;
INSERT INTO `news` VALUES (51,'2024-03-05 13:53:21','青花瓷','素胚勾勒出青花笔锋浓转淡，瓶身描绘的牡丹一如你初妆，冉冉檀香透过窗心事我了然，宣上走笔至此搁一半，釉色渲染仕女图韵味被私藏，而你嫣然的一笑如含苞待放，你的美缕飘散，去到我去不了的地方，天青色等烟雨 而我在等你，炊烟袅袅升起','file/newsPicture1.jpg','素胚勾勒出青花笔锋浓转淡，瓶身描绘的牡丹一如你初妆，冉冉檀香透过窗心事我了然，宣上走笔至此搁一半，釉色渲染仕女图韵味被私藏，而你嫣然的一笑如含苞待放，你的美缕飘散，去到我去不了的地方，天青色等烟雨 而我在等你，炊烟袅袅升起， 隔江千万里，在瓶底书前朝的飘逸，就当我为遇见你伏笔，天青色等烟雨， 而我在等你，月色被打捞起， 晕开了局，如传世的青花瓷自顾自美丽，你眼带笑意，色白花青的锦鲤跃然于碗底，临摹宋体落款时却惦记着你，你隐藏在窑烧里千年的秘密，极细腻犹如绣花针落地，帘外芭蕉惹骤雨门环惹铜绿，而我路过那江南小镇惹了你，在泼墨山水画里，你从墨色深处被隐去，天青色等烟雨 ，而我在等你，炊烟袅袅升起 ，隔江千万里，在瓶底书汉隶仿前朝的飘逸，就当我为遇见你伏笔，天色等烟雨 ，而我在等你，月色被打捞起， 晕开了结局，如传世的青花瓷自顾自美丽，你眼带笑意，天青色等烟雨 ，而我在等你，炊烟袅袅升起 ，隔江千万里，在瓶底书汉隶仿前朝的飘逸，就当我为遇见你伏笔，天青色等烟雨， 而我在等你，月色被打捞起 ，晕开了结局，如传世的青花瓷自顾自美丽，你眼带笑意。'),(52,'2024-03-05 13:53:21','理想三旬','雨后有车驶来，驶过暮色苍白，旧铁皮往南开，恋人已不在，收听浓烟下的，诗歌电台，不动情的咳嗽，至少看起来，归途也还可爱，琴弦少了姿态，再不见那夜里，听歌的小孩，时光匆匆独白，将颠沛磨成卡带，已枯倦的情怀，踏碎成年代，就老去吧，孤独别醒来，你渴望的离开，只是无处停摆，就歌唱吧，眼睛眯起来，而热泪的崩坏，只是没抵达的存在','file/newsPicture2.jpg','雨后有车驶来，驶过暮色苍白，旧铁皮往南开，恋人已不在，收听浓烟下的，诗歌电台，不动情的咳嗽，至少看起来，归途也还可爱，琴弦少了姿态，再不见那夜里，听歌的小孩，时光匆匆独白，将颠沛磨成卡带，已枯倦的情怀，踏碎成年代，就老去吧，孤独别醒来，你渴望的离开，只是无处停摆，就歌唱吧，眼睛眯起来，而热泪的崩坏，只是没抵达的存在，青春又醉倒在，籍籍无名的怀，靠嬉笑来虚度，聚散得慷慨，辗转却去不到，对的站台，如果漂泊是成长，必经的路牌，你迷醒岁月中，那贫瘠的未来，像遗憾季节里，未结果的爱，弄脏了每一页诗，吻最疼痛的告白，而风声吹到这，已不需要释怀，就老去吧，孤独别醒来，渴望的离开只是无处停摆就歌唱吧，眼睛眯起来而热泪的崩坏，只是没抵达的存在，就甜蜜地忍耐，繁星润湿窗台，光影跳动着像在，困倦里说爱，再无谓的感慨，以为明白，梦倒塌的地方，今已爬满青苔。'),(53,'2024-03-05 13:53:21','七里香','窗外的麻雀在电线杆上多嘴，你说这一句很有夏天的感觉，手中的铅笔在纸上来来回回，我用几行字形容你是我的谁，秋刀鱼的滋味猫跟你都想了解，初恋的香味就这样被我们寻回，那温暖的阳光像刚摘的鲜艳草莓，你说你舍不得吃掉这一种感觉，雨下整夜我的爱溢出就像雨水，院子落叶跟我的思念厚厚一叠','file/newsPicture3.jpg','窗外的麻雀在电线杆上多嘴，你说这一句很有夏天的感觉，手中的铅笔在纸上来来回回，我用几行字形容你是我的谁，秋刀鱼的滋味猫跟你都想了解，初恋的香味就这样被我们寻回，那温暖的阳光像刚摘的鲜艳草莓，你说你舍不得吃掉这一种感觉，雨下整夜我的爱溢出就像雨水，院子落叶跟我的思念厚厚一叠，几句是非也无法将我的热情冷却，你出现在我诗的每一页，雨下整夜我的爱溢出就像雨水，窗台蝴蝶像诗里纷飞的美丽章节，我接着写，把永远爱你写进诗的结尾，你是我唯一想要的了解，雨下整夜我的爱溢出就像雨水，院子落叶跟我的思念厚厚一叠，几句是非也无法将我的热情冷却，你出现在我诗的每一页，那饱满的稻穗幸福了这个季节，而你的脸颊像田里熟透的番茄，你突然对我说七里香的名字很美，我此刻却只想亲吻你倔强的嘴，雨下整夜我的爱溢出就像雨水，院子落叶跟我的思念厚厚一叠，几句是非也无法将我的热情冷却，你出现在我诗的每一页，整夜我的爱溢出就像雨水，窗台蝴蝶像诗里纷飞的美丽章节，我接着写，把永远爱你写进诗的结尾，是我唯一想要的了解。'),(54,'2024-03-05 13:53:21','江南','风到这里就是粘，粘住过客的思念，雨到了这里缠成线，缠着我们流连人世间，你在身边就是缘，缘分写在三生石上面，爱有万分之一甜，宁愿我就葬在这一点，圈圈圆圆圈圈，天天年年天天的我，深深看你的脸，生气的温柔，埋怨的温柔的脸','file/newsPicture4.jpg','风到这里就是粘，粘住过客的思念，雨到了这里缠成线，缠着我们流连人世间，你在身边就是缘，缘分写在三生石上面，爱有万分之一甜，宁愿我就葬在这一点，圈圈圆圆圈圈，天天年年天天的我，深深看你的脸，生气的温柔，埋怨的温柔的脸，不懂爱恨情愁煎熬的我们，都以为相爱就像风云的善变，相信爱一天抵过永远，在这一刹那冻结了时间，不懂怎么表现温柔的我们，还以为殉情只是古老的传言，离愁能有多痛痛有多浓，当梦被埋在江南烟雨中，心碎了才懂，圈圈圆圆圈圈，天天年年天天的我，深深看你的脸，生气的温柔，埋怨的温柔的脸，不懂爱恨情愁煎熬的我们，都以为相爱就像风云的善变，相信爱一天 抵过永远，在这一刹那冻结了时间，不懂怎么表现温柔的我们，还以为殉情只是古老的传言，离愁能有多痛 痛有多浓，当梦被埋在江南烟雨中，心碎了才懂，相信爱一天抵过永远。在这一刹那冻结了时间，不懂怎么表现温柔的我们，还以为殉情只是古老的传言，离愁能有多痛 痛有多浓，当梦被埋在江南烟雨中，心碎了才懂。'),(55,'2024-03-05 13:53:21','那些你很冒险的梦','当两颗心开始震动，当你瞳孔学会闪躲，当爱慢慢被遮住只剩下黑，距离像影子被拉拖，当爱的故事剩听说，我找不到你单纯的面孔，当生命每分每秒都为你转动，心多执着就加倍心痛，那些你很冒险的梦， 我陪你去疯，折纸飞机碰到雨天终究会坠落','file/newsPicture5.jpg','当两颗心开始震动，当你瞳孔学会闪躲，当爱慢慢被遮住只剩下黑，距离像影子被拉拖，当爱的故事剩听说，我找不到你单纯的面孔，当生命每分每秒都为你转动，心多执着就加倍心痛，那些你很冒险的梦， 我陪你去疯，折纸飞机碰到雨天终究会坠落，太残忍的话我直说 因为爱很重，你却不想懂 只往反方向走，当爱的故事剩听说，我找不到你单纯的面孔，当生命每分每秒都为你转动，心有多执着就加倍心痛，那些你很冒险的梦 我陪你去疯，折纸飞机 碰到雨天 终究会坠落，太残忍的话我直说 因为爱很重，你却不想懂 只往反方向走，我不想放手 你松开的左手，你爱的放纵 我白不回天空，我输了 累了，但你再也 不回头，那些你很冒险的梦 我陪你去疯，折纸飞机 碰到雨天 终究会坠落，太残忍的话我直说 因为爱很重，你却不想懂 只往反方向走，你真的不懂 我的爱已降落。'),(56,'2024-03-05 13:53:21','孤勇者','都，是勇敢的，你额头的伤口 你的 不同 你犯的错，都 不必隐藏，你破旧的玩偶 你的 面具 你的自我，他们说 要带着光 驯服每一头怪兽，他们说 要缝好你的伤，没有人爱小丑 为何孤独 不可 光荣，人只有不完美 值得歌颂，谁说污泥满身的不算英雄，爱你孤身走暗巷，爱你不跪的模样，爱你对峙过绝望','file/newsPicture6.jpg','都，是勇敢的，你额头的伤口 你的 不同 你犯的错，都 不必隐藏，你破旧的玩偶 你的 面具 你的自我，他们说 要带着光 驯服每一头怪兽，他们说 要缝好你的伤，没有人爱小丑 为何孤独 不可 光荣，人只有不完美 值得歌颂，谁说污泥满身的不算英雄，爱你孤身走暗巷，爱你不跪的模样，爱你对峙过绝望，不肯哭一场，爱你破烂的衣裳，却敢堵命运的枪，爱你和我那么像，缺口都一样，去吗 配吗 这褴褛的披风，战吗 战啊 以最卑微的梦，致那黑夜中的呜咽与怒吼，谁说站在光里的才算英雄，他们说 要戒了你的狂，就像擦掉了污垢，他们说 要顺台阶而上，而代价是低头，那就让我 不可 乘风，你一样骄傲着 那种孤勇，谁说对弈平凡的不算英雄，爱你孤身走暗巷 爱你不跪的模样，爱你对峙过绝望 不肯哭一场，爱你破烂的衣裳 却敢堵命运的枪，爱你和我那么像 缺口都一样，去吗 配吗 这褴褛的披风，战吗 战啊 以最卑微的梦，致那黑夜中的呜咽与怒吼，谁说站在光里的才算英雄，你的斑驳 与众不同 与众不同，你的沉默 震耳欲聋 震耳欲聋，You Are The Hero，爱你孤身走暗巷 爱你不跪的模样，爱你对峙过绝望 不肯哭一场，爱你来自于蛮荒 一生不借谁的光，你将造你的城邦 在废墟之上，去吗 去啊 以最卑微的梦，战吗 战啊 以最孤高的梦，致那黑夜中的呜咽与怒吼，谁说站在光里的才算英雄。'),(1709647051171,'2024-03-05 13:57:31','1','1','file/1709647048724.jpg','<p>1</p>');
/*!40000 ALTER TABLE `news` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `token`
--

DROP TABLE IF EXISTS `token`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `token` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `userid` bigint(20) NOT NULL COMMENT '用户id',
  `username` varchar(100) NOT NULL COMMENT '用户名',
  `tablename` varchar(100) DEFAULT NULL COMMENT '表名',
  `role` varchar(100) DEFAULT NULL COMMENT '角色',
  `token` varchar(200) NOT NULL COMMENT '密码',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '新增时间',
  `expiratedtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '过期时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 COMMENT='token表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `token`
--

LOCK TABLES `token` WRITE;
/*!40000 ALTER TABLE `token` DISABLE KEYS */;
INSERT INTO `token` VALUES (1,1,'admin','users','管理员','8vdt8eq0akru319m7s3o7xjr8phk0iru','2024-03-05 13:57:19','2024-03-05 14:57:20'),(2,1709646985612,'3','jiaoshi','管理员','y93fyp994u3tkx7ay8n2gc412lt92ii5','2024-03-05 13:57:41','2024-03-05 14:59:02'),(3,1709647024367,'1','xuesheng','学生','nl9bpvlmyjvthrl5jlkwpwc3p6d52vqq','2024-03-05 13:58:16','2024-03-05 14:59:35');
/*!40000 ALTER TABLE `token` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `users` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `username` varchar(200) NOT NULL COMMENT '用户名',
  `password` varchar(200) NOT NULL COMMENT '密码',
  `role` varchar(200) DEFAULT NULL COMMENT '角色',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='管理员';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'2024-03-05 13:53:21','admin','admin','管理员');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `xuesheng`
--

DROP TABLE IF EXISTS `xuesheng`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `xuesheng` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `xuehao` varchar(200) NOT NULL COMMENT '学号',
  `mima` varchar(200) NOT NULL COMMENT '密码',
  `xingming` varchar(200) NOT NULL COMMENT '姓名',
  `touxiang` longtext COMMENT '头像',
  `xingbie` varchar(200) DEFAULT NULL COMMENT '性别',
  `lianxidianhua` varchar(200) DEFAULT NULL COMMENT '联系电话',
  `banji` varchar(200) DEFAULT NULL COMMENT '班级',
  PRIMARY KEY (`id`),
  UNIQUE KEY `xuehao` (`xuehao`)
) ENGINE=InnoDB AUTO_INCREMENT=1709647024368 DEFAULT CHARSET=utf8 COMMENT='学生';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `xuesheng`
--

LOCK TABLES `xuesheng` WRITE;
/*!40000 ALTER TABLE `xuesheng` DISABLE KEYS */;
INSERT INTO `xuesheng` VALUES (31,'2024-03-05 13:53:21','学号1','123456','姓名1','file/xueshengTouxiang1.jpg','男','19819881111','班级1'),(32,'2024-03-05 13:53:21','学号2','123456','姓名2','file/xueshengTouxiang2.jpg','男','19819881112','班级2'),(33,'2024-03-05 13:53:21','学号3','123456','姓名3','file/xueshengTouxiang3.jpg','男','19819881113','班级3'),(34,'2024-03-05 13:53:21','学号4','123456','姓名4','file/xueshengTouxiang4.jpg','男','19819881114','班级4'),(35,'2024-03-05 13:53:21','学号5','123456','姓名5','file/xueshengTouxiang5.jpg','男','19819881115','班级5'),(36,'2024-03-05 13:53:21','学号6','123456','姓名6','file/xueshengTouxiang6.jpg','男','19819881116','班级6'),(1709647024367,'2024-03-05 13:57:04','1','1','1','file/1709647017554.jpg','女','13622224445','101');
/*!40000 ALTER TABLE `xuesheng` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `xueshengchengji`
--

DROP TABLE IF EXISTS `xueshengchengji`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `xueshengchengji` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `kaoshichengji` double DEFAULT NULL COMMENT '考试成绩',
  `xuehao` varchar(200) DEFAULT NULL COMMENT '学号',
  `xingming` varchar(200) DEFAULT NULL COMMENT '姓名',
  `banji` varchar(200) DEFAULT NULL COMMENT '班级',
  `jiaoshigonghao` varchar(200) DEFAULT NULL COMMENT '教师工号',
  `jiaoshixingming` varchar(200) DEFAULT NULL COMMENT '教师姓名',
  `tianjiariqi` datetime DEFAULT NULL COMMENT '添加日期',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1709647160180 DEFAULT CHARSET=utf8 COMMENT='学生成绩';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `xueshengchengji`
--

LOCK TABLES `xueshengchengji` WRITE;
/*!40000 ALTER TABLE `xueshengchengji` DISABLE KEYS */;
INSERT INTO `xueshengchengji` VALUES (101,'2024-03-05 13:53:21',1,'学号1','姓名1','班级1','教师工号1','教师姓名1','2024-03-05 21:53:21'),(102,'2024-03-05 13:53:21',2,'学号2','姓名2','班级2','教师工号2','教师姓名2','2024-03-05 21:53:21'),(103,'2024-03-05 13:53:21',3,'学号3','姓名3','班级3','教师工号3','教师姓名3','2024-03-05 21:53:21'),(104,'2024-03-05 13:53:21',4,'学号4','姓名4','班级4','教师工号4','教师姓名4','2024-03-05 21:53:21'),(105,'2024-03-05 13:53:21',5,'学号5','姓名5','班级5','教师工号5','教师姓名5','2024-03-05 21:53:21'),(106,'2024-03-05 13:53:21',6,'学号6','姓名6','班级6','教师工号6','教师姓名6','2024-03-05 21:53:21'),(1709647160179,'2024-03-05 13:59:20',50,'1','1','101','3','3老师','2024-03-05 21:59:07');
/*!40000 ALTER TABLE `xueshengchengji` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `homework`
--

DROP TABLE IF EXISTS `homework`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `homework` (
  `id` bigint(20) NOT NULL COMMENT '主键',
  `content` longtext NOT NULL COMMENT '作业内容',
  `is_permanent` tinyint(1) DEFAULT '0' COMMENT '是否永久保存',
  `attachment_url` varchar(500) DEFAULT NULL COMMENT '附件地址',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_homework_create_time` (`create_time`),
  KEY `idx_homework_is_permanent` (`is_permanent`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='作业';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `homework`
--

LOCK TABLES `homework` WRITE;
/*!40000 ALTER TABLE `homework` DISABLE KEYS */;
/*!40000 ALTER TABLE `homework` ENABLE KEYS */;
UNLOCK TABLES;
--
-- Table structure for table `jiazhangjiaoshigoutong`
--

DROP TABLE IF EXISTS `jiazhangjiaoshigoutong`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `jiazhangjiaoshigoutong` (
  `id` bigint(20) NOT NULL COMMENT '主键',
  `addtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `from_role` varchar(50) NOT NULL COMMENT '发送方角色',
  `from_id` bigint(20) NOT NULL COMMENT '发送方id',
  `from_name` varchar(200) DEFAULT NULL COMMENT '发送方名称',
  `to_role` varchar(50) NOT NULL COMMENT '接收方角色',
  `to_id` bigint(20) NOT NULL COMMENT '接收方id',
  `to_name` varchar(200) DEFAULT NULL COMMENT '接收方名称',
  `content` longtext NOT NULL COMMENT '沟通内容',
  `is_read` tinyint(1) DEFAULT '0' COMMENT '是否已读',
  PRIMARY KEY (`id`),
  KEY `idx_jzg_from` (`from_role`,`from_id`),
  KEY `idx_jzg_to` (`to_role`,`to_id`),
  KEY `idx_jzg_addtime` (`addtime`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='家长教师沟通';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `jiazhangjiaoshigoutong`
--

LOCK TABLES `jiazhangjiaoshigoutong` WRITE;
/*!40000 ALTER TABLE `jiazhangjiaoshigoutong` DISABLE KEYS */;
/*!40000 ALTER TABLE `jiazhangjiaoshigoutong` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `parent_meeting`
--

DROP TABLE IF EXISTS `parent_meeting`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `parent_meeting` (
  `id` bigint(20) NOT NULL COMMENT '主键',
  `meeting_type` tinyint(2) NOT NULL DEFAULT '1' COMMENT '会议类型：1家长一对一预约 2教师群体家长会',
  `title` varchar(200) DEFAULT NULL COMMENT '会议标题',
  `creator_role` varchar(20) NOT NULL COMMENT '创建者角色：parent/teacher',
  `creator_id` bigint(20) NOT NULL COMMENT '创建者ID',
  `parent_id` bigint(20) DEFAULT NULL COMMENT '家长ID（单聊）',
  `teacher_id` bigint(20) NOT NULL COMMENT '教师ID',
  `meeting_time` datetime NOT NULL COMMENT '会议时间',
  `status` tinyint(2) NOT NULL DEFAULT '0' COMMENT '状态：0待确认 1已确认 2已完成 3已取消',
  `room_id` varchar(100) DEFAULT NULL COMMENT '会议室ID',
  `scope` varchar(20) NOT NULL DEFAULT 'single' COMMENT '范围：single/group',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_pm_parent_time` (`parent_id`,`meeting_time`),
  KEY `idx_pm_teacher_time` (`teacher_id`,`meeting_time`),
  KEY `idx_pm_status` (`status`),
  KEY `idx_pm_creator` (`creator_role`,`creator_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='在线家长会/会议预约';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `parent_meeting`
--

LOCK TABLES `parent_meeting` WRITE;
/*!40000 ALTER TABLE `parent_meeting` DISABLE KEYS */;
INSERT INTO `parent_meeting` (`id`,`meeting_type`,`title`,`creator_role`,`creator_id`,`parent_id`,`teacher_id`,`meeting_time`,`status`,`room_id`,`scope`,`create_time`) VALUES
(1713274800001,1,'一对一家长沟通','parent',1709647024367,1709647024367,91,DATE_FORMAT(DATE_ADD(CURDATE(), INTERVAL (7 - WEEKDAY(CURDATE())) DAY), '%Y-%m-%d 19:00:00'),0,'ROOM-WEEK-01','single',NOW()),
(1713274800002,1,'一对一家长沟通','parent',1709647024367,1709647024367,92,DATE_FORMAT(DATE_ADD(CURDATE(), INTERVAL (8 - WEEKDAY(CURDATE())) DAY), '%Y-%m-%d 10:00:00'),0,'ROOM-WEEK-02','single',NOW()),
(1713274800003,1,'一对一家长沟通','parent',1709647024367,1709647024367,93,DATE_FORMAT(DATE_ADD(CURDATE(), INTERVAL (9 - WEEKDAY(CURDATE())) DAY), '%Y-%m-%d 19:30:00'),0,'ROOM-WEEK-03','single',NOW()),
(1713274800004,2,'本周班级家长会','teacher',91,NULL,91,DATE_FORMAT(DATE_ADD(CURDATE(), INTERVAL (10 - WEEKDAY(CURDATE())) DAY), '%Y-%m-%d 20:00:00'),0,'ROOM-GROUP-01','group',NOW());
/*!40000 ALTER TABLE `parent_meeting` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `parent_meeting_attendee`
--

DROP TABLE IF EXISTS `parent_meeting_attendee`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `parent_meeting_attendee` (
  `id` bigint(20) NOT NULL COMMENT '主键',
  `meeting_id` bigint(20) NOT NULL COMMENT '会议ID',
  `parent_id` bigint(20) NOT NULL COMMENT '家长ID',
  `join_status` tinyint(2) NOT NULL DEFAULT '0' COMMENT '参会状态：0待确认 1已确认 2已拒绝',
  `confirm_time` datetime DEFAULT NULL COMMENT '确认时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_meeting_parent` (`meeting_id`,`parent_id`),
  KEY `idx_pma_parent` (`parent_id`),
  KEY `idx_pma_status` (`join_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='会议参会家长';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `parent_meeting_attendee`
--

LOCK TABLES `parent_meeting_attendee` WRITE;
/*!40000 ALTER TABLE `parent_meeting_attendee` DISABLE KEYS */;
INSERT INTO `parent_meeting_attendee` (`id`,`meeting_id`,`parent_id`,`join_status`,`confirm_time`,`create_time`) VALUES
(1713275800001,1713274800001,1709647024367,0,NULL,NOW()),
(1713275800002,1713274800002,1709647024367,0,NULL,NOW()),
(1713275800003,1713274800003,1709647024367,0,NULL,NOW()),
(1713275800004,1713274800004,1709647024367,0,NULL,NOW());
/*!40000 ALTER TABLE `parent_meeting_attendee` ENABLE KEYS */;
UNLOCK TABLES;

/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-03-09  8:17:34cl21002101
