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
package it.water.repository.service.javax;

import it.water.core.model.exceptions.ValidationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class RepositoryServiceJavaxTest {

    @Test
    void testSuccess(){
        TestEntity t = new TestEntity();
        t.setId(1);
        t.setEntityField("prova");
        TestEntitySystemServiceImpl api = new TestEntitySystemServiceImpl();
        Assertions.assertDoesNotThrow(() -> api.validate(t));
    }

    @Test
    void testFail(){
        TestEntity t = new TestEntity();
        t.setId(1);
        TestEntitySystemServiceImpl api = new TestEntitySystemServiceImpl();
        Assertions.assertThrows(ValidationException.class,() -> api.validate(t));
    }
}
