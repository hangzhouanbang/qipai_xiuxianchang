package com.anbang.qipai.xiuxianchang.msg.receiver;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;

import com.anbang.qipai.xiuxianchang.cqrs.c.domain.game.GameRoomNotFoundException;
import com.anbang.qipai.xiuxianchang.cqrs.c.domain.member.MemberNotFoundException;
import com.anbang.qipai.xiuxianchang.cqrs.c.service.GameRoomCmdService;
import com.anbang.qipai.xiuxianchang.cqrs.c.service.MemberGoldCmdService;
import com.anbang.qipai.xiuxianchang.cqrs.q.service.MemberGoldQueryService;
import com.anbang.qipai.xiuxianchang.msg.channel.sink.DianpaoMajiangGameSink;
import com.anbang.qipai.xiuxianchang.msg.msjobs.CommonMO;
import com.anbang.qipai.xiuxianchang.plan.bean.Game;
import com.anbang.qipai.xiuxianchang.plan.bean.MemberGameRoom;
import com.anbang.qipai.xiuxianchang.plan.service.GameService;
import com.dml.accounting.AccountingRecord;
import com.dml.accounting.InsufficientBalanceException;
import com.google.gson.Gson;

@EnableBinding(DianpaoMajiangGameSink.class)
public class DianpaoMajiangGameMsgReceiver {
	@Autowired
	private GameService gameService;

	@Autowired
	private GameRoomCmdService gameRoomCmdService;

	@Autowired
	private MemberGoldCmdService memberGoldCmdService;

	@Autowired
	private MemberGoldQueryService memberGoldQueryService;

	private Gson gson = new Gson();

	@StreamListener(DianpaoMajiangGameSink.DIANPAOMAJIANGGAME)
	public void receive(CommonMO mo) {
		String msg = mo.getMsg();
		if ("playerQuit".equals(msg)) {// 有人退出游戏
			Map data = (Map) mo.getData();
			String gameId = (String) data.get("gameId");
			String playerId = (String) data.get("playerId");
			try {
				gameRoomCmdService.leaveGameRoom(gameId, playerId);
				gameService.leaveGameRoom(Game.dianpaoMajiang, gameId, playerId);
			} catch (GameRoomNotFoundException e) {
				e.printStackTrace();
			}
		}
		if ("ju canceled".equals(msg)) {// 取消游戏
			Map data = (Map) mo.getData();
			String gameId = (String) data.get("gameId");
			String playerId = (String) data.get("playerId");
			long leaveTime = (Long) data.get("leaveTime");
			List<MemberGameRoom> memberRooms = gameService.findMemberGameRoomByGame(Game.dianpaoMajiang, gameId);
			memberRooms.forEach((memberRoom) -> {
				if (playerId.equals(memberRoom.getMemberId())) {
					try {
						AccountingRecord accountingRecord = memberGoldCmdService.withdraw(memberRoom.getMemberId(),
								1500, "xiuxianchang game cancle", leaveTime);
						memberGoldQueryService.withdraw(memberRoom.getMemberId(), accountingRecord);
					} catch (MemberNotFoundException e) {
						e.printStackTrace();
					} catch (InsufficientBalanceException e) {
						e.printStackTrace();
					}
				} else {
					try {
						AccountingRecord accountingRecord = memberGoldCmdService
								.giveGoldToMember(memberRoom.getMemberId(), 500, "xiuxianchang game cancle", leaveTime);
						memberGoldQueryService.withdraw(memberRoom.getMemberId(), accountingRecord);
					} catch (MemberNotFoundException e) {
						e.printStackTrace();
					}
				}
			});
			gameService.updateGameRoomFinishedByGame(Game.dianpaoMajiang, gameId, true);
			gameService.finishMemberGameRoom(Game.dianpaoMajiang, gameId);
		}
		if ("ju finished".equals(msg)) {// 一局游戏结束
			Map data = (Map) mo.getData();
			String gameId = (String) data.get("gameId");
			gameService.updateGameRoomFinishedByGame(Game.dianpaoMajiang, gameId, true);
			gameService.finishMemberGameRoom(Game.dianpaoMajiang, gameId);
		}
	}
}
