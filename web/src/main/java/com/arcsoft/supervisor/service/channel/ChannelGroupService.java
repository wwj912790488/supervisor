package com.arcsoft.supervisor.service.channel;

import com.arcsoft.supervisor.model.domain.channel.ChannelGroup;

import java.util.List;

/**
 * Interface for handling logic of <tt>ChannelGroup</tt>.
 *
 * @author zw.
 */
public interface ChannelGroupService {

    /**
     * Save a {@code group}.
     *
     * @param group the {@link ChannelGroup}
     */
    public void save(ChannelGroup group);

    /**
     * Deletes a group with {@code groupId}.
     *
     * @param groupId the identify value of group
     */
    public void delete(int groupId);

    /**
     * Retrieves all of {@link ChannelGroup}.
     *
     * @return the list of {@code ChannelGroup}
     */
    public List<ChannelGroup> listAll();

    /**
     * Updates the name of group.
     */
    public void updateGroupName(int groupId, String name);

    /**
     * Retrieves the {@link ChannelGroup} with specify {@code groupId}.
     *
     * @param groupId the identify value of group
     * @return the group
     */
    public ChannelGroup getById(int groupId);

    /**
     * Retrieves the {@link ChannelGroup} with specify {@code groupName}.
     *
     * @param groupName the name of group
     * @return the group
     */
    public ChannelGroup getByName(String groupName);
}
