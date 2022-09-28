//package hexlet.code.constraints;
//
//import javax.validation.Constraint;
//import javax.validation.Payload;
//import java.lang.annotation.ElementType;
//import java.lang.annotation.Retention;
//import java.lang.annotation.RetentionPolicy;
//import java.lang.annotation.Target;
//
//@Target(ElementType.FIELD)
//@Retention(RetentionPolicy.RUNTIME)
//@Constraint(validatedBy = TaskStatusNameConstraintValidator.class)
//public @interface TaskStatusNameConstraint {
//
//    String message() default "Status must be \"Новый\", \"В работе\", \"На тестировании\", \"Завершен\"";
//
//    Class<?>[] groups() default {};
//
//    Class<? extends Payload>[] payload() default {};
//}
