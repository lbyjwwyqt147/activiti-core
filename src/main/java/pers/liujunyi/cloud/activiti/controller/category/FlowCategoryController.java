package pers.liujunyi.cloud.activiti.controller.category;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pers.liujunyi.cloud.activiti.domain.category.FlowCategoryDto;
import pers.liujunyi.cloud.activiti.domain.category.FlowCategoryQueryDto;
import pers.liujunyi.cloud.activiti.service.category.FlowCategoryElasticsearchService;
import pers.liujunyi.cloud.activiti.service.category.FlowCategoryService;
import pers.liujunyi.cloud.common.annotation.ApiVersion;
import pers.liujunyi.cloud.common.controller.BaseController;
import pers.liujunyi.cloud.common.restful.ResultInfo;
import pers.liujunyi.cloud.common.restful.ResultUtil;
import pers.liujunyi.cloud.security.domain.IdParamDto;

import javax.validation.Valid;

/***
 * 文件名称: FlowCategoryController.java
 * 文件描述: 流程分类 Controller
 * 公 司:
 * 内容摘要:
 * 其他说明:
 * 完成日期:2019年03月22日
 * 修改记录:
 * @version 1.0
 * @author ljy
 */
@Api(tags = "流程分类 API")
@RestController
public class FlowCategoryController extends BaseController {

    @Autowired
    private FlowCategoryService flowCategoryService;
    @Autowired
    private FlowCategoryElasticsearchService flowCategoryElasticsearchService;

    /**
     * 保存数据
     *
     * @param param
     * @return
     */
    @ApiOperation(value = "保存数据", notes = "适用于保存数据 请求示例：127.0.0.1:18080/api/v1/verify/flow/category/s")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "version", value = "版本号", paramType = "query", required = true, dataType = "integer", defaultValue = "v1")
    })
    @PostMapping(value = "verify/flow/category/s")
    @ApiVersion(1)
    public ResultInfo saveRecord(@Valid FlowCategoryDto param) {
        return this.flowCategoryService.saveRecord(param);
    }


    /**
     * 批量删除
     *
     * @param param 　 多个id 用 , 隔开
     * @return
     */
    @ApiOperation(value = "删除多条数据", notes = "适用于批量删除数据 请求示例：127.0.0.1:18080/api/v1/verify/flow/category/d/b")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "version", value = "版本号", paramType = "query", required = true, dataType = "integer", defaultValue = "v1"),
            @ApiImplicitParam(name = "ids", value = "ids",  required = true, dataType = "String")
    })
    @DeleteMapping(value = "verify/flow/category/d/b")
    @ApiVersion(1)
    public ResultInfo batchDelete(@Valid IdParamDto param) {
        return this.flowCategoryService.deleteBatch(param.getIdList());
    }

    /**
     * 分页列表数据
     *
     * @param query
     * @return
     */
    @ApiOperation(value = "分页列表数据", notes = "适用于分页grid 显示数据 请求示例：127.0.0.1:18080/api/v1/table/flow/category/g")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "version", value = "版本号", paramType = "query", required = true, dataType = "integer", defaultValue = "v1")
    })
    @GetMapping(value = "table/flow/category/g")
    @ApiVersion(1)
    public ResultInfo findPageGrid(FlowCategoryQueryDto query) {
        return this.flowCategoryElasticsearchService.findPageGird(query);
    }


    /**
     *  修改数据状态
     *
     * @param param
     * @return
     */
    @ApiOperation(value = "修改数据状态", notes = "适用于修改数据状态 请求示例：127.0.0.1:18080/api/v1/verify/flow/category/p")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "version", value = "版本号", paramType = "query", required = true, dataType = "integer", defaultValue = "v1"),
            @ApiImplicitParam(name = "ids", value = "ids",  required = true, dataType = "String"),
            @ApiImplicitParam(name = "status", value = "status",  required = true, dataType = "integer"),
            @ApiImplicitParam(name = "otherIds", value = "账户id",  required = true, dataType = "integer")
    })
    @PutMapping(value = "verify/flow/category/p")
    @ApiVersion(1)
    public ResultInfo updateDataStatus(@Valid IdParamDto param ) {
        return this.flowCategoryService.updateStatus(param.getStatus(), param.getIdList());
    }


    /**
     * 根据id 获取详细信息
     * @param id
     * @return
     */
    @ApiOperation(value = "根据id 获取详细信息", notes = "适用于根据id 获取详细信息 请求示例：127.0.0.1:18080/api/v1/table/flow/category/{id}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "version", value = "版本号", paramType = "query", required = true, dataType = "integer", defaultValue = "v1"),
            @ApiImplicitParam(name = "id", value = "id", paramType = "path",   required = true, dataType = "Long")
    })
    @GetMapping(value = "table/flow/category/{id}")
    @ApiVersion(1)
    public ResultInfo findById(@PathVariable(name = "id") Long id) {
        return ResultUtil.success(this.flowCategoryElasticsearchService.findById(id));
    }

    /**
     * 员工下拉框数据
     * @param query
     * @return
     */
    @ApiOperation(value = "员工下拉框数据", notes = "员工下拉框数据 获取详细信息 请求示例：127.0.0.1:18080/api/v1/table/flow/category/select")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "version", value = "版本号", paramType = "query", required = true, dataType = "integer", defaultValue = "v1"),
    })
    @GetMapping(value = "table/flow/category/select")
    @ApiVersion(1)
    public ResultInfo flowCategorySelect(FlowCategoryQueryDto query) {
        return ResultUtil.success(this.flowCategoryElasticsearchService.categorySelect(query));
    }

    /**
     *  同步数据到es中
     * @param
     * @return
     */
    @ApiOperation(value = "同步数据", notes = "同步数据 请求示例：127.0.0.1:18080/api/v1/verify/flow/category/sync")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "version", value = "版本号", paramType = "query", required = true, dataType = "integer", defaultValue = "v1"),
    })
    @PostMapping(value = "verify/flow/category/sync")
    @ApiVersion(1)
    public ResultInfo syncDataToElasticsearch() {
        return this.flowCategoryService.syncDataToElasticsearch();
    }

    /**
     * 验证 名称 是否存在
     * @param categoryName  最新值
     * @param history  历史值
     * @return
     */
    @ApiOperation(value = "验证 名称 是否存在", notes = "适用于验证 名称 是否存在 请求示例：127.0.0.1:18080/api/v1/table/flow/category/verify/name")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "version", value = "版本号", paramType = "query", required = true, dataType = "integer", defaultValue = "v1"),
            @ApiImplicitParam(name = "categoryName", value = "名称",  required = true, dataType = "String"),
            @ApiImplicitParam(name = "history", value = "历史名称",  required = false, dataType = "String")
    })
    @GetMapping(value = "table/flow/category/verify/name")
    @ApiVersion(1)
    public String  verifyCategoryName(String categoryName,  String history) {
        return this.flowCategoryElasticsearchService.verifyCategoryName(categoryName, history);
    }
}
