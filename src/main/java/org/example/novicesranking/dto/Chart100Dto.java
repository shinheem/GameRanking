package org.example.novicesranking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class Chart100Dto extends OurRankingDto {

    private int ranking;
    private String gamename;
    private String genre;
    private String madecompany;


}
