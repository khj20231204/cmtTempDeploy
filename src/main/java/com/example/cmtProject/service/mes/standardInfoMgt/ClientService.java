package com.example.cmtProject.service.mes.standardInfoMgt;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.cmtProject.dto.mes.standardInfoMgt.ClientsDTO;
import com.example.cmtProject.mapper.mes.standardInfoMgt.ClientMapper;

@Service
public class ClientService {
	@Autowired ClientMapper clientMapper;
	
	public int regiClientList(ClientsDTO clientsDTO) {
		return clientMapper.insertClientList(clientsDTO);
	}

	public List<ClientsDTO> getClientList() {
		return clientMapper.selectClientList();
	}

	public int deleteClient(Long cltNo) {
		return clientMapper.deleteClientList(cltNo);
	}
}
