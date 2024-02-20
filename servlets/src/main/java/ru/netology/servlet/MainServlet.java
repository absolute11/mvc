package ru.netology.servlet;


import com.google.gson.Gson;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.netology.AppConfig.AppConfig;
import ru.netology.controller.PostController;
import ru.netology.model.Post;
import ru.netology.repository.PostRepository;
import ru.netology.service.PostService;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class MainServlet extends HttpServlet {
  private static final String API_POSTS = "/api/posts";
  private static final String APPLICATION_JSON = "application/json";
  private static final String DELETE_METHOD = "DELETE";
  private static final String GET_METHOD = "GET";
  private static final String POST_METHOD = "POST";
  private final PostController controller;

  public MainServlet() {
    final var repository = new PostRepository();
    final var service = new PostService(repository);
    AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
    controller = context.getBean(PostController.class);
  }

  @Override
  protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    try {
      final var path = req.getRequestURI();
      final var method = req.getMethod();
      if (method.equals(GET_METHOD) && path.equals(API_POSTS)) {
        controller.all();
        return;
      }
      if (method.equals(GET_METHOD) && path.matches("/api/posts/\\d+")) {
        final var id = Long.parseLong(path.substring(path.lastIndexOf("/") + 1));
        controller.getById(id);
        return;
      }
      if (method.equals(POST_METHOD) && path.equals(API_POSTS)) {
        StringBuilder requestBody = new StringBuilder();
        String line;
        try (BufferedReader reader = req.getReader()) {
          while ((line = reader.readLine()) != null) {
            requestBody.append(line);
          }
        } catch (IOException e) {
          e.printStackTrace();
          resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
          return;
        }
        // Преобразование JSON-строки в объект Post с использованием Gson
        Post post = new Gson().fromJson(requestBody.toString(), Post.class);
        controller.save(post);
        return;
      }
      if (method.equals(DELETE_METHOD) && path.matches("/api/posts/\\d+")) {
        final var id = Long.parseLong(path.substring(path.lastIndexOf("/") + 1));
        controller.removeById(id);
        return;
      }
      resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
    } catch (Exception e) {
      e.printStackTrace();
      resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
  }
  }
