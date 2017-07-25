package com.arcsoft.supervisor.repository.message;

import com.arcsoft.supervisor.model.domain.message.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

/**
 * Repository interface for <tt>Message</tt>
 *
 * @author jt.
 */
public interface MessageRepository extends JpaRepository<Message, Integer>, QueryDslPredicateExecutor<Message> {

    public Message findByUserNameAndMessage(String userName, String message);

    public Long countByUserName(String userName);

}
