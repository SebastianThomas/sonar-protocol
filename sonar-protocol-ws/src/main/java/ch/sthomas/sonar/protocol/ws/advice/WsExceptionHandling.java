package ch.sthomas.sonar.protocol.ws.advice;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.zalando.problem.spring.web.advice.ProblemHandling;
import org.zalando.problem.spring.web.advice.security.SecurityAdviceTrait;

@ControllerAdvice
public class WsExceptionHandling implements ProblemHandling, SecurityAdviceTrait {}
