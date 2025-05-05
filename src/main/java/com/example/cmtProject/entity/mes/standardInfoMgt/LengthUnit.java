package com.example.cmtProject.entity.mes.standardInfoMgt;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "LENGTH_UNIT")
@NoArgsConstructor
@AllArgsConstructor
public class LengthUnit {

	@Id
	@Column(name = "LT_TYPE_CODE")
    private String ltTypeCode;
    
	@Column(name = "LT_TYPE_NAME")
    private String ltTypeName;
    
	@Column(name = "LT_TYPE_SYMBOL")
    private String ltTypeSymbol;
    
	@Column(name = "LT_TYPE_COMMENT")
    private String ltTypeComment;
    
	@Column(name = "LT_TYPE_USERYN")
    private String ltTypeUseryn;
}
