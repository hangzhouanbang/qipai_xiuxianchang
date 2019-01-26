package com.anbang.qipai.xiuxianchang.plan.dao;

import java.util.List;

import com.anbang.qipai.xiuxianchang.plan.bean.Game;
import com.anbang.qipai.xiuxianchang.plan.bean.historicalresult.GameHistoricalJuResult;

public interface GameHistoricalJuResultDao {

	void addGameHistoricalResult(GameHistoricalJuResult result);

	List<GameHistoricalJuResult> findGameHistoricalResultByMemberId(int page, int size, String memberId);

	long getAmountByMemberId(String memberId);

	int countGameNumByGameAndTime(Game game, long startTime, long endTime);

	GameHistoricalJuResult findGameHistoricalResultById(String id);
}
