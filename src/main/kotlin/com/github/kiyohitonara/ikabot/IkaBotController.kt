/*
 * Copyright 2016 Kiyohito Nara
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

import com.linecorp.bot.client.LineMessagingService
import com.linecorp.bot.model.ReplyMessage
import com.linecorp.bot.model.action.URIAction
import com.linecorp.bot.model.event.Event
import com.linecorp.bot.model.event.MessageEvent
import com.linecorp.bot.model.event.message.TextMessageContent
import com.linecorp.bot.model.message.TemplateMessage
import com.linecorp.bot.model.message.TextMessage
import com.linecorp.bot.model.message.template.CarouselColumn
import com.linecorp.bot.model.message.template.CarouselTemplate
import com.linecorp.bot.spring.boot.annotation.EventMapping
import com.linecorp.bot.spring.boot.annotation.LineMessageHandler
import com.squareup.moshi.Moshi
import org.springframework.beans.factory.annotation.Autowired
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.IOException

@LineMessageHandler
open class IkaBotController {
    @Autowired
    lateinit var lineMessagingService: LineMessagingService

    companion object {
        @JvmField val OFFICIAL_URL = "https://splatoon.nintendo.net"
        @JvmField val RULE_REGULAR = "レギュラー"
        @JvmField val RULE_GACHI = "ガチ"
        @JvmField val STAGES: Array<String> = arrayOf("デカライン高架下", "シオノメ油田", "Ｂバスパーク", "ハコフグ倉庫", "アロワナモール", "ホッケふ頭", "モズク農園", "ネギトロ炭鉱", "タチウオパーキング", "モンガラキャンプ場", "ヒラメが丘団地", "マサバ海峡大橋", "キンメダイ美術館", "マヒマヒリゾート＆スパ", "ショッツル鉱山", "アンチョビットゲームズ")
        @JvmField val STAGE_URL = "https://raw.githubusercontent.com/KiyohitoNara/IkaBot-Line/images/map_%03d.jpg"
    }

    @EventMapping
    @Throws(IOException::class)
    fun handleTextMessageEvent(event: MessageEvent<TextMessageContent>) {
        when (event.message.text) {
            RULE_REGULAR -> reply(event.replyToken, getIkaState("regular").result[0])
            RULE_GACHI -> reply(event.replyToken, getIkaState("gachi").result[0])
            else -> reply(event.replyToken)
        }
    }

    @EventMapping
    fun handleDefaultMessageEvent(event: Event) {

    }

    fun getIkaState(rule: String): IkaState {
        val moshi = Moshi.Builder().build()

        val retrofit = Retrofit.Builder()
                .baseUrl("http://splapi.retrorocket.biz")
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()

        val service = retrofit.create(IkaStateService::class.java)

        return service.getResult(rule).execute().body()
    }

    fun reply(token: String) {
        lineMessagingService.replyMessage(ReplyMessage(token, TextMessage("くコ:ミ"))).execute()
    }

    fun reply(token: String, ikaResult: IkaResult) {
        val columns: MutableList<CarouselColumn> = mutableListOf()
        columns.add(CarouselColumn(STAGE_URL.format(STAGES.indexOf(ikaResult.maps[0]) + 1), ikaResult.maps[0], ikaResult.rule, listOf(URIAction("詳細", OFFICIAL_URL))))
        columns.add(CarouselColumn(STAGE_URL.format(STAGES.indexOf(ikaResult.maps[1]) + 1), ikaResult.maps[1], ikaResult.rule, listOf(URIAction("詳細", OFFICIAL_URL))))

        lineMessagingService.replyMessage(ReplyMessage(token, TemplateMessage("ステージ", CarouselTemplate(columns)))).execute()
    }
}