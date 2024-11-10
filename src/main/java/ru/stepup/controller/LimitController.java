package ru.stepup.controller;

import ru.stepup.dto.Limit;
import org.springframework.web.bind.annotation.*;
import ru.stepup.service.LimitService;

import java.math.BigDecimal;

@RestController
@RequestMapping("/limits")
public class LimitController {

    private final LimitService limitService;

    public LimitController(LimitService limitService) {
        this.limitService = limitService;
    }

    @GetMapping("/limit/{userId}")
    public Limit getLimitByUserId(@PathVariable Long userId) {
        return limitService.getLimitByUserId(userId);
    }

    @PostMapping("/reducelimit")
    public Limit reduceUserLimit(@RequestParam Long userId, @RequestParam BigDecimal summa) {
        return limitService.reduceUserLimit(userId, summa);
    }

    @PostMapping("/restorelimit/{userId}")
    public Limit restoreUserLimit(@PathVariable Long userId) {
        return limitService.restoreUserLimit(userId);
    }
}