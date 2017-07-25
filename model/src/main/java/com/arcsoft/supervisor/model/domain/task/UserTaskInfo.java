package com.arcsoft.supervisor.model.domain.task;

import com.arcsoft.supervisor.commons.profile.Sartf;
import com.arcsoft.supervisor.model.domain.user.SartfUser;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "user_task_info")
@Sartf
public class UserTaskInfo {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
	private SartfUser user;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "task_id")
	private Task task;
	
	private String rtspOpsFileName;
	
	private String rtspMobileFileName;
	
	private Date lastUpdate;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

    public SartfUser getUser() {
        return user;
    }

    public void setUser(SartfUser user) {
        this.user = user;
    }

    public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
	}

	public String getRtspOpsFileName() {
		return rtspOpsFileName;
	}

	public void setRtspOpsFileName(String rtspOpsFileName) {
		this.rtspOpsFileName = rtspOpsFileName;
	}

	public String getRtspMobileFileName() {
		return rtspMobileFileName;
	}

	public void setRtspMobileFileName(String rtspMobileFileName) {
		this.rtspMobileFileName = rtspMobileFileName;
	}

	public Date getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}
}
