// js/tabs.js

let groupId;
let bulkTabCounter = 0;

// DOM 요소
const groupTitle = document.getElementById('groupTitle');
const tabTree = document.getElementById('tabTree');
const addRootTabBtn = document.getElementById('addRootTabBtn');
const addBulkRootTabBtn = document.getElementById('addBulkRootTabBtn');
const contentPanel = document.getElementById('contentPanel');

// 기존 모달
const addTabModal = document.getElementById('addTabModal');
const editTabModal = document.getElementById('editTabModal');
const contentModal = document.getElementById('contentModal');
const addTabForm = document.getElementById('addTabForm');
const editTabForm = document.getElementById('editTabForm');
const contentForm = document.getElementById('contentForm');

// 벌크 탭 모달
const bulkAddTabModal = document.getElementById('bulkAddTabModal');
const bulkAddTabForm = document.getElementById('bulkAddTabForm');
const bulkTabsContainer = document.getElementById('bulkTabsContainer');
const bulkParentIdInput = document.getElementById('bulkParentId');
const bulkAddRootBtn = document.getElementById('bulkAddRootBtn');
const bulkCancelBtn = document.getElementById('bulkCancelBtn');

// ==================== 유틸리티 함수 ====================
/**
 * 쿠키에서 특정 이름의 값을 가져옵니다.
 */
function getCookie(name) {
    const value = `; ${document.cookie}`;
    const parts = value.split(`; ${name}=`);
    if (parts.length === 2) return parts.pop().split(';').shift();
    return null;
}

/**
 * CSRF 토큰을 가져옵니다.
 */
function getCsrfToken() {
    const name = '_csrf=';
    const decodedCookie = decodeURIComponent(document.cookie);
    const cookieArray = decodedCookie.split(';');

    for (let cookie of cookieArray) {
        cookie = cookie.trim();
        if (cookie.indexOf(name) === 0) {
            return cookie.substring(name.length, cookie.length);
        }
    }
    return '';
}

/**
 * CSRF 헤더를 반환합니다.
 */
function getCsrfHeaders() {
    const csrfToken = getCsrfToken();
    if (csrfToken) {
        return {
            'X-CSRF-Token': csrfToken
        };
    }
    return {};
}

/**
 * Authorization 헤더가 포함된 요청 옵션을 생성합니다.
 */
function getRequestOptions(method = 'GET', body = null) {
    const accessToken = getCookie('accessToken');

    const options = {
        method: method,
        headers: {
            'Content-Type': 'application/json',
            ...getCsrfHeaders()
        },
        credentials: 'include'
    };

    if (accessToken) {
        options.headers['Authorization'] = `Bearer ${accessToken}`;
    }

    if (body) {
        options.body = JSON.stringify(body);
    }

    return options;
}

/**
 * 토큰을 갱신합니다.
 */
async function refreshAccessToken() {
    console.log('토큰 갱신 시도...');
    try {
        const options = getRequestOptions('POST');
        const response = await fetch('/refresh-token', options);

        if (response.ok) {
            console.log('토큰 갱신 성공');
            return true;
        } else {
            console.log('토큰 갱신 실패:', response.status);
            return false;
        }
    } catch (error) {
        console.error('토큰 갱신 오류:', error);
        return false;
    }
}

/**
 * API 요청을 수행합니다 (토큰 리프레시 자동 처리).
 */
async function apiRequest(url, method = 'GET', body = null, isRetry = false) {
    const options = getRequestOptions(method, body);
    const response = await fetch(url, options);

    // 401 에러 처리
    if (response.status === 401 && !isRetry) {
        try {
            const errorData = await response.json();
            console.log('401 에러 상세:', errorData);

            // EXPIRED_TOKEN인 경우 토큰 갱신 시도
            if (errorData.code === 'EXPIRED_TOKEN') {
                console.log('만료된 토큰 감지 - 갱신 시도');
                const refreshed = await refreshAccessToken();

                if (refreshed) {
                    console.log('토큰 갱신 성공 - 요청 재시도');
                    return await apiRequest(url, method, body, true);
                } else {
                    console.log('토큰 갱신 실패 - 로그인 필요');
                    alert('로그인이 만료되었습니다. 다시 로그인해주세요.');
                    handleLogout();
                }
            }
        } catch (e) {
            console.error('401 에러 파싱 실패:', e);
        }
    }

    return response;
}

