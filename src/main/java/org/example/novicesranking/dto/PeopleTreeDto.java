package org.example.novicesranking.dto;

import lombok.Builder;
import lombok.Getter;

import lombok.Setter;
import lombok.ToString;

@Getter
@Builder
@Setter
@ToString
public class PeopleTreeDto extends OurRankingDto{
    private int ranking;    //순위
    private String gamename;    //게임명
    private String madecompany; //제조사
    private String genre;   //장르
}
