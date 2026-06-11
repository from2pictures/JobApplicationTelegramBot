package jobApplication.bot.dto;

import java.util.List;
import java.util.Map;

public record VacancyDTO(
        String job_id,
        String job_title,
        String employer_name,
        String employer_logo,
        String employer_website,
        String job_publisher,
        String job_employment_type,
        List<String> job_employment_types,
        String job_apply_link,
        Boolean job_apply_is_direct,
        List<ApplyOptionDTO> apply_options,
        String job_description,
        Boolean job_is_remote,
        String job_posted_at,
        Long job_posted_at_timestamp,
        String job_posted_at_datetime_utc,
        String job_location,
        String job_city,
        String job_state,
        String job_country,
        Double job_latitude,
        Double job_longitude,
        List<String> job_benefits,
        List<String> job_benefits_strings,
        String job_google_link,
        Integer job_salary,
        String job_salary_string,
        Integer job_min_salary,
        Integer job_max_salary,
        String job_salary_period,
        Map<String, Object> job_highlights,
        String job_onet_soc,
        String job_onet_job_zone,
        Object employer_reviews
) {}