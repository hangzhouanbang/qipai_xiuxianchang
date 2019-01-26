package com.anbang.qipai.xiuxianchang.plan.dao;

import java.util.List;

import com.anbang.qipai.xiuxianchang.plan.bean.Game;
import com.anbang.qipai.xiuxianchang.plan.bean.GameServer;

public interface GameServerDao {

	void save(GameServer gameServer);

	void remove(String[] ids);

	List<GameServer> findByGame(Game game);
}
