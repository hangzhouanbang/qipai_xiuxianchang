package com.anbang.qipai.xiuxianchang.cqrs.c.service.disruptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.anbang.qipai.xiuxianchang.cqrs.c.domain.game.GameRoomHasExistAlreadyException;
import com.anbang.qipai.xiuxianchang.cqrs.c.domain.game.GameRoomNotFoundException;
import com.anbang.qipai.xiuxianchang.cqrs.c.domain.game.MemberHasJoinGameRoomException;
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
			throws GameRoomHasExistAlreadyException {
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
			if (e instanceof GameRoomHasExistAlreadyException) {
				throw (GameRoomHasExistAlreadyException) e;
			} else {
				throw new RuntimeException(e);
			}
		}
	}

	@Override
	public String joinGame(String gameId, String memberId)
			throws GameRoomNotFoundException, MemberHasJoinGameRoomException {
		CommonCommand cmd = new CommonCommand(GameRoomCmdServiceImpl.class.getName(), "joinGame", gameId, memberId);

		DeferredResult<String> result = publishEvent(disruptorFactory.getCoreCmdDisruptor(), cmd, () -> {
			String id = gameRoomCmdServiceImpl.joinGame(cmd.getParameter(), cmd.getParameter());
			return id;
		});
		try {
			return result.getResult();
		} catch (Exception e) {
			if (e instanceof GameRoomNotFoundException) {
				throw (GameRoomNotFoundException) e;
			} else if (e instanceof MemberHasJoinGameRoomException) {
				throw (MemberHasJoinGameRoomException) e;
			} else {
				throw new RuntimeException(e);
			}
		}
	}

	@Override
	public String leaveGameRoom(String gameId, String memberId) throws GameRoomNotFoundException {
		CommonCommand cmd = new CommonCommand(GameRoomCmdServiceImpl.class.getName(), "leaveGameRoom", gameId,
				memberId);
		DeferredResult<String> result = publishEvent(disruptorFactory.getCoreCmdDisruptor(), cmd, () -> {
			String id = gameRoomCmdServiceImpl.leaveGameRoom(cmd.getParameter(), cmd.getParameter());
			return id;
		});
		try {
			return result.getResult();
		} catch (Exception e) {
			if (e instanceof GameRoomNotFoundException) {
				throw (GameRoomNotFoundException) e;
			} else {
				throw new RuntimeException(e);
			}
		}
	}

	@Override
	public String removeGameRoom(String gameId) {
		CommonCommand cmd = new CommonCommand(GameRoomCmdServiceImpl.class.getName(), "removeGameRoom", gameId);
		DeferredResult<String> result = publishEvent(disruptorFactory.getCoreCmdDisruptor(), cmd, () -> {
			String id = gameRoomCmdServiceImpl.removeGameRoom(cmd.getParameter());
			return id;
		});
		try {
			return result.getResult();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
