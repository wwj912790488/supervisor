package com.arcsoft.supervisor.repository.channel;

import com.arcsoft.supervisor.model.domain.channel.*;
import com.mysema.query.jpa.impl.JPAQuery;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * A jpa repository implementation of channel.
 *
 * @author zw.
 */
public class ChannelRepositoryImpl implements JpaChannelRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Channel getChannelWithoutLazy(int id) {
        QChannel channel = QChannel.channel;
        QChannelMobileConfig mobileConfig = QChannelMobileConfig.channelMobileConfig;
        QChannelContentDetectConfig contentDetectConfig = QChannelContentDetectConfig.channelContentDetectConfig;
        QChannelSignalDetectConfig signalDetectConfig = QChannelSignalDetectConfig.channelSignalDetectConfig;
        JPAQuery query = new JPAQuery(em);
        query.from(channel)
                .leftJoin(channel.mobileConfigs, mobileConfig).fetch()
                .leftJoin(channel.contentDetectConfig, contentDetectConfig).fetch()
                .leftJoin(channel.signalDetectConfig, signalDetectConfig).fetch()
                .where(channel.id.eq(id));
        return query.uniqueResult(channel);
    }

}
