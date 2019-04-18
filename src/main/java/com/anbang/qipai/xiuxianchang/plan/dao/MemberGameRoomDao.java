package com.anbang.qipai.xiuxianchang.plan.dao;

import java.util.List;

import com.anbang.qipai.xiuxianchang.plan.bean.Game;
import com.anbang.qipai.xiuxianchang.plan.bean.MemberGameRoom;

public interface MemberGameRoomDao {

	void save(MemberGameRoom memberGameRoom);

	void remove(Game game, String serverGameId, String memberId);

	void remove(Game game, String serverGameId);

	MemberGameRoom findByGameAndMemberId(Game game, String gameId, String memberId);

	List<MemberGameRoom> findByGame(Game game, String gameId);

	List<MemberGameRoom> find(int page, int size);

}
