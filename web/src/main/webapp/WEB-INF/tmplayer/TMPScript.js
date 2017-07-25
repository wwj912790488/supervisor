var ATMPlayer = null;  // the instance of tmplayer plugin
var boxLeft = 0, boxTop = 0, boxRight = 0, boxBottom = 0; // temporary varialbes for selecting rectangle box.

/**
* Description: 
*	The function to plug a tmplayer in web page.
* Parameter:
*   width: [IN] the width of tmplayer plugin.
*   height: [IN] the height of tmplayer plugin.
* Return:
* Remarks:
*
*/
function WelcomeTMPlayer(width, height) {
    document.writeln('<object classid="clsid:b14dcdc6-dc3a-4e99-80b2-3169b06ef069"');
    document.writeln('codebase="TMPlayer.CAB#Version=2,0,0,91"');
    document.writeln('id="ArcSoft_TMPlayer"');
    document.writeln('width="' + width + '" height="' + height + '"');
    document.writeln('viewastext standby="Loading ArcSoft TotalMedia Player ...">');
    document.writeln('<param name="ApplicationType" value="0" />');
    document.writeln('<param name="PanelType" value="3" />');
    document.writeln('<param name="ResizeMode" value="7" />');
    //document.writeln('<param name="InitVars" value="DisplayMode=0" />');
    document.writeln('<br/><br/>');
    document.writeln('<p style="font-size:12px">This website wants to install ArcSoft TotalMedia Player.<br/>');
	document.writeln('If you did not see a notice, please check the security policy of your browser and computer.<br/></p>');
	document.writeln('<a href="TMPSetup.exe"><b style="color:green;font-size:13px">Or directly click here to install ArcSoft TotalMedia Player.</b></a><br/>');
	document.writeln('<a href="#" onclick="location.reload()"><b style="font-size:13px">Please refresh this web page after you finish installation.</b></a><br/>');
    document.writeln('</object>');
    ATMPlayer = document.getElementById("ArcSoft_TMPlayer");
    //ATMPlayer.attachEvent("PlayState", PlayStateChanged);
    //ATMPlayer.attachEvent("TimePosition", PlaybackTimeChanged);
    //ATMPlayer.attachEvent("SelBoxPosition", SelBoxPositionChanged);
    //ATMPlayer.attachEvent("SelPointPosition", SelPointChanged);
    //ATMPlayer.attachEvent("FullScreen", FullScreenEvent);
}

/**
* Description: 
*	The event function for playback state changed.
* Parameter:
*   state: the state of playback, as follows:
*   STATE_CLOSED    = 0, no media opened, or media closed.
*   STATE_STOPPED   = 1, playback is stopped.
*   STATE_PAUSED    = 2, playback is paused.
*   STATE_RUNNING   = 3, playback is running.
*   STATE_INITING   = 4, in process of opening media.
*	STATE_COMPLETE  = 5, playback is completed
*	STATE_OPENOK    = 6, media opened successfully
*	STATE_OPENFAIL  = 7, failed to open media.
*	STATE_ERRORABORT = -1, playback is aborted because of error
* Return:
* Remarks:
*
*/
function PlayStateChanged(state) {
};

/**
* Description: 
*	The event function for playback time position changed, triggered periodically in around every second.
* Parameter:
*   time: the time position of playback, milliseconds.
* Return:
* Remarks:
*
*/
function PlaybackTimeChanged(time) {
}

/**
* Description: 
*	The event function for selecting rectangle box, triggered once rectangle position/size changed.
* Parameter:
*   left: the left position of box.
*   top: the top position of box.
*   right: the right position of box.
*   bottom: the bottom position of box.
* Return:
* Remarks:
*
*/
function SelBoxPositionChanged(left, top, right, bottom) {
    boxLeft = left;
    boxTop = top;
    boxRight = right;
    boxBottom = bottom;
    //ApplySelBoxForMaskout();
}

/**
* Description: 
*	The event function for selecting a point, triggered once the point position changed or mouse left button is down/up.
* Parameter:
*   x: the x positon of point.
*   y: the y position of point.
*   flag: the mouse state as follows.
*       0x0200: Mouse is moving.
*       0x0201: Left button of mouse is pressed down.
*       0x0202: Left button of mouse is released up.
* Return:
* Remarks:
*
*/
function SelPointChanged(x, y, flag) {
}

/**
* Description: 
*	The event function to notify playback got in/off full screen.
* Parameter:
*   fullScreen: the screen mode of playback.
*       1: in full screen. 
*       0: not in full screen -- in other words, in normal screen.
* Return:
* Remarks:
*
*/
function FullScreenEvent(fullScreen) {
}

/**
* Description: 
*	The sample function to add rectangle box.
* Parameter:
* Return:
*	S_OK: successful, no error.
*	Positive value: uncritical error.
*	Negative value: critical error.
* Remarks:
*
*/
function AddSelBoxForMosaic() {
    return ATMPlayer.AddRectBox(1, boxLeft, boxTop, boxRight, boxBottom, 1, 0x00ff00, 1); 
}

/**
* Description: 
*	The sample function to add rectangle box.
* Parameter:
* Return:
*	S_OK: successful, no error.
*	Positive value: uncritical error.
*	Negative value: critical error.
* Remarks:
*
*/
function AddSelBoxForBorder() {
    return ATMPlayer.AddRectBox(2, boxLeft, boxTop, boxRight, boxBottom, 1, 0xffffff, 1);
}

