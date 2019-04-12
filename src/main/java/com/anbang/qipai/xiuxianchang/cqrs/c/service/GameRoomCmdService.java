package com.anbang.qipai.xiuxianchang.cqrs.c.service;

import com.anbang.qipai.xiuxianchang.plan.bean.Game;

public interface GameRoomCmdService {

	String createGame(String gameId, String memberId, Game game, Integer renshu, Long createTime) throws Exception;

	String joinGame(String gameId, String memberId) throws Exception;

	String leaveGameRoom(String gameId, String memberId) throws Exception;

	String removeGameRoom(String gameId) throws Exception;
}
