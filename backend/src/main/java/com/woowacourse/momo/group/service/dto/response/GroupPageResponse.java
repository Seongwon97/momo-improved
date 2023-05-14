package com.woowacourse.momo.group.service.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GroupPageResponse {

    boolean hasNextPage;
    int pageNumber;
    List<GroupSummaryResponse> groups;
}
