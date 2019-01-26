package com.anbang.qipai.xiuxianchang.plan.dao;

import com.anbang.qipai.xiuxianchang.plan.bean.Game;
import com.anbang.qipai.xiuxianchang.plan.bean.MemberGameRoom;

public interface MemberGameRoomDao {

	void save(MemberGameRoom memberGameRoom);

	void remove(Game game, String serverGameId, String memberId);

	void remove(Game game, String serverGameId);

}
