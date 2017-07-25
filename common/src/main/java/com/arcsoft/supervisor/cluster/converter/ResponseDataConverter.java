package com.arcsoft.supervisor.cluster.converter;

import com.arcsoft.supervisor.cluster.action.Actions;
import com.arcsoft.supervisor.cluster.action.BaseResponse;
import com.arcsoft.supervisor.cluster.action.server.*;
import com.arcsoft.supervisor.cluster.action.settings.host.RebootResponse;
import com.arcsoft.supervisor.cluster.action.settings.host.ShutdownResponse;
import com.arcsoft.supervisor.cluster.action.settings.license.ListLicenseResponse;
import com.arcsoft.supervisor.cluster.action.settings.license.UpdateLicenseResponse;
import com.arcsoft.supervisor.cluster.action.settings.network.*;
import com.arcsoft.supervisor.cluster.action.settings.storage.*;
import com.arcsoft.supervisor.cluster.action.settings.time.GetNTPResponse;
import com.arcsoft.supervisor.cluster.action.settings.time.GetTimeZoneResponse;
import com.arcsoft.supervisor.cluster.action.settings.time.SetDateTimeResponse;
import com.arcsoft.supervisor.cluster.action.settings.time.SetTimeZoneResponse;
import com.arcsoft.supervisor.cluster.action.task.*;


/**
 * Converter between response and data package.
 * 
 * @author fjli
 */
public class ResponseDataConverter extends XmlDataConverter<BaseResponse> {

	/**
	 * Construct new response converter.
	 */
	public ResponseDataConverter() {
		mapping(Actions.GET_AGENT_DESC, GetAgentDescResponse.class);
		mapping(Actions.ADD_AGENT, AddAgentResponse.class);
		mapping(Actions.REMOVE_AGENT, RemoveAgentResponse.class);
		mapping(Actions.ERROR_REPORT, ErrorReportResponse.class);
		mapping(Actions.STATE_REPORT, StateReportResponse.class);
		mapping(Actions.CAPS_CHANGED, CapabilitiesChangedResponse.class);
		mapping(Actions.GROUP_LIVE_BIND, GroupBindResponse.class);
		mapping(Actions.LIVE_BIND, BindResponse.class);
		mapping(Actions.LIVE_UNBIND, UnbindResponse.class);
		mapping(Actions.LIVE_ROLE_SWITCH_EVENT, LiveRoleSwitchResponse.class);
		mapping(Actions.LIVE_SWITCH_ROLE, SwitchRoleResponse.class);
		mapping(Actions.NETWORK_LIST, ListEthResponse.class);
		mapping(Actions.NETWORK_STAT, StatEthResponse.class);
		mapping(Actions.NETWORK_SAVE, SaveEthResponse.class);
		mapping(Actions.NETWORK_BOND, BondAndUpdateEthResponse.class);
		mapping(Actions.DNS_LIST, ListDNSResponse.class);
		mapping(Actions.DNS_ADD, AddDNSResponse.class);
		mapping(Actions.DNS_DELETE, DeleteDNSResponse.class);
		mapping(Actions.ROUTE_LIST, ListRouteResponse.class);
		mapping(Actions.ROUTE_ADD, AddRouteResponse.class);
		mapping(Actions.ROUTE_DELETE, DeleteRouteResponse.class);
		mapping(Actions.FIREWALL_LIST, ListFirewallResponse.class);
		mapping(Actions.FIREWALL_ADD, AddFirewallResponse.class);
		mapping(Actions.FIREWALL_DELETE, DeleteFirewallResponse.class);
		mapping(Actions.FIREWALL_START,	StartFirewallResponse.class);
		mapping(Actions.FIREWALL_STOP, StopFirewallResponse.class);
		mapping(Actions.FIREWALL_GET_STATUS, GetFirewallStatusResponse.class);
		mapping(Actions.SYSTEM_REBOOT, RebootResponse.class);
		mapping(Actions.SYSTEM_SHUTDOWN, ShutdownResponse.class);
		mapping(Actions.SYSTEM_SET_TIMEZONE, SetTimeZoneResponse.class);
		mapping(Actions.SYSTEM_GET_TIMEZONE, GetTimeZoneResponse.class);
		mapping(Actions.SYSTEM_SET_TIME, SetDateTimeResponse.class);
		mapping(Actions.SYSTEM_GET_NTP, GetNTPResponse.class);
		mapping(Actions.STORAGE_ADD, AddStorageResponse.class);
		mapping(Actions.STORAGE_DELETE, DeleteStorageResponse.class);
		mapping(Actions.STORAGE_MOUNT, MountStorageResponse.class);
		mapping(Actions.STORAGE_UNMOUNT, UnmountStorageResponse.class);
		mapping(Actions.STORAGE_UPDATE, UpdateStorageResponse.class);
		mapping(Actions.STORAGE_FIND_REMOTE_MOUNTED, FindRemoteMountedStorageResponse.class);
		mapping(Actions.STORAGE_FIND, FindStorageResponse.class);
		mapping(Actions.LICENSE_LIST, ListLicenseResponse.class);
		mapping(Actions.LICENSE_UPDATE, UpdateLicenseResponse.class);
		mapping(Actions.START_TASK, StartResponse.class);
		mapping(Actions.STOP_TASK, StopResponse.class);
		mapping(Actions.TASK_STATE_CHANGE, StateChangeResponse.class);
		mapping(Actions.GET_TASK_PROGRESS, GetTaskProgressResponse.class);
		mapping(Actions.GET_TASK_THUMBNAIL, GetTaskThumbnailResponse.class);
		mapping(Actions.TASK_PROCESS_DETECT, TaskProcessDetectResponse.class);
		mapping(Actions.MD5SUM_GENERATE, Md5sumResponse.class);
		mapping(Actions.MD5SUM_GENERATE_COMPLETE, Md5sumCompleteResponse.class);
		mapping(Actions.GET_TASK_STATE_FROM_CACHE, GetTaskStateFromCacheResponse.class);
        mapping(Actions.TASK_REPORT_CONTENT_DETECT_RESULT, ContentDetectResultResponse.class);
        mapping(Actions.TASK_COMPOSE_STREAM_SCREEN_WARNING_BORDER, ScreenWarningBorderResponse.class);
        mapping(Actions.TASK_COMPOSE_STREAM_DISPLAY_MESSAGE, DisplayMessageResponse.class);
		mapping(Actions.TASK_SWITCH_AUDIO_BY_CHANNEL, SwitchAudioByChannelResponse.class);
		mapping(Actions.TASK_COMPOSE_RELOAD, ReloadResponse.class);
		mapping(Actions.TASK_GET_TRANSCODER_XML, GetTranscoderXmlResponse.class);
		mapping(Actions.TASK_ALERT, AlertResponse.class);
        mapping(Actions.SDI_LIST, ListSDIResponse.class);
        mapping(Actions.GET_SDI, GetSDIResponse.class);
        mapping(Actions.RECOGNIZE_SDI, RecognizeSDIResponse.class);
        mapping(Actions.LIST_COMPONENTS, ListComponentResponse.class);
		mapping(Actions.TASK_DISPLAY_STYLED_MESSAGE, DisplayStyledMessageResponse.class);
	}

	@Override
	public int getDataType() {
		return Actions.TYPE_RESPONSE;
	}

	@Override
	public Class<BaseResponse> getDataClass() {
		return BaseResponse.class;
	}

}
