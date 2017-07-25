package com.arcsoft.supervisor.cluster.converter;

import com.arcsoft.supervisor.cluster.action.Actions;
import com.arcsoft.supervisor.cluster.action.BaseRequest;
import com.arcsoft.supervisor.cluster.action.server.*;
import com.arcsoft.supervisor.cluster.action.settings.host.RebootRequest;
import com.arcsoft.supervisor.cluster.action.settings.host.ShutdownRequest;
import com.arcsoft.supervisor.cluster.action.settings.license.ListLicenseRequest;
import com.arcsoft.supervisor.cluster.action.settings.license.UpdateLicenseRequest;
import com.arcsoft.supervisor.cluster.action.settings.network.*;
import com.arcsoft.supervisor.cluster.action.settings.storage.*;
import com.arcsoft.supervisor.cluster.action.settings.time.GetNTPRequest;
import com.arcsoft.supervisor.cluster.action.settings.time.GetTimeZoneRequest;
import com.arcsoft.supervisor.cluster.action.settings.time.SetDateTimeRequest;
import com.arcsoft.supervisor.cluster.action.settings.time.SetTimeZoneRequest;
import com.arcsoft.supervisor.cluster.action.task.*;


/**
 * Converter between request and data package.
 * 
 * @author fjli
 */
public class RequestDataConverter extends XmlDataConverter<BaseRequest> {

	/**
	 * Construct new request data converter.
	 */
	public RequestDataConverter() {
		mapping(Actions.GET_AGENT_DESC, GetAgentDescRequest.class);
		mapping(Actions.ADD_AGENT, AddAgentRequest.class);
		mapping(Actions.REMOVE_AGENT, RemoveAgentRequest.class);
		mapping(Actions.ERROR_REPORT, ErrorReportRequest.class);
		mapping(Actions.STATE_REPORT, StateReportRequest.class);
		mapping(Actions.CAPS_CHANGED, CapabilitiesChangedRequest.class);
		mapping(Actions.GROUP_LIVE_BIND, GroupBindRequest.class);
		mapping(Actions.LIVE_BIND, BindRequest.class);
		mapping(Actions.LIVE_UNBIND, UnbindRequest.class);
		mapping(Actions.LIVE_ROLE_SWITCH_EVENT, LiveRoleSwitchRequest.class);
		mapping(Actions.LIVE_SWITCH_ROLE, SwitchRoleRequest.class);
		mapping(Actions.NETWORK_LIST, ListEthRequest.class);
		mapping(Actions.NETWORK_STAT, StatEthRequest.class);
		mapping(Actions.NETWORK_SAVE, SaveEthRequest.class);
		mapping(Actions.NETWORK_BOND, BondAndUpdateEthRequest.class);
		mapping(Actions.DNS_LIST, ListDNSRequest.class);
		mapping(Actions.DNS_ADD, AddDNSRequest.class);
		mapping(Actions.DNS_DELETE, DeleteDNSRequest.class);
		mapping(Actions.ROUTE_LIST, ListRouteRequest.class);
		mapping(Actions.ROUTE_ADD, AddRouteRequest.class);
		mapping(Actions.ROUTE_DELETE, DeleteRouteRequest.class);
		mapping(Actions.FIREWALL_LIST, ListFirewallRequest.class);
		mapping(Actions.FIREWALL_ADD, AddFirewallRequest.class);
		mapping(Actions.FIREWALL_DELETE, DeleteFirewallRequest.class);
		mapping(Actions.FIREWALL_START,	StartFirewallRequest.class);
		mapping(Actions.FIREWALL_STOP, StopFirewallRequest.class);
		mapping(Actions.FIREWALL_GET_STATUS, GetFirewallStatusRequest.class);
		mapping(Actions.SYSTEM_REBOOT, RebootRequest.class);
		mapping(Actions.SYSTEM_SHUTDOWN, ShutdownRequest.class);
		mapping(Actions.SYSTEM_SET_TIMEZONE, SetTimeZoneRequest.class);
		mapping(Actions.SYSTEM_GET_TIMEZONE, GetTimeZoneRequest.class);
		mapping(Actions.SYSTEM_SET_TIME, SetDateTimeRequest.class);
		mapping(Actions.SYSTEM_GET_NTP, GetNTPRequest.class);
		mapping(Actions.STORAGE_ADD, AddStorageRequest.class);
		mapping(Actions.STORAGE_DELETE, DeleteStorageRequest.class);
		mapping(Actions.STORAGE_MOUNT, MountStorageRequest.class);
		mapping(Actions.STORAGE_UNMOUNT, UnmountStorageRequest.class);
		mapping(Actions.STORAGE_FIND, FindStorageRequest.class);
		mapping(Actions.STORAGE_UPDATE, UpdateStorageRequest.class);
		mapping(Actions.STORAGE_FIND_REMOTE_MOUNTED, FindRemoteMountedStorageRequest.class);
		mapping(Actions.LICENSE_LIST, ListLicenseRequest.class);
		mapping(Actions.LICENSE_UPDATE, UpdateLicenseRequest.class);
		mapping(Actions.START_TASK, StartRequest.class);
		mapping(Actions.STOP_TASK, StopRequest.class);
		mapping(Actions.TASK_STATE_CHANGE, StateChangeRequest.class);
		mapping(Actions.GET_TASK_PROGRESS, GetTaskProgressRequest.class);
		mapping(Actions.GET_TASK_THUMBNAIL, GetTaskThumbnailRequest.class);
		mapping(Actions.TASK_PROCESS_DETECT, TaskProcessDetectRequest.class);
		mapping(Actions.MD5SUM_GENERATE, Md5sumRequest.class);
		mapping(Actions.MD5SUM_GENERATE_COMPLETE, Md5sumCompleteRequest.class);
		mapping(Actions.GET_TASK_STATE_FROM_CACHE, GetTaskStateFromCacheRequest.class);
        mapping(Actions.TASK_REPORT_CONTENT_DETECT_RESULT, ContentDetectResultRequest.class);
        mapping(Actions.TASK_COMPOSE_STREAM_SCREEN_WARNING_BORDER, ScreenWarningBorderRequest.class);
        mapping(Actions.TASK_COMPOSE_STREAM_DISPLAY_MESSAGE, DisplayMessageRequest.class);
		mapping(Actions.TASK_SWITCH_AUDIO_BY_CHANNEL, SwitchAudioByChannelRequest.class);
		mapping(Actions.TASK_COMPOSE_RELOAD, ReloadRequest.class);
		mapping(Actions.TASK_GET_TRANSCODER_XML, GetTranscoderXmlRequest.class);
		mapping(Actions.TASK_ALERT, AlertRequest.class);
        mapping(Actions.SDI_LIST, ListSDIRequest.class);
        mapping(Actions.GET_SDI, GetSDIRequest.class);
        mapping(Actions.RECOGNIZE_SDI, RecognizeSDIRequest.class);
        mapping(Actions.LIST_COMPONENTS, ListComponentRequest.class);
		mapping(Actions.TASK_DISPLAY_STYLED_MESSAGE, DisplayStyledMessageRequest.class);
	}

	@Override
	public int getDataType() {
		return Actions.TYPE_REQUEST;
	}

	@Override
	public Class<BaseRequest> getDataClass() {
		return BaseRequest.class;
	}

}