/**
* Description: 
*	The sample function to set a mosaic mask rectangle area.
* Parameter:
* Return:
*	S_OK: successful, no error.
*	Positive value: uncritical error.
*	Negative value: critical error.
* Remarks:
*
*/
function ApplySelBoxForMosaic() {
    return ATMPlayer.AddMosaicMask(boxLeft, boxTop, boxRight-boxLeft, boxBottom-boxTop, 70);
}

/**
* Description: 
*	The sample function to mask out video.
* Parameter:
* Return:
*	S_OK: successful, no error.
*	Positive value: uncritical error.
*	Negative value: critical error.
* Remarks:
*
*/
function ApplySelBoxForMaskout() {
    return ATMPlayer.ApplyMaskout(boxLeft, boxTop, boxRight, boxBottom, 127, 0xffffff);
}

/**
* Description: 
*	The sample function to trim out video.
* Parameter:
* Return:
*	S_OK: successful, no error.
*	Positive value: uncritical error.
*	Negative value: critical error.
* Remarks:
*
*/
function ApplySelBoxForTrimout() {
    var ratio = GetVideoDisplayWidth() * GetVideoHeight() / (GetVideoDisplayHeight() * GetVideoWidth());
    var ret = ApplyTrimout(boxLeft, boxTop, boxRight, boxBottom);
    if (ret >= 0) {
        var newDisplayWidth = GetVideoWidth() * ratio;
        var newDisplayHeight = GetVideoHeight();
        ChangeAspectRatio(newDisplayWidth, newDisplayHeight, 0, 0x000000);
        ResizeTMPlayer(480, newDisplayHeight * 480 / newDisplayWidth);
        SetSelBoxPosition(0, 0, 0, 0);
    }
    return ret;
}

/**
* Description: 
*	The function to resize tmplayer size.
* Parameter:
*   newWidth: [IN] the new width of tmplayer.
*   newHeight: [IN] the new height of tmplayer.
* Return:
* Remarks:
*
*/
function ResizeTMPlayer(newWidth, newHeight) {
    if (ATMPlayer != null) {
        ATMPlayer.width = newWidth;
        ATMPlayer.height = newHeight;
    }
}

/**
* Description: 
*	The function to open media.
* Parameter:
*   mediaPath: [IN] the URL or full path of media.
*   mediaType: [IN] 0 for normal file, 1 for BD folder, 2 for DVD folder, 3 for UDP media, 4 for FTP media, 11 for SDP file.
*   subtitleWanted: [IN] 0 indicates turning off subtitle, 1 indicates turn on subtitle
* Return:
*	S_OK: successful, no error.
*	Positive value: uncritical error.
*	Negative value: critical error.
* Remarks:
*
*/
function OpenMedia(mediaPath, mediaType, subtitleWanted) {
    var ret = 0;
    if (mediaPath != 0 && mediaPath.length != 0) {
        ret = ATMPlayer.LoadMedia(mediaPath, "", subtitleWanted * 0x1000000 + mediaType * 0x10000 + 3);
    }
    return ret;
}

/**
* Description: 
*	The function to open and play media.
* Parameter:
*   mediaPath: [IN] the URL or full path of media.
*   mediaType: [IN] 0 for normal file, 1 for BD folder, 2 for DVD folder, 3 for UDP media, 4 for FTP media, 11 for SDP file.
*   subtitleWanted: [IN] 0 indicates turning off subtitle, 1 indicates turn on subtitle
* Return:
*	S_OK: successful, no error.
*	Positive value: uncritical error.
*	Negative value: critical error.
* Remarks:
*
*/
function PlayMedia(mediaPath, mediaType, subtitleWanted) {
    var ret = 0;
    if (mediaPath != 0 && mediaPath.length != 0) {
        ret = ATMPlayer.LoadMedia(mediaPath, "", subtitleWanted * 0x1000000 + mediaType * 0x10000 + 3);
        if (ret == 0) {
            ret = ATMPlayer.Play();
        }
    }
    return ret;
}

/**
* Description: 
*	The function to close media.
* Parameter:
* Return:
*	S_OK: successful, no error.
*	Positive value: uncritical error.
*	Negative value: critical error.
* Remarks:
*
*/
function CloseMedia() {
    return ATMPlayer.Close();
}

/**
* Description: 
*	The function to start playback.
* Parameter:
* Return:
*	S_OK: successful, no error.
*	Positive value: uncritical error.
*	Negative value: critical error.
* Remarks:
*
*/
function StartPlayback() {
    return ATMPlayer.Play();
}

/**
* Description: 
*	The function to pause playback.
* Parameter:
* Return:
*	S_OK: successful, no error.
*	Positive value: uncritical error.
*	Negative value: critical error.
* Remarks:
*
*/
function PausePlayback() {
    return ATMPlayer.Pause();
}

/**
* Description: 
*	The function to stop playback.
* Parameter:
* Return:
*	S_OK: successful, no error.
*	Positive value: uncritical error.
*	Negative value: critical error.
* Remarks:
*
*/
function StopPlayback() {
    return ATMPlayer.Stop();
}

/**
* Description: 
*	The function to get the current time position of playback, milliseconds.
* Parameter:
* Return:
*   The current time position of playback.
* Remarks:
*
*/
function GetPlaybackTime() {
    return ATMPlayer.GetPlaybackTime();
}

