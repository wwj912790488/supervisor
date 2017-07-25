{
"r":"
<#if (videoProfile.width)?? && (videoProfile.width > 0)>${videoProfile.width?c}
    <#else>-1
</#if>x
<#if videoProfile.height?? && (videoProfile.height > 0)>${videoProfile.height?c}
    <#else>-1
</#if>",
"s":${(screenPositionConfigs?size)?c},
"cs":{
<#if screenPositionConfigs?? && (screenPositionConfigs?size > 0)>
    "c":[
    <#list screenPositionConfigs as config>
        {
        "i":${(config.index)?c},
        <#if (config.config.apiHeart)??>
            "heart":"${config.config.apiHeart}",
            <#else>
                "heart":-1,
        </#if>
        "p":"${(config.config.column * perWidth)?c},${(config.config.row * perHeight)?c},
        <#if (config.config.column + config.config.x) != columnCount>${(perWidth*config.config.x)?c}
            <#else>${(perWidth*config.config.x)?c}
        </#if>
        ,
        <#if (config.config.row + config.config.y) != rowCount>${(perHeight*config.config.y)?c}
            <#else>${(perHeight*config.config.y)?c}
        </#if>
        ",
        <#if (config.validindex>=0)>
            "a":${(AudioStartpid+config.validindex)?c},
            <#if (config.config.originalId)??>
                "id":"${config.config.originalId}"
                <#else>
                    "id":-1
            </#if>
            <#else>
                "a":-1,
                "id":-1
        </#if>
        }
        <#if (config_has_next)>,</#if>
    </#list>
    ]
</#if>
}
}
