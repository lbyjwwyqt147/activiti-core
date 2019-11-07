package pers.liujunyi.cloud.activiti.service.category;

import pers.liujunyi.cloud.activiti.domain.category.FlowCategoryDto;
import pers.liujunyi.cloud.activiti.entity.category.FlowCategory;
import pers.liujunyi.cloud.common.restful.ResultInfo;
import pers.liujunyi.cloud.common.service.BaseService;

import java.util.List;

/***
 * 文件名称: FlowCategoryService.java
 * 文件描述:  流程分类 Service
 * 公 司:
 * 内容摘要:
 * 其他说明:
 * 完成日期:2019年11月07日
 * 修改记录:
 * @version 1.0
 * @author ljy
 */
public interface FlowCategoryService extends BaseService<FlowCategory, Long> {

    /**
     * 保存数据
     * @param record
     * @return
     */
    ResultInfo saveRecord(FlowCategoryDto record);

    /**
     * 修改状态
     * @param status   0：正常  1：禁用
     * @param ids
     * @return
     */
    ResultInfo updateStatus(Byte status, List<Long> ids);

    /**
     * 批量删除
     * @param ids
     * @return
     */
    ResultInfo deleteBatch(List<Long> ids);

    /**
     * 同步数据到es中
     * @return
     */
    ResultInfo syncDataToElasticsearch();

}
