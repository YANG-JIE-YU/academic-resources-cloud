/*
 Navicat Premium Data Transfer

 Source Server         : 本地8.0
 Source Server Type    : MySQL
 Source Server Version : 80038 (8.0.38)
 Source Host           : localhost:3306
 Source Schema         : academic_cloud_ai_db

 Target Server Type    : MySQL
 Target Server Version : 80038 (8.0.38)
 File Encoding         : 65001

 Date: 27/03/2026 19:06:47
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for ai_conversation_log
-- ----------------------------
DROP TABLE IF EXISTS `ai_conversation_log`;
CREATE TABLE `ai_conversation_log`  (
                                        `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
                                        `user_id` bigint NULL DEFAULT NULL COMMENT 'User ID',
                                        `question` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'User question',
                                        `answer` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT 'Model answer',
                                        `model_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'Model name',
                                        `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Created time',
                                        PRIMARY KEY (`id`) USING BTREE,
                                        INDEX `idx_ai_conversation_log_user_id`(`user_id` ASC) USING BTREE,
                                        INDEX `idx_ai_conversation_log_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'AI conversation logs' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of ai_conversation_log
-- ----------------------------
INSERT INTO `ai_conversation_log` VALUES (1, 2, '你好', '这是学术助手的演示回复：你可以继续提问论文选题、摘要优化、关键词提取与文献检索。', 'rule-based-demo', '2026-03-27 16:26:41');
INSERT INTO `ai_conversation_log` VALUES (2, 2, '我想学习一下', '这是学术助手的演示回复：你可以继续提问论文选题、摘要优化、关键词提取与文献检索。', 'rule-based-demo', '2026-03-27 16:27:23');
INSERT INTO `ai_conversation_log` VALUES (3, 2, '你好', 'Academic assistant demo reply: keep asking about topic selection, summary polishing, keywords and retrieval.', 'rule-based-demo', '2026-03-27 16:48:21');
INSERT INTO `ai_conversation_log` VALUES (4, 2, '你好啊', 'Academic assistant demo reply: keep asking about topic selection, summary polishing, keywords and retrieval.', 'rule-based-demo', '2026-03-27 16:48:54');
INSERT INTO `ai_conversation_log` VALUES (5, 2, '你好啊', 'Academic assistant demo reply: keep asking about topic selection, summary polishing, keywords and retrieval.', 'rule-based-demo', '2026-03-27 16:50:45');
INSERT INTO `ai_conversation_log` VALUES (6, 2, '你好，测试流式显示', 'Academic assistant demo reply: keep asking about topic selection, summary polishing, keywords and retrieval.', 'rule-based-demo', '2026-03-27 16:52:08');
INSERT INTO `ai_conversation_log` VALUES (7, 2, '你好啊', '你好！很高兴为你服务。\n\n我是学术门户助手，可以帮助你：\n\n- **学术资源查询**：文献检索、数据库使用\n- **论文写作指导**：格式规范、结构建议\n- **学术信息咨询**：专业介绍、院校信息\n- **日常学习问题**：解答疑惑、提供建议\n\n有什么学术方面的问题需要帮忙吗？', 'MiniMax-M2.7', '2026-03-27 16:58:27');

-- ----------------------------
-- Table structure for ai_paper_task_log
-- ----------------------------
DROP TABLE IF EXISTS `ai_paper_task_log`;
CREATE TABLE `ai_paper_task_log`  (
                                      `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
                                      `user_id` bigint NULL DEFAULT NULL COMMENT 'User ID',
                                      `task_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'Task type: SUMMARY/KEYWORD',
                                      `input_text` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT 'Input text',
                                      `output_text` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT 'Output text',
                                      `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Created time',
                                      PRIMARY KEY (`id`) USING BTREE,
                                      INDEX `idx_ai_paper_task_log_user_id`(`user_id` ASC) USING BTREE,
                                      INDEX `idx_ai_paper_task_log_task_type`(`task_type` ASC) USING BTREE,
                                      INDEX `idx_ai_paper_task_log_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'AI paper-task logs' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of ai_paper_task_log
-- ----------------------------

-- ----------------------------
-- Table structure for ai_recommend_log
-- ----------------------------
DROP TABLE IF EXISTS `ai_recommend_log`;
CREATE TABLE `ai_recommend_log`  (
                                     `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
                                     `user_id` bigint NULL DEFAULT NULL COMMENT 'User ID',
                                     `scene` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'Recommendation scene',
                                     `trigger_text` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'Trigger text',
                                     `recommend_items` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT 'Recommendation payload JSON',
                                     `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Created time',
                                     PRIMARY KEY (`id`) USING BTREE,
                                     INDEX `idx_ai_recommend_log_user_id`(`user_id` ASC) USING BTREE,
                                     INDEX `idx_ai_recommend_log_scene`(`scene` ASC) USING BTREE,
                                     INDEX `idx_ai_recommend_log_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'AI recommendation logs' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of ai_recommend_log
-- ----------------------------
INSERT INTO `ai_recommend_log` VALUES (1, 1, 'HOME', 'latest', '[]', '2026-03-27 14:29:01');
INSERT INTO `ai_recommend_log` VALUES (2, 2, 'HOME', 'latest', '[{\"id\":23,\"title\":\"学术搜索日志驱动的查询改写研究\",\"abstractText\":\"利用检索日志中的点击与停留信号进行学术查询改写。\",\"keywords\":\"查询改写,搜索日志,学术检索,意图识别\",\"categoryId\":1,\"categoryName\":\"Computer Science\",\"publishTime\":\"2026-03-06T12:30:00\",\"coverUrl\":\"https://images.unsplash.com/photo-1484417894907-623942c8ee29\"},{\"id\":34,\"title\":\"社交媒体使用强度与大学生注意力分配\",\"abstractText\":\"研究社交媒体使用强度对大学生注意力分配与学习效率的影响。\",\"keywords\":\"社交媒体,注意力,大学生,行为实验\",\"categoryId\":6,\"categoryName\":\"Psychology\",\"publishTime\":\"2026-03-05T12:10:00\",\"coverUrl\":\"https://images.unsplash.com/photo-1521737604893-d14cc237f11d\"},{\"id\":10,\"title\":\"联邦学习在高校隐私数据分析中的应用探索\",\"abstractText\":\"探讨联邦学习在不共享原始数据前提下支持高校联合建模的可行性。\",\"keywords\":\"联邦学习,隐私保护,联合建模,高校数据\",\"categoryId\":1,\"categoryName\":\"Computer Science\",\"publishTime\":\"2026-03-01T17:25:00\",\"coverUrl\":\"https://images.unsplash.com/photo-1518770660439-4636190af475\"},{\"id\":37,\"title\":\"脑机接口信号解码算法的鲁棒性研究\",\"abstractText\":\"研究脑机接口信号解码算法在噪声环境下的鲁棒性表现。\",\"keywords\":\"脑机接口,信号解码,鲁棒性,时序建模\",\"categoryId\":7,\"categoryName\":\"Biomedical Engineering\",\"publishTime\":\"2026-02-27T17:40:00\",\"coverUrl\":\"https://images.unsplash.com/photo-1532094349884-543bc11b234d\"},{\"id\":20,\"title\":\"引文上下文增强的论文推荐算法\",\"abstractText\":\"提出利用引文上下文语义信息增强论文推荐效果的算法框架。\",\"keywords\":\"引文分析,论文推荐,语义表示,重排\",\"categoryId\":1,\"categoryName\":\"Computer Science\",\"publishTime\":\"2026-02-18T17:00:00\",\"coverUrl\":\"https://images.unsplash.com/photo-1504384308090-c894fdcc538d\"},{\"id\":31,\"title\":\"高校科研成果转化中的法律风险识别\",\"abstractText\":\"识别高校科研成果转化流程中的常见法律风险并提出应对策略。\",\"keywords\":\"科研转化,法律风险,知识产权,技术许可\",\"categoryId\":5,\"categoryName\":\"Law and Policy\",\"publishTime\":\"2026-02-11T16:00:00\",\"coverUrl\":\"https://images.unsplash.com/photo-1519337265831-281ec6cc8514\"},{\"id\":7,\"title\":\"大语言模型在论文摘要生成中的可控性研究\",\"abstractText\":\"围绕摘要长度、术语一致性和事实准确性评估大语言模型可控生成能力。\",\"keywords\":\"大语言模型,摘要生成,可控生成,学术写作\",\"categoryId\":1,\"categoryName\":\"Computer Science\",\"publishTime\":\"2026-02-05T10:50:00\",\"coverUrl\":\"https://images.unsplash.com/photo-1486312338219-ce68d2c6f44d\"},{\"id\":17,\"title\":\"学术资源门户中的个性化排序策略\",\"abstractText\":\"提出融合用户兴趣与内容时效性的个性化排序策略。\",\"keywords\":\"个性化排序,推荐系统,用户兴趣,时效性\",\"categoryId\":1,\"categoryName\":\"Computer Science\",\"publishTime\":\"2026-01-22T10:35:00\",\"coverUrl\":\"https://images.unsplash.com/photo-1496171367470-9ed9a91ea931\"},{\"id\":28,\"title\":\"在线教育资源推荐的冷启动问题研究\",\"abstractText\":\"针对新用户与新课程场景提出冷启动推荐策略。\",\"keywords\":\"在线教育,推荐系统,冷启动,资源分发\",\"categoryId\":4,\"categoryName\":\"Education Technology\",\"publishTime\":\"2026-01-14T09:35:00\",\"coverUrl\":\"https://images.unsplash.com/photo-1523240795612-9a054b0db644\"}]', '2026-03-27 16:26:55');
INSERT INTO `ai_recommend_log` VALUES (3, 2, 'HOME', '社交', '[{\"id\":34,\"title\":\"社交媒体使用强度与大学生注意力分配\",\"abstractText\":\"研究社交媒体使用强度对大学生注意力分配与学习效率的影响。\",\"keywords\":\"社交媒体,注意力,大学生,行为实验\",\"categoryId\":6,\"categoryName\":\"Psychology\",\"publishTime\":\"2026-03-05T12:10:00\",\"coverUrl\":\"https://images.unsplash.com/photo-1521737604893-d14cc237f11d\"}]', '2026-03-27 16:27:06');
INSERT INTO `ai_recommend_log` VALUES (4, 2, 'HOME', '学术搜索日志驱动的查询改写研究', '[{\"id\":23,\"title\":\"学术搜索日志驱动的查询改写研究\",\"abstractText\":\"利用检索日志中的点击与停留信号进行学术查询改写。\",\"keywords\":\"查询改写,搜索日志,学术检索,意图识别\",\"categoryId\":1,\"categoryName\":\"Computer Science\",\"publishTime\":\"2026-03-06T12:30:00\",\"coverUrl\":\"https://images.unsplash.com/photo-1484417894907-623942c8ee29\"}]', '2026-03-27 16:33:50');
INSERT INTO `ai_recommend_log` VALUES (5, 2, 'HOME', '学术搜索日志驱动的查询改写研究', '[{\"id\":23,\"title\":\"学术搜索日志驱动的查询改写研究\",\"abstractText\":\"利用检索日志中的点击与停留信号进行学术查询改写。\",\"keywords\":\"查询改写,搜索日志,学术检索,意图识别\",\"categoryId\":1,\"categoryName\":\"Computer Science\",\"publishTime\":\"2026-03-06T12:30:00\",\"coverUrl\":\"https://images.unsplash.com/photo-1484417894907-623942c8ee29\"}]', '2026-03-27 16:33:54');
INSERT INTO `ai_recommend_log` VALUES (6, 2, 'HOME', '学术搜索日志驱动的查询改写研究', '[{\"id\":23,\"title\":\"学术搜索日志驱动的查询改写研究\",\"abstractText\":\"利用检索日志中的点击与停留信号进行学术查询改写。\",\"keywords\":\"查询改写,搜索日志,学术检索,意图识别\",\"categoryId\":1,\"categoryName\":\"Computer Science\",\"publishTime\":\"2026-03-06T12:30:00\",\"coverUrl\":\"https://images.unsplash.com/photo-1484417894907-623942c8ee29\"}]', '2026-03-27 16:33:56');
INSERT INTO `ai_recommend_log` VALUES (7, 2, 'HOME', '学术搜索日志驱动的查询改写研究', '[{\"id\":23,\"title\":\"学术搜索日志驱动的查询改写研究\",\"abstractText\":\"利用检索日志中的点击与停留信号进行学术查询改写。\",\"keywords\":\"查询改写,搜索日志,学术检索,意图识别\",\"categoryId\":1,\"categoryName\":\"Computer Science\",\"publishTime\":\"2026-03-06T12:30:00\",\"coverUrl\":\"https://images.unsplash.com/photo-1484417894907-623942c8ee29\"}]', '2026-03-27 16:34:01');
INSERT INTO `ai_recommend_log` VALUES (8, 2, 'HOME', '学术搜索日志驱动的查询改写研究', '[{\"id\":23,\"title\":\"学术搜索日志驱动的查询改写研究\",\"abstractText\":\"利用检索日志中的点击与停留信号进行学术查询改写。\",\"keywords\":\"查询改写,搜索日志,学术检索,意图识别\",\"categoryId\":1,\"categoryName\":\"Computer Science\",\"publishTime\":\"2026-03-06T12:30:00\",\"coverUrl\":\"https://images.unsplash.com/photo-1484417894907-623942c8ee29\"}]', '2026-03-27 16:34:05');
INSERT INTO `ai_recommend_log` VALUES (9, 2, 'HOME', '学术搜索日志驱动的查询改写研究', '[{\"id\":23,\"title\":\"学术搜索日志驱动的查询改写研究\",\"abstractText\":\"利用检索日志中的点击与停留信号进行学术查询改写。\",\"keywords\":\"查询改写,搜索日志,学术检索,意图识别\",\"categoryId\":1,\"categoryName\":\"Computer Science\",\"publishTime\":\"2026-03-06T12:30:00\",\"coverUrl\":\"https://images.unsplash.com/photo-1484417894907-623942c8ee29\"}]', '2026-03-27 16:34:12');
INSERT INTO `ai_recommend_log` VALUES (10, 2, 'HOME', '社交媒体使用强度与大学生注意力分配', '[{\"id\":34,\"title\":\"社交媒体使用强度与大学生注意力分配\",\"abstractText\":\"研究社交媒体使用强度对大学生注意力分配与学习效率的影响。\",\"keywords\":\"社交媒体,注意力,大学生,行为实验\",\"categoryId\":6,\"categoryName\":\"Psychology\",\"publishTime\":\"2026-03-05T12:10:00\",\"coverUrl\":\"https://images.unsplash.com/photo-1521737604893-d14cc237f11d\"}]', '2026-03-27 16:34:24');

SET FOREIGN_KEY_CHECKS = 1;
