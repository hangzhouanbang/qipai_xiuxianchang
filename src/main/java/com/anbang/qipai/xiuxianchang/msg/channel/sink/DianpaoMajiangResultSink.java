package com.anbang.qipai.xiuxianchang.msg.channel.sink;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface DianpaoMajiangResultSink {
	String DIANPAOMAJIANGRESULT = "dianpaoMajiangResult";

	@Input
	SubscribableChannel dianpaoMajiangResult();
}
