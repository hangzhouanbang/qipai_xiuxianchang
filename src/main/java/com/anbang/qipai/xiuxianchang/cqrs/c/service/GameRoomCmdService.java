package com.anbang.qipai.xiuxianchang.cqrs.c.service;

import com.anbang.qipai.xiuxianchang.cqrs.c.domain.game.GameRoomHasExistAlreadyException;
import com.anbang.qipai.xiuxianchang.cqrs.c.domain.game.GameRoomNotFoundException;
import com.anbang.qipai.xiuxianchang.cqrs.c.domain.game.MemberHasJoinGameRoomException;
import com.anbang.qipai.xiuxianchang.plan.bean.Game;

public interface GameRoomCmdService {

	String createGame(String gameId, String memberId, Game game, Integer renshu, Long createTime)
			throws GameRoomHasExistAlreadyException;

	String joinGame(String gameId, String memberId) throws GameRoomNotFoundException, MemberHasJoinGameRoomException;

	String leaveGameRoom(String gameId, String memberId) throws GameRoomNotFoundException;

	String removeGameRoom(String gameId);
}
