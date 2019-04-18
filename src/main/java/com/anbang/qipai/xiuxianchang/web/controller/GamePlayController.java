package com.anbang.qipai.xiuxianchang.web.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.anbang.qipai.xiuxianchang.cqrs.c.service.GameRoomCmdService;
import com.anbang.qipai.xiuxianchang.cqrs.q.dbo.MemberGoldAccountDbo;
import com.anbang.qipai.xiuxianchang.cqrs.q.service.MemberGoldQueryService;
import com.anbang.qipai.xiuxianchang.msg.service.ChayuanShuangkouGameRoomMsgService;
import com.anbang.qipai.xiuxianchang.msg.service.DianpaoGameRoomMsgService;
import com.anbang.qipai.xiuxianchang.msg.service.DoudizhuGameRoomMsgService;
import com.anbang.qipai.xiuxianchang.msg.service.FangpaoGameRoomMsgService;
import com.anbang.qipai.xiuxianchang.msg.service.RuianGameRoomMsgService;
import com.anbang.qipai.xiuxianchang.msg.service.WenzhouGameRoomMsgService;
import com.anbang.qipai.xiuxianchang.msg.service.WenzhouShuangkouGameRoomMsgService;
import com.anbang.qipai.xiuxianchang.plan.bean.Game;
import com.anbang.qipai.xiuxianchang.plan.bean.GameRoom;
import com.anbang.qipai.xiuxianchang.plan.bean.GameServer;
import com.anbang.qipai.xiuxianchang.plan.bean.MemberGameRoom;
import com.anbang.qipai.xiuxianchang.plan.bean.MemberLoginLimitRecord;
import com.anbang.qipai.xiuxianchang.plan.service.GameService;
import com.anbang.qipai.xiuxianchang.plan.service.MemberAuthService;
import com.anbang.qipai.xiuxianchang.plan.service.MemberLoginLimitRecordService;
import com.anbang.qipai.xiuxianchang.web.fb.CyskLawsFB;
import com.anbang.qipai.xiuxianchang.web.fb.DdzLawsFB;
import com.anbang.qipai.xiuxianchang.web.fb.DpmjLawsFB;
import com.anbang.qipai.xiuxianchang.web.fb.FpmjLawsFB;
import com.anbang.qipai.xiuxianchang.web.fb.RamjLawsFB;
import com.anbang.qipai.xiuxianchang.web.fb.WzmjLawsFB;
import com.anbang.qipai.xiuxianchang.web.fb.WzskLawsFB;
import com.anbang.qipai.xiuxianchang.web.vo.CommonVO;
import com.anbang.qipai.xiuxianchang.web.vo.MemberPlayingRoomVO;
import com.google.gson.Gson;

/**
 * 去玩游戏相关的控制器
 * 
 * @author Neo
 *
 */
@RestController
@RequestMapping("/game")
public class GamePlayController {

	@Autowired
	private MemberAuthService memberAuthService;

	@Autowired
	private MemberLoginLimitRecordService memberLoginLimitRecordService;

	@Autowired
	private GameService gameService;

	@Autowired
	private GameRoomCmdService gameRoomCmdService;

	@Autowired
	private RuianGameRoomMsgService ruianGameRoomMsgService;

	@Autowired
	private FangpaoGameRoomMsgService fangpaoGameRoomMsgService;

	@Autowired
	private WenzhouGameRoomMsgService wenzhouGameRoomMsgService;

	@Autowired
	private DianpaoGameRoomMsgService dianpaoGameRoomMsgService;

	@Autowired
	private WenzhouShuangkouGameRoomMsgService wenzhouShuangkouGameRoomMsgService;

	@Autowired
	private ChayuanShuangkouGameRoomMsgService chayuanShuangkouGameRoomMsgService;

	@Autowired
	private DoudizhuGameRoomMsgService doudizhuGameRoomMsgService;

	@Autowired
	private MemberGoldQueryService memberGoldQueryService;

	@Autowired
	private HttpClient httpClient;

	private Gson gson = new Gson();

	/**
	 * 加入瑞安麻将房间
	 */
	@RequestMapping(value = "/join_ramj_room")
	public CommonVO joinRamjRoom(String token) {
		CommonVO vo = new CommonVO();
		String memberId = memberAuthService.getMemberIdBySessionId(token);
		if (memberId == null) {
			vo.setSuccess(false);
			vo.setMsg("invalid token");
			return vo;
		}
		MemberLoginLimitRecord loginLimitRecord = memberLoginLimitRecordService.findByMemberId(memberId, true);
		if (loginLimitRecord != null) {
			vo.setSuccess(false);
			vo.setMsg("login limited");
			return vo;
		}
		MemberGoldAccountDbo account = memberGoldQueryService.findMemberGoldAccount(memberId);
		if (account == null || account.getBalance() < 1500) {
			vo.setSuccess(false);
			vo.setMsg("gold insufficient");
			return vo;
		}
		Random r = new Random();
		List<GameRoom> gameRoomList = gameService.findNotFullGameRoom(Game.ruianMajiang);
		GameRoom gameRoom = null;
		if (gameRoomList != null && !gameRoomList.isEmpty()) {
			gameRoom = gameRoomList.get(r.nextInt(gameRoomList.size()));
		}
		boolean joinSuccess = false;// 是否加入成功
		if (gameRoom != null) {
			// 处理如果是自己暂时离开的房间
			String serverGameId = "";
			serverGameId = gameRoom.getServerGame().getGameId();
			boolean backSuccess = false;// 是否返回成功
			MemberGameRoom memberGameRoom = gameService.findMemberGameRoomByGameAndMemberId(gameRoom.getGame(),
					serverGameId, memberId);
			if (memberGameRoom != null) {
				// 游戏服务器rpc返回房间
				GameServer gameServer = gameRoom.getServerGame().getServer();
				Request req = httpClient.newRequest(gameServer.getHttpUrl() + "/game/backtogame");
				req.param("playerId", memberId);
				req.param("gameId", serverGameId);
				Map resData = null;
				try {
					ContentResponse res = req.send();
					String resJson = new String(res.getContent());
					CommonVO resVo = gson.fromJson(resJson, CommonVO.class);
					if (resVo.isSuccess()) {
						resData = (Map) resVo.getData();
						backSuccess = true;
					} else {
						backSuccess = false;
					}
				} catch (Exception e) {
					backSuccess = false;
				}
				if (backSuccess) {
					Map data = new HashMap();
					data.put("httpUrl", gameRoom.getServerGame().getServer().getHttpUrl());
					data.put("wsUrl", gameRoom.getServerGame().getServer().getWsUrl());
					data.put("token", resData.get("token"));
					data.put("gameId", serverGameId);
					data.put("game", gameRoom.getGame());
					vo.setData(data);
					return vo;
				}
			} else {
				// 游戏服务器rpc加入房间
				GameServer gameServer = gameRoom.getServerGame().getServer();
				Request req = httpClient.newRequest(gameServer.getHttpUrl() + "/game/joingame");
				req.param("playerId", memberId);
				req.param("gameId", serverGameId);
				Map resData = null;
				try {
					ContentResponse res = req.send();
					String resJson = new String(res.getContent());
					CommonVO resVo = gson.fromJson(resJson, CommonVO.class);
					if (resVo.isSuccess()) {
						resData = (Map) resVo.getData();
						joinSuccess = true;
					} else {
						joinSuccess = false;
					}
				} catch (Exception e) {
					joinSuccess = false;
				}
				try {
					gameRoomCmdService.joinGame(serverGameId, memberId);
				} catch (Exception e) {
					joinSuccess = false;
				}
				if (joinSuccess) {
					gameService.joinGameRoom(gameRoom, memberId);

					Map data = new HashMap();
					data.put("httpUrl", gameRoom.getServerGame().getServer().getHttpUrl());
					data.put("wsUrl", gameRoom.getServerGame().getServer().getWsUrl());
					data.put("token", resData.get("token"));
					data.put("gameId", serverGameId);
					data.put("game", gameRoom.getGame());
					vo.setData(data);
					return vo;
				}
			}
		}
		if (!joinSuccess) {// 加入失败,创建房间
			// 创建玩法
			List<String> lawNames = new ArrayList<>();
			lawNames.add("wsf");
			lawNames.add("bxts");
			lawNames.add("yj");
			lawNames.add("sir");
			lawNames.add("dpwf");
			try {
				gameRoom = gameService.buildRamjGameRoom(memberId, lawNames);
			} catch (Exception e) {
				vo.setSuccess(false);
				vo.setMsg(e.getClass().getName());
				return vo;
			}
			gameService.saveGameRoom(gameRoom);
			// 游戏服务器rpc，需要手动httpclientrpc
			GameServer gameServer = gameRoom.getServerGame().getServer();
			RamjLawsFB fb = new RamjLawsFB(lawNames);
			Request req = httpClient.newRequest(gameServer.getHttpUrl() + "/game/newgame_leave_quit");
			req.param("playerId", memberId);
			req.param("difen", fb.getDifen());
			req.param("taishu", fb.getTaishu());
			req.param("panshu", fb.getPanshu());
			req.param("renshu", fb.getRenshu());
			req.param("dapao", fb.getDapao());
			Map resData;
			try {
				ContentResponse res = req.send();
				String resJson = new String(res.getContent());
				CommonVO resVo = gson.fromJson(resJson, CommonVO.class);
				if (!resVo.isSuccess()) {
					vo.setSuccess(false);
					vo.setMsg("SysException");
					return vo;
				}
				resData = (Map) resVo.getData();
				gameRoom.getServerGame().setGameId((String) resData.get("gameId"));
			} catch (Exception e) {
				vo.setSuccess(false);
				vo.setMsg("SysException");
				return vo;
			}
			try {
				gameRoomCmdService.createGame(gameRoom.getServerGame().getGameId(), memberId, gameRoom.getGame(),
						Integer.valueOf(fb.getRenshu()), System.currentTimeMillis());
			} catch (Exception e) {
				vo.setSuccess(false);
				vo.setMsg(e.getClass().getName());
				return vo;
			}
			gameService.createGameRoom(gameRoom, memberId);
			Map data = new HashMap();
			data.put("httpUrl", gameRoom.getServerGame().getServer().getHttpUrl());
			data.put("wsUrl", gameRoom.getServerGame().getServer().getWsUrl());
			data.put("gameId", gameRoom.getServerGame().getGameId());
			data.put("token", resData.get("token"));
			data.put("game", gameRoom.getGame());
			vo.setData(data);
			ruianGameRoomMsgService.createGameRoom(gameRoom.getServerGame().getGameId(), gameRoom.getGame().name());
			return vo;
		}
		// 全部失败
		vo.setSuccess(false);
		vo.setMsg("NoServerAvailableForGameException");
		return vo;
	}

