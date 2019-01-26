package com.anbang.qipai.xiuxianchang.msg.receiver;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;

import com.anbang.qipai.xiuxianchang.cqrs.c.domain.member.MemberNotFoundException;
import com.anbang.qipai.xiuxianchang.cqrs.c.service.MemberGoldCmdService;
import com.anbang.qipai.xiuxianchang.cqrs.q.service.MemberGoldQueryService;
import com.anbang.qipai.xiuxianchang.msg.channel.sink.WenzhouShuangkouResultSink;
import com.anbang.qipai.xiuxianchang.msg.msjobs.CommonMO;
import com.anbang.qipai.xiuxianchang.plan.bean.Game;
import com.anbang.qipai.xiuxianchang.plan.bean.historicalresult.GameHistoricalJuResult;
import com.anbang.qipai.xiuxianchang.plan.bean.historicalresult.GameHistoricalPanResult;
import com.anbang.qipai.xiuxianchang.plan.bean.historicalresult.GameJuPlayerResult;
import com.anbang.qipai.xiuxianchang.plan.bean.historicalresult.GamePanPlayerResult;
import com.anbang.qipai.xiuxianchang.plan.bean.historicalresult.puke.WenzhouShuangkouJuPlayerResult;
import com.anbang.qipai.xiuxianchang.plan.bean.historicalresult.puke.WenzhouShuangkouPanPlayerResult;
import com.anbang.qipai.xiuxianchang.plan.service.GameHistoricalJuResultService;
import com.anbang.qipai.xiuxianchang.plan.service.GameHistoricalPanResultService;
import com.dml.accounting.AccountingRecord;
import com.google.gson.Gson;

@EnableBinding(WenzhouShuangkouResultSink.class)
public class WenzhouShuangkouResultMsgReceiver {

	@Autowired
	private GameHistoricalJuResultService gameHistoricalResultService;

	@Autowired
	private GameHistoricalPanResultService gameHistoricalPanResultService;

	@Autowired
	private MemberGoldCmdService memberGoldCmdService;

	@Autowired
	private MemberGoldQueryService memberGoldQueryService;

	private Gson gson = new Gson();

	@StreamListener(WenzhouShuangkouResultSink.WENZHOUSHUANGKOURESULT)
	public void recordMajiangHistoricalResult(CommonMO mo) {
		String msg = mo.getMsg();
		String json = gson.toJson(mo.getData());
		Map map = gson.fromJson(json, Map.class);
		if ("wenzhoushuangkou ju result".equals(msg)) {
			Object gid = map.get("gameId");
			Object dyjId = map.get("dayingjiaId");
			Object dthId = map.get("datuhaoId");
			if (gid != null && dyjId != null && dthId != null) {
				String gameId = (String) gid;
				GameHistoricalJuResult pukeHistoricalResult = new GameHistoricalJuResult();
				pukeHistoricalResult.setGameId(gameId);
				pukeHistoricalResult.setGame(Game.wenzhouShuangkou);
				pukeHistoricalResult.setDayingjiaId((String) dyjId);
				pukeHistoricalResult.setDatuhaoId((String) dthId);

				Object playerList = map.get("playerResultList");
				if (playerList != null) {
					List<GameJuPlayerResult> juPlayerResultList = new ArrayList<>();
					((List) playerList).forEach((juPlayerResult) -> juPlayerResultList
							.add(new WenzhouShuangkouJuPlayerResult((Map) juPlayerResult)));
					pukeHistoricalResult.setPlayerResultList(juPlayerResultList);

					pukeHistoricalResult.setPanshu(((Double) map.get("panshu")).intValue());
					pukeHistoricalResult.setLastPanNo(((Double) map.get("lastPanNo")).intValue());
					pukeHistoricalResult.setFinishTime(((Double) map.get("finishTime")).longValue());

					gameHistoricalResultService.addGameHistoricalResult(pukeHistoricalResult);

					juPlayerResultList.forEach((playerResult) -> {
						WenzhouShuangkouJuPlayerResult pr = (WenzhouShuangkouJuPlayerResult) playerResult;
						try {
							AccountingRecord accountingRecord = memberGoldCmdService.giveGoldToMember(pr.getPlayerId(),
									pr.getTotalScore(), "xiuxianchang ju result", pukeHistoricalResult.getFinishTime());
							memberGoldQueryService.withdraw(pr.getPlayerId(), accountingRecord);
						} catch (MemberNotFoundException e) {
							e.printStackTrace();
						}
					});
				}
			}
		}
		if ("wenzhoushuangkou pan result".equals(msg)) {
			Object gid = map.get("gameId");
			if (gid != null) {
				String gameId = (String) gid;
				GameHistoricalPanResult pukeHistoricalResult = new GameHistoricalPanResult();
				pukeHistoricalResult.setGameId(gameId);
				pukeHistoricalResult.setGame(Game.wenzhouShuangkou);

				Object playerList = map.get("playerResultList");
				if (playerList != null) {
					List<GamePanPlayerResult> panPlayerResultList = new ArrayList<>();
					((List) playerList).forEach((panPlayerResult) -> panPlayerResultList
							.add(new WenzhouShuangkouPanPlayerResult((Map) panPlayerResult)));
					pukeHistoricalResult.setPlayerResultList(panPlayerResultList);

					pukeHistoricalResult.setNo(((Double) map.get("no")).intValue());
					pukeHistoricalResult.setFinishTime(((Double) map.get("finishTime")).longValue());

					gameHistoricalPanResultService.addGameHistoricalResult(pukeHistoricalResult);
				}
			}
		}
	}
}
