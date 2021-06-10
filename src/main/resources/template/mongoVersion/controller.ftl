<#import "common.ftl" as fun/>

package ${containPackageName};

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Date;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Min;
import javax.validation.constraints.Max;

import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.annotation.Validated;
import com.wdit.common.msg.ReturnMsg;
import com.wdit.common.utils.EntitySetter;
import com.wdit.common.utils.MongoUtil;
import com.wdit.common.vo.PageVo;
import com.wdit.common.persistence.Page;
import static com.wdit.common.utils.ApiUtils.buildPage;
import ${containPackageName}.${shortBeanName}Dao;
import ${containPackageName}.${shortBeanName}Entity;
import ${containPackageName}.${shortBeanName}PageVo;
import ${containPackageName}.${shortBeanName}Api;
import com.wdit.common.vo.PageVo;
import com.wdit.common.vo.IdVo;
import javax.validation.Valid;


@RestController
@Validated
public class ${shortBeanName}Controller extends ${baseController} implements ${shortBeanName}Api {

    @Autowired
    ${shortBeanName}Service ${shortBeanName?uncap_first}Service;


    @Override
    public ReturnMsg<String> insert(@Valid ${shortBeanName}Vo vo){

        <#list fields as fieldItem >
            ${fieldItem.fieldType} ${fieldItem.fieldName?uncap_first} =  vo.get${fieldItem.fieldName?cap_first}();
        </#list>
        if(com.wdit.common.utils.StringUtils.isAnyBlank("")){
            return failResult("不能为空");
        }
        ${fullBeanName} entity = new EntitySetter<>(${fullBeanName}::new)
        <#list fields as fieldItem >
            <#if fieldItem.fieldName == "id">
                .set(${fullBeanName}::set${fieldItem.fieldName?cap_first}, MongoUtil.getMongoRandomId())
            <#elseif fieldItem.fieldName == "createDate">
                .set(${fullBeanName}::set${fieldItem.fieldName?cap_first}, MongoUtil.getMongoDate(new Date()))
            <#elseif fieldItem.fieldType == "String" && fieldItem.mustRequired == false>
                .setOrDefault(${fullBeanName}::set${fieldItem.fieldName?cap_first}, ${fieldItem.fieldName},"")
            <#elseif fieldItem.fieldType == "Integer" && fieldItem.mustRequired == false>
                .setOrDefault(${fullBeanName}::set${fieldItem.fieldName?cap_first}, ${fieldItem.fieldName},0)
            <#else>
                .set(${fullBeanName}::set${fieldItem.fieldName?cap_first},${fieldItem.fieldName})
            </#if>
            <#if fieldItem.fieldName == "createDate">
                .setIfNotNull(${fullBeanName}::set${fieldItem.fieldName?cap_first}, MongoUtil.getMongoDate(new Date()))
            </#if>
        </#list>
        .done();
        ${shortBeanName?uncap_first}Service.save(entity);
        return successResult(entity.getId());
    }

    @Override
    public ReturnMsg<Object> delete(@Valid IdVo vo){
        String id = vo.getId();
        ${shortBeanName?uncap_first}Service.delete(id);
        return successResult();
    }

    @Override
    public ReturnMsg<Object> update(@Valid ${shortBeanName}Vo vo){
        <#list fields as fieldItem >
            ${fieldItem.fieldType} ${fieldItem.fieldName?uncap_first} =  vo.get${fieldItem.fieldName?cap_first}();
        </#list>
        ${fullBeanName} entity = ${shortBeanName?uncap_first}Service.get(id);
        if(entity == null){
            return failResult("找不到id对应信息");
        }
        entity = new EntitySetter<>(entity)
        <#list fields as fieldItem >
            <#-- id,siteId不修改updateDate特殊处理-->
            <#if fieldItem.fieldName != "id" && fieldItem.fieldName != "siteId" && fieldItem.fieldName != "createDate" && fieldItem.fieldName != "updateDate" >
                .setIfNotNull(${fullBeanName}::set${fieldItem.fieldName?cap_first}, ${fieldItem.fieldName})
            </#if>
            <#-- updateDate特殊处理-->
            <#if fieldItem.fieldName == "updateDate" >
                .setIfNotNull(${fullBeanName}::set${fieldItem.fieldName?cap_first}, MongoUtil.getMongoDate(new Date()))
            </#if>
        </#list>
        .done();
        ${shortBeanName?uncap_first}Service.save(entity);
        return successResult(entity.getId());
    }

    @Override
    public ReturnMsg<${shortBeanName}Entity> get(@Valid IdVo vo){
        String id = vo.getId();
        ${fullBeanName} entity = ${shortBeanName?uncap_first}Service.get(id);
        if(entity == null){
            return failResult("找不到id对应信息");
        }
        ${shortBeanName}Entity result = ${shortBeanName}Service.toEntity(entity);
        return successResult(result);
    }

    @Override
    public ReturnMsg<PageVo<${shortBeanName}Entity>> list(@Valid ${shortBeanName}PageVo vo){
        PageVo<${shortBeanName}Entity> result = ${shortBeanName?uncap_first}Service.findAdminPage(vo);
            return successResult(result);
    }

}