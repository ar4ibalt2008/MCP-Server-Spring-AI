package com.example.MCP_Server.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
public class MatchStats {
    @JsonProperty("match_id")
    private String matchId;

    @JsonProperty("map_name")
    private String mapName;

    private List<Round> rounds;

    @Data
    public static class Round {
        private List<Team> teams;
    }

    @Data
    public static class Team {
        @JsonProperty("team_id")
        private String teamId;

        private List<PlayerStats> players;
    }

    @Data
    public static class PlayerStats {
        @JsonProperty("player_id")
        private String playerId;

        private String nickname;
        private Integer kills;
        private Integer deaths;
        private Integer assists;
        private Integer headshots;
    }
}