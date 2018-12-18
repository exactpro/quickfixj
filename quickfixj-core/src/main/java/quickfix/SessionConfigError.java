/******************************************************************************
 * Copyright 2009-2018 Exactpro (Exactpro Systems Limited)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
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
 ******************************************************************************/
package quickfix;

/**
 * This exception is thrown when a session configuration error is detected.
 *
 * @author nikita.smirnov
 */
public class SessionConfigError extends ConfigError{

    public SessionConfigError() {
        super();
    }

    public SessionConfigError(String sessionField, String message) {
        super(message);
        this.sessionField = sessionField;
    }

    public SessionConfigError(String sessionField, Throwable cause) {
        super(cause);
        this.sessionField = sessionField;
    }

    public SessionConfigError(String sessionField, String string, Throwable e) {
        super(string, e);
        this.sessionField = sessionField;
    }

    public String getSessionField(){
        return sessionField;
    }

    private String sessionField;
}
