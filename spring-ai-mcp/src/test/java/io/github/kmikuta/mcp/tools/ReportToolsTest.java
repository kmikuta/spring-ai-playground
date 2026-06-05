package io.github.kmikuta.mcp.tools;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ReportToolsTest {

  private ReportTools reportTools;

  @BeforeEach
  void setUp() {
    reportTools = new ReportTools();
  }

  @Test
  void shouldGenerateReport() {
    // given
    var date = "2026-06-05";

    // when
    var report = reportTools.generate(date);

    // then
    assertThat(report.date()).isEqualTo(date);
    assertThat(report.status()).isEqualTo(ReportTools.Status.SUCCESS);
    assertThat(report.id()).isNotNull();
  }
}
