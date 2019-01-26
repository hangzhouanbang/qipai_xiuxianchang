package com.anbang.qipai.xiuxianchang.msg.channel.sink;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface RuianMajiangResultSink {
	String RUIANMAJIANGRESULT = "ruianMajiangResult";

	@Input
	SubscribableChannel ruianMajiangResult();
}
