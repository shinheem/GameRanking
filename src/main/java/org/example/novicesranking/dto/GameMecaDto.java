package org.example.novicesranking.dto;

import lombok.*;


@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class GameMecaDto extends OurRankingDto{

    private String gamename;        //게임명
    private int ranking;         //순위
    private String genre;           //장르
    private String madecompany;     //제조사


}
