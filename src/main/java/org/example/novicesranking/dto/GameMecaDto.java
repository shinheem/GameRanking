package org.example.novicesranking.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.sql.Date;

@Getter
@Setter
@Builder
@ToString
public class GameMecaDto extends OurRankingDto{

    private String gamename;        //게임명
    private int ranking;         //순위
    private String genre;           //장르
    private String madecompany;     //제조사


}
