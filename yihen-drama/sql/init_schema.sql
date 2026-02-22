-- --------------------------------------------------------
-- 主机:                           127.0.0.1
-- 服务器版本:                        8.0.45 - MySQL Community Server - GPL
-- 服务器操作系统:                      Win64
-- HeidiSQL 版本:                  12.14.0.7165
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

-- 导出  表 yihendrama.characters 结构
DROP TABLE IF EXISTS `characters`;
CREATE TABLE IF NOT EXISTS `characters` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `episode_id` bigint NOT NULL COMMENT '所属章节ID',
  `project_id` bigint NOT NULL COMMENT '所属项目ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色名称',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '角色描述',
  `avatar` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '角色头像URL',
  `video_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '角色视频URL',
  `features` json DEFAULT NULL COMMENT '角色特征描述(用于一致性生成)',
  `is_deleted` tinyint DEFAULT '0' COMMENT '逻辑删除: 0-未删除 1-已删除',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_episode_id` (`episode_id`),
  KEY `idx_project_id` (`project_id`)
) ENGINE=InnoDB AUTO_INCREMENT=121 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';

-- 正在导出表  yihendrama.characters 的数据：~0 rows (大约)
DELETE FROM `characters`;

-- 导出  表 yihendrama.episode 结构
DROP TABLE IF EXISTS `episode`;
CREATE TABLE IF NOT EXISTS `episode` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '章节ID',
  `project_id` bigint NOT NULL COMMENT '所属项目ID',
  `chapter_number` int NOT NULL COMMENT '章节序号',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '章节名称',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '原始小说内容',
  `status` tinyint DEFAULT '0' COMMENT '状态: 0-待开始 1-处理中 2-已完成',
  `progress` int DEFAULT '0' COMMENT '处理进度百分比',
  `current_step` int DEFAULT '0' COMMENT '当前步骤: 0-输入内容 1-提取信息 2-生成图片 4-固定角色 5-生成分镜 6-生成视频 7-合成完成',
  `is_deleted` tinyint DEFAULT '0' COMMENT '逻辑删除: 0-未删除 1-已删除',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_project_id` (`project_id`),
  KEY `idx_chapter_number` (`chapter_number`),
  CONSTRAINT `fk_episode_project` FOREIGN KEY (`project_id`) REFERENCES `project` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=33 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='章节表';

-- 正在导出表  yihendrama.episode 的数据：~0 rows (大约)
DELETE FROM `episode`;

-- 导出  表 yihendrama.generated_image 结构
DROP TABLE IF EXISTS `generated_image`;
CREATE TABLE IF NOT EXISTS `generated_image` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '图片ID',
  `episode_id` bigint NOT NULL COMMENT '所属章节ID',
  `image_type` tinyint DEFAULT '1' COMMENT '图片类型: 1-角色 2-场景',
  `target_id` bigint DEFAULT NULL COMMENT '关联角色或场景的ID',
  `image_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '生成的图片URL',
  `prompt` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '生成使用的提示词',
  `status` tinyint DEFAULT '1' COMMENT '状态: 0-生成中 1-成功 2-失败',
  `is_deleted` tinyint DEFAULT '0' COMMENT '逻辑删除: 0-未删除 1-已删除',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_episode_id` (`episode_id`),
  KEY `idx_image_type` (`image_type`),
  CONSTRAINT `fk_image_episode` FOREIGN KEY (`episode_id`) REFERENCES `episode` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='生成图片表';

-- 正在导出表  yihendrama.generated_image 的数据：~0 rows (大约)
DELETE FROM `generated_image`;

-- 导出  表 yihendrama.model_config 结构
DROP TABLE IF EXISTS `model_config`;
CREATE TABLE IF NOT EXISTS `model_config` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '配置ID',
  `config_type` tinyint NOT NULL COMMENT '配置类型: 1-TEXT 2-IMAGE 3-VIDEO 4-AUDIO',
  `provider` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '模型提供商',
  `model_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '模型名称',
  `parameters` json DEFAULT NULL COMMENT '默认参数配置',
  `is_active` tinyint DEFAULT '1' COMMENT '是否激活',
  `is_deleted` tinyint DEFAULT '0' COMMENT '逻辑删除: 0-未删除 1-已删除',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_config_type` (`config_type`),
  KEY `idx_config_type` (`config_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='模型配置表';

-- 正在导出表  yihendrama.model_config 的数据：~0 rows (大约)
DELETE FROM `model_config`;

