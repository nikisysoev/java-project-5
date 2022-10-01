package hexlet.code.controller;

import hexlet.code.dto.LabelDto;
import hexlet.code.exceptions.DeleteException;
import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.Valid;
import java.util.List;
import static hexlet.code.controller.LabelController.LABEL_CONTROLLER;
import static hexlet.code.controller.UserController.ID;

@RestController
@RequestMapping("${base-url}" + LABEL_CONTROLLER)
public class LabelController {

    public static final String LABEL_CONTROLLER = "/labels";
    private final LabelRepository labelRepository;

    @Autowired
    public LabelController(final LabelRepository labelRepository) {
        this.labelRepository = labelRepository;
    }

    @Operation(summary = "Get label")
    @GetMapping(ID)
    public Label getLabel(@PathVariable final Long id) {
        return labelRepository.findById(id).get();
    }

    @Operation(summary = "Get all labels")
    @GetMapping
    public List<Label> getLabels() {
        return labelRepository.findAll();
    }

    @Operation(summary = "Create new label")
    @ApiResponses(@ApiResponse(responseCode = "201", description = "Label created"))
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Label createLabel(@RequestBody @Valid final LabelDto labelDto) {
        final Label label = new Label();
        label.setName(labelDto.getName());
        return labelRepository.save(label);
    }

    @Operation(summary = "Update label")
    @PutMapping(ID)
    public Label updateLabel(@PathVariable final Long id,
                             @RequestBody @Valid final LabelDto labelDto) {

        final Label label = labelRepository.findById(id).get();
        label.setName(labelDto.getName());
        return labelRepository.save(label);
    }

    @Operation(summary = "Delete label")
    @DeleteMapping(ID)
    public void deleteLabel(@PathVariable final Long id) throws DeleteException {
        final Label label = labelRepository.findById(id).get();

        if (!CollectionUtils.isEmpty(label.getTasks())) {
            throw new DeleteException("Label is used with tasks, firstly delete tasks");
        }

        labelRepository.delete(label);
    }
}
