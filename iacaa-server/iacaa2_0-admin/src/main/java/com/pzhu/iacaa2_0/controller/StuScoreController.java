package com.pzhu.iacaa2_0.controller;


import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import com.gapache.security.annotation.AuthResource;
import com.gapache.security.annotation.NeedAuth;
import com.pzhu.iacaa2_0.common.ActionResult;
import com.pzhu.iacaa2_0.entity.CheckLink;
import com.pzhu.iacaa2_0.entity.Course;
import com.pzhu.iacaa2_0.entity.StuScore;
import com.pzhu.iacaa2_0.entityVo.CheckLinkVo;
import com.pzhu.iacaa2_0.entityVo.StuScoreVo;
import com.pzhu.iacaa2_0.service.ICheckLinkService;
import com.pzhu.iacaa2_0.service.IStuScoreService;
import com.pzhu.iacaa2_0.utils.ExportFileUtils;
import com.pzhu.iacaa2_0.utils.FileUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author ZhaoZezhong
 * @since 2021-04-21
 */
@RestController
@RequestMapping("/stuScore")
@NeedAuth("StuScore")
public class StuScoreController {
    @Autowired
    IStuScoreService stuScoreService;

    @Autowired
    ICheckLinkService checkLinkService;

    @RequestMapping("list")
    @AuthResource(scope = "list", name = "学生成绩列表")
    public ActionResult list(@RequestBody StuScore stuScore){
        List<StuScore> stuScoreList = stuScoreService.list(stuScore);
        return ActionResult.ofSuccess(stuScoreList);
    }

    @RequestMapping("delete")
    @AuthResource(scope = "delete", name = "删除学生成绩")
    public ActionResult delete(@RequestBody StuScore stuScore){
        stuScoreService.removeById(stuScore.getId());
        return ActionResult.ofSuccess();
    }

    @RequestMapping("saveOrUpdate")
    @AuthResource(scope = "saveOrUpdate", name = "保存或更新学生成绩列表")
    public ActionResult saveOrUpdate(@RequestBody List<StuScore> stuScoreList){
        if(stuScoreList.isEmpty()){
            return ActionResult.ofFail("不能为空数据");
        }
        CheckLink checkLink = checkLinkService.getById(stuScoreList.get(0).getCheckLinkId());
        Map<String,String> check = new HashMap<>();
        for (StuScore stuScore : stuScoreList) {
            if(check.get(stuScore.getStuno()) == null){
                check.put(stuScore.getStuno(),"exsist");
            }else {
                return ActionResult.ofFail(String.format("已存在学生%S此考核环节分数",stuScore.getStuno()));
            }
            if(stuScore.getScore() == null || stuScore.getScore() < 0){
                return ActionResult.ofFail(String.format("学生%S成绩不合法数",stuScore.getStuno()));
            }
            if(stuScore.getStuno() == null || stuScore.getStuno().length() != 12){
                return ActionResult.ofFail(String.format("学号%S不合法",stuScore.getStuno()));
            }
            if (stuScore.getScore() > checkLink.getTargetScore()+0.00000001){
                return ActionResult.ofFail(String.format("学生%S成绩不得大于考核环节目标成绩:%S",stuScore.getStuno(),checkLink.getTargetScore()));
            }

            stuScore.setMixScore(stuScore.getScore()/checkLink.getTargetScore());
            if(stuScore.getId() == null){
                stuScore.setCreatedDate(LocalDateTime.now());
            }
            stuScore.setUpdateDate(LocalDateTime.now());
        }
        boolean b = stuScoreService.saveOrUpdateBatch(stuScoreList);
        Boolean aBoolean = stuScoreService.summaryCheckLinkScoreById(checkLink.getId());
        return b && aBoolean ? ActionResult.ofSuccess() : ActionResult.ofFail("保存失败");
    }

    @RequestMapping("/exportTemplate")
    @AuthResource(scope = "exportTemplate", name = "成绩导入模板")
    public void exportTemplate(HttpServletResponse response, @RequestBody StuScoreVo stuScoreVo) throws IOException {
//        FileUtils.download("D:/doc/","scoreImport.xslx",response);
        CheckLinkVo vo = new CheckLinkVo();
        vo.setYear(stuScoreVo.getYear());
        vo.setCourseId(stuScoreVo.getCourseId());
        List<CheckLink> list1 = checkLinkService.list(vo);

        ExportParams exportParams = new ExportParams();
        exportParams.setTitle("导入数据模板");
        Workbook workbook = ExcelExportUtil.exportExcel(exportParams,CheckLink.class,list1);
        try {
            response.reset();
            response.setCharacterEncoding("utf-8");
            // Content-disposition 告诉浏览器以下载的形式打开
            response.setHeader("Content-disposition", "attachment; filename=" + "fileName.xlsx");
            // application/ms-excel;charset=utf-8 告诉浏览器下载的文件是excel
            response.setContentType("application/ms-excel");
            workbook.write(response.getOutputStream());
            workbook.close();
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
    }
}
