class TabManager {
    constructor() {
        this.API_BASE_URL = 'http://localhost:8080/api/tabs';
        this.GROUP_API_BASE_URL = 'http://localhost:8080/api/groups';
        this.currentGroupId = this.getGroupIdFromUrl();
        this.draggedTab = null;
        this.dropIndicator = null;
        this.dropTarget = null;
        this.lockStates = new Map();
        this.allTabs = [];
        this.init();
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

        document.getElementById('addTabForm').addEventListener('submit', async (e) => {
            e.preventDefault();
            await this.handleAddTab();
        });

        document.getElementById('editTabForm').addEventListener('submit', async (e) => {
            e.preventDefault();
            await this.handleEditTab();
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
                    ${lockButton}
                    <button class="btn-secondary add-child" data-id="${tab.id}">
                        자식 추가
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
        console.log(`자물쇠 토글: tabId=${tabId}, newState=${!currentState ? 'locked' : 'unlocked'}`);
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

        console.log('드래그 시작:', tab.title, '(id=' + tab.id + ', locked=' + this.draggedTab.isLocked + ')');
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
        console.log('🌐 전역 drop 발생 - dropTarget:', this.dropTarget?.title, 'indicator:', this.dropIndicator);

        if (!this.draggedTab || !this.dropTarget || !this.dropIndicator) {
            console.log('❌ drop 조건 미충족');
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

            console.log(`이동 시작: locked=${isLocked}, indicator=${this.dropIndicator}`);

            if (this.dropIndicator === 'child') {
                console.log(`"${this.draggedTab.title}"을(를) "${targetTab.title}"의 자식으로 이동`);
                await this.moveTab(draggedTabId, targetTabId, isLocked);

            } else if (this.dropIndicator === 'before' || this.dropIndicator === 'after') {
                const isAfter = this.dropIndicator === 'after';
                console.log(`"${this.draggedTab.title}"을(를) "${targetTab.title}"의 ${isAfter ? '뒤' : '앞'}으로 이동`);

                if (this.draggedTab.parentId !== targetTab.parentId) {
                    console.log('부모 변경: ' + this.draggedTab.parentId + ' → ' + targetTab.parentId);
                    await this.moveTab(draggedTabId, targetTab.parentId, isLocked);
                    await this.renderTree();
                    await new Promise(resolve => setTimeout(resolve, 200));
                    await this.reorderTab(draggedTabId, targetTabId, isAfter);
                } else {
                    console.log('같은 부모 내 순서 변경');
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
                console.log('자식 탭 추가:', { parentId, title, url });
                response = await fetch(`${this.API_BASE_URL}/${parentId}/children`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ title, url })
                });
            } else {
                console.log('루트 탭 추가:', { groupId: this.currentGroupId, title, url });
                response = await fetch(`${this.API_BASE_URL}/groups/${this.currentGroupId}/root`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ title, url })
                });
            }

            if (!response.ok) {
                const error = await response.text();
                throw new Error(error || '탭 추가에 실패했습니다.');
            }

            const result = await response.json();
            console.log('탭 생성 완료:', result);

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
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ title, url })
            });

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
            console.log(`탭 삭제: tabId=${tabId}, withSubtree=${withSubtree}`);

            const response = await fetch(`${this.API_BASE_URL}/${tabId}?withSubtree=${withSubtree}`, {
                method: 'DELETE'
            });

            if (!response.ok) {
                const error = await response.text();
                throw new Error(error || '탭 삭제에 실패했습니다.');
            }

            this.lockStates.delete(tabId);
            await this.renderTree();

            console.log('탭 삭제 완료');
        } catch (error) {
            console.error('탭 삭제 실패:', error);
            alert(error.message || '탭 삭제에 실패했습니다.');
        }
    }

    async moveTab(tabId, newParentId, withSubtree) {
        console.log(`moveTab 호출: tabId=${tabId}, newParentId=${newParentId}, withSubtree=${withSubtree}`);

        // newParentId가 null이면 루트로 이동
        if (newParentId === null || newParentId === undefined || newParentId === '') {
            console.log('루트로 이동');
            const response = await fetch(`${this.API_BASE_URL}/${tabId}/move/root?withSubtree=${withSubtree}`, {
                method: 'PUT'
            });

            if (!response.ok) {
                const error = await response.text();
                throw new Error(error || '탭을 루트로 이동하는데 실패했습니다.');
            }
        } else {
            // 부모 변경
            console.log('부모 변경');
            const response = await fetch(`${this.API_BASE_URL}/${tabId}/move`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    newParentId: newParentId,
                    withSubtree: withSubtree
                })
            });

            if (!response.ok) {
                const error = await response.text();
                throw new Error(error || '탭 이동에 실패했습니다.');
            }
        }

        console.log(`moveTab 완료`);
    }

    async reorderTab(tabId, targetTabId, after) {
        console.log(`reorderTab 호출: tabId=${tabId}, targetTabId=${targetTabId}, after=${after}`);

        const response = await fetch(`${this.API_BASE_URL}/${tabId}/reorder`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                targetTabId: targetTabId,
                after: after
            })
        });

        if (!response.ok) {
            const error = await response.text();
            throw new Error(error || '순서 변경에 실패했습니다.');
        }

        console.log('reorderTab 완료');
    }

    escapeHtml(text) {
        const div = document.createElement('div');
        div.textContent = text;
        return div.innerHTML;
    }
}

document.addEventListener('DOMContentLoaded', () => {
    new TabManager();
});
