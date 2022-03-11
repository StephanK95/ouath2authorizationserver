package com.klamann.ouath2authorizationserver;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/")
public class TestCallbackController {

    @GetMapping(
            path = "callback"
    ) public String callbackReturn() {
        return "callback";
    }
}
