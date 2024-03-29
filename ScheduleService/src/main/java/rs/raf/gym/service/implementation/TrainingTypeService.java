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

package rs.raf.gym.service.implementation;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import rs.raf.gym.commons.dto.training_type.TrainingTypeCreateDto;
import rs.raf.gym.commons.dto.training_type.TrainingTypeDto;
import rs.raf.gym.commons.dto.training_type.TrainingTypeUpdateDto;
import rs.raf.gym.commons.exception.GymException;
import rs.raf.gym.exception.ExceptionType;
import rs.raf.gym.mapper.TrainingTypeMapper;
import rs.raf.gym.model.TrainingType;
import rs.raf.gym.repository.ITrainingTypeRepository;
import rs.raf.gym.service.ITrainingTypeService;
import rs.raf.gym.specification.TrainingTypeSpecification;

import java.util.List;

@Service
@AllArgsConstructor
public class TrainingTypeService implements ITrainingTypeService {

    private final ITrainingTypeRepository repository;
    private final TrainingTypeMapper      mapper;

    @Override
    public List<TrainingTypeDto> getAll() {
        return repository.findAll()
                         .stream()
                         .map(mapper::toTrainingTypeDto)
                         .toList();
    }

    @Override
    public Page<TrainingTypeDto> findAll(String name, Pageable pageable) {
        TrainingTypeSpecification specification = new TrainingTypeSpecification(name);

        return repository.findAll(specification.filter(), pageable)
                         .map(mapper::toTrainingTypeDto);
    }

    @Override
    public TrainingTypeDto create(TrainingTypeCreateDto createDto) {
        TrainingType trainingType = new TrainingType();

        mapper.map(trainingType, createDto);

        return mapper.toTrainingTypeDto(repository.save(trainingType));
    }

    @Override
    public TrainingTypeDto update(TrainingTypeUpdateDto updateDto) throws GymException {
        TrainingType trainingType = repository.findByName(updateDto.getOldName())
                                              .orElseThrow(() -> new GymException(ExceptionType.UPDATE_TRAINING_TYPE_NOT_FOUND_TRAINING_TYPE,
                                                                                  updateDto.getOldName()));

        mapper.map(trainingType, updateDto);

        return mapper.toTrainingTypeDto(repository.save(trainingType));
    }

}
