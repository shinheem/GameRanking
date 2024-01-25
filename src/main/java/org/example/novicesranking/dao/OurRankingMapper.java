package org.example.novicesranking.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.novicesranking.dto.OurRankingDto;

import java.util.List;
@Mapper
public interface OurRankingMapper {

    //OurRanking db insert
    public int insertOurRanking(@Param("list") List<OurRankingDto> dto);

    public void deleteOurRanking();
    //OurRankings 순위 리스트
    public List<OurRankingDto> selectOurRanking();

}
