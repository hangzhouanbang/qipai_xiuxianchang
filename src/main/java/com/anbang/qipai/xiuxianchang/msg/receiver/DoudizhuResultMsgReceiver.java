package com.anbang.qipai.xiuxianchang.msg.receiver;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;

import com.anbang.qipai.xiuxianchang.cqrs.c.service.MemberGoldCmdService;
import com.anbang.qipai.xiuxianchang.cqrs.q.service.MemberGoldQueryService;
import com.anbang.qipai.xiuxianchang.msg.channel.sink.DoudizhuResultSink;
import com.anbang.qipai.xiuxianchang.msg.msjobs.CommonMO;
import com.anbang.qipai.xiuxianchang.plan.bean.Game;
import com.anbang.qipai.xiuxianchang.plan.bean.GameRoom;
import com.anbang.qipai.xiuxianchang.plan.bean.historicalresult.GameHistoricalJuResult;
import com.anbang.qipai.xiuxianchang.plan.bean.historicalresult.GameHistoricalPanResult;
import com.anbang.qipai.xiuxianchang.plan.bean.historicalresult.GameJuPlayerResult;
import com.anbang.qipai.xiuxianchang.plan.bean.historicalresult.GamePanPlayerResult;
import com.anbang.qipai.xiuxianchang.plan.bean.historicalresult.majiang.DianpaoMajiangJuPlayerResult;
import com.anbang.qipai.xiuxianchang.plan.bean.historicalresult.puke.DoudizhuJuPlayerResult;
import com.anbang.qipai.xiuxianchang.plan.bean.historicalresult.puke.DoudizhuPanPlayerResult;
import com.anbang.qipai.xiuxianchang.plan.service.GameHistoricalJuResultService;
import com.anbang.qipai.xiuxianchang.plan.service.GameHistoricalPanResultService;
import com.anbang.qipai.xiuxianchang.plan.service.GameService;
import com.dml.accounting.AccountingRecord;
import com.google.gson.Gson;

@EnableBinding(DoudizhuResultSink.class)
public class DoudizhuResultMsgReceiver {

	@Autowired
	private GameHistoricalJuResultService majiangHistoricalResultService;

	@Autowired
	private GameHistoricalPanResultService majiangHistoricalPanResultService;

	@Autowired
	private MemberGoldCmdService memberGoldCmdService;

	@Autowired
	private MemberGoldQueryService memberGoldQueryService;

	@Autowired
	private GameService gameService;

	private Gson gson = new Gson();

	@StreamListener(DoudizhuResultSink.DOUDIZHURESULT)
	public void recordMajiangHistoricalResult(CommonMO mo) {
		String msg = mo.getMsg();
		String json = gson.toJson(mo.getData());
		Map map = gson.fromJson(json, Map.class);
		if ("doudizhu ju result".equals(msg)) {
			Object gid = map.get("gameId");
			Object dyjId = map.get("dayingjiaId");
			Object dthId = map.get("datuhaoId");
			if (gid != null && dyjId != null && dthId != null) {
				String gameId = (String) gid;
				GameRoom room = gameService.findGameRoomByGame(Game.doudizhu, gameId);
				if (room != null) {
					GameHistoricalJuResult majiangHistoricalResult = new GameHistoricalJuResult();
					majiangHistoricalResult.setGameId(gameId);
					majiangHistoricalResult.setGame(Game.doudizhu);
					majiangHistoricalResult.setDayingjiaId((String) dyjId);
					majiangHistoricalResult.setDatuhaoId((String) dthId);

					Object playerList = map.get("playerResultList");
					if (playerList != null) {
						List<GameJuPlayerResult> juPlayerResultList = new ArrayList<>();
						((List) playerList).forEach((juPlayerResult) -> juPlayerResultList
								.add(new DoudizhuJuPlayerResult((Map) juPlayerResult)));
						majiangHistoricalResult.setPlayerResultList(juPlayerResultList);

						majiangHistoricalResult.setPanshu(((Double) map.get("panshu")).intValue());
						majiangHistoricalResult.setLastPanNo(((Double) map.get("lastPanNo")).intValue());
						majiangHistoricalResult.setFinishTime(((Double) map.get("finishTime")).longValue());

						majiangHistoricalResultService.addGameHistoricalResult(majiangHistoricalResult);

						juPlayerResultList.forEach((playerResult) -> {
							DianpaoMajiangJuPlayerResult pr = (DianpaoMajiangJuPlayerResult) playerResult;
							try {
								AccountingRecord accountingRecord = memberGoldCmdService.giveGoldToMember(
										pr.getPlayerId(), pr.getTotalScore(), "xiuxianchang ju result",
										majiangHistoricalResult.getFinishTime());
								memberGoldQueryService.withdraw(pr.getPlayerId(), accountingRecord);
							} catch (Exception e) {
								e.printStackTrace();
							}
						});
					}
				}
			}
		}
		if ("doudizhu pan result".equals(msg)) {
			Object gid = map.get("gameId");
			if (gid != null) {
				String gameId = (String) gid;
				GameRoom room = gameService.findGameRoomByGame(Game.doudizhu, gameId);
				if (room != null) {
					GameHistoricalPanResult majiangHistoricalResult = new GameHistoricalPanResult();
					majiangHistoricalResult.setGameId(gameId);
					majiangHistoricalResult.setGame(Game.doudizhu);

					Object playerList = map.get("playerResultList");
					if (playerList != null) {
						List<GamePanPlayerResult> panPlayerResultList = new ArrayList<>();
						((List) map.get("playerResultList")).forEach((panPlayerResult) -> panPlayerResultList
								.add(new DoudizhuPanPlayerResult((Map) panPlayerResult)));
						majiangHistoricalResult.setPlayerResultList(panPlayerResultList);

						majiangHistoricalResult.setNo(((Double) map.get("no")).intValue());
						majiangHistoricalResult.setFinishTime(((Double) map.get("finishTime")).longValue());

						majiangHistoricalPanResultService.addGameHistoricalResult(majiangHistoricalResult);
					}
				}
			}
		}
	}
}