/**
* Description: 
*	The function to seek playback to the time position.
* Parameter:
*   time: [IN] time position, milliseconds.
*   flag: [IN] 1 means the specified position is absolute time, 2 means the specified position is relative to the current time.
*   accurate: [IN] indicates whether to seek accurately or not, 1 for accurate, 0 for rough.
* Return:
*	S_OK: successful, no error.
*	Positive value: uncritical error.
*	Negative value: critical error.
* Remarks:
*
*/
function SeekByTime(time, flag, accurate) {
    return ATMPlayer.Seek(time, accurate * 0x01000000 + flag);
}

/**
* Description: 
*	The function to query whether playback is in full screen.
* Parameter:
* Return:
*   1 if full screen, else 0.
* Remarks:
*
*/
function GetFullScreen() {
    return ATMPlayer.GetFullScreen();
}

/**
* Description: 
*	The function to query whether video can step frame(s) forward at a time.
* Parameter:
*   bMultiple: [IN] zero or positive integer value.
* Return:
*   If bMultiple is zero and the method returns S_OK, video can step one frame at a time. 
*   If bMultiple is greater than zero and the method returns S_OK, video can step bMultiple frames at a time. 
*   Else video can't step forward
* Remarks:
*
*/
function CanStep(bMultiple) {
    return ATMPlayer.CanStep(bMultiple);
}

/**
* Description: 
*	The function to step frame(s) forward/backward.
* Parameter:
*   frames: [IN] the count of video frames to step forward/backward, positive value for forward, negative value for backward.
* Return:
*	S_OK: successful, no error.
*	Positive value: uncritical error.
*	Negative value: critical error.
* Remarks:
*
*/
function StepByFrame(frames) {
    return ATMPlayer.Step(frames);
}

/**
* Description: 
*	The function to get the playback state: 
* Parameter:
*   frames: the count of video frames to step forward, must be positive integer value.
* Return:
*   STATE_CLOSED    = 0, no media opened, or media closed.
*   STATE_STOPPED   = 1, playback is stopped.
*   STATE_PAUSED    = 2, playback is paused.
*   STATE_RUNNING   = 3, playback is running.
*   STATE_INITING   = 4, in process of opening media.
* Remarks:
*
*/
function GetPlayState() {
    return ATMPlayer.GetPlayState();
}

/**
* Description: 
*	The function to get the total duration of media, in milliseconds.
* Parameter:
* Return:
*	the total duration of media, in milliseconds.
* Remarks:
*   Invoke this function after OpenMedia method invoked, or return 0.
*
*/
function GetMediaDuration() {
    return ATMPlayer.GetMediaDuration();
}

/**
* Description: 
*	The function to get video display width
* Parameter:
* Return:
*	video display width
* Remarks:
*   Invoke this function after OpenMedia method invoked, or return negative value.
*
*/
function GetVideoDisplayWidth() {
    return ATMPlayer.GetVideoDisplayWidth();
}

/**
* Description: 
*	The function to get video display height
* Parameter:
* Return:
*	video display height
* Remarks:
*   Invoke this function after OpenMedia method invoked, or return negative value.
*
*/
function GetVideoDisplayHeight() {
    return ATMPlayer.GetVideoDisplayHeight();
}

/**
* Description: 
*	The function to get video width
* Parameter:
* Return:
*	video width
* Remarks:
*   Invoke this function after OpenMedia method invoked, or return negative value.
*
*/
function GetVideoWidth() {
    return ATMPlayer.GetVideoWidth();
}

/**
* Description: 
*	The function to get video height
* Parameter:
* Return:
*	video height
* Remarks:
*   Invoke this function after OpenMedia method invoked, or return negative value.
*
*/
function GetVideoHeight() {
    return ATMPlayer.GetVideoHeight();
}

/**
* Description: 
*	The function to get the frame rate of video
* Parameter:
* Return:
*	the frame rate of video, float number, frames per second.
* Remarks:
*   Invoke this function after OpenMedia method invoked, or return 0.
*
*/
function GetVideoFrameRate() {
    return ATMPlayer.GetVideoFrameRate();
}

/**
* Description: 
*	The function to get the bit rate of video.
* Parameter:
* Return:
*	the bit rate of video, integer number, bits per second.
* Remarks:
*   Invoke this function after OpenMedia method invoked, or return 0.
*
*/
function GetVideoBitRate() {
    return ATMPlayer.GetVideoBitRate();
}

/**
* Description: 
*	The function to change the aspect ratio of video.
* Parameter:
*   aspectX: [IN] x-direction value for aspect ratio, 0 indicates no change.
*   aspectY: [IN] y-direction value for aspect ratio, 0 indicates no change.
*   operate: [IN] 0: fill with color, 1: inflate, -1: deflate, -100: remove the last setting.
*   fillColor: [IN] fill color, in 0xBBGGRR format.
* Return:
*	S_OK: successful, no error.
*	Positive value: uncritical error.
*	Negative value: critical error.
* Remarks:
*
*/
function ChangeAspectRatio(aspectX, aspectY, operate, fillColor) {
    return ATMPlayer.ChangeAspectRatio(aspectX, aspectY, operate, fillColor);
}

