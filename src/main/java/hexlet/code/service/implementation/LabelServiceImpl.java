package hexlet.code.service.implementation;

import hexlet.code.dto.LabelDto;
import hexlet.code.exceptions.DeleteException;
import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;
import hexlet.code.service.LabelService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
@AllArgsConstructor
public class LabelServiceImpl implements LabelService {

    private final LabelRepository labelRepository;

    @Override
    public Label getLabel(final Long id) {
        return labelRepository.findById(id).get();
    }

    @Override
    public List<Label> getLabels() {
        return labelRepository.findAll();
    }

    @Override
    public Label createLabel(final LabelDto labelDto) {
        final Label label = new Label();
        label.setName(labelDto.getName());
        return labelRepository.save(label);
    }

    @Override
    public Label updateLabel(final Long id, final LabelDto labelDto) {
        final Label label = labelRepository.findById(id).get();
        label.setName(labelDto.getName());
        return labelRepository.save(label);
    }

    @Override
    public void deleteLabel(final Long id) throws DeleteException {
        final Label label = labelRepository.findById(id).get();

        if (!CollectionUtils.isEmpty(label.getTasks())) {
            throw new DeleteException("Label is used with tasks, firstly delete tasks");
        }

        labelRepository.delete(label);
    }
}
