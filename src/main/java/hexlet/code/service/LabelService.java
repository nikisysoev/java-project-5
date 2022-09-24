package hexlet.code.service;

import hexlet.code.dto.LabelDto;
import hexlet.code.exceptions.DeleteException;
import hexlet.code.model.Label;

import java.util.List;

public interface LabelService {

    Label getLabel(Long id);

    List<Label> getLabels();

    Label createLabel(LabelDto labelDto);

    Label updateLabel(Long id, LabelDto labelDto);

    void deleteLabel(Long id) throws DeleteException;
}
