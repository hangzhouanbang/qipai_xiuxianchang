package com.anbang.qipai.xiuxianchang.msg.channel.sink;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface DoudizhuResultSink {
	String DOUDIZHURESULT = "doudizhuResult";

	@Input
	SubscribableChannel doudizhuResult();
}
