package pers.liujunyi.cloud.activiti.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
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
    @Column(name = "ID_", columnDefinition="bigint(20)", nullable = false)
    private Long id;

    /** 创建时间 */
    @Column(name = "CREATE_TIME_", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date createTime;

    /** 创建人ID */
    @Column(name = "CREATE_USER_ID_", columnDefinition="bigint(20)", nullable = false)
    @CreatedBy
    private Long createUserId;

    /** 最后更新时间 */
    @Column(name = "UPDATE_TIME_",  nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    @LastModifiedDate
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date updateTime;

    /** 最后更新人ID */
    @Column(name = "UPDATE_USER_ID_", columnDefinition="bigint(20)", nullable = true)
    @LastModifiedBy
    private Long updateUserId;

    /** 租户Id  */
    @Column(name = "LESSEE_", columnDefinition="bigint(20)", nullable = true)
    private Long lessee;
}