/**
 * 로그아웃을 처리합니다.
 */
async function handleLogout() {
    console.log('=== 로그아웃 시작 ===');

    try {
        await apiRequest('/logout', 'POST');
    } catch (error) {
        console.error('로그아웃 요청 실패:', error);
    } finally {
        // 쿠키 삭제
        document.cookie = 'accessToken=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;';
        document.cookie = 'refreshToken=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;';

        // groups.html로 이동
        setTimeout(() => {
            window.location.href = '/groups.html';
        }, 500);
    }
}

function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

// ==================== 초기화 ====================
document.addEventListener('DOMContentLoaded', () => {
    groupId = new URLSearchParams(window.location.search).get('groupId');
    if (!groupId) {
        window.location.href = 'groups.html';
        return;
    }

    loadGroupInfo();
    loadTabTree();
    setupEventListeners();
});

// ==================== 그룹 정보 로드 ====================
async function loadGroupInfo() {
    try {
        const response = await apiRequest(`/api/groups/${groupId}`, 'GET');
        if (response.ok) {
            const group = await response.json();
            groupTitle.textContent = group.name;
        }
    } catch (error) {
        console.error('그룹 정보 로드 오류:', error);
    }
}

// ==================== 이벤트 리스너 설정 ====================
function setupEventListeners() {
    // 기존 탭 추가 버튼
    addRootTabBtn.addEventListener('click', () => {
        document.getElementById('parentId').value = '';
        openAddTabModal();
    });

    // 벌크 탭 추가 버튼
    addBulkRootTabBtn.addEventListener('click', () => {
        bulkParentIdInput.value = '';
        openBulkTabModal('루트 탭 일괄 추가');
    });

    // 기존 모달 닫기
    document.querySelectorAll('#addTabModal .close, #editTabModal .close, #contentModal .close').forEach(btn => {
        btn.addEventListener('click', (e) => {
            e.target.closest('.modal').classList.remove('show');
        });
    });

    // 벌크 모달 닫기
    document.querySelectorAll('#bulkAddTabModal .close').forEach(btn => {
        btn.addEventListener('click', (e) => {
            e.target.closest('.modal').classList.remove('show');
        });
    });

    // 기존 탭 추가 폼
    addTabForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        await createTab();
    });

    // 기존 탭 수정 폼
    editTabForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        await updateTab();
    });

    // 콘텐츠 폼
    contentForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        await saveContent();
    });

    // 벌크 탭 폼
    bulkAddTabForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        await createBulkTabs();
    });

    // 벌크 최상위 탭 추가
    bulkAddRootBtn.addEventListener('click', (e) => {
        e.preventDefault();
        addBulkTabItem(null, 0);
    });

    // 벌크 취소
    bulkCancelBtn.addEventListener('click', (e) => {
        e.preventDefault();
        bulkAddTabModal.classList.remove('show');
    });

    // 모달 배경 클릭으로 닫기
    [addTabModal, editTabModal, contentModal, bulkAddTabModal].forEach(modal => {
        modal.addEventListener('click', (e) => {
            if (e.target === modal) {
                modal.classList.remove('show');
            }
        });
    });
}

// ==================== 탭 트리 로드 ====================
async function loadTabTree() {
    try {
        // GET /api/tabs/groups/{groupId}/tree
        const response = await apiRequest(`/api/tabs/groups/${groupId}/tree`, 'GET');
        if (response.ok) {
            const data = await response.json();
            // TabTreeResponse에서 탭 데이터 추출
            const tabs = data.tabs || [];
            tabTree.innerHTML = '';
            tabs.forEach(tab => {
                tabTree.appendChild(createTabElement(tab));
            });
        } else if (response.status === 404) {
            console.warn('탭 조회 실패. 빈 목록으로 초기화합니다.');
            tabTree.innerHTML = '';
        }
    } catch (error) {
        console.error('탭 트리 로드 오류:', error);
    }
}