	/**
	 * 加入放炮麻将房间
	 */
	@RequestMapping(value = "/join_fpmj_room")
	public CommonVO joinFpmjRoom(String token) {
		CommonVO vo = new CommonVO();
		String memberId = memberAuthService.getMemberIdBySessionId(token);
		if (memberId == null) {
			vo.setSuccess(false);
			vo.setMsg("invalid token");
			return vo;
		}
		MemberLoginLimitRecord loginLimitRecord = memberLoginLimitRecordService.findByMemberId(memberId, true);
		if (loginLimitRecord != null) {
			vo.setSuccess(false);
			vo.setMsg("login limited");
			return vo;
		}
		MemberGoldAccountDbo account = memberGoldQueryService.findMemberGoldAccount(memberId);
		if (account == null || account.getBalance() < 1500) {
			vo.setSuccess(false);
			vo.setMsg("gold insufficient");
			return vo;
		}
		Random r = new Random();
		List<GameRoom> gameRoomList = gameService.findNotFullGameRoom(Game.fangpaoMajiang);
		GameRoom gameRoom = null;
		if (gameRoomList != null && !gameRoomList.isEmpty()) {
			gameRoom = gameRoomList.get(r.nextInt(gameRoomList.size()));
		}
		boolean joinSuccess = false;// 是否加入成功
		if (gameRoom != null) {
			// 处理如果是自己暂时离开的房间
			String serverGameId = "";
			serverGameId = gameRoom.getServerGame().getGameId();
			boolean backSuccess = false;// 是否返回成功
			MemberGameRoom memberGameRoom = gameService.findMemberGameRoomByGameAndMemberId(gameRoom.getGame(),
					serverGameId, memberId);
			if (memberGameRoom != null) {
				// 游戏服务器rpc返回房间
				GameServer gameServer = gameRoom.getServerGame().getServer();
				Request req = httpClient.newRequest(gameServer.getHttpUrl() + "/game/backtogame");
				req.param("playerId", memberId);
				req.param("gameId", serverGameId);
				Map resData = null;
				try {
					ContentResponse res = req.send();
					String resJson = new String(res.getContent());
					CommonVO resVo = gson.fromJson(resJson, CommonVO.class);
					if (resVo.isSuccess()) {
						resData = (Map) resVo.getData();
						backSuccess = true;
					} else {
						backSuccess = false;
					}
				} catch (Exception e) {
					backSuccess = false;
				}
				if (backSuccess) {
					Map data = new HashMap();
					data.put("httpUrl", gameRoom.getServerGame().getServer().getHttpUrl());
					data.put("wsUrl", gameRoom.getServerGame().getServer().getWsUrl());
					data.put("token", resData.get("token"));
					data.put("gameId", serverGameId);
					data.put("game", gameRoom.getGame());
					vo.setData(data);
					return vo;
				}
			} else {
				// 游戏服务器rpc加入房间
				GameServer gameServer = gameRoom.getServerGame().getServer();
				Request req = httpClient.newRequest(gameServer.getHttpUrl() + "/game/joingame");
				req.param("playerId", memberId);
				req.param("gameId", serverGameId);
				Map resData = null;
				try {
					ContentResponse res = req.send();
					String resJson = new String(res.getContent());
					CommonVO resVo = gson.fromJson(resJson, CommonVO.class);
					if (resVo.isSuccess()) {
						resData = (Map) resVo.getData();
						joinSuccess = true;
					} else {
						joinSuccess = false;
					}
				} catch (Exception e) {
					joinSuccess = false;
				}
				try {
					gameRoomCmdService.joinGame(serverGameId, memberId);
				} catch (Exception e) {
					joinSuccess = false;
				}
				if (joinSuccess) {
					gameService.joinGameRoom(gameRoom, memberId);

					Map data = new HashMap();
					data.put("httpUrl", gameRoom.getServerGame().getServer().getHttpUrl());
					data.put("wsUrl", gameRoom.getServerGame().getServer().getWsUrl());
					data.put("token", resData.get("token"));
					data.put("gameId", serverGameId);
					data.put("game", gameRoom.getGame());
					vo.setData(data);
					return vo;
				}
			}
		}
		if (!joinSuccess) {// 加入失败,创建房间
			// 创建玩法
			List<String> lawNames = new ArrayList<>();
			lawNames.add("yj");
			lawNames.add("sir");
			lawNames.add("hzdcs");
			lawNames.add("dp");
			lawNames.add("spfb");
			try {
				gameRoom = gameService.buildFpmjGameRoom(memberId, lawNames);
			} catch (Exception e) {
				vo.setSuccess(false);
				vo.setMsg(e.getClass().getName());
				return vo;
			}
			gameService.saveGameRoom(gameRoom);
			// 游戏服务器rpc，需要手动httpclientrpc
			GameServer gameServer = gameRoom.getServerGame().getServer();
			FpmjLawsFB fb = new FpmjLawsFB(lawNames);
			Request req = httpClient.newRequest(gameServer.getHttpUrl() + "/game/newgame_leave_quit");
			req.param("playerId", memberId);
			req.param("panshu", fb.getPanshu());
			req.param("renshu", fb.getRenshu());
			req.param("hongzhongcaishen", fb.getHognzhongcaishen());
			req.param("dapao", fb.getDapao());
			req.param("sipaofanbei", fb.getSipaofanbei());
			req.param("zhuaniao", fb.getZhuaniao());
			req.param("niaoshu", fb.getNiaoshu());
			Map resData;
			try {
				ContentResponse res = req.send();
				String resJson = new String(res.getContent());
				CommonVO resVo = gson.fromJson(resJson, CommonVO.class);
				if (!resVo.isSuccess()) {
					vo.setSuccess(false);
					vo.setMsg("SysException");
					return vo;
				}
				resData = (Map) resVo.getData();
				gameRoom.getServerGame().setGameId((String) resData.get("gameId"));
			} catch (Exception e) {
				vo.setSuccess(false);
				vo.setMsg("SysException");
				return vo;
			}
			try {
				gameRoomCmdService.createGame(gameRoom.getServerGame().getGameId(), memberId, gameRoom.getGame(),
						Integer.valueOf(fb.getRenshu()), System.currentTimeMillis());
			} catch (Exception e) {
				vo.setSuccess(false);
				vo.setMsg(e.getClass().getName());
				return vo;
			}
			gameService.createGameRoom(gameRoom, memberId);
			Map data = new HashMap();
			data.put("httpUrl", gameRoom.getServerGame().getServer().getHttpUrl());
			data.put("wsUrl", gameRoom.getServerGame().getServer().getWsUrl());
			data.put("gameId", gameRoom.getServerGame().getGameId());
			data.put("token", resData.get("token"));
			data.put("game", gameRoom.getGame());
			vo.setData(data);
			fangpaoGameRoomMsgService.createGameRoom(gameRoom.getServerGame().getGameId(), gameRoom.getGame().name());
			return vo;
		}
		// 全部失败
		vo.setSuccess(false);
		vo.setMsg("NoServerAvailableForGameException");
		return vo;
	}

