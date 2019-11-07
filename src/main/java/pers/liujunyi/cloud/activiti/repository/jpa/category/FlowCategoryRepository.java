package pers.liujunyi.cloud.activiti.repository.jpa.category;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import pers.liujunyi.cloud.activiti.entity.category.FlowCategory;
import pers.liujunyi.cloud.common.repository.jpa.BaseRepository;

import java.util.Date;
import java.util.List;

/***
 * 文件名称: FlowCategoryRepository.java
 * 文件描述: 流程分类 Repository
 * 公 司:
 * 内容摘要:
 * 其他说明:
 * 完成日期:2019年11月07日
 * 修改记录:
 * @version 1.0
 * @author ljy
 */
public interface FlowCategoryRepository extends BaseRepository<FlowCategory, Long> {

    /**
     * 修改状态
     * @param categoryStatus  0：正常  1：禁用
     * @param ids
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    @Modifying(clearAutomatically = true)
    @Query(value = "update ACT_FLOW_CATEGORY u set u.CATEGORY_STATUS_ = ?1, u.UPDATE_TIME_ = ?2 where u.id in (?3)", nativeQuery = true)
    int setStatusByIds(Byte categoryStatus, Date updateTime, List<Long> ids);

}