-- 导出  表 yihendrama.model_definition 结构
DROP TABLE IF EXISTS `model_definition`;
CREATE TABLE IF NOT EXISTS `model_definition` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `provider_code` varchar(50) NOT NULL COMMENT '厂商标识 openai / aliyun / baidu',
  `base_url` varchar(255) DEFAULT NULL COMMENT '模型默认Base URL',
  `status` tinyint DEFAULT '1' COMMENT '1启用 0禁用',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint DEFAULT '0' COMMENT '0-未删除，1-已删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 正在导出表  yihendrama.model_definition 的数据：~4 rows (大约)
DELETE FROM `model_definition`;
INSERT INTO `model_definition` (`id`, `provider_code`, `base_url`, `status`, `create_time`, `update_time`, `is_deleted`) VALUES
	(1, 'Deepseek', 'https://api.deepseek.com', 1, '2026-01-20 12:57:40', '2026-01-22 06:12:59', 0),
	(2, '阿里百炼平台', 'https://dashscope.aliyuncs.com', 1, '2026-01-20 13:48:20', '2026-01-20 13:49:33', 0),
	(3, 'Kimi', 'https://api.moonshot.cn/v1', 1, '2026-01-21 03:57:22', '2026-01-22 12:03:58', 0),
	(4, '火山引擎', 'https://ark.cn-beijing.volces.com/api/v3', 1, '2026-01-22 12:03:28', '2026-01-22 12:03:28', 0);

-- 导出  表 yihendrama.model_instance 结构
DROP TABLE IF EXISTS `model_instance`;
CREATE TABLE IF NOT EXISTS `model_instance` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `model_def_id` bigint NOT NULL COMMENT '关联模型定义',
  `model_code` varchar(100) NOT NULL COMMENT '模型编码 gpt-4o / qwen-max',
  `model_type` int NOT NULL DEFAULT '1' COMMENT '1-TEXT / 2-IMAGE /3- VIDEO',
  `instance_name` varchar(100) NOT NULL COMMENT '实例名称，如剧情生成/分镜生成',
  `scene_code` int NOT NULL COMMENT '使用场景 1-通用文本生成 2-信息提取',
  `path` varchar(50) DEFAULT NULL COMMENT '请求路径，与base_url拼接',
  `api_key` varchar(255) NOT NULL COMMENT '密钥',
  `params` json DEFAULT NULL COMMENT '实例级参数',
  `status` tinyint DEFAULT '1',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint DEFAULT '0' COMMENT '0-未删除，1-已删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 正在导出表  yihendrama.model_instance 的数据：~8 rows (大约)
DELETE FROM `model_instance`;
INSERT INTO `model_instance` (`id`, `model_def_id`, `model_code`, `model_type`, `instance_name`, `scene_code`, `path`, `api_key`, `params`, `status`, `create_time`, `update_time`, `is_deleted`) VALUES
	(1, 1, 'deepseek-chat', 1, 'deepseek-chat', 2, '/chat/completions', 'sk-', '{"max_tokens": 8192, "temperature": 0.3}', 0, '2026-01-20 13:07:58', '2026-02-03 12:08:48', 0),
	(2, 2, 'cosyvoice-v3-flash', 4, 'Cosyvoice-v3-flash', 6, '/', 'sk-', '{"speed": 1, "voice": "zh-CN-Xiaoxiao"}', 1, '2026-01-20 13:51:42', '2026-02-02 13:41:06', 0),
	(3, 3, 'moonshot-v1-32k', 1, 'moonshot-v1-32k', 2, '/chat/completions', 'sk-','{"max_tokens": 8192, "temperature": 0.3}', 0, '2026-01-21 04:01:53', '2026-02-03 12:05:34', 0),
	(4, 4, 'doubao-seed-1-8-251228', 1, '豆包', 1, '/chat/completions', 'sk-', '{"max_tokens": 4000, "temperature": 0.7}', 1, '2026-01-22 12:21:19', '2026-02-03 12:08:36', 0),
	(7, 4, 'doubao-seedream-4-5-251128', 2, ' Doubao-Seedream-4.5', 3, '/images/generations', 'sk-', '{"size": "2K", "watermark": false}', 1, '2026-01-23 12:13:28', '2026-02-04 13:47:58', 0),
	(9, 4, 'doubao-seedance-1-5-pro-251215', 3, 'Doubao-seedance-1-5-pro', 5, '/contents/generations/tasks', 'sk-', '{"duration": 5}', 1, '2026-01-24 09:37:53', '2026-01-24 12:38:03', 0),
	(10, 2, 'kimi-k2-thinking', 1, 'Kimi-k2-thinking', 1, '/compatible-mode/v1', 'sk-','{"max_tokens": 4000, "temperature": 0.7}', 1, '2026-02-02 13:19:55', '2026-02-03 12:08:39', 0),
	(11, 4, 'doubao-seedance-1-0-lite-i2v-250428', 3, 'Doubao-seedance-1-0-lite-i2v', 5, '/contents/generations/tasks', 'sk-', '{"fps": 30, "duration": 5, "watermark": false}', 1, '2026-02-05 09:06:15', '2026-02-05 09:37:26', 0);

