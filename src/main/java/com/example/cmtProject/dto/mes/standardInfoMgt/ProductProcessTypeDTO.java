package com.example.cmtProject.dto.mes.standardInfoMgt;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductProcessTypeDTO {

	  private Long pdtPrcNo;
	  private String pdtPrcTypeCode;
	  private String pdtPrcTypeName;
	  private int  pdtPrcPriority;
	  private LocalDate createDate;
	  private String pdtComment;
}
