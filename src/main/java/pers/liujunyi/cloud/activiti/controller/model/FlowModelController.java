package pers.liujunyi.cloud.activiti.controller.model;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import pers.liujunyi.cloud.activiti.domain.model.FlowModelDto;
import pers.liujunyi.cloud.activiti.domain.model.FlowModelQueryDto;
import pers.liujunyi.cloud.activiti.service.model.FlowModelService;
import pers.liujunyi.cloud.common.annotation.ApiVersion;
import pers.liujunyi.cloud.common.controller.BaseController;
import pers.liujunyi.cloud.common.restful.ResultInfo;
import pers.liujunyi.cloud.common.util.SystemUtils;

import javax.servlet.http.HttpServletResponse;

/***
 * 文件名称: FlowModelController.java
 * 文件描述: 流程模型 Controller
 * 公 司:
 * 内容摘要:
 * 其他说明:
 * 完成日期:2019年11月05日
 * 修改记录:
 * @version 1.0
 * @author ljy
 */
@Api(tags = "流程模型 API")
@RestController
public class FlowModelController extends BaseController {

    @Autowired
    private FlowModelService flowModelService;

    /**
     * 分页列表
     * @param query
     * @return
     */
    @ApiOperation(value = "分页列表", notes = "适用于分页列表 请求示例：127.0.0.1:18080/api/v1/table/flow/model/g")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "version", value = "版本号", paramType = "query", required = true, dataType = "integer", defaultValue = "v1")
    })
    @GetMapping(value = "table/flow/model/g")
    @ApiVersion(1)
    public ResultInfo findPageGird(FlowModelQueryDto query) {
        return this.flowModelService.findPageGird(query);
    }

    /**
     * 保存模型
     * @param record
     * @return
     */
    @ApiOperation(value = "保存模型", notes = "适用于保存数据 请求示例：127.0.0.1:18080/api/v1/verify/flow/model/s")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "version", value = "版本号", paramType = "query", required = true, dataType = "integer", defaultValue = "v1")
    })
    @PostMapping(value = "verify/flow/model/s")
    @ApiVersion(1)
    public ResultInfo saveRecord(FlowModelDto record) {
        return this.flowModelService.saveRecord(record);
    }

    /**
     * 批量删除
     * @param ids
     * @return
     */
    @ApiOperation(value = "删除多条数据", notes = "适用于批量删除数据 请求示例：127.0.0.1:18080/api/v1/verify/flow/model/d/b")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "version", value = "版本号", paramType = "query", required = true, dataType = "integer", defaultValue = "v1"),
            @ApiImplicitParam(name = "ids", value = "ids",  required = true, dataType = "String")
    })
    @DeleteMapping(value = "verify/flow/model/d/b")
    @ApiVersion(1)
    public ResultInfo deleteBatch(String ids) {
        return this.flowModelService.deleteBatch(SystemUtils.stringToList(ids));
    }

    /**
     * 导出model的xml文件
     * @param modelId
     * @param response
     */
    @ApiOperation(value = "导出model的xml文件", notes = "适用于导出model的xml文件 请求示例：127.0.0.1:18080/api/v1/verify/flow/model/export")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "version", value = "版本号", paramType = "query", required = true, dataType = "integer", defaultValue = "v1"),
            @ApiImplicitParam(name = "modelId", value = "模型ID",  required = true, dataType = "String")
    })
    @PostMapping(value = "verify/flow/model/export")
    @ApiVersion(1)
    public void exportFlowModel(String modelId, HttpServletResponse response) {
        this.flowModelService.exportFlowModel(modelId, response);
    }

    /**
     * 部署发布模型
     * @param modelId
     * @return
     */
    @ApiOperation(value = "部署发布模型", notes = "适用于部署发布模型 请求示例：127.0.0.1:18080/api/v1/verify/flow/model/deploy")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "version", value = "版本号", paramType = "query", required = true, dataType = "integer", defaultValue = "v1"),
            @ApiImplicitParam(name = "modelId", value = "模型ID",  required = true, dataType = "String")
    })
    @PostMapping(value = "verify/flow/model/deploy")
    @ApiVersion(1)
    public ResultInfo deployFlowModel(String modelId) {
        return this.flowModelService.deployFlowModel(modelId);
    }

    /**
     * 验证 标识key 是否存在
     * @param flowModelKey  最新值
     * @param history  历史值
     * @return
     */
    @ApiOperation(value = "验证 标识key 是否存在", notes = "适用于验证 标识key 是否存在 请求示例：127.0.0.1:18080/api/v1/table/flow/model/verify/key")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "version", value = "版本号", paramType = "query", required = true, dataType = "integer", defaultValue = "v1"),
            @ApiImplicitParam(name = "flowModelKey", value = "标识KEY",  required = true, dataType = "String"),
            @ApiImplicitParam(name = "history", value = "历史标识KEY",  required = false, dataType = "String")
    })
    @GetMapping(value = "table/flow/model/verify/key")
    @ApiVersion(1)
    public String  verifyModelKey(String flowModelKey,  String history) {
        return this.flowModelService.verifyModelKey(flowModelKey, history);
    }

}