-- 导出  表 yihendrama.model_instance_default 结构
DROP TABLE IF EXISTS `model_instance_default`;
CREATE TABLE IF NOT EXISTS `model_instance_default` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `model_type` int NOT NULL COMMENT '模型类型：1-TEXT / 2-IMAGE / 3-VIDEO',
  `model_instance_id` bigint NOT NULL COMMENT '默认使用的模型实例ID',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：1-启用，0-禁用',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注说明',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint DEFAULT '0' COMMENT '0-未删除，1-已删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='模型默认实例配置表';

-- 正在导出表  yihendrama.model_instance_default 的数据：~4 rows (大约)
DELETE FROM `model_instance_default`;
INSERT INTO `model_instance_default` (`id`, `model_type`, `model_instance_id`, `status`, `remark`, `create_time`, `update_time`, `is_deleted`) VALUES
	(2, 1, 3, 1, NULL, '2026-01-22 02:55:32', '2026-01-24 04:15:02', 0),
	(3, 2, 7, 1, NULL, '2026-01-23 18:11:29', '2026-01-23 18:11:50', 0),
	(4, 3, 11, 1, NULL, '2026-01-24 09:38:01', '2026-02-05 09:06:18', 0),
	(5, 4, 2, 1, NULL, '2026-02-02 13:22:59', '2026-02-02 13:22:59', 0);

