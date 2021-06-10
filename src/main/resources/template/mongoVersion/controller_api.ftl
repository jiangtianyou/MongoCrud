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

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.wdit.common.msg.ReturnMsg;
import com.wdit.common.vo.PageVo;
import com.wdit.common.persistence.Page;
import static com.wdit.common.utils.ApiUtils.buildPage;
import ${containPackageName}.${shortBeanName}Dao;
import ${containPackageName}.${shortBeanName}Entity;
import ${containPackageName}.${shortBeanName}ParamVo;
import com.wdit.common.vo.PageVo;


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
    ReturnMsg<String> insert(<@fun.insertParam "controller_api"/>);

    /**
    * 删除
    */
    @PostMapping("/api/${moduleName}/${shortBeanName?uncap_first}/delete")
    ReturnMsg<Object> delete(@RequestParam(name="id") @NotBlank String id);

    /**
    * 更新
    */
    @PostMapping("/api/${moduleName}/${shortBeanName?uncap_first}/update")
    ReturnMsg<Object> update(<@fun.updateParam "controller_api"/>);

    /**
    * 获取
    */
    @PostMapping("/api/${moduleName}/${shortBeanName?uncap_first}/get")
    ReturnMsg<${shortBeanName}Entity> get(@RequestParam(name="id") @NotBlank String id);

    /**
    * 列表
    */
    @PostMapping("/api/${moduleName}/${shortBeanName?uncap_first}/list")
    ReturnMsg<PageVo<${shortBeanName}Entity>> list(@RequestParam(name = "siteId") @NotBlank String siteId,
          @RequestParam(name = "pageNo") @Min(1) Integer pageNo,
         @RequestParam(name = "pageSize") @Min(1) Integer pageSize,
         @RequestParam(name = "startDate",required=false) Date startDate,
         @RequestParam(name = "endDate",required=false) Date endDate
    );

}
