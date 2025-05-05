package com.example.cmtProject.controller.mes.standardInfoMgt;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.cmtProject.dto.mes.standardInfoMgt.ClientsDTO;
import com.example.cmtProject.service.mes.standardInfoMgt.ClientService;


@Controller
public class ClientInfoController {
	@Autowired ClientService clientService;

	@GetMapping("/clientInfo")
	public String clientInfo(ClientsDTO clientsDTO,Model model) {
		List<ClientsDTO> clientList = clientService.getClientList();
		model.addAttribute("clientList",clientList);
		
		return "mes/standardInfoMgt/clientInfo";
	}
	
	@PostMapping("/clientInfoForm")
	public String clientInfoForm(ClientsDTO clientsDTO) {
		int clientList = clientService.regiClientList(clientsDTO); 
		System.out.println(clientList);
		
		return "redirect:/clientInfo";
	}
	@ResponseBody
	@PostMapping("/deleteClient")
	public String deleteClient(@RequestParam("cltNo") Long cltNo) {
		int delete = clientService.deleteClient(cltNo);
		
		return "삭제 완료";
	}
	
}
