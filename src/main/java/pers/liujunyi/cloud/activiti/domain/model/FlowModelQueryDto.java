package pers.liujunyi.cloud.activiti.domain.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import pers.liujunyi.cloud.common.query.BasePagination;

/***
 * 文件名称: FlowModelQueryDto.java
 * 文件描述: 流程模型 查询条件
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
@EqualsAndHashCode(callSuper = true)
public class FlowModelQueryDto  extends BasePagination {


    private static final long serialVersionUID = -6985391619743265651L;

    /** 模型名称 */
    @ApiModelProperty(value = "模型名称")
    private String flowModelName;

    /** 标识KEY */
    @ApiModelProperty(value = "标识KEY")
    private String flowModelKey;

    /** 分类 */
    @ApiModelProperty(value = "分类")
    private String flowModelCategory;
}
