package io.hhplus.tdd.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.hhplus.tdd.point.controller.PointController;
import io.hhplus.tdd.point.service.PointService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest(PointController.class)
public class PointControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private PointService pointService;

    @DisplayName("유저 포인트 조회")
    @Test
    public void pointTest() throws Exception {
        // given
        long id = 1L;
        // when, then
        // 응답이 성공했는지 확인한다. (200)
        mockMvc.perform(
            MockMvcRequestBuilders.get("/point/{id}", id))
               .andExpect(status().isOk());
    }

    @DisplayName("유저의 포인트 충전/이용 이력 조회")
    @Test
    public void history() throws Exception {
        // given
        long id = 1L;
        // when, then
        // 응답이 성공했는지 확인한다. (200)
        mockMvc.perform(
            MockMvcRequestBuilders.get("/point/{id}/histories", id))
               .andExpect(status().isOk());
    }

    @DisplayName("유저의 포인트 충전")
    @Test
    public void chargeTest() throws Exception {
        // given
        long id = 1L;
        long amount = 10000L;
        // when, then
        // 응답이 성공했는지 확인한다. (200)
        mockMvc.perform(
            MockMvcRequestBuilders.patch("/point/{id}/charge", id)
                .content(String.valueOf(amount))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }

    @DisplayName("유저의 포인트 사용")
    @Test
    public void useTest() throws Exception {
        // given
        long id = 1L;
        long amount = 10000L;
        // when, then
        mockMvc.perform(
            MockMvcRequestBuilders.patch("/point/{id}/use", id)
                .content(String.valueOf(amount))
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
    }



}
