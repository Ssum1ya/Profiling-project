package app.project_profile.common.exception;

import app.project_profile.api.MessageController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice(assignableTypes = {MessageController.class})
@Slf4j
public class ControllerExceptionHandler {

    @ExceptionHandler(ProcessedValueNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ModelAndView handleProcessedValueNotFoundException(
            ProcessedValueNotFoundException ex
    ) {
        var message = "Processed value not found";
        log.warn("Processed value not found: uuid = {}", ex.getUuid());

        ModelAndView mav = new ModelAndView("processed-value-not-found");
        mav.addObject("message", message);
        mav.addObject("uuid", ex.getUuid());

        return mav;
    }
}
