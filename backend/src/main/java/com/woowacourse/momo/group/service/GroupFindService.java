package com.woowacourse.momo.group.service;

import static com.woowacourse.momo.group.exception.GroupErrorCode.NOT_EXIST;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.woowacourse.momo.group.domain.Group;
import com.woowacourse.momo.group.domain.participant.ParticipantRepository;
import com.woowacourse.momo.group.domain.search.GroupSearchRepository;
import com.woowacourse.momo.group.domain.search.SearchCondition;
import com.woowacourse.momo.group.domain.search.dto.GroupSummaryRepositoryResponse;
import com.woowacourse.momo.group.domain.search.dto.GroupSummaryRepositoryResponses;
import com.woowacourse.momo.group.exception.GroupException;
import com.woowacourse.momo.group.service.dto.response.CachedGroupResponse;
import com.woowacourse.momo.group.service.dto.response.GroupResponseAssembler;
import com.woowacourse.momo.member.domain.Member;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class GroupFindService {

    private final GroupSearchRepository groupSearchRepository;
    private final ParticipantRepository participantRepository;

    public Group findGroup(Long id) {
        return groupSearchRepository.findById(id)
                .orElseThrow(() -> new GroupException(NOT_EXIST));
    }

    public Group findByIdForUpdate(Long id) {
        return groupSearchRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new GroupException(NOT_EXIST));
    }

    public List<Group> findParticipatedGroups(Member member) {
        List<Long> participatedGroupIds = participantRepository.findGroupIdWhichParticipated(member.getId());
        return groupSearchRepository.findParticipatedGroups(member, participatedGroupIds);
    }

    @Cacheable(value = "Groups", cacheManager = "cacheManager")
    public GroupSummaryRepositoryResponses findGroups(SearchCondition condition, Pageable pageable) {
        Page<GroupSummaryRepositoryResponse> groups = groupSearchRepository.findGroups(condition, pageable);

        return new GroupSummaryRepositoryResponses(groups);
    }

    @Cacheable(value = "Group", key = "#id", cacheManager = "cacheManager")
    public CachedGroupResponse findByIdWithHostAndSchedule(Long id) {
        Group group = groupSearchRepository.findByIdWithHostAndSchedule(id)
                .orElseThrow(() -> new GroupException(NOT_EXIST));

        return GroupResponseAssembler.cachedGroupResponse(group);
    }
}