// ==================== 탭 엘리먼트 생성 ====================
function createTabElement(tab) {
    const li = document.createElement('li');
    li.className = 'tab-item';
    li.setAttribute('data-tab-id', tab.id);
    li.draggable = true;

    // 탭 콘텐츠
    const content = document.createElement('div');
    content.className = 'tab-content';

    // 탭 정보
    const info = document.createElement('div');
    info.className = 'tab-info';
    info.innerHTML = `
        <div class="tab-title">${escapeHtml(tab.title)}</div>
        <div class="tab-url">${escapeHtml(tab.url)}</div>
    `;
    info.addEventListener('click', () => loadTabContents(tab.id));

    // 탭 액션
    const actions = document.createElement('div');
    actions.className = 'tab-actions';

    const lockBtn = document.createElement('button');
    lockBtn.className = 'btn-lock unlocked';
    lockBtn.textContent = '🔒';
    lockBtn.title = '잠금';
    lockBtn.addEventListener('click', (e) => {
        e.preventDefault();
        e.stopPropagation();
        lockBtn.classList.toggle('locked');
        lockBtn.classList.toggle('unlocked');
    });

    const editBtn = document.createElement('button');
    editBtn.className = 'btn-secondary';
    editBtn.textContent = '수정';
    editBtn.addEventListener('click', (e) => {
        e.preventDefault();
        e.stopPropagation();
        openEditTabModal(tab);
    });

    const deleteBtn = document.createElement('button');
    deleteBtn.className = 'btn-danger';
    deleteBtn.textContent = '삭제';
    deleteBtn.addEventListener('click', (e) => {
        e.preventDefault();
        e.stopPropagation();
        if (confirm('탭을 삭제하시겠습니까?')) {
            deleteTab(tab.id);
        }
    });

    const addChildBtn = document.createElement('button');
    addChildBtn.className = 'btn-secondary';
    addChildBtn.textContent = '추가';
    addChildBtn.addEventListener('click', (e) => {
        e.preventDefault();
        e.stopPropagation();
        document.getElementById('parentId').value = tab.id;
        openAddTabModal();
    });

    const bulkAddChildBtn = document.createElement('button');
    bulkAddChildBtn.className = 'btn-secondary';
    bulkAddChildBtn.textContent = '일괄 추가';
    bulkAddChildBtn.addEventListener('click', (e) => {
        e.preventDefault();
        e.stopPropagation();
        openBulkTabModalForParent(tab.id, tab.title);
    });

    actions.appendChild(lockBtn);
    actions.appendChild(editBtn);
    actions.appendChild(deleteBtn);
    actions.appendChild(addChildBtn);
    actions.appendChild(bulkAddChildBtn);

    content.appendChild(info);
    content.appendChild(actions);

    const tabContent = document.createElement('div');
    tabContent.className = 'tab-content-wrapper';
    tabContent.appendChild(content);

    // 자식 탭들
    if (tab.children && tab.children.length > 0) {
        const childrenList = document.createElement('ul');
        childrenList.className = 'tab-children';
        tab.children.forEach(child => {
            childrenList.appendChild(createTabElement(child));
        });
        tabContent.appendChild(childrenList);
    }

    li.appendChild(tabContent);

    // 드래그 이벤트
    setupDragEvents(li, tab);

    return li;
}

// ==================== 기존 탭 추가 ====================
function openAddTabModal() {
    addTabModal.classList.add('show');
    document.getElementById('tabTitle').focus();
}

async function createTab() {
    const parentId = document.getElementById('parentId').value;
    const title = document.getElementById('tabTitle').value.trim();
    const url = document.getElementById('tabUrl').value.trim();

    if (!title || !url) {
        alert('제목과 URL을 입력해주세요');
        return;
    }

    try {
        let endpoint;
        let requestBody;

        if (parentId) {
            // 자식 탭 추가: POST /api/tabs/{parentId}/children
            endpoint = `/api/tabs/${parentId}/children`;
            requestBody = { title, url };
        } else {
            // 루트 탭 추가: POST /api/tabs/groups/{groupId}/root
            endpoint = `/api/tabs/groups/${groupId}/root`;
            requestBody = { title, url };
        }

        const response = await apiRequest(endpoint, 'POST', requestBody);

        if (response.ok) {
            addTabModal.classList.remove('show');
            addTabForm.reset();
            loadTabTree();
        } else {
            const errorData = await response.json().catch(() => ({}));
            alert('탭 추가 실패: ' + (errorData.message || ''));
        }
    } catch (error) {
        console.error('탭 추가 오류:', error);
        alert('오류: ' + error.message);
    }
}

