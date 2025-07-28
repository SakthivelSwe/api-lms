package ApiGatewayService.Repo;

import ApiGatewayService.model.FileUpload;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FileRepository extends JpaRepository<FileUpload,Long> {
    Optional<FileUpload> findByFileName(String fileName);
}
