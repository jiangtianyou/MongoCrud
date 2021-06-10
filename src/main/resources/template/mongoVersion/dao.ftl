package ${packageName}.${shortBeanName?uncap_first};

import com.wdit.common.utils.MongoUtil;
import com.wdit.common.utils.StringUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;


@Service
public class ${shortBeanName}Dao extends com.wdit.common.mongo.base.MongoBaseDao<${fullBeanName}>{

    public <R> PageVo<R> findAdminPage( ${containPackageName}.${shortBeanName}ParamVo vo,
            java.util.function.Function<${fullBeanName}, R> converter) {
        String siteId = vo.getSiteId();
        Integer pageNo = vo.getPageNo();
        Integer pageSize = vo.getPageSize();
        java.util.Date startDate = vo.getStartDate();
        java.util.Date endDate = vo.getEndDate();
        Criteria criteria = Criteria.where("site_id").is(siteId);
    //    if (StringUtils.isNotBlank(sth)) {
    //        criteria.and("sth").is(sth);
    //    }
        if(startDate != null && endDate != null ){
            criteria.and("create_date").gte(MongoUtil.getMongoDate(startDate))
                                        .lte(MongoUtil.getMongoDate(MongoUtil.getDayEnd(endDate)));
        }
        com.wdit.common.vo.PageVo<R> page = new com.wdit.common.vo.PageVo<>(pageNo, pageSize);
        return findPage(Query.query(criteria), page,converter);
    }



}