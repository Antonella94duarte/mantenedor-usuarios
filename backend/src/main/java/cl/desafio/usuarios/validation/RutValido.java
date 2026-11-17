package cl.desafio.usuarios.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Valida que el dígito verificador corresponda al RUT (módulo 11). */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = RutValidoValidator.class)
public @interface RutValido {

    String message() default "El dígito verificador no corresponde al RUT";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
