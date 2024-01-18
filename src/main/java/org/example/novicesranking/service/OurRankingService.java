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
import java.util.*;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OurRankingService {

    private final OurRankingMapper dao;
    private static final Logger logger = LoggerFactory.getLogger(OurRankingService.class);

    public List<OurRankingDto> selectOurRanking() throws IOException {
        return dao.selectOurRanking();
    }

    // 게임 메카 데이터와 국민트리 데이터, 차트100 데이터를 가중치를 적용하여 합치는 메서드
    public List<OurRankingDto> mergeScoresAndInsert(String gameMeca_URL, String peopleTree_URL,
                                                    String chart100_URL1, String chart100_URL2,
                                                    String chart100_URL3, String chart100_URL4) throws IOException {
        List<OurRankingDto> existingData = dao.selectOurRanking();

        // 게임 메카 데이터 가져오기
        List<GameMecaDto> gameMecaData = getGameMecaData(gameMeca_URL, 0.5);

        // 국민트리 데이터 가져오기
        List<PeopleTreeDto> peopleTreeData = getPeopleTreeData(peopleTree_URL, 0.3);

        // 차트100 데이터 가져오기
        List<Chart100Dto> chart100Data = getChart100Data(chart100_URL1, chart100_URL2, chart100_URL3, chart100_URL4, 0.2);

        // 두 데이터를 합치기
        List<OurRankingDto> mergedData = mergeData(gameMecaData, peopleTreeData, chart100Data);
        logger.info("머지된 데이터: {}", mergedData.size());

        // 순위 매기기
        assignRankings(mergedData, 1.0);
        // 데이터를 테이블에 삽입
        if (!check(existingData, mergedData)) {
            int result = dao.insertOurRanking(mergedData);
            logger.info("insert 확인: {}", result);
        }

        // 결과에서 상위 40개만 반환
        return mergedData.stream().limit(40).collect(Collectors.toList());
    }

    //중복된 리스트 insert 못하게하기
    private boolean check(List<OurRankingDto> existingData, List<OurRankingDto> newData) {
        return existingData.size() == newData.size();
    }


    // 각 데이터에 순위에 따라 점수를 부여하고 순위를 매기는 메서드
    private <T extends OurRankingDto> void assignRankings(List<T> dataList, double weight) {
        int maxRanking = dataList.size();
        dataList.sort(Comparator.comparingDouble(T::getScore).reversed());

        for (int i = 0; i < dataList.size(); i++) {
            final int index = i;
            T data = dataList.get(index);
            // 순위에 따라 점수 부여 (1등이 가장 높은 점수)
            double rawScore = (maxRanking - index) * weight;
            double roundedScore = Math.round(rawScore * 100.0) / 100.0; // 둘째 자리까지 반올림
            data.setScore(roundedScore);
            data.setRanking(index + 1); // 환산된 순위 매기기
        }
    }

    // 각 데이터에 순위에 따라 점수를 부여하는 메서드
    private <T extends OurRankingDto> void assignScores(List<T> dataList, double weight) {
        int maxRanking = dataList.size();
        for (int i = 0; i < dataList.size(); i++) {
            T data = dataList.get(i);
            // 순위에 따라 점수 부여 (1등이 가장 높은 점수)
            double rawScore = (maxRanking - i) * weight;
            double roundedScore = Math.round(rawScore * 100.0) / 100.0; // 둘째 자리까지 반올림
            data.setScore(roundedScore);
            log.info("score: {}", roundedScore);
        }
    }

    private <T extends OurRankingDto> List<OurRankingDto> mergeData(List<GameMecaDto> gameMecaData, List<PeopleTreeDto> peopleTreeData, List<Chart100Dto> chart100Data) {
        List<OurRankingDto> mergedData = new ArrayList<>();

        // 게임 메카 데이터에 점수를 매기고 내림차순으로 정렬
        assignScores(gameMecaData, 0.5);
        gameMecaData.sort(Comparator.comparingDouble(GameMecaDto::getScore).reversed());

        // 국민트리 데이터에 점수를 매기고 내림차순으로 정렬
        assignScores(peopleTreeData, 0.3);
        peopleTreeData.sort(Comparator.comparingDouble(PeopleTreeDto::getScore).reversed());

        // 차트100 데이터에 점수를 매기고 내림차순으로 정렬
        assignScores(chart100Data, 0.2);
        chart100Data.sort(Comparator.comparingDouble(Chart100Dto::getScore).reversed());

        // 중복된 데이터 합치기
        int rankingCounter = 1;
        for (GameMecaDto gameMecaDto : gameMecaData) {
            // 이름이 같은 국민트리 데이터 찾기
            List<PeopleTreeDto> matchingPeopleTreeList = peopleTreeData.stream()
                    .filter(peopleTreeDto -> removeParentheses(peopleTreeDto.getGamename()).equals(removeParentheses(gameMecaDto.getGamename())))
                    .collect(Collectors.toList());

            // 차트100 데이터 찾기
            List<Chart100Dto> matchingChart100List = chart100Data.stream()
                    .filter(chart100Dto -> removeParentheses(chart100Dto.getGamename()).equals(removeParentheses(gameMecaDto.getGamename())))
                    .collect(Collectors.toList());

            // 이름이 같은 경우, 중복된 데이터를 합치고 국민트리 리스트에서 제거
            if (!matchingPeopleTreeList.isEmpty()) {
                PeopleTreeDto matchingPeopleTree = matchingPeopleTreeList.get(0);
                OurRankingDto mergedDto = mergeData(gameMecaDto, matchingPeopleTree, rankingCounter++);
                mergedData.add(mergedDto);
                peopleTreeData.remove(matchingPeopleTree);
            } else if (!matchingChart100List.isEmpty()) {
                Chart100Dto matchingChart100 = matchingChart100List.get(0);
                OurRankingDto mergedDto = mergeData(gameMecaDto, matchingChart100, rankingCounter++);
                mergedData.add(mergedDto);
                chart100Data.remove(matchingChart100);
            }
        }

        // 나머지 데이터 합치기
        mergedData.addAll(mergeRemainingData(gameMecaData, rankingCounter++));
        mergedData.addAll(mergeRemainingData(peopleTreeData, rankingCounter++));
        mergedData.addAll(mergeRemainingData(chart100Data, rankingCounter));

        return mergedData;
    }

    private <T extends OurRankingDto> OurRankingDto mergeData(GameMecaDto gameMeca, T otherData, int ranking) {
        OurRankingDto mergedDto = new OurRankingDto();
        mergedDto.setGamename(gameMeca.getGamename());
        mergedDto.setScore(gameMeca.getScore() + otherData.getScore());
        mergedDto.setMadecompany(gameMeca.getMadecompany());
        mergedDto.setGenre(gameMeca.getGenre());

        if (otherData instanceof Chart100Dto) {
            Chart100Dto chart100Data = (Chart100Dto) otherData;
            mergedDto.setMadecompany(""); // 또는 다른 기본값 설정
            mergedDto.setGenre(""); // 또는 다른 기본값 설정
            if (chart100Data != null) {
                mergedDto.setMadecompany(chart100Data.getMadecompany());
                mergedDto.setGenre(chart100Data.getGenre());
            }
        }

        logger.info("게임 이름: {}, 랭킹: {}", mergedDto.getGamename(), mergedDto.getRanking());
        mergedDto.setRanking(ranking);
        return mergedDto;
    }

    private <T extends OurRankingDto> List<OurRankingDto> mergeRemainingData(List<T> remainingData, int initialRanking) {
        final int[] ranking = {initialRanking};

        Map<String, OurRankingDto> mergedDataMap = remainingData.stream()
                .collect(Collectors.toMap(
                        dto -> dto.getGamename(),
                        dto -> dto,
                        (dto1, dto2) -> {
                            OurRankingDto mergedDto = new OurRankingDto();
                            mergedDto.setGamename(dto1.getGamename());
                            mergedDto.setScore(dto1.getScore() + dto2.getScore());
                            mergedDto.setMadecompany(dto1.getMadecompany());
                            mergedDto.setGenre(dto1.getGenre());
                            mergedDto.setRanking(ranking[0]++);
                            return mergedDto;
                        },
                        LinkedHashMap::new
                ));

        return new ArrayList<>(mergedDataMap.values());
    }






    // 메카 게임 데이터 가져오는 메소드
    private List<GameMecaDto> getGameMecaData(String gameMeca_URL, double weight) throws IOException {
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
        assignScores(list, weight);

        return list;
    }


    // 국민트리 데이터 가져오는 메소드
    private List<PeopleTreeDto> getPeopleTreeData(String peopletree_url, double weight) throws IOException {
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
        assignScores(list, weight);

        return list;
    }
    
    //차트100 데이터 가져오는 메소드
    private List<Chart100Dto> getChart100Data(String chart100_URL1, String chart100_URL2, String chart100_URL3, String chart100_URL4, double weight) throws IOException {
        List<Chart100Dto> list = new ArrayList<>();

        processPage(chart100_URL1, list);
        processPage(chart100_URL2, list);
        processPage(chart100_URL3, list);
        processPage(chart100_URL4, list);

        // 점수 부여
        assignScores(list, weight);
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
        String result = input.replaceAll("\\p{C}", "").replaceAll("\\([^)]*\\)", "").trim();
        result = result.replaceAll("\\s","");
        result = result.toLowerCase();

        return result;
    }





}
