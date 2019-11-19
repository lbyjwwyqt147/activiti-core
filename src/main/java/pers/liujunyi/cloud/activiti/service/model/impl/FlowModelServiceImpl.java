package pers.liujunyi.cloud.activiti.service.model.impl;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.impl.util.json.XML;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.Model;
import org.activiti.engine.repository.ModelQuery;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import pers.liujunyi.cloud.activiti.domain.model.FlowModelDto;
import pers.liujunyi.cloud.activiti.domain.model.FlowModelQueryDto;
import pers.liujunyi.cloud.activiti.domain.model.FlowModelVo;
import pers.liujunyi.cloud.activiti.service.model.FlowModelService;
import pers.liujunyi.cloud.activiti.util.ModelDataJsonConstants;
import pers.liujunyi.cloud.common.restful.ResultInfo;
import pers.liujunyi.cloud.common.restful.ResultUtil;
import pers.liujunyi.cloud.common.util.Base64Convert;
import pers.liujunyi.cloud.common.util.DozerBeanMapperUtil;
import pers.liujunyi.cloud.common.util.UserContext;
import pers.liujunyi.cloud.security.entity.category.CategoryInfo;
import pers.liujunyi.cloud.security.service.category.CategoryInfoElasticsearchService;
import pers.liujunyi.cloud.security.util.SecurityConstant;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

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
    @Autowired
    private CategoryInfoElasticsearchService categoryInfoElasticsearchService;

    @Override
    public ResultInfo findPageGird(FlowModelQueryDto query) {
        List<FlowModelVo> resultData = new CopyOnWriteArrayList<>();
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
        Long lesseeId = UserContext.currentLesseeId();
        if (lesseeId != null) {
            modelQuery.modelTenantId(lesseeId.toString());
        }
        // 分页参数 类似与MySQL的分页功能，第一个参数是从第几条开始，第二个参数是一共几条
        List<Model> modelList  = modelQuery.orderByCreateTime().desc().listPage(query.getFirstResult(), query.getPageSize());
        Map<String, String> categoryNameMap = new ConcurrentHashMap<>();
        if (!CollectionUtils.isEmpty(modelList)) {
            modelList.stream().forEach(item -> {
                FlowModelVo modelVo = DozerBeanMapperUtil.copyProperties(item, FlowModelVo.class);
                JSONObject jsonObject = JSONObject.parseObject(item.getMetaInfo());
                modelVo.setDescription(jsonObject.getString("description"));
                String categoryName = null;
                if (categoryNameMap.containsKey(item.getCategory())) {
                    categoryName = categoryNameMap.get(item.getCategory());
                } else {
                    CategoryInfo categoryInfo = this.categoryInfoElasticsearchService.findById(Long.valueOf(item.getCategory()));
                    if (categoryInfo != null) {
                        categoryName = categoryInfo.getCategoryName();
                        categoryNameMap.put(item.getCategory(), categoryName);
                    }
                }
                modelVo.setCategoryName(categoryName);
                if (StringUtils.isBlank(item.getDeploymentId())) {
                    modelVo.setStatus(SecurityConstant.DISABLE_STATUS);
                } else {
                    modelVo.setStatus(SecurityConstant.ENABLE_STATUS);
                }
                resultData.add(modelVo);
            });
        }
        ResultInfo resultInfo = ResultUtil.success(resultData);
        long total = resultData.size();
        if (total == query.getPageSize()) {
            total = (long)modelQuery.list().size();
        }
        resultInfo.setTotal(total);
        return resultInfo;
    }

    @Override
    public ResultInfo saveRecord(FlowModelDto record) {
        ResultInfo result = null;
        try {
            // 创建模型对象
            Model modelData  = null;
            if (StringUtils.isNotBlank(record.getId())) {
                modelData = this.repositoryService.getModel(record.getId());
            } else {
                modelData  = this.repositoryService.newModel();
            }
            //将xml转为json
            org.activiti.engine.impl.util.json.JSONObject jsonObject = XML.toJSONObject(record.getBpmn_xml());
            String test = XML.toString(jsonObject);
            ObjectNode modelObjectNode = this.objectMapper.createObjectNode();
            // 设置属性
            modelObjectNode.put(ModelDataJsonConstants.MODEL_NAME, record.getFlowModelName());
            modelObjectNode.put(ModelDataJsonConstants.MODEL_REVISION, 1);
            String description = StringUtils.defaultString(record.getDescription());
            modelObjectNode.put(ModelDataJsonConstants.MODEL_DESCRIPTION, description);
            // 设置对象值
            modelData.setMetaInfo(modelObjectNode.toString());
            modelData.setName(record.getFlowModelName());
            Long lesseeId = UserContext.currentLesseeId();
            if (lesseeId != null) {
                modelData.setTenantId(lesseeId.toString());
            }
            modelData.setKey(StringUtils.defaultString(record.getFlowModelKey()));
            //模型分类 结合自己的业务逻辑
            modelData.setCategory(record.getFlowModelCategory());
            this.repositoryService.saveModel(modelData);

            // 将流程模型xml数据转为二进制数据 保存到 ACT_GE_BYTEARRAY 表中 name = source
            this.repositoryService.addModelEditorSource(modelData.getId(),  record.getJson_xml().getBytes("utf-8"));

            // 将svg 转换为png
            InputStream svgStream = new ByteArrayInputStream(record.getSvg_xml().getBytes("utf-8"));
            TranscoderInput input = new TranscoderInput(svgStream);
            PNGTranscoder transcoder = new PNGTranscoder();
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            TranscoderOutput output = new TranscoderOutput(outStream);
            transcoder.transcode(input, output);
            final byte[] resultByte = outStream.toByteArray();
            // 将png图片转为二进制数据 保存到 ACT_GE_BYTEARRAY 表中 name = source-extra
            this.repositoryService.addModelEditorSourceExtra(modelData.getId(), resultByte);
            outStream.close();

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
    public ResultInfo deleteBatch(List<String> ids, List<String> deploymentIds) {
        for(String id : ids){
            this.repositoryService.deleteModel(id);
        }
        if (!CollectionUtils.isEmpty(deploymentIds)) {
            for(String deploymentId : deploymentIds){
                if (StringUtils.isNotBlank(deploymentId)) {
                    // 删除部署信息 1.true级联删除；2.false非级联删除
                    this.repositoryService.deleteDeployment(deploymentId, true);
                }
            }
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
            JsonNode modelNode = this.objectMapper.readTree(bytes);
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

    @Override
    public ResultInfo diagram(String modelId) throws IOException {
        String jsonXml = null;
        // 获取流程图模型数据
        Model modelData = this.repositoryService.getModel(modelId);
        if (modelData != null) {
            // 将流程图息转换为xml字符串
            jsonXml = new String(repositoryService.getModelEditorSource(modelData.getId()), "utf-8");
           // jsonXml = JsonUtils.jsonToXml(jsonXml);
        }
        return ResultUtil.success(jsonXml);
    }

    @Override
    public Model findByKey(String modelKey) {
        Model model  = this.repositoryService.createModelQuery().modelKey(modelKey).singleResult();
        return model;
    }

    @Override
    public FlowModelVo findById(String id) {
        FlowModelVo model = null;
        List<Model> modelList  =  this.repositoryService.createModelQuery().modelId(id).list();
        if (!CollectionUtils.isEmpty(modelList)) {
            model = DozerBeanMapperUtil.copyProperties(modelList.get(0), FlowModelVo.class);
        }
        return model;
    }

    @Override
    public String verifyModelKey(String modelKey, String history) {
        String result = "true";
        boolean verify = true;
        if (StringUtils.isNotBlank(history) && modelKey.equals(history)) {
            verify = false;
        }
        if (verify) {
            Model model = this.findByKey(modelKey);
            if (model != null && StringUtils.isNotBlank(model.getId())) {
                result = "false";
            }
        }
        return result;
    }

    @Override
    public String flowImageBase64(String deploymentId, String type) {
        String image = "";
        if ("deploy".equals(type)) {
            try {
                // 从仓库中找需要展示的文件
                List<String> names = this.repositoryService.getDeploymentResourceNames(deploymentId);
                String imageName = null;
                for (String name : names) {
                    if (name.indexOf(".png") >= 0) {
                        imageName = name;
                        break;
                    }
                }
                // 通过部署ID和文件名称得到文件的输入流
                InputStream in = this.repositoryService.getResourceAsStream(deploymentId, imageName);
                image = Base64Convert.imageToBase64(in);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if ("model".equals(type)) {
            //模型模块
            byte[] bytes = this.repositoryService.getModelEditorSourceExtra(deploymentId);
            image = Base64.encodeBase64String(bytes);
        }
        return "data:image/png;base64," + image;
    }
}
