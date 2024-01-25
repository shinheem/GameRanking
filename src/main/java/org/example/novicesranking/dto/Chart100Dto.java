package org.example.novicesranking.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Chart100Dto extends OurRankingDto {

    private int ranking;             //순위
    private String gamename;         //게임명
    private String genre;            //장르
    private String madecompany;      //제조사


}
