/**
 * commonGrid.js - TOAST UI Grid 확장 유틸리티
 * 
 * TOAST UI Grid 기본 기능을 확장하여 프로젝트 특화 기능 및 공통 패턴을 제공합니다.
 * 프로젝트에서 필요한 ROW_TYPE 관리 및 비즈니스 로직을 중앙화합니다.
 * 
 * @version 1.3.0
 * @since 2025-04-25
 */

const GridUtil = (function() {
    // 그리드 인스턴스를 저장하는 객체
    const _gridInstances = {};

    // =============================
    // 그리드 인스턴스 관리 함수
    // =============================

    /**
     * 그리드 인스턴스 생성 및 등록 함수
     * 기본 TOAST UI Grid 인스턴스를 생성하고 내부 저장소에 등록합니다.
     * 
     * @param {Object} options - 그리드 생성 옵션
     * @param {string} options.id - 그리드를 생성할 HTML 요소의 ID
     * @param {Array} options.columns - 컬럼 정의 배열
     * @param {Array} [options.data=[]] - 초기 데이터 배열
     * @param {Array} [options.hiddenColumns=[]] - 숨길 컬럼명 배열
     * @param {boolean} [options.draggable=false] - 행 드래그 가능 여부
     * @param {string} [options.displayColumnName=''] - 드래그 시 자동 정렬에 사용할 컬럼명
     * @param {Object} [options.gridOptions={}] - Toast UI Grid 추가 옵션
     * @param {boolean} [options.toggleRowCheckedOnClick=false] - 행 클릭 시 체크박스 토글 여부
     * @param {Function} [options.onInitialized] - 그리드 초기화 후 실행될 콜백 함수
     * 
     * @returns {Object} 생성된 TOAST UI Grid 인스턴스
     */
    function registerGrid(options) {
        try {
            // 필수 옵션 확인
            if (!options.id || !options.columns) {
                throw new Error('그리드 ID와 컬럼 정의는 필수입니다.');
            }

            // 대상 요소 확인
            const targetElement = document.getElementById(options.id);
            if (!targetElement) {
                throw new Error(`ID가 '${options.id}'인 HTML 요소를 찾을 수 없습니다.`);
            }

            const Grid = tui.Grid;

            // 기본 그리드 옵션
            const defaultOptions = {
                scrollX: true,
                scrollY: true,
                rowHeaders: ['rowNum']
            };

            // options에서 내부 처리용 속성들을 제외한 모든 속성을 추출
            const { 
                id, 
                hiddenColumns, 
                displayColumnName, 
                onInitialized, 
                toggleRowCheckedOnClick,
                gridOptions = {}, 
                ...otherOptions 
            } = options;

            // 최종 옵션 구성 (우선순위: 1. gridOptions 2. otherOptions 3. defaultOptions)
            const finalOptions = {
                ...defaultOptions,
                ...otherOptions,
                ...gridOptions,
                el: targetElement,
                columns: options.columns,
                data: options.data || []
            };

            // 그리드 생성 - 모든 옵션을 전달
            const gridInstance = new Grid(finalOptions);

            // 숨김 컬럼 처리
            if (hiddenColumns && hiddenColumns.length > 0) {
                hiddenColumns.forEach(column => {
                    gridInstance.hideColumn(column);
                });
            }

            // 드래그앤드롭 이벤트 (순서 컬럼이 있는 경우)
            if (options.draggable && displayColumnName) {
                gridInstance.on('drop', () => {
                    _updateRowOrder(gridInstance, displayColumnName);
                });
            }
            
            // 행 클릭 시 체크박스 토글 기능 설정
            if (toggleRowCheckedOnClick === true) {
                gridInstance.on('click', ev => {
                    const { rowKey, columnName } = ev;
                    // 체크박스 영역 클릭은 무시 (기본 토글 동작 유지)
                    if (columnName === '_checked') return;
                    
                    // 현재 체크 상태 확인 후 토글
                    const isChecked = gridInstance.getCheckedRowKeys().includes(rowKey);
                    if (!isChecked) {
                        gridInstance.check(rowKey);
                    } else {
                        gridInstance.uncheck(rowKey);
                    }
                });
            }

            // 인스턴스 저장
            _gridInstances[id] = gridInstance;

            // 초기화 완료 콜백 실행
            if (typeof onInitialized === 'function') {
                onInitialized(gridInstance);
            }

            return gridInstance;
        } catch (error) {
            console.error('그리드 생성 중 오류 발생:', error);
            if (window.AlertUtil) {
                AlertUtil.showError('그리드 초기화 오류', error.message);
            }
            return null;
        }
    }

    /**
     * 그리드 인스턴스 가져오기
     * 내부 저장소에서 그리드 ID로 인스턴스를 조회합니다.
     * 
     * @param {string} gridId - 그리드 ID
     * @returns {Object|null} TOAST UI Grid 인스턴스 또는 null
     */
    function getGrid(gridId) {
        const grid = _gridInstances[gridId];
        if (!grid) {
            console.warn(`ID가 '${gridId}'인 그리드를 찾을 수 없습니다.`);
        }
        return grid;
    }

    /**
     * 그리드 인스턴스 제거 함수
     * 메모리 누수 방지를 위해 그리드 인스턴스를 제거합니다.
     * 
     * @param {string} gridId - 그리드 ID
     * @returns {boolean} 제거 성공 여부
     */
    function unregisterGrid(gridId) {
        try {
            const grid = _gridInstances[gridId];
            if (!grid) {
                console.warn(`ID가 '${gridId}'인 그리드를 찾을 수 없습니다.`);
                return false;
            }

            // 이벤트 핸들러 제거
            removeEventHandlers(gridId);

            // 인스턴스 제거
            delete _gridInstances[gridId];

            return true;
        } catch (error) {
            console.error('그리드 리소스 정리 중 오류:', error);
            return false;
        }
    }

    // =============================
    // 프로젝트 특화 기능 - 비즈니스 로직
    // =============================

    /**
     * 변경된 데이터 추출 함수 (신규, 수정, 삭제 항목)
     * ROW_TYPE 기반으로 변경된 데이터만 추출합니다.
     * 
     * @param {string} gridId - 그리드 ID
     * @param {Object} [options] - 추출 옵션
     * @param {Array<string>} [options.types=['insert', 'update', 'delete']] - 추출할 ROW_TYPE
     * @param {Array<string>} [options.columns] - 추출할 특정 컬럼 이름 배열
     * 
     * @returns {Promise<Object>} 유형별로 분류된 변경 데이터 객체
     */
    async function extractChangedData(gridId, options = {}) {
        return new Promise((resolve, reject) => {
            try {
                const grid = _gridInstances[gridId];
                if (!grid) {
                    throw new Error(`ID가 '${gridId}'인 그리드를 찾을 수 없습니다.`);
                }

                const types = options.types || ['insert', 'update', 'delete'];
                const result = {
                    insert: [],
                    update: [],
                    delete: []
                };

                // 모든 데이터 가져오기
                let allData = grid.getData();

                // 유형별로 분류
                types.forEach(type => {
                    const filteredData = allData.filter(row => row.ROW_TYPE === type);
                    
                    // 특정 컬럼만 추출
                    if (Array.isArray(options.columns) && options.columns.length > 0) {
                        result[type] = filteredData.map(row => {
                            const extractedRow = {};
                            options.columns.forEach(column => {
                                if (column in row) {
                                    extractedRow[column] = row[column];
                                }
                            });
                            return extractedRow;
                        });
                    } else {
                        result[type] = filteredData;
                    }
                });

                resolve(result);
            } catch (error) {
                console.error('변경 데이터 추출 중 오류:', error);
                reject(error);
            }
        });
    }

    /**
     * 신규 행 추가 함수 (ROW_TYPE 관리 포함)
     * TOAST UI Grid에 새 행을 추가하고 자동으로 ROW_TYPE을 'insert'로 설정합니다.
     * 
     * @param {string} gridId - 그리드 ID
     * @param {Object} rowData - 추가할 행 데이터
     * @param {Object} [options] - TOAST UI Grid appendRow 옵션
     * 
     * @returns {Promise<number>} 추가된 행의 rowKey
     */
    async function addNewRow(gridId, rowData, options = {}) {
        return new Promise((resolve, reject) => {
            try {
                const grid = _gridInstances[gridId];
                if (!grid) {
                    throw new Error(`ID가 '${gridId}'인 그리드를 찾을 수 없습니다.`);
                }

                // ROW_TYPE 필드 자동 설정
                const finalRowData = {
                    ...rowData,
                    ROW_TYPE: 'insert'
                };

                // 원본 TOAST UI Grid API 호출
                const rowKey = grid.appendRow(finalRowData, options);
                resolve(rowKey);
            } catch (error) {
                console.error('행 추가 중 오류:', error);
                reject(error);
            }
        });
    }

    /**
     * 행 데이터 수정 함수 (ROW_TYPE 관리 포함)
     * 행 데이터를 업데이트하고 ROW_TYPE을 적절히 변경합니다.
     * 
     * @param {string} gridId - 그리드 ID 
     * @param {number} rowKey - 수정할 행의 rowKey
     * @param {Object} rowData - 수정할 데이터
     * 
     * @returns {Promise<boolean>} 성공 여부
     */
    async function updateRowData(gridId, rowKey, rowData) {
        return new Promise((resolve, reject) => {
            try {
                const grid = _gridInstances[gridId];
                if (!grid) {
                    throw new Error(`ID가 '${gridId}'인 그리드를 찾을 수 없습니다.`);
                }

                // 현재 행 데이터 가져오기
                const currentRowData = grid.getRow(rowKey);
                if (!currentRowData) {
                    throw new Error(`rowKey ${rowKey}에 해당하는 행을 찾을 수 없습니다.`);
                }

                // ROW_TYPE 업데이트 (신규가 아닌 경우 'update'로 설정)
                if (currentRowData.ROW_TYPE !== 'insert') {
                    grid.setValue(rowKey, 'ROW_TYPE', 'update');
                }

                // 원본 TOAST UI Grid API를 사용하여 값 업데이트
                Object.keys(rowData).forEach(fieldName => {
                    grid.setValue(rowKey, fieldName, rowData[fieldName]);
                });

                resolve(true);
            } catch (error) {
                console.error('행 업데이트 중 오류:', error);
                reject(error);
            }
        });
    }

    /**
     * 선택된 행 삭제 함수 (확인 대화상자 및 콜백 포함)
     * 선택된 행을 삭제하고 확인 프로세스 및 콜백을 처리합니다.
     * 
     * @param {string} gridId - 그리드 ID
     * @param {Object} [options] - 삭제 옵션
     * @param {string} [options.confirmTitle='삭제 확인'] - 확인 대화상자 제목
     * @param {string} [options.confirmMessage='선택한 항목을 삭제하시겠습니까?'] - 확인 메시지
     * @param {boolean} [options.showConfirm=true] - 확인 대화상자 표시 여부
     * @param {Function} [options.onBeforeDelete] - 삭제 전 호출될 콜백 함수
     * @param {Function} [options.onAfterDelete] - 삭제 후 호출될 콜백 함수
     * 
     * @returns {Promise<Array<number>|false>} 삭제된 행의 rowKey 배열 또는 실패 시 false
     */
    async function deleteSelectedRows(gridId, options = {}) {
        try {
            const grid = _gridInstances[gridId];
            if (!grid) {
                throw new Error(`ID가 '${gridId}'인 그리드를 찾을 수 없습니다.`);
            }

            const selectedRowKeys = grid.getCheckedRowKeys();

            if (selectedRowKeys.length === 0) {
                await AlertUtil.showWarning('삭제할 항목을 선택해주세요.');
                return false;
            }

            // 삭제 전 콜백 호출
            if (options.onBeforeDelete) {
                const shouldProceed = await options.onBeforeDelete(selectedRowKeys);
                if (shouldProceed === false) {
                    return false;
                }
            }

            // 확인 대화상자 표시 여부
            const showConfirm = options.showConfirm !== undefined ? options.showConfirm : true;

            if (showConfirm) {
                const confirmed = await AlertUtil.showConfirm({
                    title: options.confirmTitle || '삭제 확인',
                    text: options.confirmMessage || '선택한 항목을 삭제하시겠습니까?',
                    icon: 'warning'
                });

                if (!confirmed) {
                    return false;
                }
            }

            // 행 삭제 - 원본 TOAST UI Grid API 사용
            grid.removeRows(selectedRowKeys);

            // 삭제 후 콜백 호출
            if (options.onAfterDelete) {
                options.onAfterDelete(selectedRowKeys);
            }

            return selectedRowKeys;
        } catch (error) {
            console.error('행 삭제 중 오류:', error);
            await AlertUtil.notifyDeleteError('삭제 중 오류가 발생했습니다.', error.message);
            return false;
        }
    }

    /**
     * 그리드 행 순서 업데이트 내부 함수
     * 드래그앤드롭으로 행 순서가 변경된 후, 특정 컬럼에 순서값을 자동 업데이트합니다.
     * 
     * @param {Object} grid - 그리드 인스턴스
     * @param {string} displayColumnName - 순서를 저장할 컬럼명
     * @private
     */
    function _updateRowOrder(grid, displayColumnName) {
        const rowData = grid.getData();
        rowData.forEach((row, index) => {
            grid.setValue(row.rowKey, displayColumnName, index + 1);
        });
    }

	// ====================================================== 추가
	// =============================
    // 상태 포맷터 및 이벤트 처리 - 간소화 버전
    // =============================

    /**
     * 상태 표시 포맷터 생성 함수 (간소화 버전)
     * 상태값과 스타일을 시각적으로 표시하는 포맷터를 생성합니다.
     * 클릭 이벤트는 그리드의 click 이벤트에서 별도로 처리해야 합니다.
     * 
     * @param {Object} options - 포맷터 옵션
     * @param {Object} options.styles - 상태별 스타일 클래스 매핑 객체
     * @param {string} [options.defaultState] - 기본 상태값
     * 
     * @returns {Function} 생성된 포맷터 함수
     */
	function createStatusFormatter(options) {
	    // 간단하게 직접 formatter 함수 반환
	    return function(obj) {
	        const value = obj.value || options.defaultState;
	        const state = String(value);
	        
	        // 색상 매핑
	        const colorMap = {
	            '대기': '#6c757d',
	            '검수중': '#007bff',
	            '완료': '#28a745',
	            '취소': '#dc3545'
	        };
	        
	        const bgColor = colorMap[state] || '#6c757d';
	        
	        // 단순 HTML 문자열 반환
	        return `<span style="display:inline-block; width:100%; text-align:center; padding:2px 5px; background-color:${bgColor}; color:white; border-radius:3px;">${state}</span>`;
	    };
	}

    /**
     * 상태값 변경 함수
     * 지정된 행의 상태값을 변경하고 ROW_TYPE도 적절히 업데이트합니다.
     * 
     * @param {string} gridId - 그리드 ID
     * @param {number} rowKey - 행 키
     * @param {string} columnName - 상태 컬럼명
     * @param {Object} statusConfig - 상태 설정 객체
     * @returns {Promise<boolean>} 성공 여부
     */
    async function changeStatus(gridId, rowKey, columnName, statusConfig) {
        try {
            const grid = _gridInstances[gridId];
            if (!grid) {
                throw new Error(`ID가 '${gridId}'인 그리드를 찾을 수 없습니다.`);
            }

            const rowData = grid.getRow(rowKey);
            if (!rowData) {
                throw new Error(`rowKey ${rowKey}에 해당하는 행을 찾을 수 없습니다.`);
            }

            const currentState = rowData[columnName] || statusConfig.defaultState;
            const currentIndex = statusConfig.states.indexOf(currentState);
            
            if (currentIndex === -1) {
                throw new Error(`현재 상태 '${currentState}'가 상태 목록에 없습니다.`);
            }
            
            // 다음 상태 계산
            const nextIndex = (currentIndex + 1) % statusConfig.states.length;
            const nextState = statusConfig.states[nextIndex];
            
            // 상태 변경 확인
            const confirmed = await AlertUtil.showConfirm({
                title: '상태 변경',
                text: `'${currentState}'에서 '${nextState}'로 변경하시겠습니까?`,
                icon: 'question'
            });
            
            if (!confirmed) {
                return false;
            }
            
            // 상태 변경
            grid.setValue(rowKey, columnName, nextState);
            
            // ROW_TYPE 업데이트
            if (statusConfig.updateRowType && rowData.ROW_TYPE !== 'insert') {
                grid.setValue(rowKey, 'ROW_TYPE', 'update');
            }
            
            // 콜백 호출
            if (typeof statusConfig.onStateChange === 'function') {
                statusConfig.onStateChange(rowKey, currentState, nextState, grid);
            }
            
            return true;
        } catch (error) {
            console.error('상태 변경 중 오류:', error);
            await AlertUtil.showError('상태 변경 오류', error.message);
            return false;
        }
    }

    /**
     * 상태 변경 이벤트 처리 설정 함수
     * 지정된 그리드에 상태 클릭 이벤트 핸들러를 등록합니다.
     * 
     * @param {string} gridId - 그리드 ID
     * @param {string} statusColumnName - 상태 컬럼명
     * @param {Object} statusConfig - 상태 설정 객체
     * @returns {boolean} 설정 성공 여부
     */
    function setupStatusChangeEvent(gridId, statusColumnName, statusConfig) {
        try {
            const grid = _gridInstances[gridId];
            if (!grid) {
                throw new Error(`ID가 '${gridId}'인 그리드를 찾을 수 없습니다.`);
            }

            // 그리드 클릭 이벤트에 핸들러 추가
            grid.on('click', ev => {
                // 상태 컬럼 클릭 시 상태 변경 처리
                if (ev.columnName === statusColumnName) {
                    changeStatus(gridId, ev.rowKey, statusColumnName, statusConfig);
                }
            });

            return true;
        } catch (error) {
            console.error('상태 변경 이벤트 설정 중 오류:', error);
            return false;
        }
    }
	// ====================================================== 추가
    // =============================
    // 프로젝트 특화 에디터 및 유틸리티
    // =============================

    /**
     * Y/N 드롭다운 에디터 생성 함수
     * 
     * @param {Array} [customItems] - 커스텀 선택 항목(기본 Y/N 외에 추가할 항목)
     * @returns {Object} Y/N 드롭다운 에디터 정의
     */
    function createYesNoEditor(customItems = []) {
        const defaultItems = [{
                text: 'Y',
                value: 'Y'
            },
            {
                text: 'N',
                value: 'N'
            }
        ];

        return {
            type: 'select',
            options: {
                listItems: [...defaultItems, ...customItems]
            }
        };
    }

    /**
     * 키 컬럼 제어 설정 함수
     * ROW_TYPE에 따라 특정 컬럼을 비활성화/활성화합니다.
     * 
     * @param {string} gridId - 그리드 ID
     * @param {string|Array<string>} keyColumns - 제어할 키 컬럼명 또는 컬럼명 배열
     * @returns {boolean} 설정 성공 여부
     */
    function setupKeyColumnControl(gridId, keyColumns) {
        try {
            const grid = _gridInstances[gridId];
            if (!grid) {
                throw new Error(`ID가 '${gridId}'인 그리드를 찾을 수 없습니다.`);
            }

            // 문자열을 배열로 변환
            const columns = Array.isArray(keyColumns) ? keyColumns : [keyColumns];

            // 더블클릭 이벤트보다 선행하는 editingStart 이벤트 사용
            grid.on('editingStart', ev => {
                const rowData = grid.getRow(ev.rowKey);
                const rowType = rowData?.ROW_TYPE;

                // 키 컬럼에 해당하는지 확인
                if (columns.includes(ev.columnName)) {
                    // INSERT가 아닌 데이터의 키 컬럼 편집 방지
                    if (rowType !== 'insert') {
                        // 기존 데이터는 키 컬럼 편집 취소
                        ev.stop();
                        console.log(`키 컬럼(${ev.columnName}) 편집이 방지되었습니다. 행 타입: ${rowType}`);
                    }
                }
            });

            return true;
        } catch (error) {
            console.error('키 컬럼 제어 설정 중 오류:', error);
            return false;
        }
    }

    // =============================
    // 이벤트 관리 유틸리티
    // =============================

    /**
     * 그리드 이벤트 핸들러 제거 함수
     * 
     * @param {string} gridId - 그리드 ID
     * @param {Array<string>} [eventNames] - 제거할 이벤트명 배열 (미지정 시 모든 일반 이벤트 제거)
     * @returns {boolean} 제거 성공 여부
     */
    function removeEventHandlers(gridId, eventNames) {
        try {
            const grid = _gridInstances[gridId];
            if (!grid) {
                throw new Error(`ID가 '${gridId}'인 그리드를 찾을 수 없습니다.`);
            }

            // 제거할 이벤트 목록
            const events = eventNames || ['click', 'dblclick', 'check', 'uncheck', 'drop', 'scrollEnd', 'editingStart', 'editingFinish'];

            // 각 이벤트 핸들러 제거
            events.forEach(eventName => {
                grid.off(eventName);
            });

            return true;
        } catch (error) {
            console.error('이벤트 핸들러 제거 중 오류:', error);
            return false;
        }
    }

    /**
     * 행 클릭 이벤트 처리 함수 - 편의 기능
     * 
     * @param {string} gridId - 그리드 ID
     * @param {Function} callback - 행 클릭 시 호출될 콜백 함수
     * @returns {boolean} 이벤트 등록 성공 여부
     */
    function onRowClick(gridId, callback) {
        try {
            const grid = _gridInstances[gridId];
            if (!grid) {
                throw new Error(`ID가 '${gridId}'인 그리드를 찾을 수 없습니다.`);
            }

            grid.on('click', ev => {
                // ev.rowKey가 유효한지 확인
                if (ev.rowKey === undefined || ev.rowKey === null) {
                    console.warn('유효하지 않은 rowKey:', ev.rowKey);
                    if (typeof callback === 'function') {
                        callback(null, ev.rowKey, ev.columnName);
                    }
                    return;
                }
                
                const rowData = grid.getRow(ev.rowKey);
                if (typeof callback === 'function') {
                    callback(rowData, ev.rowKey, ev.columnName);
                }
            });

            return true;
        } catch (error) {
            console.error('행 클릭 이벤트 등록 중 오류:', error);
            return false;
        }
    }

    /**
     * 더블 클릭 이벤트 처리 함수 - 편의 기능
     * 
     * @param {string} gridId - 그리드 ID
     * @param {Function} callback - 더블 클릭 시 호출될 콜백 함수
     * @returns {boolean} 이벤트 등록 성공 여부
     */
    function onDblClick(gridId, callback) {
        try {
            const grid = _gridInstances[gridId];
            if (!grid) {
                throw new Error(`ID가 '${gridId}'인 그리드를 찾을 수 없습니다.`);
            }

            grid.on('dblclick', ev => {
                const rowData = grid.getRow(ev.rowKey);
                if (typeof callback === 'function') {
                    callback(rowData, ev.rowKey, ev.columnName);
                }
            });

			return true;
			        } catch (error) {
			            console.error('더블 클릭 이벤트 등록 중 오류:', error);
			            return false;
			        }
			    }

			    // 공개 API - 모듈의 공개 인터페이스
			    return {
			        // 그리드 인스턴스 관리
			        registerGrid,      // 그리드 인스턴스 생성 및 등록
			        getGrid,           // 그리드 인스턴스 조회
			        unregisterGrid,    // 그리드 인스턴스 제거

			        // 프로젝트 특화 데이터 관리 함수
			        addNewRow,         // ROW_TYPE 자동 설정하여 행 추가
			        updateRowData,     // ROW_TYPE 자동 업데이트하며 행 데이터 수정
					deleteSelectedRows, // 선택 행 삭제 (확인 대화상자 포함)
			        extractChangedData, // 변경된 데이터만 추출

			        // 이벤트 핸들러 관리
			        onRowClick,        // 행 클릭 이벤트 등록 (편의 함수)
			        onDblClick,        // 더블 클릭 이벤트 등록 (편의 함수)
			        removeEventHandlers, // 이벤트 핸들러 일괄 제거

			        // 프로젝트 특화 유틸리티
			        setupKeyColumnControl, // 키 컬럼 제어 설정
			        createYesNoEditor,  // Y/N 드롭다운 에디터 생성
					// ====================================================== 추가
			        // 상태 관리 유틸리티 (간소화 버전)
			        createStatusFormatter, // 상태 포맷터 생성
			        changeStatus,          // 상태값 변경
			        setupStatusChangeEvent // 상태 변경 이벤트 설정
					// ====================================================== 추가
			    };
			})();