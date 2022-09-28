package hexlet.code.dto;

//import hexlet.code.constraints.TaskStatusNameConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskStatusDto {

    @NotBlank
    //@TaskStatusNameConstraint
    private String name;
}
