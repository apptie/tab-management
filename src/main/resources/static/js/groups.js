// groups.js
const API_BASE_URL = '/api/groups';

// DOM Elements
const groupList = document.getElementById('groupList');
const addGroupBtn = document.getElementById('addGroupBtn');
const addGroupModal = document.getElementById('addGroupModal');
const editGroupModal = document.getElementById('editGroupModal');
const addGroupForm = document.getElementById('addGroupForm');
const editGroupForm = document.getElementById('editGroupForm');
const groupNameInput = document.getElementById('groupName');
const editGroupIdInput = document.getElementById('editGroupId');
const editGroupNameInput = document.getElementById('editGroupName');

// 모달 닫기 버튼들
const closeButtons = document.querySelectorAll('.close');

// 날짜 포맷팅 함수
function formatDateTime(dateTimeString) {
    const date = new Date(dateTimeString);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');

    return `${year}-${month}-${day} ${hours}:${minutes}`;
}

// 그룹 목록 조회
async function loadGroups() {
    try {
        const response = await fetch(API_BASE_URL);
        const data = await response.json();

        // TabGroupCollectionResponse의 groups 필드 접근
        displayGroups(data.groups);
    } catch (error) {
        console.error('그룹 목록 조회 실패:', error);
        alert('그룹 목록을 불러오는데 실패했습니다.');
    }
}

// 그룹 목록 표시
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

    // 그룹 카드 클릭 이벤트 (탭 페이지로 이동)
    document.querySelectorAll('.group-card').forEach(card => {
        card.addEventListener('click', (e) => {
            // 버튼 클릭 시에는 이동하지 않음
            if (e.target.tagName === 'BUTTON') return;

            const groupId = card.dataset.id;
            window.location.href = `/tabs.html?groupId=${groupId}`;
        });
    });
}

// HTML 이스케이프 처리
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

// 그룹 추가
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
            },
            body: JSON.stringify({ name })
        });

        if (response.ok) {
            const data = await response.json();
            console.log('그룹 생성 성공:', data);

            closeModal(addGroupModal);
            addGroupForm.reset();
            loadGroups();
        } else {
            throw new Error('그룹 추가 실패');
        }
    } catch (error) {
        console.error('그룹 추가 실패:', error);
        alert('그룹 추가에 실패했습니다.');
    }
});

// 그룹 수정 모달 열기
function openEditModal(id, name) {
    editGroupIdInput.value = id;
    editGroupNameInput.value = name;
    openModal(editGroupModal);
}

// 그룹 수정
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
            },
            body: JSON.stringify({ name })
        });

        if (response.ok) {
            closeModal(editGroupModal);
            loadGroups();
        } else {
            throw new Error('그룹 수정 실패');
        }
    } catch (error) {
        console.error('그룹 수정 실패:', error);
        alert('그룹 수정에 실패했습니다.');
    }
});

// 그룹 삭제
async function deleteGroup(id) {
    if (!confirm('정말 이 그룹을 삭제하시겠습니까?\n그룹에 포함된 모든 탭도 함께 삭제됩니다.')) {
        return;
    }

    try {
        const response = await fetch(`${API_BASE_URL}/${id}`, {
            method: 'DELETE'
        });

        if (response.ok) {
            loadGroups();
        } else {
            throw new Error('그룹 삭제 실패');
        }
    } catch (error) {
        console.error('그룹 삭제 실패:', error);
        alert('그룹 삭제에 실패했습니다.');
    }
}

// 모달 열기
function openModal(modal) {
    modal.classList.add('show');
}

// 모달 닫기
function closeModal(modal) {
    modal.classList.remove('show');
}

// 이벤트 리스너
addGroupBtn.addEventListener('click', () => openModal(addGroupModal));

closeButtons.forEach(btn => {
    btn.addEventListener('click', function() {
        closeModal(this.closest('.modal'));
    });
});

// 모달 외부 클릭시 닫기
window.addEventListener('click', (e) => {
    if (e.target.classList.contains('modal')) {
        closeModal(e.target);
    }
});

// 페이지 로드시 그룹 목록 조회
document.addEventListener('DOMContentLoaded', loadGroups);
