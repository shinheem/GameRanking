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

    private static String gameMeca_URL = "https://www.gamemeca.com/ranking.php#ranking-top";
    private static String peopleTree_URL = "https://trees.gamemeca.com/ranking.php";
    private static String chart100_URL1 = "http://www.gamechart100.com/bbs/board.php?bo_table=B11";
    private static String chart100_URL2 = "http://www.gamechart100.com/bbs/board.php?bo_table=B11&page=2";
    private static String chart100_URL3 = "http://www.gamechart100.com/bbs/board.php?bo_table=B11&page=3";
    private static String chart100_URL4 = "http://www.gamechart100.com/bbs/board.php?bo_table=B11&page=4";




    @GetMapping("/ourRanking")
    public void selectOurRanking(Model model) throws IOException {
        List<OurRankingDto> mergedData = service.mergeScoresAndInsert(gameMeca_URL, peopleTree_URL,chart100_URL1,chart100_URL2,chart100_URL3,chart100_URL4);
        List<OurRankingDto> list = service.selectOurRanking();
        model.addAttribute("list",list);

    }




}