	/**
	 * 加入温州麻将房间
	 */
	@RequestMapping(value = "/join_wzmj_room")
	public CommonVO joinWzmjRoom(String token) {
		CommonVO vo = new CommonVO();
		String memberId = memberAuthService.getMemberIdBySessionId(token);
		if (memberId == null) {
			vo.setSuccess(false);
			vo.setMsg("invalid token");
			return vo;
		}
		MemberLoginLimitRecord loginLimitRecord = memberLoginLimitRecordService.findByMemberId(memberId, true);
		if (loginLimitRecord != null) {
			vo.setSuccess(false);
			vo.setMsg("login limited");
			return vo;
		}
		MemberGoldAccountDbo account = memberGoldQueryService.findMemberGoldAccount(memberId);
		if (account == null || account.getBalance() < 1500) {
			vo.setSuccess(false);
			vo.setMsg("gold insufficient");
			return vo;
		}
		Random r = new Random();
		List<GameRoom> gameRoomList = gameService.findNotFullGameRoom(Game.wenzhouMajiang);
		GameRoom gameRoom = null;
		if (gameRoomList != null && !gameRoomList.isEmpty()) {
			gameRoom = gameRoomList.get(r.nextInt(gameRoomList.size()));
		}
		boolean joinSuccess = false;// 是否加入成功
		if (gameRoom != null) {
			// 处理如果是自己暂时离开的房间
			String serverGameId = "";
			serverGameId = gameRoom.getServerGame().getGameId();
			boolean backSuccess = false;// 是否返回成功
			MemberGameRoom memberGameRoom = gameService.findMemberGameRoomByGameAndMemberId(gameRoom.getGame(),
					serverGameId, memberId);
			if (memberGameRoom != null) {
				// 游戏服务器rpc返回房间
				GameServer gameServer = gameRoom.getServerGame().getServer();
				Request req = httpClient.newRequest(gameServer.getHttpUrl() + "/game/backtogame");
				req.param("playerId", memberId);
				req.param("gameId", serverGameId);
				Map resData = null;
				try {
					ContentResponse res = req.send();
					String resJson = new String(res.getContent());
					CommonVO resVo = gson.fromJson(resJson, CommonVO.class);
					if (resVo.isSuccess()) {
						resData = (Map) resVo.getData();
						backSuccess = true;
					} else {
						backSuccess = false;
					}
				} catch (Exception e) {
					backSuccess = false;
				}
				if (backSuccess) {
					Map data = new HashMap();
					data.put("httpUrl", gameRoom.getServerGame().getServer().getHttpUrl());
					data.put("wsUrl", gameRoom.getServerGame().getServer().getWsUrl());
					data.put("token", resData.get("token"));
					data.put("gameId", serverGameId);
					data.put("game", gameRoom.getGame());
					vo.setData(data);
					return vo;
				}
			} else {
				// 游戏服务器rpc加入房间
				GameServer gameServer = gameRoom.getServerGame().getServer();
				Request req = httpClient.newRequest(gameServer.getHttpUrl() + "/game/joingame");
				req.param("playerId", memberId);
				req.param("gameId", serverGameId);
				Map resData = null;
				try {
					ContentResponse res = req.send();
					String resJson = new String(res.getContent());
					CommonVO resVo = gson.fromJson(resJson, CommonVO.class);
					if (resVo.isSuccess()) {
						resData = (Map) resVo.getData();
						joinSuccess = true;
					} else {
						joinSuccess = false;
					}
				} catch (Exception e) {
					joinSuccess = false;
				}
				try {
					gameRoomCmdService.joinGame(serverGameId, memberId);
				} catch (Exception e) {
					joinSuccess = false;
				}
				if (joinSuccess) {
					gameService.joinGameRoom(gameRoom, memberId);

					Map data = new HashMap();
					data.put("httpUrl", gameRoom.getServerGame().getServer().getHttpUrl());
					data.put("wsUrl", gameRoom.getServerGame().getServer().getWsUrl());
					data.put("token", resData.get("token"));
					data.put("gameId", serverGameId);
					data.put("game", gameRoom.getGame());
					vo.setData(data);
					return vo;
				}
			}
		}
		if (!joinSuccess) {// 加入失败,创建房间
			// 创建玩法
			List<String> lawNames = new ArrayList<>();
			lawNames.add("yj");
			lawNames.add("sir");
			lawNames.add("jj1");
			lawNames.add("tssf");
			lawNames.add("csq");
			lawNames.add("szf");
			lawNames.add("gsf");
			try {
				gameRoom = gameService.buildWzmjGameRoom(memberId, lawNames);
			} catch (Exception e) {
				vo.setSuccess(false);
				vo.setMsg(e.getClass().getName());
				return vo;
			}
			gameService.saveGameRoom(gameRoom);
			// 游戏服务器rpc，需要手动httpclientrpc
			GameServer gameServer = gameRoom.getServerGame().getServer();
			WzmjLawsFB fb = new WzmjLawsFB(lawNames);
			Request req = httpClient.newRequest(gameServer.getHttpUrl() + "/game/newgame_leave_quit");
			req.param("playerId", memberId);
			req.param("panshu", fb.getPanshu());
			req.param("renshu", fb.getRenshu());
			req.param("jinjie1", fb.getJinjie1());
			req.param("jinjie2", fb.getJinjie2());
			req.param("gangsuanfen", fb.getGangsuanfen());
			req.param("teshushuangfan", fb.getTeshushuangfan());
			req.param("caishenqian", fb.getCaishenqian());
			req.param("shaozhongfa", fb.getShaozhongfa());
			req.param("lazila", fb.getLazila());
			Map resData;
			try {
				ContentResponse res = req.send();
				String resJson = new String(res.getContent());
				CommonVO resVo = gson.fromJson(resJson, CommonVO.class);
				if (!resVo.isSuccess()) {
					vo.setSuccess(false);
					vo.setMsg("SysException");
					return vo;
				}
				resData = (Map) resVo.getData();
				gameRoom.getServerGame().setGameId((String) resData.get("gameId"));
			} catch (Exception e) {
				vo.setSuccess(false);
				vo.setMsg("SysException");
				return vo;
			}
			try {
				gameRoomCmdService.createGame(gameRoom.getServerGame().getGameId(), memberId, gameRoom.getGame(),
						Integer.valueOf(fb.getRenshu()), System.currentTimeMillis());
			} catch (Exception e) {
				vo.setSuccess(false);
				vo.setMsg(e.getClass().getName());
				return vo;
			}
			gameService.createGameRoom(gameRoom, memberId);
			Map data = new HashMap();
			data.put("httpUrl", gameRoom.getServerGame().getServer().getHttpUrl());
			data.put("wsUrl", gameRoom.getServerGame().getServer().getWsUrl());
			data.put("gameId", gameRoom.getServerGame().getGameId());
			data.put("token", resData.get("token"));
			data.put("game", gameRoom.getGame());
			vo.setData(data);
			wenzhouGameRoomMsgService.createGameRoom(gameRoom.getServerGame().getGameId(), gameRoom.getGame().name());
			return vo;
		}
		// 全部失败
		vo.setSuccess(false);
		vo.setMsg("NoServerAvailableForGameException");
		return vo;
	}

