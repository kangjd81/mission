/*
 * Copyright 2012-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package sample.web.ui.mvc;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class CheckController {
	private Log log = LogFactory.getLog(this.getClass().getName());
	
	private String appID;
	
	@PostConstruct
    public void id() {
		String SOURCES = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";
		appID = generateString(new Random(), SOURCES, 15);
    }

	@Value("${app.version:1.0}")
    private String version;
	
	@RequestMapping(value="/health")
    @ResponseBody
	public Map<String, String> health() {
		
		Map<String, String> data = new  HashMap<>();
		data.put("status", "up");
		data.put("version", version);
		data.put("appID", appID);
		
		log.info("CheckController health: " + data.toString());
		
		return data;
	}
	
	// 랜덤 문자열 생성
	private String generateString(Random random, String characters, int length) {
        char[] text = new char[length];
        for (int i = 0; i < length; i++) {
            text[i] = characters.charAt(random.nextInt(characters.length()));
        }
        return new String(text);
    }
	
	
}
