package org.example.novicesranking.dto;

import lombok.*;

@Getter
@Builder
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PeopleTreeDto extends OurRankingDto{
    private int ranking;    //순위
    private String gamename;    //게임명
    private String madecompany; //제조사
    private String genre;   //장르
}
