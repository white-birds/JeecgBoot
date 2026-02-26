package org.aihom.modules.approval.service.impl;

import org.aihom.modules.approval.entity.AppProject;
import org.aihom.modules.approval.entity.AppRecord;
import org.aihom.modules.approval.mapper.AppRecordMapper;
import org.aihom.modules.approval.mapper.AppProjectMapper;
import org.aihom.modules.approval.service.IAppProjectService;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import java.io.Serializable;
import java.util.List;
import java.util.Collection;

/**
 * @Description: 审批主表
 * @Author: jeecg-boot
 * @Date:   2026-01-20
 * @Version: V1.0
 */
@Service
public class AppProjectServiceImpl extends ServiceImpl<AppProjectMapper, AppProject> implements IAppProjectService {

	@Autowired
	private AppProjectMapper appProjectMapper;
	@Autowired
	private AppRecordMapper appRecordMapper;
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void saveMain(AppProject appProject, List<AppRecord> appRecordList) {
		appProjectMapper.insert(appProject);
		if(appRecordList!=null && appRecordList.size()>0) {
			for(AppRecord entity:appRecordList) {
				//外键设置
				entity.setProjectId(appProject.getId());
				appRecordMapper.insert(entity);
			}
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateMain(AppProject appProject,List<AppRecord> appRecordList) {
		appProjectMapper.updateById(appProject);
		
		//1.先删除子表数据
		appRecordMapper.deleteByMainId(appProject.getId());
		
		//2.子表数据重新插入
		if(appRecordList!=null && appRecordList.size()>0) {
			for(AppRecord entity:appRecordList) {
				//外键设置
				entity.setProjectId(appProject.getId());
				appRecordMapper.insert(entity);
			}
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delMain(String id) {
		appRecordMapper.deleteByMainId(id);
		appProjectMapper.deleteById(id);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delBatchMain(Collection<? extends Serializable> idList) {
		for(Serializable id:idList) {
			appRecordMapper.deleteByMainId(id.toString());
			appProjectMapper.deleteById(id);
		}
	}
	
}
