package com.nju.partner.controller;

import com.nju.partner.common.Result;
import com.nju.partner.service.FavoriteService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class FavoriteController {

    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @PostMapping("/posts/{id}/favorite")
    public Result<Void> addFavorite(@PathVariable Long id) {
        favoriteService.addFavorite(id);
        return Result.success();
    }

    @DeleteMapping("/posts/{id}/favorite")
    public Result<Void> removeFavorite(@PathVariable Long id) {
        favoriteService.removeFavorite(id);
        return Result.success();
    }

    @GetMapping("/posts/{id}/favorite/status")
    public Result<Boolean> checkFavorite(@PathVariable Long id) {
        return Result.success(favoriteService.isFavorited(id));
    }
}
