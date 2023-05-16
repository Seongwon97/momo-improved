package com.woowacourse.momo.group.service.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.woowacourse.momo.category.domain.Category;
import com.woowacourse.momo.member.service.dto.response.MemberResponse;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CachedGroupResponse {

    private long id;
    private String name;
    private MemberResponse host;
    private Category category;
    private int capacity;
    private DurationResponse duration;
    private List<ScheduleResponse> schedules;
    private boolean finished;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm", timezone = "Asia/Seoul")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime deadline;
    private LocationResponse location;
    private String description;
}
