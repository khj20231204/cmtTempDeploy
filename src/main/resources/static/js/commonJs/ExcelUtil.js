/**
 * ExcelUtil - 엑셀 처리 공통 라이브러리
 * 
 * 그리드 데이터의 엑셀 변환 및 엑셀 파일 처리를 위한 공통 모듈
 * XLSX 라이브러리 기반으로 동작합니다.
 * 
 * @version 1.0.0
 * @since 2025-04-21
 */

const ExcelUtil = (function() {
    // =============================
    // 엑셀 다운로드 관련 함수
    // =============================

    /**
     * 그리드 데이터를 엑셀 파일로 다운로드
     * 
     * @param {Object} options - 다운로드 옵션
     * @param {string} options.gridId - 대상 그리드 ID
     * @param {string} [options.fileName='grid-data.xlsx'] - 다운로드 파일명
     * @param {string} [options.sheetName='Sheet1'] - 시트 이름
     * @param {boolean} [options.includeHidden=false] - 숨겨진 컬럼 포함 여부
     * @param {Function} [options.beforeDownload] - 다운로드 전 실행할 함수
     * @param {Function} [options.afterDownload] - 다운로드 후 실행할 함수
     * @param {Function} [options.customDataProcessor] - 데이터 가공을 위한 사용자 정의 함수
     * 
     * @returns {boolean} 다운로드 성공 여부
     */
    function downloadGridToExcel(options) {
        try {
            console.log('엑셀 다운로드 시작:', options.gridId);

            // 옵션 기본값 설정
            const fileName = options.fileName || 'grid-data.xlsx';
            const sheetName = options.sheetName || 'Sheet1';
            const includeHidden = options.includeHidden === true;

            // 그리드 참조 가져오기
            const grid = GridUtil.getGrid(options.gridId);
            if (!grid) {
                console.error(`ID가 '${options.gridId}'인 그리드를 찾을 수 없습니다.`);
                return false;
            }

            // 다운로드 전 콜백 실행
            if (options.beforeDownload && typeof options.beforeDownload === 'function') {
                const shouldContinue = options.beforeDownload();
                if (shouldContinue === false) {
                    return false;
                }
            }

            // 현재 그리드 데이터 가져오기
            const gridData = grid.getData();

            // 컬럼 정보 가져오기
            let columns = grid.getColumns();

            // 숨김 컬럼 필터링
            if (!includeHidden) {
                columns = columns.filter(col => !col.hidden);
            }

            // 컬럼을 헤더와 이름으로 분리
            const header = columns.map(col => col.header);
            const keys = columns.map(col => col.name);

            // 엑셀 데이터 생성
            let exportData = [header];

            // 사용자 정의 데이터 가공 함수 사용(있는 경우)
            if (options.customDataProcessor && typeof options.customDataProcessor === 'function') {
                const processedData = options.customDataProcessor(gridData, columns);
                if (Array.isArray(processedData)) {
                    exportData = [header, ...processedData];
                }
            } else {
                // 기본 데이터 가공: 각 행의 데이터 추가
                gridData.forEach(row => {
                    const rowData = keys.map(key => row[key]);
                    exportData.push(rowData);
                });
            }

            // XLSX 라이브러리 사용하여 엑셀 생성
            const worksheet = XLSX.utils.aoa_to_sheet(exportData);
            const workbook = XLSX.utils.book_new();

            XLSX.utils.book_append_sheet(workbook, worksheet, sheetName);
            XLSX.writeFile(workbook, fileName);

            // 다운로드 후 콜백 실행
            if (options.afterDownload && typeof options.afterDownload === 'function') {
                options.afterDownload();
            }

            console.log('엑셀 다운로드 완료:', fileName);
            return true;
        } catch (error) {
            console.error('엑셀 다운로드 중 오류 발생:', error);
            return false;
        }
    }

    /**
     * 그리드 다운로드 버튼에 이벤트 연결
     * 
     * @param {Object} options - 설정 옵션
     * @param {string} options.buttonId - 다운로드 버튼 ID
     * @param {string} options.gridId - 대상 그리드 ID
     * @param {string} [options.fileName='grid-data.xlsx'] - 다운로드 파일명
     * @param {string} [options.sheetName='Sheet1'] - 시트 이름
     * @param {boolean} [options.includeHidden=false] - 숨겨진 컬럼 포함 여부
     * @param {Function} [options.beforeDownload] - 다운로드 전 실행할 함수
     * @param {Function} [options.afterDownload] - 다운로드 후 실행할 함수
     * 
     * @returns {boolean} 설정 성공 여부
     */
    function setupExcelDownloadButton(options) {
        try {
            // 필수 옵션 검증
            if (!options.buttonId || !options.gridId) {
                console.error('필수 옵션이 누락되었습니다: buttonId, gridId');
                return false;
            }

            const downloadButton = document.getElementById(options.buttonId);
            if (!downloadButton) {
                console.error(`ID가 '${options.buttonId}'인 버튼을 찾을 수 없습니다.`);
                return false;
            }

            // 다운로드 버튼 클릭 이벤트 설정
            downloadButton.addEventListener('click', function() {
                downloadGridToExcel({
                    gridId: options.gridId,
                    fileName: options.fileName || 'grid-data.xlsx',
                    sheetName: options.sheetName || 'Sheet1',
                    includeHidden: options.includeHidden === true,
                    beforeDownload: options.beforeDownload,
                    afterDownload: options.afterDownload,
                    customDataProcessor: options.customDataProcessor
                });
            });

            return true;
        } catch (error) {
            console.error('엑셀 다운로드 버튼 설정 중 오류 발생:', error);
            return false;
        }
    }

    // =============================
    // 엑셀 업로드 관련 함수
    // =============================

    /**
     * 엑셀 파일을 그리드에 로드
     * 
     * @param {Object} options - 업로드 옵션
     * @param {File} options.file - 업로드할 엑셀 파일 객체
     * @param {string} options.gridId - 대상 그리드 ID
     * @param {Object} [options.headerMapping] - 엑셀 헤더와 그리드 컬럼명 매핑 (예: {'엑셀헤더': 'gridColumnName'})
     * @param {boolean} [options.hasHeader=true] - 엑셀 파일의 첫 번째 행이 헤더인지 여부
     * @param {Function} [options.beforeLoad] - 로드 전 실행할 함수
     * @param {Function} [options.afterLoad] - 로드 후 실행할 함수
     * @param {Function} [options.customDataProcessor] - 데이터 가공을 위한 사용자 정의 함수
     * @param {string} [options.apiUrl] - 데이터 저장 API URL (있는 경우 로드 후 바로 저장)
     * 
     * @returns {Promise<boolean>} 로드 성공 여부를 담은 Promise
     */
    async function loadExcelToGrid(options) {
        try {
            console.log('엑셀 업로드 시작');

            // 필수 옵션 검증
            if (!options.file || !options.gridId) {
                console.error('필수 옵션이 누락되었습니다: file, gridId');
                return false;
            }

            // 그리드 참조 가져오기
            const grid = GridUtil.getGrid(options.gridId);
            if (!grid) {
                console.error(`ID가 '${options.gridId}'인 그리드를 찾을 수 없습니다.`);
                return false;
            }

            // 로드 전 콜백 실행
            if (options.beforeLoad && typeof options.beforeLoad === 'function') {
                const shouldContinue = options.beforeLoad();
                if (shouldContinue === false) {
                    return false;
                }
            }

            // 파일 읽기
            const data = await readExcelFile(options.file);
            if (!data) {
                return false;
            }

            // 워크시트 가져오기
            const workbook = XLSX.read(data, {
                type: 'array'
            });
            const firstSheetName = workbook.SheetNames[0];
            const worksheet = workbook.Sheets[firstSheetName];

            // 헤더 매핑 적용 (있는 경우)
            if (options.headerMapping) {
                applyHeaderMapping(worksheet, options.headerMapping);
            }

            // JSON 데이터로 변환
            const jsonData = XLSX.utils.sheet_to_json(worksheet, {
                defval: "",
                header: options.hasHeader === false ? 1 : undefined
            });

            // 사용자 정의 데이터 가공 함수 사용 (있는 경우)
            let processedData = jsonData;
            if (options.customDataProcessor && typeof options.customDataProcessor === 'function') {
                processedData = options.customDataProcessor(jsonData);
            }

            // 그리드에 데이터 적용
            grid.resetData(processedData);

            // 데이터 저장 처리 (API URL이 있는 경우)
            let saveResult = true;
            if (options.apiUrl) {
                try {
                    const response = await fetch(options.apiUrl, {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/json'
                        },
                        body: JSON.stringify(processedData)
                    });

                    if (!response.ok) {
                        throw new Error('저장 실패: ' + response.statusText);
                    }

                    // 성공 메시지 표시
                    if (window.Swal) {
                        await Swal.fire({
                            icon: 'success',
                            title: '업로드 완료',
                            text: '엑셀 업로드 및 저장이 완료되었습니다.',
                            confirmButtonText: '확인'
                        });
                    } else if (window.AlertUtil) {
                        await AlertUtil.showSuccess('업로드 완료', '엑셀 업로드 및 저장이 완료되었습니다.');
                    } else {
                        alert('엑셀 업로드 및 저장이 완료되었습니다.');
                    }
                } catch (error) {
                    console.error('데이터 저장 중 오류 발생:', error);

                    // 오류 메시지 표시
                    if (window.Swal) {
                        await Swal.fire({
                            icon: 'error',
                            title: '저장 오류',
                            text: '엑셀 저장 중 오류가 발생했습니다.',
                            confirmButtonText: '닫기'
                        });
                    } else if (window.AlertUtil) {
                        await AlertUtil.showError('저장 오류', '엑셀 저장 중 오류가 발생했습니다.');
                    } else {
                        alert('엑셀 저장 중 오류가 발생했습니다.');
                    }

                    saveResult = false;
                }
            }

            // 로드 후 콜백 실행
            if (options.afterLoad && typeof options.afterLoad === 'function') {
                options.afterLoad(processedData, saveResult);
            }

            console.log('엑셀 업로드 완료');
            return true;
        } catch (error) {
            console.error('엑셀 업로드 중 오류 발생:', error);
            return false;
        }
    }

    /**
     * 엑셀 파일 읽기 (Promise 기반)
     * 
     * @param {File} file - 엑셀 파일
     * @returns {Promise<ArrayBuffer|null>} 파일 데이터 또는 오류 시 null
     * @private
     */
    function readExcelFile(file) {
        return new Promise((resolve, reject) => {
            const reader = new FileReader();

            reader.onload = function(event) {
                resolve(event.target.result);
            };

            reader.onerror = function(event) {
                console.error('파일 읽기 오류:', event);
                reject(null);
            };

            reader.readAsArrayBuffer(file);
        });
    }


    /**
     * 워크시트에 헤더 매핑 적용
     * 
     * @param {Object} worksheet - XLSX 워크시트 객체
     * @param {Object} headerMapping - 엑셀 헤더와 그리드 컬럼명 매핑
     * @private
     */
    function applyHeaderMapping(worksheet, headerMapping) {
        // 첫 번째 행(헤더)의 셀들을 순회
        const range = XLSX.utils.decode_range(worksheet['!ref']);
        for (let col = range.s.c; col <= range.e.c; col++) {
            const cellAddress = XLSX.utils.encode_cell({
                r: 0,
                c: col
            });
            const cell = worksheet[cellAddress];

            if (cell && cell.v && headerMapping[cell.v]) {
                // 헤더 값을 매핑된 값으로 변경
                const newValue = headerMapping[cell.v];
                worksheet[cellAddress] = {
                    t: 's',
                    v: newValue,
                    r: `<t>${newValue}</t>`,
                    h: newValue,
                    w: newValue
                };
            }
        }
    }

    /**
     * 엑셀 업로드 버튼과 파일 입력에 이벤트 연결
     * 
     * @param {Object} options - 설정 옵션
     * @param {string} options.fileInputId - 파일 입력 요소 ID
     * @param {string} options.uploadButtonId - 업로드 버튼 ID
     * @param {string} options.gridId - 대상 그리드 ID
     * @param {Object} [options.headerMapping] - 엑셀 헤더와 그리드 컬럼명 매핑
     * @param {boolean} [options.hasHeader=true] - 엑셀 파일의 첫 번째 행이 헤더인지 여부
     * @param {Function} [options.beforeLoad] - 로드 전 실행할 함수
     * @param {Function} [options.afterLoad] - 로드 후 실행할 함수
     * @param {Function} [options.customDataProcessor] - 데이터 가공을 위한 사용자 정의 함수
     * @param {string} [options.apiUrl] - 데이터 저장 API URL
     * 
     * @returns {boolean} 설정 성공 여부
     */
    function setupExcelUploadButton(options) {
        try {
            // 필수 옵션 검증
            if (!options.fileInputId || !options.uploadButtonId || !options.gridId) {
                console.error('필수 옵션이 누락되었습니다: fileInputId, uploadButtonId, gridId');
                return false;
            }

            const fileInput = document.getElementById(options.fileInputId);
            const uploadButton = document.getElementById(options.uploadButtonId);

            if (!fileInput || !uploadButton) {
                console.error('파일 입력 또는 업로드 버튼 요소를 찾을 수 없습니다.');
                return false;
            }

            // 선택된 파일 참조를 저장할 변수
            let selectedFile = null;

            // 파일 선택 이벤트 처리
            fileInput.addEventListener('change', function(e) {
                selectedFile = e.target.files[0];

                if (!selectedFile) {
                    // 경고 메시지 표시
                    if (window.Swal) {
                        Swal.fire({
                            icon: 'warning',
                            title: '파일 없음',
                            text: '파일을 선택해 주세요.',
                            confirmButtonText: '확인'
                        });
                    } else if (window.AlertUtil) {
                        AlertUtil.showWarning('파일 없음', '파일을 선택해 주세요.');
                    } else {
                        alert('파일을 선택해 주세요.');
                    }
                    return;
                }
            });

            // 업로드 버튼 클릭 이벤트 처리
            uploadButton.addEventListener('click', async function() {
                if (!selectedFile) {
                    // 파일이 선택되지 않은 경우 파일 선택 다이얼로그 표시
                    fileInput.click();
                    return;
                }

                // 엑셀 파일 로드 및 처리
                await loadExcelToGrid({
                    file: selectedFile,
                    gridId: options.gridId,
                    headerMapping: options.headerMapping,
                    hasHeader: options.hasHeader !== false,
                    beforeLoad: options.beforeLoad,
                    afterLoad: options.afterLoad,
                    customDataProcessor: options.customDataProcessor,
                    apiUrl: options.apiUrl
                });

                // 처리 후 파일 입력 초기화
                fileInput.value = '';
                selectedFile = null;
            });

            return true;
        } catch (error) {
            console.error('엑셀 업로드 버튼 설정 중 오류 발생:', error);
            return false;
        }
    }

    /**
     * 사용자 정의 헤더 워크시트 적용
     * 엑셀 헤더를 사용자 정의 형태로 설정합니다.
     * 
     * @param {Object} worksheet - XLSX 워크시트 객체
     * @param {Object} customHeaders - 컬럼 인덱스(A1, B1 등)와 헤더 정보 매핑 객체
     * @private
     */
    function applyCustomHeaders(worksheet, customHeaders) {
        // 각 헤더 정의 적용
        Object.keys(customHeaders).forEach(cellKey => {
            worksheet[cellKey] = customHeaders[cellKey];
        });
    }

    // 공개 API
    return {
        // 엑셀 다운로드 관련
        downloadGridToExcel, // 그리드 데이터 엑셀 파일로 다운로드
        setupExcelDownloadButton, // 다운로드 버튼 설정

        // 엑셀 업로드 관련
        loadExcelToGrid, // 엑셀 파일 그리드에 로드
        setupExcelUploadButton // 업로드 버튼 설정
    };
})();