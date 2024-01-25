package org.example.novicesranking.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
public class ScheduleService {

    private final OurRankingService service;

    public ScheduleService(OurRankingService service) {
        this.service = service;
    }

    private static String gameMeca_URL = "https://www.gamemeca.com/ranking.php#ranking-top";
    private static String peopleTree_URL = "https://trees.gamemeca.com/ranking.php";
    private static String chart100_URL1 = "http://www.gamechart100.com/bbs/board.php?bo_table=B11";
    private static String chart100_URL2 = "http://www.gamechart100.com/bbs/board.php?bo_table=B11&page=2";
    private static String chart100_URL3 = "http://www.gamechart100.com/bbs/board.php?bo_table=B11&page=3";
    private static String chart100_URL4 = "http://www.gamechart100.com/bbs/board.php?bo_table=B11&page=4";

    @Scheduled(cron = "0 0/3 * * * *")
    public void updateOurRanking() {
        try {
        service.mergeScoresAndInsert(gameMeca_URL, peopleTree_URL,chart100_URL1,chart100_URL2,chart100_URL3,chart100_URL4);

        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
