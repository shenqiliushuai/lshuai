SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
     `id` bigint UNSIGNED NOT NULL AUTO_INCREMENT,
     `name` varchar(255)  NULL DEFAULT NULL,
     `password` varchar(255)  NULL DEFAULT NULL,
     PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, 'lshuai', 'lshuai');

SET FOREIGN_KEY_CHECKS = 1;