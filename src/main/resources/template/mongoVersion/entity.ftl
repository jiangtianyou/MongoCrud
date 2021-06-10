package ${packageName};

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)
public class ${shortBeanName}Entity{

<#list fields as fieldItem >
    @com.fasterxml.jackson.annotation.JsonProperty("${fieldItem.fieldName}")<#if fieldItem.fieldType == "Date" >@com.fasterxml.jackson.annotation.JsonFormat( pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+0" )  </#if>
    private ${fieldItem.fieldType} ${fieldItem.fieldName};
</#list>


<#list fields as fieldItem >
    public ${fieldItem.fieldType} get${fieldItem.fieldName?cap_first}() {
        return ${fieldItem.fieldName};
    }
    public void set${fieldItem.fieldName?cap_first}(${fieldItem.fieldType} ${fieldItem.fieldName}) {
        this.${fieldItem.fieldName} = ${fieldItem.fieldName};
    }
</#list>



}