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
import org.springframework.web.bind.annotation.RequestBody;

import com.wdit.common.msg.ReturnMsg;
import com.wdit.common.vo.PageVo;
import com.wdit.common.persistence.Page;
import static com.wdit.common.utils.ApiUtils.buildPage;
import ${containPackageName}.${shortBeanName}Dao;
import ${containPackageName}.${shortBeanName}Entity;
import ${containPackageName}.${shortBeanName}PageVo;
import com.wdit.common.vo.PageVo;
import com.wdit.common.vo.IdVo;
import javax.validation.Valid;

<#macro updateParam>
    <#list fields?filter( x -> x.fieldName != "id" && x.fieldName != "createDate" && x.fieldName != "updateDate")  as fieldItem >
        @RequestParam(name="${fieldItem.fieldName}" <#if fieldItem.fieldName != "siteId">, required= false</#if>)<#if fieldItem.fieldName == "siteId">@NotBlank</#if> ${fieldItem.fieldType} ${fieldItem.fieldName}<#if fieldItem?has_next>,
        </#if>
    </#list>
</#macro>
public interface ${shortBeanName}Api {

    /**
    * 新增
    */
    @PostMapping("/api/${moduleName}/${shortBeanName?uncap_first}/insert")
    ReturnMsg<String> insert(@RequestBody @Valid ${shortBeanName}Vo vo);

    /**
    * 删除
    */
    @PostMapping("/api/${moduleName}/${shortBeanName?uncap_first}/delete")
    ReturnMsg<Object> delete(@RequestBody @Valid IdVo vo);

    /**
    * 更新
    */
    @PostMapping("/api/${moduleName}/${shortBeanName?uncap_first}/update")
    ReturnMsg<Object> update(@RequestBody @Valid ${shortBeanName}Vo vo);

    /**
    * 获取
    */
    @PostMapping("/api/${moduleName}/${shortBeanName?uncap_first}/get")
    ReturnMsg<${shortBeanName}Entity> get(@RequestBody @Valid IdVo vo);

    /**
    * 列表
    */
    @PostMapping("/api/${moduleName}/${shortBeanName?uncap_first}/list")
    ReturnMsg<PageVo<${shortBeanName}Entity>> list(@RequestBody @Valid ${shortBeanName}PageVo vo );

}