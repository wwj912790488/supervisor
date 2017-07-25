{
    "code" : ${statusCode},
    <#assign groups=groupObj>
    <#if groups?? && (groups?size > 0)>
    "groups" : [
        <#list groups as group>
        {
            "id" : ${group.id},
            "name" : "${group.name}"
        }<#if (group_has_next)>,</#if>
        </#list>
    ]
    </#if>
}
