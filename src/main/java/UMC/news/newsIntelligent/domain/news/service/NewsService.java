package UMC.news.newsIntelligent.domain.news.service;

import UMC.news.newsIntelligent.domain.news.News;
import UMC.news.newsIntelligent.domain.news.dto.NewsResponseDTO;
import UMC.news.newsIntelligent.domain.news.repository.NewsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NewsService {

    private final NewsRepository newsRepository;

    public NewsResponseDTO.NewsResDTO getRelatedNews(Long topicId, Long lastId, int size) {
        Pageable pageable = PageRequest.of(0, size);

        List<News> newsList = newsRepository.findByTopicIdWithPaging(topicId, lastId, pageable);
        int totalCount = newsRepository.countByTopicId(topicId);

        List<NewsResponseDTO.NewsListResDTO> content = newsList.stream()
                .map(n -> new NewsResponseDTO.NewsListResDTO(
                        n.getId(),
                        n.getTitle(),
                        n.getNewsSummary(),
                        n.getNewsLink(),
                        n.getPublishDate(),
                        n.getPress(),
                        n.getImageUrl(),
                        n.getImageSource()
                ))
                .toList();

        Long newLastId = content.isEmpty() ? null : content.get(content.size() - 1).id();
        boolean hasNext = newsList.size() == size;

        return new NewsResponseDTO.NewsResDTO(content, totalCount, newLastId, size, hasNext);
    }
}
