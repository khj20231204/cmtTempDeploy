package com.example.cmtProject.controller.erp.saleMgt.commonModel;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.example.cmtProject.entity.comm.CommoncodeDetail;
import com.example.cmtProject.entity.erp.employees.Employees;
import com.example.cmtProject.entity.erp.salesMgt.PurchasesOrderStatus;
import com.example.cmtProject.entity.erp.salesMgt.SalesOrderStatus;
import com.example.cmtProject.entity.mes.standardInfoMgt.Clients;
import com.example.cmtProject.entity.mes.standardInfoMgt.Materials;
import com.example.cmtProject.entity.mes.standardInfoMgt.Products;
import com.example.cmtProject.repository.comm.CommonCodeDetailRepository;
import com.example.cmtProject.repository.erp.employees.EmployeesRepository;
import com.example.cmtProject.repository.erp.saleMgt.ClientsRepository;
import com.example.cmtProject.repository.erp.saleMgt.MaterialsOrderRepository;
import com.example.cmtProject.repository.erp.saleMgt.PurchasesOrderRepository;
import com.example.cmtProject.repository.erp.saleMgt.PurchasesOrderStatusRepository;
import com.example.cmtProject.repository.erp.saleMgt.SalesOrderRepository;
import com.example.cmtProject.repository.erp.saleMgt.SalesOrderStatusRepository;
import com.example.cmtProject.repository.mes.standardInfoMgt.ProductsRepository;
import com.example.cmtProject.service.erp.saleMgt.SalesOrderService;

@Service
public class PurchasesOrderModels {

	@Autowired
	private MaterialsOrderRepository materialsOrderRepository;
	
	@Autowired
	private ClientsRepository clientsRepository;
	
	@Autowired
	private EmployeesRepository employeesRepository;
	
	@Autowired
	private CommonCodeDetailRepository commoncodeDetailRepository;
	
	@Autowired
	private  PurchasesOrderStatusRepository purchasesOrderStatusRepository;
	
	public void commonPurchasesOrderModels(Model model) {
		
		//거래처
		//List<Clients> cltList = clientsRepository.findAll();
		List<Clients> cltList = clientsRepository.findByCltType("SUPPLIER");
		
		//인사
        List<Employees> empList = employeesRepository.findAll();
        
        //상품
        List<Materials> materialList = materialsOrderRepository.findAll();
        
        //발주 상태
        List<PurchasesOrderStatus> poStatusList = purchasesOrderStatusRepository.findAll();
        
        //부서
        List<CommoncodeDetail> commListDetp = commoncodeDetailRepository.findByCmnCode("DEPT");
        
        //직위
        List<CommoncodeDetail> commListPosition = commoncodeDetailRepository.findByCmnCode("POSITION");

        model.addAttribute("cltList", cltList);
        model.addAttribute("empList", empList);
        model.addAttribute("materialList", materialList);
        model.addAttribute("poStatusList", poStatusList);
        model.addAttribute("commListDetp", commListDetp);
        model.addAttribute("commListPosition", commListPosition);
	}

}
