package com.arcsoft.supervisor.service.channel;

import com.arcsoft.supervisor.model.domain.channel.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Interface for handling logic of <tt>Channel</tt>.
 *
 * @author zw.
 */
public interface ChannelService {

    /**
     * Save a channel.
     *
     * @param channel the channel will be saved
     */
    public void save(Channel channel);

    public void save(Channel channel, ChannelInfo info);

    /**
     * Deletes the channel with id.
     *
     * @param id the identify value of channel
     */
    public void deleteById(int id);

    public void deleteByIds(List<Channel> channels);

    public Channel getById(int id);

    public void update();

    public List<Channel> listAll();

    public List<Channel> getUngrouped();

    public List<Channel> getByGroupId(int groupId);

    public void updateGroup(List<Channel> channels, int groupId);

    public Page<Channel> pagenate(int pageNo, int pageSize);

    /**
     * Retrieves paginated channel with support mobile.
     *
     * @param pageNo   currently page no
     * @param pageSize the size of each page
     * @return all of support mobile's channel
     */
    public Page<Channel> pagenateBySupportMobile(int pageNo, int pageSize);

    public Page<Channel> pagenateByGroup(int groupId, int pageNo, int pageSize);

    public Channel getByIdWithOutLazy(int id);

	public void saveAddress(Channel channel, String ip);

    public Page<Channel> paginate(ChannelQueryParams q, PageRequest pageRequest);

    public Channel getByOrigchannelid(String origchannelid);

    public Channel fromChannelDesc(NewChannelDesc desc);

    public boolean isChannelNameExists(Integer id, String name);

    public boolean isChannelExists(Integer id, String ip, Integer port, String programId, String audioId);

    public boolean isChannelOriginalidExists(Integer id,String originalchanid);

    public Channel findByIpAndPortAndProgramIdAndAudioId(String ip, Integer port, String programId, String audioId);

}
