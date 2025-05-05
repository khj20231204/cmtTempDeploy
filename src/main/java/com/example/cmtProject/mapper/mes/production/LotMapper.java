package com.example.cmtProject.mapper.mes.production;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.cmtProject.dto.mes.production.LotOrderDTO;
import com.example.cmtProject.dto.mes.production.LotOriginDTO;
import com.example.cmtProject.dto.mes.production.LotStructurePathDTO;
import com.example.cmtProject.dto.mes.production.SavePRCDTO;
import com.example.cmtProject.dto.mes.production.SemiFinalBomQty;
import com.example.cmtProject.dto.mes.qualityControl.IpiDTO;
import com.example.cmtProject.dto.mes.standardInfoMgt.BomStructurePathDTO;

@Mapper
public interface LotMapper {

	//lot테이블에서 마지막 lotNo값 가져오기
	Long getLotNo();

	//LOT-20250421-PR-02 뒤에 02부분을 가져온다 AS
	LotOrderDTO getLotOrderPrcType(@Param("todayStr") String todayStr,@Param("type") String type);

	//LOT테이블에 데이터 입력
	void insertLot(LotOriginDTO lod);

	//재귀로 BOM의 path가져오기
	List<BomStructurePathDTO> selectStructurePath(String pdtCode);

	//LOT테이블에서 하단 오른쪽 그리드에 최초 보여줄 전체 데이터 PATH
	List<LotStructurePathDTO> selectStructurePathAll(@Param("woCode") String woCode,@Param("pdtCode") String pdtCode);

	//두번째 그리드에 보여줄 데이터
	List<LotOriginDTO> selectLotOrigin(String woCode);

	//두번째 그리드에서 현재 공정 업데이트
	void updateLotPresentPRC(LotOriginDTO lotOrigin);

	//작업 완료 버튼 클릭시 이후 공정 startTime 입력
	void updateLotNextPRC(@Param("nextLotNo") Long nextLotNo, @Param("startTime") String startTime);

	//완제품 작업 완료시 작업 지시서 CP로 업데이트
	void updateWOtoCP(String woCode);

	//save_prc테이블에 데이터가 있는지 없는지 확인
	int selectCheckSavePRC();

	//QI가 있는지 없는지 확인
	String selectCheckQI(String woCode);

	//rnRowNum 값으로 현재 작업 중인 rn상태의 max rownum
	String selectRNRowNum(@Param("nowWoCode") String nowWoCode);

	//save_prc테이블에서 데이터 가져오기 
	List<SavePRCDTO> selectSavePRC();

	//rnRowNum 값으로 전체데이터의 max rownum값
	Integer selectRnRowNumMax(String woCode);

	//save_prc테이블에 데이터 넣기
	void insertSavePrc(SavePRCDTO savePrcDto);

	//save_prc테이블에 데이터 삭제
	void deleteSavePrc();

	//반제품 수량 넘겨 받기
	List<SemiFinalBomQty> getBomQty(String woCode);

	//반제품 수량을 입력 할 parentPdtCode
	List<String> selectParentPdtCode(String pdtCode);

	//IPI 테이블에서 IPI_NO 받아오기
	Long getIpiNo();

	//작업지시서 종료 날짜 업데이트
	void updateWoEndDate(@Param("woCode") String woCode, @Param("today") String today);

	//IPI 테이블에 입력
	void insertIpi(IpiDTO ipidto);

	//MFG_SCHEDULES 상태 업데이트
	void updateMfgScdStatus(@Param("woCode") String woCode, @Param("mfgscd") String mfgscd);

	//MFG_PLANS 상태 업데이트
	void updateMfgPlanStatus(@Param("woCode") String woCode, @Param("mfgPlan") String mfgPlan);
	
	//List<LotOrderDTO> getLotOrderPrcType(@Param("todayStr") String todayStr,@Param("type") String type);

}
