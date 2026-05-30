package com.nju.partner.controller;

import com.nju.partner.common.Result;
import com.nju.partner.dto.ApplicationCreateDTO;
import com.nju.partner.service.ApplicationService;
import com.nju.partner.vo.ApplicationVO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ApplicationController {

    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @PostMapping("/posts/{id}/apply")
    public Result<Void> apply(@PathVariable Long id, @Valid @RequestBody ApplicationCreateDTO dto) {
        applicationService.apply(id, dto.getMessage());
        return Result.success();
    }

    @GetMapping("/posts/{id}/applications")
    public Result<List<ApplicationVO>> getApplications(@PathVariable Long id) {
        return Result.success(applicationService.getApplications(id));
    }

    @PutMapping("/applications/{id}/pass")
    public Result<Void> pass(@PathVariable Long id) {
        applicationService.passApplication(id);
        return Result.success();
    }

    @PutMapping("/applications/{id}/reject")
    public Result<Void> reject(@PathVariable Long id) {
        applicationService.rejectApplication(id);
        return Result.success();
    }

    @PutMapping("/applications/{id}/cancel")
    public Result<Void> cancel(@PathVariable Long id) {
        applicationService.cancelApplication(id);
        return Result.success();
    }
}
