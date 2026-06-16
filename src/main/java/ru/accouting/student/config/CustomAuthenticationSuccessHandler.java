package ru.accouting.student.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import ru.accouting.student.model.Student;
import ru.accouting.student.model.User;
import ru.accouting.student.repository.StudentRepository;

import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final StudentRepository studentRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        // Редирект только для студентов
        if (authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("STUDENT"))) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof User user) {
                Long userId = user.getIdUser();
                Optional<Student> studentOpt = studentRepository.findByIdUser(userId);
                if (studentOpt.isPresent()) {
                    String targetUrl = "/students/" + studentOpt.get().getIdStudent() + "/portfolio";
                    getRedirectStrategy().sendRedirect(request, response, targetUrl);
                    return;
                }
            }
        }
        super.onAuthenticationSuccess(request, response, authentication);
    }
}
