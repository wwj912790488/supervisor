<?xml version="1.0" encoding="utf-8"?>
<MulticreenInfo>
		<resolution><#if (videoProfile.width)?? && (videoProfile.width > 0)>${videoProfile.width?c}<#else>-1</#if>x<#if videoProfile.height?? && (videoProfile.height > 0)>${videoProfile.height?c}<#else>-1</#if></resolution>
		<cellcount>${(screenPositionConfigs?size)?c}</cellcount>
    <cells>
    	<#list screenPositionConfigs as config>
        <cell idx="${(config.index)?c}">
            <video>
                <x>${(config.config.column * perWidth)?c}</x>
                <y>${(config.config.row * perHeight)?c}</y>
                <w><#if (config.config.column + config.config.x) != columnCount>${(perWidth*config.config.x)?c}<#else>${(perWidth*config.config.x)?c}</#if></w>
                <h><#if (config.config.row + config.config.y) != rowCount>${(perHeight*config.config.y)?c}<#else>${(perHeight*config.config.y)?c}</#if></h>
            </video>
            <audio>
            <#if (config.validindex>=0)>
                <id>${(AudioStartpid+config.validindex)?c}</id>
            <#else>
                <id></id>
            </#if>
            </audio>
            <channel>
             <#if (config.validindex>=0)>
                <title>${config.config.channelName}</title>
                <id><#if(config.config.originalId)??>${config.config.originalId}<#else>-1</#if></id>
                <url></url>
             <#else>
                <title></title>
                <id></id>
                <url></url>
             </#if>
            </channel>
        </cell>
        </#list>
    </cells>
</MulticreenInfo>