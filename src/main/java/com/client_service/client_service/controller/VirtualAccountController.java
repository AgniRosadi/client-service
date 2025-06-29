package com.client_service.client_service.controller;

import com.api.common.dto.PrefixDataRes;
import com.api.common.dto.VirtualAccountReq;
import com.client_service.client_service.service.VirtualAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.api.common.dto.VirtualAccountRes;

@RestController
@RequestMapping(value = "/va")
public class VirtualAccountController {

    @Autowired
    VirtualAccountService virtualAccountService;

    @PostMapping("/check-prefix")
    public PrefixDataRes checkPrefix(@RequestBody VirtualAccountReq request) {
        return virtualAccountService.checkPrefix(request);
    }

}
