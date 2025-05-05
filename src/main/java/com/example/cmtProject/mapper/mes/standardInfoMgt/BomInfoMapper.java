package com.example.cmtProject.mapper.mes.standardInfoMgt;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.cmtProject.dto.mes.standardInfoMgt.BomEditDTO;
import com.example.cmtProject.dto.mes.standardInfoMgt.BomInfoTotalDTO;

@Mapper
public interface BomInfoMapper {

	public List<BomInfoTotalDTO> getBomInfoTotalList(String pdtCode);

	public int bomMainUpdate(BomEditDTO bomEditDto);

}
