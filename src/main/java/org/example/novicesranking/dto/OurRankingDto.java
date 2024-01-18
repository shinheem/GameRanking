package org.example.novicesranking.dto;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class OurRankingDto {

    private int ranking;     //순위
    private String gamename;    //게임명
    private String madecompany; //제조사
    private String genre;       //장르
    private double score;          //점수

}
