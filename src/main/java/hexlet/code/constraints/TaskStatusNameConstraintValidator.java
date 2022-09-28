//package hexlet.code.constraints;
//
//import javax.validation.ConstraintValidator;
//import javax.validation.ConstraintValidatorContext;
//import java.util.List;
//
//public class TaskStatusNameConstraintValidator implements ConstraintValidator<TaskStatusNameConstraint, String> {
//
//    private static final List<String> STATUS_NAMES = List.of("Новый", "В работе", "На тестировании", "Завершен");
//
//    @Override
//    public boolean isValid(String taskStatusName, ConstraintValidatorContext context) {
//        return STATUS_NAMES.stream()
//                .anyMatch(taskStatusName::equals);
//    }
//}
