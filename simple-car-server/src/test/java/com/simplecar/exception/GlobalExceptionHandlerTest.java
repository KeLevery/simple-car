package com.simplecar.exception;

import com.simplecar.result.ApiResponse;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void mapsBusinessExceptionCodeAndMessage() {
        ApiResponse<Void> response = handler.handleBusiness(new BusinessException(401, "用户名或密码错误"));

        assertEquals(401, response.getCode());
        assertEquals("用户名或密码错误", response.getMsg());
    }

    @Test
    void mapsValidationExceptionToFirstFieldMessage() throws NoSuchMethodException {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "loginRequest");
        bindingResult.addError(new FieldError("loginRequest", "username", "用户名不能为空"));
        MethodParameter parameter = new MethodParameter(
                GlobalExceptionHandlerTest.class.getDeclaredMethod("dummy", String.class), 0);
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(parameter, bindingResult);

        ApiResponse<Void> response = handler.handleValidation(exception);

        assertEquals(400, response.getCode());
        assertEquals("用户名不能为空", response.getMsg());
    }

    @Test
    void hidesUnexpectedExceptionDetails() {
        ApiResponse<Void> response = handler.handleException(new Exception("secret stack"));

        assertEquals(500, response.getCode());
        assertEquals("系统异常，请稍后重试", response.getMsg());
    }

    @SuppressWarnings("unused")
    private void dummy(String value) {
    }
}
