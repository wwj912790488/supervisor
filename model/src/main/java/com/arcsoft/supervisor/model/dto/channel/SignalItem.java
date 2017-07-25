package com.arcsoft.supervisor.model.dto.channel;


/**
 *
 * The {@code SignalItem} contains some configurations used for signal detect.
 * <p>This object can be used for {@code warning} and {@code switch}.</p>
 *
 * @author zw.
 */
public class SignalItem {
    /**
     * The value of type for Signal detect.The definition see below:
     * <ul>
     *     <li>0: SIGNAL_BROKEN</li>
     *     <li>1: PAT LOSS</li>
     *     <li>2: PROGID_LOSS</li>
     *     <li>3: PMT_LOSS</li>
     *     <li>4: VIDEO_LOSS</li>
     *     <li>5: AUDIO_LOSS</li>
     *     <li>6: CC_ERROR</li>
     * </ul>
     */
    private int type;

    private boolean checked;

    private int millisecondsOfTimeout;

    /**
     * The para used for currently type.The value is 0 in most case.
     */
    private int para = 0;

    public int getType() {
        return type;
    }

    public boolean isChecked() {
        return checked;
    }

    /**
     * Returns the integer value accord with {@link #checked}.
     *
     * @return the integer value accord with {@link #checked}.
     */
    public int getValueOfChecked() {
        return checked ? 1 : 0;
    }

    public int getMillisecondsOfTimeout() {
        return millisecondsOfTimeout;
    }

    public int getPara() {
        return para;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(SignalItem signalItem){
        return new Builder(signalItem);
    }

    static class Builder implements org.apache.commons.lang3.builder.Builder<SignalItem> {

        private final SignalItem signalItem;

        public Builder() {
            this.signalItem = new SignalItem();
        }

        public Builder(SignalItem signalItem) {
            this();
            type(signalItem.type);
            checked(signalItem.checked);
            millisecondsOfTimeout(signalItem.millisecondsOfTimeout);
            para(signalItem.para);
        }

        public Builder type(int type) {
            signalItem.type = type;
            return this;
        }

        public Builder checked(boolean checked) {
            signalItem.checked = checked;
            return this;
        }

        public Builder millisecondsOfTimeout(int millisecondsOfTimeout) {
            signalItem.millisecondsOfTimeout = millisecondsOfTimeout;
            return this;
        }

        public Builder para(int para) {
            if (signalItem.para != para) {
                signalItem.para = para;
            }
            return this;
        }

        @Override
        public SignalItem build() {
            return signalItem;
        }
    }

}
