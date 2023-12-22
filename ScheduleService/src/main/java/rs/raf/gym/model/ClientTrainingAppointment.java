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

package rs.raf.gym.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "client_training_appointment")
public class ClientTrainingAppointment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "date",        nullable = false),
            @JoinColumn(name = "gym_id",      nullable = false),
            @JoinColumn(name = "training_id", nullable = false),
            @JoinColumn(name = "time",        nullable = false)
    })
    private TrainingAppointment trainingAppointment;

    @Column(nullable = false)
    private Long clientId;

    @ManyToOne
    @JoinColumn(nullable = false)
    private ClientAppointmentStatus status;

    /**
     * Returns the training appointment field identifier.
     * @return training appointment identifier
     */
    public static String trainingAppointment() {
        return "trainingAppointment";
    }

    /**
     * Returns the client field identifier.
     * @return client identifier
     */
    public static String client() {
        return "clientId";
    }

    /**
     * Returns the status field identifier.
     * @return status identifier
     */
    public static String status() {
        return "status";
    }

}
