package org.example.novicesranking.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.novicesranking.dto.OurRankingDto;

import java.util.List;
@Mapper
public interface OurRankingMapper {

    //insert
    int insertOurRanking(@Param("list") List<OurRankingDto> dto);

    // delete
    void deleteOurRanking();

    //OurRankings 전체순위 리스트
    List<OurRankingDto> selectOurRanking();     //전체
    List<OurRankingDto> selectOurRankingByCategory(String category); //카테고리에 따른 리스트

}
