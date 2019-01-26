package com.anbang.qipai.xiuxianchang.plan.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.anbang.qipai.xiuxianchang.plan.bean.Game;
import com.anbang.qipai.xiuxianchang.plan.bean.GameLaw;
import com.anbang.qipai.xiuxianchang.plan.bean.GameRoom;
import com.anbang.qipai.xiuxianchang.plan.bean.GameServer;
import com.anbang.qipai.xiuxianchang.plan.bean.IllegalGameLawsException;
import com.anbang.qipai.xiuxianchang.plan.bean.LawsMutexGroup;
import com.anbang.qipai.xiuxianchang.plan.bean.MemberGameRoom;
import com.anbang.qipai.xiuxianchang.plan.bean.NoServerAvailableForGameException;
import com.anbang.qipai.xiuxianchang.plan.bean.ServerGame;
import com.anbang.qipai.xiuxianchang.plan.dao.GameLawDao;
import com.anbang.qipai.xiuxianchang.plan.dao.GameRoomDao;
import com.anbang.qipai.xiuxianchang.plan.dao.GameServerDao;
import com.anbang.qipai.xiuxianchang.plan.dao.LawsMutexGroupDao;
import com.anbang.qipai.xiuxianchang.plan.dao.MemberGameRoomDao;

@Service
public class GameService {

	@Autowired
	private GameLawDao gameLawDao;

	@Autowired
	private GameServerDao gameServerDao;

	@Autowired
	private GameRoomDao gameRoomDao;

	@Autowired
	private MemberGameRoomDao memberGameRoomDao;

	@Autowired
	private LawsMutexGroupDao lawsMutexGroupDao;

	/**
	 * 创建瑞安麻将房间
	 */
	public GameRoom buildRamjGameRoom(String memberId, List<String> lawNames)
			throws NoServerAvailableForGameException, IllegalGameLawsException {
		List<GameServer> allServers = gameServerDao.findByGame(Game.ruianMajiang);
		if (allServers == null || allServers.isEmpty()) {
			throw new NoServerAvailableForGameException();
		}
		Random r = new Random();
		GameServer gameServer = allServers.get(r.nextInt(allServers.size()));
		ServerGame serverGame = new ServerGame();
		serverGame.setServer(gameServer);
		GameRoom gameRoom = new GameRoom();
		gameRoom.setServerGame(serverGame);

		List<GameLaw> laws = new ArrayList<>();
		lawNames.forEach((name) -> laws.add(gameLawDao.findByGameAndName(Game.ruianMajiang, name)));
		gameRoom.setLaws(laws);
		if (!gameRoom.validateLaws()) {
			throw new IllegalGameLawsException();
		}

		if (lawNames.contains("er")) {
			gameRoom.setPlayersCountPerJu(2);
		} else if (lawNames.contains("sanr")) {
			gameRoom.setPlayersCountPerJu(3);
		} else if (lawNames.contains("sir")) {
			gameRoom.setPlayersCountPerJu(4);
		} else {
			gameRoom.setPlayersCountPerJu(4);
		}

		gameRoom.setDeadlineTime(System.currentTimeMillis() + 2 * 60 * 60 * 1000);
		gameRoom.setGame(Game.ruianMajiang);

		gameRoom.setCreateTime(System.currentTimeMillis());
		return gameRoom;
	}

	/**
	 * 创建放炮麻将房间
	 */
	public GameRoom buildFpmjGameRoom(String memberId, List<String> lawNames)
			throws NoServerAvailableForGameException, IllegalGameLawsException {
		List<GameServer> allServers = gameServerDao.findByGame(Game.fangpaoMajiang);
		if (allServers == null || allServers.isEmpty()) {
			throw new NoServerAvailableForGameException();
		}
		Random r = new Random();
		GameServer gameServer = allServers.get(r.nextInt(allServers.size()));
		ServerGame serverGame = new ServerGame();
		serverGame.setServer(gameServer);
		GameRoom gameRoom = new GameRoom();
		gameRoom.setServerGame(serverGame);

		List<GameLaw> laws = new ArrayList<>();
		lawNames.forEach((name) -> laws.add(gameLawDao.findByGameAndName(Game.fangpaoMajiang, name)));
		gameRoom.setLaws(laws);
		if (!gameRoom.validateLaws()) {
			throw new IllegalGameLawsException();
		}

		if (lawNames.contains("er")) {
			gameRoom.setPlayersCountPerJu(2);
		} else if (lawNames.contains("sanr")) {
			gameRoom.setPlayersCountPerJu(3);
		} else if (lawNames.contains("sir")) {
			gameRoom.setPlayersCountPerJu(4);
		} else {
			gameRoom.setPlayersCountPerJu(4);
		}

		gameRoom.setDeadlineTime(System.currentTimeMillis() + 2 * 60 * 60 * 1000);
		gameRoom.setGame(Game.fangpaoMajiang);

		gameRoom.setCreateTime(System.currentTimeMillis());
		return gameRoom;
	}

