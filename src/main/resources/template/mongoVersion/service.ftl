import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.wdit.common.utils.StringUtils;

import ${containPackageName}.${shortBeanName}Dao;
import ${containPackageName}.${shortBeanName}Entity;
import com.wdit.common.vo.PageVo;

@Service
public class ${shortBeanName}Service {
    @Autowired
    private ${shortBeanName}Dao dao;



    public void save(${fullBeanName} entity) {
        dao.save(entity);
    }

    public PageVo<${shortBeanName}Entity> findAdminPage(${shortBeanName}PageVo vo) {
        PageVo<${shortBeanName}Entity>  result =  dao.findAdminPage(vo, ${shortBeanName}Service::toEntity);
        return result;
    }


    public ${fullBeanName} get(String id) {
        if (StringUtils.isAnyBlank(id)) {
            return null;
        }
        return dao.queryById(id);
    }

    public void delete(String id) {
        dao.deleteById(id);
    }


    public static ${shortBeanName}Entity toEntity(${fullBeanName} entity) {
        ${shortBeanName}Entity rtn = new ${shortBeanName}Entity();
        <#list fields as fieldItem >
            rtn.set${fieldItem.fieldName?cap_first}(entity.get${fieldItem.fieldName?cap_first}());
        </#list>
        return rtn;
    }


}
