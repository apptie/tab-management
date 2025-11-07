package com.management.tab.presentation.tab;

import com.management.tab.application.tab.InsertMultipleTabService;
import com.management.tab.application.tab.dto.request.InsertMultipleTabRequest;
import com.management.tab.application.tab.dto.response.InsertMultipleTabResponse;
import com.management.tab.config.auth.resolver.CurrentUser;
import com.management.tab.config.auth.resolver.CurrentUserId;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/groups/{groupId}/tabs")
@RequiredArgsConstructor
public class InsertMultipleTabController {

    private final InsertMultipleTabService insertMultipleTabService;

    @PostMapping("/multiple")
    public ResponseEntity<List<InsertMultipleTabResponse>> insertMultipleRootTabs(
            @PathVariable Long groupId,
            @RequestBody List<InsertMultipleTabRequest> requests,
            @CurrentUser CurrentUserId currentUserId
    ) {
        List<InsertMultipleTabResponse> responses = insertMultipleTabService.insertMultipleRootTabs(
                groupId,
                currentUserId.userId(),
                requests
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(responses);
    }

    @PostMapping("/{parentTabId}/multiple-children")
    public ResponseEntity<List<InsertMultipleTabResponse>> insertMultipleChildTabs(
            @PathVariable Long groupId,
            @PathVariable Long parentTabId,
            @RequestBody List<InsertMultipleTabRequest> requests,
            @CurrentUser CurrentUserId currentUserId
    ) {
        List<InsertMultipleTabResponse> responses = insertMultipleTabService.insertMultipleChildTabs(
                groupId,
                parentTabId,
                currentUserId.userId(),
                requests
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(responses);
    }
}
