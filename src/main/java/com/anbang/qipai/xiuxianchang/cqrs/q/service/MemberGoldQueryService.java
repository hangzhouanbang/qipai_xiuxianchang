package com.anbang.qipai.xiuxianchang.cqrs.q.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.anbang.qipai.xiuxianchang.cqrs.c.domain.gold.CreateGoldAccountResult;
import com.anbang.qipai.xiuxianchang.cqrs.q.dao.MemberGoldAccountDboDao;
import com.anbang.qipai.xiuxianchang.cqrs.q.dao.MemberGoldRecordDboDao;
import com.anbang.qipai.xiuxianchang.cqrs.q.dbo.MemberGoldAccountDbo;
import com.anbang.qipai.xiuxianchang.cqrs.q.dbo.MemberGoldRecordDbo;
import com.anbang.qipai.xiuxianchang.web.vo.RecordSummaryTexts;
import com.dml.accounting.AccountingRecord;
import com.dml.accounting.TextAccountingSummary;
import com.highto.framework.web.page.ListPage;

@Component
public class MemberGoldQueryService {

	@Autowired
	private MemberGoldRecordDboDao memberGoldRecordDboDao;

	@Autowired
	private MemberGoldAccountDboDao memberGoldAccountDboDao;

	/**
	 * 新建玩家金币账户
	 */
	public void createMemberGoldAccount(CreateGoldAccountResult createGoldAccountResult) {
		MemberGoldAccountDbo account = new MemberGoldAccountDbo();
		account.setId(createGoldAccountResult.getAccountId());
		account.setMemberId(createGoldAccountResult.getMemberId());
		memberGoldAccountDboDao.save(account);

		AccountingRecord accountingRecord = createGoldAccountResult.getAccountingRecordForGiveGold();
		MemberGoldRecordDbo dbo = new MemberGoldRecordDbo();
		dbo.setMemberId(createGoldAccountResult.getMemberId());
		dbo.setAccountId(accountingRecord.getAccountId());
		dbo.setAccountingAmount((int) accountingRecord.getAccountingAmount());
		dbo.setAccountingNo(accountingRecord.getAccountingNo());
		dbo.setBalanceAfter((int) accountingRecord.getBalanceAfter());
		dbo.setSummary(accountingRecord.getSummary());
		dbo.setAccountingTime(accountingRecord.getAccountingTime());
		memberGoldRecordDboDao.save(dbo);

		memberGoldAccountDboDao.update(accountingRecord.getAccountId(), (int) accountingRecord.getBalanceAfter());
	}

	/**
	 * 查询玩家金币账户
	 */
	public MemberGoldAccountDbo findMemberGoldAccount(String memberId) {
		return memberGoldAccountDboDao.findByMemberId(memberId);
	}

	/**
	 * 查询玩家金币流水
	 */
	public ListPage findMemberGoldRecords(int page, int size, String memberId) {
		List<MemberGoldRecordDbo> recordList = memberGoldRecordDboDao.findMemberGoldRecordByMemberId(memberId, page,
				size);
		for (int i = 0; i < recordList.size(); i++) {
			TextAccountingSummary summary = (TextAccountingSummary) recordList.get(i).getSummary();
			TextAccountingSummary newSummary = new TextAccountingSummary(
					RecordSummaryTexts.getSummaryText(summary.getText()));
			recordList.get(i).setSummary(newSummary);
		}
		long amount = memberGoldRecordDboDao.getCountByMemberId(memberId);
		ListPage listPage = new ListPage(recordList, page, size, (int) amount);
		return listPage;
	}

	/**
	 * 记录金币流水
	 */
	public MemberGoldRecordDbo withdraw(String memberId, AccountingRecord accountingRecord) {

		MemberGoldRecordDbo dbo = new MemberGoldRecordDbo();
		dbo.setMemberId(memberId);
		dbo.setAccountId(accountingRecord.getAccountId());
		dbo.setAccountingAmount((int) accountingRecord.getAccountingAmount());
		dbo.setAccountingNo(accountingRecord.getAccountingNo());
		dbo.setBalanceAfter((int) accountingRecord.getBalanceAfter());
		dbo.setSummary(accountingRecord.getSummary());
		dbo.setAccountingTime(accountingRecord.getAccountingTime());
		memberGoldRecordDboDao.save(dbo);

		memberGoldAccountDboDao.update(accountingRecord.getAccountId(), (int) accountingRecord.getBalanceAfter());
		return dbo;
	}
}
