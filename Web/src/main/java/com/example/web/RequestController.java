package com.example.web;

import com.example.RequestService;
import com.example.dto.Approval.ApprovalDTO;
import com.example.dto.Approval.ApprovalRequestDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:4200/")
public class RequestController {

    private final RequestService requestService;


    @GetMapping("requests/approve/{userId}")
    public List<ApprovalDTO> getToApproveRequestsForUser(@PathVariable("userId") int userId) {
        return this.requestService.getToApproveRequestsForUser(userId);
    }
    @PutMapping("requests/answer")
    public void approveRequest(@RequestParam("requestId") int requestId,@RequestParam("approverId") int approverId,@RequestParam("response") String answer) {
        this.requestService.answerRequest(requestId, approverId, answer);
    }
    @GetMapping("requests")
    public List<ApprovalRequestDTO> getMyRequests(@RequestParam("userId") int userId) {
        return this.requestService.getMyRequests(userId);
    }
}
