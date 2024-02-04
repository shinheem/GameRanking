package org.example.novicesranking.controller;

import lombok.RequiredArgsConstructor;
import org.example.novicesranking.dto.OurRankingDto;
import org.example.novicesranking.service.OurRankingService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
// json 형태로 보내기 위한 RestController

@RestController
@RequestMapping("/game")
@RequiredArgsConstructor
public class OurRankingRestController {

    private final OurRankingService service;

    //전체
    @GetMapping("/ourRanking")
    public List<OurRankingDto> selectOurRanking() throws IOException {
        return service.selectOurRanking();
    }
    
    //category 종류: rpg,action(액션),fps,etc(기타)
    @GetMapping("/ranking/{category}")
    public List<OurRankingDto> selectOurRankingByCategory(@PathVariable String category) throws IOException {
        return service.selectOurRankingByCategory(category);
    }
    





}