	/**
	 * 创建温州麻将房间
	 */
	public GameRoom buildWzmjGameRoom(String memberId, List<String> lawNames)
			throws NoServerAvailableForGameException, IllegalGameLawsException {
		List<GameServer> allServers = gameServerDao.findByGame(Game.wenzhouMajiang);
		if (allServers == null || allServers.isEmpty()) {
			throw new NoServerAvailableForGameException();
		}
		Random r = new Random();
		GameServer gameServer = allServers.get(r.nextInt(allServers.size()));
		ServerGame serverGame = new ServerGame();
		serverGame.setServer(gameServer);
		GameRoom gameRoom = new GameRoom();
		gameRoom.setServerGame(serverGame);

		List<GameLaw> laws = new ArrayList<>();
		lawNames.forEach((name) -> laws.add(gameLawDao.findByGameAndName(Game.wenzhouMajiang, name)));
		gameRoom.setLaws(laws);
		if (!gameRoom.validateLaws()) {
			throw new IllegalGameLawsException();
		}

		if (lawNames.contains("er")) {
			gameRoom.setPlayersCountPerJu(2);
		} else if (lawNames.contains("sanr")) {
			gameRoom.setPlayersCountPerJu(3);
		} else if (lawNames.contains("sir")) {
			gameRoom.setPlayersCountPerJu(4);
		} else {
			gameRoom.setPlayersCountPerJu(4);
		}

		gameRoom.setDeadlineTime(System.currentTimeMillis() + 2 * 60 * 60 * 1000);
		gameRoom.setGame(Game.wenzhouMajiang);

		gameRoom.setCreateTime(System.currentTimeMillis());
		return gameRoom;
	}

	/**
	 * 创建点炮麻将房间
	 */
	public GameRoom buildDpmjGameRoom(String memberId, List<String> lawNames)
			throws NoServerAvailableForGameException, IllegalGameLawsException {
		List<GameServer> allServers = gameServerDao.findByGame(Game.dianpaoMajiang);
		if (allServers == null || allServers.isEmpty()) {
			throw new NoServerAvailableForGameException();
		}
		Random r = new Random();
		GameServer gameServer = allServers.get(r.nextInt(allServers.size()));
		ServerGame serverGame = new ServerGame();
		serverGame.setServer(gameServer);
		GameRoom gameRoom = new GameRoom();
		gameRoom.setServerGame(serverGame);

		List<GameLaw> laws = new ArrayList<>();
		lawNames.forEach((name) -> laws.add(gameLawDao.findByGameAndName(Game.dianpaoMajiang, name)));
		gameRoom.setLaws(laws);
		if (!gameRoom.validateLaws()) {
			throw new IllegalGameLawsException();
		}

		if (lawNames.contains("er")) {
			gameRoom.setPlayersCountPerJu(2);
		} else if (lawNames.contains("sanr")) {
			gameRoom.setPlayersCountPerJu(3);
		} else if (lawNames.contains("sir")) {
			gameRoom.setPlayersCountPerJu(4);
		} else {
			gameRoom.setPlayersCountPerJu(4);
		}

		gameRoom.setDeadlineTime(System.currentTimeMillis() + 2 * 60 * 60 * 1000);
		gameRoom.setGame(Game.dianpaoMajiang);

		gameRoom.setCreateTime(System.currentTimeMillis());
		return gameRoom;
	}

	/**
	 * 创建温州双扣房间
	 */
	public GameRoom buildWzskGameRoom(String memberId, List<String> lawNames)
			throws NoServerAvailableForGameException, IllegalGameLawsException {
		List<GameServer> allServers = gameServerDao.findByGame(Game.wenzhouShuangkou);
		if (allServers == null || allServers.isEmpty()) {
			throw new NoServerAvailableForGameException();
		}
		Random r = new Random();
		GameServer gameServer = allServers.get(r.nextInt(allServers.size()));
		ServerGame serverGame = new ServerGame();
		serverGame.setServer(gameServer);
		GameRoom gameRoom = new GameRoom();
		gameRoom.setServerGame(serverGame);

		List<GameLaw> laws = new ArrayList<>();
		lawNames.forEach((name) -> laws.add(gameLawDao.findByGameAndName(Game.wenzhouShuangkou, name)));
		gameRoom.setLaws(laws);
		if (!gameRoom.validateLaws()) {
			throw new IllegalGameLawsException();
		}

		if (lawNames.contains("er")) {
			gameRoom.setPlayersCountPerJu(2);
		} else if (lawNames.contains("sanr")) {
			gameRoom.setPlayersCountPerJu(3);
		} else if (lawNames.contains("sir")) {
			gameRoom.setPlayersCountPerJu(4);
		} else {
			gameRoom.setPlayersCountPerJu(4);
		}

		gameRoom.setDeadlineTime(System.currentTimeMillis() + 2 * 60 * 60 * 1000);
		gameRoom.setGame(Game.wenzhouShuangkou);

		gameRoom.setCreateTime(System.currentTimeMillis());
		return gameRoom;
	}

