package pers.liujunyi.cloud.activiti.domain.category;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Length;
import pers.liujunyi.cloud.common.dto.BaseDto;
import pers.liujunyi.cloud.common.util.RegexpUtils;

import javax.validation.constraints.*;

/***
 * 文件名称: FlowCategoryDto.java
 * 文件描述: 流程分类 dto
 * 公 司:
 * 内容摘要:
 *
 * 完成日期:2019年11月07日
 * 修改记录:
 * @version 1.0
 * @author ljy
 */
@ApiModel
@Data
@EqualsAndHashCode(callSuper = true)
public class FlowCategoryDto extends BaseDto {
    private static final long serialVersionUID = 4323040086559539425L;

    /** 分类名称 */
    @ApiModelProperty(value = "分类名称")
    @NotBlank(message = "分类名称 必须填写")
    @Length(min = 1, max = 32, message = "分类名称 最多可以输入32个字符")
    @Pattern(regexp = RegexpUtils.ALNUM_NAME_REGEXP, message = "分类名称 " + RegexpUtils.ALNUM_NAME_MSG)
    private String categoryName;

    /** 序号 */
    @ApiModelProperty(value = "序号")
    @NotNull(message = "序号 必须选择")
    @Min(value = 0, message = "序号 值必须大于0")
    @Max(value = 99999, message = "序号 最大值不能大于99999")
    private Integer sequenceNumber;

    /** 父级主键id */
    private Long parentId;

    /** 完整层级ID */
    private String fullParent;

    /** 完整的机构名称 */
    private String fullName;

    /** 描述说明 */
    @ApiModelProperty(value = "描述说明")
    @Length(min = 0, max = 100, message = "描述说明 最多可以输入100个字符")
    private String description;

    /** 状态：0：正常  1：禁用 */
    @ApiModelProperty(value = "状态")
    @Min(value = 0, message = "状态 值必须大于0")
    @Max(value = 127, message = "状态 最大值不能大于127")
    private Byte categoryStatus;
}