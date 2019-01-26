package com.anbang.qipai.xiuxianchang.web.vo;

public enum RecordSummaryTexts {
	新玩家注册, 购买会员卡赠送, 管理员赠送金币, 管理员赠送积分, 邮件奖励, 任务奖励, 创建房间, 加入房间, 离开房间, 绑定邀请码, 发送俏皮话;

	public static String getSummaryText(String text) {
		switch (text) {
		case "new member":
			return 新玩家注册.name();
		case "give for buy clubcard":
			return 购买会员卡赠送.name();
		case "admin_give_gold":
			return 管理员赠送金币.name();
		case "admin_give_score":
			return 管理员赠送积分.name();
		case "mail_reward":
			return 邮件奖励.name();
		case "task_reward":
			return 任务奖励.name();
		case "pay for create room":
			return 创建房间.name();
		case "pay for join room":
			return 加入房间.name();
		case "back gold to leave game":
			return 离开房间.name();
		case "back gold to leave home":
			return 离开房间.name();
		case "bind invitioncode":
			return 绑定邀请码.name();
		case "wisecrack":
			return 发送俏皮话.name();
		default:
			return text;
		}
	}
}
