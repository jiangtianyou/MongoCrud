package ${containPackageName};

import ${containPackageName}.${shortBeanName}Api;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(contextId = "${shortBeanName?uncap_first}Client",
name = "stream-member",
path = "/stream-member")
@ConditionalOnMissingClass("${containPackageName}.${shortBeanName}Controller")
public interface ${shortBeanName}Client extends ${containPackageName}.${shortBeanName}Api{

}