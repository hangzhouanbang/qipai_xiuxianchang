package com.anbang.qipai.xiuxianchang.plan.dao;

import com.anbang.qipai.xiuxianchang.plan.bean.Game;
import com.anbang.qipai.xiuxianchang.plan.bean.GameLaw;

public interface GameLawDao {

	GameLaw findByGameAndName(Game game, String name);

	void save(GameLaw law);

	void update(GameLaw law);

	void remove(String id);

}
