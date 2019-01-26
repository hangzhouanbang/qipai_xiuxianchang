package com.anbang.qipai.xiuxianchang.msg.channel.sink;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface GameServerSink {
	String GAMESERVER = "gameServer";

	@Input
	SubscribableChannel gameServer();

}
