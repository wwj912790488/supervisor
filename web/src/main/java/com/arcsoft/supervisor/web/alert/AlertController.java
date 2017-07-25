package com.arcsoft.supervisor.web.alert;

import com.arcsoft.supervisor.model.domain.log.ContentDetectLog;
import com.arcsoft.supervisor.model.dto.alert.AlertMessage;
import com.arcsoft.supervisor.service.log.ContentDetectLogService;
import com.arcsoft.supervisor.web.ControllerSupport;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.math.IntMath;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Shorts;
import com.google.common.util.concurrent.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Nullable;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

@Controller
@RequestMapping("/alert")
public class AlertController extends ControllerSupport {
	
	@Autowired
	private ContentDetectLogService contentDetectLogService;

	@RequestMapping(value = "/get", method = RequestMethod.GET)
    @ResponseBody
    public AlertMessage getLastAlert(Long lastId) {
		AlertMessage message = new AlertMessage();
		ContentDetectLog log = contentDetectLogService.findLatestOne();
		if(lastId == -1 ) {
			if(log == null) {
				message.setId((long) -1);
			} else {
				message.setId(log.getId());
				message.setMessageFrom(log);
			}
			
		} else {
			if(log == null) {
				message.setId((long) -1);
			} else if(!log.getId().equals(lastId)) {
				message.setId(log.getId());
				message.setMessageFrom(log);
			} else {
				message.setId(lastId);
			}
			
		}
		return message;
	}

	public static void main(String[] args) {
		for (int i = 0; i <5 ; i++) {
			if(i==3){
				continue;
			}else {
				System.out.println(i);
			}
			System.out.println(i);
		}
		/*System.out.println("hello");
		ListeningExecutorService service= MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(3));
		ListenableFuture explosion =service.submit(new Callable<Object>() {
			int i=1;
			@Override
			public Object call() throws Exception {
				return i++;
			}
		});
		Futures.addCallback(explosion, new FutureCallback() {
			@Override
			public void onSuccess(@Nullable Object result) {
				System.out.println("scuuess:"+result);
			}

			@Override
			public void onFailure(Throwable t) {
				System.out.println("Failure:"+t.getMessage());
			}
		});
		service.shutdown();
		Joiner joiner=Joiner.on("--").skipNulls();
		System.out.println( joiner.join("Harry",null, "Ron", "Hermione"));
		System.out.println("foo,bar,,   qux".split(","));
		System.out.println(Splitter.on(",").trimResults().omitEmptyStrings().split("foo,bar,,   qux"));
		int i=10;
		Arrays.asList(i);
		System.out.println(IntMath.gcd(16,3));*/
	}
}
