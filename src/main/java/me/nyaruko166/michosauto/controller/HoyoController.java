package me.nyaruko166.michosauto.controller;

import me.nyaruko166.michosauto.request.AccountRequest;
import me.nyaruko166.michosauto.service.HoyoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/account")
public class HoyoController {

    @Autowired
    private HoyoService hoyoService;

    @PostMapping("/add")
    public ResponseEntity<?> addAccount(@Validated @RequestBody AccountRequest accountRequest) {
        hoyoService.addAccount(accountRequest);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
