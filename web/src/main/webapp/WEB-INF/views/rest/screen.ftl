{
    "code" : ${statusCode},
    "screen" : {
    "id" : ${screen.id},
    "name" : ${(screen.name)!"\"\""},
    "template_id" : ${(screen.activeSchema.id)!-1}<#if screen.schemas?? && (screen.schemas?size > 0)>,
    "template" : [
        <#list screen.schemas as schema>
            {
                "id" : ${schema.id},
                "row" : ${schema.rowCount},
                "col" : ${schema.columnCount},
                "group" : ${schema.groupCount},
                "switchTime" : ${schema.switchTime}<#if schema.screenPositions?? && (schema.screenPositions?size > 0)>,
                "subscrns" : [
                    <#list schema.screenPositions as position>
                        {
                        "row" : ${position.row},
                        "col" : ${position.column},
                        "group" : ${position.groupIndex},
                        "x" : ${position.x},
                        "y" : ${position.y},
                        "channel" : ${(position.channel.id)!-1}
                        }<#if (position_has_next)>,</#if>
                    </#list>
                ]
                </#if>
            }<#if (schema_has_next)>,</#if>
        </#list>
    ]
    </#if>
    }
}
