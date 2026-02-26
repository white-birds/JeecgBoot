package org.aihom.modules.approval.service;

import org.aihom.modules.approval.entity.AppRecord;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

/**
 * @Description: 审批流转记录表
 * @Author: jeecg-boot
 * @Date:   2026-01-20
 * @Version: V1.0
 */
public interface IAppRecordService extends IService<AppRecord> {

	/**
	 * 通过主表id查询子表数据
	 *
	 * @param mainId 主表id
	 * @return List<AppRecord>
	 */
	public List<AppRecord> selectByMainId(String mainId);
}
