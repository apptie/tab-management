const API_BASE_URL = '/api/groups';
const KAKAO_LOGIN_URL = '/oauth2/authorization/kakao';
const REFRESH_TOKEN_URL = '/refresh-token';

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

async function refreshAccessToken() {
    try {
        const response = await fetch(REFRESH_TOKEN_URL, {
            method: 'POST',
            credentials: 'include',
            headers: {
                ...getCsrfHeaders()
            }
        });

        if (response.ok) {
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

async function apiCall(url, options = {}, isRetry = false) {
    const accessToken = getCookie('accessToken');

    const headers = {
        'Content-Type': 'application/json',
        ...options.headers,
        ...getCsrfHeaders()
    };

    if (accessToken) {
        headers['Authorization'] = `Bearer ${accessToken}`;
    }

    const finalOptions = {
        ...options,
        headers,
        credentials: 'include'
    };

    const response = await fetch(url, finalOptions);

    if (response.status === 401 && !isRetry) {
        try {
            const errorData = await response.json();
            console.log('401 에러 상세:', errorData);

            if (errorData.code === 'EXPIRED_TOKEN') {
                const refreshed = await refreshAccessToken();

                if (refreshed) {
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
    document.cookie = `${name}=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;`;
    document.cookie = `${name}=; max-age=0; path=/;`;
}

async function handleLogout() {
      deleteCookie('accessToken');
      deleteCookie('refreshToken');

      setTimeout(() => {
          updateLoginButtonUI(false);
          location.href = '/groups.html';
      }, 500);
}

function handleKakaoLogin() {
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
        window.location.href = KAKAO_LOGIN_URL;
        return;
    }

    let checkCount = 0;
    const checkInterval = setInterval(() => {
        try {
            if (popup.closed) {
                clearInterval(checkInterval);

                setTimeout(() => {
                    if (hasAccessToken()) {
                        location.href = '/groups.html';
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
    };
}

function updateLoginButtonUI(isLoggedIn) {
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

function formatDateTime(dateTimeString) {
    const date = new Date(dateTimeString);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');

    return `${year}-${month}-${day} ${hours}:${minutes}`;
}

async function loadMyGroups() {
    try {
        const response = await apiCall(`${API_BASE_URL}/my`, {
            method: 'GET'
        });

        if (response.status === 401) {
            groupList.innerHTML = '<p style="text-align: center; color: #999;">로그인이 필요합니다.</p>';
            return;
        }

        if (response.ok) {
            const data = await response.json();
            displayMyGroups(data.groups);
        } else {
            throw new Error('내 그룹 조회 실패');
        }
    } catch (error) {
        console.error('내 그룹 조회 실패:', error);
        groupList.innerHTML = '<p style="text-align: center; color: #999;">그룹을 불러오는데 실패했습니다.</p>';
    }
}

async function loadAllGroups() {
    try {
        const response = await apiCall(API_BASE_URL, {
            method: 'GET'
        });

        if (response.ok) {
            const data = await response.json();
            displayAllGroups(data.groups);
        } else {
            throw new Error('전체 그룹 조회 실패');
        }
    } catch (error) {
        console.error('전체 그룹 조회 실패:', error);
        allGroupList.innerHTML = '<p style="text-align: center; color: #999;">그룹을 불러오는데 실패했습니다.</p>';
    }
}

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

function openModal(modal) {
    modal.classList.add('show');
}

function closeModal(modal) {
    modal.classList.remove('show');
}

function handleKakaoLoginButtonClick() {
    const action = kakaoLoginBtn.dataset.action;

    if (action === 'logout') {
        handleLogout();
    } else {
        handleKakaoLogin();
    }
}

function handleTabButtonClick(e) {
    const tabName = e.target.dataset.tab;
    switchTab(tabName);
}

function setupEventListeners() {
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

document.addEventListener('DOMContentLoaded', async () => {
    setupEventListeners();

    const isLoggedIn = hasAccessToken();

    updateLoginButtonUI(isLoggedIn);

    if (isLoggedIn) {
        loadMyGroups();
    } else {
        loadAllGroups();
    }
});