/**
* Description: 
*	The function to set a logo image.
* Parameter:
*   logoUrl: [IN] the url of logo image, null value indicates removing all logo image(s).
*   xPos: [IN] x positon of logo relative to output rectangle
*   yPos: [IN] y positon of logo relative to output rectangle
*   width: [IN] width of logo, 
*       (1) -1 indicates removing the logo image, 
*       (2) 0 indicates using the native width of logo image, 
*       (3) if highest byte((0xFF000000)) is 1, indicates video output width used to scale logo image.
*   height: [IN] height of logo,
*       (1) -1 indicates removing the logo image, 
*       (2) 0 indicates using the native height of logo image, 
*       (3) if highest byte((0xFF000000)) is 1, indicates video output height used to scale logo image.
*   alpha: [IN] the value of alpha channel, ranging from 0(complete transparent) to 255(complete opaque).
* Return:
*	S_OK: successful, no error.
*	Positive value: uncritical error.
*	Negative value: critical error.
* Remarks:
*
*/
function AddLogoImage(logoUrl, xPos, yPos, width, height, alpha) {
    return ATMPlayer.AddLogoImage(logoUrl, xPos, yPos, width, height, alpha);
}

/**
* Description: 
*	The function to set the image used for cutting media by time.
* Parameter:
*   imgUrl: [IN] the url of image, null value indicates removing the image.
*   xPos: [IN] x positon of image relative to output rectangle
*   yPos: [IN] y positon of image relative to output rectangle
*   width: [IN] width of image, 0 indicates using the native width of image
*   height: [IN] height of image, 0 indicates using the native height of image
*   alpha: [IN] the value of alpha channel, ranging from 0(complete transparent) to 255(complete opaque).
*   mode: [IN] 0 indicates the time segment remained, 1 indicates the time segment removed.
* Return:
*	S_OK: successful, no error.
*	Positive value: uncritical error.
*	Negative value: critical error.
* Remarks:
*
*/
function SetCutTimeImage(imgUrl, xPos, yPos, width, height, alpha, mode) {
    return ATMPlayer.SetCutTimeImage(imgUrl, xPos, yPos, width, height, alpha, mode);
}

/**
* Description: 
*	The function to add a time segment used for cutting media.
* Parameter:
*   beginTime: [IN] the begin time of time segment, milliseconds.
*   endTime: [IN] the end time of time segment, milliseconds.
* Return:
*	S_OK: successful, no error.
*	Positive value: uncritical error.
*	Negative value: critical error.
* Remarks:
*
*/
function AddCutTime(beginTime, endTime) {
    return ATMPlayer.AddCutTime(beginTime, endTime);
}

/**
* Description: 
*	The function to add a rectangle area for mosaic mask.
* Parameter:
*   xPos: [IN] x positon of mosaic box, relative to original video frame
*   yPos: [IN] y positon of mosaic box, relative to original video frame
*   width: [IN] width of mosaic box, relative to original video frame
*   height: [IN] height of mosaic box, relative to original video frame
*   style: [IN] the style of mosaic, 0:granule(颗粒), 1:erasion(擦除), 2:blur(模糊)
*   density: [IN] the density of mosaic, ranging from 0 to 100, more larger, more heavy.
*   beginTime: [IN] the begin time of mosaic, milliseconds.
*   endTime: [IN] the end time of mosaic, -1 indicates the end of media, milliseconds.
* Return:
*	S_OK: successful, no error.
*	Positive value: uncritical error.
*	Negative value: critical error.
* Remarks:
*
*/
function AddMosaicMask(xPos, yPos, width, height, density) {
    return ATMPlayer.AddMosaicMask(xPos, yPos, width, height, density); 
    // i.e.: ATMPlayer.AddMosaicMaskEx(xPos, yPos, width, height, 0, density, 0, -1);
}
function AddMosaicMaskEx(xPos, yPos, width, height, style, density, beginTime, endTime) {
    return ATMPlayer.AddMosaicMaskEx(xPos, yPos, width, height, style, density, beginTime, endTime);
}

/**
* Description: 
*	The function to clear all rectangle areas for mosaic.
* Parameter:
* Return:
*	S_OK: successful, no error.
*	Positive value: uncritical error.
*	Negative value: critical error.
* Remarks:
*
*/
function ClearMosaicMask() {
    return ATMPlayer.ClearMosaicMask();
}

/**
* Description: 
*	The function to mask video outside the specified rectangle area.
* Parameter:
*   left: [IN] the left position of rectangle.
*   top: [IN] the top position of rectangle.
*   right: [IN] the right position of rectangle.
*   bottom: [IN] the bottom position of rectangle.
*   alpha: [IN] alpha value for mask, ranging from 0 to 255.
*   maskColor: [IN] mask color, in 0xBBGGRR format.
* Return:
*	S_OK: successful, no error.
*	Positive value: uncritical error.
*	Negative value: critical error.
* Remarks:
*
*/
function ApplyMaskout(left, top, right, bottom, alpha, maskColor) {
    return ATMPlayer.ApplyMaskout(left, top, right, bottom, alpha, maskColor);
}

/**
* Description: 
*	The function to trim video outside the specified rectangle area.
* Parameter:
*   left: [IN] the left position of rectangle.
*   top: [IN] the top position of rectangle.
*   right: [IN] the right position of rectangle.
*   bottom: [IN] the bottom position of rectangle.
* Return:
*	S_OK: successful, no error.
*	Positive value: uncritical error.
*	Negative value: critical error.
* Remarks:
*
*/
function ApplyTrimout(left, top, right, bottom) {
    return ATMPlayer.ApplyTrimout(left, top, right, bottom);
}

