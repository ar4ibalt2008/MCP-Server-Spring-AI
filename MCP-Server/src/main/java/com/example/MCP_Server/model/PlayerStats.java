package com.example.MCP_Server.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PlayerStats {
    @JsonProperty("player_id")
    private String playerId;

    private String nickname;
    private String country;
    private Games games;

    @Data
    public static class Games {
        private Cs2 cs2;
    }

    @Data
    public static class Cs2 {
        @JsonProperty("faceit_elo")
        private Integer faceitElo;

        @JsonProperty("skill_level")
        private Integer skillLevel;
    }
}