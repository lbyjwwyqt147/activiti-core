package pers.liujunyi.cloud.activiti.service.category.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import pers.liujunyi.cloud.activiti.domain.category.FlowCategoryDto;
import pers.liujunyi.cloud.activiti.entity.category.FlowCategory;
import pers.liujunyi.cloud.activiti.repository.elasticsearch.category.FlowCategoryElasticsearchRepository;
import pers.liujunyi.cloud.activiti.repository.jpa.category.FlowCategoryRepository;
import pers.liujunyi.cloud.activiti.service.category.FlowCategoryService;
import pers.liujunyi.cloud.common.exception.ErrorCodeEnum;
import pers.liujunyi.cloud.common.repository.jpa.BaseRepository;
import pers.liujunyi.cloud.common.restful.ResultInfo;
import pers.liujunyi.cloud.common.restful.ResultUtil;
import pers.liujunyi.cloud.common.service.impl.BaseServiceImpl;
import pers.liujunyi.cloud.common.util.DozerBeanMapperUtil;
import pers.liujunyi.cloud.common.util.UserContext;
import pers.liujunyi.cloud.security.util.SecurityConstant;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/***
 * 文件名称: FlowCategoryServiceImpl.java
 * 文件描述: 流程分类 Service Impl
 * 公 司:
 * 内容摘要:
 * 其他说明:
 * 完成日期:2019年11月07日
 * 修改记录:
 * @version 1.0
 * @author ljy
 */
@Service
public class FlowCategoryServiceImpl extends BaseServiceImpl<FlowCategory, Long> implements FlowCategoryService {

    @Autowired
    private FlowCategoryRepository flowCategoryRepository;
    @Autowired
    private FlowCategoryElasticsearchRepository flowCategoryElasticsearchRepository;


    public FlowCategoryServiceImpl(BaseRepository<FlowCategory, Long> baseRepository) {
        super(baseRepository);
    }


    @Override
    public ResultInfo saveRecord(FlowCategoryDto record) {
        ResultInfo result = ResultUtil.success();
        if (record.getId() != null) {
            record.setUpdateTime(new Date());
            record.setUpdateUserId(UserContext.currentUserId());
        }
        if (record.getCategoryStatus() == null) {
            record.setCategoryStatus(SecurityConstant.ENABLE_STATUS);
        }
        FlowCategory flowCategory = DozerBeanMapperUtil.copyProperties(record, FlowCategory.class);
        FlowCategory saveObj = this.flowCategoryRepository.save(flowCategory);
        if (saveObj != null && saveObj.getId() != null) {
            this.flowCategoryElasticsearchRepository.save(saveObj);
        }else {
            result.setSuccess(false);
            result.setStatus(ErrorCodeEnum.FAIL.getCode());
        }
        return result;
    }

    @Override
    public ResultInfo updateStatus(Byte status, List<Long> ids) {
        int count = this.flowCategoryRepository.setStatusByIds(status, new Date(), ids);
        if (count > 0) {
            Map<String, Map<String, Object>> sourceMap = new ConcurrentHashMap<>();
            Map<String, Object> docDataMap = new HashMap<>();
            docDataMap.put("categoryStatus", status);
            docDataMap.put("updateTime", System.currentTimeMillis());
            ids.stream().forEach(item -> {
                sourceMap.put(String.valueOf(item), docDataMap);
            });
            super.updateBatchElasticsearchData(sourceMap);
            return ResultUtil.success();
        }
        return ResultUtil.fail();
    }

    @Override
    public ResultInfo deleteBatch(List<Long> ids) {
        long count = this.flowCategoryRepository.deleteByIdIn(ids);
        if (count > 0) {
            this.flowCategoryElasticsearchRepository.deleteByIdIn(ids);
            return ResultUtil.success();
        }
        return ResultUtil.fail();
    }

    @Override
    public ResultInfo syncDataToElasticsearch() {
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        List<FlowCategory> list = this.flowCategoryRepository.findAll(sort);
        if (!CollectionUtils.isEmpty(list)) {
            this.flowCategoryElasticsearchRepository.deleteAll();
            // 限制条数
            int pointsDataLimit = 1000;
            int size = list.size();
            //判断是否有必要分批
            if(pointsDataLimit < size){
                //分批数
                int part = size/pointsDataLimit;
                for (int i = 0; i < part; i++) {
                    //1000条
                    List<FlowCategory> partList = new LinkedList<>(list.subList(0, pointsDataLimit));
                    //剔除
                    list.subList(0, pointsDataLimit).clear();
                    this.flowCategoryElasticsearchRepository.saveAll(partList);
                }
                //表示最后剩下的数据
                if (!CollectionUtils.isEmpty(list)) {
                    this.flowCategoryElasticsearchRepository.saveAll(list);
                }
            } else {
                this.flowCategoryElasticsearchRepository.saveAll(list);
            }
        } else {
            this.flowCategoryElasticsearchRepository.deleteAll();
        }
        return ResultUtil.success();
    }

}
