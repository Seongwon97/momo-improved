package com.woowacourse.momo.group.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

import static com.woowacourse.momo.fixture.GroupFixture.MOMO_STUDY;
import static com.woowacourse.momo.fixture.MemberFixture.MOMO;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.jdbc.Sql;

import lombok.RequiredArgsConstructor;

import com.woowacourse.momo.auth.support.SHA256Encoder;
import com.woowacourse.momo.group.domain.GroupRepository;
import com.woowacourse.momo.member.domain.Member;
import com.woowacourse.momo.member.domain.MemberRepository;
import com.woowacourse.momo.member.domain.Password;
import com.woowacourse.momo.member.domain.UserId;
import com.woowacourse.momo.member.domain.UserName;

@Sql(value = "classpath:init.sql", executionPhase = BEFORE_TEST_METHOD)
@Sql(value = "classpath:truncate.sql", executionPhase = AFTER_TEST_METHOD)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
@SpringBootTest
class ParticipateServiceConcurrencyTest {

    private final ParticipateService participateService;
    private final GroupRepository groupRepository;
    private final MemberRepository memberRepository;

    private Member host;

    @BeforeEach
    void setUp() {
        this.host = memberRepository.save(MOMO.toMember());
    }

    @DisplayName("모임 참여 동시 요청이 올 경우에도 정원을 넘어선 인원이 모임에 참여할 수 없다")
    @Test
    void participateConcurrencyTest() throws InterruptedException {
        int capacity = 3;
        int numOfParticipants = 50;
        long groupId = groupRepository.save(
                MOMO_STUDY.builder()
                        .capacity(capacity)
                        .toGroup(host)
        ).getId();

        List<Long> participantIds = new ArrayList<>();
        for (int i = 0; i < numOfParticipants; i++) {
            Member savedMember = memberRepository.save(
                    new Member(UserId.momo("user" + i),
                            Password.encrypt("User123!", new SHA256Encoder()),
                            UserName.from("user" + i)));
            participantIds.add(savedMember.getId());
        }

        CountDownLatch latch = new CountDownLatch(numOfParticipants);
        ExecutorService executor = Executors.newFixedThreadPool(numOfParticipants);

        for (Long participantId : participantIds) {
            executor.submit(() -> {
                try {
                    participateService.participate(groupId, participantId);
                } finally {
                    latch.countDown();
                }
            });
        }

        executor.shutdown();
        latch.await();

        long actual = participateService.findParticipants(groupId).size();

        assertThat(actual).isEqualTo(capacity);
    }
}
