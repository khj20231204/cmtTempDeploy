/**
 * AttachmentManager - 첨부파일 관리 모듈
 * 
 * 전자결재 첨부파일 관리를 위한 기능을 제공합니다.
 * - 파일 추가/삭제 기능
 * - 파일 목록 관리 및 UI 업데이트
 * - 파일 업로드 준비 및 FormData 통합
 * - 파일 크기 및 형식 검증
 * 
 * @version 1.1.0
 * @since 2025-04-03
 * @update 2025-04-03 - SimpleGridManager 템플릿 스타일로 코드 리팩토링
 */
const AttachmentManager = (function() {
    //===========================================================================
    // 모듈 내부 변수 - 필요에 맞게 수정하세요
    //===========================================================================
    
    /**
     * 첨부 파일 목록
     * File 객체 배열
     */
    const fileList = [];
    
    /**
     * 파일 관련 상수 정의
     */
    const FILE_CONSTANTS = {
        MAX_FILE_SIZE: 20 * 1024 * 1024,  // 최대 파일 크기 (20MB)
        ALLOWED_EXTENSIONS: ['.pdf', '.doc', '.docx', '.xls', '.xlsx', '.ppt', '.pptx', '.jpg', '.jpeg', '.png', '.gif', '.zip'],
        MAX_FILES: 10                     // 최대 첨부 파일 수
    };
    
    //===========================================================================
    // 초기화 및 이벤트 처리 함수
    //===========================================================================
    
    /**
     * 모듈 초기화 함수
     * 첨부파일 관리자를 초기화하고 이벤트 리스너를 등록합니다.
     * 
     * @returns {Promise<void>}
     */
    async function initialize() {
        try {
            console.log('AttachmentManager 초기화를 시작합니다.');
            
            // 이벤트 핸들러 설정
            await setupEventHandlers();
            
            // 초기 UI 렌더링
            await renderFileList();
            
            console.log('AttachmentManager 초기화가 완료되었습니다.');
        } catch (error) {
            console.error('첨부파일 관리자 초기화 중 오류 발생:', error);
            await AlertUtil.showError('초기화 오류', '첨부파일 관리자 초기화 중 오류가 발생했습니다.');
        }
    }
    
    /**
     * 이벤트 핸들러 설정 함수
     * 파일 추가/삭제 관련 이벤트 리스너를 등록합니다.
     * 
     * @returns {Promise<void>}
     */
    async function setupEventHandlers() {
        try {
            console.log('첨부파일 이벤트 핸들러 등록을 시작합니다.');
            
            // 파일 입력 필드 - 파일 선택 시 추가
            const fileInput = document.getElementById('fileInput');
            if (fileInput) {
                fileInput.addEventListener('change', function() {
                    addFiles(this.files);
                    this.value = ''; // 파일 선택 초기화 (동일 파일 재선택 가능하도록)
                });
                console.log('파일 입력 필드 이벤트 등록 완료');
            } else {
                console.warn('fileInput 요소를 찾을 수 없습니다.');
            }
            
            // 파일 추가 버튼 클릭
            const addFilesBtn = document.getElementById('btnAddFiles');
            if (addFilesBtn) {
                addFilesBtn.addEventListener('click', function() {
                    if (fileInput) fileInput.click();
                });
                console.log('파일 추가 버튼 이벤트 등록 완료');
            } else {
                console.warn('btnAddFiles 요소를 찾을 수 없습니다.');
            }
            
            // 파일 삭제 버튼 - 동적 요소에 대한 이벤트 위임
            const fileListContainer = document.getElementById('fileList');
            if (fileListContainer) {
                fileListContainer.addEventListener('click', function(event) {
                    // 삭제 버튼 클릭 확인
                    const removeBtn = event.target.closest('.btn-remove-file');
                    if (removeBtn && removeBtn.dataset.index !== undefined) {
                        const index = parseInt(removeBtn.dataset.index);
                        removeFile(index);
                    }
                });
                console.log('파일 목록 컨테이너 이벤트 등록 완료');
            } else {
                console.warn('fileList 요소를 찾을 수 없습니다.');
            }
            
            console.log('첨부파일 이벤트 핸들러 등록이 완료되었습니다.');
        } catch (error) {
            console.error('이벤트 핸들러 등록 중 오류:', error);
            throw error;
        }
    }
    
    //===========================================================================
    // 파일 관리 함수
    //===========================================================================
    
    /**
     * 파일 추가 함수
     * FileList에서 유효한 파일을 추출하여 내부 파일 목록에 추가합니다.
     * 파일 유효성 검사를 수행하고 UI를 업데이트합니다.
     * 
     * @param {FileList} newFiles - 추가할 파일 목록
     * @returns {Promise<void>}
     */
    async function addFiles(newFiles) {
        try {
            if (!newFiles || newFiles.length === 0) {
                console.log('추가할 파일이 없습니다.');
                return;
            }
            
            console.log(`${newFiles.length}개 파일 추가 시도`);
            
            // 최대 파일 개수 검사
            if (fileList.length + newFiles.length > FILE_CONSTANTS.MAX_FILES) {
                await AlertUtil.showWarning(
                    '파일 개수 초과', 
                    `첨부파일은 최대 ${FILE_CONSTANTS.MAX_FILES}개까지 추가할 수 있습니다.`
                );
                return;
            }
            
            // 파일 유효성 검사 후 추가
            let addedCount = 0;
            let errorMessages = [];
            
            for (let i = 0; i < newFiles.length; i++) {
                const file = newFiles[i];
                
                // 파일 크기 검사
                if (file.size > FILE_CONSTANTS.MAX_FILE_SIZE) {
                    errorMessages.push(`'${file.name}': 파일 크기가 너무 큽니다. (최대 ${formatFileSize(FILE_CONSTANTS.MAX_FILE_SIZE)})`);
                    continue;
                }
                
                // 파일 확장자 검사
                const extension = '.' + file.name.split('.').pop().toLowerCase();
                if (!FILE_CONSTANTS.ALLOWED_EXTENSIONS.includes(extension)) {
                    errorMessages.push(`'${file.name}': 지원되지 않는 파일 형식입니다.`);
                    continue;
                }
                
                // 중복 파일명 검사
                const isDuplicate = fileList.some(existingFile => existingFile.name === file.name);
                if (isDuplicate) {
                    errorMessages.push(`'${file.name}': 동일한 이름의 파일이 이미 존재합니다.`);
                    continue;
                }
                
                // 유효한 파일 추가
                fileList.push(file);
                addedCount++;
            }
            
            // 오류 메시지 표시 (있는 경우)
            if (errorMessages.length > 0) {
                await AlertUtil.showWarning(
                    '일부 파일 추가 실패', 
                    errorMessages.join('\n')
                );
            }
            
            console.log(`${addedCount}개 파일이 성공적으로 추가되었습니다.`);
            
            // UI 업데이트
            await renderFileList();
        } catch (error) {
            console.error('파일 추가 중 오류:', error);
            await AlertUtil.showError('파일 추가 오류', '파일 추가 중 오류가 발생했습니다.');
        }
    }
    
    /**
     * 파일 삭제 함수
     * 지정된 인덱스의 파일을 목록에서 제거하고 UI를 업데이트합니다.
     * 
     * @param {number} index - 삭제할 파일 인덱스
     * @returns {Promise<void>}
     */
    async function removeFile(index) {
        try {
            // 유효한 인덱스 검사
            if (index < 0 || index >= fileList.length) {
                console.warn(`유효하지 않은 파일 인덱스: ${index}`);
                return;
            }
            
            const fileName = fileList[index].name;
            console.log(`파일 '${fileName}' 삭제 시도`);
            
            // 파일 목록에서 제거
            fileList.splice(index, 1);
            console.log(`파일 '${fileName}'이(가) 목록에서 제거되었습니다.`);
            
            // UI 업데이트
            await renderFileList();
        } catch (error) {
            console.error('파일 삭제 중 오류:', error);
            await AlertUtil.showError('파일 삭제 오류', '파일 삭제 중 오류가 발생했습니다.');
        }
    }
    
    //===========================================================================
    // UI 렌더링 함수
    //===========================================================================
    
    /**
     * 파일 목록 렌더링 함수
     * 현재 파일 목록을 기반으로 UI를 업데이트합니다.
     * 
     * @returns {Promise<void>}
     */
    async function renderFileList() {
        try {
            console.log('파일 목록 렌더링 시작');
            
            // 파일 목록 컨테이너 요소 조회
            const fileListContainer = document.getElementById('fileList');
            if (!fileListContainer) {
                throw new Error('fileList 요소를 찾을 수 없습니다.');
            }
            
            // 파일 목록이 비어있는 경우
            if (fileList.length === 0) {
                fileListContainer.innerHTML = '<div class="text-muted text-center">첨부된 파일이 없습니다</div>';
                return;
            }
            
            // 파일 목록 HTML 생성
            let fileListHtml = '';
            fileList.forEach((file, index) => {
                // 파일 아이콘 선택 (확장자별 다른 아이콘 표시 가능)
                const iconClass = getFileIconClass(file.name);
                
                fileListHtml += `
                    <div class="d-flex align-items-center justify-content-between mb-2">
                        <div class="file-item">
                            <i class="${iconClass} mr-2"></i>
                            <span class="ms-2">${file.name} (${formatFileSize(file.size)})</span>
                        </div>
                        <button type="button" class="btn btn-sm btn-outline-danger btn-remove-file" data-index="${index}">
                            <i class="bi bi-x"></i>
                        </button>
                    </div>
                `;
            });
            
            // HTML 삽입
            fileListContainer.innerHTML = fileListHtml;
            
            console.log('파일 목록 렌더링 완료');
        } catch (error) {
            console.error('파일 목록 렌더링 중 오류:', error);
            await AlertUtil.showError('렌더링 오류', '파일 목록 표시 중 오류가 발생했습니다.');
        }
    }
    
    //===========================================================================
    // 유틸리티 함수
    //===========================================================================
    
    /**
     * 파일 크기 포맷팅 함수
     * 바이트 단위의 파일 크기를 사람이 읽기 쉬운 형태로 변환합니다.
     * 
     * @param {number} bytes - 바이트 단위 파일 크기
     * @returns {string} 포맷팅된 파일 크기 (예: "3.45 MB")
     */
    function formatFileSize(bytes) {
        if (bytes === 0) return '0 Bytes';
        
        const k = 1024;
        const sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB'];
        const i = Math.floor(Math.log(bytes) / Math.log(k));
        
        return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
    }
    
    /**
     * 파일 아이콘 클래스 반환 함수
     * 파일 확장자에 따라 적절한 Bootstrap 아이콘 클래스를 반환합니다.
     * 
     * @param {string} fileName - 파일 이름 (확장자 포함)
     * @returns {string} Bootstrap 아이콘 클래스
     */
    function getFileIconClass(fileName) {
        const extension = fileName.split('.').pop().toLowerCase();
        
        // 파일 유형별 아이콘 매핑
        const iconMap = {
            pdf: 'bi bi-file-earmark-pdf',
            doc: 'bi bi-file-earmark-word',
            docx: 'bi bi-file-earmark-word',
            xls: 'bi bi-file-earmark-excel', 
            xlsx: 'bi bi-file-earmark-excel',
            ppt: 'bi bi-file-earmark-ppt',
            pptx: 'bi bi-file-earmark-ppt',
            jpg: 'bi bi-file-earmark-image',
            jpeg: 'bi bi-file-earmark-image',
            png: 'bi bi-file-earmark-image',
            gif: 'bi bi-file-earmark-image',
            zip: 'bi bi-file-earmark-zip',
            rar: 'bi bi-file-earmark-zip'
        };
        
        return iconMap[extension] || 'bi bi-file-earmark';
    }
    
    /**
     * 현재 파일 목록 반환 함수
     * 외부 모듈에서 파일 목록에 접근할 수 있도록 합니다.
     * 
     * @returns {Array<File>} 파일 목록 배열
     */
    function getFiles() {
        return fileList;
    }
    
    /**
     * 파일 FormData 추가 함수
     * 제공된 FormData 객체에 현재 파일 목록을 추가합니다.
     * 
     * @param {FormData} formData - 폼 데이터 객체
     */
    function appendFilesToFormData(formData) {
        try {
            if (!formData) {
                console.error('FormData 객체가 제공되지 않았습니다.');
                return;
            }
            
            // 파일 목록 FormData에 추가
            for (let i = 0; i < fileList.length; i++) {
                formData.append('files', fileList[i]);
            }
            
            console.log(`${fileList.length}개 파일이 FormData에 추가되었습니다.`);
        } catch (error) {
            console.error('FormData에 파일 추가 중 오류:', error);
            throw error;
        }
    }
    
    //===========================================================================
    // 공개 API - 외부에서 접근 가능한 메서드
    //===========================================================================
    
    return {
        // 초기화 및 기본 기능
        initialize,              // 모듈 초기화
        
        // 파일 관리 함수
        addFiles,                // 파일 추가
        removeFile,              // 파일 삭제
        
        // 데이터 접근 함수
        getFiles,                // 파일 목록 조회
        appendFilesToFormData    // FormData에 파일 추가
    };
})();

// DOM 로드 시 문서 폼에서 초기화하므로 여기서는 자동 초기화하지 않음