package com.example.cmtProject.service.erp.notice;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.cmtProject.dto.erp.notice.NoticeDTO;
import com.example.cmtProject.entity.erp.notice.Notice;
import com.example.cmtProject.mapper.erp.notice.NoticeMapper;
import com.example.cmtProject.repository.erp.notice.NoticeRepository;

@Service
public class NoticeService {
	@Autowired NoticeRepository noticeRepository;
	@Autowired NoticeMapper noticeMapper;


	public List<NoticeDTO> getAllNoticesWithNames() {
		 return noticeRepository.findAllWithDeptAndPosition();
	}

	//공지사항 insert
	public int regiNoti(NoticeDTO noticeDTO) {
		return noticeMapper.insertNotice(noticeDTO);
	}
	//공지사항 디테일
	public NoticeDTO getNoticeDetail(Long id) {
		return noticeMapper.selectNoti(id);
	}

	public int deleteById(Long noticeId) {
		return noticeMapper.deleteNoti(noticeId);
		
	}

	
	

}
