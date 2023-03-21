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

-- mysql8的递归查询语法
with recursive t(temp_id,temp_parent_id,temp_name ) as (
    select * from test_recursive where parent_id = 0
    union all
    select o.id,o.parent_id,o.`name` from t,test_recursive o where o.parent_id = t.temp_id
)
select * from t;

SELECT GROUP_CONCAT(`COLUMN_NAME` SEPARATOR ",") FROM information_schema.`COLUMNS`
WHERE TABLE_SCHEMA = 'springcloud' AND TABLE_NAME = 'test_recursive'