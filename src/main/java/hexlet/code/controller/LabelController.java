package hexlet.code.controller;

import hexlet.code.dto.LabelDto;
import hexlet.code.exceptions.DeleteException;
import hexlet.code.model.Label;
import hexlet.code.service.LabelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    private final LabelService labelService;

    @Autowired
    public LabelController(LabelService labelService) {
        this.labelService = labelService;
    }

    @Operation(summary = "Get label")
    @GetMapping(ID)
    public Label getLabel(@PathVariable final Long id) {
        return labelService.getLabel(id);
    }

    @Operation(summary = "Get all labels")
    @GetMapping
    public List<Label> getLabels() {
        return labelService.getLabels();
    }

    @Operation(summary = "Create new label")
    @ApiResponses(@ApiResponse(responseCode = "201", description = "Label created"))
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Label createLabel(@RequestBody @Valid final LabelDto labelDto) {
        return labelService.createLabel(labelDto);
    }

    @Operation(summary = "Update label")
    @PutMapping(ID)
    public Label updateLabel(@PathVariable final Long id,
                             @RequestBody @Valid final LabelDto labelDto) {

        return labelService.updateLabel(id, labelDto);
    }

    @Operation(summary = "Delete label")
    @DeleteMapping(ID)
    public void deleteLabel(@PathVariable final Long id) throws DeleteException {
        labelService.deleteLabel(id);
    }

}
