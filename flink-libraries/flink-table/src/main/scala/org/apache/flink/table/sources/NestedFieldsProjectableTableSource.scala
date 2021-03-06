/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.flink.table.sources

import org.apache.flink.api.java.DataSet
import org.apache.flink.streaming.api.datastream.DataStream

/**
  * Adds support for projection push-down to a [[TableSource]] with nested fields.
  * A [[TableSource]] extending this interface is able to project the fields of the returned
  * [[DataSet]] if it is a [[BatchTableSource]] or [[DataStream]] if it is a [[StreamTableSource]].
  *
  * @tparam T The return type of the [[TableSource]].
  */
trait NestedFieldsProjectableTableSource[T] {

  /**
    * Creates a copy of the [[TableSource]] that projects its output to the given field indexes.
    * The field indexes relate to the physical return type ([[TableSource.getReturnType]]) and not
    * to the table schema ([[TableSource.getTableSchema]] of the [[TableSource]].
    *
    * The table schema ([[TableSource.getTableSchema]] of the [[TableSource]] copy must not be
    * modified by this method, but only the return type ([[TableSource.getReturnType]]) and the
    * produced [[DataSet]] ([[BatchTableSource.getDataSet(]]) or [[DataStream]]
    * ([[StreamTableSource.getDataStream]]). The return type may only be changed by
    * removing or reordering first level fields. The type of the first level fields must not be
    * changed.
    *
    * If the [[TableSource]] implements the [[DefinedFieldMapping]] interface, it might
    * be necessary to adjust the mapping as well.
    *
    * The nestedFields parameter contains all nested fields that are accessed by the query.
    * This information can be used to only read and set the accessed fields.
    * Non-accessed fields may be left empty, set to null, or to a default value.
    *
    * The [[projectNestedFields()]] method is called with parameters as shown in the example below:
    *
    * // schema
    * tableSchema = {
    *       id,
    *       student<\school<\city, tuition>, age, name>,
    *       teacher<\age, name>
    *       }
    *
    * // query
    * select (id, student.school.city, student.age, teacher)
    *
    * // parameters
    * fields = field = [0, 1, 2]
    * nestedFields  \[\["*"], ["school.city", "age"], ["*"\]\]
    *
    * IMPORTANT: This method must return a true copy and must not modify the original table source
    * object.
    *
    * @param fields The indexes of the fields to return.
    * @param nestedFields The paths of all nested fields which are accessed by the query. All other
    *                     nested fields may be empty.
    * @return A copy of the [[TableSource]] that projects its output.
    */
  def projectNestedFields(
      fields: Array[Int],
      nestedFields: Array[Array[String]]): TableSource[T]

}
