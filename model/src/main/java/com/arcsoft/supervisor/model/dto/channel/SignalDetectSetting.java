package com.arcsoft.supervisor.model.dto.channel;

import com.arcsoft.supervisor.model.domain.channel.Channel;
import com.arcsoft.supervisor.model.domain.channel.ChannelSignalDetectTypeConfig;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * A {@code SignalDetectSetting} holds settings of signal detect.
 *
 * @author zw.
 */
public class SignalDetectSetting {

    private static int defaultWarningCcErrorTimeout = 5000;
    private static int defaultWarningCcErrorCount = 2000;

    /**
     * Mode of signal switch.The value definition see below:
     * <ul>
     * <li>0:master+backup+pad</li>
     * <li>1:master+pad</li>
     * <li>2:backup+pad</li>
     * <li>3:only pad</li>
     * </ul>
     * <b>Notes: We used 1 for now.</b>
     */
    private int switchMode = 1;

    /**
     * The milliseconds of warning period.
     */
    private int periodMilliSecondsOfWarning;

    /**
     * Items of {@link SignalItem} for switch.
     */
    private List<SignalItem> switchItems;

    /**
     * Items of {@link SignalItem} for warning.
     */
    private List<SignalItem> warningItems;

    private SignalDetectSetting() {
        this.switchItems = new ArrayList<>();
        this.warningItems = new ArrayList<>();
    }

    public int getSwitchMode() {
        return switchMode;
    }

    public int getPeriodMilliSecondsOfWarning() {
        return periodMilliSecondsOfWarning;
    }

    public List<SignalItem> getSwitchItems() {
        return switchItems;
    }

    public List<SignalItem> getWarningItems() {
        return warningItems;
    }

    public static Builder builder() {
        return new Builder();
    }


    public static class Builder implements org.apache.commons.lang3.builder.Builder<SignalDetectSetting> {

        private SignalDetectSetting signalDetectSetting;

        public Builder() {
            this.signalDetectSetting = new SignalDetectSetting();
        }


        @Override
        public SignalDetectSetting build() {
            return signalDetectSetting;
        }

        /**
         * Sets {@link SignalDetectSetting} base on channel.
         * <p><b>Notes:</b>Sets {@link #signalDetectSetting} to null if {@link Channel#enableSignalDetectByType}
         * is false</p>
         *
         * @param channel the object of channel
         * @return the instance of builder self
         */
        public Builder channel(Channel channel) {
            if (channel.getEnableSignalDetectByType()) {
                ChannelSignalDetectTypeConfig channelSignalDetectTypeConfig = channel.getSignalDetectByTypeConfig();
                signalDetectSetting.periodMilliSecondsOfWarning = channelSignalDetectTypeConfig.getNotifyInterval() * 1000;
                addSwitchAndWarningItems(channelSignalDetectTypeConfig);
            } else {
                signalDetectSetting = null;
            }
            return this;
        }

        private void addSwitchAndWarningItems(ChannelSignalDetectTypeConfig channelSignalDetectTypeConfig) {
            if (channelSignalDetectTypeConfig.getEnableWarningSignalBroken()) {
                addItems(0, channelSignalDetectTypeConfig.getWarningSignalBrokenTimeout());
            }

            if (channelSignalDetectTypeConfig.getEnableWarningProgidLoss()) {
                addItems(2, channelSignalDetectTypeConfig.getWarningProgidLossTimeout());
            }

            if (channelSignalDetectTypeConfig.getEnableWarningVideoLoss()) {
                addItems(4, channelSignalDetectTypeConfig.getWarningVideoLossTimeout());
            }

            if (channelSignalDetectTypeConfig.getEnableWarningAudioLoss()) {
                addItems(5, channelSignalDetectTypeConfig.getWarningAudioLossTimeout());
            }

            if (channelSignalDetectTypeConfig.getEnableWarningCcError()) {
                addItems(
                        6,
                        channelSignalDetectTypeConfig.getWarningCcErrorTimeout(),
                        channelSignalDetectTypeConfig.getWarningCcErrorCount()
                );
            }/* else {
                addItems(
                        6,
                        defaultWarningCcErrorTimeout,
                        defaultWarningCcErrorCount
                );
            }*/
        }

        /**
         * Adds {@code Switch} and {@code Warning} of signal item to {@link #signalDetectSetting}.
         *
         * @param type                  the type of switch or warning
         * @param millisecondsOfTimeout the timeout in milliseconds
         */
        private void addItems(int type, int millisecondsOfTimeout) {
            addItems(type, millisecondsOfTimeout, 0);
        }

        private void addItems(int type, int millisecondsOfTimeout, int para) {
            Pair<SignalItem, SignalItem> switchAndWarningItems = createSwitchAndWarningItem(
                    type,
                    millisecondsOfTimeout,
                    para
            );
            addToSwitchItems(switchAndWarningItems.getLeft());
            addToWarningItems(switchAndWarningItems.getRight());
        }

        /**
         * Creates switch item and warning item.
         *
         * @param type                  the type of switch or warning
         * @param millisecondsOfTimeout the timeout in milliseconds
         * @param para                  additional parameter for detect item accord with type
         * @return A pair of signal item.The switch item on the left and warning item on the right
         */
        private Pair<SignalItem, SignalItem> createSwitchAndWarningItem(int type, int millisecondsOfTimeout, int para) {
            //Because transcoder need switch items but we don't need it.
            //So we create switch item but don't enable detect option
            SignalItem switchItem = SignalItem.builder()
                    .type(type)
                    .checked(false)
                    .millisecondsOfTimeout(millisecondsOfTimeout)
                    .para(para)
                    .build();
            SignalItem warningItem = SignalItem.builder(switchItem)
                    .checked(true)
                    .build();
            return Pair.of(switchItem, warningItem);

        }

        private void addToSwitchItems(SignalItem switchItem) {
            signalDetectSetting.switchItems.add(switchItem);
        }

        private void addToWarningItems(SignalItem warningItem) {
            signalDetectSetting.warningItems.add(warningItem);
        }

    }
}
