package com.test.swaggercustom;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Plan API for Test")
@RestController
@RequestMapping(value = "/plan")
public class TestController {

    @Operation(summary = "추천 일정 가져오기")
    @GetMapping("/recommended")
    public void getRecommendedPlan() {
    }

    @Operation(summary = "일정 추가하기")
    @PostMapping("/create")
    public String createPlan(String plan) {
        return "POST 일정 추가하기";
    }

    @Operation(summary = "일정 가져오기")
    @GetMapping({"/v1", "/v2"})
    public void getPlan() {
    }

    @Operation(summary = "일정 추가하기")
    @GetMapping("/create")
    public void createPlan() {
    }
}
