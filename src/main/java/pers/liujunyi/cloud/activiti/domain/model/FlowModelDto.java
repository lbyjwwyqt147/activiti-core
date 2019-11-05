package pers.liujunyi.cloud.activiti.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/***
 * 文件名称: FlowModelDto.java
 * 文件描述: 流程模型 dto
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
public class FlowModelDto implements Serializable {
    private static final long serialVersionUID = -5008481728837491719L;

    /** 模型名称 */
    @ApiModelProperty(value = "模型名称")
    @NotBlank(message = "模型名称必须填写")
    private String flowModelName;

    /** 标识KEY */
    @ApiModelProperty(value = "标识KEY")
    @NotBlank(message = "标识KEY必须填写")
    private String flowModelKey;

    /** 描述 */
    @ApiModelProperty(value = "描述")
    private String description;

    /** 模型分类 */
    @ApiModelProperty(value = "模型分类")
    private String flowModelCategory;

}
