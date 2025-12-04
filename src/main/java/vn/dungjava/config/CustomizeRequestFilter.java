package vn.dungjava.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import vn.dungjava.common.TokenType;
import vn.dungjava.service.JwtService;
import vn.dungjava.service.UserServiceDetail;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

@Component
@Slf4j(topic = "CUSTOMIZE-REQUEST-FILTER")
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class CustomizeRequestFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserServiceDetail userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("{} {}", request.getMethod(), request.getRequestURI());

        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            header = header.substring(7);
            log.info("Bearer {}", header.substring(0, 20));

            String username = "";
            try {
                username = jwtService.extractUsername(header, TokenType.ACCESS_TOKEN);
                log.info("Username {}", username);
            } catch (AccessDeniedException e) {
                log.error("Access denied, message: {}", e.getMessage());
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write(ErrorResponse(e.getMessage()));
                return;
            }

            UserDetails userDetails = userDetailsService.UserServiceDetail().loadUserByUsername(username);

            SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            securityContext.setAuthentication(authenticationToken);
            SecurityContextHolder.setContext(securityContext);
            filterChain.doFilter(request, response);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String ErrorResponse(String message) {
        try {
            ErrorResponse errorResponse = new ErrorResponse();
            errorResponse.setTimestamp(new Date());
            errorResponse.setError("Forbidden");
            errorResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
            errorResponse.setMessage(message);

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            return gson.toJson(errorResponse);
        } catch (Exception e) {
            return ""; //return empty string if serialization failed
        }
    }

    @Getter
    @Setter
    private class ErrorResponse {
        private Date timestamp;
        private int status;
        private String error;
        private String message;
    }
}
