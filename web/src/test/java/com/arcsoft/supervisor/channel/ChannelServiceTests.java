package com.arcsoft.supervisor.channel;

import com.arcsoft.supervisor.ProductionTestSupport;
import com.arcsoft.supervisor.model.domain.channel.Channel;
import com.arcsoft.supervisor.service.channel.ChannelGroupService;
import com.arcsoft.supervisor.service.channel.ChannelService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

/**
 * A tests for {@code ChannelService}.
 *
 * @author zw.
 */
public class ChannelServiceTests extends ProductionTestSupport {

    @Autowired
    private ChannelService channelService;
    @Autowired
    private ChannelGroupService groupService;


    @Test
    @Transactional
    @Rollback(false)
    public void tests(){
        for (int i = 17; i < 37; i++){
            Channel channel = new Channel();
            channel.setName("测试频道-" + i);
            channel.setIp("127.0.0.1");
            channel.setPort(9090);
            channel.setProtocol("udp");
            channel.setIsSupportMobile(false);
            channel.setMaxPersistDays((byte)2);
            channel.setGroup(groupService.getById(1));
            channelService.save(channel);
        }
    }
    @Test
    public void testFindByGroupId(){
        channelService.getByGroupId(1);
    }
}
