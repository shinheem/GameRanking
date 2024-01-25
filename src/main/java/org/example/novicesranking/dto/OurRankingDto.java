package org.example.novicesranking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OurRankingDto {

    private int ranking;        //순위
    private String gamename;    //게임명
    private String madecompany; //제조사
    private String genre;       //장르
    private double score;       //점수
    private Date createAt;      //업데이트 이전날짜
//    private Date updateAt;      //업데이트 날짜



}
