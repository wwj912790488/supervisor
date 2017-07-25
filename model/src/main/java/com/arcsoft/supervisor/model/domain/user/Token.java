
package com.arcsoft.supervisor.model.domain.user;

import com.arcsoft.supervisor.commons.profile.Production;
import com.arcsoft.supervisor.commons.profile.Sartf;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.Date;


/**
 * Entity class of <tt>Token</tt>.
 *
 * @author jt.
 */
@Entity
@Table(name="token")
@DynamicUpdate
@Production
public class Token
{
	@Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "create_time")
    private long create_time;
    
    @Column(name = "name")
    private String name;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    private User user;

	@Column(name = "login_count")
    private Integer count;
    
    @Transient
    private Date createDatetime;
    
    public Date getCreateDatetime() {
        return this.create_time > 0 ? new Date(this.create_time) : null;
    }
    
    public Token() {}
      
    public Token(String name, User user, long createtime) {
    	this.name = name;
    	this.user = user;
    	this.create_time = createtime;
    	this.count = 1;
    }
    
    /**
     * @return Returns the id.
     */
    public Integer getId()
    {
        return id;
    }

    /**
     * @param id The id to set.
     */
    public void setId(Integer id)
    {
        this.id = id;
    }

    /**
     * @return Returns the create_time.
     */
    public long getCreate_time()
    {
        return create_time;
    }

    /**
     * @param create_time The create_time to set.
     */
    public void setCreate_time(long create_time)
    {
        this.create_time = create_time;
    }

    /**
     * @return Returns the name.
     */
    public String getName()
    {
        return name;
    }

    /**
     * @param name The name to set.
     */
    public void setName(String name)
    {
        this.name = name;
    }

    public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

    public void setCreateDatetime(Date createDatetime) {
		this.createDatetime = createDatetime;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

}
