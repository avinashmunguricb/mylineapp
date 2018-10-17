/*
 * Copyright 2016 LINE Corporation
 *
 * LINE Corporation licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.example.bot.spring.echo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.linecorp.bot.model.event.Event;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.spring.boot.annotation.EventMapping;
import com.linecorp.bot.spring.boot.annotation.LineMessageHandler;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.EnumMap;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.Properties;
import java.util.TreeMap;
import java.util.WeakHashMap;

@SpringBootApplication
@LineMessageHandler
public class EchoApplication {
	public static void main(String[] args) {
		SpringApplication.run(EchoApplication.class, args);
	}

	@EventMapping
	public Message handleTextMessageEvent(MessageEvent<TextMessageContent> event) {
		System.out.println("event: " + event);
		StringBuilder stringBuilder = new StringBuilder();
		String messageText = event.getMessage().getText();
		String tagName = " ==>Saahir ";
		stringBuilder.append(messageText);
		stringBuilder.append(tagName);
		
		String originalMessageText = event.getMessage().getText();
		String replyBotMessage = botReplies(originalMessageText);
		final String followedUserId = event.getSource().getUserId();
		
		final LineMessagingClient client = LineMessagingClient
		        .builder("KvPdRVx9Zhye3c74GhvJ2u6HtyUgJFuZSKU22wD8IfodRKhBsw4fdkSey0q/xsa/VuPBrA9shefDEn49yb4xo8Yy6sPF1izTfsgnfmm1aU4hrZgBOQasXMZwHvRdFcvupcGeFxZd1/JeVrWd6V54QwdB04t89/1O/w1cDnyilFU=")
		        .build();
		final UserProfileResponse userProfileResponse;
		try {
		    userProfileResponse = client.getProfile("Uddc14c99497b8f4366b4b01f413084a1").get();
		} catch (InterruptedException | ExecutionException e) {
		    e.printStackTrace();
		}

		System.out.println(userProfileResponse.getUserId());
		System.out.println(userProfileResponse.getDisplayName());
		System.out.println(userProfileResponse.getPictureUrl());
		
		return new TextMessage(userProfileResponse.getDisplayName());
	}

	@EventMapping
	public void handleDefaultMessageEvent(Event event) {
		
		System.out.println("event: " + event);
	}
	
	public String botReplies(String originalMessage) {
		String botReplyMessage = "Command not recognized !\nHere are a list of keywords : \n";
		Map<String, String> mapA = new HashMap<>();
		mapA.put("Hello", "Hello, How can i help you ?");
		mapA.put("Document", "The document is in drawer 5");
		mapA.put("Thank you", "Your welcome");
		
		for(String myKey : mapA.keySet()) {
			botReplyMessage += myKey+"\n";
			if(originalMessage.toLowerCase().contains(myKey.toLowerCase())) {
				botReplyMessage = mapA.get(myKey);
				break;
			}
		}		
		return botReplyMessage;
	}
}
