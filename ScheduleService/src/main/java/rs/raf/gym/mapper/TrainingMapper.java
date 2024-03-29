/*
 * Copyright (C) 2023. Lazar Dobrota and Nemanja Radovanovic
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package rs.raf.gym.mapper;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import rs.raf.gym.commons.dto.training.TrainingCreateDto;
import rs.raf.gym.commons.dto.training.TrainingDto;
import rs.raf.gym.commons.dto.training.TrainingUpdateDto;
import rs.raf.gym.model.Training;

@Component
@AllArgsConstructor
public class TrainingMapper {

    private final TrainingTypeMapper trainingTypeMapper;

    /**
     * Maps Training to TrainingDto object.
     * @param training training
     * @return TrainingDto object
     */
    public TrainingDto toTrainingDto(Training training) {
        return new TrainingDto(training.getName(),
                               trainingTypeMapper.toTrainingTypeDto(training.getType()),
                               training.getDescription(),
                               training.getLoyalty());
    }

    /**
     * Maps TrainingCreateDto to existing Training object.
     * @param training training
     * @param createDto create dto
     * @return Training object
     */
    public Training map(Training training, TrainingCreateDto createDto) {
        training.setName(createDto.getName());
        training.setDescription(createDto.getDescription());
        training.setLoyalty(createDto.getLoyalty());

        return training;
    }

    /**
     * Maps TrainingUpdateDto to existing Training object.
     * @param training training
     * @param updateDto update dto
     * @return Training object
     */
    public Training map(Training training, TrainingUpdateDto updateDto) {
        training.setName(updateDto.getName());
        training.setDescription(updateDto.getDescription());
        training.setLoyalty(updateDto.getLoyalty());

        return training;
    }

}