/**
* Description: 
*	The function to add/remove black margin of video.
* Parameter:
*   leftMargin: [IN] left margin to remove(positive value) or add(negative value), relative to original video frame
*   topMargin: [IN] top margin remove(positive value) or add(negative value), relative to original video frame
*   rightMargin: [IN] right margin to remove(positive value) or add(negative value), relative to original video frame
*   bottomMargin: [IN] bottom margin to remove(positive value) or add(negative value), relative to original video frame
*   fillColor: [IN] fill color, in 0xBBGGRR format.
* Return:
*	S_OK: successful, no error.
*	Positive value: uncritical error.
*	Negative value: critical error.
* Remarks:
*
*/
function ChangeBlackMargin(leftMargin, topMargin, rightMargin, bottomMargin, fillColor) {
    return ATMPlayer.ChangeBlackMargin(leftMargin, topMargin, rightMargin, bottomMargin, fillColor);
}

/**
* Description: 
*	The function to select a rectangle box on video.
* Parameter:
*   begin: [IN] 1 indicate beginning to select box, 0 indicate ending the last selecting box.
*   lineColor: [IN] the color for box line, in 0xRRGGBB format.
* Return:
*   1: the previous selecting box is enabled.
*   0: the previous selecting box is disabled.
* Remarks:
*
*/
function SelectRectBox(begin, lineColor) 
{
    return ATMPlayer.SelectRectBox(begin, lineColor);
}

/**
* Description: 
*	The function to set a rectangle used for selecting box.
* Parameter:
*   left: [IN] the left position of rectangle.
*   top: [IN] the top position of rectangle.
*   right: [IN] the right position of rectangle.
*   bottom: [IN] the bottom position of rectangle.
* Return:
*	S_OK: successful, no error.
*	Positive value: uncritical error.
*	Negative value: critical error.
* Remarks:
*
*/
function SetSelBoxPosition(left, top, right, bottom)
{
    return ATMPlayer.SetSelBoxPosition(left, top, right, bottom);
}

/**
* Description: 
*	The function to add a rectangle box.
* Parameter:
*   boxType: [IN] any integer value, for app to identify this box type such as "Black Border", "Mosaic".
*   left: [IN] the left position of rectangle.
*   top: [IN] the top position of rectangle.
*   right: [IN] the right position of rectangle.
*   bottom: [IN] the bottom position of rectangle.
*   lineWidth: [IN] the width of line.
*   lineColor: [IN] the color of line, in 0xAARRGGBB format.
*   bShow: [IN] whether to show this box.
* Return:
*	S_OK: successful, no error.
*	Positive value: uncritical error.
*	Negative value: critical error.
* Remarks:
*
*/
function AddRectBox(boxType, left, top, right, bottom, lineWidth, lineColor, bShow)
{
    return ATMPlayer.AddRectBox(boxType, left, top, right, bottom, lineWidth, lineColor, bShow);
}

/**
* Description: 
*	The function to remove rectangle box with specific box type and left-top position.
* Parameter:
*   boxType: [IN] the type of box that you want to remove, Notes: this type identifier is defined by app in AddRectBox method.
*   boxLeft: [IN] the left position of rectangle box that you want to remove.
*   boxTop: [IN] the top position of rectangle box that you want to remove.
* Return:
*	S_OK: successful, no error.
*	Positive value: uncritical error.
*	Negative value: critical error.
* Remarks:
*
*/
function RemoveRectBox(boxType, boxLeft, boxTop)
{
    return ATMPlayer.RemoveRectBox(boxType, boxLeft, boxTop);
}

/**
* Description: 
*	The function to remove rectangle box(es) with specific box type.
* Parameter:
*   boxType: [IN] the type of box that you want to remove, Notes: this type identifier is defined by app in AddRectBox method.
* Return:
*	S_OK: successful, no error.
*	Positive value: uncritical error.
*	Negative value: critical error.
* Remarks:
*
*/
function RemoveRectBoxWithType(boxType)
{
    return ATMPlayer.RemoveRectBoxWithType(boxType);
}

/**
* Description: 
*	The function to show/hide rectangle box(es) with specific box type.
* Parameter:
*   boxType: [IN] the type of box that you want to remove, Notes: this type identifier is defined by app in AddRectBox method.
*   bShow: [IN] whether to show this box.
* Return:
*	S_OK: successful, no error.
*	Positive value: uncritical error.
*	Negative value: critical error.
* Remarks:
*
*/
function ShowRectBox(boxType, bShow)
{
    return ATMPlayer.ShowRectBox(boxType, bShow);
}

/**
* Description: 
*	The function to select a point position on video.
* Parameter:
*   bBegin: [IN] 1 indicate beginning to select point, 0 indicate ending the last selecting point.
*   color: [IN] the color for point, in 0xRRGGBB format.
* Return:
*   1: the previous selecting point is enabled.
*   0: the previous selecting point is disabled.
* Remarks:
*
*/
function SelectPoint(begin, color) {
    return ATMPlayer.SelectPoint(begin, color);
}

