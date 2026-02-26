package org.aihom.modules.superset.controller;

import org.aihom.modules.superset.service.ISupersetService;
import org.aihom.modules.superset.vo.SupersetDashboardVO;
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/superset")
public class SupersetController {

    @Autowired
    private ISupersetService supersetService;

    @GetMapping("/config")
    public Result<Map<String, String>> getConfig() {
        return Result.OK(supersetService.getConfig());
    }

    @GetMapping("/system-token")
    public Result<String> getSystemToken(
            @RequestParam(name = "dashboardId", required = false) String dashboardId,
            @RequestParam(name = "embeddedUuid", required = false) String embeddedUuid
    ) {
        if (embeddedUuid != null && !embeddedUuid.trim().isEmpty()) {
            return Result.OK(supersetService.getGuestTokenForEmbeddedDashboard(embeddedUuid));
        }
        if (dashboardId == null || dashboardId.trim().isEmpty()) {
            return Result.error("dashboardId 或 embeddedUuid 不能为空");
        }
        return Result.OK(supersetService.getGuestTokenForDashboard(dashboardId));
    }
    @GetMapping("/dashboards")
    public Result<List<SupersetDashboardVO>> getDashboards() {
        List<SupersetDashboardVO> list = supersetService.getDashboardList();
        return Result.OK(list);
    }
}

