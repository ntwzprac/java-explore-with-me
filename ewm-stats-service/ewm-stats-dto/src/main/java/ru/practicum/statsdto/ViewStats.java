package ru.practicum.statsdto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ViewStats {
    private String app;
    private String uri;
    private Long hits;
}
