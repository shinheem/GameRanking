package org.example.novicesranking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.novicesranking.dao.OurRankingMapper;
import org.example.novicesranking.dto.Chart100Dto;
import org.example.novicesranking.dto.GameMecaDto;
import org.example.novicesranking.dto.OurRankingDto;
import org.example.novicesranking.dto.PeopleTreeDto;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class OurRankingService {

    private final OurRankingMapper dao;
    private static final Logger logger = LoggerFactory.getLogger(OurRankingService.class);


    // 장르에 따른 랭킹 데이터 가져오기
    public List<OurRankingDto> selectOurRankingByGenre(String genre) {
        return dao.selectOurRankingByGenre(genre);
    }

    // OurRanking 불러오기
    public List<OurRankingDto> selectOurRanking() throws IOException {
        return dao.selectOurRanking();
    }

    // RPG 랭킹 불러오기
    public List<OurRankingDto> selectOurRankingRPG() throws IOException {
        return dao.getOurRankingRPG();
    }

    // 액션 랭킹 불러오기
    public List<OurRankingDto> selectOurRankingAction() throws IOException {
        return dao.getOurRankingAction();
    }

    // FPS 랭킹 불러오기
    public List<OurRankingDto> selectOurRankingFps() throws IOException {
        return dao.getOurRankingFps();
    }

    // 기타 랭킹 불러오기
    public List<OurRankingDto> selectOurRankingEtc() throws IOException {
        return dao.getOurRankingEtc();
    }

    // OurRanking 삭제하기
    public void deleteOurRanking() {
        dao.deleteOurRanking();
    }
    

    // 게임 메카 데이터와 국민트리 데이터, 차트100 데이터를 가중치를 적용하여 합치는 메서드
    public List<OurRankingDto> mergeScoresAndInsert(String gameMeca_URL, String peopleTree_URL,
                                                    String chart100_URL1, String chart100_URL2,
                                                    String chart100_URL3, String chart100_URL4) throws IOException {
        List<OurRankingDto> existingData = dao.selectOurRanking();

        LocalDate localDate = LocalDate.now();
        Date recentdate = Date.valueOf(localDate);
        log.info("최근 날짜 : {}", recentdate);

        // 게임 메카 데이터 가져오기
        List<GameMecaDto> gameMecaData = getGameMecaData(gameMeca_URL);

        // 국민트리 데이터 가져오기
        List<PeopleTreeDto> peopleTreeData = getPeopleTreeData(peopleTree_URL);

        // 차트100 데이터 가져오기
        List<Chart100Dto> chart100Data = getChart100Data(chart100_URL1, chart100_URL2, chart100_URL3, chart100_URL4);

        // 중복된 데이터 합치기
        List<OurRankingDto> mergedData = mergeData(existingData, gameMecaData, peopleTreeData, chart100Data);

        for (int i = 0; i < mergedData.size(); i++) {
            mergedData.get(i).setCreateAt(recentdate);
        }

        // 중복된 리스트 insert 못하게하기
        if(existingData.size() < 40) {
            dao.insertOurRanking(mergedData);
            log.error("기존값이 없어 insert 하였습니다.");
        }else if (recentdate == mergedData.get(0).getCreateAt()) {
            log.error("기존값을 불러옵니다.");
        }else {
            dao.deleteOurRanking();
            dao.insertOurRanking(mergedData);
            log.error("기존값을 삭제하고 insert 완료했습니다.");
        }

        return mergedData;
    }

    // 각데이터를 가져와서 가중치를 주고 합치는 로직
    private <T extends OurRankingDto> List<OurRankingDto> mergeData(List<OurRankingDto> existingData,
                                                                    List<GameMecaDto> gameMecaData,
                                                                    List<PeopleTreeDto> peopleTreeData,
                                                                    List<Chart100Dto> chart100Data) {

        // 각 데이터에 순위에 따라 점수를 부여하고 순위를 매기는 메서드
        assignRankings(gameMecaData, 0.5);
        assignRankings(peopleTreeData, 0.3);
        assignRankings(chart100Data, 0.2);


        List<OurRankingDto> mergedData = new ArrayList<>(gameMecaData.stream().map(gameMecaDto -> {
            OurRankingDto ourRankingDto = new OurRankingDto();
            ourRankingDto.setGamename(gameMecaDto.getGamename());
            ourRankingDto.setMadecompany(gameMecaDto.getMadecompany());
            ourRankingDto.setGenre(gameMecaDto.getGenre());
            ourRankingDto.setScore(gameMecaDto.getScore());
            ourRankingDto.setRanking(0);
            return ourRankingDto;
        }).toList());

        //ourRanking에 들어있는 게임메카와 피플트리 비교
        mergedData.forEach(dto -> {
            peopleTreeData.forEach(
                peopleTreeDto -> {
                    if (dto.getGamename().equals(peopleTreeDto.getGamename())) {
                        dto.setScore(dto.getScore()+peopleTreeDto.getScore());
                    }
                }
            );

            //ourRanking에 들어있는 게임메카와 차트100 비교
            chart100Data.forEach(
                chart100Dto -> {
                    if (dto.getGamename().equals(chart100Dto.getGamename())) {
                        dto.setScore(dto.getScore()+chart100Dto.getScore());
                    }
                }
            );
        });

        mergedData.sort(Comparator.comparingDouble(OurRankingDto::getScore).reversed());

        IntStream.range(0, mergedData.size())
                .forEach(i -> mergedData.get(i).setRanking(i + 1));

        return mergedData;
    }


    // 각 데이터에 순위에 따라 점수를 부여하고 순위를 매기는 메서드
    private <T extends OurRankingDto> void assignRankings(List<T> dataList, double weight) {
        int maxRanking = dataList.size();
        dataList.sort(Comparator.comparingDouble(T::getScore).reversed());

        for (int i = 0; i < dataList.size(); i++) {
            final int index = i;
            T data = dataList.get(index);
            double rawScore = (maxRanking - index) * weight;
            log.info("본점수 : {}" , rawScore);
            double roundedScore = Math.round(rawScore * 100.0) / 100.0; // 둘째 자리까지 반올림
            data.setScore(roundedScore);
            data.setRanking(index + 1);
        }
    }

    // 각 데이터에 순위에 따라 점수를 부여하는 메서드
    private <T extends OurRankingDto> void assignScores(List<T> dataList) {
        int maxRanking = dataList.size();
        for (int i = 0; i < dataList.size(); i++) {
            T data = dataList.get(i);
            double rawScore = (double) (maxRanking - i);
            data.setScore(rawScore);

        }
    }

    // 메카 게임 데이터 가져오는 메소드
    private List<GameMecaDto> getGameMecaData(String gameMeca_URL) throws IOException {
        Document doc = Jsoup.connect(gameMeca_URL).get();
        Elements contents = doc.getElementsByAttributeValue("class", "ranking-table-rows");

        List<GameMecaDto> list = new ArrayList<>();
        for (int loopcounter = 0; loopcounter < 40 && loopcounter < contents.size(); loopcounter++) {
            Element content = contents.get(loopcounter);
            Element gameInfo = content.getElementsByClass("game-info").first();
            Elements span = gameInfo.getElementsByTag("span");

            if (span.size() >= 2) {
                Element secondspan = span.get(1);
                Element ranking = content.getElementsByTag("span").first();

                GameMecaDto dto = GameMecaDto.builder()
                        .gamename(removeParentheses(content.getElementsByAttributeValue("class", "game-name").text()))
                        .ranking(Integer.parseInt(ranking.text()))
                        .genre(secondspan.text())
                        .madecompany(content.getElementsByAttributeValue("class", "company").text())
                        .build();
                list.add(dto);
            }
        }

        // 점수 부여
        assignScores(list);

        return list;
    }


    // 국민트리 데이터 가져오는 메소드
    private List<PeopleTreeDto> getPeopleTreeData(String peopletree_url) throws IOException {
        Document doc = Jsoup.connect(peopletree_url).get();
        Elements contents = doc.select("#rankO tr.ranking-table-rows");
        int listcount = 0;

        List<PeopleTreeDto> list = new ArrayList<>();
        for (int loopcounter = 0; loopcounter < 40 && loopcounter < contents.size(); loopcounter++) {
            Element content = contents.get(loopcounter);
            Element gameInfo = content.getElementsByClass("game-info").first();
            Elements span = gameInfo.getElementsByTag("span");

            if (span.size() >= 2) {
                Element secondspan = span.get(1);
                Element ranking = content.getElementsByTag("span").first();

                PeopleTreeDto dto = PeopleTreeDto.builder()
                        .ranking(Integer.parseInt(ranking.text()))
                        .gamename(removeParentheses(content.getElementsByAttributeValue("class", "game-name").text()))
                        .madecompany(content.select("div.game-info > span.company").text())
                        .genre(secondspan.text())
                        .build();
                list.add(dto);
                listcount++;

                if (listcount >= 40) {
                    break;
                }
            }
        }

        // 점수 부여
        assignScores(list);

        return list;
    }

    //차트100 데이터 가져오는 메소드
    private List<Chart100Dto> getChart100Data(String chart100_URL1, String chart100_URL2, String chart100_URL3, String chart100_URL4) throws IOException {
        List<Chart100Dto> list = new ArrayList<>();

        processPage(chart100_URL1, list);
        processPage(chart100_URL2, list);
        processPage(chart100_URL3, list);
        processPage(chart100_URL4, list);

        // 점수 부여
        assignScores(list);
        return list;
    }

    //페이지가 4개인 차트100 중복코드를 없애기 위한 메소드
    private void processPage(String url, List<Chart100Dto> list) throws IOException {
        Document doc = Jsoup.connect(url).get();
        Elements chart100 = doc.select("div .mw_basic_list_subject_desc span");

        for (int loopcounter = 0; loopcounter < 40 && loopcounter < chart100.size(); loopcounter += 3) {
            Element rankingElement = chart100.get(loopcounter);
            Element gamenameElement = chart100.get(loopcounter + 2);

            int ranking = Integer.parseInt(rankingElement.text());
            String gamename = removeParentheses(gamenameElement.text());

            Chart100Dto dto = Chart100Dto.builder()
                    .ranking(ranking)
                    .gamename(gamename)
                    .build();
            list.add(dto);
        }
    }

    //괄호나 공백 제거 메소드
    private static String removeParentheses(String input) {
        final String removeStr = "플레이어언노운스";

        // 문자열 제거 및 정리
        String result = input.replaceAll("\\p{C}", "").replaceAll("\\([^)]*\\)", "").trim().replaceAll("\\s", "");

        // 특정 단어 제거
        int remove = result.indexOf(removeStr);
        if (remove != -1) {
            result = new StringBuilder(result).delete(remove, remove + removeStr.length()).toString();
        }

        return result.toLowerCase();
    }



}
