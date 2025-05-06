package ch.sthomas.sonar.protocol.ws.advice;

import ch.sthomas.sonar.protocol.model.exception.GameException;
import ch.sthomas.sonar.protocol.model.exception.GameNotFoundException;
import ch.sthomas.sonar.protocol.model.exception.PlayerNotFoundException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.NativeWebRequest;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;
import org.zalando.problem.spring.web.advice.AdviceTrait;

public interface GameExceptionHandling extends AdviceTrait {
    @ExceptionHandler
    default ResponseEntity<Problem> handleGameException(
            final GameNotFoundException exception, final NativeWebRequest request) {
        return create(Status.NOT_FOUND, exception, request);
    }

    @ExceptionHandler
    default ResponseEntity<Problem> handleGameException(
            final PlayerNotFoundException exception, final NativeWebRequest request) {
        return create(Status.NOT_FOUND, exception, request);
    }

    @ExceptionHandler
    default ResponseEntity<Problem> handleGameException(
            final GameException exception, final NativeWebRequest request) {
        return create(Status.BAD_GATEWAY, exception, request);
    }
}