	/**
	 * 加入点炮麻将房间
	 */
	@RequestMapping(value = "/join_dpmj_room")
	public CommonVO joinDpmjRoom(String token) {
		CommonVO vo = new CommonVO();
		String memberId = memberAuthService.getMemberIdBySessionId(token);
		if (memberId == null) {
			vo.setSuccess(false);
			vo.setMsg("invalid token");
			return vo;
		}
		MemberLoginLimitRecord loginLimitRecord = memberLoginLimitRecordService.findByMemberId(memberId, true);
		if (loginLimitRecord != null) {
			vo.setSuccess(false);
			vo.setMsg("login limited");
			return vo;
		}
		MemberGoldAccountDbo account = memberGoldQueryService.findMemberGoldAccount(memberId);
		if (account == null || account.getBalance() < 1500) {
			vo.setSuccess(false);
			vo.setMsg("gold insufficient");
			return vo;
		}
		Random r = new Random();
		List<GameRoom> gameRoomList = gameService.findNotFullGameRoom(Game.dianpaoMajiang);
		GameRoom gameRoom = null;
		if (gameRoomList != null && !gameRoomList.isEmpty()) {
			gameRoom = gameRoomList.get(r.nextInt(gameRoomList.size()));
		}
		boolean joinSuccess = false;// 是否加入成功
		if (gameRoom != null) {
			// 处理如果是自己暂时离开的房间
			String serverGameId = "";
			serverGameId = gameRoom.getServerGame().getGameId();
			boolean backSuccess = false;// 是否返回成功
			MemberGameRoom memberGameRoom = gameService.findMemberGameRoomByGameAndMemberId(gameRoom.getGame(),
					serverGameId, memberId);
			if (memberGameRoom != null) {
				// 游戏服务器rpc返回房间
				GameServer gameServer = gameRoom.getServerGame().getServer();
				Request req = httpClient.newRequest(gameServer.getHttpUrl() + "/game/backtogame");
				req.param("playerId", memberId);
				req.param("gameId", serverGameId);
				Map resData = null;
				try {
					ContentResponse res = req.send();
					String resJson = new String(res.getContent());
					CommonVO resVo = gson.fromJson(resJson, CommonVO.class);
					if (resVo.isSuccess()) {
						resData = (Map) resVo.getData();
						backSuccess = true;
					} else {
						backSuccess = false;
					}
				} catch (Exception e) {
					backSuccess = false;
				}
				if (backSuccess) {
					Map data = new HashMap();
					data.put("httpUrl", gameRoom.getServerGame().getServer().getHttpUrl());
					data.put("wsUrl", gameRoom.getServerGame().getServer().getWsUrl());
					data.put("token", resData.get("token"));
					data.put("gameId", serverGameId);
					data.put("game", gameRoom.getGame());
					vo.setData(data);
					return vo;
				}
			} else {
				// 游戏服务器rpc加入房间
				GameServer gameServer = gameRoom.getServerGame().getServer();
				Request req = httpClient.newRequest(gameServer.getHttpUrl() + "/game/joingame");
				req.param("playerId", memberId);
				req.param("gameId", serverGameId);
				Map resData = null;
				try {
					ContentResponse res = req.send();
					String resJson = new String(res.getContent());
					CommonVO resVo = gson.fromJson(resJson, CommonVO.class);
					if (resVo.isSuccess()) {
						resData = (Map) resVo.getData();
						joinSuccess = true;
					} else {
						joinSuccess = false;
					}
				} catch (Exception e) {
					joinSuccess = false;
				}
				try {
					gameRoomCmdService.joinGame(serverGameId, memberId);
				} catch (Exception e) {
					joinSuccess = false;
				}
				if (joinSuccess) {
					gameService.joinGameRoom(gameRoom, memberId);

					Map data = new HashMap();
					data.put("httpUrl", gameRoom.getServerGame().getServer().getHttpUrl());
					data.put("wsUrl", gameRoom.getServerGame().getServer().getWsUrl());
					data.put("token", resData.get("token"));
					data.put("gameId", serverGameId);
					data.put("game", gameRoom.getGame());
					vo.setData(data);
					return vo;
				}
			}
		}
		if (!joinSuccess) {// 加入失败,创建房间
			// 创建玩法
			List<String> lawNames = new ArrayList<>();
			lawNames.add("yj");
			lawNames.add("sir");
			lawNames.add("dianpao");
			lawNames.add("dp");
			lawNames.add("qzfb");
			lawNames.add("qingyise");
			try {
				gameRoom = gameService.buildDpmjGameRoom(memberId, lawNames);
			} catch (Exception e) {
				vo.setSuccess(false);
				vo.setMsg(e.getClass().getName());
				return vo;
			}
			gameService.saveGameRoom(gameRoom);
			// 游戏服务器rpc，需要手动httpclientrpc
			GameServer gameServer = gameRoom.getServerGame().getServer();
			DpmjLawsFB fb = new DpmjLawsFB(lawNames);
			// 远程调用游戏服务器的newgame
			Request req = httpClient.newRequest(gameServer.getHttpUrl() + "/game/newgame_leave_quit");
			req.param("playerId", memberId);
			req.param("panshu", fb.getPanshu());
			req.param("renshu", fb.getRenshu());
			req.param("dianpao", fb.getDianpao());
			req.param("dapao", fb.getDapao());
			req.param("quzhongfabai", fb.getQuzhongfabai());
			req.param("zhuaniao", fb.getZhuaniao());
			req.param("niaoshu", fb.getNiaoshu());
			req.param("qingyise", fb.getQingyise());
			Map resData;
			try {
				ContentResponse res = req.send();
				String resJson = new String(res.getContent());
				CommonVO resVo = gson.fromJson(resJson, CommonVO.class);
				if (!resVo.isSuccess()) {
					vo.setSuccess(false);
					vo.setMsg("SysException");
					return vo;
				}
				resData = (Map) resVo.getData();
				gameRoom.getServerGame().setGameId((String) resData.get("gameId"));
			} catch (Exception e) {
				vo.setSuccess(false);
				vo.setMsg("SysException");
				return vo;
			}
			try {
				gameRoomCmdService.createGame(gameRoom.getServerGame().getGameId(), memberId, gameRoom.getGame(),
						Integer.valueOf(fb.getRenshu()), System.currentTimeMillis());
			} catch (Exception e) {
				vo.setSuccess(false);
				vo.setMsg(e.getClass().getName());
				return vo;
			}
			gameService.createGameRoom(gameRoom, memberId);
			Map data = new HashMap();
			data.put("httpUrl", gameRoom.getServerGame().getServer().getHttpUrl());
			data.put("wsUrl", gameRoom.getServerGame().getServer().getWsUrl());
			data.put("gameId", gameRoom.getServerGame().getGameId());
			data.put("token", resData.get("token"));
			data.put("game", gameRoom.getGame());
			vo.setData(data);
			dianpaoGameRoomMsgService.createGameRoom(gameRoom.getServerGame().getGameId(), gameRoom.getGame().name());
			return vo;
		}
		// 全部失败
		vo.setSuccess(false);
		vo.setMsg("NoServerAvailableForGameException");
		return vo;
	}

