package com.example.MCP_Server.service;

import com.example.MCP_Server.model.PlayerStats;
import com.example.MCP_Server.model.MatchHistory;
import com.example.MCP_Server.model.MatchStats;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class FaceItService {

    private final WebClient faceItWebClient;

    public PlayerStats getPlayerStats(String nickname) {
        try {
            return faceItWebClient.get()
                    .uri("/players?nickname={nickname}", nickname)
                    .retrieve()
                    .bodyToMono(PlayerStats.class)
                    .block();
        } catch (Exception e) {
            log.error("Error fetching player: {}", e.getMessage());
            return null;
        }
    }

    public MatchHistory getMatchHistory(String playerId, int limit) {
        try {
            String uri = UriComponentsBuilder.fromPath("/players/{playerId}/history")
                    .queryParam("game", "cs2")
                    .queryParam("offset", 0)
                    .queryParam("limit", limit)
                    .build(playerId)
                    .toString();

            return faceItWebClient.get()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(MatchHistory.class)
                    .block();
        } catch (Exception e) {
            log.error("Error fetching match history: {}", e.getMessage());
            return null;
        }
    }

    public MatchStats getMatchStats(String matchId) {
        try {
            return faceItWebClient.get()
                    .uri("/matches/{matchId}", matchId)
                    .retrieve()
                    .bodyToMono(MatchStats.class)
                    .block();
        } catch (Exception e) {
            log.error("Error fetching match stats: {}", e.getMessage());
            return null;
        }
    }

    public Map<String, Object> getPlayerAnalysis(String nickname) {
        Map<String, Object> result = new HashMap<>();

        PlayerStats player = getPlayerStats(nickname);
        if (player == null) {
            result.put("error", "Player not found: " + nickname);
            return result;
        }

        result.put("player", player);

        MatchHistory matches = getMatchHistory(player.getPlayerId(), 5); // Уменьшил до 5 матчей для теста
        if (matches != null && matches.getItems() != null) {
            result.put("recentMatches", matches.getItems().size());

            // Анализ последних матчей
            double totalKills = 0;
            double totalDeaths = 0;
            double totalHeadshots = 0;
            int matchesAnalyzed = 0;

            for (MatchHistory.Match match : matches.getItems()) {
                try {
                    MatchStats matchStats = getMatchStats(match.getMatchId());
                    if (matchStats != null) {
                        MatchStats.PlayerStats playerStats = findPlayerStats(matchStats, player.getPlayerId());
                        if (playerStats != null) {
                            totalKills += playerStats.getKills();
                            totalDeaths += playerStats.getDeaths();
                            totalHeadshots += playerStats.getHeadshots();
                            matchesAnalyzed++;
                        }
                    }
                    Thread.sleep(200); // Задержка для API
                } catch (Exception e) {
                    log.warn("Error analyzing match {}: {}", match.getMatchId(), e.getMessage());
                }
            }

            if (matchesAnalyzed > 0) {
                double avgKills = totalKills / matchesAnalyzed;
                double avgDeaths = totalDeaths / matchesAnalyzed;
                double kdRatio = avgDeaths == 0 ? avgKills : avgKills / avgDeaths;
                double headshotRate = totalKills == 0 ? 0 : (totalHeadshots / totalKills) * 100;

                result.put("avgKills", Math.round(avgKills * 10) / 10.0);
                result.put("avgDeaths", Math.round(avgDeaths * 10) / 10.0);
                result.put("kdRatio", Math.round(kdRatio * 100) / 100.0);
                result.put("headshotRate", Math.round(headshotRate * 10) / 10.0);
                result.put("matchesAnalyzed", matchesAnalyzed);
            }
        }

        return result;
    }

    private MatchStats.PlayerStats findPlayerStats(MatchStats matchStats, String playerId) {
        if (matchStats.getRounds() == null) return null;

        return matchStats.getRounds().stream()
                .flatMap(round -> round.getTeams().stream())
                .flatMap(team -> team.getPlayers().stream())
                .filter(player -> playerId.equals(player.getPlayerId()))
                .findFirst()
                .orElse(null);
    }
}