// ==================== 기존 탭 수정 ====================
function openEditTabModal(tab) {
    document.getElementById('editTabId').value = tab.id;
    document.getElementById('editTabTitle').value = tab.title;
    document.getElementById('editTabUrl').value = tab.url;
    editTabModal.classList.add('show');
    document.getElementById('editTabTitle').focus();
}

async function updateTab() {
    const tabId = document.getElementById('editTabId').value;
    const title = document.getElementById('editTabTitle').value.trim();
    const url = document.getElementById('editTabUrl').value.trim();

    try {
        // PUT /api/tabs/{tabId}
        const response = await apiRequest(`/api/tabs/${tabId}`, 'PUT', { title, url });

        if (response.ok) {
            editTabModal.classList.remove('show');
            editTabForm.reset();
            loadTabTree();
        } else {
            const errorData = await response.json().catch(() => ({}));
            alert('탭 수정 실패: ' + (errorData.message || ''));
        }
    } catch (error) {
        console.error('탭 수정 오류:', error);
        alert('오류: ' + error.message);
    }
}

// ==================== 기존 탭 삭제 ====================
async function deleteTab(tabId) {
    try {
        // DELETE /api/tabs/{tabId}
        const response = await apiRequest(`/api/tabs/${tabId}`, 'DELETE');

        if (response.ok) {
            loadTabTree();
        } else {
            const errorData = await response.json().catch(() => ({}));
            alert('탭 삭제 실패: ' + (errorData.message || ''));
        }
    } catch (error) {
        console.error('탭 삭제 오류:', error);
        alert('오류: ' + error.message);
    }
}

// ==================== 벌크 탭 추가 모달 ====================
function openBulkTabModal(title) {
    document.querySelector('.bulk-modal-header h2').textContent = title;
    bulkTabsContainer.innerHTML = '';
    bulkTabCounter = 0;
    addBulkTabItem(null, 0);
    bulkAddTabModal.classList.add('show');
}

function openBulkTabModalForParent(parentTabId, parentTabTitle) {
    bulkParentIdInput.value = parentTabId;
    openBulkTabModal(`[${escapeHtml(parentTabTitle)}] 하위 탭 일괄 추가`);
}

// ==================== 벌크 탭 아이템 추가 ====================
function addBulkTabItem(parentElement, depth) {
    const tabId = bulkTabCounter++;

    const tabItem = document.createElement('div');
    tabItem.className = 'bulk-tab-item';
    tabItem.setAttribute('data-tab-id', tabId);
    tabItem.setAttribute('data-depth', depth);

    const inputsDiv = document.createElement('div');
    inputsDiv.className = 'bulk-tab-inputs';

    const titleInput = document.createElement('input');
    titleInput.type = 'text';
    titleInput.className = 'bulk-tab-title';
    titleInput.placeholder = '탭 제목';
    titleInput.required = true;

    const urlInput = document.createElement('input');
    urlInput.type = 'url';
    urlInput.className = 'bulk-tab-url';
    urlInput.placeholder = 'URL';
    urlInput.required = true;

    const actionsDiv = document.createElement('div');
    actionsDiv.className = 'bulk-tab-actions';

    const addChildBtn = document.createElement('button');
    addChildBtn.type = 'button';
    addChildBtn.className = 'btn-add-child';
    addChildBtn.textContent = '자식 추가';
    addChildBtn.title = '자식 탭 추가';
    addChildBtn.addEventListener('click', (e) => {
        e.preventDefault();
        const childrenContainer = tabItem.querySelector('.bulk-tab-children');
        addBulkTabItem(childrenContainer, depth + 1);
    });

    const addSiblingBtn = document.createElement('button');
    addSiblingBtn.type = 'button';
    addSiblingBtn.className = 'btn-add-sibling';
    addSiblingBtn.textContent = '형제 추가';
    addSiblingBtn.title = '형제 탭 추가';
    addSiblingBtn.addEventListener('click', (e) => {
        e.preventDefault();
        const container = parentElement || bulkTabsContainer;
        addBulkTabItem(container, depth);
    });

    const removeBtn = document.createElement('button');
    removeBtn.type = 'button';
    removeBtn.className = 'btn-remove-tab';
    removeBtn.textContent = '제거';
    removeBtn.title = '탭 제거';
    removeBtn.addEventListener('click', (e) => {
        e.preventDefault();
        const totalItems = document.querySelectorAll('[data-tab-id]').length;
        if (totalItems > 1) {
            tabItem.remove();
        } else {
            alert('최소 하나의 탭은 필요합니다');
        }
    });

    actionsDiv.appendChild(addChildBtn);
    actionsDiv.appendChild(addSiblingBtn);
    actionsDiv.appendChild(removeBtn);

    inputsDiv.appendChild(titleInput);
    inputsDiv.appendChild(urlInput);
    inputsDiv.appendChild(actionsDiv);

    const childrenDiv = document.createElement('div');
    childrenDiv.className = 'bulk-tab-children';

    tabItem.appendChild(inputsDiv);
    tabItem.appendChild(childrenDiv);

    if (parentElement) {
        parentElement.appendChild(tabItem);
    } else {
        bulkTabsContainer.appendChild(tabItem);
    }
}

