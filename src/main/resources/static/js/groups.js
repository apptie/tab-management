const API_BASE_URL = '/api/groups';
const KAKAO_LOGIN_URL = '/oauth2/authorization/kakao';
const REFRESH_TOKEN_URL = '/refresh-token';

// DOM Elements
const groupList = document.getElementById('groupList');
const allGroupList = document.getElementById('allGroupList');
const addGroupBtn = document.getElementById('addGroupBtn');
const kakaoLoginBtn = document.getElementById('kakaoLoginBtn');
const addGroupModal = document.getElementById('addGroupModal');
const editGroupModal = document.getElementById('editGroupModal');
const addGroupForm = document.getElementById('addGroupForm');
const editGroupForm = document.getElementById('editGroupForm');
const groupNameInput = document.getElementById('groupName');
const editGroupIdInput = document.getElementById('editGroupId');
const editGroupNameInput = document.getElementById('editGroupName');
const tabNavigation = document.getElementById('tabNavigation');
const tabButtons = document.querySelectorAll('.tab-button');
const tabContents = document.querySelectorAll('.tab-content');
const closeButtons = document.querySelectorAll('.close');

// ============ 쿠키 유틸리티 ============
function getCookie(name) {
    const value = `; ${document.cookie}`;
    const parts = value.split(`; ${name}=`);
    if (parts.length === 2) {
        const cookieValue = parts.pop().split(';').shift();
        return decodeURIComponent(cookieValue);
    }
    return null;
}

function hasAccessToken() {
    const token = getCookie('accessToken');
    console.log('AccessToken 존재?:', !!token);
    return !!token;
}

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

function getCsrfHeaders() {
    const csrfToken = getCsrfToken();
    if (csrfToken) {
        return {
            'X-CSRF-Token': csrfToken
        };
    }
    return {};
}

