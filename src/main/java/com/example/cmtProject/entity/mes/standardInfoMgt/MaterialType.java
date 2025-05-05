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
@Table(name = "MATERIAL_TYPE")
@NoArgsConstructor
@AllArgsConstructor
public class MaterialType {

	@Id
	@Column(name = "MTL_TYPE_CODE")
    private String mtlTypeCode;
    
    @Column(name = "MTL_TYPE_NAME")
    private String mtlTypeName;
    
    @Column(name = "MTL_TYPE_SYMBOL")
    private String mtlTypeSymbol;
    
    @Column(name = "MTL_TYPE_COMMENT")
    private String mtlTypeComment;
    
    @Column(name = "MTL_TYPE_USERYN")
    private String mtlTypeUseryn;
}
