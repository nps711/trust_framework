package com.trust.common.file.export;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LocalTradeReportExporter implements TradeReportExporter {
    private final ObjectMapper objectMapper;

    public LocalTradeReportExporter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public String exportCsv(String reportName, List<Map<String, Object>> rows) {
        try {
            Path dir = Files.createTempDirectory("quant-report-");
            Path file = dir.resolve(reportName + ".csv");
            List<String> lines = new ArrayList<>();
            if (!rows.isEmpty()) {
                List<String> headers = new ArrayList<>(rows.get(0).keySet());
                lines.add(String.join(",", headers));
                for (Map<String, Object> row : rows) {
                    lines.add(headers.stream().map(h -> String.valueOf(row.getOrDefault(h, ""))).collect(Collectors.joining(",")));
                }
            }
            Files.write(file, lines, StandardCharsets.UTF_8);
            return file.toString();
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to export csv", ex);
        }
    }

    @Override
    public String exportJson(String reportName, List<Map<String, Object>> rows) {
        try {
            Path dir = Files.createTempDirectory("quant-report-");
            Path file = dir.resolve(reportName + ".json");
            Files.writeString(file, objectMapper.writeValueAsString(rows), StandardCharsets.UTF_8);
            return file.toString();
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to export json", ex);
        }
    }
}
