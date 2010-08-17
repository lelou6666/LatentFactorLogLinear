/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.mahout.classifier.sgd;

import com.google.common.collect.ImmutableMap;
import org.apache.mahout.math.DenseVector;
import org.apache.mahout.math.Vector;
import org.apache.mahout.vectors.Dictionary;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CsvRecordFactoryTest {
  @Test
  public void testAddToVector() {
    CsvRecordFactory csv = new CsvRecordFactory("y", ImmutableMap.of("x1", "n", "x2", "w", "x3", "t"));
    csv.firstLine("z,x1,y,x2,x3,q");
    csv.maxTargetValue(2);

    Vector v = new DenseVector(2000);
    int t = csv.processLine("ignore,3.1,yes,tiger, \"this is text\",ignore", v);
    assertEquals(0, t);
    // should have 9 values set
    assertEquals(9.0, v.norm(0), 0);
    // all should be = 1 except for the 3.1
    assertEquals(3.1, v.maxValue(), 0);
    v.set(v.maxValueIndex(), 0);
    assertEquals(8.0, v.norm(0), 0);
    assertEquals(8.0, v.norm(1), 0);
    assertEquals(1.0, v.maxValue(), 0);

    v.assign(0);
    t = csv.processLine("ignore,5.3,no,line, \"and more text and more\",ignore", v);
    assertEquals(1, t);

    // should have 9 values set
    assertEquals(9.0, v.norm(0), 0);
    // all should be = 1 except for the 3.1
    assertEquals(5.3, v.maxValue(), 0);
    v.set(v.maxValueIndex(), 0);
    assertEquals(8.0, v.norm(0), 0);
    assertEquals(12.0, v.norm(1), 0);
    assertEquals(2, v.maxValue(), 0);

    v.assign(0);
    t = csv.processLine("ignore,5.3,invalid,line, \"and more text and more\",ignore", v);
    assertEquals(1, t);

    // should have 9 values set
    assertEquals(9.0, v.norm(0), 0);
    // all should be = 1 except for the 3.1
    assertEquals(5.3, v.maxValue(), 0);
    v.set(v.maxValueIndex(), 0);
    assertEquals(8.0, v.norm(0), 0);
    assertEquals(12.0, v.norm(1), 0);
    assertEquals(2, v.maxValue(), 0);
  }

  @Test
  public void testDictionaryOrder() {
    Dictionary dict = new Dictionary();

    dict.intern("a");
    dict.intern("d");
    dict.intern("c");
    dict.intern("b");
    dict.intern("qrz");

    Assert.assertEquals("[a, d, c, b, qrz]", dict.values().toString());

    Dictionary dict2 = Dictionary.fromList(dict.values());
    Assert.assertEquals("[a, d, c, b, qrz]", dict2.values().toString());

  }
}
