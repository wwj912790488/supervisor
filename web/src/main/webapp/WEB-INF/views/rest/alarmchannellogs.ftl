{
    "code" : ${statusCode},
    "searchStart" : ${start?string('#')},
    "searchEnd" : ${end?string('#')},
    <#assign logs=logobjs>
    <#if logs?? && (logs?size > 0)>
    "logs" : [
        <#list logs as log>
        {
            "chid": ${log.channelId},
            "name": "${log.channelName}",
            "type": ${log.type},
            "startTime": "<#if log.startTimeAsDate??>${log.startTimeAsDate?string("yyyy/MM/dd HH:mm:ss")}<#else>--/--/-- --:--:--</#if>",
            "endTime": "<#if log.endTimeAsDate??>${log.endTimeAsDate?string("yyyy/MM/dd HH:mm:ss")}<#else>--/--/-- --:--:--</#if>"
        }<#if (log_has_next)>,</#if>
        </#list>
    ]
    </#if>
}
