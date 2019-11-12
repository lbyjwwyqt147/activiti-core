package pers.liujunyi.cloud.activiti.entity.category;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import pers.liujunyi.cloud.activiti.entity.BaseActivitiEntity;

import javax.persistence.Entity;

/***
 * 文件名称: FlowCategory.java
 * 文件描述: 流程分类
 * 公 司:
 * 内容摘要:
 *
 * 完成日期:2019年11月07日
 * 修改记录:
 * @version 1.0
 * @author ljy
 */
@Data
@Entity
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Document(indexName = "photo_manage_flow_category", type = "FlowCategory", shards = 1, replicas = 0)
@DynamicInsert
@DynamicUpdate
public class ActFlowCategory extends BaseActivitiEntity {

    private static final long serialVersionUID = -4042324289072474794L;

    /** 分类名称 */
    private String categoryName;

    /** 序号 */
    private Integer sequenceNumber;

    /** 父级主键id */
    private Long parentId;

    /** 完整层级ID */
    private String fullParent;

    /** 完整的机构名称 */
    @Field(index = false, type = FieldType.Keyword)
    private String fullName;

    /** 描述说明 */
    @Field(index = false, type = FieldType.Keyword)
    private String description;

    /** 状态：0：正常  1：禁用 */
    private Byte categoryStatus;

}