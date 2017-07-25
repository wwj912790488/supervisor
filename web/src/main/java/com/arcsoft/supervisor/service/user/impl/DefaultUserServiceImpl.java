package com.arcsoft.supervisor.service.user.impl;

import com.arcsoft.supervisor.commons.spring.event.EventReceiver;
import com.arcsoft.supervisor.commons.profile.Production;
import com.arcsoft.supervisor.exception.user.TokenExpireException;
import com.arcsoft.supervisor.exception.user.TokenNotExistException;
import com.arcsoft.supervisor.model.domain.user.Token;
import com.arcsoft.supervisor.model.domain.user.User;
import com.arcsoft.supervisor.repository.user.UserRepository;
import com.arcsoft.supervisor.repository.user.TokenRepository;
import com.arcsoft.supervisor.service.user.ProductionUserService;
import com.arcsoft.supervisor.service.user.event.UserPhoneNumberUpdatedEvent;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A {@link com.arcsoft.supervisor.service.user.UserService} implementation.
 *
 * @author zw.
 */
@Service
@Production
public class DefaultUserServiceImpl extends AbstractUserServiceSupport<User> implements ProductionUserService{

    private final ConcurrentHashMap<Integer, String> userPhoneNumbers;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    protected DefaultUserServiceImpl(UserRepository userRepository) {
        super(userRepository);
        this.userPhoneNumbers = new ConcurrentHashMap<>();
        List<User> containPhoneNumberUsers = userRepository.findByPhoneNumberNotNull();
        for (User user : containPhoneNumberUsers) {
            this.userPhoneNumbers.put(user.getId(), user.getPhoneNumber());
        }
    }

    @Override
    public User newInstance(String userName, String password, Integer role) {
        return new User(userName, password, role);
    }

    @Override
    public void updateUserPhoneNumber(int id, String phoneNumber) {
        User user = getUserRepository().findOne(id);
        if (user != null &&
                !StringUtils.defaultString(phoneNumber).equals(StringUtils.defaultString(user.getPhoneNumber()))) {
            user.setPhoneNumber(phoneNumber);
            getEventManager().submit(new UserPhoneNumberUpdatedEvent(user.getId(), phoneNumber));
        }
    }

    @Override
    public List<String> getAllPhoneNumbers() {
        return new ArrayList<>(userPhoneNumbers.values());
    }

    @Override
    public User findUser(int id) {
        return getUserRepository().findOne(id);
    }

    @EventReceiver(value = UserPhoneNumberUpdatedEvent.class)
    public void onUserPhoneNumberUpdated(UserPhoneNumberUpdatedEvent event) {
        if (StringUtils.isBlank(event.getPhoneNumber())) {
            userPhoneNumbers.remove(event.getUserId());
        } else {
            userPhoneNumbers.put(event.getUserId(), event.getPhoneNumber());
        }
    }
    
    @Override
    @Transactional
    public User login(String userName, String md5Password, boolean generateToken) {

    	User user = userRepository.findByUserNameAndPassword(userName, md5Password);
    	
		if (generateToken && user != null) {
			// Generate Token
			Token token;
			token = tokenRepository.findByUserId(user.getId());
			if (token != null) {
				Integer login_count = token.getCount();
				if(login_count == null || login_count == 0) {
					String tokenStr = TokenKit.getInstance().generateTokenString(
							user.getId());
					token.setName(tokenStr);
				}
				token.setCount(login_count == null ? 1 : login_count + 1);
				user.setToken(token.getName());
			} else {
				String tokenStr = TokenKit.getInstance().generateTokenString(
						user.getId());
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date date;
				try {
					date = df.parse(df.format(new Date()));
				} catch (Exception e) {
					date = new Date();
				}

				token = new Token(tokenStr, user, date.getTime());
				tokenRepository.save(token);
                user.setToken(token.getName());

			}
		}
    	  
        return user;
    }
    
    @Override 
    public Integer getIdByToken(String tokenstr)
    {
    	//check the token DB
    	Token token = tokenRepository.findByName(tokenstr);
    	if(token == null)
    		throw new TokenNotExistException();

    	return token.getUser().getId();
    }

    @Override
    public User getUserByToken(String tokenstr)
    {
        //check the token DB
        Token token = tokenRepository.findByName(tokenstr);
        if(token == null)
            throw new TokenNotExistException();

        if(token == null)
            throw new TokenNotExistException();

        Date date = new Date();
        if((date.getTime() - token.getCreate_time()) > EXPIRE_TIME)
        {
            tokenRepository.delete(token.getId());
            throw new TokenExpireException();
        }

        return token.getUser();
    }
    
    @Override
    public void logout(Integer userId) 
    {
        try
        {
            Token token = tokenRepository.findByUserId(userId);

          /*  Integer login_count = token.getCount();
            if(login_count == null || login_count == 0) {
                token.setCount(0);
            } else {
                token.setCount(login_count - 1);
            }

            tokenRepository.save(token);
            */

            Integer login_count = token.getCount();
            if(login_count==null || login_count <= 0)
            {
                token.setCount(0);
                login_count = Integer.valueOf(0);
            }
            else
            {
                token.setCount(--login_count);
            }

            if(login_count <= 0)
            {
                tokenRepository.delete(token.getId());
            }
            else
            {
                tokenRepository.save(token);
            }
        }
        catch (Exception e)
        {
            logger.info(e.getMessage(),e);
        }
    }
}
