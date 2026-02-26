package org.aihom.modules.approval.service.impl;

import org.aihom.modules.approval.entity.AppRecord;
import org.aihom.modules.approval.mapper.AppRecordMapper;
import org.aihom.modules.approval.service.IAppRecordService;
import org.springframework.stereotype.Service;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Description: 审批流转记录表
 * @Author: jeecg-boot
 * @Date:   2026-01-20
 * @Version: V1.0
 */
@Service
public class AppRecordServiceImpl extends ServiceImpl<AppRecordMapper, AppRecord> implements IAppRecordService {
	
	@Autowired
	private AppRecordMapper appRecordMapper;
	
	@Override
	public List<AppRecord> selectByMainId(String mainId) {
		return appRecordMapper.selectByMainId(mainId);
	}
}
