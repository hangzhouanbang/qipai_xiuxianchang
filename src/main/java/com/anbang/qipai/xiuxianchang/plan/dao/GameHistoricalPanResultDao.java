package com.anbang.qipai.xiuxianchang.plan.dao;

import java.util.List;

import com.anbang.qipai.xiuxianchang.plan.bean.Game;
import com.anbang.qipai.xiuxianchang.plan.bean.historicalresult.GameHistoricalPanResult;

public interface GameHistoricalPanResultDao {

	void addGameHistoricalResult(GameHistoricalPanResult result);

	List<GameHistoricalPanResult> findGameHistoricalResultByGameIdAndGame(int page, int size, String gameId, Game game);

	long getAmountByGameIdAndGame(String gameId, Game game);
}
