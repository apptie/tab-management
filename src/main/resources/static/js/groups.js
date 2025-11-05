const API_BASE_URL = '/api/groups';
const KAKAO_LOGIN_URL = '/oauth2/authorization/kakao';

// DOM Elements
const groupList = document.getElementById('groupList');
const addGroupBtn = document.getElementById('addGroupBtn');
const kakaoLoginBtn = document.getElementById('kakaoLoginBtn');
const addGroupModal = document.getElementById('addGroupModal');
const editGroupModal = document.getElementById('editGroupModal');
const addGroupForm = document.getElementById('addGroupForm');
const editGroupForm = document.getElementById('editGroupForm');
const groupNameInput = document.getElementById('groupName');
const editGroupIdInput = document.getElementById('editGroupId');
const editGroupNameInput = document.getElementById('editGroupName');

const closeButtons = document.querySelectorAll('.close');

// ============ 쿠키 확인 ============
function hasAccessToken() {
    const name = 'accessToken=';
    const decodedCookie = decodeURIComponent(document.cookie);
    return decodedCookie.indexOf(name) > -1;
}

// ============ CSRF 토큰 처리 ============
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

// ============ 로그인 UI 업데이트 (간단함) ============
async function updateLoginUI() {
    const isLoggedIn = hasAccessToken();  // 쿠키만 확인

    if (isLoggedIn) {
        kakaoLoginBtn.innerHTML = `
            <span class="kakao-icon">
                <svg version="1.1" xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 16 16" fill="currentColor">
                    <path d="M8 1.5c-3.722 0-6.75 2.486-6.75 5.552 0 1.85 1.177 3.482 2.97 4.501-0.049 0.459-0.241 0.855-0.482 1.368-0.034 0.088-0.069 0.18-0.105 0.271-0.075 0.193-0.15 0.385-0.215 0.577-0.022 0.062-0.038 0.117-0.055 0.172-0.078 0.239-0.147 0.476-0.197 0.708 2.215-0.065 3.921-1.267 4.777-1.988 0.47 0.072 0.958 0.11 1.456 0.11 3.722 0 6.75-2.486 6.75-5.552s-3.027-5.552-6.75-5.552z"/>
                </svg>
            </span>
            로그아웃
        `;
        kakaoLoginBtn.onclick = handleLogout;
        return true;
    } else {
        kakaoLoginBtn.innerHTML = `
            <span class="kakao-icon">
                <svg version="1.1" xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 16 16" fill="currentColor">
                    <path d="M8 1.5c-3.722 0-6.75 2.486-6.75 5.552 0 1.85 1.177 3.482 2.97 4.501-0.049 0.459-0.241 0.855-0.482 1.368-0.034 0.088-0.069 0.18-0.105 0.271-0.075 0.193-0.15 0.385-0.215 0.577-0.022 0.062-0.038 0.117-0.055 0.172-0.078 0.239-0.147 0.476-0.197 0.708 2.215-0.065 3.921-1.267 4.777-1.988 0.47 0.072 0.958 0.11 1.456 0.11 3.722 0 6.75-2.486 6.75-5.552s-3.027-5.552-6.75-5.552z"/>
                </svg>
            </span>
            카카오로 로그인
        `;
        kakaoLoginBtn.onclick = handleKakaoLogin;
        return false;
    }
}

// ============ 카카오 로그인 ============
function handleKakaoLogin() {
    const width = 500;
    const height = 600;
    const left = (window.innerWidth - width) / 2;
    const top = (window.innerHeight - height) / 2;

    // postMessage 리스너 등록
    const handleLoginMessage = (event) => {
        console.log('메시지 수신:', event.data);

        if (event.data && event.data.type === 'LOGIN_SUCCESS') {
            console.log('로그인 성공 신호 수신 - 새로고침');
            window.removeEventListener('message', handleLoginMessage);
            location.reload();
        }
    };

    window.addEventListener('message', handleLoginMessage);

    const popup = window.open(
        KAKAO_LOGIN_URL,
        'kakao_login',
        `width=${width},height=${height},left=${left},top=${top},resizable=no`
    );

    if (!popup) {
        console.log('팝업 차단됨 - 새 탭으로 열기');
        window.open(KAKAO_LOGIN_URL, '_blank');

        setTimeout(() => {
            window.removeEventListener('message', handleLoginMessage);
        }, 30000);

        return;
    }

    console.log('팝업 정상 열림');

    setTimeout(() => {
        window.removeEventListener('message', handleLoginMessage);
    }, 30000);
}

// ============ 로그아웃 ============
async function handleLogout() {
    try {
        const response = await fetch('/logout', {
            method: 'POST',
            headers: {
                ...getCsrfHeaders()
            },
            credentials: 'include'
        });

        if (response.ok) {
            updateLoginUI();
            groupList.innerHTML = '<p style="text-align: center; color: #999;">로그인이 필요합니다.</p>';
        }
    } catch (error) {
        console.error('로그아웃 실패:', error);
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

// ============ 그룹 목록 조회 ============
async function loadGroups() {
    const isLoggedIn = await updateLoginUI();

    if (!isLoggedIn) {
        groupList.innerHTML = '<p style="text-align: center; color: #999;">로그인이 필요합니다.</p>';
        return;
    }

    try {
        const response = await fetch(API_BASE_URL, {
            credentials: 'include'
        });

        if (response.status === 401) {
            groupList.innerHTML = '<p style="text-align: center; color: #999;">로그인이 필요합니다.</p>';
            return;
        }

        if (response.ok) {
            const data = await response.json();
            displayGroups(data.groups);
        } else {
            throw new Error('그룹 목록 조회 실패');
        }
    } catch (error) {
        console.error('그룹 목록 조회 실패:', error);
        alert('그룹 목록을 불러오는데 실패했습니다.');
    }
}

// ============ 그룹 목록 표시 ============
function displayGroups(groups) {
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

    document.querySelectorAll('.group-card').forEach(card => {
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
        const response = await fetch(API_BASE_URL, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                ...getCsrfHeaders()
            },
            credentials: 'include',
            body: JSON.stringify({ name })
        });

        if (response.ok) {
            const data = await response.json();
            console.log('그룹 생성 성공:', data);

            closeModal(addGroupModal);
            addGroupForm.reset();
            loadGroups();
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
        const response = await fetch(`${API_BASE_URL}/${id}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                ...getCsrfHeaders()
            },
            credentials: 'include',
            body: JSON.stringify({ name })
        });

        if (response.ok) {
            closeModal(editGroupModal);
            loadGroups();
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
        const response = await fetch(`${API_BASE_URL}/${id}`, {
            method: 'DELETE',
            headers: {
                ...getCsrfHeaders()
            },
            credentials: 'include'
        });

        if (response.ok) {
            loadGroups();
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

// ============ 이벤트 리스너 ============
addGroupBtn.addEventListener('click', () => openModal(addGroupModal));
kakaoLoginBtn.addEventListener('click', handleKakaoLogin);

closeButtons.forEach(btn => {
    btn.addEventListener('click', function() {
        closeModal(this.closest('.modal'));
    });
});

window.addEventListener('click', (e) => {
    if (e.target.classList.contains('modal')) {
        closeModal(e.target);
    }
});

// ============ 페이지 로드시 ============
document.addEventListener('DOMContentLoaded', loadGroups);
