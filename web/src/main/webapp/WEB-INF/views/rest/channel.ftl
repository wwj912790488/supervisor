{
    "code" : ${statusCode}<#if (pageObject.number + 1) <= pageObject.totalPages>,
    "channels" : {
        "pageno" : ${pageObject.number + 1},
        "total" : ${pageObject.totalElements},
        "pagesize" : ${pageObject.size},
        <#assign channels=pageObject.content>
        <#if channels?? && (channels?size > 0)>
        "channel" : [
            <#list channels as channel>
            {
            "id" : ${channel.id},
            "name" : "${channel.name}",
            "sd" : "${channel.sdUrl!""}",
            "hd" : "${channel.hdUrl!""}",
            "msupport" : <#if channel.isSupportMobile?? && channel.isSupportMobile>1<#else>0</#if>
            }<#if (channel_has_next)>,</#if>
            </#list>
        ]
        </#if>
   }
    </#if>
}