	/**
	 * 加入温州双扣房间
	 */
	@RequestMapping(value = "/join_wzsk_room")
	public CommonVO joinWzskRoom(String token) {
		CommonVO vo = new CommonVO();
		String memberId = memberAuthService.getMemberIdBySessionId(token);
		if (memberId == null) {
			vo.setSuccess(false);
			vo.setMsg("invalid token");
			return vo;
		}
		MemberLoginLimitRecord loginLimitRecord = memberLoginLimitRecordService.findByMemberId(memberId, true);
		if (loginLimitRecord != null) {
			vo.setSuccess(false);
			vo.setMsg("login limited");
			return vo;
		}
		MemberGoldAccountDbo account = memberGoldQueryService.findMemberGoldAccount(memberId);
		if (account == null || account.getBalance() < 1500) {
			vo.setSuccess(false);
			vo.setMsg("gold insufficient");
			return vo;
		}
		Random r = new Random();
		List<GameRoom> gameRoomList = gameService.findNotFullGameRoom(Game.wenzhouShuangkou);
		GameRoom gameRoom = null;
		if (gameRoomList != null && !gameRoomList.isEmpty()) {
			gameRoom = gameRoomList.get(r.nextInt(gameRoomList.size()));
		}
		boolean joinSuccess = false;// 是否加入成功
		if (gameRoom != null) {
			// 处理如果是自己暂时离开的房间
			String serverGameId = "";
			serverGameId = gameRoom.getServerGame().getGameId();
			boolean backSuccess = false;// 是否返回成功
			MemberGameRoom memberGameRoom = gameService.findMemberGameRoomByGameAndMemberId(gameRoom.getGame(),
					serverGameId, memberId);
			if (memberGameRoom != null) {
				// 游戏服务器rpc返回房间
				GameServer gameServer = gameRoom.getServerGame().getServer();
				Request req = httpClient.newRequest(gameServer.getHttpUrl() + "/game/backtogame");
				req.param("playerId", memberId);
				req.param("gameId", serverGameId);
				Map resData = null;
				try {
					ContentResponse res = req.send();
					String resJson = new String(res.getContent());
					CommonVO resVo = gson.fromJson(resJson, CommonVO.class);
					if (resVo.isSuccess()) {
						resData = (Map) resVo.getData();
						backSuccess = true;
					} else {
						backSuccess = false;
					}
				} catch (Exception e) {
					backSuccess = false;
				}
				if (backSuccess) {
					Map data = new HashMap();
					data.put("httpUrl", gameRoom.getServerGame().getServer().getHttpUrl());
					data.put("wsUrl", gameRoom.getServerGame().getServer().getWsUrl());
					data.put("token", resData.get("token"));
					data.put("gameId", serverGameId);
					data.put("game", gameRoom.getGame());
					vo.setData(data);
					return vo;
				}
			} else {
				// 游戏服务器rpc加入房间
				GameServer gameServer = gameRoom.getServerGame().getServer();
				Request req = httpClient.newRequest(gameServer.getHttpUrl() + "/game/joingame");
				req.param("playerId", memberId);
				req.param("gameId", serverGameId);
				Map resData = null;
				try {
					ContentResponse res = req.send();
					String resJson = new String(res.getContent());
					CommonVO resVo = gson.fromJson(resJson, CommonVO.class);
					if (resVo.isSuccess()) {
						resData = (Map) resVo.getData();
						joinSuccess = true;
					} else {
						joinSuccess = false;
					}
				} catch (Exception e) {
					joinSuccess = false;
				}
				try {
					gameRoomCmdService.joinGame(serverGameId, memberId);
				} catch (Exception e) {
					joinSuccess = false;
				}
				if (joinSuccess) {
					gameService.joinGameRoom(gameRoom, memberId);

					Map data = new HashMap();
					data.put("httpUrl", gameRoom.getServerGame().getServer().getHttpUrl());
					data.put("wsUrl", gameRoom.getServerGame().getServer().getWsUrl());
					data.put("token", resData.get("token"));
					data.put("gameId", serverGameId);
					data.put("game", gameRoom.getGame());
					vo.setData(data);
					return vo;
				}
			}
		}
		if (!joinSuccess) {// 加入失败,创建房间
			// 创建玩法
			List<String> lawNames = new ArrayList<>();
			lawNames.add("yj");
			lawNames.add("sir");
			lawNames.add("qianbian");
			lawNames.add("ba");
			lawNames.add("jiu");
			try {
				gameRoom = gameService.buildWzskGameRoom(memberId, lawNames);
			} catch (Exception e) {
				vo.setSuccess(false);
				vo.setMsg(e.getClass().getName());
				return vo;
			}
			gameService.saveGameRoom(gameRoom);
			// 游戏服务器rpc，需要手动httpclientrpc
			GameServer gameServer = gameRoom.getServerGame().getServer();
			WzskLawsFB fb = new WzskLawsFB(lawNames);
			// 远程调用游戏服务器的newgame
			Request req = httpClient.newRequest(gameServer.getHttpUrl() + "/game/newgame_leave_quit");
			req.param("playerId", memberId);
			req.param("panshu", fb.getPanshu());
			req.param("renshu", fb.getRenshu());
			req.param("bx", fb.getBx());
			req.param("chapai", fb.getChapai());
			req.param("fapai", fb.getFapai());
			req.param("chaodi", fb.getChaodi());
			req.param("shuangming", fb.getShuangming());
			req.param("bxfd", fb.getBxfd());
			Map resData;
			try {
				ContentResponse res = req.send();
				String resJson = new String(res.getContent());
				CommonVO resVo = gson.fromJson(resJson, CommonVO.class);
				if (!resVo.isSuccess()) {
					vo.setSuccess(false);
					vo.setMsg("SysException");
					return vo;
				}
				resData = (Map) resVo.getData();
				gameRoom.getServerGame().setGameId((String) resData.get("gameId"));
			} catch (Exception e) {
				vo.setSuccess(false);
				vo.setMsg("SysException");
				return vo;
			}
			try {
				gameRoomCmdService.createGame(gameRoom.getServerGame().getGameId(), memberId, gameRoom.getGame(),
						Integer.valueOf(fb.getRenshu()), System.currentTimeMillis());
			} catch (Exception e) {
				vo.setSuccess(false);
				vo.setMsg(e.getClass().getName());
				return vo;
			}
			gameService.createGameRoom(gameRoom, memberId);
			Map data = new HashMap();
			data.put("httpUrl", gameRoom.getServerGame().getServer().getHttpUrl());
			data.put("wsUrl", gameRoom.getServerGame().getServer().getWsUrl());
			data.put("gameId", gameRoom.getServerGame().getGameId());
			data.put("token", resData.get("token"));
			data.put("game", gameRoom.getGame());
			vo.setData(data);
			wenzhouShuangkouGameRoomMsgService.createGameRoom(gameRoom.getServerGame().getGameId(),
					gameRoom.getGame().name());
			return vo;
		}
		// 全部失败
		vo.setSuccess(false);
		vo.setMsg("NoServerAvailableForGameException");
		return vo;
	}

