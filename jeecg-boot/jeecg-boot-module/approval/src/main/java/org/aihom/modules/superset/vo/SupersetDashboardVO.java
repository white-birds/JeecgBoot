package org.aihom.modules.superset.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SupersetDashboardVO {
    private String name;          // 大屏标题
    private String embeddedUuid;  // 格式化后的标准 UUID
}