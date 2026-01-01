let groupId;
let bulkTabCounter = 0;

async function refreshAccessToken() {
    try {
        const response = await fetch('/refresh-token', {
            method: 'POST',
            credentials: 'include'
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

async function handleLogout() {
    try {
        await fetch('/logout', { method: 'POST', credentials: 'include' });
    } catch (error) {
        console.error('로그아웃 요청 실패:', error);
    } finally {
        document.cookie = 'accessToken=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;';
        document.cookie = 'refreshToken=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;';
        setTimeout(() => {
            window.location.href = '/groups.html';
        }, 500);
    }
}

class TabManager {
    constructor() {
        this.API_BASE_URL = 'http://localhost:8080/api/tabs';
        this.CONTENT_API_BASE_URL = 'http://localhost:8080/api';
        this.GROUP_API_BASE_URL = 'http://localhost:8080/api/groups';
        this.currentGroupId = this.getGroupIdFromUrl();
        this.draggedTab = null;
        this.dropIndicator = null;
        this.dropTarget = null;
        this.lockStates = new Map();
        this.allTabs = [];

        this.currentSelectedTabId = null;
        this.currentSelectedContentId = null;

        this.init();
    }

    getCookie(name) {
        const value = `; ${document.cookie}`;
        const parts = value.split(`; ${name}=`);
        if (parts.length === 2) {
            return decodeURIComponent(parts.pop().split(';').shift());
        }
        return null;
    }

    getAuthHeaders() {
        const accessToken = this.getCookie('accessToken');
        const headers = { 'Content-Type': 'application/json' };

        if (accessToken) {
            headers['Authorization'] = `Bearer ${accessToken}`;
        }

        return headers;
    }

    getGroupIdFromUrl() {
        const params = new URLSearchParams(window.location.search);
        const groupId = parseInt(params.get('groupId'));
        if (!groupId || groupId <= 0) {
            alert('잘못된 그룹 ID입니다.');
            window.location.href = 'groups.html';
            return 1;
        }
        return groupId;
    }

    async init() {
        await this.loadGroupInfo();
        this.setupEventListeners();
        await this.renderTree();
    }

    async loadGroupInfo() {
        try {
            const response = await fetch(`${this.GROUP_API_BASE_URL}/${this.currentGroupId}`);
            if (!response.ok) {
                throw new Error('그룹을 찾을 수 없습니다.');
            }
            const group = await response.json();
            document.getElementById('groupTitle').textContent = `📁 ${group.name}`;
            document.title = `${group.name} - Tab Manager`;
        } catch (error) {
            console.error('그룹 정보 로드 실패:', error);
            alert('그룹을 찾을 수 없습니다.');
            window.location.href = 'groups.html';
        }
    }

    setupEventListeners() {
        document.getElementById('addRootTabBtn').addEventListener('click', () => {
            this.showAddModal(null);
        });

        document.getElementById('addBulkRootTabBtn')?.addEventListener('click', () => {
            this.openBulkTabModal('루트 탭 일괄 추가', null);
        });

        document.getElementById('addTabForm').addEventListener('submit', async (e) => {
            e.preventDefault();
            await this.handleAddTab();
        });

        document.getElementById('editTabForm').addEventListener('submit', async (e) => {
            e.preventDefault();
            await this.handleEditTab();
        });

        document.getElementById('bulkAddTabForm')?.addEventListener('submit', async (e) => {
            e.preventDefault();
            await this.createBulkTabs();
        });

        document.querySelectorAll('.close').forEach(closeBtn => {
            closeBtn.addEventListener('click', (e) => {
                e.target.closest('.modal').classList.remove('show');
            });
        });

        window.addEventListener('click', (e) => {
            if (e.target.classList.contains('modal')) {
                e.target.classList.remove('show');
            }
        });

        document.addEventListener('dragover', (e) => {
            if (this.draggedTab) {
                e.preventDefault();
            }
        });

        document.addEventListener('drop', (e) => {
            if (this.draggedTab) {
                e.preventDefault();
                this.handleGlobalDrop(e);
            }
        });

        document.getElementById('contentForm').addEventListener('submit', (e) => {
            e.preventDefault();
            this.saveContent();
        });

        document.getElementById('bulkCancelBtn')?.addEventListener('click', () => {
            document.getElementById('bulkAddTabModal').classList.remove('show');
        });
    }

    async renderTree() {
        try {
            const response = await fetch(`${this.API_BASE_URL}/groups/${this.currentGroupId}/tree`);
            if (!response.ok) {
                throw new Error('트리 조회 실패');
            }
            const data = await response.json();
            const tree = data.tabs;
            this.allTabs = this.flattenTree(tree);

            const treeContainer = document.getElementById('tabTree');
            treeContainer.innerHTML = '';

            if (tree.length === 0) {
                treeContainer.innerHTML = '<p style="text-align: center; color: #999; padding: 40px;">탭이 없습니다. 최상위 탭을 추가해보세요.</p>';
                return;
            }

            for (const tab of tree) {
                const tabElement = this.createTabElement(tab);
                treeContainer.appendChild(tabElement);
            }
        } catch (error) {
            console.error('트리 조회 실패:', error);
            alert('탭 목록을 불러오는데 실패했습니다: ' + error.message);
        }
    }

    flattenTree(tree) {
        const result = [];
        const flatten = (nodes) => {
            for (const node of nodes) {
                result.push(node);
                if (node.children && node.children.length > 0) {
                    flatten(node.children);
                }
            }
        };
        flatten(tree);
        return result;
    }

    createTabElement(tab) {
        const div = document.createElement('div');
        div.className = 'tab-item';
        div.dataset.tabId = tab.id;
        div.dataset.parentId = tab.parentId || '';
        div.dataset.depth = tab.depth;
        div.draggable = true;

        div.addEventListener('dragstart', (e) => this.handleDragStart(e, tab));
        div.addEventListener('dragover', (e) => this.handleDragOver(e, tab));
        div.addEventListener('dragleave', (e) => this.handleDragLeave(e));
        div.addEventListener('dragend', (e) => this.handleDragEnd(e));

        const hasChildren = tab.children && tab.children.length > 0;

        if (!this.lockStates.has(tab.id)) {
            this.lockStates.set(tab.id, true);
        }
        const isLocked = this.lockStates.get(tab.id);

        const lockButton = hasChildren ? `
            <button class="btn-lock ${isLocked ? 'locked' : 'unlocked'}"
                    data-id="${tab.id}"
                    title="${isLocked ? '🔒 잠김: 하위 탭과 함께 이동/삭제' : '🔓 열림: 이 탭만 이동/삭제 (자식 승격)'}">
                ${isLocked ? '🔒' : '🔓'}
            </button>
        ` : '';

        div.innerHTML = `
            <div class="tab-content">
                <div class="tab-info">
                    <div class="tab-title">${this.escapeHtml(tab.title)}</div>
                    <div class="tab-url">${this.escapeHtml(tab.url)}</div>
                </div>
                <div class="tab-actions">
                    <button class="btn-open-url" data-url="${this.escapeHtml(tab.url)}" title="새 탭에서 열기"></button>
                    ${lockButton}
                    <button class="btn-secondary add-child" data-id="${tab.id}">
                        자식 추가
                    </button>
                    <button class="btn-secondary bulk-add-child" data-id="${tab.id}">
                        일괄 추가
                    </button>
                    <button class="btn-secondary edit-tab" data-id="${tab.id}">
                        수정
                    </button>
                    <button class="btn-danger delete-tab" data-id="${tab.id}">
                        삭제
                    </button>
                </div>
            </div>
        `;

        div.querySelector('.tab-info').addEventListener('click', (e) => {
            e.stopPropagation();
            this.onTabClick(tab.id, tab.title, div);
        });

        div.querySelector('.btn-open-url').addEventListener('click', (e) => {
            e.stopPropagation();
            window.open(tab.url, '_blank', 'noopener,noreferrer');
        });

        const lockBtn = div.querySelector('.btn-lock');
        if (lockBtn) {
            lockBtn.addEventListener('click', (e) => {
                e.stopPropagation();
                this.toggleLock(tab.id);
            });
        }

        div.querySelector('.add-child').addEventListener('click', (e) => {
            e.stopPropagation();
            this.showAddModal(tab.id);
        });

        div.querySelector('.bulk-add-child').addEventListener('click', (e) => {
            e.stopPropagation();
            this.openBulkTabModal(`[${this.escapeHtml(tab.title)}] 하위 탭 일괄 추가`, tab.id);
        });

        div.querySelector('.edit-tab').addEventListener('click', (e) => {
            e.stopPropagation();
            this.showEditModal(tab);
        });

        div.querySelector('.delete-tab').addEventListener('click', async (e) => {
            e.stopPropagation();
            const isLocked = this.lockStates.get(tab.id) !== false;
            const message = isLocked
                ? `"${tab.title}" 탭과 하위 탭들을 모두 삭제하시겠습니까?`
                : `"${tab.title}" 탭만 삭제하시겠습니까? (자식 탭들은 승격됩니다)`;

            if (confirm(message)) {
                await this.deleteTab(tab.id, isLocked);
            }
        });

        if (hasChildren) {
            const childrenContainer = document.createElement('div');
            childrenContainer.className = 'tab-children';

            for (const child of tab.children) {
                childrenContainer.appendChild(this.createTabElement(child));
            }

            div.appendChild(childrenContainer);
        }

        return div;
    }

    toggleLock(tabId) {
        const currentState = this.lockStates.get(tabId);
        this.lockStates.set(tabId, !currentState);
        this.renderTree();
    }

    handleDragStart(e, tab) {
        e.stopPropagation();
        this.draggedTab = {
            ...tab,
            isLocked: this.lockStates.get(tab.id) !== false
        };
        const draggedElement = e.target.closest('.tab-item');
        draggedElement.classList.add('dragging');
        e.dataTransfer.effectAllowed = 'move';
    }

    handleDragOver(e, targetTab) {
        e.preventDefault();
        e.stopPropagation();
        e.dataTransfer.dropEffect = 'move';

        if (!this.draggedTab || this.draggedTab.id === targetTab.id) {
            return false;
        }

        this.dropTarget = targetTab;
        const tabItem = e.currentTarget;
        const rect = tabItem.getBoundingClientRect();
        const mouseY = e.clientY - rect.top;
        const itemHeight = rect.height;

        const childrenElement = tabItem.querySelector('.tab-children');
        let contentHeight = itemHeight;
        if (childrenElement) {
            contentHeight = childrenElement.offsetTop;
        }

        if (mouseY < contentHeight * 0.25) {
            this.dropIndicator = 'before';
            this.clearDropStyles();
            tabItem.classList.add('drop-before');
        } else if (mouseY > contentHeight * 0.75) {
            this.dropIndicator = 'after';
            this.clearDropStyles();
            tabItem.classList.add('drop-after');
        } else {
            this.dropIndicator = 'child';
            this.clearDropStyles();
            tabItem.classList.add('drop-child');
        }

        return false;
    }

    clearDropStyles() {
        document.querySelectorAll('.tab-item').forEach(item => {
            item.classList.remove('drop-before', 'drop-after', 'drop-child');
        });
    }

    handleDragLeave(e) {
        e.stopPropagation();
    }

    async handleGlobalDrop(e) {
        if (!this.draggedTab || !this.dropTarget || !this.dropIndicator) {
            this.clearDropStyles();
            this.draggedTab = null;
            this.dropTarget = null;
            this.dropIndicator = null;
            return;
        }

        await this.performMove();
    }

    async performMove() {
        this.clearDropStyles();
        const targetTab = this.dropTarget;

        try {
            const isLocked = this.draggedTab.isLocked;
            const draggedTabId = this.draggedTab.id;
            const targetTabId = targetTab.id;

            if (this.dropIndicator === 'child') {
                await this.moveTab(draggedTabId, targetTabId, isLocked);
            } else if (this.dropIndicator === 'before' || this.dropIndicator === 'after') {
                const isAfter = this.dropIndicator === 'after';

                if (this.draggedTab.parentId !== targetTab.parentId) {
                    await this.moveTab(draggedTabId, targetTab.parentId, isLocked);
                    await this.renderTree();
                    await new Promise(resolve => setTimeout(resolve, 200));
                    await this.reorderTab(draggedTabId, targetTabId, isAfter);
                } else {
                    await this.reorderTab(draggedTabId, targetTabId, isAfter);
                }
            }

            await this.renderTree();
        } catch (error) {
            console.error('이동 실패:', error);
            alert(error.message || '탭 이동에 실패했습니다.');
            await this.renderTree();
        }

        this.dropIndicator = null;
        this.draggedTab = null;
        this.dropTarget = null;
    }

    handleDragEnd(e) {
        e.stopPropagation();
        const draggedElement = e.target.closest('.tab-item');
        if (draggedElement) {
            draggedElement.classList.remove('dragging');
        }
        this.clearDropStyles();
        this.draggedTab = null;
        this.dropIndicator = null;
        this.dropTarget = null;
    }

    showAddModal(parentId) {
        document.getElementById('parentId').value = parentId || '';
        document.getElementById('tabTitle').value = '';
        document.getElementById('tabUrl').value = '';
        document.getElementById('addTabModal').classList.add('show');
        document.getElementById('tabTitle').focus();
    }

    showEditModal(tab) {
        document.getElementById('editTabId').value = tab.id;
        document.getElementById('editTabTitle').value = tab.title;
        document.getElementById('editTabUrl').value = tab.url;
        document.getElementById('editTabModal').classList.add('show');
        document.getElementById('editTabTitle').focus();
    }

    async handleAddTab() {
        const parentIdValue = document.getElementById('parentId').value;
        const parentId = parentIdValue ? parseInt(parentIdValue) : null;
        const title = document.getElementById('tabTitle').value.trim();
        const url = document.getElementById('tabUrl').value.trim();

        if (!title || !url) {
            alert('제목과 URL을 모두 입력해주세요.');
            return;
        }

        try {
            let response;

            if (parentId) {
                response = await fetch(`${this.API_BASE_URL}/${parentId}/children`, {
                    method: 'POST',
                    headers: this.getAuthHeaders(),
                    body: JSON.stringify({ title, url }),
                    credentials: 'include'
                });
            } else {
                response = await fetch(`${this.API_BASE_URL}/groups/${this.currentGroupId}/root`, {
                    method: 'POST',
                    headers: this.getAuthHeaders(),
                    body: JSON.stringify({ title, url }),
                    credentials: 'include'
                });
            }

            if (response.status === 401) {
                const refreshed = await refreshAccessToken();
                if (refreshed) {
                    await this.handleAddTab();
                    return;
                } else {
                    handleLogout();
                    return;
                }
            }

            if (!response.ok) {
                const error = await response.text();
                throw new Error(error || '탭 추가에 실패했습니다.');
            }

            document.getElementById('addTabModal').classList.remove('show');
            await this.renderTree();
        } catch (error) {
            console.error('탭 추가 실패:', error);
            alert('탭 추가에 실패했습니다: ' + error.message);
        }
    }

    async handleEditTab() {
        const tabId = parseInt(document.getElementById('editTabId').value);
        const title = document.getElementById('editTabTitle').value.trim();
        const url = document.getElementById('editTabUrl').value.trim();

        if (!title || !url) {
            alert('제목과 URL을 모두 입력해주세요.');
            return;
        }

        try {
            const response = await fetch(`${this.API_BASE_URL}/${tabId}`, {
                method: 'PUT',
                headers: this.getAuthHeaders(),
                body: JSON.stringify({ title, url }),
                credentials: 'include'
            });

            if (response.status === 401) {
                const refreshed = await refreshAccessToken();
                if (refreshed) {
                    await this.handleEditTab();
                    return;
                } else {
                    handleLogout();
                    return;
                }
            }

            if (!response.ok) {
                const error = await response.text();
                throw new Error(error || '탭 수정에 실패했습니다.');
            }

            document.getElementById('editTabModal').classList.remove('show');
            await this.renderTree();
        } catch (error) {
            console.error('탭 수정 실패:', error);
            alert('탭 수정에 실패했습니다: ' + error.message);
        }
    }

    async deleteTab(tabId, withSubtree) {
        try {
            const response = await fetch(`${this.API_BASE_URL}/${tabId}?withSubtree=${withSubtree}`, {
                method: 'DELETE',
                headers: this.getAuthHeaders(),
                credentials: 'include'
            });

            if (response.status === 401) {
                const refreshed = await refreshAccessToken();
                if (refreshed) {
                    await this.deleteTab(tabId, withSubtree);
                    return;
                } else {
                    handleLogout();
                    return;
                }
            }

            if (!response.ok) {
                const error = await response.text();
                throw new Error(error || '탭 삭제에 실패했습니다.');
            }

            this.lockStates.delete(tabId);
            await this.renderTree();
        } catch (error) {
            console.error('탭 삭제 실패:', error);
            alert(error.message || '탭 삭제에 실패했습니다.');
        }
    }

    async moveTab(tabId, newParentId, withSubtree) {
        if (newParentId === null || newParentId === undefined || newParentId === '') {
            const response = await fetch(`${this.API_BASE_URL}/${tabId}/move/root?withSubtree=${withSubtree}`, {
                method: 'PUT',
                headers: this.getAuthHeaders(),
                credentials: 'include'
            });

            if (response.status === 401) {
                const refreshed = await refreshAccessToken();
                if (refreshed) {
                    await this.moveTab(tabId, newParentId, withSubtree);
                    return;
                } else {
                    handleLogout();
                    return;
                }
            }

            if (!response.ok) {
                const error = await response.text();
                throw new Error(error || '탭을 루트로 이동하는데 실패했습니다.');
            }
        } else {
            const response = await fetch(`${this.API_BASE_URL}/${tabId}/move`, {
                method: 'PUT',
                headers: this.getAuthHeaders(),
                body: JSON.stringify({
                    newParentId: newParentId,
                    withSubtree: withSubtree
                }),
                credentials: 'include'
            });

            if (response.status === 401) {
                const refreshed = await refreshAccessToken();
                if (refreshed) {
                    await this.moveTab(tabId, newParentId, withSubtree);
                    return;
                } else {
                    handleLogout();
                    return;
                }
            }

            if (!response.ok) {
                const error = await response.text();
                throw new Error(error || '탭 이동에 실패했습니다.');
            }
        }
    }

    async reorderTab(tabId, targetTabId, after) {
        const response = await fetch(`${this.API_BASE_URL}/${tabId}/reorder`, {
            method: 'PUT',
            headers: this.getAuthHeaders(),
            body: JSON.stringify({
                targetTabId: targetTabId,
                after: after
            }),
            credentials: 'include'
        });

        if (response.status === 401) {
            const refreshed = await refreshAccessToken();
            if (refreshed) {
                await this.reorderTab(tabId, targetTabId, after);
                return;
            } else {
                handleLogout();
                return;
            }
        }

        if (!response.ok) {
            const error = await response.text();
            throw new Error(error || '순서 변경에 실패했습니다.');
        }
    }

    async onTabClick(tabId, tabTitle, element) {
        document.querySelectorAll('.tab-item').forEach(item => {
            item.classList.remove('selected');
        });

        element.classList.add('selected');

        this.currentSelectedTabId = tabId;
        document.getElementById('contentPanel').classList.remove('hidden');

        await this.loadTabContents(tabId);
        this.showContentList();
    }

    async loadTabContents(tabId) {
        try {
            const response = await fetch(`${this.CONTENT_API_BASE_URL}/tabs/${tabId}/contents`, {
                headers: this.getAuthHeaders(),
                credentials: 'include'
            });

            if (response.status === 401) {
                const refreshed = await refreshAccessToken();
                if (refreshed) {
                    await this.loadTabContents(tabId);
                    return;
                } else {
                    handleLogout();
                    return;
                }
            }

            if (!response.ok) {
                console.error('Response status:', response.status);
                throw new Error('Failed to load contents');
            }

            const data = await response.json();
            this.renderContents(data.contents);
        } catch (error) {
            console.error('Error loading contents:', error);
            alert('탭 내용을 불러오는데 실패했습니다.');
        }
    }

    renderContents(contents) {
        this.showContentList(contents);
    }

    async deleteContentFromList(contentId) {
        if (!confirm('이 내용을 삭제하시겠습니까?')) return;

        try {
            await fetch(`${this.CONTENT_API_BASE_URL}/contents/${contentId}`, {
                method: 'DELETE',
                headers: this.getAuthHeaders(),
                credentials: 'include'
            });

            await this.loadTabContents(this.currentSelectedTabId);
        } catch (error) {
            console.error('Error deleting content:', error);
            alert('삭제에 실패했습니다.');
        }
    }

    showContentList(contents = null) {
        const contentPanel = document.getElementById('contentPanel');

        if (contents === null) {
            this.loadTabContents(this.currentSelectedTabId);
            return;
        }

        let html = `
            <div style="padding: 20px; border-bottom: 1px solid #dadce0;">
                <button class="btn-back-detail" onclick="tabManager.closeContentPanel()">← 탭 그룹으로 돌아가기</button>
            </div>
            <div style="flex: 1; overflow-y: auto; padding: 20px;">
        `;

        if (contents.length === 0) {
            html += '<div style="text-align: center; padding: 40px; color: #5f6368;">탭 내용이 없습니다.</div>';
        } else {
            html += contents.map(content => `
                <div class="content-item" data-content-id="${content.id}" onclick="tabManager.showContentDetail(${content.id}, \`${this.escapeHtml(content.content)}\`)">
                    <div class="content-item-text">${this.escapeHtml(content.content)}</div>
                    <button class="content-item-delete" onclick="event.stopPropagation(); tabManager.deleteContentFromList(${content.id})">×</button>
                </div>
            `).join('');
        }

        html += `
            </div>
            <div style="padding: 20px; border-top: 1px solid #dadce0; display: flex; justify-content: flex-end;">
                <button class="btn-primary" onclick="tabManager.openContentModal('add')">탭 내용 추가</button>
            </div>
        `;

        contentPanel.innerHTML = html;
    }

    showContentDetail(contentId, contentText) {
        this.currentSelectedContentId = contentId;

        const contentPanel = document.getElementById('contentPanel');

        contentPanel.innerHTML = `
            <div style="padding: 20px; border-bottom: 1px solid #dadce0;">
                <button class="btn-back-detail" onclick="tabManager.showContentList()">← 목록으로 돌아가기</button>
            </div>
            <div style="flex: 1; overflow-y: auto; padding: 30px;">
                <div class="content-detail-text">${this.escapeHtml(contentText)}</div>
            </div>
            <div style="padding: 20px; border-top: 1px solid #dadce0; display: flex; justify-content: flex-end; gap: 12px;">
                <button class="btn-secondary" onclick="tabManager.openContentModal('edit', ${contentId})">수정</button>
                <button class="btn-danger" onclick="tabManager.deleteContent(${contentId})">삭제</button>
            </div>
        `;
    }

    closeContentPanel() {
        document.getElementById('contentPanel').classList.add('hidden');
        document.querySelectorAll('.tab-item').forEach(item => {
            item.classList.remove('selected');
        });
        this.currentSelectedTabId = null;
        this.currentSelectedContentId = null;
    }

    openContentModal(mode, contentId = null) {
        const modal = document.getElementById('contentModal');
        const title = document.getElementById('contentModalTitle');
        const form = document.getElementById('contentForm');

        form.reset();
        document.getElementById('contentTabId').value = this.currentSelectedTabId;

        if (mode === 'add') {
            title.textContent = '탭 내용 추가';
            document.getElementById('contentId').value = '';
        } else {
            title.textContent = '탭 내용 수정';
            document.getElementById('contentId').value = contentId;
            this.loadContentForEdit(contentId);
        }

        modal.classList.add('show');
    }

    async loadContentForEdit(contentId) {
        try {
            const response = await fetch(`${this.CONTENT_API_BASE_URL}/contents/${contentId}`, {
                headers: this.getAuthHeaders(),
                credentials: 'include'
            });

            if (response.status === 401) {
                const refreshed = await refreshAccessToken();
                if (refreshed) {
                    await this.loadContentForEdit(contentId);
                    return;
                } else {
                    handleLogout();
                    return;
                }
            }

            if (!response.ok) throw new Error('Failed to load content');

            const data = await response.json();
            document.getElementById('contentText').value = data.content;
        } catch (error) {
            console.error('Error loading content:', error);
            alert('내용을 불러오는데 실패했습니다.');
        }
    }

    async saveContent() {
        const contentId = document.getElementById('contentId').value;
        const tabId = document.getElementById('contentTabId').value;
        const content = document.getElementById('contentText').value;

        try {
            if (contentId) {
                await fetch(`${this.CONTENT_API_BASE_URL}/contents/${contentId}`, {
                    method: 'PUT',
                    headers: this.getAuthHeaders(),
                    body: JSON.stringify({ content }),
                    credentials: 'include'
                });
            } else {
                await fetch(`${this.CONTENT_API_BASE_URL}/tabs/${tabId}/contents`, {
                    method: 'POST',
                    headers: this.getAuthHeaders(),
                    body: JSON.stringify({ content }),
                    credentials: 'include'
                });
            }

            document.getElementById('contentModal').classList.remove('show');
            await this.loadTabContents(this.currentSelectedTabId);
        } catch (error) {
            console.error('Error saving content:', error);
            alert('저장에 실패했습니다.');
        }
    }

    async deleteContent(contentId) {
        if (!confirm('이 내용을 삭제하시겠습니까?')) return;

        try {
            await fetch(`${this.CONTENT_API_BASE_URL}/contents/${contentId}`, {
                method: 'DELETE',
                headers: this.getAuthHeaders(),
                credentials: 'include'
            });

            await this.loadTabContents(this.currentSelectedTabId);
        } catch (error) {
            console.error('Error deleting content:', error);
            alert('삭제에 실패했습니다.');
        }
    }

    escapeHtml(text) {
        const div = document.createElement('div');
        div.textContent = text;
        return div.innerHTML;
    }

    openBulkTabModal(title, parentId = null) {
        const modal = document.getElementById('bulkAddTabModal');
        if (!modal) return;

        document.querySelector('.bulk-modal-header h2').textContent = title;
        document.getElementById('bulkParentId').value = parentId || '';
        const bulkTabsContainer = document.getElementById('bulkTabsContainer');
        bulkTabsContainer.innerHTML = '';
        bulkTabCounter = 0;
        this.addBulkTabItem(null, 0);
        modal.classList.add('show');
    }

    addBulkTabItem(parentElement, depth) {
        const tabId = bulkTabCounter++;
        const bulkTabsContainer = document.getElementById('bulkTabsContainer');

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
        addChildBtn.addEventListener('click', (e) => {
            e.preventDefault();
            const childrenContainer = tabItem.querySelector('.bulk-tab-children');
            this.addBulkTabItem(childrenContainer, depth + 1);
        });

        const addSiblingBtn = document.createElement('button');
        addSiblingBtn.type = 'button';
        addSiblingBtn.className = 'btn-add-sibling';
        addSiblingBtn.textContent = '형제 추가';
        addSiblingBtn.addEventListener('click', (e) => {
            e.preventDefault();
            const container = parentElement || bulkTabsContainer;
            this.addBulkTabItem(container, depth);
        });

        const removeBtn = document.createElement('button');
        removeBtn.type = 'button';
        removeBtn.className = 'btn-remove-tab';
        removeBtn.textContent = '제거';
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

    extractBulkTabData() {
        const bulkTabsContainer = document.getElementById('bulkTabsContainer');
        const rootItems = bulkTabsContainer.querySelectorAll(':scope > .bulk-tab-item');
        return Array.from(rootItems).map(item => this.extractTabItem(item));
    }

    extractTabItem(element) {
        const titleInput = element.querySelector('.bulk-tab-title');
        const urlInput = element.querySelector('.bulk-tab-url');
        const childrenContainer = element.querySelector('.bulk-tab-children');

        const children = childrenContainer
            ? Array.from(childrenContainer.querySelectorAll(':scope > .bulk-tab-item')).map(child => this.extractTabItem(child))
            : [];

        return {
            title: titleInput.value.trim(),
            url: urlInput.value.trim(),
            children: children
        };
    }

    validateBulkTabs(tabs) {
        for (const tab of tabs) {
            if (!tab.title || !tab.url) return false;
            if (tab.children && tab.children.length > 0 && !this.validateBulkTabs(tab.children)) return false;
        }
        return true;
    }

    async createBulkTabs() {
        const parentId = document.getElementById('bulkParentId').value;
        const bulkTabs = this.extractBulkTabData();

        if (!this.validateBulkTabs(bulkTabs)) {
            alert('모든 탭에 제목과 URL을 입력해주세요');
            return;
        }

        try {
            const url = parentId
                ? `/api/groups/${this.currentGroupId}/tabs/${parentId}/multiple-children`
                : `/api/groups/${this.currentGroupId}/tabs/multiple`;

            const response = await fetch(url, {
                method: 'POST',
                headers: this.getAuthHeaders(),
                body: JSON.stringify(bulkTabs),
                credentials: 'include'
            });

            if (response.status === 401) {
                const refreshed = await refreshAccessToken();
                if (refreshed) {
                    await this.createBulkTabs();
                    return;
                } else {
                    handleLogout();
                    return;
                }
            }

            if (!response.ok) {
                const errorData = await response.json().catch(() => ({}));
                throw new Error(errorData.message || '탭 저장에 실패했습니다');
            }

            document.getElementById('bulkAddTabModal').classList.remove('show');
            document.getElementById('bulkAddTabForm').reset();
            await this.renderTree();
        } catch (error) {
            console.error('벌크 탭 생성 오류:', error);
            alert('오류: ' + error.message);
        }
    }
}

let tabManager;

document.addEventListener('DOMContentLoaded', () => {
    tabManager = new TabManager();
});