// ==================== 벌크 탭 데이터 추출 ====================
function extractBulkTabData() {
    const rootItems = bulkTabsContainer.querySelectorAll(':scope > .bulk-tab-item');
    return Array.from(rootItems).map(item => extractTabItem(item));
}

function extractTabItem(element) {
    const titleInput = element.querySelector('.bulk-tab-title');
    const urlInput = element.querySelector('.bulk-tab-url');
    const childrenContainer = element.querySelector('.bulk-tab-children');

    const children = childrenContainer
        ? Array.from(childrenContainer.querySelectorAll(':scope > .bulk-tab-item')).map(child => extractTabItem(child))
        : [];

    return {
        title: titleInput.value.trim(),
        url: urlInput.value.trim(),
        children: children
    };
}

// ==================== 벌크 탭 데이터 검증 ====================
function validateBulkTabs(tabs) {
    for (const tab of tabs) {
        if (!tab.title || !tab.url) return false;
        if (tab.children && tab.children.length > 0 && !validateBulkTabs(tab.children)) return false;
    }
    return true;
}

// ==================== 벌크 탭 생성 ====================
async function createBulkTabs() {
    const parentId = bulkParentIdInput.value;
    const bulkTabs = extractBulkTabData();

    if (!validateBulkTabs(bulkTabs)) {
        alert('모든 탭에 제목과 URL을 입력해주세요');
        return;
    }

    try {
        // 벌크 API 사용
        const url = parentId
            ? `/api/groups/${groupId}/tabs/${parentId}/bulk-children`
            : `/api/groups/${groupId}/tabs/bulk`;

        console.log('요청 URL:', url);
        console.log('요청 데이터:', bulkTabs);

        const response = await apiRequest(url, 'POST', bulkTabs);

        if (!response.ok) {
            const errorData = await response.json().catch(() => ({}));
            throw new Error(errorData.message || '탭 저장에 실패했습니다');
        }

        alert('탭이 저장되었습니다');
        bulkAddTabModal.classList.remove('show');
        bulkAddTabForm.reset();
        loadTabTree();
    } catch (error) {
        console.error('벌크 탭 생성 오류:', error);
        alert('오류: ' + error.message);
    }
}

// ==================== 탭 콘텐츠 로드 ====================
async function loadTabContents(tabId) {
    try {
        const response = await apiRequest(`/api/tabs/${tabId}/contents`, 'GET');
        if (response.ok) {
            const contents = await response.json();
            renderTabContents(tabId, contents);
        }
    } catch (error) {
        console.error('탭 콘텐츠 로드 오류:', error);
    }
}

