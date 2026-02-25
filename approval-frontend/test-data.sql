-- 审批模块测试数据
-- 使用前请确保数据库 jeecg-boot 已存在，并且表 app_project 和 app_record 已创建

USE jeecg_boot;

-- 清空现有数据（可选）
-- TRUNCATE TABLE app_record;
-- TRUNCATE TABLE app_project;

-- 插入测试审批数据（已通过/已驳回的记录使用最近1小时内的时间）
INSERT INTO `app_project` (`id`, `create_by`, `create_time`, `update_by`, `update_time`, `title`, `type`, `content`, `applicant`, `current_approver`, `status`) VALUES
('1001', 'admin', NOW(), 'admin', NOW(), '2024年春节假期申请', '请假', '申请春节假期7天，从2024年2月10日至2月16日。', 'admin', 'admin', '待审批'),
('1002', 'admin', DATE_SUB(NOW(), INTERVAL 50 MINUTE), 'manager', DATE_SUB(NOW(), INTERVAL 10 MINUTE), '办公用品采购报销', '报销', '采购办公用品：打印纸、笔记本、文件夹等，总计1280元。', 'admin', 'manager', '已通过'),
('1003', 'zhangsan', DATE_SUB(NOW(), INTERVAL 55 MINUTE), 'admin', DATE_SUB(NOW(), INTERVAL 15 MINUTE), '项目加班申请', '加班', '因项目紧急，申请本周末加班2天，预计完成核心功能开发。', 'zhangsan', 'admin', '已通过'),
('1004', 'lisi', DATE_SUB(NOW(), INTERVAL 45 MINUTE), 'admin', DATE_SUB(NOW(), INTERVAL 20 MINUTE), '病假申请', '请假', '因感冒发烧，需要请假3天休息治疗。', 'lisi', 'admin', '已驳回'),
('1005', 'wangwu', NOW(), 'admin', NOW(), '差旅费报销', '报销', '1月出差北京，机票、住宿、交通费用合计3500元。', 'wangwu', 'admin', '待审批'),
('1006', 'zhaoliu', NOW(), 'admin', NOW(), '年假申请', '请假', '申请年假5天，计划2月份外出旅游。', 'zhaoliu', 'admin', '待审批'),
('1007', 'admin', DATE_SUB(NOW(), INTERVAL 48 MINUTE), 'manager', DATE_SUB(NOW(), INTERVAL 35 MINUTE), '团建活动经费报销', '报销', '部门团建活动费用：餐饮、娱乐等，总计2800元。', 'admin', 'manager', '已通过'),
('1008', 'zhangsan', NOW(), 'admin', NOW(), '周末项目开发加班', '加班', '因客户需求变更，需要周末加班调整系统功能。', 'zhangsan', 'admin', '待审批'),
('1009', 'lisi', DATE_SUB(NOW(), INTERVAL 40 MINUTE), 'admin', DATE_SUB(NOW(), INTERVAL 25 MINUTE), '事假申请', '请假', '家里有急事需要处理，申请请假1天。', 'lisi', 'admin', '已通过'),
('1010', 'wangwu', DATE_SUB(NOW(), INTERVAL 52 MINUTE), 'admin', DATE_SUB(NOW(), INTERVAL 30 MINUTE), '培训费用报销', '报销', '参加外部技术培训，费用1500元。', 'wangwu', 'admin', '已驳回'),
('1011', 'admin', NOW(), 'manager', NOW(), '产假申请', '请假', '预产期在2月中旬，申请产假98天。', 'admin', 'manager', '待审批'),
('1012', 'zhaoliu', DATE_SUB(NOW(), INTERVAL 58 MINUTE), 'admin', DATE_SUB(NOW(), INTERVAL 40 MINUTE), '紧急项目加班', '加班', '生产环境出现问题，需要紧急加班处理。', 'zhaoliu', 'admin', '已通过'),
('1013', 'zhangsan', NOW(), 'admin', NOW(), '设备采购报销', '报销', '购买开发用笔记本电脑，费用8500元。', 'zhangsan', 'admin', '待审批'),
('1014', 'lisi', NOW(), 'admin', NOW(), '调休申请', '请假', '之前加班累计8小时，申请调休1天。', 'lisi', 'admin', '待审批'),
('1015', 'wangwu', DATE_SUB(NOW(), INTERVAL 42 MINUTE), 'admin', DATE_SUB(NOW(), INTERVAL 5 MINUTE), '年终加班申请', '加班', '年终总结报告需要加班整理，预计2天。', 'wangwu', 'admin', '已通过');

-- 插入审批记录（对应已审批的项目，使用最近时间）
INSERT INTO `app_record` (`id`, `update_by`, `update_time`, `project_id`, `operator`, `action`, `comment`) VALUES
('2001', 'manager', DATE_SUB(NOW(), INTERVAL 10 MINUTE), '1002', 'manager', '通过', '报销单据齐全，金额合理，同意报销。'),
('2002', 'admin', DATE_SUB(NOW(), INTERVAL 15 MINUTE), '1003', 'admin', '通过', '项目确实紧急，同意加班申请，注意休息。'),
('2003', 'admin', DATE_SUB(NOW(), INTERVAL 20 MINUTE), '1004', 'admin', '驳回', '建议先看医生开具病假证明再重新提交申请。'),
('2004', 'manager', DATE_SUB(NOW(), INTERVAL 35 MINUTE), '1007', 'manager', '通过', '团建活动有助于团队建设，同意报销。'),
('2005', 'admin', DATE_SUB(NOW(), INTERVAL 25 MINUTE), '1009', 'admin', '通过', '紧急情况可以理解，同意请假。'),
('2006', 'admin', DATE_SUB(NOW(), INTERVAL 30 MINUTE), '1010', 'admin', '驳回', '该培训未在年度培训计划内，暂不予报销。'),
('2007', 'admin', DATE_SUB(NOW(), INTERVAL 40 MINUTE), '1012', 'admin', '通过', '生产问题需要紧急处理，同意加班，辛苦了。'),
('2008', 'admin', DATE_SUB(NOW(), INTERVAL 5 MINUTE), '1015', 'admin', '通过', '年终确实工作量大，同意加班安排。');

-- 验证数据
SELECT '=== 审批项目统计 ===' as '';
SELECT status, COUNT(*) as count FROM app_project GROUP BY status;

SELECT '=== 最近审批列表（最近1小时内通过/驳回的）===' as '';
SELECT id, title, type, applicant, status, update_time 
FROM app_project 
WHERE (status = '已通过' OR status = '已驳回')
  AND update_time > DATE_SUB(NOW(), INTERVAL 1 HOUR)
ORDER BY update_time DESC;

SELECT '=== 审批记录统计 ===' as '';
SELECT action, COUNT(*) as count FROM app_record GROUP BY action;

