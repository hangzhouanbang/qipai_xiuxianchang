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
			gameRoom.setPlayersCount(2);
		} else if (lawNames.contains("sanr")) {
			gameRoom.setPlayersCount(3);
		} else if (lawNames.contains("sir")) {
			gameRoom.setPlayersCount(4);
		} else {
			gameRoom.setPlayersCount(4);
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
			gameRoom.setPlayersCount(2);
		} else if (lawNames.contains("sanr")) {
			gameRoom.setPlayersCount(3);
		} else if (lawNames.contains("sir")) {
			gameRoom.setPlayersCount(4);
		} else {
			gameRoom.setPlayersCount(4);
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
			gameRoom.setPlayersCount(2);
		} else if (lawNames.contains("sanr")) {
			gameRoom.setPlayersCount(3);
		} else if (lawNames.contains("sir")) {
			gameRoom.setPlayersCount(4);
		} else {
			gameRoom.setPlayersCount(4);
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
			gameRoom.setPlayersCount(2);
		} else if (lawNames.contains("sanr")) {
			gameRoom.setPlayersCount(3);
		} else if (lawNames.contains("sir")) {
			gameRoom.setPlayersCount(4);
		} else {
			gameRoom.setPlayersCount(4);
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
			gameRoom.setPlayersCount(2);
		} else if (lawNames.contains("sanr")) {
			gameRoom.setPlayersCount(3);
		} else if (lawNames.contains("sir")) {
			gameRoom.setPlayersCount(4);
		} else {
			gameRoom.setPlayersCount(4);
		}

		gameRoom.setDeadlineTime(System.currentTimeMillis() + 2 * 60 * 60 * 1000);
		gameRoom.setGame(Game.wenzhouShuangkou);

		gameRoom.setCreateTime(System.currentTimeMillis());
		return gameRoom;
	}

	public void onlineServer(GameServer gameServer) {
		gameServer.setOnlineTime(System.currentTimeMillis());
		gameServerDao.save(gameServer);
	}

	public void offlineServer(String[] gameServerIds) {
		gameServerDao.remove(gameServerIds);
	}

	public void saveGameRoom(GameRoom gameRoom) {
		gameRoomDao.save(gameRoom);
	}

	public GameRoom findNotFullGameRoom(Game game) {
		return gameRoomDao.findNotFullGameRoom(game);
	}

	public void finishGameRoom(String id) {
		gameRoomDao.updateGameRoomFinished(id, true);
	}

	public void expireGameRoom(List<String> ids) {
		gameRoomDao.updateGameRoomFinished(ids, true);
	}

	public void createGameRoom(GameRoom gameRoom) {
		gameRoomDao.save(gameRoom);
		MemberGameRoom mgr = new MemberGameRoom();
		mgr.setGameRoom(gameRoom);
		memberGameRoomDao.save(mgr);
	}

	public void joinGameRoom(GameRoom gameRoom, String memberId) {
		if (gameRoom.getPanCountPerJu() > gameRoom.getPlayersCount() + 1) {
			gameRoomDao.updateGameRoomPlayerCountAndFull(gameRoom.getId(), gameRoom.getPlayersCount() + 1, false);
		} else {
			gameRoomDao.updateGameRoomPlayerCountAndFull(gameRoom.getId(), gameRoom.getPlayersCount() + 1, true);
		}
		MemberGameRoom mgr = new MemberGameRoom();
		mgr.setGameRoom(gameRoom);
		mgr.setMemberId(memberId);
		memberGameRoomDao.save(mgr);
	}

	public List<GameRoom> findExpireGameRoom(long deadlineTime) {
		return gameRoomDao.findExpireGameRoom(deadlineTime, false);
	}

	public void finishMemberGameRoom(Game game, String serverGameId) {
		memberGameRoomDao.remove(game, serverGameId);
	}

	public void removeMemberGameRoomByMemberId(Game game, String serverGameId, String memberId) {
		memberGameRoomDao.remove(game, serverGameId, memberId);
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