/**
* Description: 
*	The function to save/upload the snapshot image of current video frame.
* Parameter:
*   filePath: [IN] the full file path or http URL to which the snapshot image will be saved/uploaded.
*   type: [IN] the image format type you want. 1: bmp, 4: jpeg.
*   width: [IN] useless for now, reserved for future to specify the image width you want, 0 indicates using the native width of video.
*   height: [IN] useless for now, reserved for future to specify the image height you want, 0 indicates using the native height of video.
* Return:
*	S_OK: successful, no error.
*	Positive value: uncritical error.
*	Negative value: critical error.
* Remarks:
*
*/
function SaveVideoSnapshot(filePath, type, width, height) {
    return ATMPlayer.SaveVideoSnapshot(filePath, type, width, height);
}

/**
* Description: 
*	The function to get the snapshot image of current video frame.
* Parameter:
*   type: [IN] the image format type you want. 1: bmp, 4: jpeg.
*   width: [IN] useless for now, reserved for future to specify the image width you want, 0 indicates using the native width of video.
*   height: [IN] useless for now, reserved for future to specify the image height you want, 0 indicates using the native height of video.
*   bWideString: [IN] to specify what string you want to return, 1 for UTF-16 string, 0 for UTF-8 string. Reminder: JavaScript uses UTF-16 string.
* Return:
*   the base64-encoded string of image, user need base64-decode this string to get image data.
* Remarks:
*
*/
function GetVideoSnapshot(type, width, height, bWideString) {
    return ATMPlayer.GetVideoSnapshot(type, width, height, bWideString);
}

/**
* Description: 
*	The function to get the count of programs in media.
* Parameter:
* Return:
*   the count of programs in media.
* Remarks:
*
*/
function MediaProgramCount() {
    return ATMPlayer.MediaProgramCount();
}

/**
* Description: 
*	The function to get the program description.
* Parameter:
*   programIndex: [IN] the zero-based index of program, -1 indicates the current program.
* Return:
*   the string of program description.
* Remarks:
*
*/
function MediaProgramDesc(programIndex) {
    return ATMPlayer.MediaProgramDesc(programIndex);
}

/**
* Description: 
*	The function to get the zero-based index of current program.
* Parameter:
* Return:
*   the zero-based index of current program.
* Remarks:
*
*/
function CurrentMediaProgram() {
    return ATMPlayer.CurrentMediaProgram();
}

/**
* Description: 
*	The function to get the count of audio streams embedded in program.
* Parameter:
*   programIndex: [IN] the zero-based index of program, -1 indicates the current program.
* Return:
*   the count of audio streams embedded in program.
* Remarks:
*
*/
function MediaAudioCount(programIndex) {
    return ATMPlayer.MediaAudioCount(programIndex);
}

/**
* Description: 
*	The function to get the language code of audio stream.
* Parameter:
*   programIndex: [IN] the zero-based index of program, -1 indicates the current program.
*   audioIndex: [IN] the zero-based index of audio, -1 indicates the current audio.
* Return:
*   ISO 639-2 language code, for example: "eng", "chi"
* Remarks:
*
*/
function MediaAudioLanguage(programIndex, audioIndex) {
    return ATMPlayer.MediaAudioLanguage(programIndex, audioIndex);
}

/**
* Description: 
*	The function to get the PID of audio stream.
* Parameter:
*   programIndex: [IN] the zero-based index of program, -1 indicates the current program.
*   audioIndex: [IN] the zero-based index of audio, -1 indicates the current audio.
* Return:
*   the PID of audio stream.
* Remarks:
*
*/
function MediaAudioPID(programIndex, audioIndex) {
    return ATMPlayer.MediaAudioPID(programIndex, audioIndex);
}

/**
* Description: 
*	The function to get the zero-based index of current audio in the specified program.
* Parameter:
*   programIndex: [IN] the zero-based index of program, -1 indicates the current program.
* Return:
*   the zero-based index of current audio in the specified program
* Remarks:
*
*/
function CurrentMediaAudio(programIndex) {
    return ATMPlayer.CurrentMediaAudio(programIndex);
}

/**
* Description: 
*	The function to select program/video/audio/subtitle of media by index number.
* Parameter:
*   programIndex: [IN] the zero-based index of program, -1 indicates selected by default.
*   videoIndex: [IN] the zero-based index of video, -1 indicates selected by default, -2 indicates no video.
*   audioIndex: [IN] the zero-based index of audio, -1 indicates selected by default, -2 indicates no audio.
*   subtitleIndex: [IN] the zero-based index of subtitle, -1 indicates selected by default, -2 indicates no subtitle.
* Return:
*	S_OK: successful, no error.
*	Positive value: uncritical error.
*	Negative value: critical error.
* Remarks:
*
*/
function SelectMediaTrack(programIndex, videoIndex, audioIndex, subtitleIndex) {
    return ATMPlayer.SelectMediaTrack(programIndex, videoIndex, audioIndex, subtitleIndex);
}

/**
* Description: 
*	The function to select program/video/audio/subtitle of media by stream PID.
* Parameter:
*   programIndex: [IN] the zero-based program index, or (0x10000000 + PID). -1 indicates unspecified.
*   videoPID: [IN] the PID of video, -1 indicates selected by default, -2 indicates no video.
*   audioPID: [IN] the PID of audio, -1 indicates selected by default, -2 indicates no audio.
*   subtitlePID: [IN] the PID of subtitle, -1 indicates selected by default, -2 indicates no subtitle.
* Return:
*	S_OK: successful, no error.
*	Positive value: uncritical error.
*	Negative value: critical error.
* Remarks:
*
*/
function SelectTrackByID(programIndex, videoPID, audioPID, subtitlePID) {
    return ATMPlayer.SelectTrackByID(programIndex, videoPID, audioPID, subtitlePID);
}

