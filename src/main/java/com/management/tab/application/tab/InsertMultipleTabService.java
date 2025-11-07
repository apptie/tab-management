package com.management.tab.application.tab;

import com.management.tab.application.tab.dto.request.InsertMultipleTabRequest;
import com.management.tab.application.tab.dto.response.InsertMultipleTabResponse;
import com.management.tab.domain.group.TabGroup;
import com.management.tab.domain.group.vo.TabGroupId;
import com.management.tab.domain.repository.TabGroupRepository;
import com.management.tab.domain.repository.TabRepository;
import com.management.tab.domain.tab.Tab;
import com.management.tab.domain.tab.TabBuilder;
import com.management.tab.domain.tab.TabTree;
import com.management.tab.domain.tab.vo.TabId;
import com.management.tab.domain.tab.vo.TabPosition;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InsertMultipleTabService {

    private final TabRepository tabRepository;
    private final TabGroupRepository tabGroupRepository;

    @Transactional
    public List<InsertMultipleTabResponse> insertMultipleRootTabs(
            Long groupId,
            Long writerId,
            List<InsertMultipleTabRequest> requests
    ) {
        TabGroup tabGroup = tabGroupRepository.findById(groupId);

        if (tabGroup.isNotWriter(writerId)) {
            throw new BulkTabInsertForbiddenException();
        }

        TabTree tabTree = tabRepository.findTabTree(TabGroupId.create(groupId));

        int requestMaxDepth = calculateMaxDepth(requests);
        tabTree.validateCreateDepth(requestMaxDepth, 0);

        TabPosition nextRootPosition = tabTree.getNextRootPosition();

        List<InsertMultipleTabResponse> responses = new ArrayList<>();
        TabPosition currentPosition = nextRootPosition;

        for (InsertMultipleTabRequest request : requests) {
            InsertMultipleTabResponse response = saveBulkTabRecursively(
                    groupId,
                    writerId,
                    request,
                    null,
                    currentPosition,
                    0
            );
            responses.add(response);
            currentPosition = currentPosition.next();
        }

        return responses;
    }

    @Transactional
    public List<InsertMultipleTabResponse> insertMultipleChildTabs(
            Long groupId,
            Long parentTabId,
            Long writerId,
            List<InsertMultipleTabRequest> requests
    ) {
        TabGroup tabGroup = tabGroupRepository.findById(groupId);

        if (tabGroup.isNotWriter(writerId)) {
            throw new BulkTabInsertForbiddenException();
        }

        Tab parentTab = tabRepository.findTab(parentTabId);

        if (parentTab.isNotWriterId(writerId)) {
            throw new BulkTabInsertForbiddenException();
        }

        TabTree tabTree = tabRepository.findTabTree(TabGroupId.create(groupId));
        TabId parentTabIdValue = TabId.create(parentTabId);

        tabTree.validateAddChildDepth(parentTabIdValue);

        int requestMaxDepth = calculateMaxDepth(requests);
        int parentDepth = tabTree.findDepth(parentTabIdValue);
        tabTree.validateCreateDepth(requestMaxDepth, parentDepth + 1);

        TabPosition nextChildPosition = tabTree.getNextChildPosition(parentTabIdValue);

        List<InsertMultipleTabResponse> responses = new ArrayList<>();
        TabPosition currentPosition = nextChildPosition;

        for (InsertMultipleTabRequest request : requests) {
            InsertMultipleTabResponse response = saveBulkTabRecursively(
                    groupId,
                    writerId,
                    request,
                    parentTab,
                    currentPosition,
                    parentDepth + 1
            );
            responses.add(response);
            currentPosition = currentPosition.next();
        }

        return responses;
    }

    private InsertMultipleTabResponse saveBulkTabRecursively(
            Long groupId,
            Long writerId,
            InsertMultipleTabRequest request,
            Tab parentTab,
            TabPosition position,
            int currentDepth
    ) {
        Tab savedTab = saveTab(groupId, writerId, request, parentTab, position);

        List<InsertMultipleTabResponse> childResponses = new ArrayList<>();

        if (!request.children().isEmpty()) {
            TabPosition childPosition = TabPosition.defaultPosition();

            for (InsertMultipleTabRequest childRequest : request.children()) {
                InsertMultipleTabResponse childResponse = saveBulkTabRecursively(
                        groupId,
                        writerId,
                        childRequest,
                        savedTab,
                        childPosition,
                        currentDepth + 1
                );
                childResponses.add(childResponse);
                childPosition = childPosition.next();
            }
        }

        return mapToResponse(savedTab, currentDepth, childResponses);
    }

    private Tab saveTab(
            Long groupId,
            Long writerId,
            InsertMultipleTabRequest request,
            Tab parentTab,
            TabPosition position
    ) {
        if (parentTab == null) {
            return saveRootTab(groupId, writerId, request, position);
        }
        return saveChildTab(parentTab, request, position);
    }

    private Tab saveRootTab(Long groupId, Long writerId, InsertMultipleTabRequest request, TabPosition position) {
        Tab rootTab = TabBuilder.createRoot(
                groupId,
                writerId,
                request.title(),
                request.url(),
                position
        ).build();
        return tabRepository.saveRoot(rootTab);
    }

    private Tab saveChildTab(Tab parentTab, InsertMultipleTabRequest request, TabPosition position) {
        Tab childTab = TabBuilder.createChild(
                parentTab,
                request.title(),
                request.url(),
                position
        ).build();
        return tabRepository.saveChild(childTab);
    }

    private InsertMultipleTabResponse mapToResponse(Tab tab, int depth, List<InsertMultipleTabResponse> childResponses) {
        return new InsertMultipleTabResponse(
                tab.id().getValue(),
                tab.getTitle(),
                tab.getUrl(),
                depth,
                tab.position().getValue(),
                childResponses
        );
    }

    private int calculateMaxDepth(List<InsertMultipleTabRequest> requests) {
        return requests.stream()
                       .mapToInt(this::calculateMaxDepth)
                       .max()
                       .orElse(0);
    }

    private int calculateMaxDepth(InsertMultipleTabRequest request) {
        if (request.children().isEmpty()) {
            return 1;
        }

        int maxChildDepth = request.children().stream()
                                   .mapToInt(this::calculateMaxDepth)
                                   .max()
                                   .orElse(0);

        return maxChildDepth + 1;
    }

    public static class BulkTabInsertForbiddenException extends IllegalArgumentException {

        public BulkTabInsertForbiddenException() {
            super("탭 작성자가 아닙니다.");
        }
    }
}
