package com.anbang.qipai.xiuxianchang.remote.service;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.anbang.qipai.xiuxianchang.remote.vo.CommonRemoteVO;

/**
 * 会员中心远程服务
 * 
 * @author neo
 *
 */
@FeignClient("qipai-members")
public interface QipaiMembersRemoteService {

	@RequestMapping(value = "/auth/trytoken")
	public CommonRemoteVO auth_trytoken(@RequestParam(value = "token") String token);

}