	/**
	 * 加入茶苑双扣房间
	 */
	@RequestMapping(value = "/join_cysk_room")
	public CommonVO joinCyskRoom(String token) {
		CommonVO vo = new CommonVO();
		String memberId = memberAuthService.getMemberIdBySessionId(token);
		if (memberId == null) {
			vo.setSuccess(false);
			vo.setMsg("invalid token");
			return vo;
		}
		MemberLoginLimitRecord loginLimitRecord = memberLoginLimitRecordService.findByMemberId(memberId, true);
		if (loginLimitRecord != null) {
			vo.setSuccess(false);
			vo.setMsg("login limited");
			return vo;
		}
		MemberGoldAccountDbo account = memberGoldQueryService.findMemberGoldAccount(memberId);
		if (account == null || account.getBalance() < 1500) {
			vo.setSuccess(false);
			vo.setMsg("gold insufficient");
			return vo;
		}
		Random r = new Random();
		List<GameRoom> gameRoomList = gameService.findNotFullGameRoom(Game.chayuanShuangkou);
		GameRoom gameRoom = null;
		if (gameRoomList != null && !gameRoomList.isEmpty()) {
			gameRoom = gameRoomList.get(r.nextInt(gameRoomList.size()));
		}
		boolean joinSuccess = false;// 是否加入成功
		if (gameRoom != null) {
			// 处理如果是自己暂时离开的房间
			String serverGameId = "";
			serverGameId = gameRoom.getServerGame().getGameId();
			boolean backSuccess = false;// 是否返回成功
			MemberGameRoom memberGameRoom = gameService.findMemberGameRoomByGameAndMemberId(gameRoom.getGame(),
					serverGameId, memberId);
			if (memberGameRoom != null) {
				// 游戏服务器rpc返回房间
				GameServer gameServer = gameRoom.getServerGame().getServer();
				Request req = httpClient.newRequest(gameServer.getHttpUrl() + "/game/backtogame");
				req.param("playerId", memberId);
				req.param("gameId", serverGameId);
				Map resData = null;
				try {
					ContentResponse res = req.send();
					String resJson = new String(res.getContent());
					CommonVO resVo = gson.fromJson(resJson, CommonVO.class);
					if (resVo.isSuccess()) {
						resData = (Map) resVo.getData();
						backSuccess = true;
					} else {
						backSuccess = false;
					}
				} catch (Exception e) {
					backSuccess = false;
				}
				if (backSuccess) {
					Map data = new HashMap();
					data.put("httpUrl", gameRoom.getServerGame().getServer().getHttpUrl());
					data.put("wsUrl", gameRoom.getServerGame().getServer().getWsUrl());
					data.put("token", resData.get("token"));
					data.put("gameId", serverGameId);
					data.put("game", gameRoom.getGame());
					vo.setData(data);
					return vo;
				}
			} else {
				// 游戏服务器rpc加入房间
				GameServer gameServer = gameRoom.getServerGame().getServer();
				Request req = httpClient.newRequest(gameServer.getHttpUrl() + "/game/joingame");
				req.param("playerId", memberId);
				req.param("gameId", serverGameId);
				Map resData = null;
				try {
					ContentResponse res = req.send();
					String resJson = new String(res.getContent());
					CommonVO resVo = gson.fromJson(resJson, CommonVO.class);
					if (resVo.isSuccess()) {
						resData = (Map) resVo.getData();
						joinSuccess = true;
					} else {
						joinSuccess = false;
					}
				} catch (Exception e) {
					joinSuccess = false;
				}
				try {
					gameRoomCmdService.joinGame(serverGameId, memberId);
				} catch (Exception e) {
					joinSuccess = false;
				}
				if (joinSuccess) {
					gameService.joinGameRoom(gameRoom, memberId);

					Map data = new HashMap();
					data.put("httpUrl", gameRoom.getServerGame().getServer().getHttpUrl());
					data.put("wsUrl", gameRoom.getServerGame().getServer().getWsUrl());
					data.put("token", resData.get("token"));
					data.put("gameId", serverGameId);
					data.put("game", gameRoom.getGame());
					vo.setData(data);
					return vo;
				}
			}
		}
		if (!joinSuccess) {// 加入失败,创建房间
			// 创建玩法
			List<String> lawNames = new ArrayList<>();
			lawNames.add("yj");
			lawNames.add("sir");
			lawNames.add("qianbian");
			lawNames.add("jiu");
			try {
				gameRoom = gameService.buildCyskGameRoom(memberId, lawNames);
			} catch (Exception e) {
				vo.setSuccess(false);
				vo.setMsg(e.getClass().getName());
				return vo;
			}
			gameService.saveGameRoom(gameRoom);
			// 游戏服务器rpc，需要手动httpclientrpc
			GameServer gameServer = gameRoom.getServerGame().getServer();
			CyskLawsFB fb = new CyskLawsFB(lawNames);
			// 远程调用游戏服务器的newgame
			Request req = httpClient.newRequest(gameServer.getHttpUrl() + "/game/newgame_leave_quit");
			req.param("playerId", memberId);
			req.param("panshu", fb.getPanshu());
			req.param("renshu", fb.getRenshu());
			req.param("bx", fb.getBx());
			req.param("fapai", fb.getFapai());
			req.param("shuangming", fb.getShuangming());
			Map resData;
			try {
				ContentResponse res = req.send();
				String resJson = new String(res.getContent());
				CommonVO resVo = gson.fromJson(resJson, CommonVO.class);
				if (!resVo.isSuccess()) {
					vo.setSuccess(false);
					vo.setMsg("SysException");
					return vo;
				}
				resData = (Map) resVo.getData();
				gameRoom.getServerGame().setGameId((String) resData.get("gameId"));
			} catch (Exception e) {
				vo.setSuccess(false);
				vo.setMsg("SysException");
				return vo;
			}
			try {
				gameRoomCmdService.createGame(gameRoom.getServerGame().getGameId(), memberId, gameRoom.getGame(),
						Integer.valueOf(fb.getRenshu()), System.currentTimeMillis());
			} catch (Exception e) {
				vo.setSuccess(false);
				vo.setMsg(e.getClass().getName());
				return vo;
			}
			gameService.createGameRoom(gameRoom, memberId);
			Map data = new HashMap();
			data.put("httpUrl", gameRoom.getServerGame().getServer().getHttpUrl());
			data.put("wsUrl", gameRoom.getServerGame().getServer().getWsUrl());
			data.put("gameId", gameRoom.getServerGame().getGameId());
			data.put("token", resData.get("token"));
			data.put("game", gameRoom.getGame());
			vo.setData(data);
			chayuanShuangkouGameRoomMsgService.createGameRoom(gameRoom.getServerGame().getGameId(),
					gameRoom.getGame().name());
			return vo;
		}
		// 全部失败
		vo.setSuccess(false);
		vo.setMsg("NoServerAvailableForGameException");
		return vo;
	}

