package org.aihom.modules.approval.mapper;

import java.util.List;
import org.aihom.modules.approval.entity.AppRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Description: 审批流转记录表
 * @Author: jeecg-boot
 * @Date:   2026-01-20
 * @Version: V1.0
 */
public interface AppRecordMapper extends BaseMapper<AppRecord> {

	/**
	 * 通过主表id删除子表数据
	 *
	 * @param mainId 主表id
	 * @return boolean
	 */
	public boolean deleteByMainId(@Param("mainId") String mainId);

  /**
   * 通过主表id查询子表数据
   *
   * @param mainId 主表id
   * @return List<AppRecord>
   */
	public List<AppRecord> selectByMainId(@Param("mainId") String mainId);
}
