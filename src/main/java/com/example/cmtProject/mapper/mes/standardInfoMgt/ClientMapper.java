package com.example.cmtProject.mapper.mes.standardInfoMgt;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.cmtProject.dto.mes.standardInfoMgt.ClientsDTO;

@Mapper
public interface ClientMapper {

	int insertClientList(ClientsDTO clientsDTO);

	List<ClientsDTO> selectClientList();

	int deleteClientList(Long cltNo);

}
