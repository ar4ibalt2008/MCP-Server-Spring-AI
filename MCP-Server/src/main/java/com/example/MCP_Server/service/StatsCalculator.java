package com.example.MCP_Server.service;

import com.example.MCP_Server.model.PlayerStats;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class StatsCalculator {

    public String generateAnalysisReport(Map<String, Object> analysis) {
        if (analysis.containsKey("error")) {
            return "❌ Ошибка: " + analysis.get("error");
        }

        PlayerStats player = (PlayerStats) analysis.get("player");
        StringBuilder report = new StringBuilder();

        report.append("🎮 **Анализ игрока: ").append(player.getNickname()).append("**\n\n");

        // Основная информация
        report.append("📊 **Основная информация:**\n");
        report.append("• ELO: ").append(player.getGames().getCs2().getFaceitElo()).append("\n");
        report.append("• Уровень навыка: ").append(player.getGames().getCs2().getSkillLevel()).append("\n");
        report.append("• Страна: ").append(player.getCountry()).append("\n");

        // Статистика матчей
        if (analysis.containsKey("matchesAnalyzed")) {
            int matchesAnalyzed = (int) analysis.get("matchesAnalyzed");
            report.append("\n📈 **Статистика последних ").append(matchesAnalyzed).append(" матчей:**\n");
            report.append("• Средние убийства: ").append(analysis.get("avgKills")).append("\n");
            report.append("• Средние смерти: ").append(analysis.get("avgDeaths")).append("\n");
            report.append("• K/D Ratio: ").append(analysis.get("kdRatio")).append("\n");
            report.append("• Процент хедшотов: ").append(analysis.get("headshotRate")).append("%\n");

            // Рекомендации
            report.append("\n💡 **Рекомендации:**\n");
            double kdRatio = (double) analysis.get("kdRatio");
            double headshotRate = (double) analysis.get("headshotRate");

            if (kdRatio < 1.0) {
                report.append("• Нужно улучшить прицеливание и позиционирование\n");
            }
            if (headshotRate < 40) {
                report.append("• Тренируйте прицел в голову на aim maps\n");
            }
            if (kdRatio > 1.2 && headshotRate > 45) {
                report.append("• Отличные показатели! Продолжайте в том же духе\n");
            }
        } else {
            report.append("\n⚠️ Не удалось проанализировать матчи\n");
        }

        return report.toString();
    }
}