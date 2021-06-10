<#-- type值为 controller_api或controller -->
<#macro insertParam type>
    <#list fields?filter(x -> x.fieldName != "id" &&  x.fieldName != "createDate" && x.fieldName != "updateDate") as fieldItem >
        <#if type == "controller_api">
            @RequestParam(name="${fieldItem.fieldName}"  <#if fieldItem.mustRequired == false >,required = false</#if>)<#t>
        </#if>
        <#--下面是验证注解部分 -->
        <#if fieldItem.mustRequired && fieldItem.fieldType == "String" > @NotBlank <#elseif fieldItem.mustRequired> @NotNull</#if> <#t>
        <#-- 下面是参数部分 -->
         ${fieldItem.fieldType} ${fieldItem.fieldName}<#if fieldItem?has_next>,${"\n"}</#if> <#t>
    </#list>
</#macro>


<#macro updateParam type>
    <#list fields?filter( x ->  x.fieldName != "createDate" && x.fieldName != "updateDate")  as fieldItem >
        <#if type == "controller_api">
            @RequestParam(name="${fieldItem.fieldName}"  <#if fieldItem.fieldName != "id" && fieldItem.fieldName != "siteId">,required = false</#if>) <#t>
        </#if>
        <#--下面是验证注解部分 -->
        <#if fieldItem.fieldName == "id" ||  fieldItem.fieldName == "siteId" >
            @NotBlank <#t>
        </#if>
        <#-- 下面是参数部分 -->
        ${fieldItem.fieldType} ${fieldItem.fieldName}<#if fieldItem?has_next>,${"\n"}</#if> <#t>
    </#list>
</#macro>