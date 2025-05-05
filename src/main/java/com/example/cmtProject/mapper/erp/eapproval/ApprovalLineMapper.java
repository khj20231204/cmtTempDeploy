package com.example.cmtProject.mapper.erp.eapproval;

import com.example.cmtProject.dto.erp.eapproval.ApprovalLineDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ApprovalLineMapper {
    // 결재선 저장
    public void insertApprovalLine(ApprovalLineDTO approvalLine);
    
    // 결재선 수정
    public void updateApprovalLine(ApprovalLineDTO approvalLine);
    
    // 문서ID로 결재선 조회
    public List<ApprovalLineDTO> selectApprovalLinesByDocId(String docId);
    
    // 특정 결재자의 특정 문서 결재선 조회
    public ApprovalLineDTO selectApprovalLineByDocIdAndApproverId(
            @Param("docId") String docId, 
            @Param("approverId") String approverId);  // Integer approverNo -> String approverId로 변경
    
    // 문서의 다음 결재자 조회
    public List<ApprovalLineDTO> selectNextApproversByDocId(String docId);
    
    // 문서의 결재선 삭제 (문서 삭제 시)
    public void deleteApprovalLinesByDocId(String docId);
    
    // 결재선 일괄 저장 (배치)
    public void insertApprovalLines(List<ApprovalLineDTO> approvalLines);
    
} //ApprovalLineMapper