	/**
	 * 加入斗地主房间
	 */
	@RequestMapping(value = "/join_ddz_room")
	public CommonVO joinDdzRoom(String token) {
		CommonVO vo = new CommonVO();
		String memberId = memberAuthService.getMemberIdBySessionId(token);
		if (memberId == null) {
			vo.setSuccess(false);
			vo.setMsg("invalid token");
			return vo;
		}
		MemberLoginLimitRecord loginLimitRecord = memberLoginLimitRecordService.findByMemberId(memberId, true);
		if (loginLimitRecord != null) {
			vo.setSuccess(false);
			vo.setMsg("login limited");
			return vo;
		}
		MemberGoldAccountDbo account = memberGoldQueryService.findMemberGoldAccount(memberId);
		if (account == null || account.getBalance() < 1500) {
			vo.setSuccess(false);
			vo.setMsg("gold insufficient");
			return vo;
		}
		Random r = new Random();
		List<GameRoom> gameRoomList = gameService.findNotFullGameRoom(Game.doudizhu);
		GameRoom gameRoom = null;
		if (gameRoomList != null && !gameRoomList.isEmpty()) {
			gameRoom = gameRoomList.get(r.nextInt(gameRoomList.size()));
		}
		boolean joinSuccess = false;// 是否加入成功
		if (gameRoom != null) {
			// 处理如果是自己暂时离开的房间
			String serverGameId = "";
			serverGameId = gameRoom.getServerGame().getGameId();
			boolean backSuccess = false;// 是否返回成功
			MemberGameRoom memberGameRoom = gameService.findMemberGameRoomByGameAndMemberId(gameRoom.getGame(),
					serverGameId, memberId);
			if (memberGameRoom != null) {
				// 游戏服务器rpc返回房间
				GameServer gameServer = gameRoom.getServerGame().getServer();
				Request req = httpClient.newRequest(gameServer.getHttpUrl() + "/game/backtogame");
				req.param("playerId", memberId);
				req.param("gameId", serverGameId);
				Map resData = null;
				try {
					ContentResponse res = req.send();
					String resJson = new String(res.getContent());
					CommonVO resVo = gson.fromJson(resJson, CommonVO.class);
					if (resVo.isSuccess()) {
						resData = (Map) resVo.getData();
						backSuccess = true;
					} else {
						backSuccess = false;
					}
				} catch (Exception e) {
					backSuccess = false;
				}
				if (backSuccess) {
					Map data = new HashMap();
					data.put("httpUrl", gameRoom.getServerGame().getServer().getHttpUrl());
					data.put("wsUrl", gameRoom.getServerGame().getServer().getWsUrl());
					data.put("token", resData.get("token"));
					data.put("gameId", serverGameId);
					data.put("game", gameRoom.getGame());
					vo.setData(data);
					return vo;
				}
			} else {
				// 游戏服务器rpc加入房间
				GameServer gameServer = gameRoom.getServerGame().getServer();
				Request req = httpClient.newRequest(gameServer.getHttpUrl() + "/game/joingame");
				req.param("playerId", memberId);
				req.param("gameId", serverGameId);
				Map resData = null;
				try {
					ContentResponse res = req.send();
					String resJson = new String(res.getContent());
					CommonVO resVo = gson.fromJson(resJson, CommonVO.class);
					if (resVo.isSuccess()) {
						resData = (Map) resVo.getData();
						joinSuccess = true;
					} else {
						joinSuccess = false;
					}
				} catch (Exception e) {
					joinSuccess = false;
				}
				try {
					gameRoomCmdService.joinGame(serverGameId, memberId);
				} catch (Exception e) {
					joinSuccess = false;
				}
				if (joinSuccess) {
					gameService.joinGameRoom(gameRoom, memberId);

					Map data = new HashMap();
					data.put("httpUrl", gameRoom.getServerGame().getServer().getHttpUrl());
					data.put("wsUrl", gameRoom.getServerGame().getServer().getWsUrl());
					data.put("token", resData.get("token"));
					data.put("gameId", serverGameId);
					data.put("game", gameRoom.getGame());
					vo.setData(data);
					return vo;
				}
			}
		}
		if (!joinSuccess) {// 加入失败,创建房间
			// 创建玩法
			List<String> lawNames = new ArrayList<>();
			lawNames.add("yj");
			lawNames.add("sanr");
			lawNames.add("qxp");
			lawNames.add("yf");
			try {
				gameRoom = gameService.buildDdzGameRoom(memberId, lawNames);
			} catch (Exception e) {
				vo.setSuccess(false);
				vo.setMsg(e.getClass().getName());
				return vo;
			}
			gameService.saveGameRoom(gameRoom);
			// 游戏服务器rpc，需要手动httpclientrpc
			GameServer gameServer = gameRoom.getServerGame().getServer();
			DdzLawsFB fb = new DdzLawsFB(lawNames);
			// 远程调用游戏服务器的newgame
			Request req = httpClient.newRequest(gameServer.getHttpUrl() + "/game/newgame_leave_quit");
			req.param("playerId", memberId);
			req.param("panshu", fb.getPanshu());
			req.param("renshu", fb.getRenshu());
			req.param("difen", fb.getDifen());
			req.param("qxp", fb.getQxp());
			Map resData;
			try {
				ContentResponse res = req.send();
				String resJson = new String(res.getContent());
				CommonVO resVo = gson.fromJson(resJson, CommonVO.class);
				if (!resVo.isSuccess()) {
					vo.setSuccess(false);
					vo.setMsg("SysException");
					return vo;
				}
				resData = (Map) resVo.getData();
				gameRoom.getServerGame().setGameId((String) resData.get("gameId"));
			} catch (Exception e) {
				vo.setSuccess(false);
				vo.setMsg("SysException");
				return vo;
			}
			try {
				gameRoomCmdService.createGame(gameRoom.getServerGame().getGameId(), memberId, gameRoom.getGame(),
						Integer.valueOf(fb.getRenshu()), System.currentTimeMillis());
			} catch (Exception e) {
				vo.setSuccess(false);
				vo.setMsg(e.getClass().getName());
				return vo;
			}
			gameService.createGameRoom(gameRoom, memberId);
			Map data = new HashMap();
			data.put("httpUrl", gameRoom.getServerGame().getServer().getHttpUrl());
			data.put("wsUrl", gameRoom.getServerGame().getServer().getWsUrl());
			data.put("gameId", gameRoom.getServerGame().getGameId());
			data.put("token", resData.get("token"));
			data.put("game", gameRoom.getGame());
			vo.setData(data);
			doudizhuGameRoomMsgService.createGameRoom(gameRoom.getServerGame().getGameId(), gameRoom.getGame().name());
			return vo;
		}
		// 全部失败
		vo.setSuccess(false);
		vo.setMsg("NoServerAvailableForGameException");
		return vo;
	}