// ============ 토큰 리프레시 ============
async function refreshAccessToken() {
    console.log('토큰 갱신 시도...');
    try {
        const response = await fetch(REFRESH_TOKEN_URL, {
            method: 'POST',
            credentials: 'include',
            headers: {
                ...getCsrfHeaders()
            }
        });

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

// ============ API 요청 헬퍼 (토큰 리프레시 포함) ============
async function apiCall(url, options = {}, isRetry = false) {
    const accessToken = getCookie('accessToken');

    const headers = {
        'Content-Type': 'application/json',
        ...options.headers,
        ...getCsrfHeaders()
    };

    if (accessToken) {
        headers['Authorization'] = `Bearer ${accessToken}`;
        console.log('Authorization 헤더 추가됨');
    } else {
        console.log('AccessToken 없음 - Authorization 헤더 미추가');
    }

    const finalOptions = {
        ...options,
        headers,
        credentials: 'include'
    };

    console.log('API 요청:', url, finalOptions);

    const response = await fetch(url, finalOptions);

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
                    // 재귀 호출로 원래 요청 재시도 (isRetry=true로 무한 루프 방지)
                    return await apiCall(url, options, true);
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

function deleteCookie(name) {
    console.log(`${name} 쿠키 삭제 시도`);
    document.cookie = `${name}=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;`;
    document.cookie = `${name}=; max-age=0; path=/;`;
    console.log(`${name} 쿠키 삭제 완료`);
}

function debugCookies() {
    console.log('=== 현재 쿠키 상태 ===');
    const accessToken = getCookie('accessToken');
    const refreshToken = getCookie('refreshToken');
    console.log('AccessToken:', accessToken ? accessToken.substring(0, 20) + '...' : 'None');
    console.log('RefreshToken:', refreshToken ? refreshToken.substring(0, 20) + '...' : 'None');
}

// ============ 로그아웃 처리 ============
async function handleLogout() {
    console.log('=== 로그아웃 시작 ===');
    debugCookies();

    try {
        console.log('서버 로그아웃 요청...');
        const response = await apiCall('/logout', {
            method: 'POST'
        });

        console.log('로그아웃 응답:', response.status);

    } catch (error) {
        console.error('로그아웃 요청 실패:', error);
    } finally {
        console.log('클라이언트 쿠키 삭제 시작...');
        deleteCookie('accessToken');
        deleteCookie('refreshToken');

        setTimeout(() => {
            console.log('로그아웃 후 쿠키 상태:');
            debugCookies();
            updateLoginButtonUI(false);
            location.href = '/groups.html';
        }, 500);
    }
}

function handleKakaoLogin() {
    console.log('=== 카카오 로그인 시작 ===');
    const width = 500;
    const height = 600;
    const left = (window.innerWidth - width) / 2;
    const top = (window.innerHeight - height) / 2;

    const popup = window.open(
        KAKAO_LOGIN_URL,
        'kakao_login',
        `width=${width},height=${height},left=${left},top=${top},resizable=no`
    );

    if (!popup) {
        console.log('팝업 차단됨 - 새 탭으로 열기');
        window.location.href = KAKAO_LOGIN_URL;
        return;
    }

    console.log('팝업 정상 열림');

    let checkCount = 0;
    const checkInterval = setInterval(() => {
        try {
            if (popup.closed) {
                clearInterval(checkInterval);
                console.log('팝업 종료 - 쿠키 확인 중...');

                setTimeout(() => {
                    debugCookies();
                    if (hasAccessToken()) {
                        console.log('AccessToken 확인됨');
                        location.href = '/groups.html';
                    } else {
                        console.log('AccessToken 미확인');
                    }
                }, 1000);
            }
        } catch (e) {
            console.error('팝업 체크 실패:', e);
        }

        checkCount++;
        if (checkCount > 60) {
            clearInterval(checkInterval);
            if (!popup.closed) {
                popup.close();
            }
        }
    }, 500);

    popup.onbeforeunload = function() {
        popup.close();
        console.log('팝업 자동 종료');
    };
}

// ============ 로그인 UI 업데이트 ============
function updateLoginButtonUI(isLoggedIn) {
    console.log('로그인 UI 업데이트:', isLoggedIn ? '로그인' : '미로그인');

    if (isLoggedIn) {
        kakaoLoginBtn.innerHTML = `
            <span class="kakao-icon">
                <svg version="1.1" xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 16 16" fill="currentColor">
                    <path d="M8 1.5c-3.722 0-6.75 2.486-6.75 5.552 0 1.85 1.177 3.482 2.97 4.501-0.049 0.459-0.241 0.855-0.482 1.368-0.034 0.088-0.069 0.18-0.105 0.271-0.075 0.193-0.15 0.385-0.215 0.577-0.022 0.062-0.038 0.117-0.055 0.172-0.078 0.239-0.147 0.476-0.197 0.708 2.215-0.065 3.921-1.267 4.777-1.988 0.47 0.072 0.958 0.11 1.456 0.11 3.722 0 6.75-2.486 6.75-5.552s-3.027-5.552-6.75-5.552z"/>
                </svg>
            </span>
            로그아웃
        `;
        kakaoLoginBtn.dataset.action = 'logout';
        addGroupBtn.style.display = 'block';
        tabNavigation.style.display = 'flex';
    } else {
        kakaoLoginBtn.innerHTML = `
            <span class="kakao-icon">
                <svg version="1.1" xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 16 16" fill="currentColor">
                    <path d="M8 1.5c-3.722 0-6.75 2.486-6.75 5.552 0 1.85 1.177 3.482 2.97 4.501-0.049 0.459-0.241 0.855-0.482 1.368-0.034 0.088-0.069 0.18-0.105 0.271-0.075 0.193-0.15 0.385-0.215 0.577-0.022 0.062-0.038 0.117-0.055 0.172-0.078 0.239-0.147 0.476-0.197 0.708 2.215-0.065 3.921-1.267 4.777-1.988 0.47 0.072 0.958 0.11 1.456 0.11 3.722 0 6.75-2.486 6.75-5.552s-3.027-5.552-6.75-5.552z"/>
                </svg>
            </span>
            카카오로 로그인
        `;
        kakaoLoginBtn.dataset.action = 'login';
        addGroupBtn.style.display = 'none';
        tabNavigation.style.display = 'none';
    }
}

// ============ 탭 전환 ============
function switchTab(tabName) {
    tabButtons.forEach(btn => btn.classList.remove('active'));
    tabContents.forEach(content => content.classList.remove('active'));

    document.querySelector(`[data-tab="${tabName}"]`).classList.add('active');
    document.getElementById(tabName).classList.add('active');

    if (tabName === 'all-groups') {
        loadAllGroups();
    } else if (tabName === 'my-groups') {
        loadMyGroups();
    }
}

// ============ 날짜 포맷팅 ============
function formatDateTime(dateTimeString) {
    const date = new Date(dateTimeString);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');

    return `${year}-${month}-${day} ${hours}:${minutes}`;
}

// ============ 내 그룹 조회 ============
async function loadMyGroups() {
    console.log('내 그룹 조회 시작');
    try {
        const response = await apiCall(`${API_BASE_URL}/my`, {
            method: 'GET'
        });

        console.log('내 그룹 조회 응답:', response.status);

        if (response.status === 401) {
            groupList.innerHTML = '<p style="text-align: center; color: #999;">로그인이 필요합니다.</p>';
            console.log('401 - 로그인 필요');
            return;
        }

        if (response.ok) {
            const data = await response.json();
            console.log('내 그룹 데이터:', data);
            displayMyGroups(data.groups);
        } else {
            throw new Error('내 그룹 조회 실패');
        }
    } catch (error) {
        console.error('내 그룹 조회 실패:', error);
        groupList.innerHTML = '<p style="text-align: center; color: #999;">그룹을 불러오는데 실패했습니다.</p>';
    }
}

// ============ 모든 그룹 조회 ============
async function loadAllGroups() {
    console.log('전체 그룹 조회 시작');
    try {
        const response = await apiCall(API_BASE_URL, {
            method: 'GET'
        });

        console.log('전체 그룹 조회 응답:', response.status);

        if (response.ok) {
            const data = await response.json();
            console.log('전체 그룹 데이터:', data);
            displayAllGroups(data.groups);
        } else {
            throw new Error('전체 그룹 조회 실패');
        }
    } catch (error) {
        console.error('전체 그룹 조회 실패:', error);
        allGroupList.innerHTML = '<p style="text-align: center; color: #999;">그룹을 불러오는데 실패했습니다.</p>';
    }
}

// ============ 내 그룹 표시 ============
function displayMyGroups(groups) {
    if (groups.length === 0) {
        groupList.innerHTML = '<p style="text-align: center; color: #999;">등록된 그룹이 없습니다.</p>';
        return;
    }

    groupList.innerHTML = groups.map(group => `
        <div class="group-card" data-id="${group.id}">
            <div class="group-card-header">
                <h2 class="group-card-title">${escapeHtml(group.name)}</h2>
                <div class="group-card-actions">
                    <button class="btn-secondary" onclick="openEditModal(${group.id}, '${escapeHtml(group.name)}')">수정</button>
                    <button class="btn-danger" onclick="deleteGroup(${group.id})">삭제</button>
                </div>
            </div>
            <div class="group-card-info">
                <div class="group-card-date">생성일: ${formatDateTime(group.createdAt)}</div>
                <div class="group-card-date">수정일: ${formatDateTime(group.updatedAt)}</div>
            </div>
        </div>
    `).join('');

    document.querySelectorAll('#my-groups .group-card').forEach(card => {
        card.addEventListener('click', (e) => {
            if (e.target.tagName === 'BUTTON') return;
            const groupId = card.dataset.id;
            window.location.href = `/tabs.html?groupId=${groupId}`;
        });
    });
}

// ============ 전체 그룹 표시 ============
function displayAllGroups(groups) {
    if (groups.length === 0) {
        allGroupList.innerHTML = '<p style="text-align: center; color: #999;">등록된 그룹이 없습니다.</p>';
        return;
    }

    allGroupList.innerHTML = groups.map(group => `
        <div class="group-card" data-id="${group.id}">
            <div class="group-card-header">
                <h2 class="group-card-title">${escapeHtml(group.name)}</h2>
                <div class="group-card-actions"></div>
            </div>
            <div class="group-card-info">
                <div class="group-card-date">생성일: ${formatDateTime(group.createdAt)}</div>
                <div class="group-card-date">수정일: ${formatDateTime(group.updatedAt)}</div>
            </div>
        </div>
    `).join('');

    document.querySelectorAll('#all-groups .group-card').forEach(card => {
        card.addEventListener('click', (e) => {
            if (e.target.tagName === 'BUTTON') return;
            const groupId = card.dataset.id;
            window.location.href = `/tabs.html?groupId=${groupId}`;
        });
    });
}

// ============ HTML 이스케이프 처리 ============
function escapeHtml(text) {
    const map = {
        '&': '&amp;',
        '<': '&lt;',
        '>': '&gt;',
        '"': '&quot;',
        "'": '&#039;'
    };
    return text.replace(/[&<>"']/g, m => map[m]);
}

// ============ 그룹 추가 ============
addGroupForm.addEventListener('submit', async (e) => {
    e.preventDefault();

    const name = groupNameInput.value.trim();
    if (!name) {
        alert('그룹 이름을 입력해주세요.');
        return;
    }

    try {
        const response = await apiCall(API_BASE_URL, {
            method: 'POST',
            body: JSON.stringify({ name })
        });

        if (response.ok) {
            const data = await response.json();
            console.log('그룹 생성 성공:', data);

            closeModal(addGroupModal);
            addGroupForm.reset();
            loadMyGroups();
        } else if (response.status === 401) {
            alert('로그인이 필요합니다.');
        } else {
            throw new Error('그룹 추가 실패');
        }
    } catch (error) {
        console.error('그룹 추가 실패:', error);
        alert('그룹 추가에 실패했습니다.');
    }
});

// ============ 그룹 수정 ============
function openEditModal(id, name) {
    editGroupIdInput.value = id;
    editGroupNameInput.value = name;
    openModal(editGroupModal);
}

editGroupForm.addEventListener('submit', async (e) => {
    e.preventDefault();

    const id = editGroupIdInput.value;
    const name = editGroupNameInput.value.trim();

    if (!name) {
        alert('그룹 이름을 입력해주세요.');
        return;
    }

    try {
        const response = await apiCall(`${API_BASE_URL}/${id}`, {
            method: 'PUT',
            body: JSON.stringify({ name })
        });

        if (response.ok) {
            closeModal(editGroupModal);
            loadMyGroups();
        } else if (response.status === 401) {
            alert('로그인이 필요합니다.');
        } else {
            throw new Error('그룹 수정 실패');
        }
    } catch (error) {
        console.error('그룹 수정 실패:', error);
        alert('그룹 수정에 실패했습니다.');
    }
});

// ============ 그룹 삭제 ============
async function deleteGroup(id) {
    if (!confirm('정말 이 그룹을 삭제하시겠습니까?\n그룹에 포함된 모든 탭도 함께 삭제됩니다.')) {
        return;
    }

    try {
        const response = await apiCall(`${API_BASE_URL}/${id}`, {
            method: 'DELETE'
        });

        if (response.ok) {
            loadMyGroups();
        } else if (response.status === 401) {
            alert('로그인이 필요합니다.');
        } else {
            throw new Error('그룹 삭제 실패');
        }
    } catch (error) {
        console.error('그룹 삭제 실패:', error);
        alert('그룹 삭제에 실패했습니다.');
    }
}

// ============ 모달 제어 ============
function openModal(modal) {
    modal.classList.add('show');
}

function closeModal(modal) {
    modal.classList.remove('show');
}

// ============ 카카오 로그인 버튼 클릭 핸들러 ============
function handleKakaoLoginButtonClick() {
    const action = kakaoLoginBtn.dataset.action;
    console.log('로그인 버튼 클릭 - Action:', action);

    if (action === 'logout') {
        handleLogout();
    } else {
        handleKakaoLogin();
    }
}

// ============ 탭 버튼 클릭 핸들러 ============
function handleTabButtonClick(e) {
    const tabName = e.target.dataset.tab;
    console.log('탭 버튼 클릭:', tabName);
    switchTab(tabName);
}

// ============ 이벤트 리스너 등록 ============
function setupEventListeners() {
    console.log('이벤트 리스너 등록 중...');

    kakaoLoginBtn.removeEventListener('click', handleKakaoLoginButtonClick);
    kakaoLoginBtn.addEventListener('click', handleKakaoLoginButtonClick);

    addGroupBtn.removeEventListener('click', () => openModal(addGroupModal));
    addGroupBtn.addEventListener('click', () => openModal(addGroupModal));

    tabButtons.forEach(btn => {
        btn.removeEventListener('click', handleTabButtonClick);
        btn.addEventListener('click', handleTabButtonClick);
    });

    closeButtons.forEach(btn => {
        btn.removeEventListener('click', function() {
            closeModal(this.closest('.modal'));
        });
        btn.addEventListener('click', function() {
            closeModal(this.closest('.modal'));
        });
    });

    window.removeEventListener('click', handleModalBackgroundClick);
    window.addEventListener('click', handleModalBackgroundClick);
}

function handleModalBackgroundClick(e) {
    if (e.target.classList.contains('modal')) {
        closeModal(e.target);
    }
}

// ============ 페이지 로드시 ============
document.addEventListener('DOMContentLoaded', async () => {
    console.log('=== 페이지 로드 ===');
    debugCookies();

    setupEventListeners();

    const isLoggedIn = hasAccessToken();
    console.log('초기 로그인 상태:', isLoggedIn);

    updateLoginButtonUI(isLoggedIn);

    if (isLoggedIn) {
        console.log('로그인 상태: 내 그룹 로드');
        loadMyGroups();
    } else {
        console.log('미로그인 상태: 전체 그룹 로드');
        loadAllGroups();
    }
});
