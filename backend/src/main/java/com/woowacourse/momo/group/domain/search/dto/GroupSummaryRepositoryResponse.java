package com.woowacourse.momo.group.domain.search.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import lombok.Getter;
import lombok.NoArgsConstructor;

import com.woowacourse.momo.category.domain.Category;

@Getter
@NoArgsConstructor
public class GroupSummaryRepositoryResponse {

    private Long groupId;
    private String groupName;
    private Long hostId;
    private String hostName;
    private Category category;
    private int capacity;
    private int numOfParticipant;
    private boolean closedEarly;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime deadline;
    private String imageName;

    public GroupSummaryRepositoryResponse(Long groupId, String groupName, Long hostId, String hostName,
                                          Category category, int capacity, int numOfParticipant, boolean closedEarly,
                                          LocalDateTime deadline, String imageName) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.hostId = hostId;
        this.hostName = hostName;
        this.category = category;
        this.capacity = capacity;
        this.numOfParticipant = numOfParticipant;
        this.isClosedEarly = isClosedEarly;
        this.deadline = deadline;

        boolean defaultImage = isDefaultImage(imageName);
        this.imageName = getGroupImage(imageName, defaultImage);
    }

    private boolean isDefaultImage(String imageName) {
        return imageName == null || category.isDefaultImage(imageName);
    }

    private String getGroupImage(String imageName, boolean defaultImage) {
        if (defaultImage) {
            return category.getDefaultImageName();
        }
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }
}
