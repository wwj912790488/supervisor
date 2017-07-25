<ContetDetectTask ID="${taskId}">
<Server>${ip}</Server>
<Port>${port?c}</Port>
<#if contentDetectConfigs??>
<maxcount>${(contentDetectConfigs?size)?c}</maxcount>
<#list contentDetectConfigs as config>
<ContetDetectIndex idx="${(config.index)?c}" channel="${(config.channelId)?c}" live="1">
<#if (config.detectSettings)?? && (config.detectSettings?size > 0)>
<#list config.detectSettings as setting>
<DetectType idx="${setting.index?c}">
<#list setting.params?keys as key>
<${key}>${setting.params[key]}</${key}>
</#list>
</DetectType>
</#list>
</#if>
</ContetDetectIndex>
</#list>
<#else>
</#if>
</ContetDetectTask>