-- 导出  表 yihendrama.operation_log 结构
DROP TABLE IF EXISTS `operation_log`;
CREATE TABLE IF NOT EXISTS `operation_log` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志ID',
  `operation_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '操作类型',
  `operation_desc` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '操作描述',
  `request_params` json DEFAULT NULL COMMENT '请求参数',
  `response_data` json DEFAULT NULL COMMENT '响应数据',
  `ip_address` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'IP地址',
  `status` tinyint DEFAULT '1' COMMENT '状态: 0-失败 1-成功',
  `is_deleted` tinyint DEFAULT '0' COMMENT '逻辑删除: 0-未删除 1-已删除',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_operation_type` (`operation_type`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作日志表';

-- 正在导出表  yihendrama.operation_log 的数据：~0 rows (大约)
DELETE FROM `operation_log`;

-- 导出  表 yihendrama.project 结构
DROP TABLE IF EXISTS `project`;
CREATE TABLE IF NOT EXISTS `project` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '项目ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '项目名称',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '项目描述',
  `cover` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '封面URL',
  `style_id` bigint DEFAULT NULL COMMENT '风格: 1-动漫 2-写实 3-油画 4-赛博朋克',
  `status` tinyint DEFAULT '0' COMMENT '状态: 0-草稿 1-处理中 2-已完成',
  `consistency` int DEFAULT '0' COMMENT '一致性百分比',
  `chapter_count` int DEFAULT '0' COMMENT '章节数量',
  `progress` int DEFAULT '0' COMMENT '总体进度百分比',
  `is_deleted` tinyint DEFAULT '0' COMMENT '逻辑删除: 0-未删除 1-已删除',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='项目表';

-- 正在导出表  yihendrama.project 的数据：~0 rows (大约)
DELETE FROM `project`;

-- 导出  表 yihendrama.prompt_template 结构
DROP TABLE IF EXISTS `prompt_template`;
CREATE TABLE IF NOT EXISTS `prompt_template` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `prompt_code` varchar(64) NOT NULL COMMENT '提示词唯一标识',
  `prompt_name` varchar(128) NOT NULL COMMENT '提示词名称',
  `scene_code` int NOT NULL COMMENT '业务场景（SceneCode 枚举）',
  `prompt_content` text NOT NULL COMMENT '提示词内容（支持变量占位）',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint DEFAULT '0' COMMENT '0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_prompt_code` (`prompt_code`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='提示词模板表';

-- 正在导出表  yihendrama.prompt_template 的数据：~6 rows (大约)
DELETE FROM `prompt_template`;
INSERT INTO `prompt_template` (`id`, `prompt_code`, `prompt_name`, `scene_code`, `prompt_content`, `create_time`, `update_time`, `is_deleted`) VALUES
	(1, 'INFO_EXTRACT_V1', '信息提取-人物场景物品', 2, '你是一名专业的信息抽取与资产整理助手，擅长从小说和剧情文本中提取可复用的结构化资产信息。\n你当前执行的是【资产级人物与场景抽取任务】，不是文学分析、剧情复述或过程描写任务。\n\n请严格按照以下要求执行任务。\n\n【任务目标】\n从用户提供的剧情文本中，逐一提取所有“人物”和“场景”，并判断它们是否与【已有资产】重复。\n不得遗漏任何符合定义的实体。\n\n【资产定义】\n1. 人物：\n   - 指文本中真实出现或被明确提及的单一、具体角色（包括主角、配角）\n   - 不包括群体、团队或无明确个体指代的角色\n   - 指代同一人的代称（如“他”“这名少年”）需合并为同一人物资产\n\n2. 场景：\n   - 指故事发生的具体或抽象环境\n   - 包括地点、空间结构、时间或氛围特征\n\n【去重与复用规则（非常重要）】\n1. 若人物 / 场景与【已有资产】中的名称与核心特征高度一致：\n   - 判定为已存在资产\n   - 不新建资产\n   - description 仅补充文本中新出现的、明确的细节（如无新信息则保持简洁）\n\n2. 若名称相同但核心特征明显不同：\n   - 判定为不同资产\n   - 作为新资产输出\n\n3. 若名称不同但明显指代同一实体：\n   - 合并为同一资产\n   - 使用最明确、最完整的名称作为 name\n\n4. 严禁编造与文本不符的信息或引入新剧情\n\n【人物描述要求（核心规则）】\n1. description 字段用于生成【可直接复用的角色外观资产】，服务于后续图像或多模态生成。\n2. description 必须是一段【纯视觉、静态、可被直接绘制的外观描述】，语言风格应接近“角色设定说明”，而非小说叙述。\n3. description 必须尽量覆盖以下外观维度（不得因原文信息不足而省略）：\n   - 年龄阶段与性别外观（如二十岁出头的年轻男性）\n   - 脸型与整体面部轮廓（如棱角分明、线条柔和等）\n   - 五官特征（眼神、眉形、整体气质层面的可视特征）\n   - 发型与发色（允许使用通用、常见的视觉占位描述）\n   - 身材或体态（如匀称、偏瘦、结实等）\n   - 穿着风格与服装组合（需符合人物身份与故事环境）\n4. 对原文未明确描述的外观维度，允许进行【克制、通用、视觉常识级的合理补全】，但必须遵循：\n   - 不与原文已知设定冲突\n   - 不引入额外世界观、时代或风格标签\n   - 不体现具体剧情行为或时间点\n5. description 中【严禁出现】：\n   - 任何解释性或元叙述语句（如“原文未提及”“可合理补全为”）\n   - 行为、动作、过程或状态变化（如“奔跑后”“刚经历过”）\n   - 心理活动、情绪波动、声音或内心描写\n   - 分析性、评价性、总结性语言\n6. description 的目标是：**即使脱离原文上下文，也能被画师或图像模型直接使用。**\n\n【已有资产（用于去重判断）】\n人物：\n{{existing_characters}}\n\n场景：\n{{existing_scenes}}\n\n【输入文本】\n{{input}}\n\n【输出要求】\n请严格按照以下 JSON 格式返回：\n- 不要输出任何额外说明文字\n- 不要使用 ```json 或 ``` 包裹\n- 不得增加、删除或修改字段结构\n\n{\n  "characters": [\n    {\n      "name": "人物名称",\n      "description": "人物描述（高密度、纯静态、可绘制的外观设定）",\n      "isNew": true\n    }\n  ],\n  "scenes": [\n    {\n      "name": "场景名称",\n      "description": "场景描述（仅包含明确出现的环境与空间信息）",\n      "isNew": false\n    }\n  ]\n}\n', '2026-01-21 05:05:57', '2026-02-03 04:22:58', 0),
	(2, 'IMAGE_GEN_v1', '角色生成', 3, '角色设定稿，角色三视图，角色转面图，正交视图\n\n同一个角色，仅展示三种视角：\n1）严格正面视角，正对镜头\n2）严格侧面视角，面向左侧\n3）严格背面视角\n\n全身展示，\n角色保持完全静止，\n标准中立站姿，\n双臂自然下垂于身体两侧，\n无行走、无动作、无动态姿势，\n不摆姿势、不做表情变化\n\n正交投影，\n无透视变化，\n无景深，\n无夸张构图\n\n纯角色参考设定图，\n白色背景，\n均匀、平直光照\n\n画面中禁止出现任何文字、标注、箭头、符号、水印，\n无说明文字、无设计注释、无语言元素\n\n角色比例高度一致，\n面部、服装、体型在三视图中保持完全一致\n\n角色外观设定（仅包含可被绘制的静态外貌特征）：\n{{input}}\n\n绘画风格：{{style_template}}\n', '2026-01-22 15:40:13', '2026-02-03 04:23:56', 0),
	(7, 'SCENE_IMAGE_GEN_v1', '场景生成', 1, '请生成一个场景照，下面是场景的特征： {{input}}。风格为 {{style_template}}。请注意：所有的场景都不能出现人物！', '2026-01-31 11:11:32', '2026-01-31 11:37:22', 0),
	(8, 'SHOT_GENERATE_V1', '分镜生成', 4, '你是一名专业的影视分镜与剧情视觉化助手，擅长将小说内容改写为结构化、可管理的分镜数据。\n你的任务不是复述小说，而是将小说内容转化为【可拍摄、可绘制、可资产化管理的分镜列表】。\n\n【任务目标】\n1. 将输入的小说文本，合理切分为多个“分镜单元”\n2. 每个分镜必须是一个独立的视觉画面描述\n3. 每个分镜必须关联完整的【角色资产对象】与【场景资产对象】\n4. 返回的数据将直接用于前端分镜管理与后续图像生成\n\n---\n\n【分镜定义（非常重要）】\n- 一个分镜 = 一个可被单独拍摄或绘制的画面单元\n- 分镜描述应具备明确、单一的视觉焦点\n- 分镜描述可以采用以下两种方式之一：\n  1. 镜头导向表达（描述镜头运动或视角）\n  2. 画面导向表达（直接描述画面中发生的视觉内容）\n- 并非每个分镜都必须出现“镜头”“视角”等摄影术语\n- 分镜数量控制在 8～12 个以内\n- 如内容较长，优先合并相邻分镜，保证 JSON 完整输出\n---\n\n【分镜切分规则】\n1. 按“视觉焦点变化”切分，而不是按句号或段落切分\n2. 环境建立、角色动作、关键细节，通常应拆分为不同分镜\n3. 避免一个分镜中同时出现多个画面或跳跃视角\n4. 不新增原文中不存在的剧情事件\n5. 不改变原文的事件顺序与因果关系\n\n---\n\n【角色关联规则】\n1. 每个分镜需关联 0 个或多个角色\n2. 角色必须从【已有角色资产】中选择\n3. 返回时需返回【完整角色对象】，不得仅返回角色名称\n4. 若分镜中未出现角色，则返回空数组 []\n\n---\n\n【场景关联规则】\n1. 每个分镜必须关联 1 个场景\n2. 场景必须从【已有场景资产】中选择\n3. 返回时需返回【完整场景对象】\n4. 不得新建场景资产\n\n---\n\n【分镜文本要求】\n1. 使用偏导演/分镜描述风格的语言\n2. 语言应具备画面感，但不进行文学扩写\n3. 不加入主观评论或分析性语言\n4. 不包含“这是一个镜头”“这一幕表现了”等元叙述\n\n---\n\n【已有角色资产（对象数组）】\n{{existing_characters}}\n\n【已有场景资产（对象数组）】\n{{existing_scenes}}\n\n【输入小说文本】\n{{input}}\n\n---\n\n【输出要求（严格遵守）】\n- 仅返回 JSON\n- 不要输出任何额外说明文字\n- 不要使用 ```json 或 ``` 包裹\n- 不得新增、删除或修改字段结构\n\n输出格式如下：\n\n{\n  "shots": [\n    {\n      "shotText": "分镜画面描述文本",\n      "characters": [\n        {\n          "id": "角色ID",\n          "name": "角色名称",\n          "description": "角色描述"\n        }\n      ],\n      "scene": {\n        "id": "场景ID",\n        "name": "场景名称",\n        "description": "场景描述"\n      }\n    }\n  ]\n}\n', '2026-02-03 10:23:07', '2026-02-03 12:05:03', 0),
	(9, 'FIRST_FRAME_GEN_V1', '首帧生成', 7, '你是一名专业的“镜头首帧文生图提示词编写器”（Prompt Engineer）。\n\n你的任务是：将【分镜描述】+【关联场景资产】+【关联角色资产】\n转译为一条【可直接用于图像生成模型的“首帧画面提示词”】【而非剧情复述】。\n\n【首帧生成的核心原则（极其重要）】\n- 首帧是“动作发生前或刚起势的一瞬间”\n- 画面中所有角色必须与场景处于【同一物理空间】\n- 严禁出现“人物像贴在背景上”的构图结果\n\n【场景视角自适应规则（必须遵守）】\n- 场景参考图用于提供“同一物理场所”的空间信息，而非固定镜头或构图模板\n- 生成画面时，必须基于分镜描述，自动选择最合理的场景视角、机位高度与观察方向\n- 允许在保持场景一致性的前提下，对场景进行视角变化（如正视、侧视、斜角、人物视角、近景或中景）\n- 禁止机械复刻场景参考图的原始构图，除非分镜内容明确要求\n- 所有视角变化必须被理解为“同一地点内的合理观察角度变化”，而非切换场景\n\n【场景描述去重规则（必须遵守）】\n- 场景段不得复用或套用任何固定的修辞模板或既有场景描写句式\n- 每次场景段的光线、天气、时间、氛围描述，必须仅由当前分镜内容决定\n- 禁止自动补全与以往生成结果相似的场景开场描写\n\n\n你必须在生成的提示词中，明确要求模型完成：\n- 人物与场景的空间一致性\n- 人物与地面的接触关系\n- 人物与环境光照、阴影、空气层次的相互作用\n\n【核心输出要求】\n1) 只输出一整段中文提示词（纯文本），禁止输出任何解释、分析或多余内容。\n2) 提示词中必须严格使用以下引用标记，格式必须完全一致：\n   - 场景引用：[场景参考图X]\n   - 角色引用：[角色参考图X]\n   X 按输入顺序从 1 开始编号。如 [场景参考图1],[角色参考图2],[角色参考图3]\n3) 提示词内容必须严格按以下顺序组织（顺序不可改变）：\n   A. 场景段（必须以 [场景参考图X] 开头）\n   B. 角色与构图关系段（按角色顺序，必须包含全部角色引用）\n   C. 场景-角色空间融合段（必须存在，不能省略）\n   D. 画面质感与氛围增强段\n   E. 风格与模型控制段（原样输出 style_template）\n\n【角色与构图规则】\n- 将分镜中的“奔跑、穿过、追逐、拉扯”等过程动作，\n  转换为“起势前 / 即将发生 / 动作定格的一瞬间”\n- 允许身体前倾、重心变化、衣角轻扬等“轻度动态暗示”\n- 禁止完整奔跑姿态、高速位移、多阶段动作\n\n【场景-角色空间融合规则（必须体现）】\n在生成的提示词中，你必须明确表达以下内容（可自然融入语言）：\n- 角色站立在场景真实地面上，双脚与地面形成清晰接触\n- 人物尺度、站位、透视与场景一致\n- 人物投射的阴影方向、长度与场景主光源一致\n- 人物边缘受环境光影响，存在逆光轮廓或柔和光晕\n- 人物与背景之间存在自然空气层次与光影过渡\n这些要求必须出现在最终提示词中，否则视为不合格输出。\n\n【画面质感规则】\n- 画面应强调整体真实感与统一性，而非微观写实\n- 避免过度强调汗毛、毛孔等容易破坏风格统一的细节\n- 光影应表现为自然、连贯的电影级环境光\n\n【禁止事项】\n- 禁止镜头运动描述（推、拉、摇、移）\n- 禁止多时间点叙述（先……然后……）\n- 禁止任何元语言（如“这是一个”“画面表现为”“如下”）\n- 禁止新增角色或未在输入中出现的元素\n\n【输入数据】\n- 分镜描述（shotDescription）：\n{{shot_description}}\n\n- 关联场景资产数组（scenes，包含 refIndex / name / description）：\n{{scenes_json}}\n\n- 关联角色资产数组（characters，包含 refIndex / name / description）：\n{{characters_json}}\n\n- 风格模板（style_template，必须原样输出在最后）：\n{{style_template}}\n\n【现在开始生成】\n只输出最终的一段中文首帧文生图提示词（纯文本），不要输出任何其他内容。\n', '2026-02-04 04:46:35', '2026-02-07 13:01:02', 0),
	(10, 'SHOT_VIDEO_GEN_V1', '分镜视频生成', 5, '你是一名专业的视频生成提示词编写器（Video Prompt Engineer）。\n\n你的任务是：在【首帧画面已确定】的前提下，\n将分镜描述转译为【视频生成模型可直接执行的电影级动态画面提示词】。\n\n【阶段约束（极其重要）】\n- 当前阶段为“视频生成阶段”，不是图像生成阶段\n- 所有场景与角色外观均已由首帧锁定\n- 你生成的提示词中【禁止出现任何参考图标记或引用编号】\n\n严禁出现以下内容：\n- [场景参考图X]\n- [角色参考图X]\n- 任何形式的“参考图”“引用”“编号”\n\n【输出规则（严格）】\n- 只允许输出四行内容\n- 不允许输出任何编号、标题、解释或说明性文字\n- 不允许输出多余空行\n\n输出格式必须严格如下：\n第一行：一整段连续的动态画面描述（自然语言，不换行）\n第二行：以 **运镜**: 开头\n第三行：以 **音效**: 开头\n第四行：以 **台词**: 开头\n\n若无台词，第四行必须写为：\n**台词**: 无\n\n【动态画面描述核心规则（必须遵守）】\n- 动态画面必须从“首帧定格姿态”自然展开\n- 动作应体现明确的速度变化与节奏推进（起势 → 加速 → 稳定或强化）\n- 描述中必须出现画面空间关系的重构（构图展开、视角变化、前中远层级关系变化）\n- 构图变化过程中，人物始终保持为画面视觉主体，\n  人物轮廓、动作与服装细节优先级高于环境展示\n- 所谓“拉远”仅指空间关系的展开，而非人物在画面中占比的显著缩小\n- 必须体现横向视差或纵深变化，\n  禁止全程仅使用前后方向的背景模糊\n- 禁止使用“远景”“人物融入环境”“背影远去”等会弱化人物细节的表达\n- 若人物细节随镜头推移明显下降，视为低质量输出\n\n【运镜规则（电影级）】\n- 允许在单一连续镜头内出现多个运镜阶段\n- 运镜可以在不切镜头的前提下发生：\n  - 轻微前推\n  - 侧向滑移\n  - 构图拉开或视角重置\n- 运镜必须服务于空间关系变化，同时保持人物比例与清晰度稳定\n\n【音效规则】\n- 仅描述环境音与动作音\n- 音效应随空间与速度变化产生层次变化，但不得压过人物动作存在感\n\n【输入】\n分镜描述：\n{{shot_description}}\n\n首帧提示词（仅用于理解，不复述、不引用）：\n{{first_frame_prompt}}\n\n【现在开始】\n只输出最终四行视频生成提示词，不要输出任何其他内容。\n', '2026-02-05 03:31:29', '2026-02-05 04:57:54', 0);

-- 导出  表 yihendrama.prompt_template_default 结构
DROP TABLE IF EXISTS `prompt_template_default`;
CREATE TABLE IF NOT EXISTS `prompt_template_default` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `prompt_template_id` bigint NOT NULL COMMENT '默认使用的提示词模板ID',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：1-启用，0-禁用',
  `scene_code` int NOT NULL COMMENT '业务场景（SceneCode 枚举）',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注说明',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint DEFAULT '0' COMMENT '0-未删除，1-已删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- 正在导出表  yihendrama.prompt_template_default 的数据：~6 rows (大约)
DELETE FROM `prompt_template_default`;
INSERT INTO `prompt_template_default` (`id`, `prompt_template_id`, `status`, `scene_code`, `remark`, `create_time`, `update_time`, `is_deleted`) VALUES
	(1, 1, 1, 2, NULL, '2026-01-23 03:28:57', '2026-01-23 18:14:06', 0),
	(2, 2, 1, 3, NULL, '2026-01-23 03:31:31', '2026-01-23 03:31:31', 0),
	(3, 7, 1, 1, NULL, '2026-01-31 11:11:32', '2026-01-31 11:11:32', 0),
	(4, 8, 1, 4, NULL, '2026-02-03 10:23:07', '2026-02-03 10:23:07', 0),
	(5, 9, 1, 7, NULL, '2026-02-04 04:46:35', '2026-02-04 04:46:35', 0),
	(6, 10, 1, 5, NULL, '2026-02-05 03:31:29', '2026-02-05 03:31:29', 0);

-- 导出  表 yihendrama.scene 结构
DROP TABLE IF EXISTS `scene`;
CREATE TABLE IF NOT EXISTS `scene` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '场景ID',
  `episode_id` bigint NOT NULL COMMENT '所属章节ID',
  `project_id` bigint NOT NULL COMMENT '所属项目ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '场景名称',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '场景描述',
  `thumbnail` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '场景缩略图URL',
  `is_deleted` tinyint DEFAULT '0' COMMENT '逻辑删除: 0-未删除 1-已删除',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_project_id` (`project_id`) USING BTREE,
  KEY `idx_episode_id` (`episode_id`)
) ENGINE=InnoDB AUTO_INCREMENT=72 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='场景表';

-- 正在导出表  yihendrama.scene 的数据：~0 rows (大约)
DELETE FROM `scene`;

-- 导出  表 yihendrama.storyboard 结构
DROP TABLE IF EXISTS `storyboard`;
CREATE TABLE IF NOT EXISTS `storyboard` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '分镜ID',
  `episode_id` bigint NOT NULL COMMENT '所属章节ID',
  `shot_number` int NOT NULL COMMENT '镜头序号',
  `shot_type` tinyint DEFAULT '1' COMMENT '镜头类型: 1-远景 2-中景 3-近景 4-特写',
  `duration` int DEFAULT '5' COMMENT '镜头时长(秒)',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '画面描述',
  `dialogue` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '台词',
  `thumbnail` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '分镜缩略图URL',
  `video_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '分镜视频URL',
  `image_prompt` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '图片生成提示词',
  `video_prompt` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '视频生成提示词',
  `order_index` int DEFAULT '0' COMMENT '排序索引',
  `is_deleted` tinyint DEFAULT '0' COMMENT '逻辑删除: 0-未删除 1-已删除',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_episode_id` (`episode_id`),
  KEY `idx_shot_number` (`shot_number`),
  CONSTRAINT `fk_storyboard_episode` FOREIGN KEY (`episode_id`) REFERENCES `episode` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='分镜表';

-- 正在导出表  yihendrama.storyboard 的数据：~0 rows (大约)
DELETE FROM `storyboard`;

-- 导出  表 yihendrama.storyboard_character 结构
DROP TABLE IF EXISTS `storyboard_character`;
CREATE TABLE IF NOT EXISTS `storyboard_character` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `episode_id` bigint NOT NULL COMMENT '所属章节ID',
  `storyboard_id` bigint NOT NULL COMMENT '分镜ID',
  `character_id` bigint NOT NULL COMMENT '角色ID',
  `project_id` bigint NOT NULL COMMENT '项目ID',
  `is_deleted` tinyint DEFAULT '0' COMMENT '逻辑删除: 0-未删除 1-已删除',
  `create_time` timestamp NOT NULL DEFAULT (now()) COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT (now()) ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=82 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 正在导出表  yihendrama.storyboard_character 的数据：~0 rows (大约)
DELETE FROM `storyboard_character`;

-- 导出  表 yihendrama.storyboard_scene 结构
DROP TABLE IF EXISTS `storyboard_scene`;
CREATE TABLE IF NOT EXISTS `storyboard_scene` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `episode_id` bigint NOT NULL COMMENT '所属章节ID',
  `storyboard_id` bigint NOT NULL COMMENT '分镜ID',
  `scene_id` bigint NOT NULL COMMENT '场景ID',
  `project_id` bigint NOT NULL COMMENT '项目ID',
  `is_deleted` tinyint DEFAULT '0' COMMENT '逻辑删除: 0-未删除 1-已删除',
  `create_time` timestamp NOT NULL DEFAULT (now()) COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT (now()) ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=41 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 正在导出表  yihendrama.storyboard_scene 的数据：~0 rows (大约)
DELETE FROM `storyboard_scene`;

-- 导出  表 yihendrama.style_template 结构
DROP TABLE IF EXISTS `style_template`;
CREATE TABLE IF NOT EXISTS `style_template` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '模板ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '风格名称',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '风格描述',
  `preview` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '预览图URL',
  `parameters` json DEFAULT NULL COMMENT '风格参数配置',
  `is_default` tinyint DEFAULT '0' COMMENT '是否默认风格',
  `is_deleted` tinyint DEFAULT '0' COMMENT '逻辑删除: 0-未删除 1-已删除',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='风格模板表';

-- 正在导出表  yihendrama.style_template 的数据：~4 rows (大约)
DELETE FROM `style_template`;
INSERT INTO `style_template` (`id`, `name`, `description`, `preview`, `parameters`, `is_default`, `is_deleted`, `create_time`, `update_time`) VALUES
	(1, '写实风格', '现代写实风格', NULL, NULL, 0, 0, '2026-02-20 13:25:09', '2026-02-20 13:25:09'),
	(2, '动漫风格', '现代二次元动漫风格', NULL, NULL, 0, 0, '2026-01-23 12:42:03', '2026-02-11 13:42:59'),
	(3, '油画质感', '油画质感', NULL, NULL, 0, 0, '2026-02-20 13:26:01', '2026-02-20 13:26:01'),
	(4, '赛博朋克', '赛博朋克', NULL, NULL, 0, 0, '2026-02-20 13:26:19', '2026-02-20 13:26:19');

-- 导出  表 yihendrama.video_task 结构
DROP TABLE IF EXISTS `video_task`;
CREATE TABLE IF NOT EXISTS `video_task` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `project_id` bigint NOT NULL COMMENT '所属项目ID',
  `instance_id` bigint NOT NULL COMMENT '所属模型实例ID',
  `target_id` bigint NOT NULL COMMENT '当任务类型为1，则为角色id，当任务类型为2，则为分镜id',
  `task_id` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '' COMMENT '任务ID',
  `task_type` tinyint DEFAULT '1' COMMENT '任务类型: 1-角色视频生成 2-分镜视频合成 3-音视频合成',
  `status` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '状态: success',
  `progress` int DEFAULT '0' COMMENT '进度百分比',
  `overall_progress` int DEFAULT '0' COMMENT '总体进度',
  `video_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '生成的视频URL',
  `error_message` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '错误信息',
  `next_poll_at` timestamp NULL DEFAULT NULL COMMENT '下次允许查询的时间',
  `poll_count` int DEFAULT NULL COMMENT '轮询次数',
  `is_deleted` tinyint DEFAULT '0' COMMENT '逻辑删除: 0-未删除 1-已删除',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='视频任务表';

-- 正在导出表  yihendrama.video_task 的数据：~0 rows (大约)
DELETE FROM `video_task`;

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
