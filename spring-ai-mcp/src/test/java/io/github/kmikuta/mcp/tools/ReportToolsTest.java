package io.github.kmikuta.mcp.tools;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
    var content = "Quarterly sales summary";
    var expectedDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));

    // when
    var report = reportTools.generate(content);

    // then
    assertThat(report.content()).isEqualTo(content);
    assertThat(report.date()).isEqualTo(expectedDate);
    assertThat(report.status()).isEqualTo(ReportTools.Status.SUCCESS);
  }
}
