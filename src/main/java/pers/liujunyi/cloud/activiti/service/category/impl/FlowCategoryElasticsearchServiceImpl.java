package pers.liujunyi.cloud.activiti.service.category.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import pers.liujunyi.cloud.activiti.domain.category.FlowCategoryQueryDto;
import pers.liujunyi.cloud.activiti.entity.category.FlowCategory;
import pers.liujunyi.cloud.activiti.repository.elasticsearch.category.FlowCategoryElasticsearchRepository;
import pers.liujunyi.cloud.activiti.service.category.FlowCategoryElasticsearchService;
import pers.liujunyi.cloud.common.repository.elasticsearch.BaseElasticsearchRepository;
import pers.liujunyi.cloud.common.restful.ResultInfo;
import pers.liujunyi.cloud.common.restful.ResultUtil;
import pers.liujunyi.cloud.common.service.impl.BaseElasticsearchServiceImpl;
import pers.liujunyi.cloud.security.util.SecurityConstant;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;


/***
 * 文件名称: FlowCategoryElasticsearchServiceImpl.java
 * 文件描述: 流程分类 Elasticsearch Service impl
 * 公 司:
 * 内容摘要:
 * 其他说明:
 * 完成日期:2019年11月07日
 * 修改记录:
 * @version 1.0
 * @author ljy
 */
@Service
public class FlowCategoryElasticsearchServiceImpl extends BaseElasticsearchServiceImpl<FlowCategory, Long> implements FlowCategoryElasticsearchService {

    @Autowired
    private FlowCategoryElasticsearchRepository flowCategoryElasticsearchRepository;


    public FlowCategoryElasticsearchServiceImpl(BaseElasticsearchRepository<FlowCategory, Long> baseElasticsearchRepository) {
        super(baseElasticsearchRepository);
    }


    @Override
    public ResultInfo findPageGird(FlowCategoryQueryDto query) {
        // 排序方式 解决无数据时异常 No mapping found for [createTime] in order to sort on
       // SortBuilder sort = SortBuilders.fieldSort("createTime").unmappedType("date").order(SortOrder.DESC);
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        //分页参数
        Pageable pageable = query.toPageable(sort);
        // 查询数据
        SearchQuery searchQuery = query.toSpecPageable(pageable);
        Page<FlowCategory> searchPageResults = this.flowCategoryElasticsearchRepository.search(searchQuery);
        List<FlowCategory> searchDataList = searchPageResults.getContent();
        Long totalElements =  searchPageResults.getTotalElements();
        ResultInfo result = ResultUtil.success(searchDataList);
        result.setTotal(totalElements);
        return  result;
    }



    @Override
    public FlowCategory findById(Long id) {
        Optional<FlowCategory> optional  = this.flowCategoryElasticsearchRepository.findById(id);
        if (optional.isPresent()) {
            return optional.get();
        }
        return null;
    }


    @Override
    public List<Map<String, String>> categorySelect(FlowCategoryQueryDto query) {
        query.setCategoryStatus(SecurityConstant.ENABLE_STATUS);
        List<Map<String, String>> result = new LinkedList<>();
        //分页参数
        Pageable pageable = this.allPageable;
        // 查询数据
        SearchQuery searchQuery = query.toSpecPageable(pageable);
        Page<FlowCategory> searchPageResults = this.flowCategoryElasticsearchRepository.search(searchQuery);
        List<FlowCategory> FlowCategoryList = searchPageResults.getContent();
        if (!CollectionUtils.isEmpty(FlowCategoryList)) {
            FlowCategoryList.stream().forEach(item -> {
                Map<String, String> map = new ConcurrentHashMap<>();
                map.put("id", item.getId().toString());
                map.put("text", item.getCategoryName());
                result.add(map);
            });
        }
        return result;
    }

    @Override
    public Map<Long, String> getCategoryNameMap(List<Long> ids) {
        List<FlowCategory> FlowCategoryList = this.findAllByIdIn(ids);
        if (!CollectionUtils.isEmpty(FlowCategoryList)) {
            Map<Long, String> nameMap = new ConcurrentHashMap<>();
            FlowCategoryList.stream().forEach(item -> {
                nameMap.put(item.getId(), item.getCategoryName());
            });
            return nameMap;
        }
        return null;
    }

    @Override
    public String verifyCategoryName(String categoryName, String history) {
        String result = "true";
        boolean verify = true;
        if (StringUtils.isNotBlank(history) && categoryName.equals(history)) {
            verify = false;
        }
        if (verify) {
            List<FlowCategory> categoryList = this.flowCategoryElasticsearchRepository.findByCategoryName(categoryName);
            if (!CollectionUtils.isEmpty(categoryList)) {
                result = "false";
            }
        }
        return result;
    }

}
