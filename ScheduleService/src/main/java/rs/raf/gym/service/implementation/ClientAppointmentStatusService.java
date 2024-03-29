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
import rs.raf.gym.commons.dto.client_appointment_status.ClientAppointmentStatusCreateDto;
import rs.raf.gym.commons.dto.client_appointment_status.ClientAppointmentStatusDto;
import rs.raf.gym.commons.dto.client_appointment_status.ClientAppointmentStatusUpdateDto;
import rs.raf.gym.commons.exception.GymException;
import rs.raf.gym.exception.ExceptionType;
import rs.raf.gym.mapper.ClientAppointmentStatusMapper;
import rs.raf.gym.model.ClientAppointmentStatus;
import rs.raf.gym.repository.IClientAppointmentStatusRepository;
import rs.raf.gym.service.IClientAppointmentStatusService;
import rs.raf.gym.specification.ClientAppointmentStatusSpecification;

import java.util.List;

@Service
@AllArgsConstructor
public class ClientAppointmentStatusService implements IClientAppointmentStatusService {

    private final IClientAppointmentStatusRepository repository;
    private final ClientAppointmentStatusMapper      mapper;

    @Override
    public List<ClientAppointmentStatusDto> getAll() {
        return repository.findAll()
                         .stream()
                         .map(mapper::toClientAppointmentStatusDto)
                         .toList();
    }

    @Override
    public Page<ClientAppointmentStatusDto> findAll(String name, Pageable pageable) {
        ClientAppointmentStatusSpecification specification = new ClientAppointmentStatusSpecification(name);

        return repository.findAll(specification.filter(), pageable)
                         .map(mapper::toClientAppointmentStatusDto);
    }

    @Override
    public ClientAppointmentStatusDto create(ClientAppointmentStatusCreateDto createDto) {
        ClientAppointmentStatus appointmentStatus = new ClientAppointmentStatus();

        mapper.map(appointmentStatus, createDto);

        return mapper.toClientAppointmentStatusDto(repository.save(appointmentStatus));
    }

    @Override
    public ClientAppointmentStatusDto update(ClientAppointmentStatusUpdateDto updateDto) throws GymException {
        ClientAppointmentStatus appointmentStatus = repository.findByName(updateDto.getOldName())
                                                              .orElseThrow(() -> new GymException(ExceptionType.UPDATE_CLIENT_APPOINTMENT_STATUS_NOT_FOUND_CLIENT_APPOINTMENT_STATUS,
                                                                                                  updateDto.getOldName()));

        mapper.map(appointmentStatus, updateDto);

        return mapper.toClientAppointmentStatusDto(repository.save(appointmentStatus));
    }

}