	/**
	 * 服务器上线
	 */
	public void onlineServer(GameServer gameServer) {
		gameServer.setOnlineTime(System.currentTimeMillis());
		gameServerDao.save(gameServer);
	}

	/**
	 * 服务器下线
	 */
	public void offlineServer(String[] gameServerIds) {
		gameServerDao.remove(gameServerIds);
	}

	/**
	 * 保存游戏房间
	 */
	public void saveGameRoom(GameRoom gameRoom) {
		gameRoomDao.save(gameRoom);
	}

	/**
	 * 查询未满的游戏房间
	 */
	public GameRoom findNotFullGameRoom(Game game) {
		return gameRoomDao.findNotFullGameRoom(game);
	}

	/**
	 * 批量终结游戏房间
	 */
	public void expireGameRoom(List<String> ids) {
		gameRoomDao.updateGameRoomFinished(ids, true);
	}

	public void updateGameRoomFinishedByGame(Game game, String serverGameId, boolean finished) {
		gameRoomDao.updateGameRoomFinishedByGame(game, serverGameId, finished);
	}

	/**
	 * 创建游戏房间
	 */
	public void createGameRoom(GameRoom gameRoom, String memberId) {
		MemberGameRoom mgr = new MemberGameRoom();
		mgr.setMemberId(memberId);
		mgr.setGameRoom(gameRoom);
		memberGameRoomDao.save(mgr);

		gameRoomDao.updateGameRoomPlayersCountAndFull(gameRoom.getId(), gameRoom.getPlayersCount() + 1, false);
	}

	/**
	 * 加入游戏房间
	 */
	public void joinGameRoom(GameRoom gameRoom, String memberId) {
		if (gameRoom.getPlayersCountPerJu() > gameRoom.getPlayersCount() + 1) {
			gameRoomDao.updateGameRoomPlayersCountAndFull(gameRoom.getId(), gameRoom.getPlayersCount() + 1, false);
		} else {
			gameRoomDao.updateGameRoomPlayersCountAndFull(gameRoom.getId(), gameRoom.getPlayersCount() + 1, true);
		}
		MemberGameRoom mgr = new MemberGameRoom();
		mgr.setGameRoom(gameRoom);
		mgr.setMemberId(memberId);
		memberGameRoomDao.save(mgr);
	}

	/**
	 * 离开游戏房间
	 */
	public void leaveGameRoom(Game game, String serverGameId, String memberId) {
		GameRoom gameRoom = gameRoomDao.findGameRoomByGame(game, serverGameId);
		gameRoomDao.updateGameRoomPlayersCountAndFull(gameRoom.getId(), gameRoom.getPlayersCount() - 1, false);
		memberGameRoomDao.remove(game, serverGameId, memberId);
	}

	/**
	 * 查询到期的游戏房间
	 */
	public List<GameRoom> findExpireGameRoom(long deadlineTime) {
		return gameRoomDao.findExpireGameRoom(deadlineTime, false);
	}

	/**
	 * 删除玩家游戏房间
	 */
	public void finishMemberGameRoom(Game game, String serverGameId) {
		memberGameRoomDao.remove(game, serverGameId);
	}

	public GameLaw findGameLaw(Game game, String lawName) {
		return gameLawDao.findByGameAndName(game, lawName);
	}

	public void createGameLaw(GameLaw law) {
		gameLawDao.save(law);
	}

	public void updateGameLaw(GameLaw law) {
		gameLawDao.update(law);
	}

	public void removeGameLaw(String lawId) {
		gameLawDao.remove(lawId);
	}

	public void addLawsMutexGroup(LawsMutexGroup lawsMutexGroup) {
		lawsMutexGroupDao.save(lawsMutexGroup);
	}

	public void removeLawsMutexGroup(String groupId) {
		lawsMutexGroupDao.remove(groupId);
	}

}
