package com.woowacourse.momo.group.domain.search;

import org.springframework.data.domain.Pageable;

import com.woowacourse.momo.group.domain.search.dto.GroupSummaryRepositoryResponses;

public interface GroupSearchRepositoryCustom {

    GroupSummaryRepositoryResponses findGroups(SearchCondition condition, Pageable pageable);

    GroupSummaryRepositoryResponses findHostedGroups(SearchCondition condition, Long memberId, Pageable pageable);

    GroupSummaryRepositoryResponses findParticipatedGroups(SearchCondition condition, Long memberId,
                                                           Pageable pageable);

    GroupSummaryRepositoryResponses findLikedGroups(SearchCondition condition, Long memberId, Pageable pageable);
}
