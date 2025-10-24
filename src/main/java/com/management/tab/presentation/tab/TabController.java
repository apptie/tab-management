package com.management.tab.presentation.tab;

import com.management.tab.application.TabService;
import com.management.tab.domain.tab.TabTree;
import com.management.tab.domain.tab.vo.TabId;
import com.management.tab.presentation.common.ResponseVoidConst;
import com.management.tab.presentation.tab.dto.request.CreateChildTabRequest;
import com.management.tab.presentation.tab.dto.request.CreateRootTabRequest;
import com.management.tab.presentation.tab.dto.request.MoveTabRequest;
import com.management.tab.presentation.tab.dto.request.ReorderTabRequest;
import com.management.tab.presentation.tab.dto.request.UpdateTabRequest;
import com.management.tab.presentation.tab.dto.response.CreateChildTabResponse;
import com.management.tab.presentation.tab.dto.response.CreateRootTabResponse;
import com.management.tab.presentation.tab.dto.response.TabTreeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tabs")
@RequiredArgsConstructor
public class TabController {

    private final TabService tabService;

    @PostMapping("/groups/{groupId}/root")
    public ResponseEntity<CreateRootTabResponse> createRootTab(
            @PathVariable Long groupId,
            @RequestBody CreateRootTabRequest request
    ) {
        TabId tabId = tabService.createRootTab(groupId, request.title(), request.url());
        CreateRootTabResponse response = new CreateRootTabResponse(tabId.getValue());

        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(response);
    }

    @PostMapping("/{parentId}/children")
    public ResponseEntity<CreateChildTabResponse> createChildTab(
            @PathVariable Long parentId,
            @RequestBody CreateChildTabRequest request
    ) {
        TabId childTabId = tabService.createChildTab(parentId, request.title(), request.url());
        CreateChildTabResponse response = new CreateChildTabResponse(childTabId.getValue());

        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(response);
    }

    @DeleteMapping("/{tabId}")
    public ResponseEntity<Void> deleteTab(
            @PathVariable Long tabId,
            @RequestParam(defaultValue = "true") boolean withSubtree
    ) {
        if (withSubtree) {
            tabService.deleteTabWithSubtree(tabId);
            return ResponseVoidConst.NO_CONTENT;
        }

        tabService.deleteTab(tabId);
        return ResponseVoidConst.NO_CONTENT;
    }

    @PutMapping("/{tabId}/move/root")
    public ResponseEntity<Void> moveTabToRoot(
            @PathVariable Long tabId,
            @RequestParam(defaultValue = "true") boolean withSubtree
    ) {
        if (withSubtree) {
            tabService.moveRootWithSubtree(tabId);
            return ResponseVoidConst.NO_CONTENT;
        }

        tabService.moveRoot(tabId);
        return ResponseVoidConst.NO_CONTENT;
    }

    @PutMapping("/{tabId}/move")
    public ResponseEntity<Void> moveTab(
            @PathVariable Long tabId,
            @RequestBody MoveTabRequest request
    ) {
        if (request.withSubtree()) {
            tabService.moveWithSubtree(tabId, request.newParentId());
            return ResponseVoidConst.OK;
        }

        tabService.move(tabId, request.newParentId());
        return ResponseVoidConst.OK;
    }

    @PutMapping("/{tabId}/reorder")
    public ResponseEntity<Void> reorderTab(
            @PathVariable Long tabId,
            @RequestBody ReorderTabRequest request
    ) {
        tabService.reorderTab(tabId, request.targetTabId(), request.after());
        return ResponseVoidConst.OK;
    }

    @PutMapping("/{tabId}")
    public ResponseEntity<Void> updateTab(
            @PathVariable Long tabId,
            @RequestBody UpdateTabRequest request
    ) {
        tabService.updateTab(tabId, request.title(), request.url());
        return ResponseVoidConst.OK;
    }

    @GetMapping("/groups/{groupId}/tree")
    public ResponseEntity<TabTreeResponse> getTabTree(@PathVariable Long groupId) {
        TabTree tree = tabService.getTabTree(groupId);
        TabTreeResponse response = TabTreeResponse.from(tree);

        return ResponseEntity.ok(response);
    }

}
