package com.zl.zllangchain4j.controller;


import dev.langchain4j.model.openai.OpenAiChatModel;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    private OpenAiChatModel model;

    @RequestMapping("/chat")
    @Operation(summary = "AI会话")
    public String chat(@RequestParam("prompt") String prompt){
        String result = model.chat(prompt);
        return result;
    }



}
