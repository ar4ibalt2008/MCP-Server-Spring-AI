package com.example.MCP_Server.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
public class MatchHistory {
    private List<Match> items;

    @Data
    public static class Match {
        @JsonProperty("match_id")
        private String matchId;

        @JsonProperty("game_id")
        private String gameId;

        @JsonProperty("finished_at")
        private Long finishedAt;
    }
}