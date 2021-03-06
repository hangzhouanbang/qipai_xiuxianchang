package com.anbang.qipai.xiuxianchang.msg.receiver;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;

import com.anbang.qipai.xiuxianchang.cqrs.c.service.GameRoomCmdService;
import com.anbang.qipai.xiuxianchang.cqrs.c.service.MemberGoldCmdService;
import com.anbang.qipai.xiuxianchang.cqrs.q.service.MemberGoldQueryService;
import com.anbang.qipai.xiuxianchang.msg.channel.sink.ChayuanShuangkouGameSink;
import com.anbang.qipai.xiuxianchang.msg.msjobs.CommonMO;
import com.anbang.qipai.xiuxianchang.plan.bean.Game;
import com.anbang.qipai.xiuxianchang.plan.bean.GameRoom;
import com.anbang.qipai.xiuxianchang.plan.bean.MemberGameRoom;
import com.anbang.qipai.xiuxianchang.plan.service.GameService;
import com.dml.accounting.AccountingRecord;
import com.google.gson.Gson;

@EnableBinding(ChayuanShuangkouGameSink.class)
public class ChayuanShuangkouGameMsgReceiver {

	@Autowired
	private GameService gameService;

	@Autowired
	private GameRoomCmdService gameRoomCmdService;

	@Autowired
	private MemberGoldCmdService memberGoldCmdService;

	@Autowired
	private MemberGoldQueryService memberGoldQueryService;

	private Gson gson = new Gson();

	@StreamListener(ChayuanShuangkouGameSink.CHAYUANSHUANGKOUGAME)
	public void receive(CommonMO mo) {
		String msg = mo.getMsg();
		if ("playerQuit".equals(msg)) {// 有人退出游戏
			Map data = (Map) mo.getData();
			String gameId = (String) data.get("gameId");
			String playerId = (String) data.get("playerId");
			GameRoom room = gameService.findGameRoomByGame(Game.chayuanShuangkou, gameId);
			if (room != null) {
				try {
					gameRoomCmdService.leaveGameRoom(gameId, playerId);
					gameService.leaveGameRoom(Game.chayuanShuangkou, gameId, playerId);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		if ("ju canceled".equals(msg)) {// 取消游戏
			Map data = (Map) mo.getData();
			String gameId = (String) data.get("gameId");
			String playerId = (String) data.get("playerId");
			long leaveTime = (Long) data.get("leaveTime");
			List<MemberGameRoom> memberRooms = gameService.findMemberGameRoomByGame(Game.chayuanShuangkou, gameId);
			memberRooms.forEach((memberRoom) -> {
				if (playerId.equals(memberRoom.getMemberId())) {
					try {
						AccountingRecord accountingRecord = memberGoldCmdService.withdraw(memberRoom.getMemberId(), 60,
								"xiuxianchang game cancle", leaveTime);
						memberGoldQueryService.withdraw(memberRoom.getMemberId(), accountingRecord);
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					try {
						AccountingRecord accountingRecord = memberGoldCmdService
								.giveGoldToMember(memberRoom.getMemberId(), 20, "xiuxianchang game cancle", leaveTime);
						memberGoldQueryService.withdraw(memberRoom.getMemberId(), accountingRecord);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			gameService.updateGameRoomFinishedByGame(Game.chayuanShuangkou, gameId, true);
			gameService.finishMemberGameRoom(Game.chayuanShuangkou, gameId);
		}
		if ("ju finished".equals(msg)) {// 一局游戏结束
			Map data = (Map) mo.getData();
			String gameId = (String) data.get("gameId");
			gameService.updateGameRoomFinishedByGame(Game.chayuanShuangkou, gameId, true);
			gameService.finishMemberGameRoom(Game.chayuanShuangkou, gameId);
		}
		if ("game delay".equals(msg)) {// 游戏延时
			Map data = (Map) mo.getData();
			String gameId = (String) data.get("gameId");
			GameRoom gameRoom = gameService.findGameRoomByGame(Game.chayuanShuangkou, gameId);
			// 延时1小时
			gameService.delayGameRoom(Game.chayuanShuangkou, gameId, gameRoom.getDeadlineTime() + 1 * 60 * 60 * 1000);
		}
	}
}
