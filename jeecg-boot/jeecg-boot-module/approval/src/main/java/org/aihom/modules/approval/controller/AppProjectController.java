package org.aihom.modules.approval.controller;

import java.io.UnsupportedEncodingException;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.HashMap;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.jeecg.common.system.vo.LoginUser;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.query.QueryRuleEnum;
import org.jeecg.common.util.oConvertUtils;
import org.jeecgframework.poi.excel.entity.enmus.ExcelType;
import org.aihom.modules.approval.entity.AppRecord;
import org.aihom.modules.approval.entity.AppProject;
import org.aihom.modules.approval.vo.AppProjectPage;
import org.aihom.modules.approval.service.IAppProjectService;
import org.aihom.modules.approval.service.IAppRecordService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import com.alibaba.fastjson.JSON;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.apache.shiro.authz.annotation.RequiresPermissions;


/**
 * @Description: 审批主表
 * @Author: jeecg-boot
 * @Date: 2026-01-20
 * @Version: V1.0
 */
@Tag(name = "审批主表")
@RestController
@RequestMapping("/approval/appProject")
@Slf4j
public class AppProjectController {
    @Autowired
    private IAppProjectService appProjectService;
    @Autowired
    private IAppRecordService appRecordService;

    /**
     * 分页列表查询
     *
     * @param appProject
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    //@AutoLog(value = "审批主表-分页列表查询")
    @Operation(summary = "审批主表-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<AppProject>> queryPageList(AppProject appProject,
                                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                   HttpServletRequest req) {
        QueryWrapper<AppProject> queryWrapper = QueryGenerator.initQueryWrapper(appProject, req.getParameterMap());
        Page<AppProject> page = new Page<AppProject>(pageNo, pageSize);
        IPage<AppProject> pageList = appProjectService.page(page, queryWrapper);
        return Result.OK(pageList);
    }

    /**
     * 添加
     *
     * @param appProjectPage
     * @return
     */
    @AutoLog(value = "审批主表-添加")
    @Operation(summary = "审批主表-添加")
    @RequiresPermissions("approval:app_project:add")
    @PostMapping(value = "/add")
    public Result<String> add(@RequestBody AppProjectPage appProjectPage) {
        AppProject appProject = new AppProject();
        BeanUtils.copyProperties(appProjectPage, appProject);
        appProjectService.saveMain(appProject, appProjectPage.getAppRecordList());
        return Result.OK("添加成功！");
    }

