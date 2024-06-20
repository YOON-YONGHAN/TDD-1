package io.hhplus.tdd.point.service;

import io.hhplus.tdd.point.PointHistory;
import io.hhplus.tdd.point.TransactionType;
import io.hhplus.tdd.point.UserPoint;
import io.hhplus.tdd.point.repository.PointHistoryRepository;
import io.hhplus.tdd.point.repository.UserPointRepository;
import java.util.List;
import lombok.AllArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PointService {

    private final UserPointRepository userPointRepository;
    private final PointHistoryRepository pointHistoryRepository;

    // 유저 포인트 조회
    public UserPoint getUserPoint(long id) {
        return userPointRepository.selectById(id);
    }

    // 유저 포인트 히스토리 조회
    public List<PointHistory> getUserPointHistory(long id) {
        return pointHistoryRepository.selectAllByUserId(id);
    }

    // 유저 포인트 충전
    public UserPoint chargePoint(long id, long amount) {

        // 포인트 충전
        UserPoint userPoint = userPointRepository.insertOrUpdate(id, amount);

        // 포인트 충전에 성공하면 포인트 히스토리 기록
        pointHistoryUpdate(id, amount, TransactionType.CHARGE);

        return userPoint;
    }

    // 유저 포인트 사용
    public UserPoint usePoint(long id, long amount) throws Exception {

        // 보유 포인트 확인
        UserPoint userPoint = userPointRepository.selectById(id);

        // 사용 포인트가 보유 포인트보다 많은지 예외처리
        if (userPoint.point() < amount) {
            throw new Exception("포인트 없어!!");
        }

        // 포인트 사용
        userPoint = userPointRepository.insertOrUpdate(userPoint.id(), userPoint.point() - amount);

        // 포인트를 사용하면 히스토리 기록
        pointHistoryUpdate(id, amount, TransactionType.USE);

        return userPoint;
    }

    // 히스토리 업데이트
    public PointHistory pointHistoryUpdate(long id, long amount, TransactionType type) {
        return pointHistoryRepository.insert(id, amount, type, System.currentTimeMillis());
    }

}
