package pers.liujunyi.cloud.activiti.service.category;

import pers.liujunyi.cloud.activiti.domain.category.FlowCategoryQueryDto;
import pers.liujunyi.cloud.activiti.entity.category.ActFlowCategory;
import pers.liujunyi.cloud.common.restful.ResultInfo;
import pers.liujunyi.cloud.common.service.BaseElasticsearchService;

import java.util.List;
import java.util.Map;

/***
 * 文件名称: FlowCategoryElasticsearchService.java
 * 文件描述: 流程分类 Elasticsearch Service
 * 公 司:
 * 内容摘要:
 * 其他说明:
 * 完成日期:2019年11月07日
 * 修改记录:
 * @version 1.0
 * @author ljy
 */
public interface FlowCategoryElasticsearchService extends BaseElasticsearchService<ActFlowCategory, Long> {



    /**
     * 分页列表
     * @param query
     * @return
     */
    ResultInfo findPageGird(FlowCategoryQueryDto query);


    /**
     * 根据 帐号id 获取员工详细数据
     * @param id
     * @return
     */
    ActFlowCategory findById(Long id);


    /**
     * 分类下拉框数据
     * @param query
     * @return
     */
    List<Map<String, String>> categorySelect(FlowCategoryQueryDto query);

    /**
     * 获取分类名称
     * @param ids
     * @return
     */
    Map<Long, String> getCategoryNameMap(List<Long> ids);

    /**
     * 验证名称是否重复
     * @param categoryName  最新值
     * @param history  历史值
     * @return
     */
    String verifyCategoryName(String categoryName, String history);

}
