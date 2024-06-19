package boot.domain.posts;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PostsRepositoryTest {

    @Autowired
    PostsRepository postsRepository;

    @AfterEach
    public void cleanUp() {
        postsRepository.deleteAll();
    }

    @Test
    @DisplayName("게시글 저장 불러오기")
    void postsSave() throws Exception {
        //given
         String title = "테스트 게시물";
         String content = "테스트 본문";

         postsRepository.save(Posts.builder()
                 .title(title)
                 .content(content)
                 .author("minseongkimim@gmail.com")
                 .build());
        //when
        List<Posts> postsList = postsRepository.findAll();
        //then
        Posts posts = postsList.get(0);
        assertThat(posts.getTitle()).isEqualTo(title);
        assertThat(posts.getContent()).isEqualTo(content);
    }

    @Test
    @DisplayName("BaseTimeEntity 등록 확인")
    void baseTimeEntityCheck() throws Exception {
        //given
        LocalDateTime now = LocalDateTime.of(2024,5,27,0,0,0);
        postsRepository.save(Posts.builder()
                .title("title")
                .content("content")
                .author("author")
                .build());
        //when
        List<Posts> all = postsRepository.findAll();

        //then
        Posts posts = all.get(0);

        assertThat(posts.getCreatedDate()).isAfter(now);
        assertThat(posts.getLastModifiedDate()).isAfter(now);
    }

}