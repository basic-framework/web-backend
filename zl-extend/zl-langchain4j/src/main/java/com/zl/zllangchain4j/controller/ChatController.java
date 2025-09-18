package com.zl.zllangchain4j.controller;

import com.zl.zllangchain4j.aiservice.ConsultantService;
import dev.langchain4j.model.openai.OpenAiChatModel;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

 /**
 * LangChain4j 测试接口
 * @Author GuihaoLv
 */
@RestController
@RequestMapping("/web/lc")
@Slf4j
@Tag(name = "LangChain4j 测试接口")
public class ChatController {

     @Autowired
     private ConsultantService consultantService;

     @RequestMapping(value = "/chat",produces = "text/html;charset=utf-8")
     public Flux<String> chat(String memoryId,@RequestParam("prompt") String prompt){
         Flux<String> result = consultantService.chat(memoryId,prompt);
         return result;
     }



}
