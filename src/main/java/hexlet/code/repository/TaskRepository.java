package hexlet.code.repository;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.SimpleExpression;
import hexlet.code.model.QTask;
import hexlet.code.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long>,
        QuerydslPredicateExecutor<Task>,
        QuerydslBinderCustomizer<QTask> {

    List<Task> findAll(Predicate predicate);

    //for custom task filtration
    @Override
    default void customize(QuerydslBindings bindings, QTask task) {

        bindings.bind(task.taskStatus.id).first(SimpleExpression::eq);
        bindings.bind(task.executor.id).first(SimpleExpression::eq);
        bindings.bind(task.labels.any().id).first((SimpleExpression::eq));
        bindings.bind(task.author.id).first(SimpleExpression::eq);
    }
}
