
/*
 * Copyright 2024 Aristide Cittadino
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package it.water.repository.entity.model;

import com.fasterxml.jackson.annotation.JsonView;
import it.water.core.api.model.BaseEntity;
import it.water.core.api.model.PaginableResult;
import it.water.core.api.service.rest.WaterJsonView;
import lombok.Getter;

import java.util.Collection;


/**
 * @param <T> entity which extends WaterBaseEntity
 * @Author Aristide Cittadino.
 * Class used to return paginated results on query.
 */
@JsonView({WaterJsonView.Extended.class,WaterJsonView.Compact.class,WaterJsonView.Internal.class,WaterJsonView.Privacy.class,WaterJsonView.Public.class,WaterJsonView.Secured.class})
public class PaginatedResult<T extends BaseEntity>
        implements PaginableResult<T> {
    /**
     * Num pages
     */
    @Getter
    private final int numPages;
    /**
     * Current page
     */
    @Getter
    private final int currentPage;
    /**
     * Next page
     */
    @Getter
    private final int nextPage;
    /**
     * Num of items per page
     */
    @Getter
    private final int delta;
    /**
     * Query results
     */
    @Getter
    private final Collection<T> results;

    public PaginatedResult(int numPages, int currentPage, int nextPage, int delta,
                           Collection<T> results) {
        super();
        this.numPages = numPages;
        this.currentPage = currentPage;
        this.nextPage = nextPage;
        this.delta = delta;
        this.results = results;
    }

}
