{
    "code" : ${statusCode},
    "groupId":${groupId},
    <#assign channels=channelObj>
    <#if channels?? && (channels?size > 0)>
    "channels" : [
        <#list channels as channel>
        {
            "id" : ${channel.id},
            "name" : "${channel.name}"
        }<#if (channel_has_next)>,</#if>
        </#list>
    ]
    </#if>
}
