package rs.raf.gym.specification;

import rs.raf.gym.model.Training;

public class TrainingSpecification extends BaseSpecification<Training> {

    public TrainingSpecification(String name, String type, Integer loyalty) {
        addNameSpecification(name);
        addTypeSpecification(type);
        addLoyaltySpecification(loyalty);
    }

    private void addNameSpecification(String name) {
        if (name == null)
            return;

        specifications.add((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(Training.name()), name));
    }

    private void addTypeSpecification(String type) {
        if (type == null)
            return;

        specifications.add((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.join(Training.trainingType())
                                                                                       .get(Training.name()),
                                                                                   type));
    }

    private void addLoyaltySpecification(Integer loyalty) {
        if (loyalty == null)
            return;

        specifications.add((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(Training.loyalty()),
                                                                                   loyalty));
    }

}