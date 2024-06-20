package io.hhplus.tdd.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.hhplus.tdd.point.PointHistory;
import io.hhplus.tdd.point.TransactionType;
import io.hhplus.tdd.point.UserPoint;
import io.hhplus.tdd.point.service.PointService;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
public class PointServiceTest {

//    private UserPointRepository userPointRepository = Mockito.mock(UserPointRepository.class);
//    private PointHistoryRepository pointHistoryRepository = Mockito.mock(PointHistoryRepository.class);
//    private PointService pointService = Mockito.mock(PointService.class);
//    private PointService pointService = new PointService(userPointRepository, pointHistoryRepository);
//    @MockBean
//    private UserPointRepository userPointRepository;
//    @MockBean
//    private PointHistoryRepository pointHistoryRepository;
//    @InjectMocks
//    private PointService pointService = Mockito.mock(PointService.class);
    @Autowired
    private PointService pointService;


//    @DisplayName("유저 포인트 조회 서비스 테스트")
//    @Test
//    public void getUserPointTest() {
//        // given
//        long id = 1L;
//        when(pointService.getUserPoint(id)).thenReturn(new UserPoint(id, 10000L, 0));
//        // when
//        UserPoint userPoint = pointService.getUserPoint(id);
//        System.out.println(userPoint);
//        // then
//        assertThat(userPoint.id()).isEqualTo(id);
//        assertThat(userPoint.point()).isEqualTo(10000L);
//        assertThat(userPoint.updateMillis()).isEqualTo(0);
//    }
//
//    @DisplayName("유저 포인트 이용/충전 내역 조회 서비스 테스트")
//    @Test
//    public void getUserPointHistoryTest() {
//        // given
//        long id = 1L;
//        List<PointHistory> pointHistoryList = new ArrayList<>();
//        pointHistoryList.add(new PointHistory(1L, id, 10000L, TransactionType.CHARGE, 0));
//        pointHistoryList.add(new PointHistory(2L, id, 20000L, TransactionType.CHARGE, 0));
//        pointHistoryList.add(new PointHistory(3L, id, 30000L, TransactionType.CHARGE, 0));
//        when(pointService.getUserPointHistory(id)).thenReturn(pointHistoryList);
//        // when
//        List<PointHistory> result = pointService.getUserPointHistory(id);
//        // then
//        assertThat(result.get(0).userId()).isEqualTo(1L);
//        assertThat(result.get(2).amount()).isEqualTo(30000L);
//        assertThat(result).isEqualTo(pointHistoryList);
//    }
//
//    @DisplayName("유저 포인트 충전 서비스 테스트")
//    @Test
//    public void chargePointTest() {
//        // given
//        long id = 1L;
//        long amount = 10000L;
//        // 포인트 충전 및 히스토리 업데이트
//        when(pointService.chargePoint(id, amount)).thenReturn(new UserPoint(1L, 10000L, 0));
//        when(pointService.pointHistoryUpdate(id, amount, TransactionType.CHARGE)).thenReturn(new PointHistory(1L, 1L, 10000L, TransactionType.CHARGE, 0));
//        // when
//        UserPoint userPoint = pointService.chargePoint(id, amount);
//        PointHistory pointHistory = pointService.pointHistoryUpdate(id, amount, TransactionType.CHARGE);
//        // then
//        assertThat(userPoint.id()).isEqualTo(1L);
//        assertThat(userPoint.point()).isEqualTo(10000L);
//        // 히스토리가 존재 하는지
//        assertThat(pointHistory).isNotNull();
//        assertThat(pointHistory.amount()).isEqualTo(10000L);
//    }
//
//    @DisplayName("유저 포인트 사용 초과 에러 테스트")
//    @Test
//    public void usePointErrorTest() throws Exception {
//        // given
//        long id = 1L;
//        long amount = 2L;
//        when(pointService.getUserPoint(id)).thenReturn(new UserPoint(1L, 1L, 0));
//        // when, then
//        assertThrows(IllegalArgumentException.class, () -> {
//            if (pointService.getUserPoint(id).point() < amount) {
//                throw new IllegalArgumentException("포인트 없어!!");
//            }
//        });
//    }

    @DisplayName("동시성 테스트")
    @Test
    public void usePointTest() throws ExecutionException, InterruptedException {
        long id = 1L;

        // 포인트 100 충전
        pointService.chargePoint(id, 100L);

        // 쓰레드 리스트 생성
        CompletableFuture<UserPoint> future1 = CompletableFuture.supplyAsync(() -> {
            try {
                return pointService.usePoint(id, 30L);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        CompletableFuture<UserPoint> future2 = CompletableFuture.supplyAsync(() -> {
            try {
                return pointService.usePoint(id, 30L);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        CompletableFuture<UserPoint> future3 = CompletableFuture.supplyAsync(() -> {
            try {
                return pointService.usePoint(id, 30L);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        CompletableFuture<UserPoint> future4 = CompletableFuture.supplyAsync(() -> {
            try {
                return pointService.usePoint(id, 30L);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        List<CompletableFuture<UserPoint>> futures = List.of(future1, future2, future3, future4);

        CompletableFuture<List<UserPoint>> result = CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()]))
            .thenApply(v -> futures.stream().map(CompletableFuture::join)
                .collect(Collectors.toList()));

        // run
        result.get().forEach(System.out::println);
    }

}
