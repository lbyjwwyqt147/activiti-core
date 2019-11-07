package pers.liujunyi.cloud.activiti.repository.elasticsearch.category;

import pers.liujunyi.cloud.activiti.entity.category.FlowCategory;
import pers.liujunyi.cloud.common.repository.elasticsearch.BaseElasticsearchRepository;

import java.util.List;

/***
 * 文件名称: FlowCategoryElasticsearchRepository.java
 * 文件描述: 流程分类 Elasticsearch Repository
 * 公 司:
 * 内容摘要:
 * 其他说明:
 * 完成日期:2019年11月07日
 * 修改记录:
 * @version 1.0
 * @author ljy
 */
public interface FlowCategoryElasticsearchRepository extends BaseElasticsearchRepository<FlowCategory, Long> {

    /**
     * 根据状态 获取数据
     * @param categoryStatus  0:正常  1：禁用
     * @return
     */
    List<FlowCategory> findByCategoryStatus(Byte categoryStatus);

    /**
     * 根据categoryName 获取数据
     * @param categoryName  名称
     * @return
     */
    List<FlowCategory> findByCategoryName(String categoryName);
}
