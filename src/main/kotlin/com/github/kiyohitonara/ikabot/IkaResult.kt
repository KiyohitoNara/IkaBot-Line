/*
 * Copyright 2017 Kiyohito Nara
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.github.kiyohitonara.ikabot

import com.squareup.moshi.Json

data class IkaResult(
        val rule: String,
        @Json(name = "rule_ex") val ruleDetail: IkaDetail,
        val maps: List<String> = listOf(),
        @Json(name = "maps_ex") val mapsDetail: List<IkaDetail> = listOf(),
        val start: String,
        val end: String)