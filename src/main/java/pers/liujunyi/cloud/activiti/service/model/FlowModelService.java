package pers.liujunyi.cloud.activiti.service.model;

import org.activiti.engine.repository.Model;
import pers.liujunyi.cloud.activiti.domain.model.FlowModelDto;
import pers.liujunyi.cloud.activiti.domain.model.FlowModelQueryDto;
import pers.liujunyi.cloud.common.restful.ResultInfo;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/***
 * 文件名称: FlowModelService.java
 * 文件描述:  工作流模型 Service
 * 公 司:
 * 内容摘要:
 * 其他说明:
 * 完成日期:2019年11月05日
 * 修改记录:
 * @version 1.0
 * @author ljy
 */
public interface FlowModelService {


    /**
     * 分页列表
     * @param query
     * @return
     */
    ResultInfo findPageGird(FlowModelQueryDto query);


    /**
     * 保存模型
     * @param record
     * @return
     */
    ResultInfo saveRecord(FlowModelDto record);

    /**
     * 批量删除
     * @param ids
     * @return
     */
    ResultInfo deleteBatch(List<String> ids);

    /**
     * 导出model的xml文件
     * @param modelId
     * @param response
     */
    void exportFlowModel(String modelId, HttpServletResponse response);

    /**
     * 部署发布模型
     * @param modelId
     * @return
     */
    ResultInfo deployFlowModel(String modelId);

    /**
     * 根据标识key 获取数据
     * @param modelKey
     * @return
     */
    Model findByKey(String modelKey);

    /**
     * 验证 标识key 是否存在
     * @param modelKey  最新值
     * @param history  历史值
     * @return
     */
    String  verifyModelKey(String modelKey, String history);
}
