package com.trust.common.file.export;

import java.util.List;
import java.util.Map;

public interface TradeReportExporter {
    String exportCsv(String reportName, List<Map<String, Object>> rows);

    String exportJson(String reportName, List<Map<String, Object>> rows);
}
