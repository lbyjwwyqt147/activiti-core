package pers.liujunyi.cloud.activiti.service.model.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.Model;
import org.activiti.engine.repository.ModelQuery;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pers.liujunyi.cloud.activiti.domain.model.FlowModelDto;
import pers.liujunyi.cloud.activiti.domain.model.FlowModelQueryDto;
import pers.liujunyi.cloud.activiti.service.model.FlowModelService;
import pers.liujunyi.cloud.activiti.util.ModelDataJsonConstants;
import pers.liujunyi.cloud.common.restful.ResultInfo;
import pers.liujunyi.cloud.common.restful.ResultUtil;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/***
 * 文件名称: FlowModelServiceImpl.java
 * 文件描述: 工作流模型 Service Impl
 * 公 司:
 * 内容摘要:
 * 其他说明:
 * 完成日期:2019年11月05日
 * 修改记录:
 * @version 1.0
 * @author ljy
 */
@Service
public class FlowModelServiceImpl implements FlowModelService {

    /**
     * 负责对流程定义文件的管理，操作一些静态文件（流程xml、流程图片），获取部署对象和资源对象
     * 详情参考 https://www.cnblogs.com/liuqing576598117/p/9815023.html
     */
    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public ResultInfo findPageGird(FlowModelQueryDto query) {
        // 创建Model查询 并且指定查询条件、分页参数、排序字段和排序方式
        ModelQuery modelQuery = this.repositoryService.createModelQuery();
        if (StringUtils.isNotBlank(query.getFlowModelName())) {
            // 模糊查询 必须这样写 不然模糊查询无效
             modelQuery.modelNameLike("%" + query.getFlowModelName() + "%");
        }
        if (StringUtils.isNotBlank(query.getFlowModelKey())) {
            modelQuery.modelKey(query.getFlowModelKey());
        }
        if (StringUtils.isNotBlank(query.getFlowModelCategory())) {
            modelQuery.modelCategory(query.getFlowModelCategory());
        }
        // 分页参数 类似与MySQL的分页功能，第一个参数是从第几条开始，第二个参数是一共几条
        List<Model> modelList  = modelQuery.orderByCreateTime().desc().listPage(query.getFirstResult(), query.getPageSize());
        return ResultUtil.success(modelList);
    }

    @Override
    public ResultInfo saveRecord(FlowModelDto record) {
        ResultInfo result = null;
        try {
            // 创建模型对象
            Model modelData  = this.repositoryService.newModel();
            ObjectNode modelObjectNode = this.objectMapper.createObjectNode();
            // 设置属性
            modelObjectNode.put(ModelDataJsonConstants.MODEL_NAME, record.getFlowModelName());
            modelObjectNode.put(ModelDataJsonConstants.MODEL_REVISION, 1);
            String description = StringUtils.defaultString(record.getDescription());
            modelObjectNode.put(ModelDataJsonConstants.MODEL_DESCRIPTION, description);
            // 设置对象值
            modelData.setMetaInfo(modelObjectNode.toString());
            modelData.setName(record.getFlowModelName());
            modelData.setKey(StringUtils.defaultString(record.getFlowModelKey()));
            //模型分类 结合自己的业务逻辑
            modelData.setCategory(record.getFlowModelCategory());
            this.repositoryService.saveModel(modelData);
            // ModelEditorSource
            ObjectNode editorNode = this.objectMapper.createObjectNode();
            editorNode.put("id", "canvas");
            editorNode.put("resourceId", "canvas");
            ObjectNode stencilSetNode = this.objectMapper.createObjectNode();
            stencilSetNode.put("namespace", "http://b3mn.org/stencilset/bpmn2.0#");
            editorNode.set("stencilset", stencilSetNode);
            this.repositoryService.addModelEditorSource(modelData.getId(), editorNode.toString().getBytes("utf-8"));
            result = ResultUtil.success();
            result.setMessage("流程模型创建成功.");
        } catch (Exception e) {
            e.printStackTrace();
            result = ResultUtil.fail();
            result.setMessage("流程模型创建失败.");
        }
        return result;
    }

    @Override
    public ResultInfo deleteBatch(List<String> ids) {
        for(String id : ids){
            this.repositoryService.deleteModel(id);
        }
        return ResultUtil.success();
    }

    @Override
    public void exportFlowModel(String modelId, HttpServletResponse response) {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        try {
            // 获取模型数据
            Model modelData = this.repositoryService.getModel(modelId);
            BpmnJsonConverter jsonConverter = new BpmnJsonConverter();
            //获取节点信息
            byte[] arg0 = this.repositoryService.getModelEditorSource(modelData.getId());
            JsonNode editorNode = new ObjectMapper().readTree(arg0);
            //将节点信息转换为xml
            BpmnModel bpmnModel = jsonConverter.convertToBpmnModel(editorNode);
            BpmnXMLConverter xmlConverter = new BpmnXMLConverter();
            byte[] bpmnBytes = xmlConverter.convertToXML(bpmnModel);
            // 写入流中输出
            ByteArrayInputStream in = new ByteArrayInputStream(bpmnBytes);
            IOUtils.copy(in, response.getOutputStream());
            String filename = modelData.getName() + ".bpmn20.xml";
            response.setHeader("Content-Disposition", "attachment; filename=" + java.net.URLEncoder.encode(filename, "UTF-8"));
            response.flushBuffer();
        } catch (Exception e){
            PrintWriter out = null;
            try {
                out = response.getWriter();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            out.write("未找到对应数据");
            e.printStackTrace();
        }

    }

    @Override
    public ResultInfo deployFlowModel(String modelId) {
        ResultInfo result = null;
        try {
            // 根据modelId获取模型信息
            Model modelData = this.repositoryService.getModel(modelId);
            byte[] bytes = this.repositoryService.getModelEditorSource(modelData.getId());
            if (bytes == null) {
                result = ResultUtil.fail();
                result.setMessage("模型数据为空,请先设计流程并成功保存,再进行发布.");
                return result;
            }
            ObjectNode modelNode = (ObjectNode) new ObjectMapper().readTree(bytes);
            BpmnModel model = new BpmnJsonConverter().convertToBpmnModel(modelNode);
            if (model.getProcesses().size() == 0) {
                result = ResultUtil.fail();
                result.setMessage("数据模型不符要求,请至少设计一条主线流程.");
                return result;
            }
            // 转换为xml对象
            byte[] bpmnBytes = new BpmnXMLConverter().convertToXML(model);
            // 部署对象以模型对象的名称命名
            String processName = modelData.getName() + ".bpmn20.xml";
            // 部署转换出来的xml对象
            Deployment deployment = this.repositoryService.createDeployment().name(modelData.getName())
                    .addString(processName, new String(bpmnBytes,"utf-8")).deploy();
            // 设置流程模型部署ID
            modelData.setDeploymentId(deployment.getId());
            this.repositoryService.saveModel(modelData);
            result = ResultUtil.success();
            result.setMessage("流程部署发布成功.");
        } catch (Exception e) {
            e.printStackTrace();
            result = ResultUtil.fail();
            result.setMessage("流程部署发布失败.");
        }
        return result;
    }
}