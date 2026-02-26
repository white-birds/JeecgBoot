package org.aihom.modules.approval.entity;

import java.io.Serializable;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableLogic;
import org.jeecg.common.constant.ProvinceCityArea;
import org.jeecg.common.util.SpringContextUtils;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import java.util.Date;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.UnsupportedEncodingException;

/**
 * @Description: 审批流转记录表
 * @Author: jeecg-boot
 * @Date:   2026-01-20
 * @Version: V1.0
 */
@Schema(description="审批流转记录表")
@Data
@TableName("app_record")
public class AppRecord implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键")
    private String id;
	/**更新人*/
    @Schema(description = "更新人")
    private String updateBy;
	/**更新日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @Schema(description = "更新日期")
    private Date updateTime;
	/**关联主表ID*/
    @Schema(description = "关联主表ID")
    private String projectId;
	/**操作人*/
	@Excel(name = "操作人", width = 15)
    @Schema(description = "操作人")
    private String operator;
	/**操作动作*/
	@Excel(name = "操作动作", width = 15)
    @Schema(description = "操作动作")
    private String action;
	/**审批意见*/
	@Excel(name = "审批意见", width = 15)
    @Schema(description = "审批意见")
    private String comment;
}
