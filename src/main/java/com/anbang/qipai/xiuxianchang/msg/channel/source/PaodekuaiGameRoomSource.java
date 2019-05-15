package com.anbang.qipai.xiuxianchang.msg.channel.source;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface PaodekuaiGameRoomSource {
	@Output
	MessageChannel paodekuaiGameRoom();
}
