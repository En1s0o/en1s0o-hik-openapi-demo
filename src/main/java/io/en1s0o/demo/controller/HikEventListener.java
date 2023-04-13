package io.en1s0o.demo.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 海康事件监听器
 *
 * @author En1s0o
 */
@Slf4j
@RestController
@RequestMapping("/hik")
public class HikEventListener {

    @PostMapping("/onEvent")
    public void onEvent(@RequestBody String event) {
        log.info("event={}", event);
    }

}
