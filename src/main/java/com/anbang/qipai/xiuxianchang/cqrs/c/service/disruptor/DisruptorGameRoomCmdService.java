package com.anbang.qipai.xiuxianchang.cqrs.c.service.disruptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.anbang.qipai.xiuxianchang.cqrs.c.service.GameRoomCmdService;
import com.anbang.qipai.xiuxianchang.cqrs.c.service.impl.GameRoomCmdServiceImpl;
import com.anbang.qipai.xiuxianchang.plan.bean.Game;
import com.highto.framework.concurrent.DeferredResult;
import com.highto.framework.ddd.CommonCommand;

@Component(value = "gameRoomCmdService")
public class DisruptorGameRoomCmdService extends DisruptorCmdServiceBase implements GameRoomCmdService {

	@Autowired
	private GameRoomCmdServiceImpl gameRoomCmdServiceImpl;

	@Override
	public String createGame(String gameId, String memberId, Game game, Integer renshu, Long createTime)
			throws Exception {
		CommonCommand cmd = new CommonCommand(GameRoomCmdServiceImpl.class.getName(), "createGame", gameId, memberId,
				game, renshu, createTime);

		DeferredResult<String> result = publishEvent(disruptorFactory.getCoreCmdDisruptor(), cmd, () -> {
			String newGameId = gameRoomCmdServiceImpl.createGame(cmd.getParameter(), cmd.getParameter(),
					cmd.getParameter(), cmd.getParameter(), cmd.getParameter());
			return newGameId;
		});
		try {
			return result.getResult();
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public String joinGame(String gameId, String memberId) throws Exception {
		CommonCommand cmd = new CommonCommand(GameRoomCmdServiceImpl.class.getName(), "joinGame", gameId, memberId);

		DeferredResult<String> result = publishEvent(disruptorFactory.getCoreCmdDisruptor(), cmd, () -> {
			String id = gameRoomCmdServiceImpl.joinGame(cmd.getParameter(), cmd.getParameter());
			return id;
		});
		try {
			return result.getResult();
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public String leaveGameRoom(String gameId, String memberId) throws Exception {
		CommonCommand cmd = new CommonCommand(GameRoomCmdServiceImpl.class.getName(), "leaveGameRoom", gameId,
				memberId);
		DeferredResult<String> result = publishEvent(disruptorFactory.getCoreCmdDisruptor(), cmd, () -> {
			String id = gameRoomCmdServiceImpl.leaveGameRoom(cmd.getParameter(), cmd.getParameter());
			return id;
		});
		try {
			return result.getResult();
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public String removeGameRoom(String gameId) throws Exception {
		CommonCommand cmd = new CommonCommand(GameRoomCmdServiceImpl.class.getName(), "removeGameRoom", gameId);
		DeferredResult<String> result = publishEvent(disruptorFactory.getCoreCmdDisruptor(), cmd, () -> {
			String id = gameRoomCmdServiceImpl.removeGameRoom(cmd.getParameter());
			return id;
		});
		try {
			return result.getResult();
		} catch (Exception e) {
			throw e;
		}
	}

}