    /**
     * 编辑
     *
     * @param appProjectPage
     * @return
     */
    @AutoLog(value = "审批主表-编辑")
    @Operation(summary = "审批主表-编辑")
    @RequiresPermissions("approval:app_project:edit")
    @RequestMapping(value = "/edit", method = {RequestMethod.PUT, RequestMethod.POST})
    public Result<String> edit(@RequestBody AppProjectPage appProjectPage) {
        AppProject appProject = new AppProject();
        BeanUtils.copyProperties(appProjectPage, appProject);
        AppProject appProjectEntity = appProjectService.getById(appProject.getId());
        if (appProjectEntity == null) {
            return Result.error("未找到对应数据");
        }
        appProjectService.updateMain(appProject, appProjectPage.getAppRecordList());
        return Result.OK("编辑成功!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "审批主表-通过id删除")
    @Operation(summary = "审批主表-通过id删除")
    @RequiresPermissions("approval:app_project:delete")
    @DeleteMapping(value = "/delete")
    public Result<String> delete(@RequestParam(name = "id", required = true) String id) {
        appProjectService.delMain(id);
        return Result.OK("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "审批主表-批量删除")
    @Operation(summary = "审批主表-批量删除")
    @RequiresPermissions("approval:app_project:deleteBatch")
    @DeleteMapping(value = "/deleteBatch")
    public Result<String> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.appProjectService.delBatchMain(Arrays.asList(ids.split(",")));
        return Result.OK("批量删除成功！");
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    //@AutoLog(value = "审批主表-通过id查询")
    @Operation(summary = "审批主表-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<AppProject> queryById(@RequestParam(name = "id", required = true) String id) {
        AppProject appProject = appProjectService.getById(id);
        if (appProject == null) {
            return Result.error("未找到对应数据");
        }
        return Result.OK(appProject);

    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    //@AutoLog(value = "审批流转记录表通过主表ID查询")
    @Operation(summary = "审批流转记录表主表ID查询")
    @GetMapping(value = "/queryAppRecordByMainId")
    public Result<List<AppRecord>> queryAppRecordListByMainId(@RequestParam(name = "id", required = true) String id) {
        List<AppRecord> appRecordList = appRecordService.selectByMainId(id);
        return Result.OK(appRecordList);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param appProject
     */
    @RequiresPermissions("approval:app_project:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, AppProject appProject) {

        // Step.1 组装查询条件查询数据
        QueryWrapper<AppProject> queryWrapper = QueryGenerator.initQueryWrapper(appProject, request.getParameterMap());
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

        //配置选中数据查询条件
        String selections = request.getParameter("selections");
        if (oConvertUtils.isNotEmpty(selections)) {
            List<String> selectionList = Arrays.asList(selections.split(","));
            queryWrapper.in("id", selectionList);
        }

        //Step.2 获取导出数据
        List<AppProject> appProjectList = appProjectService.list(queryWrapper);

        // Step.3 组装pageList
        List<AppProjectPage> pageList = new ArrayList<AppProjectPage>();
        for (AppProject main : appProjectList) {
            AppProjectPage vo = new AppProjectPage();
            BeanUtils.copyProperties(main, vo);
            List<AppRecord> appRecordList = appRecordService.selectByMainId(main.getId());
            vo.setAppRecordList(appRecordList);
            pageList.add(vo);
        }

        // Step.4 AutoPoi 导出Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        mv.addObject(NormalExcelConstants.FILE_NAME, "审批主表列表");
        mv.addObject(NormalExcelConstants.CLASS, AppProjectPage.class);
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("审批主表数据", "导出人:" + sysUser.getRealname(), "审批主表", ExcelType.XSSF));
        mv.addObject(NormalExcelConstants.DATA_LIST, pageList);
        return mv;
    }

    /**
     * 通过excel导入数据
     *
     * @param request
     * @param response
     * @return
     */
    @RequiresPermissions("approval:app_project:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            // 获取上传文件对象
            MultipartFile file = entity.getValue();
            ImportParams params = new ImportParams();
            params.setTitleRows(2);
            params.setHeadRows(1);
            params.setNeedSave(true);
            try {
                List<AppProjectPage> list = ExcelImportUtil.importExcel(file.getInputStream(), AppProjectPage.class, params);
                for (AppProjectPage page : list) {
                    AppProject po = new AppProject();
                    BeanUtils.copyProperties(page, po);
                    appProjectService.saveMain(po, page.getAppRecordList());
                }
                return Result.OK("文件导入成功！数据行数:" + list.size());
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return Result.error("文件导入失败:" + e.getMessage());
            } finally {
                try {
                    file.getInputStream().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return Result.OK("文件导入失败！");
    }

    /**
     * 获取审批统计数据
     *
     * @return
     */
    @GetMapping(value = "/statistics")
    public Result<Map<String, Object>> getStatistics() {
        Map<String, Object> statistics = new HashMap<>();

        // 总申请数
        long totalCount = appProjectService.count();
        statistics.put("totalCount", totalCount);

        // 已通过
        long approvedCount = appProjectService.count(
                new QueryWrapper<AppProject>().eq("status", "已通过")
        );
        statistics.put("approvedCount", approvedCount);

        // 待审批
        long pendingCount = appProjectService.count(
                new QueryWrapper<AppProject>().eq("status", "待审批")
        );
        statistics.put("pendingCount", pendingCount);

        // 已驳回
        long rejectedCount = appProjectService.count(
                new QueryWrapper<AppProject>().eq("status", "已驳回")
        );
        statistics.put("rejectedCount", rejectedCount);

        // 按类型统计
        List<Map<String, Object>> typeStats = new ArrayList<>();
        List<String> types = Arrays.asList("请假", "报销", "加班");
        for (String type : types) {
            Map<String, Object> typeStat = new HashMap<>();
            typeStat.put("type", type);
            long count = appProjectService.count(
                    new QueryWrapper<AppProject>().eq("type", type)
            );
            typeStat.put("count", count);
            typeStats.add(typeStat);
        }
        statistics.put("typeStats", typeStats);

        return Result.OK(statistics);
    }

}
