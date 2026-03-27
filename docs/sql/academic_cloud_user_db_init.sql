/*
 Navicat Premium Data Transfer

 Source Server         : 本地8.0
 Source Server Type    : MySQL
 Source Server Version : 80038 (8.0.38)
 Source Host           : localhost:3306
 Source Schema         : academic_cloud_user_db

 Target Server Type    : MySQL
 Target Server Version : 80038 (8.0.38)
 File Encoding         : 65001

 Date: 27/03/2026 19:06:26
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for uc_comment
-- ----------------------------
DROP TABLE IF EXISTS `uc_comment`;
CREATE TABLE `uc_comment`  (
                               `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
                               `user_id` bigint NOT NULL COMMENT 'User ID',
                               `target_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'Target type',
                               `target_id` bigint NOT NULL COMMENT 'Target ID',
                               `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'Comment content',
                               `reply_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT 'Admin reply',
                               `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Created time',
                               PRIMARY KEY (`id`) USING BTREE,
                               INDEX `idx_uc_comment_target`(`target_type` ASC, `target_id` ASC) USING BTREE,
                               INDEX `idx_uc_comment_user_id`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'Comments' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of uc_comment
-- ----------------------------
INSERT INTO `uc_comment` VALUES (1, 2, 'PAPER', 34, '1', NULL, '2026-03-27 16:26:03');
INSERT INTO `uc_comment` VALUES (2, 1, 'PAPER', 23, '111', NULL, '2026-03-27 16:29:18');
INSERT INTO `uc_comment` VALUES (3, 2, 'PAPER', 23, '好的好看', NULL, '2026-03-27 16:33:22');

-- ----------------------------
-- Table structure for uc_favorite
-- ----------------------------
DROP TABLE IF EXISTS `uc_favorite`;
CREATE TABLE `uc_favorite`  (
                                `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
                                `user_id` bigint NOT NULL COMMENT 'User ID',
                                `target_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'Target type, e.g. PAPER',
                                `target_id` bigint NOT NULL COMMENT 'Target ID',
                                `target_title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'Target title snapshot',
                                `target_cover` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'Target cover snapshot',
                                `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Created time',
                                PRIMARY KEY (`id`) USING BTREE,
                                INDEX `idx_uc_favorite_user_id`(`user_id` ASC) USING BTREE,
                                INDEX `idx_uc_favorite_target`(`target_type` ASC, `target_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'Favorites' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of uc_favorite
-- ----------------------------
INSERT INTO `uc_favorite` VALUES (1, 2, 'PAPER', 23, '学术搜索日志驱动的查询改写研究', 'https://images.unsplash.com/photo-1484417894907-623942c8ee29', '2026-03-27 16:33:23');
INSERT INTO `uc_favorite` VALUES (2, 2, 'PAPER', 34, '社交媒体使用强度与大学生注意力分配', 'https://images.unsplash.com/photo-1521737604893-d14cc237f11d', '2026-03-27 16:34:22');

-- ----------------------------
-- Table structure for uc_forum_post
-- ----------------------------
DROP TABLE IF EXISTS `uc_forum_post`;
CREATE TABLE `uc_forum_post`  (
                                  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
                                  `parent_id` bigint NULL DEFAULT 0 COMMENT '0 or null means root post',
                                  `user_id` bigint NOT NULL COMMENT 'User ID',
                                  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'Post title',
                                  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'Post content',
                                  `is_top` tinyint NOT NULL DEFAULT 0 COMMENT '1=top,0=normal',
                                  `status` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'NORMAL' COMMENT 'Post status',
                                  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Created time',
                                  PRIMARY KEY (`id`) USING BTREE,
                                  INDEX `idx_uc_forum_post_parent_id`(`parent_id` ASC) USING BTREE,
                                  INDEX `idx_uc_forum_post_user_id`(`user_id` ASC) USING BTREE,
                                  INDEX `idx_uc_forum_post_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'Forum posts and replies' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of uc_forum_post
-- ----------------------------
INSERT INTO `uc_forum_post` VALUES (1, 0, 1, '测试发布帖子', '测试发布帖子', 0, 'OPEN', '2026-03-27 14:31:07');
INSERT INTO `uc_forum_post` VALUES (2, 1, 1, 'RE:测试发布帖子', '66', 0, 'OPEN', '2026-03-27 14:38:32');

-- ----------------------------
-- Table structure for uc_role
-- ----------------------------
DROP TABLE IF EXISTS `uc_role`;
CREATE TABLE `uc_role`  (
                            `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
                            `code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'Role code, e.g. USER/ADMIN',
                            `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'Role name',
                            `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Created time',
                            PRIMARY KEY (`id`) USING BTREE,
                            UNIQUE INDEX `uk_uc_role_code`(`code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'Roles' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of uc_role
-- ----------------------------
INSERT INTO `uc_role` VALUES (1, 'USER', 'User', '2026-03-27 12:57:46');
INSERT INTO `uc_role` VALUES (2, 'ADMIN', 'Administrator', '2026-03-27 12:57:46');

-- ----------------------------
-- Table structure for uc_user
-- ----------------------------
DROP TABLE IF EXISTS `uc_user`;
CREATE TABLE `uc_user`  (
                            `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
                            `username` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'Login username',
                            `password` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'BCrypt password',
                            `real_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'Real name',
                            `phone` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'Phone number',
                            `gender` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'Gender',
                            `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'Avatar URL',
                            `status` tinyint NOT NULL DEFAULT 1 COMMENT '1=enabled,0=disabled',
                            `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Created time',
                            `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Updated time',
                            PRIMARY KEY (`id`) USING BTREE,
                            UNIQUE INDEX `uk_uc_user_username`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'Users' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of uc_user
-- ----------------------------
INSERT INTO `uc_user` VALUES (1, 'admin', '$2a$04$KljJDa/LK7QfDm0lF5OhuePhlPfjRH3tB2Wu351Uidz.oQGJXevPi', '超管', NULL, NULL, NULL, 1, '2026-03-27 14:18:00', '2026-03-27 14:18:00');
INSERT INTO `uc_user` VALUES (2, 'user1', '$2a$10$PZN4oGWId94HKBNHTAQ0d.vrsXXrK6FOThZcmyQ6vlwXv6SyWz/4y', '用户', NULL, NULL, NULL, 1, '2026-03-27 16:25:31', '2026-03-27 16:25:31');

-- ----------------------------
-- Table structure for uc_user_behavior
-- ----------------------------
DROP TABLE IF EXISTS `uc_user_behavior`;
CREATE TABLE `uc_user_behavior`  (
                                     `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
                                     `user_id` bigint NOT NULL COMMENT 'User ID',
                                     `behavior_type` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'Behavior type',
                                     `target_type` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'Target type',
                                     `target_id` bigint NULL DEFAULT NULL COMMENT 'Target ID',
                                     `metadata` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT 'Extended JSON data',
                                     `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Created time',
                                     PRIMARY KEY (`id`) USING BTREE,
                                     INDEX `idx_uc_user_behavior_user_id`(`user_id` ASC) USING BTREE,
                                     INDEX `idx_uc_user_behavior_type`(`behavior_type` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'User behavior logs' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of uc_user_behavior
-- ----------------------------
INSERT INTO `uc_user_behavior` VALUES (1, 1, 'FORUM_POST', 'forum', 1, '测试发布帖子', '2026-03-27 14:31:07');
INSERT INTO `uc_user_behavior` VALUES (2, 1, 'FORUM_REPLY', 'forum', 1, '66', '2026-03-27 14:38:32');
INSERT INTO `uc_user_behavior` VALUES (3, 2, 'COMMENT', 'PAPER', 34, '1', '2026-03-27 16:26:03');
INSERT INTO `uc_user_behavior` VALUES (4, 1, 'COMMENT', 'PAPER', 23, '111', '2026-03-27 16:29:18');
INSERT INTO `uc_user_behavior` VALUES (5, 2, 'COMMENT', 'PAPER', 23, '好的好看', '2026-03-27 16:33:22');
INSERT INTO `uc_user_behavior` VALUES (6, 2, 'FAVORITE', 'PAPER', 23, '学术搜索日志驱动的查询改写研究', '2026-03-27 16:33:23');
INSERT INTO `uc_user_behavior` VALUES (7, 2, 'FAVORITE', 'PAPER', 34, '社交媒体使用强度与大学生注意力分配', '2026-03-27 16:34:22');

-- ----------------------------
-- Table structure for uc_user_role
-- ----------------------------
DROP TABLE IF EXISTS `uc_user_role`;
CREATE TABLE `uc_user_role`  (
                                 `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
                                 `user_id` bigint NOT NULL COMMENT 'User ID',
                                 `role_id` bigint NOT NULL COMMENT 'Role ID',
                                 `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Created time',
                                 PRIMARY KEY (`id`) USING BTREE,
                                 UNIQUE INDEX `uk_uc_user_role_user_role`(`user_id` ASC, `role_id` ASC) USING BTREE,
                                 INDEX `idx_uc_user_role_user_id`(`user_id` ASC) USING BTREE,
                                 INDEX `idx_uc_user_role_role_id`(`role_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'User-role mapping' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of uc_user_role
-- ----------------------------
INSERT INTO `uc_user_role` VALUES (1, 1, 2, '2026-03-27 14:33:05');
INSERT INTO `uc_user_role` VALUES (2, 2, 1, '2026-03-27 16:25:31');

SET FOREIGN_KEY_CHECKS = 1;
