package com.udacity.vehicles.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import springfox.documentation.annotations.ApiIgnore;

@Controller
@ApiIgnore
public class SwaggerController {

    @RequestMapping("/swagger-ui.html")
    public String redirectToNewSwagger() {
        return "redirect:/swagger-ui/";
    }
}
