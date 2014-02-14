-- 1
DROP TABLE IF EXISTS `pull_send_message`;
DROP TABLE IF EXISTS `pull_message`;

CREATE TABLE `pull_message` (
  `messageId` bigint(20) NOT NULL AUTO_INCREMENT,
  `senderId` bigint(20) DEFAULT NULL COMMENT '发送人主键',
  `content` text NOT NULL,
  `sender` varchar(64) NOT NULL,
  `msgType` smallint(6) NOT NULL COMMENT '1=个人信息\r\n            2=日程安排\r\n            3=计划任务\r\n            ',
  `sendTime` datetime NOT NULL,
  `typeStr$type` varchar(32) NOT NULL COMMENT '消息类别',
  `messageUUID` varchar(100) DEFAULT NULL COMMENT '发送消息的uuid 保证推送消息的完整性',
  PRIMARY KEY (`messageId`),
  KEY `FK_SM_R_AU_PULL` (`senderId`),
  CONSTRAINT `FK_SM_R_AU_PULL` FOREIGN KEY (`senderId`) REFERENCES `app_user` (`userId`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8 COMMENT='推送消息';

CREATE TABLE `pull_send_message` (
  `receiveId` bigint(20) NOT NULL AUTO_INCREMENT,
  `messageId` bigint(20) DEFAULT NULL,
  `userId` bigint(20) DEFAULT NULL COMMENT '主键',
  `readFlag` smallint(6) NOT NULL COMMENT '1=has red\r\n            0=unread',
  `delFlag` smallint(6) NOT NULL,
  `userFullname` varchar(32) NOT NULL,
  PRIMARY KEY (`receiveId`),
  KEY `FK_IM_R_AU_PULL` (`userId`),
  KEY `FK_IM_R_SM_PULL` (`messageId`),
  CONSTRAINT `FK_IM_R_AU_PULL` FOREIGN KEY (`userId`) REFERENCES `app_user` (`userId`),
  CONSTRAINT `FK_IM_R_SM_PULL` FOREIGN KEY (`messageId`) REFERENCES `pull_message` (`messageId`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8 COMMENT='推送的消息';

-- 2
ALTER TABLE `pull_message`
ADD COLUMN `fileType`  varchar(20) NULL AFTER `senderId`,
ADD COLUMN `fileSize`  varchar(20) NULL AFTER `fileType`;

-- 3
ALTER TABLE `pull_message`
ADD COLUMN `roomId`  bigint NULL COMMENT '聊天室的id' AFTER `senderId`;

DROP TABLE IF EXISTS `pull_room`;
CREATE TABLE `pull_room` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `timeCreate` datetime DEFAULT NULL COMMENT '时间',
  `title` varchar(100) DEFAULT NULL COMMENT '标题',
  `userIdCreate` bigint(20) DEFAULT NULL COMMENT '创建人',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `pull_room_user`;
CREATE TABLE `pull_room_user` (
  `roomId` bigint(20) NOT NULL,
  `userFullName` varchar(20) DEFAULT NULL COMMENT '用户姓名',
  `userId` bigint(20) NOT NULL,
  `state` smallint(6) DEFAULT NULL COMMENT '设置消息状态',
  `timeImport` datetime DEFAULT NULL COMMENT '加入时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



