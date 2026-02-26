package org.aihom.modules.superset.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;
import java.util.Map;

@Mapper
public interface SupersetMetadataMapper {
    /**
     * 查出所有已开启嵌入的仪表盘
     * lower(hex(uuid)) 把二进制转为 32 位十六进制字符串
     */
    @Select("SELECT d.dashboard_title as name, lower(hex(ed.uuid)) as rawUuid " +
            "FROM dashboards d " +
            "JOIN embedded_dashboards ed ON d.id = ed.dashboard_id")
    List<Map<String, Object>> selectEmbeddedDashboards();
}