	/**
	 * 加入游戏
	 */
	@RequestMapping(value = "/join_game")
	public CommonVO joinGame(String token, String gameId, Game game) {
		CommonVO vo = new CommonVO();
		String memberId = memberAuthService.getMemberIdBySessionId(token);
		if (memberId == null) {
			vo.setSuccess(false);
			vo.setMsg("invalid token");
			return vo;
		}
		MemberLoginLimitRecord loginLimitRecord = memberLoginLimitRecordService.findByMemberId(memberId, true);
		if (loginLimitRecord != null) {
			vo.setSuccess(false);
			vo.setMsg("login limited");
			return vo;
		}
		MemberGoldAccountDbo account = memberGoldQueryService.findMemberGoldAccount(memberId);
		if (account == null || account.getBalance() < 1500) {
			vo.setSuccess(false);
			vo.setMsg("gold insufficient");
			return vo;
		}
		GameRoom gameRoom = gameService.findGameRoomByGame(game, gameId);
		if (gameRoom != null && !gameRoom.isFull()) {
			// 处理如果是自己暂时离开的房间
			String serverGameId = "";
			serverGameId = gameRoom.getServerGame().getGameId();
			MemberGameRoom memberGameRoom = gameService.findMemberGameRoomByGameAndMemberId(gameRoom.getGame(),
					serverGameId, memberId);
			if (memberGameRoom != null) {
				// 游戏服务器rpc返回房间
				GameServer gameServer = gameRoom.getServerGame().getServer();
				Request req = httpClient.newRequest(gameServer.getHttpUrl() + "/game/backtogame");
				req.param("playerId", memberId);
				req.param("gameId", serverGameId);
				Map resData = null;
				try {
					ContentResponse res = req.send();
					String resJson = new String(res.getContent());
					CommonVO resVo = gson.fromJson(resJson, CommonVO.class);
					if (resVo.isSuccess()) {
						resData = (Map) resVo.getData();
					} else {
						vo.setSuccess(false);
						vo.setMsg(resVo.getMsg());
						return vo;
					}
				} catch (Exception e) {
					vo.setSuccess(false);
					vo.setMsg("SysException");
					return vo;
				}
				Map data = new HashMap();
				data.put("httpUrl", gameRoom.getServerGame().getServer().getHttpUrl());
				data.put("wsUrl", gameRoom.getServerGame().getServer().getWsUrl());
				data.put("token", resData.get("token"));
				data.put("gameId", serverGameId);
				data.put("game", gameRoom.getGame());
				vo.setData(data);
				return vo;
			} else {
				// 游戏服务器rpc加入房间
				GameServer gameServer = gameRoom.getServerGame().getServer();
				Request req = httpClient.newRequest(gameServer.getHttpUrl() + "/game/joingame");
				req.param("playerId", memberId);
				req.param("gameId", serverGameId);
				Map resData = null;
				try {
					ContentResponse res = req.send();
					String resJson = new String(res.getContent());
					CommonVO resVo = gson.fromJson(resJson, CommonVO.class);
					if (resVo.isSuccess()) {
						resData = (Map) resVo.getData();
					} else {
						vo.setSuccess(false);
						vo.setMsg(resVo.getMsg());
						return vo;
					}
				} catch (Exception e) {
					vo.setSuccess(false);
					vo.setMsg("SysException");
					return vo;
				}
				try {
					gameRoomCmdService.joinGame(serverGameId, memberId);
				} catch (Exception e) {
					vo.setSuccess(false);
					vo.setMsg(e.getClass().getName());
					return vo;
				}
				gameService.joinGameRoom(gameRoom, memberId);

				Map data = new HashMap();
				data.put("httpUrl", gameRoom.getServerGame().getServer().getHttpUrl());
				data.put("wsUrl", gameRoom.getServerGame().getServer().getWsUrl());
				data.put("token", resData.get("token"));
				data.put("gameId", serverGameId);
				data.put("game", gameRoom.getGame());
				vo.setData(data);
				return vo;
			}
		}
		vo.setSuccess(false);
		vo.setMsg("GameRoomNotFoundException");
		return vo;
	}

	/**
	 * 后台rpc查询会员游戏房间
	 * 
	 * @return
	 */
	@RequestMapping(value = "/query_memberplayingroom")
	public CommonVO queryMemberPlayingRoom(@RequestParam(defaultValue = "1") int page,
			@RequestParam(defaultValue = "10") int size) {
		CommonVO vo = new CommonVO();
		List<MemberGameRoom> roomList = gameService.findPlayingMemberGameRoom(page, size);
		List<MemberPlayingRoomVO> gameRoomList = new ArrayList<>();
		roomList.forEach((memberGameRoom) -> {
			gameRoomList.add(new MemberPlayingRoomVO(memberGameRoom));
		});
		vo.setMsg("room list");
		vo.setData(gameRoomList);
		return vo;
	}

	/**
	 * 房间到时定时器，每1小时
	 */
	@Scheduled(cron = "0 0 0/1 * * ?")
	public void removeGameRoom() {
		long deadlineTime = System.currentTimeMillis();
		List<GameRoom> roomList = gameService.findExpireGameRoom(deadlineTime);
		Map<Game, List<String>> gameIdMap = new HashMap<>();
		for (Game game : Game.values()) {
			gameIdMap.put(game, new ArrayList<>());
		}
		for (GameRoom room : roomList) {
			Game game = room.getGame();
			String serverGameId = room.getServerGame().getGameId();
			gameIdMap.get(game).add(serverGameId);
		}
		ruianGameRoomMsgService.removeGameRoom(gameIdMap.get(Game.ruianMajiang));
		fangpaoGameRoomMsgService.removeGameRoom(gameIdMap.get(Game.fangpaoMajiang));
		wenzhouGameRoomMsgService.removeGameRoom(gameIdMap.get(Game.wenzhouMajiang));
		dianpaoGameRoomMsgService.removeGameRoom(gameIdMap.get(Game.dianpaoMajiang));
		wenzhouShuangkouGameRoomMsgService.removeGameRoom(gameIdMap.get(Game.wenzhouShuangkou));
		doudizhuGameRoomMsgService.removeGameRoom(gameIdMap.get(Game.doudizhu));
		chayuanShuangkouGameRoomMsgService.removeGameRoom(gameIdMap.get(Game.chayuanShuangkou));
	}
}
