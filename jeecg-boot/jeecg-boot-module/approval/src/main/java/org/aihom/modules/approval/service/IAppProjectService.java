package org.aihom.modules.approval.service;

import org.aihom.modules.approval.entity.AppRecord;
import org.aihom.modules.approval.entity.AppProject;
import com.baomidou.mybatisplus.extension.service.IService;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * @Description: 审批主表
 * @Author: jeecg-boot
 * @Date:   2026-01-20
 * @Version: V1.0
 */
public interface IAppProjectService extends IService<AppProject> {

	/**
	 * 添加一对多
	 *
	 * @param appProject
	 * @param appRecordList
	 */
	public void saveMain(AppProject appProject,List<AppRecord> appRecordList) ;
	
	/**
	 * 修改一对多
	 *
   * @param appProject
   * @param appRecordList
	 */
	public void updateMain(AppProject appProject,List<AppRecord> appRecordList);
	
	/**
	 * 删除一对多
	 *
	 * @param id
	 */
	public void delMain (String id);
	
	/**
	 * 批量删除一对多
	 *
	 * @param idList
	 */
	public void delBatchMain (Collection<? extends Serializable> idList);
	
}
