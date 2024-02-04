package org.example.novicesranking.controller;

import lombok.RequiredArgsConstructor;
import org.example.novicesranking.dto.OurRankingDto;
import org.example.novicesranking.service.OurRankingService;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.List;
//Controller로 View로 뿌릴때 사용하는 컨트롤러

//@Controller
//@RequestMapping("/game")
@RequiredArgsConstructor
public class OurRankingController {

    private final OurRankingService service;

    //직접 실행할 경우
    @GetMapping("/ourRanking")
    public void selectOurRanking(Model model) throws IOException {
        List<OurRankingDto> list = service.selectOurRanking();
        model.addAttribute("list",list);
    }
    @GetMapping("/ranking")
    public void selectOurRankingByCategory(@RequestParam String category, Model model) throws IOException {
        List<OurRankingDto> list = service.selectOurRankingByCategory(category);
        model.addAttribute("list",list);
    }





}
