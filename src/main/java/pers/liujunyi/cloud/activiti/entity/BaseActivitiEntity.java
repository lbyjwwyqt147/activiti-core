package pers.liujunyi.cloud.activiti.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import pers.liujunyi.cloud.common.entity.LesseeAuditListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/***
 * 指定activiti 风格 属性对应的字段名称
 * @author ljy
 */
@Data
@EntityListeners({AuditingEntityListener.class, LesseeAuditListener.class})
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
public class BaseActivitiEntity implements Serializable {

    private static final long serialVersionUID = 8685251305159555108L;

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long id;

    /** 创建时间 */
   // @Temporal(TemporalType.TIMESTAMP)
   // @CreatedDate
    private Date createTime;

    /** 创建人ID */
  //  @CreatedBy
    private Long createUserId;

    /** 最后更新时间 */
  //  @Temporal(TemporalType.TIMESTAMP)
   // @LastModifiedDate
    private Date updateTime;

    /** 最后更新人ID */
   // @LastModifiedBy
    private Long updateUserId;

    /** 租户Id  */
    private Long lessee;
}
