package com.arcsoft.supervisor.service.message.impl;

import com.arcsoft.supervisor.commons.spring.event.EventReceiver;
import com.arcsoft.supervisor.model.domain.message.Message;
import com.arcsoft.supervisor.model.domain.task.Task;
import com.arcsoft.supervisor.repository.message.MessageRepository;
import com.arcsoft.supervisor.service.ServiceSupport;
import com.arcsoft.supervisor.service.TransactionSupport;
import com.arcsoft.supervisor.service.message.MessageService;
import com.arcsoft.supervisor.service.message.event.AddMessageEvent;
import com.arcsoft.supervisor.service.task.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * A {@link com.arcsoft.supervisor.service.message.MessageService} implementation.
 *
 * @author jt.
 * @author zw
 */
@Service
public class DefaultMessageServiceImpl extends ServiceSupport implements MessageService, TransactionSupport {

    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private TaskService taskService;
    
    protected static final int NUMBER_OF_MESSAGES_PER_PAGE = 10;

    @Override
    public Message add(String userName, String message) {
 
        Message newmsg = new Message(userName, message);
        messageRepository.save(newmsg);
        getEventManager().submit(new AddMessageEvent(newmsg));
        return newmsg;
    }
    
    @Override
    public Message add(String userName, String realName, String message, String dateTime, String ipAddress) {
 
        Message newmsg = new Message(userName, realName, message, dateTime, ipAddress);
        messageRepository.save(newmsg);
        getEventManager().submit(new AddMessageEvent(newmsg));
        return newmsg;
    }


    @Override
    public void clearMessage() {
        sendDisplayMessageRequest(null);
    }

    @Override
    public void delete(Message message) {
        messageRepository.delete(message);
    }

    @Override
    public void deleteById(Integer id) {
        try{
        	messageRepository.delete(id);
        }catch (EmptyResultDataAccessException e){
            //Ignore this exception if the entity not exists.
        }

    }
    
    @Override
    public void deleteByIds(List<Message> msgs) {
    	messageRepository.deleteInBatch(msgs);
    }


    public Integer getCount() {
    	return (int)messageRepository.count();
    }
       

    @Override
    public Page<Message> paginate(PageRequest pageRequest) {
        return messageRepository.findAll(pageRequest);
    }


    @EventReceiver(value = AddMessageEvent.class)
    public void processMessageAddEvent(AddMessageEvent event){
        sendDisplayMessageRequest(event.getMessage().getMessage());
    }

    private void sendDisplayMessageRequest(String message){
        List<Task> tasks = taskService.getRunningComposeTasks();
        for (Task task : tasks){
            try{
                taskService.displayMessageOnComposeTask(task.getId(), message);
            }catch (Exception e){
                logger.error("Failed to display message on task [id={}]", task.getId());
            }
        }
    }
}
