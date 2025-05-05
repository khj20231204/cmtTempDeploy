package com.example.cmtProject.mapper.erp.eapproval;

import com.example.cmtProject.dto.erp.eapproval.DocFormDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DocFormMapper {
    // 양식 저장
    void insertDocForm(DocFormDTO docForm);
    
    // 양식 수정
    void updateDocForm(DocFormDTO docForm);
    
    // 양식 삭제
    void deleteDocForm(String formId);
    
    // 양식 목록 조회
    List<DocFormDTO> selectAllDocForms();
    
    // 특정 양식 조회
    DocFormDTO selectDocFormById(String formId);
    
}