/**
* Description: 
*	The function to get the sound decibel of each channel in current audio stream.
* Parameter:
* Return:
*   the string to indicate sound decibel of each channel in current audio stream: 
*   1. "channels count,db1,db2,db3,..." if successful, for example: "2,-25,-26".
*       (1) The maximum count of channels is 8.
*       (2) The audio decibel range is [-75, 0]. 
*       (3) The audio decibel order in string(in accordance with ANSI/CEA-863-A):
*           Order       Channel name
*           0 			Front left
*           1 			Front right
*           2			Center
*           3			Low frequency
*           4			Surround left
*           5			Surround right
*           6			Surround back left
*           7			Surround back right
*   2. "negative error code" if failed, for example: "-2147467259".
* Remarks:
*
*/
function GetCurrentSoundDb() {
    return ATMPlayer.GetCurrentSoundDb();
}

/**
* Description: 
*	The function to add an external subtitle, SRT for now.
* Parameters:
*	url: [IN] the url or path of external subtitle file, null or empty string indicates to close the last subtitle.
*	textPos: [IN] the relative position in y direction of subtitle text, ranging from 0 to 100, measuring from top to bottom. 
*	bPosToFullSize: [IN] whether or not the position textPos is relative to full video size. 
*	fontName: [IN] the font name for subtitle text. 
*	fontColor: [IN] the font color for subtitle text, in 0xRRGGBB format. 
*	fontSize: [IN] the font size for subtitle text. 
*	delayTime: [IN] the delay time(milliseconds) to display subtitle text, used to tune display time. Positive value for delay, negative value for advance.
* Return:
*	S_OK: successful, no error.
*	Positive value: uncritical error.
*	Negative value: critical error.
* Remarks:
*
*/
function AddExtSubtitle(url, textPos, bPosToFullSize, fontName, fontColor, fontSize, delayTime) {
    return ATMPlayer.AddExtSubtitle(filePath, textPos + (bPosToFullSize<<16), fontName, fontColor, fontSize, delayTime);
}

/**
* Description: 
*	The function to set the relative y position for subtitle text.
* Parameters:
*	textPos: [IN] the relative position in y direction of subtitle text, ranging from 0 to 100, measuring from top to bottom. 
*	bPosToFullSize: [IN] whether or not the position textPos is relative to full video size. 
* Return:
*	S_OK: successful, no error.
*	Positive value: uncritical error.
*	Negative value: critical error.
* Remarks:
*
*/
function SetExtSubtitlePos(textPos, bPosToFullSize) {
    return ATMPlayer.SetExtSubtitlePos(textPos + (bPosToFullSize << 16));
}

/**
* Description: 
*	The function to set the font name, color and size for subtitle text.
* Parameters:
*	fontName: [IN] the font name for subtitle text, null or empty string indicates default font chosen by underlying decoder.
*	fontColor: [IN] the font color for subtitle text, in 0xRRGGBB format. 
*	fontSize: [IN] the font size for subtitle text, negative value indicates default font size intelligently decided by underlying decoder.
* Return:
*	S_OK: successful, no error.
*	Positive value: uncritical error.
*	Negative value: critical error.
* Remarks:
*
*/
function SetExtSubtitleFont(fontName, fontColor, fontSize) {
    return ATMPlayer.SetExtSubtitleFont(fontName, fontColor, fontSize);
}

/**
* Description: 
*	The function to set text opaque of external subtitle.
* Parameters:
*	textOpaque: [IN] text opaque of external subtitle, range from 0 to 255. 0 is completely transparent, 255 is completely opaque.
* Return:
*	S_OK: successful, no error.
*	Positive value: uncritical error.
*	Negative value: critical error.
* Remarks:
*
*/
function SetExtSubtitleOpaque(textOpaque) {
    return ATMPlayer.SetExtSubtitleOpaque(textOpaque);
}

/**
* Description: 
*	The function to set delay time in order to tune display time of external subtitle.
* Parameters:
*	delayTime: [IN] the delay time(milliseconds) to display subtitle text, used to tune display time. Positive value for delay, negative value for advance.
* Return:
*	S_OK: successful, no error.
*	Positive value: uncritical error.
*	Negative value: critical error.
* Remarks:
*
*/
function SetExtSubtitleDelayTime(delayTime) {
    return ATMPlayer.SetExtSubtitleDelayTime(delayTime);
}

/**
* Description: 
*	The function to set the English font name and size, Chinese font name and size for subtitle text.
* Parameters:
*	fontNameForEnglish: [IN] the subtitle font name for ASCII character(such as English).
*	fontSizeForEnglish: [IN] the subtitle font size for ASCII character(such as English).
*	fontNameForChinese: [IN] the subtitle font name for double-byte character(such as Chinese, Japanese).
*	fontSizeForChinese: [IN] the subtitle font size for double-byte character(such as Chinese, Japanese).
* Return:
*	S_OK: successful, no error.
*	Positive value: uncritical error.
*	Negative value: critical error.
* Remarks:
*
*/
function SetExtSubtitleFontInfo(fontNameForEnglish, fontSizeForEnglish, fontNameForChinese, fontSizeForChinese) {
    return ATMPlayer.SetExtSubtitleFontInfo(fontNameForEnglish, fontSizeForEnglish, fontNameForChinese, fontSizeForChinese);
}

