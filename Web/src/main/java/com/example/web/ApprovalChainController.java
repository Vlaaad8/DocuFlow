package com.example.web;

import com.example.ApprovalChainService;
import com.example.dto.ChainRequest;
import com.example.login.Role;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:4200/")
public class ApprovalChainController {

    private final ApprovalChainService approvalChainService;

    @GetMapping("approvalChain/roles")
    public Role[] getRoles(){
        return this.approvalChainService.getRoles();
    }

    @PostMapping("approvalChain")
    public void createApprovalChain(@RequestBody ChainRequest chain){
        this.approvalChainService.createApprovalChain(chain);

    }
}
