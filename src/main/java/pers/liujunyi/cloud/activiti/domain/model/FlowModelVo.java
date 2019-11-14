package pers.liujunyi.cloud.activiti.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/***
 * 文件名称: FlowModelVo.java
 * 文件描述: 流程模型 vo
 * 公 司:
 * 内容摘要:
 *
 * 完成日期:2019年11月05日
 * 修改记录:
 * @version 1.0
 * @author ljy
 */
@ApiModel
@Data
public class FlowModelVo implements Serializable {

    private static final long serialVersionUID = -6722862908092013611L;

    private String id;

    /** 模型名称 */
    @ApiModelProperty(value = "模型名称")
    private String name;

    /** 标识KEY */
    @ApiModelProperty(value = "标识KEY")
    private String key;

    /** 描述 */
    @ApiModelProperty(value = "描述")
    private String description;

    /** 分类ID  */
    @ApiModelProperty(value = "分类ID")
    private String category;

    /** 分类名称  */
    @ApiModelProperty(value = "分类名称")
    private String categoryName;

    /** 创建时间  */
    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date createTime;

    /** 最后修改时间  */
    @ApiModelProperty(value = "最后修改时间")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date lastUpdateTime;

    /** 版本  */
    @ApiModelProperty(value = "版本")
    private Integer version;

    /** 部署ID  */
    @ApiModelProperty(value = "部署ID")
    private String deploymentId;

    /** 租户 */
    @ApiModelProperty(value = "租户")
    private String tenantId;

    /** 状态 0：已部署发布 1: 未部署 */
    private Byte status;

    /** 存放二进制流程模型XML数据 ACT_GE_BYTEARRAY 表中的ID name = source */
    private String editorSourceValueId;

    /** 存放SVG 二进制xml 数据 ACT_GE_BYTEARRAY 表中的ID  name = source-extra */
    private String editorSourceExtraValueId;

}
