package org.example.novicesranking.controller;

import lombok.RequiredArgsConstructor;
import org.example.novicesranking.dto.OurRankingDto;
import org.example.novicesranking.service.OurRankingService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/game")
@RequiredArgsConstructor
public class OurRankingController {

    private final OurRankingService service;

    @GetMapping("/ourRanking")
    public void selectOurRanking(Model model) throws IOException {
        List<OurRankingDto> list = service.selectOurRanking();
        model.addAttribute("list",list);
    }




}
