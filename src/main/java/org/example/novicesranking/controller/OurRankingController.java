package org.example.novicesranking.controller;

import lombok.RequiredArgsConstructor;
import org.example.novicesranking.dto.OurRankingDto;
import org.example.novicesranking.service.OurRankingService;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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
    @GetMapping("/Rpg")
    public void selectOurRankingRPG(Model model) throws IOException {
        List<OurRankingDto> list = service.selectOurRankingRPG();
        model.addAttribute("list",list);
    }
    @GetMapping("/Action")
    public void selectOurRankingAction(Model model) throws IOException {
        List<OurRankingDto> list = service.selectOurRankingAction();
        model.addAttribute("list",list);
    }
    @GetMapping("/Fps")
    public void selectOurRankingFps(Model model) throws IOException {
        List<OurRankingDto> list = service.selectOurRankingFps();
        model.addAttribute("list",list);
    }
    @GetMapping("/Etc")
    public void selectOurRankingEtc(Model model) throws IOException {
        List<OurRankingDto> list = service.selectOurRankingEtc();
        model.addAttribute("list",list);
    }






}