function renderTabContents(tabId, contents) {
    contentPanel.innerHTML = `
        <div class="content-list">
            <div class="content-items" id="contentItems"></div>
            <div class="content-footer">
                <button class="btn-primary" id="addContentBtn">+ 콘텐츠 추가</button>
            </div>
        </div>
    `;

    const contentItems = document.getElementById('contentItems');
    const addContentBtn = document.getElementById('addContentBtn');

    if (!contents || contents.length === 0) {
        contentItems.innerHTML = '<p style="text-align: center; color: #999;">콘텐츠가 없습니다.</p>';
    } else {
        contents.forEach(content => {
            const contentItem = document.createElement('div');
            contentItem.className = 'content-item';
            contentItem.innerHTML = `
                <div class="content-item-text">${escapeHtml(content.text)}</div>
                <button class="content-item-delete" title="삭제">×</button>
            `;

            contentItem.addEventListener('click', (e) => {
                if (!e.target.classList.contains('content-item-delete')) {
                    openContentModal(tabId, content);
                }
            });

            contentItem.querySelector('.content-item-delete').addEventListener('click', (e) => {
                e.preventDefault();
                e.stopPropagation();
                if (confirm('콘텐츠를 삭제하시겠습니까?')) {
                    deleteContent(content.id, tabId);
                }
            });

            contentItems.appendChild(contentItem);
        });
    }

    addContentBtn.addEventListener('click', () => {
        openContentModal(tabId, null);
    });

    contentPanel.classList.remove('hidden');
}

function openContentModal(tabId, content = null) {
    document.getElementById('contentTabId').value = tabId;
    if (content) {
        document.getElementById('contentId').value = content.id;
        document.getElementById('contentText').value = content.text;
        document.getElementById('contentModalTitle').textContent = '콘텐츠 수정';
    } else {
        document.getElementById('contentId').value = '';
        document.getElementById('contentText').value = '';
        document.getElementById('contentModalTitle').textContent = '콘텐츠 추가';
    }
    contentModal.classList.add('show');
    document.getElementById('contentText').focus();
}

async function saveContent() {
    const contentId = document.getElementById('contentId').value;
    const tabId = document.getElementById('contentTabId').value;
    const text = document.getElementById('contentText').value.trim();

    if (!text) {
        alert('콘텐츠를 입력해주세요');
        return;
    }

    try {
        const endpoint = contentId ? `/api/contents/${contentId}` : `/api/tabs/${tabId}/contents`;
        const method = contentId ? 'PUT' : 'POST';

        const response = await apiRequest(endpoint, method, { text });

        if (response.ok) {
            contentModal.classList.remove('show');
            contentForm.reset();
            loadTabContents(tabId);
        } else {
            const errorData = await response.json().catch(() => ({}));
            alert('콘텐츠 저장 실패: ' + (errorData.message || ''));
        }
    } catch (error) {
        console.error('콘텐츠 저장 오류:', error);
        alert('오류: ' + error.message);
    }
}

async function deleteContent(contentId, tabId) {
    try {
        const response = await apiRequest(`/api/contents/${contentId}`, 'DELETE');

        if (response.ok) {
            loadTabContents(tabId);
        } else {
            const errorData = await response.json().catch(() => ({}));
            alert('콘텐츠 삭제 실패: ' + (errorData.message || ''));
        }
    } catch (error) {
        console.error('콘텐츠 삭제 오류:', error);
        alert('오류: ' + error.message);
    }
}

// ==================== 드래그 & 드롭 ====================
function setupDragEvents(element, tab) {
    element.addEventListener('dragstart', (e) => {
        e.dataTransfer.effectAllowed = 'move';
        e.dataTransfer.setData('tabId', tab.id);
        element.classList.add('dragging');
    });

    element.addEventListener('dragend', () => {
        element.classList.remove('dragging');
        document.querySelectorAll('.tab-item').forEach(item => {
            item.classList.remove('drag-over', 'drop-before', 'drop-after', 'drop-child');
        });
    });

    element.addEventListener('dragover', (e) => {
        e.preventDefault();
        e.dataTransfer.dropEffect = 'move';
    });

    element.addEventListener('drop', async (e) => {
        e.preventDefault();
        const draggedTabId = parseInt(e.dataTransfer.getData('tabId'));

        if (draggedTabId !== tab.id) {
            if (confirm(`이 탭을 이동하시겠습니까?`)) {
                try {
                    // PUT /api/tabs/{tabId}/move
                    const response = await apiRequest(`/api/tabs/${draggedTabId}/move`, 'PUT', {
                        newParentId: tab.id,
                        withSubtree: true
                    });

                    if (response.ok) {
                        loadTabTree();
                    } else {
                        const errorData = await response.json().catch(() => ({}));
                        alert('탭 이동 실패: ' + (errorData.message || ''));
                    }
                } catch (error) {
                    console.error('탭 이동 오류:', error);
                    alert('오류: ' + error.message);
                }
            }
        }
    });
}
