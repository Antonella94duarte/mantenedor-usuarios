package cl.desafio.usuarios.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class RutValidoValidator implements ConstraintValidator<RutValido, ConRut> {

    @Override
    public boolean isValid(ConRut value, ConstraintValidatorContext context) {
        // Los campos ausentes los reportan @NotNull / @NotBlank; aquí solo se valida la combinación.
        if (value == null || value.rut() == null || value.dv() == null || value.dv().isBlank()) {
            return true;
        }
        if (Rut.esValido(value.rut(), value.dv())) {
            return true;
        }
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                .addPropertyNode("dv")
                .addConstraintViolation();
        return false;
    }
}
