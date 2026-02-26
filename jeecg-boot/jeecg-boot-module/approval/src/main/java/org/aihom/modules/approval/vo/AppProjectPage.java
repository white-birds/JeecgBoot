package org.aihom.modules.approval.vo;

import java.util.List;
import org.aihom.modules.approval.entity.AppProject;
import org.aihom.modules.approval.entity.AppRecord;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.jeecgframework.poi.excel.annotation.ExcelEntity;
import org.jeecgframework.poi.excel.annotation.ExcelCollection;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import java.util.Date;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecg.common.constant.ProvinceCityArea;
import org.jeecg.common.util.SpringContextUtils;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @Description: 审批主表
 * @Author: jeecg-boot
 * @Date:   2026-01-20
 * @Version: V1.0
 */
@Data
@Schema(description="审批主表")
public class AppProjectPage {

	/**主键*/
	@Schema(description = "主键")
    private String id;
	/**创建人*/
	@Schema(description = "创建人")
    private String createBy;
	/**创建日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@Schema(description = "创建日期")
    private Date createTime;
	/**更新人*/
	@Schema(description = "更新人")
    private String updateBy;
	/**更新日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@Schema(description = "更新日期")
    private Date updateTime;
	/**申请标题*/
	@Excel(name = "申请标题", width = 15)
	@Schema(description = "申请标题")
    private String title;
	/**申请内容*/
	@Excel(name = "申请内容", width = 15)
	@Schema(description = "申请内容")
    private String content;
	/**申请类型*/
	@Excel(name = "申请类型", width = 15)
	@Schema(description = "申请类型")
    private String type;
	/**申请人账号*/
	@Excel(name = "申请人账号", width = 15)
	@Schema(description = "申请人账号")
    private String applicant;
	/**状态*/
	@Excel(name = "状态", width = 15)
	@Schema(description = "状态")
    private String status;
	/**当前审批人*/
	@Excel(name = "当前审批人", width = 15)
	@Schema(description = "当前审批人")
    private String currentApprover;

	@ExcelCollection(name="审批流转记录表")
	@Schema(description = "审批流转记录表")
	private List<AppRecord> appRecordList;

}
