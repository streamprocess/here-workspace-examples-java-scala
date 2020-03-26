/*
 * Copyright (C) 2017-2020 HERE Europe B.V.
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
 *
 * SPDX-License-Identifier: Apache-2.0
 * License-Filename: LICENSE
 */

package com.here.platform.data.validation.example.quickstart.assessment.java;

import java.io.Serializable;

public class AssessmentConfig implements Serializable {
  private static final long serialVersionUID = -1820734495904295984L;
  private double criticalThreshold;

  public double getCriticalThreshold() {
    return criticalThreshold;
  }

  public void setCriticalThreshold(double value) {
    criticalThreshold = value;
  }
}