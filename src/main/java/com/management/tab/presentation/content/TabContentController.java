package com.management.tab.presentation.content;

import com.management.tab.application.TabContentService;
import com.management.tab.domain.content.TabContent;
import com.management.tab.domain.content.vo.TabContentId;
import com.management.tab.domain.tab.vo.TabId;
import com.management.tab.presentation.common.ResponseVoidConst;
import com.management.tab.presentation.content.dto.request.CreateTabContentRequest;
import com.management.tab.presentation.content.dto.request.UpdateTabContentRequest;
import com.management.tab.presentation.content.dto.response.CreateTabContentResponse;
import com.management.tab.presentation.content.dto.response.TabContentCollectionResponse;
import com.management.tab.presentation.content.dto.response.TabContentResponse;
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
@RequestMapping("/api")
@RequiredArgsConstructor
public class TabContentController {

    private final TabContentService tabContentService;

    @GetMapping("/tabs/{tabId}/contents")
    public ResponseEntity<TabContentCollectionResponse> getAllContentsByTabId(@PathVariable Long tabId) {
        List<TabContent> tabContents = tabContentService.getAllContentsByTabId(TabId.create(tabId));
        TabContentCollectionResponse response = TabContentCollectionResponse.from(tabContents);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/contents/{id}")
    public ResponseEntity<TabContentResponse> getContent(@PathVariable Long id) {
        TabContent tabContent = tabContentService.getContent(id);
        TabContentResponse response = TabContentResponse.from(tabContent);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/tabs/{tabId}/contents")
    public ResponseEntity<CreateTabContentResponse> createContent(
            @PathVariable Long tabId,
            @RequestBody CreateTabContentRequest request
    ) {
        TabContentId tabContentId = tabContentService.createContent(TabId.create(tabId), request.content());
        CreateTabContentResponse response = new CreateTabContentResponse(tabContentId.getValue());

        return ResponseEntity.ok(response);
    }

    @PutMapping("/contents/{id}")
    public ResponseEntity<Void> updateContent(
            @PathVariable Long id,
            @RequestBody UpdateTabContentRequest request
    ) {
        tabContentService.updateContent(id, request.content());
        return ResponseVoidConst.OK;
    }

    @DeleteMapping("/contents/{id}")
    public ResponseEntity<Void> deleteContent(@PathVariable Long id) {
        tabContentService.delete(id);
        return ResponseVoidConst.OK;
    }

    @DeleteMapping("/tabs/{tabId}/contents")
    public ResponseEntity<Void> deleteAllContentsByTabId(@PathVariable Long tabId) {
        tabContentService.deleteAllByTabId(TabId.create(tabId));
        return ResponseVoidConst.OK;
    }
}
