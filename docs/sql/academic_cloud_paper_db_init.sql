/*
 Navicat Premium Data Transfer

 Source Server         : 本地8.0
 Source Server Type    : MySQL
 Source Server Version : 80038 (8.0.38)
 Source Host           : localhost:3306
 Source Schema         : academic_cloud_paper_db

 Target Server Type    : MySQL
 Target Server Version : 80038 (8.0.38)
 File Encoding         : 65001

 Date: 27/03/2026 19:06:41
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for pc_activity
-- ----------------------------
DROP TABLE IF EXISTS `pc_activity`;
CREATE TABLE `pc_activity`  (
                                `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
                                `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'Activity title',
                                `location` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'Location',
                                `organizer` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'Organizer',
                                `start_time` datetime NULL DEFAULT NULL COMMENT 'Start time',
                                `end_time` datetime NULL DEFAULT NULL COMMENT 'End time',
                                `content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT 'Activity content',
                                `status` tinyint NOT NULL DEFAULT 1 COMMENT '1=published,0=offline',
                                `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Created time',
                                `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Updated time',
                                PRIMARY KEY (`id`) USING BTREE,
                                INDEX `idx_pc_activity_status_start`(`status` ASC, `start_time` ASC) USING BTREE,
                                INDEX `idx_pc_activity_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'Academic activities' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of pc_activity
-- ----------------------------
INSERT INTO `pc_activity` VALUES (1, '人工智能与教育创新论坛', '信息楼 A201', '计算机学院', '2026-04-02 14:00:00', '2026-04-02 17:00:00', '围绕AI在高校教学与科研中的应用开展主题报告与圆桌讨论。', 1, '2026-03-27 16:21:17', '2026-03-27 16:21:17');
INSERT INTO `pc_activity` VALUES (2, '学术论文写作训练营', '图书馆培训室', '研究生院', '2026-04-10 09:00:00', '2026-04-10 16:00:00', '从选题、方法设计、实验结果呈现到参考文献规范进行系统讲解。', 1, '2026-03-27 16:21:17', '2026-03-27 16:21:17');
INSERT INTO `pc_activity` VALUES (3, '跨学科研究方法分享会', '综合楼 301', '科研处', '2026-04-18 15:00:00', '2026-04-18 18:00:00', '邀请多学科教师分享跨学科项目设计、团队协作与论文产出经验。', 1, '2026-03-27 16:21:17', '2026-03-27 16:21:17');
INSERT INTO `pc_activity` VALUES (4, '文献管理工具实操讲座', '图书馆报告厅', '图书馆', '2026-04-22 19:00:00', '2026-04-22 20:30:00', '介绍常用文献管理工具在检索、标注、引用和协作场景中的实践技巧。', 1, '2026-03-27 16:21:17', '2026-03-27 16:21:17');
INSERT INTO `pc_activity` VALUES (5, '研究生科研诚信专题培训', '行政楼 506', '研究生院', '2026-04-28 14:30:00', '2026-04-28 17:00:00', '围绕学术规范、数据真实性、署名伦理与论文写作边界进行案例培训。', 1, '2026-03-27 16:21:17', '2026-03-27 16:21:17');

-- ----------------------------
-- Table structure for pc_notice
-- ----------------------------
DROP TABLE IF EXISTS `pc_notice`;
CREATE TABLE `pc_notice`  (
                              `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
                              `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'Notice title',
                              `summary` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'Notice summary',
                              `content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT 'Notice content',
                              `publish_time` datetime NULL DEFAULT NULL COMMENT 'Publish time',
                              `status` tinyint NOT NULL DEFAULT 1 COMMENT '1=published,0=offline',
                              `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Created time',
                              `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Updated time',
                              PRIMARY KEY (`id`) USING BTREE,
                              INDEX `idx_pc_notice_status_publish`(`status` ASC, `publish_time` ASC) USING BTREE,
                              INDEX `idx_pc_notice_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'Portal notices' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of pc_notice
-- ----------------------------
INSERT INTO `pc_notice` VALUES (1, '关于学术资源门户系统试运行的通知', '系统已进入试运行阶段，欢迎师生体验并反馈。', '学术资源门户系统已完成核心模块联调，现开放试运行。欢迎各位老师和同学体验论文检索、公告浏览、活动查询及AI辅助功能，并通过学院渠道反馈使用建议。', '2026-03-01 10:00:00', 1, '2026-03-27 16:21:01', '2026-03-27 16:21:01');
INSERT INTO `pc_notice` VALUES (2, '毕业设计中期检查安排', '请相关同学按要求提交中期检查材料。', '根据教学安排，毕业设计中期检查将于下周进行。请同学们按学院通知准备开题报告、阶段成果和系统演示材料，按时完成提交。', '2026-03-10 15:00:00', 1, '2026-03-27 16:21:01', '2026-03-27 16:21:01');
INSERT INTO `pc_notice` VALUES (3, '优秀论文专题栏目上线', '平台新增优秀论文专题，支持按方向浏览。', '为方便文献调研，平台新增优秀论文专题栏目，提供按研究方向聚合展示与关键词筛选，欢迎使用并提出改进建议。', '2026-03-20 09:00:00', 1, '2026-03-27 16:21:01', '2026-03-27 16:21:01');
INSERT INTO `pc_notice` VALUES (4, '关于系统维护窗口的公告', '本周末将进行数据库与网关维护。', '为提升系统稳定性，本周六晚间将进行例行维护，期间可能出现短时不可用。维护完成后将第一时间恢复服务。', '2026-03-24 18:30:00', 1, '2026-03-27 16:21:01', '2026-03-27 16:21:01');
INSERT INTO `pc_notice` VALUES (5, 'AI功能使用说明更新', '摘要生成与关键词提取功能说明已更新。', '平台已更新AI功能使用说明，包括输入长度建议、结果解释方式及常见问题处理流程，建议同学在使用前先阅读。', '2026-03-26 11:20:00', 1, '2026-03-27 16:21:01', '2026-03-27 16:21:01');

-- ----------------------------
-- Table structure for pc_paper
-- ----------------------------
DROP TABLE IF EXISTS `pc_paper`;
CREATE TABLE `pc_paper`  (
                             `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
                             `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'Paper title',
                             `abstract_text` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT 'Paper abstract',
                             `content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT 'Paper content',
                             `keywords` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'Keywords, comma separated',
                             `author_info` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'Author information',
                             `publish_time` datetime NULL DEFAULT NULL COMMENT 'Publish time',
                             `cover_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT 'Cover URL',
                             `category_id` bigint NULL DEFAULT NULL COMMENT 'Category ID',
                             `status` tinyint NOT NULL DEFAULT 1 COMMENT '1=published,0=offline',
                             `view_count` int NOT NULL DEFAULT 0 COMMENT 'View count',
                             `favorite_count` int NOT NULL DEFAULT 0 COMMENT 'Favorite count',
                             `comment_count` int NOT NULL DEFAULT 0 COMMENT 'Comment count',
                             `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Created time',
                             `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Updated time',
                             PRIMARY KEY (`id`) USING BTREE,
                             INDEX `idx_pc_paper_status_publish`(`status` ASC, `publish_time` ASC) USING BTREE,
                             INDEX `idx_pc_paper_category_id`(`category_id` ASC) USING BTREE,
                             INDEX `idx_pc_paper_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 38 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'Academic papers' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of pc_paper
-- ----------------------------
INSERT INTO `pc_paper` VALUES (1, '基于Transformer的学术文本分类方法研究', '提出一种结合领域词典增强的Transformer分类框架，用于提升学术摘要多标签分类准确率。', '本文围绕学术文本分类任务，构建了包含计算机、管理、经济等领域的语料集，并在预训练模型基础上引入关键词权重机制，实验结果显示在F1指标上优于传统方法。', 'Transformer,文本分类,自然语言处理,学术语料', '张晨, 李昊', '2025-11-12 10:00:00', 'https://images.unsplash.com/photo-1515879218367-8466d910aaa4', 1, 1, 86, 12, 4, '2026-03-27 15:37:47', '2026-03-27 15:37:47');
INSERT INTO `pc_paper` VALUES (2, '高校科研数据治理框架与实施路径', '从组织机制、数据标准和平台建设三个维度提出高校科研数据治理实施方案。', '文章结合高校信息化建设现状，分析科研数据资产化过程中的流程断点，提出基于元数据驱动的治理框架和分阶段落地路线。', '数据治理,高校科研,元数据,信息化', '王敏, 周倩', '2025-12-03 14:30:00', 'https://images.unsplash.com/photo-1454165804606-c3d57bc86b40', 2, 1, 54, 9, 3, '2026-03-27 15:37:47', '2026-03-27 15:37:47');
INSERT INTO `pc_paper` VALUES (3, '开放获取环境下经济学文献传播效率分析', '基于引用网络与下载行为数据评估开放获取政策对经济学文献传播效率的影响。', '研究构建了2018-2025年经济学期刊样本，采用双重差分方法评估开放获取政策实施效果，结果显示中长期传播效率显著提升。', '开放获取,经济学,文献传播,引用网络', '刘洋, 何宁', '2025-10-21 09:15:00', 'https://images.unsplash.com/photo-1554224155-6726b3ff858f', 3, 1, 61, 7, 2, '2026-03-27 15:37:47', '2026-03-27 15:37:47');
INSERT INTO `pc_paper` VALUES (4, '面向学术搜索的多模态论文推荐模型', '融合标题、摘要与用户行为序列，构建多模态论文推荐模型。', '本文设计了结合文本编码与行为序列建模的双塔结构，并通过在线A/B实验验证模型在点击率与收藏率上的提升效果。', '推荐系统,多模态,学术搜索,用户行为', '赵一帆, 郑宇', '2026-01-08 16:20:00', 'https://images.unsplash.com/photo-1498050108023-c5249f4df085', 1, 1, 93, 18, 6, '2026-03-27 15:37:47', '2026-03-27 15:37:47');
INSERT INTO `pc_paper` VALUES (5, '科研团队协作绩效的影响因素研究', '通过问卷与访谈数据探究科研团队协作绩效的关键影响因素。', '研究从目标一致性、沟通频率、资源共享程度等维度构建结构方程模型，识别影响协作绩效的显著路径。', '科研管理,团队协作,绩效评估,结构方程', '陈瑶, 孙凯', '2025-09-17 11:40:00', 'https://images.unsplash.com/photo-1521737604893-d14cc237f11d', 2, 1, 47, 5, 1, '2026-03-27 15:37:47', '2026-03-27 15:37:47');
INSERT INTO `pc_paper` VALUES (6, '数字普惠金融对区域创新的溢出效应', '检验数字普惠金融发展对区域创新产出的空间溢出效应。', '基于省级面板数据构建空间杜宾模型，结果表明数字普惠金融不仅促进本地创新，也对邻近区域产生显著正向影响。', '数字金融,区域创新,空间计量,经济增长', '郭磊, 许婷', '2025-08-28 13:10:00', 'https://images.unsplash.com/photo-1556740749-887f6717d7e4', 3, 1, 72, 11, 5, '2026-03-27 15:37:47', '2026-03-27 15:37:47');
INSERT INTO `pc_paper` VALUES (7, '大语言模型在论文摘要生成中的可控性研究', '围绕摘要长度、术语一致性和事实准确性评估大语言模型可控生成能力。', '文章构建了包含不同学科的评测集，并比较了提示工程与约束解码策略，分析生成质量与可控性的平衡关系。', '大语言模型,摘要生成,可控生成,学术写作', '林浩然, 唐悦', '2026-02-05 10:50:00', 'https://images.unsplash.com/photo-1486312338219-ce68d2c6f44d', 1, 1, 108, 26, 8, '2026-03-27 15:37:47', '2026-03-27 15:37:47');
INSERT INTO `pc_paper` VALUES (8, '高校知识服务平台运营模式优化研究', '提出面向师生科研场景的高校知识服务平台运营优化策略。', '通过对平台使用日志与用户访谈进行分析，识别知识发现、资源组织与服务响应中的瓶颈，并给出流程优化方案。', '知识服务,平台运营,高校图书馆,用户体验', '冯静, 黄立', '2025-11-30 15:05:00', 'https://images.unsplash.com/photo-1513530176992-0cf39c4cbed4', 2, 1, 58, 6, 2, '2026-03-27 15:37:47', '2026-03-27 15:37:47');
INSERT INTO `pc_paper` VALUES (9, '碳中和背景下产业结构升级的经济效应', '分析碳中和政策约束下产业结构升级对地区经济增长质量的影响。', '研究采用中介效应模型与异质性分析，发现绿色技术投资在产业升级与增长质量提升之间发挥关键中介作用。', '碳中和,产业升级,绿色技术,经济效应', '邵明, 罗婷', '2025-12-18 09:45:00', 'https://images.unsplash.com/photo-1473448912268-2022ce9509d8', 3, 1, 66, 10, 4, '2026-03-27 15:37:47', '2026-03-27 15:37:47');
INSERT INTO `pc_paper` VALUES (10, '联邦学习在高校隐私数据分析中的应用探索', '探讨联邦学习在不共享原始数据前提下支持高校联合建模的可行性。', '本文在多校联合数据场景下实现联邦训练原型系统，并比较不同聚合策略对模型精度和通信开销的影响。', '联邦学习,隐私保护,联合建模,高校数据', '许航, 戴琳', '2026-03-01 17:25:00', 'https://images.unsplash.com/photo-1518770660439-4636190af475', 1, 1, 81, 14, 5, '2026-03-27 15:37:47', '2026-03-27 15:37:47');
INSERT INTO `pc_paper` VALUES (11, '知识图谱驱动的跨学科文献发现方法', '提出基于实体关系融合的跨学科文献发现方法，提升检索覆盖率与结果相关性。', '研究将知识图谱与语义检索结合，围绕主题迁移和术语歧义构建重排策略，在跨学科查询场景中取得更高召回率。', '知识图谱,跨学科,文献检索,语义重排', '韩璐, 郭晨', '2025-07-09 10:20:00', 'https://images.unsplash.com/photo-1455390582262-044cdead277a', 1, 1, 63, 9, 3, '2026-03-27 15:39:37', '2026-03-27 15:39:37');
INSERT INTO `pc_paper` VALUES (12, '高校科研项目全过程绩效评价模型', '构建覆盖立项、中期和结题阶段的科研项目绩效评价指标体系。', '文章结合高校管理实践提出多维指标与权重分配方案，并通过案例数据验证模型在绩效诊断中的可解释性。', '科研项目,绩效评价,指标体系,高校管理', '梁宇, 赵静', '2025-06-18 14:15:00', 'https://images.unsplash.com/photo-1461749280684-dccba630e2f6', 2, 1, 42, 4, 1, '2026-03-27 15:39:37', '2026-03-27 15:39:37');
INSERT INTO `pc_paper` VALUES (13, '数字经济发展对就业结构的影响机制', '基于省际面板数据分析数字经济发展对就业结构升级的作用路径。', '研究发现数字基础设施和平台经济渗透率显著影响服务业高技能岗位占比，并在区域间存在异质性差异。', '数字经济,就业结构,面板数据,区域差异', '苏楠, 马会', '2025-05-27 09:40:00', 'https://images.unsplash.com/photo-1554224154-26032ffc0d07', 3, 1, 58, 8, 2, '2026-03-27 15:39:37', '2026-03-27 15:39:37');
INSERT INTO `pc_paper` VALUES (14, '面向问答系统的论文段落检索优化', '针对学术问答场景提出段落级召回与重排联合优化方案。', '本文采用双编码器召回和交叉编码器重排框架，并加入术语扩展策略，在问答数据集上提升了命中率。', '问答系统,段落检索,信息检索,双编码器', '邓杰, 罗凡', '2025-10-06 16:05:00', 'https://images.unsplash.com/photo-1519389950473-47ba0277781c', 1, 1, 77, 13, 4, '2026-03-27 15:39:37', '2026-03-27 15:39:37');
INSERT INTO `pc_paper` VALUES (15, '研究生科研能力培养路径的实证分析', '从课程、项目和导师指导三个维度分析研究生科研能力形成机制。', '通过问卷与访谈数据建立回归模型，结果表明项目实践与高频反馈对科研能力提升具有显著正向作用。', '研究生教育,科研能力,实证分析,导师指导', '蒋蕾, 吴珂', '2025-09-03 11:10:00', 'https://images.unsplash.com/photo-1434030216411-0b793f4b4173', 2, 1, 49, 6, 2, '2026-03-27 15:39:37', '2026-03-27 15:39:37');
INSERT INTO `pc_paper` VALUES (16, '绿色金融政策与企业创新投入关系研究', '评估绿色金融政策实施后企业研发投入变化及其长期效果。', '基于上市公司样本构建政策冲击模型，发现绿色信贷可有效提升高污染行业企业的绿色创新投入强度。', '绿色金融,企业创新,政策评估,研发投入', '方琳, 郑博', '2025-08-14 13:55:00', 'https://images.unsplash.com/photo-1520607162513-77705c0f0d4a', 3, 1, 67, 10, 3, '2026-03-27 15:39:37', '2026-03-27 15:39:37');
INSERT INTO `pc_paper` VALUES (17, '学术资源门户中的个性化排序策略', '提出融合用户兴趣与内容时效性的个性化排序策略。', '研究构建用户兴趣向量并加入时间衰减因子，在门户首页推荐场景下提升点击率和停留时长。', '个性化排序,推荐系统,用户兴趣,时效性', '彭越, 张弛', '2026-01-22 10:35:00', 'https://images.unsplash.com/photo-1496171367470-9ed9a91ea931', 1, 1, 95, 19, 7, '2026-03-27 15:39:37', '2026-03-27 15:39:37');
INSERT INTO `pc_paper` VALUES (18, '基于流程挖掘的科研管理流程再造', '利用流程挖掘方法识别科研管理流程中的瓶颈并提出优化方案。', '文章对项目审批与报销流程进行事件日志分析，提出流程标准化与并行审批策略以缩短办理周期。', '流程挖掘,科研管理,流程优化,事件日志', '程晓, 贺宁', '2025-12-09 15:25:00', 'https://images.unsplash.com/photo-1450101499163-c8848c66ca85', 2, 1, 53, 7, 2, '2026-03-27 15:39:37', '2026-03-27 15:39:37');
INSERT INTO `pc_paper` VALUES (19, '平台经济下中小企业融资约束缓解研究', '探究平台化交易数据如何改善中小企业融资可得性。', '通过实证分析发现，平台信用画像与交易稳定性指标能够降低金融机构风险评估成本，从而缓解融资约束。', '平台经济,中小企业,融资约束,信用画像', '任涛, 赵萌', '2025-11-01 09:05:00', 'https://images.unsplash.com/photo-1563013544-824ae1b704d3', 3, 1, 64, 9, 3, '2026-03-27 15:39:37', '2026-03-27 15:39:37');
INSERT INTO `pc_paper` VALUES (20, '引文上下文增强的论文推荐算法', '提出利用引文上下文语义信息增强论文推荐效果的算法框架。', '方法将引文句子的语义表示纳入候选重排阶段，有效提升与用户查询意图匹配的推荐结果质量。', '引文分析,论文推荐,语义表示,重排', '谢航, 林朔', '2026-02-18 17:00:00', 'https://images.unsplash.com/photo-1504384308090-c894fdcc538d', 1, 1, 88, 15, 5, '2026-03-27 15:39:37', '2026-03-27 15:39:37');
INSERT INTO `pc_paper` VALUES (21, '科研诚信治理中的制度设计与执行', '从规则制定、监督机制与惩戒措施评估科研诚信治理成效。', '文章基于多所高校案例比较治理模式差异，提出强化过程监管与学术共同体自律的协同路径。', '科研诚信,制度设计,高校治理,学术规范', '顾言, 戴芸', '2025-10-29 14:45:00', 'https://images.unsplash.com/photo-1497633762265-9d179a990aa6', 2, 1, 46, 5, 2, '2026-03-27 15:39:37', '2026-03-27 15:39:37');
INSERT INTO `pc_paper` VALUES (22, '产业数字化转型对全要素生产率的影响', '评估产业数字化转型对企业全要素生产率提升的作用机制。', '研究使用微观企业数据进行回归分析，结果表明数字化投入通过管理效率提升和创新扩散推动生产率增长。', '数字化转型,全要素生产率,企业绩效,创新扩散', '宋辰, 吴菲', '2025-12-26 10:10:00', 'https://images.unsplash.com/photo-1460925895917-afdab827c52f', 3, 1, 70, 11, 4, '2026-03-27 15:39:37', '2026-03-27 15:39:37');
INSERT INTO `pc_paper` VALUES (23, '学术搜索日志驱动的查询改写研究', '利用检索日志中的点击与停留信号进行学术查询改写。', '本文构建查询意图识别与术语扩展联合模型，在学术检索系统中降低零结果率并提升用户满意度。', '查询改写,搜索日志,学术检索,意图识别', '陆川, 孟琪', '2026-03-06 12:30:00', 'https://images.unsplash.com/photo-1484417894907-623942c8ee29', 1, 1, 95, 17, 6, '2026-03-27 15:39:37', '2026-03-27 16:33:28');
INSERT INTO `pc_paper` VALUES (24, '高校图书馆知识服务满意度影响因素', '分析高校图书馆知识服务满意度的关键影响因素及改进方向。', '基于结构方程模型验证服务响应速度、资源可得性与馆员专业度对满意度具有显著影响。', '图书馆服务,满意度,知识服务,结构方程', '秦雯, 欧阳帆', '2025-09-25 16:40:00', 'https://images.unsplash.com/photo-1521587760476-6c12a4b040da', 2, 1, 55, 8, 3, '2026-03-27 15:39:37', '2026-03-27 15:39:37');
INSERT INTO `pc_paper` VALUES (25, '区域协同创新网络的演化特征分析', '采用复杂网络方法刻画区域协同创新网络的结构演化特征。', '研究发现核心城市在网络中具有显著中介作用，政策引导可促进边缘地区创新主体连接强度提升。', '协同创新,复杂网络,区域经济,创新政策', '唐珊, 杨祺', '2025-07-31 08:55:00', 'https://images.unsplash.com/photo-1517048676732-d65bc937f952', 3, 1, 62, 9, 3, '2026-03-27 15:39:37', '2026-03-27 15:39:37');
INSERT INTO `pc_paper` VALUES (26, '智能辅学系统在高校课程中的应用评估', '评估智能辅学系统在高校课程中的教学支持效果与学生学习增益。', '研究基于两个学期的课程实践数据，对比使用与未使用智能辅学系统班级在学习投入和成绩提升方面的差异，并分析教师采纳行为。', '智能辅学,教育技术,学习分析,高校教学', '刘知远, 马会丽', '2025-09-12 10:10:00', 'https://images.unsplash.com/photo-1513258496099-48168024aec0', 4, 1, 73, 11, 4, '2026-03-27 15:42:10', '2026-03-27 15:42:10');
INSERT INTO `pc_paper` VALUES (27, '混合式教学平台学习行为特征挖掘', '从平台日志中挖掘学习行为模式，识别高绩效学习路径。', '文章构建学习行为序列并进行聚类分析，发现讨论参与和阶段性测验完成率与课程最终成绩显著相关。', '混合式教学,学习行为,教育数据挖掘,行为序列', '陈悦, 宋凯', '2025-11-03 14:20:00', 'https://images.unsplash.com/photo-1488190211105-8b0e65b80b4e', 4, 1, 65, 9, 3, '2026-03-27 15:42:10', '2026-03-27 15:42:10');
INSERT INTO `pc_paper` VALUES (28, '在线教育资源推荐的冷启动问题研究', '针对新用户与新课程场景提出冷启动推荐策略。', '研究融合内容特征与群体画像构建推荐框架，在新用户首周使用数据中显著提升点击率与完课率。', '在线教育,推荐系统,冷启动,资源分发', '高凡, 许晨曦', '2026-01-14 09:35:00', 'https://images.unsplash.com/photo-1523240795612-9a054b0db644', 4, 1, 82, 14, 5, '2026-03-27 15:42:10', '2026-03-27 15:42:10');
INSERT INTO `pc_paper` VALUES (29, '数据要素流通中的隐私合规治理路径', '讨论数据要素流通场景下的隐私保护与合规治理框架。', '本文结合现行法律规范，提出分级授权、最小必要处理与可审计留痕机制，以平衡数据利用与主体权益保护。', '数据合规,隐私保护,数据要素,治理框架', '王璟, 何思远', '2025-10-08 13:40:00', 'https://images.unsplash.com/photo-1450101499163-c8848c66ca85', 5, 1, 59, 8, 3, '2026-03-27 15:42:10', '2026-03-27 15:42:10');
INSERT INTO `pc_paper` VALUES (30, '平台算法透明度的监管机制比较研究', '比较不同监管框架下平台算法透明度治理效果。', '通过政策文本分析和案例比较，评估信息披露、第三方审计与责任追溯机制在算法治理中的适用性。', '算法治理,平台监管,透明度,政策比较', '邵宁, 顾昊', '2025-12-02 11:25:00', 'https://images.unsplash.com/photo-1496171367470-9ed9a91ea931', 5, 1, 68, 10, 4, '2026-03-27 15:42:10', '2026-03-27 15:42:10');
INSERT INTO `pc_paper` VALUES (31, '高校科研成果转化中的法律风险识别', '识别高校科研成果转化流程中的常见法律风险并提出应对策略。', '研究围绕知识产权归属、技术许可条款和收益分配机制展开，形成面向高校技术转移办公室的风控建议。', '科研转化,法律风险,知识产权,技术许可', '周瑶, 林昭', '2026-02-11 16:00:00', 'https://images.unsplash.com/photo-1519337265831-281ec6cc8514', 5, 1, 75, 12, 5, '2026-03-27 15:42:10', '2026-03-27 15:42:10');
INSERT INTO `pc_paper` VALUES (32, '研究生学业压力与心理韧性的关系研究', '分析研究生群体学业压力与心理韧性之间的关联机制。', '基于多校问卷数据，构建中介调节模型，发现社会支持在学业压力影响心理健康过程中具有显著缓冲作用。', '学业压力,心理韧性,心理健康,社会支持', '杨芮, 朱明', '2025-08-19 10:50:00', 'https://images.unsplash.com/photo-1517841905240-472988babdf9', 6, 1, 71, 10, 4, '2026-03-27 15:42:10', '2026-03-27 15:42:10');
INSERT INTO `pc_paper` VALUES (33, '线上学习环境下学生自我效能感提升路径', '探讨线上学习情境中提升学生自我效能感的关键干预策略。', '文章通过实验课程比较即时反馈、目标分解与同伴互助三类干预策略的效果，提出分层干预方案。', '自我效能感,线上学习,教育心理,学习动机', '丁可, 马会', '2025-11-26 15:15:00', 'https://images.unsplash.com/photo-1544717305-2782549b5136', 6, 1, 57, 7, 2, '2026-03-27 15:42:10', '2026-03-27 15:42:10');
INSERT INTO `pc_paper` VALUES (34, '社交媒体使用强度与大学生注意力分配', '研究社交媒体使用强度对大学生注意力分配与学习效率的影响。', '采用行为实验与问卷结合的方法，发现高强度碎片化使用与持续注意力下降显著相关，并提出数字健康建议。', '社交媒体,注意力,大学生,行为实验', '彭琳, 何宇', '2026-03-05 12:10:00', 'https://images.unsplash.com/photo-1521737604893-d14cc237f11d', 6, 1, 83, 13, 5, '2026-03-27 15:42:10', '2026-03-27 16:34:28');
INSERT INTO `pc_paper` VALUES (35, '可穿戴生理信号在疲劳检测中的应用', '探索可穿戴设备生理信号用于脑力疲劳检测的可行性。', '研究采集心率变异和皮电信号，结合机器学习模型实现疲劳状态识别，并验证跨场景泛化能力。', '可穿戴设备,疲劳检测,生理信号,机器学习', '罗翔, 韩梅', '2025-09-28 09:30:00', 'https://images.unsplash.com/photo-1516549655169-df83a0774514', 7, 1, 84, 15, 6, '2026-03-27 15:42:10', '2026-03-27 15:42:10');
INSERT INTO `pc_paper` VALUES (36, '医学影像分割模型在临床辅助中的效果评估', '评估医学影像分割模型在临床辅助诊断流程中的实际效果。', '文章在多中心影像数据集上对比不同分割网络，分析精度、推理时延和医生接受度等指标。', '医学影像,图像分割,临床辅助,深度学习', '苏鹏, 姜岚', '2025-12-21 14:05:00', 'https://images.unsplash.com/photo-1582719478250-c89cae4dc85b', 7, 1, 90, 18, 7, '2026-03-27 15:42:10', '2026-03-27 15:42:10');
INSERT INTO `pc_paper` VALUES (37, '脑机接口信号解码算法的鲁棒性研究', '研究脑机接口信号解码算法在噪声环境下的鲁棒性表现。', '本文比较多种时序解码模型在不同信噪比条件下的性能变化，并提出抗噪特征融合策略。', '脑机接口,信号解码,鲁棒性,时序建模', '郑凯, 叶桐', '2026-02-27 17:40:00', 'https://images.unsplash.com/photo-1532094349884-543bc11b234d', 7, 1, 97, 21, 8, '2026-03-27 15:42:10', '2026-03-27 15:42:10');

-- ----------------------------
-- Table structure for pc_paper_category
-- ----------------------------
DROP TABLE IF EXISTS `pc_paper_category`;
CREATE TABLE `pc_paper_category`  (
                                      `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'Primary key',
                                      `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'Category name',
                                      `sort_no` int NOT NULL DEFAULT 0 COMMENT 'Sort order',
                                      `status` tinyint NOT NULL DEFAULT 1 COMMENT '1=enabled,0=disabled',
                                      `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Created time',
                                      PRIMARY KEY (`id`) USING BTREE,
                                      UNIQUE INDEX `uk_pc_paper_category_name`(`name` ASC) USING BTREE,
                                      INDEX `idx_pc_paper_category_status_sort`(`status` ASC, `sort_no` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'Paper categories' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of pc_paper_category
-- ----------------------------
INSERT INTO `pc_paper_category` VALUES (1, 'Computer Science', 1, 1, '2026-03-27 12:57:39');
INSERT INTO `pc_paper_category` VALUES (2, 'Management', 2, 1, '2026-03-27 12:57:39');
INSERT INTO `pc_paper_category` VALUES (3, 'Economics', 3, 1, '2026-03-27 12:57:39');
INSERT INTO `pc_paper_category` VALUES (4, 'Education Technology', 4, 1, '2026-03-27 15:41:07');
INSERT INTO `pc_paper_category` VALUES (5, 'Law and Policy', 5, 1, '2026-03-27 15:41:07');
INSERT INTO `pc_paper_category` VALUES (6, 'Psychology', 6, 1, '2026-03-27 15:41:07');
INSERT INTO `pc_paper_category` VALUES (7, 'Biomedical Engineering', 7, 1, '2026-03-27 15:41:07');

SET FOREIGN_KEY_CHECKS = 1;
