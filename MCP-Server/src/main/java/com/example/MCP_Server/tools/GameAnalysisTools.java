package com.example.MCP_Server.tools;

import com.example.MCP_Server.service.FaceItService;
import com.example.MCP_Server.service.StatsCalculator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class GameAnalysisTools {

    private final FaceItService faceItService;
    private final StatsCalculator statsCalculator;

    @Tool(
            name = "getPlayerStats",
            description = "Получает и анализирует статистику игрока FaceIT CS2"
    )
    public String getPlayerStats(
            @ToolParam(description = "Никнейм игрока на FaceIT") String nickname
    ) {
        try {
            log.info("Analyzing player: {}", nickname);
            Map<String, Object> analysis = faceItService.getPlayerAnalysis(nickname);
            return statsCalculator.generateAnalysisReport(analysis);
        } catch (Exception e) {
            log.error("Error in getPlayerStats: {}", e.getMessage());
            return "❌ Ошибка при анализе игрока " + nickname + ": " + e.getMessage();
        }
    }

    @Tool(
            name = "getPlayerOverview",
            description = "Получает краткий обзор игрока: ELO, уровень, страна"
    )
    public String getPlayerOverview(
            @ToolParam(description = "Никнейм игрока") String nickname
    ) {
        try {
            var player = faceItService.getPlayerStats(nickname);
            if (player == null) {
                return "❌ Игрок " + nickname + " не найден";
            }

            return String.format("""
                👤 **Обзор игрока:** %s
                
                📊 Основные данные:
                • ELO: %d
                • Уровень: %d
                • Страна: %s
                """,
                    player.getNickname(),
                    player.getGames().getCs2().getFaceitElo(),
                    player.getGames().getCs2().getSkillLevel(),
                    player.getCountry()
            );
        } catch (Exception e) {
            return "❌ Ошибка: " + e.getMessage();
        }
    }

    @Tool(
            name = "comparePlayers",
            description = "Сравнивает статистику двух игроков FaceIT"
    )
    public String comparePlayers(
            @ToolParam(description = "Первый игрок") String player1,
            @ToolParam(description = "Второй игрок") String player2
    ) {
        try {
            Map<String, Object> analysis1 = faceItService.getPlayerAnalysis(player1);
            Map<String, Object> analysis2 = faceItService.getPlayerAnalysis(player2);

            StringBuilder comparison = new StringBuilder();
            comparison.append("🤼 **Сравнение игроков:**\n\n");
            comparison.append("**").append(player1).append(" vs ").append(player2).append("**\n\n");

            if (analysis1.containsKey("player") && analysis2.containsKey("player")) {
                var p1 = (com.example.MCP_Server.model.PlayerStats) analysis1.get("player");
                var p2 = (com.example.MCP_Server.model.PlayerStats) analysis2.get("player");

                comparison.append("🏆 **ELO сравнение:**\n");
                comparison.append("• ").append(p1.getNickname()).append(": ").append(p1.getGames().getCs2().getFaceitElo()).append("\n");
                comparison.append("• ").append(p2.getNickname()).append(": ").append(p2.getGames().getCs2().getFaceitElo()).append("\n");

                comparison.append("\n🎯 **Уровень навыка:**\n");
                comparison.append("• ").append(p1.getNickname()).append(": ").append(p1.getGames().getCs2().getSkillLevel()).append("\n");
                comparison.append("• ").append(p2.getNickname()).append(": ").append(p2.getGames().getCs2().getSkillLevel()).append("\n");
            }

            return comparison.toString();
        } catch (Exception e) {
            return "❌ Ошибка при сравнении игроков: " + e.getMessage();
        }
    }
}