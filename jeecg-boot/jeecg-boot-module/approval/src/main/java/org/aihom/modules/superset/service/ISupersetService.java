package org.aihom.modules.superset.service;

import org.aihom.modules.superset.vo.SupersetDashboardVO;

import java.util.List;
import java.util.Map;

public interface ISupersetService {

    String getGuestTokenForDashboard(String dashboardId);

    String getGuestTokenForEmbeddedDashboard(String embeddedDashboardUuid);

    Map<String, String> getConfig();

    List<SupersetDashboardVO> getDashboardList();
}

