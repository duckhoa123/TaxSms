package com.vnnet.kpi.readpdf.controller;


import com.vnnet.kpi.web.utils.JwtTokenForPdfFile;
import io.jsonwebtoken.Claims;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@Controller
public class MainController implements ErrorController {

    static {

    }

    @RequestMapping(value = { "/view", }, method = RequestMethod.GET)
    public String index(Model model, @RequestParam("p") String token) {
        try {
            Claims claims = JwtTokenForPdfFile.getClaimsFromToken(token);
            if (claims != null && claims.getExpiration().after(new Date())) {
                model.addAttribute("message", "Mã OTP sẽ được gửi đến số điện thoại " + claims.get("calledNumber"));
                model.addAttribute("isdn", claims.get("calledNumber"));
                return "view";
            } else
                return "error";
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "error";
    }

    @RequestMapping("/error")
    @ResponseBody
    public String error() {
        return "Not Found";
    }

}
