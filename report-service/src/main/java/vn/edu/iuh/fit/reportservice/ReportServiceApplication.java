package vn.edu.iuh.fit.reportservice;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import vn.edu.iuh.fit.reportservice.entity.Report;
import vn.edu.iuh.fit.reportservice.enums.ReportStatus;
import vn.edu.iuh.fit.reportservice.enums.ReportType;
import vn.edu.iuh.fit.reportservice.repository.ReportRepository;

import java.time.LocalDateTime;

@EnableFeignClients
@SpringBootApplication
public class ReportServiceApplication implements CommandLineRunner {
    private final ReportRepository reportRepository;

    public ReportServiceApplication(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    public static void main(String[] args) {
        SpringApplication.run(ReportServiceApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        // Kiểm tra nếu chưa có báo cáo trong cơ sở dữ liệu
        if (reportRepository.count() == 0) {
            // Tạo đối tượng Report
            Report report = Report.builder()
                    .roomId(101L)  // Ví dụ ID phòng
                    .userId(1L)    // Ví dụ ID người dùng
                    .type(ReportType.SCAM)  // Loại báo cáo
                    .description("Room has an issue with air conditioning.")  // Mô tả báo cáo
                    .status(ReportStatus.PENDING)  // Trạng thái báo cáo
                    .createAt(LocalDateTime.now())  // Thời gian tạo
                    .updateAt(LocalDateTime.now())  // Thời gian cập nhật
                    .build();

            // Lưu báo cáo vào cơ sở dữ liệu
            reportRepository.save(report);
            System.out.println("Report has been saved successfully!");
        } else {
            System.out.println("A report already exists, no new report will be created.");
        }
    }
}
