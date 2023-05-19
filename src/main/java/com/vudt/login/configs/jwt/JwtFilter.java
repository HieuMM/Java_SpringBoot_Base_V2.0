package com.vudt.login.configs.jwt;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;
import com.vudt.login.dtos.ErrorResponseDto;
import com.vudt.login.services.IUserService;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtFilter extends OncePerRequestFilter {
    private final Logger log = LoggerFactory.getLogger(JwtFilter.class);
    private final IUserService userService;

    public JwtFilter(IUserService userService) {
        this.userService = userService;
    }


    //Filter jw token
    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain filterChain) throws ServletException, IOException {
        log.info("JwtFilter is checking");
        //If request method is options, do filter
        if (req.getMethod().equalsIgnoreCase("OPTIONS") || req.getServletPath().equals("/socket")) {
            filterChain.doFilter(req, res);
        }
        //if not, checking token and do filter afterward
        else {
            String token = req.getHeader("Authorization");
            System.out.println("token: " + token);
            if (token == null) {
                // token null
                res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                res.getWriter().println(new JSONObject(ErrorResponseDto.ofError(3, "Token is null")));
            } else {
                if (this.userService.tokenFilter(token.substring(7), req, res))
                    filterChain.doFilter(req, res);
                else {
                    // token not valid
                    res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    res.getWriter().println(new JSONObject(ErrorResponseDto.ofError(3, "Token is not valid")));
                }
            }
        }

    }
}
