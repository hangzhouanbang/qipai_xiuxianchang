package com.anbang.qipai.xiuxianchang.web.vo;

public enum RecordSummaryTexts {
	新玩家注册, 休闲场结算, 玩家强退结算, 任务奖励;

	public static String getSummaryText(String text) {
		switch (text) {
		case "new member":
			return 新玩家注册.name();
		case "xiuxianchang ju result":
			return 休闲场结算.name();
		case "xiuxianchang game cancle":
			return 玩家强退结算.name();
		case "task_reward":
			return 任务奖励.name();
		default:
			return text;
		}
	}
}
