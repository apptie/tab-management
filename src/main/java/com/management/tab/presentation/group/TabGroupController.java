package com.management.tab.presentation.group;

import com.management.tab.application.tab.TabGroupService;
import com.management.tab.config.auth.resolver.CurrentUser;
import com.management.tab.config.auth.resolver.CurrentUserId;
import com.management.tab.domain.group.TabGroup;
import com.management.tab.presentation.common.ResponseVoidConst;
import com.management.tab.presentation.group.dto.request.CreateTabGroupRequest;
import com.management.tab.presentation.group.dto.request.UpdateTabGroupRequest;
import com.management.tab.presentation.group.dto.response.CreateTabGroupResponse;
import com.management.tab.presentation.group.dto.response.TabGroupCollectionResponse;
import com.management.tab.presentation.group.dto.response.TabGroupResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class TabGroupController {

    private final TabGroupService tabGroupService;

    @GetMapping
    public ResponseEntity<TabGroupCollectionResponse> getAllGroups() {
        List<TabGroup> tabGroups = tabGroupService.getAllGroups();
        TabGroupCollectionResponse response = TabGroupCollectionResponse.from(tabGroups);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/my")
    public ResponseEntity<TabGroupCollectionResponse> getAllWriterGroups(@CurrentUser CurrentUserId currentUserId) {
        List<TabGroup> tabGroups = tabGroupService.getAllWriterGroups(currentUserId.userId());
        TabGroupCollectionResponse response = TabGroupCollectionResponse.from(tabGroups);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TabGroupResponse> getGroup(@PathVariable Long id) {
        TabGroup tabGroup = tabGroupService.getGroup(id);
        TabGroupResponse response = TabGroupResponse.from(tabGroup);

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<CreateTabGroupResponse> createGroup(
            @RequestBody CreateTabGroupRequest request,
            @CurrentUser CurrentUserId currentUserId
    ) {
        Long tabGroupId = tabGroupService.createGroup(currentUserId.userId(), request.name());
        CreateTabGroupResponse response = new CreateTabGroupResponse(tabGroupId);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{tabGroupId}")
    public ResponseEntity<Void> updateGroup(
            @PathVariable Long tabGroupId,
            @RequestBody UpdateTabGroupRequest request,
            @CurrentUser CurrentUserId currentUserId
    ) {
        tabGroupService.updateGroup(tabGroupId, request.name(), currentUserId.userId());
        return ResponseVoidConst.OK;
    }

    @DeleteMapping("/{tabGroupId}")
    public ResponseEntity<Void> deleteGroup(@PathVariable Long tabGroupId, @CurrentUser CurrentUserId currentUserId) {
        tabGroupService.delete(tabGroupId, currentUserId.userId());
        return ResponseVoidConst.OK;
    }
}
