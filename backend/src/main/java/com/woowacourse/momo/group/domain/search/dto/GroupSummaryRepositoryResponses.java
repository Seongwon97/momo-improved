package com.woowacourse.momo.group.domain.search.dto;

import java.util.List;

import org.springframework.data.domain.Page;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GroupSummaryRepositoryResponses {

    private List<GroupSummaryRepositoryResponse> content;
    private boolean hasNext;

    public GroupSummaryRepositoryResponses(Page<GroupSummaryRepositoryResponse> responses) {
        this.content = responses.getContent();
        this.hasNext = responses.hasNext();
    }

    public boolean hasNext() {
        return hasNext;
    }
}
