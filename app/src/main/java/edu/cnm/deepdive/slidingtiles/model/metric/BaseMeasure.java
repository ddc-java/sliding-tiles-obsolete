/*
 *  Copyright 2020 Deep Dive Coding/CNM Ingenuity, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package edu.cnm.deepdive.slidingtiles.model.metric;

import edu.cnm.deepdive.slidingtiles.model.Puzzle;

/**
 * TODO Write Javadoc comment.
 *
 * @author Nicholas Bennett, Chris Hughes
 */
public abstract class BaseMeasure implements Measure {

  /**
   * TODO Write Javadoc comment.
   *
   * @param p1
   * @param p2
   * @return
   */
  @Override
  public int compare(Puzzle p1, Puzzle p2) {
    return Integer.compare(getMeasure(p1), getMeasure(p2));
  }

}
