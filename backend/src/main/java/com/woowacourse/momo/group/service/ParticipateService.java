package com.woowacourse.momo.group.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.woowacourse.momo.group.domain.Group;
import com.woowacourse.momo.member.domain.Member;
import com.woowacourse.momo.member.service.MemberFindService;
import com.woowacourse.momo.member.service.dto.response.MemberResponse;
import com.woowacourse.momo.member.service.dto.response.MemberResponseAssembler;
import com.woowacourse.momo.support.distributionlock.DistributionLock;

@RequiredArgsConstructor
@Service
public class ParticipateService {

    private final MemberFindService memberFindService;
    private final GroupFindService groupFindService;

    @DistributionLock(key = "#groupId")
    public void participate(Long groupId, Long memberId) {
        Group group = groupFindService.findGroup(groupId);
        Member member = memberFindService.findMember(memberId);

        group.participate(member);
    }

    @Transactional(readOnly = true)
    public List<MemberResponse> findParticipants(Long groupId) {
        Group group = groupFindService.findGroup(groupId);
        List<Member> participants = group.getParticipants();

        return MemberResponseAssembler.memberResponses(participants);
    }

    @Transactional
    public void leave(Long groupId, Long memberId) {
        Group group = groupFindService.findGroup(groupId);
        Member participant = memberFindService.findMember(memberId);

        group.remove(participant);
    }
}
