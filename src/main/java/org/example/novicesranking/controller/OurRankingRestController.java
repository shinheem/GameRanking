package org.example.novicesranking.controller;

import lombok.RequiredArgsConstructor;
import org.example.novicesranking.dto.OurRankingDto;
import org.example.novicesranking.service.OurRankingService;
import org.springframework.web.bind.annotation.GetMapping;
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

    @GetMapping("/ourRanking")
    public List<OurRankingDto> selectOurRanking() throws IOException {
        return service.selectOurRanking();
    }
    @GetMapping("/Rpg")
    public List<OurRankingDto> selectOurRankingRPG() throws IOException {
        return service.selectOurRankingRPG();
    }
    @GetMapping("/Action")
    public List<OurRankingDto> selectOurRankingAction() throws IOException {
        return service.selectOurRankingAction();
    }
    @GetMapping("/Fps")
    public List<OurRankingDto> selectOurRankingFps() throws IOException {
        return service.selectOurRankingFps();
    }
    @GetMapping("/Etc")
    public List<OurRankingDto> selectOurRankingEtc() throws IOException {
        return service.selectOurRankingEtc();
    }





}
