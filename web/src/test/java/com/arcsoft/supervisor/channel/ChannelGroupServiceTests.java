package com.arcsoft.supervisor.channel;

import com.arcsoft.supervisor.ProductionTestSupport;
import com.arcsoft.supervisor.model.domain.channel.ChannelGroup;
import com.arcsoft.supervisor.service.channel.ChannelGroupService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * A tests for {@code ChannelGroupService}.
 *
 * @author zw.
 */
public class ChannelGroupServiceTests extends ProductionTestSupport {

    @Autowired
    private ChannelGroupService channelGroupService;


    @Test
    @Transactional
    public void tests(){
        ChannelGroup group = new ChannelGroup();
        group.setName("group-1");
        channelGroupService.save(group);

        List<ChannelGroup> groups = channelGroupService.listAll();
        Assert.assertNotNull(groups);

        channelGroupService.updateGroupName(group.getId(), "group-1-1");
        Assert.assertEquals(channelGroupService.getById(group.getId()).getName(), "group-1-1");
        Assert.assertEquals(channelGroupService.getByName("group-1-1").getName(), "group-1-1");

        channelGroupService.delete(group.getId());

    }

}
