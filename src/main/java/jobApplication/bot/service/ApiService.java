package jobApplication.bot.service;

import jobApplication.bot.dto.ApiResponseDTO;
import jobApplication.bot.dto.VacancyFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Optional;

@Slf4j
@Service
public class ApiService {

    private final RestClient restClient;

    @Autowired
    private ApiService(@Value("${rapidapi.key}") String apiKey) {
        this.restClient = RestClient.builder()
                .baseUrl("https://jsearch.p.rapidapi.com")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("x-rapidapi-host", "jsearch.p.rapidapi.com")
                .defaultHeader("x-rapidapi-key", apiKey)
                .build();
    }

    public Optional<ApiResponseDTO> getWithQuery(String query) {
        ApiResponseDTO apiResponseDTO = restClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/search-v2")
                        .queryParam("query", query)
                        .queryParam("num_pages", 30)
                        .build())
                .retrieve()
                .body(ApiResponseDTO.class);
        return Optional.ofNullable(apiResponseDTO);
    }

    public Optional<ApiResponseDTO> searchWithFilter(VacancyFilter filter) {

        String query = buildQuery(filter);

        ApiResponseDTO response = restClient
                .get()
                .uri(uriBuilder -> {
                    uriBuilder.path("/search-v2");
                    uriBuilder.queryParam("query", query);

                    if (filter.getCountry() != null)
                        uriBuilder.queryParam("country", filter.getCountry());
                    if (filter.getIsRemote() != null)
                        uriBuilder.queryParam("work_from_home", filter.getIsRemote());
                    if (filter.getCompany() != null)
                        uriBuilder.queryParam("employers", filter.getCompany());

                    return uriBuilder.build();
                })
                .retrieve()
                .body(ApiResponseDTO.class);

        return Optional.ofNullable(response);
    }

    private String buildQuery(VacancyFilter filter) {
        StringBuilder query = new StringBuilder();
        if (filter.getTitle() != null)
            query.append(filter.getTitle());
        if (filter.getCity() != null)
            query.append(" in ").append(filter.getCity());
        return query.toString().strip();
    }
}