
/*
 Copyright 2019-2023 ACSoftware

 Licensed under the Apache License, Version 2.0 (the "License")
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.

 */

package it.water.repository.entity.model.exceptions;

import it.water.core.model.exceptions.WaterRuntimeException;
import lombok.AllArgsConstructor;
import lombok.Getter;


/**
 * @Author Aristide Cittadino.
 * Model class for WaterDuplicateEntityException.
 * It is used to map, in json format, all error messages when tries to
 * persist a new/updated entity that already exists in the database.
 */
@AllArgsConstructor()
public class DuplicateEntityException extends WaterRuntimeException {
    /**
     * A unique serial version identifier
     */
    private static final long serialVersionUID = 1L;

    /**
     * array of {@code String}s unique fields, used to return exception if the value
     * is not unique
     */
    @Getter
    private final String[] uniqueFields;
    
    @Override
    public String getMessage() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < uniqueFields.length; i++) {
            if (i > 0)
                sb.append(",");
            sb.append(this.uniqueFields[i]);
        }
        return sb.toString();
    }

}
