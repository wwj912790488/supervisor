{
"code" : ${statusCode},
"walls" : {
"count" : ${walls?size},
<#if (walls?size > 0)>
"wall" : [
    <#list walls as wall>
    {
    "id" : ${wall.id},
    "type" : ${wall.type},
    "row" : ${wall.rowCount},
    "col" : ${wall.columnCount},
    "name": "${wall.name}",
    "scrncount" : ${(wall.rowCount * wall.columnCount)}<#if wall.wallPositions?? && (wall.wallPositions?size > 0)>,
        "screen" : [
            <#list wall.wallPositions as position>
                <#if (position.screen)??>
                    {
                    "ridx": ${position.row},
                    "cidx": ${position.column},
                    "id": ${position.screen.id},
                    "name": "${(position.row * wall.columnCount + position.column + 1)}"
                    }<#if (position_has_next)>,</#if>
                </#if>
            </#list>
        ]
        </#if>
    }<#if (wall_has_next)>,</#if>
    </#list>
]
</#if>
}
}
