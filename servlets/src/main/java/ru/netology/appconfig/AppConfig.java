package ru.netology.appconfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import ru.netology.webconfig.WebConfig;
import ru.netology.repository.PostRepository;
import ru.netology.service.PostService;

@Configuration
@ComponentScan("ru.netology")
@Import(WebConfig.class)
public class AppConfig {

    @Bean
    public PostRepository postRepository() {
        return new PostRepository();
    }

    @Bean
    public PostService postService() {
        return new PostService(postRepository());
    }


}