/**
* Description: 
*	The function to set the position and size of subtitle text box.
* Parameters:
*	left: [IN] the left position of subtitle text box.
*	top: [IN] the top position of subtitle text box.
*	width: [IN] the width of subtitle text box.
*	height: [IN] the height of subtitle text box.
* Return:
*	S_OK: successful, no error.
*	Positive value: uncritical error.
*	Negative value: critical error.
* Remarks:
*
*/
function SetExtSubtitleBoxInfo(left, top, width, height) {
    return ATMPlayer.SetExtSubtitleBoxInfo(left, top, width, height);
}

/**
* Description: 
*	The function to set transcode output video size.
* Parameters:
*	width: [IN] the width of transcode output video
*	height: [IN] the height of transcode output video
*	displayAspectX: [IN] the display x aspect of transcode output video
*	displayAspectY: [IN] the display y aspect of transcode output video
* Return:
*	S_OK: successful, no error.
*	Positive value: uncritical error.
*	Negative value: critical error.
* Remarks:
*
*/
function SetTranscodeOutVideoSize(width, height, displayAspectX, displayAspectY) {
    return ATMPlayer.SetTranscodeOutVideoSize(width, height, displayAspectX, displayAspectY);
}

/**
* Description: 
*	The function to add title for composing DVD/BD content.
* Parameters:
*	titleId: [IN] the id of title you want to add.
*	videoId: [IN] the id of video you want to select.
*	audioId: [IN] the id of audio you want to select.
*	subtitleId: [IN] the id of subtitle you want to select.
* Return:
*	S_OK: successful, no error.
*	Positive value: uncritical error.
*	Negative value: critical error.
* Remarks:
*   Invoke this API before OpenMedia.
*
*/
function AddComposeTitle(titleId, videoId, audioId, subtitleId) {
    return ATMPlayer.AddComposeTitle(titleId, videoId, audioId, subtitleId);
}

/**
* Description: 
*	The function to remove all or some title for composing DVD/BD content.
* Parameters:
*	titleId: [IN] the id of title you want to remove, -1 indicates you want to remove all titles.
* Return:
*	S_OK: successful, no error.
*	Positive value: uncritical error.
*	Negative value: critical error.
* Remarks:
*   If you don't want composing DVD/BD content, please invoke this API to remove all titles before OpenMedia.
*
*/
function RemoveComposeTitle(titleId) {
    return ATMPlayer.RemoveComposeTitle(titleId);
}

/**
* Description: 
*	The function to add/remove inserted clip.
* Parameters:
*   beginTime: [IN] the begin time position to insert clip in main video, milliseconds.
*   endTime: [IN] the end time position to insert clip in main video, milliseconds.
*   strClipPath: the URL for clip source.
*   offsetTime: the offset time from the beginning of clip source, milliseconds.
* Return:
*	S_OK: successful, no error.
*	Positive value: uncritical error.
*	Negative value: critical error.
* Remarks:
*   1. To remove the clip with beginTime position, please make endTime equal to beginTime.
*   2. To remove all the clips, please pass -1 with beginTime parameter.
*
*/
function AddInsertClip(beginTime, endTime, strClipPath, offsetTime) {
    return ATMPlayer.AddInsertClip(beginTime, endTime, strClipPath, offsetTime);
}

/**
* Description: 
*	The function to edit(add,remove,modify) watch point.
* Parameters:
*   opCode: [IN] operation code, its values as following:
*       1: To add a watch point. If this watch point has existed, then replace its tipText with new tipText.
*       2: To remove the watch point with timePos, tipText parameter is useless.
*       3: To remove all watch points, timePos and tipText parameters are useless.
*	timePos: [IN] the time position of watch point, milliseconds.
*   tipText: [IN] the tip text of watch point, empty string indicates no tip text.
*   tipImagePath: [IN] the path or http url of tip image, empty string indicates no tip image.
*   tipImageDisplayWidth: [IN] the display width of tip image. 
*       0 indicates using the native width of image, or auto-scaling according to tipImageDisplayHeight if tipImageDisplayHeight > 0.
*   tipImageDisplayHeight: [IN] the display height of tip image. 
*       0 indicates using the native height of image, or auto-scaling according to tipImageDisplayWidth if tipImageDisplayWidth > 0.
* Return:
*	S_OK: successful, no error.
*	Positive value: uncritical error.
*	Negative value: critical error.
* Remarks:
*
*/
function EditWatchPoint(opCode, timePos, tipText, tipImagePath, tipImageDisplayWidth, tipImageDisplayHeight) {
    return ATMPlayer.EditWatchPoint(opCode, timePos, tipText, tipImagePath, tipImageDisplayWidth, tipImageDisplayHeight);
}

/**
* Description: 
*	The function to get the last error code.
* Parameters:
* Return:
*	S_OK: successful, no error.
*	Positive value: uncritical error.
*	Negative value: critical error.
* Remarks:
*
*/
function GetErrorCode() {
    return ATMPlayer.GetErrorCode();
}
