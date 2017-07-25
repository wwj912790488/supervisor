package com.arcsoft.supervisor.service.message;

import com.arcsoft.supervisor.model.domain.message.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

/**
 * Interface for handling logic of <tt>message</tt>.
 *
 * @author jt.
 */
public interface MessageService {

    /**
     * Register a message with <tt>userName</tt> and <tt>message</tt>
     *
     * @param userName the userName
     * @param message the message string
     * @return the registered message
     * @throws MessageException if the {@code message} is exists
     */
    public Message add(String userName, String message) ;
    
    public Message add(String userName, String realName, String message, String dateTime, String ipAddress) ;


    public void clearMessage();

    /**
     * Removes a message.
     *
     * @param message the message instance will be remove
     */
    public void delete(Message message);

    /**
     * Removes a message with the specify <tt>id</tt> identify.
     *
     * @param id
     */
    public void deleteById(Integer id);
    
    public void deleteByIds(List<Message> msgs);

    public Integer getCount();
	
    public Page<Message> paginate(PageRequest pageRequest);
	
}
